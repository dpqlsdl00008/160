package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.MessageType;
import constants.GameConstants;
import handling.ChatType;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.exped.ExpeditionType;
import handling.world.exped.MapleExpedition;
import handling.world.exped.PartySearch;
import handling.world.exped.PartySearchType;
import java.util.ArrayList;
import java.util.List;
import server.maps.Event_DojoAgent;
import server.maps.FieldLimitType;
import server.maps.MapleDoor;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import tools.data.LittleEndianAccessor;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;

public class PartyHandler {

    public static final void DenyPartyRequest(LittleEndianAccessor slea, MapleClient c) {
        //58 - Deny
        //59 - Accept
        //52 - Send
        //56 - Request already sent
        int action = slea.readByte();
        if (action == 52) { //Sent.
            MapleCharacter chr = c.getPlayer().getMap().getCharacterById(slea.readInt());
            if (chr != null) {
                chr.getClient().sendPacket(CWvsContext.PartyPacket.partyStatusMessage(52, c.getPlayer().getName()));
            }
            return;
        }
        if (action == 56) { //Already sent, in queue.
            MapleCharacter chr = c.getPlayer().getMap().getCharacterById(slea.readInt());
            if (chr != null) {
                chr.dropMessage(MessageType.POPUP, "Your request is still being processed.");
            }
            return;
        }
        if (action == 58) { //Declined
            MapleCharacter chr = c.getPlayer().getMap().getCharacterById(slea.readInt());
            if (chr != null) {
                chr.dropMessage(MessageType.POPUP, "Your request to join the party has been denied.");
            }
            return;
        }
        if (action == 59) { //Accepted.
            MapleCharacter chr = c.getPlayer().getMap().getCharacterById(slea.readInt());
            if ((chr != null) && (chr.getParty() == null) && (c.getPlayer().getParty() != null) && (c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) && (c.getPlayer().getParty().getMembers().size() < 6) && (c.getPlayer().getParty().getExpeditionId() <= 0) && (chr.getQuestNoAdd(MapleQuest.getInstance(122901)) == null) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122900)) == null)) {
                chr.setParty(c.getPlayer().getParty());
                World.Party.updateParty(c.getPlayer().getParty().getId(), PartyOperation.JOIN, new MaplePartyCharacter(chr));
                chr.receivePartyMemberHP();
                chr.updatePartyMemberHP();
            }
            return;
        }
        int partyid = slea.readInt();
        if ((c.getPlayer().getParty() == null) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
            MapleParty party = World.Party.getParty(partyid);
            if (party != null) {
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                if (action == 31) {
                    if (party.getMembers().size() < 6) {
                        c.getPlayer().setParty(party);
                        World.Party.updateParty(partyid, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                        c.getPlayer().receivePartyMemberHP();
                        c.getPlayer().updatePartyMemberHP();
                    } else {
                        c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(22, null));
                    }
                } else if (action != 30) {
                    MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterById(party.getLeader().getId());
                    if (cfrom != null) {
                        cfrom.getClient().sendPacket(CWvsContext.PartyPacket.partyStatusMessage(23, c.getPlayer().getName()));
                    }
                }
            } else {
                c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
            }
        } else {
            c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
        }
    }

    public static final void PartyOperation(LittleEndianAccessor slea, MapleClient c) {
        int operation = slea.readByte();
        MapleParty party = c.getPlayer().getParty();
        MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());
        switch (operation) {
            case 1:
                if (party == null) {
                    party = World.Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.sendPacket(CWvsContext.PartyPacket.partyCreated(party.getId()));

                    if (c.getPlayer().getDoors().size() == 2) {
                        try {
                            MapleDoor door1 = c.getPlayer().getDoors().get(0);
                            MapleDoor door2 = c.getPlayer().getDoors().get(1);
                            door1.joinPartyElseDoorOwner(c);
                            door2.joinPartyElseDoorOwner(c);
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }

                } else {
                    if (party.getExpeditionId() > 0) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                    if ((partyplayer.equals(party.getLeader())) && (party.getMembers().size() == 1)) {
                        c.sendPacket(CWvsContext.PartyPacket.partyCreated(party.getId()));
                    }
                }
                break;
            case 2:
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    final MapleExpedition exped = World.Party.getExped(party.getExpeditionId());
                    if (exped != null) {
                        for (int i : exped.getParties()) {
                            final MapleParty par = World.Party.getParty(i);
                            if (par != null) {
                                final MaplePartyCharacter id = (par.getMemberById(exped.getLeader() == c.getPlayer().getId() ? exped.getLeader() : party.getLeader().getId() == c.getPlayer().getId() ? party.getLeader().getId() : c.getPlayer().getId()));
                                final PartyOperation type = (exped.getLeader() == c.getPlayer().getId() ? PartyOperation.DISBAND : PartyOperation.EXPEL);
                                if (id != null) {
                                    World.Party.updateParty(i, type, id);
                                    if (exped.getLeader() == c.getPlayer().getId()) {
                                        World.Party.disbandExped(exped.getId());
                                        for (MapleCharacter user : c.getPlayer().getMap().getCharactersThreadsafe()) {
                                            if (user.getEventInstance() != null) {
                                                user.getEventInstance().dispose();
                                            }
                                            //if (GameConstants.ExpeditionMap(user.getMapId())) {
                                            //user.changeMap(user.getMap().getForcedReturnMap());
                                            //}
                                        }
                                    } else {
                                        c.sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(exped, false, false));
                                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeft(c.getPlayer().getName()), null);
                                        if (c.getPlayer().getEventInstance() != null) {
                                            c.getPlayer().getEventInstance().dispose();
                                        }
                                        //if (GameConstants.ExpeditionMap(c.getPlayer().getMapId())) {
                                        //c.getPlayer().changeMap(c.getPlayer().getMap().getForcedReturnMap());
                                        //}
                                    }
                                }
                            }
                        }
                        c.getPlayer().setParty(null);
                        return;
                    }
                }
                if (partyplayer.equals(party.getLeader())) {
                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().disbandParty();
                    }
                } else {
                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                    }
                }
                if (c.getPlayer().getDoors().size() == 2) {
                    try {
                        c.getPlayer().getDoors().get(0).sendSinglePortal();
                        c.getPlayer().getDoors().get(1).sendSinglePortal();
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
                c.getPlayer().setParty(null);
                break;
            case 3:
                int partyid = slea.readInt();
                if (party == null) {
                    party = World.Party.getParty(partyid);
                    if (party != null) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if ((party.getMembers().size() < 6) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
                }
                break;
            case 4:
                if (party == null) {
                    party = World.Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.sendPacket(CWvsContext.PartyPacket.partyCreated(party.getId()));
                }

                String theName = slea.readMapleAsciiString();
                int theCh = World.Find.findChannel(theName);
                if (theCh > 0) {
                    MapleCharacter invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(theName);
                    if ((invited != null) && (invited.getParty() == null) && (invited.getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if (party.getMembers().size() < 6) {
                            c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(26, invited.getName()));
                            invited.getClient().sendPacket(CWvsContext.PartyPacket.partyInvite(c.getPlayer()));
                        } else {
                            c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(21, null));
                    }
                } else {
                    c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(17, null));
                }
                break;
            case 5:
                if ((party == null) || (partyplayer == null) || (!partyplayer.equals(party.getLeader()))) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter expelled = party.getMemberById(slea.readInt());
                if (expelled != null) {
                    if ((GameConstants.isDojo(c.getPlayer().getMapId())) && (expelled.isOnline())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if ((c.getPlayer().getPyramidSubway() != null) && (expelled.isOnline())) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
                    if (c.getPlayer().getEventInstance() != null) {
                        if (expelled.isOnline()) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                    }
                }
                break;
            case 6:
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter newleader = party.getMemberById(slea.readInt());
                if ((newleader != null) && (partyplayer.equals(party.getLeader()))) {
                    World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newleader);
                }
                break;
            case 7:
                if (party != null) {
                    if ((c.getPlayer().getEventInstance() != null) || (c.getPlayer().getPyramidSubway() != null) || (party.getExpeditionId() > 0) || (GameConstants.isDojo(c.getPlayer().getMapId()))) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                    if (partyplayer.equals(party.getLeader())) {
                        World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    } else {
                        World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                    }
                    c.getPlayer().setParty(null);
                }
                int partyid_ = slea.readInt();
                party = World.Party.getParty(partyid_);
                if ((party == null) || (party.getMembers().size() >= 6)) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MapleCharacter cfrom = c.getPlayer().getMap().getCharacterById(party.getLeader().getId());
                if ((cfrom != null) && (cfrom.getQuestNoAdd(MapleQuest.getInstance(122900)) == null)) {
                    c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(50, c.getPlayer().getName()));
                    cfrom.getClient().sendPacket(CWvsContext.PartyPacket.partyRequestInvite(c.getPlayer()));
                } else {
                    c.getPlayer().dropMessage(5, "Player was not found or player is not accepting party requests.");
                }
                break;
            case 8:
                if (slea.readByte() > 0) {
                    c.getPlayer().getQuestRemove(MapleQuest.getInstance(122900));
                } else {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(122900));
                }
                break;
            default:
                System.out.println("Unhandled Party function." + operation);
        }
    }

    public static final void AllowPartyInvite(LittleEndianAccessor slea, MapleClient c) {
        if (slea.readByte() > 0) {
            c.getPlayer().getQuestRemove(MapleQuest.getInstance(122901));
        } else {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(122901));
        }
    }

    public static final void MemberSearch(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer().isInBlockedMap()) || (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()))) {
            c.getPlayer().dropMessage(5, "파티검색을 할 수 없는 장소입니다.");
            return;
        }

        List charsToInvite = new ArrayList(); //Prevents yourself from showing.
        for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
            if (chr.getId() != c.getPlayer().getId()) {
                charsToInvite.add(chr);
            }
        }
        c.sendPacket(CWvsContext.PartyPacket.showMemberSearch(charsToInvite));
    }

    public static final void PartySearch(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer().isInBlockedMap()) || (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()))) {
            c.getPlayer().dropMessage(5, "파티검색을 할 수 없는 장소입니다.");
            return;
        }
        int charPartyId = 0; //Stupid null references.
        if (c.getPlayer().getParty() != null) {
            charPartyId = c.getPlayer().getParty().getId();
        } else {
            charPartyId = 0;
        }

        List parties = new ArrayList();
        for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
            if ((chr.getParty() != null) && (chr.getParty().getId() != charPartyId) && (!parties.contains(chr.getParty()))) {
                parties.add(chr.getParty());
            }
        }
        c.sendPacket(CWvsContext.PartyPacket.showPartySearch(parties));
    }

    public static final void PartyListing(LittleEndianAccessor slea, MapleClient c) {
        int mode = slea.readByte();
        PartySearchType pst;
        switch (mode) {
            case -105:
            case -97:
            case 81:
            case 159:
                pst = PartySearchType.getById(slea.readInt());
                if ((pst == null) || (c.getPlayer().getLevel() > pst.maxLevel) || (c.getPlayer().getLevel() < pst.minLevel)) {
                    return;
                }
                if ((c.getPlayer().getParty() == null) && (World.Party.searchParty(pst).size() < 10)) {
                    MapleParty party = World.Party.createParty(new MaplePartyCharacter(c.getPlayer()), pst.id);
                    c.getPlayer().setParty(party);
                    c.sendPacket(CWvsContext.PartyPacket.partyCreated(party.getId()));
                    PartySearch ps = new PartySearch(slea.readMapleAsciiString(), pst.exped ? party.getExpeditionId() : party.getId(), pst);
                    World.Party.addSearch(ps);
                    if (pst.exped) {
                        c.sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(World.Party.getExped(party.getExpeditionId()), true, false));
                    }
                    c.sendPacket(CWvsContext.PartyPacket.partyListingAdded(ps));
                } else {
                    c.getPlayer().dropMessage(1, "Unable to create. Please leave the party.");
                }
                break;
            case -103:
            case -95:
            case 83:
            case 161:
                pst = PartySearchType.getById(slea.readInt());
                if ((pst == null) || (c.getPlayer().getLevel() > pst.maxLevel) || (c.getPlayer().getLevel() < pst.minLevel)) {
                    return;
                }
                c.sendPacket(CWvsContext.PartyPacket.getPartyListing(pst));
                break;
            case -102:
            case -94:
            case 84:
            case 162:
                break;
            case -101:
            case -93:
            case 85:
            case 163:
            case 101:
            case 104:
                MapleParty party = c.getPlayer().getParty();
                MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());
                if (party != null && mode != 104) {
                    break;
                }
                int theId = slea.readInt();
                party = World.Party.getParty(theId);
                if (party != null) {
                    PartySearch ps = World.Party.getSearchByParty(party.getId());
                    if ((ps != null) && (c.getPlayer().getLevel() <= ps.getType().maxLevel) && (c.getPlayer().getLevel() >= ps.getType().minLevel) && (party.getMembers().size() < 6)) {
                        c.getPlayer().setParty(party);
                        World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                        c.getPlayer().receivePartyMemberHP();
                        c.getPlayer().updatePartyMemberHP();
                    } else {
                        c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(21, null));
                    }
                } else {
                    //TODO: FIX
                    //theId contains expedition type (2002 for Normal_Balrog as an example).
                    //getExped(int PartyID) searches through a hashmap of Expeds that are assigned when they are created; the only key to search through is the expedID..which starts at 1.
                    //We are unable to loop/look through Expeditions unless we can organize them into expedition types (2002, etc) 
                    //Expects: Byte for unique expedition ID. Receives: Expedition Type ID.
                    MapleExpedition exped = World.Party.getExped(theId);
                    if (exped != null) {
                        PartySearch ps = World.Party.getSearchByExped(exped.getId());
                        if ((ps != null) && (c.getPlayer().getLevel() <= ps.getType().maxLevel) && (c.getPlayer().getLevel() >= ps.getType().minLevel) && (exped.getAllMembers() < exped.getType().maxMembers)) {
                            int partyId = exped.getFreeParty();
                            if (partyId < 0) {
                                c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(21, null));
                            } else if (partyId == 0) {
                                party = World.Party.createPartyAndAdd(partyplayer, exped.getId());
                                c.getPlayer().setParty(party);
                                c.sendPacket(CWvsContext.PartyPacket.partyCreated(party.getId()));
                                c.sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                            } else {
                                c.getPlayer().setParty(World.Party.getParty(partyId));
                                World.Party.updateParty(partyId, PartyOperation.JOIN, partyplayer);
                                c.getPlayer().receivePartyMemberHP();
                                c.getPlayer().updatePartyMemberHP();
                                c.sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                            }
                        } else {
                            c.sendPacket(CWvsContext.ExpeditionPacket.expeditionError(0, c.getPlayer().getName()));
                        }
                    }
                }
                break;
            default:
                if (!c.getPlayer().isGM()) {
                    break;
                }
                System.out.println("Unknown PartyListing : " + mode + "\n" + slea);
        }
    }

    public static final void Expedition(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
        int mode = slea.readByte();
        String name;
        MapleParty part;
        MapleExpedition exped;
        MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());
        int cid;
        switch (mode) {
            // 생성
            case 0x3F: {
                final ExpeditionType type = ExpeditionType.getByType(slea.readInt());
                if (type != null) {
                    if (type.map != 100000000) {
                        final MapleMap map = c.getPlayer().getClient().getChannelServer().getMapFactory().getMap(type.map);
                        c.getPlayer().changeMap(map);
                        c.getPlayer().getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, map.getStreetName() + " : " + map.getMapName() + "(으)로 이동합니다."));
                    } else {
                        c.getPlayer().dropMessage(1, "현재 구현 준비 중에 있습니다.");
                    }
                }
                
                /*
                final ExpeditionType et = ExpeditionType.getById(slea.readInt());
                if (et != null && c.getPlayer().getParty() == null && c.getPlayer().getLevel() <= et.maxLevel && c.getPlayer().getLevel() >= et.minLevel) {
                    MapleParty party = World.Party.createParty(new MaplePartyCharacter(c.getPlayer()), et.exped);
                    c.getPlayer().setParty(party);
                    c.sendPacket(CWvsContext.PartyPacket.partyCreated(party.getId()));
                    c.sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(World.Party.getExped(party.getExpeditionId()), true, false));
                } else {
                    c.sendPacket(CWvsContext.ExpeditionPacket.expeditionError(0, ""));
                }
                */
                break;
            }
            // 초대
            case 0x40: {
                name = slea.readMapleAsciiString();
                int theCh = World.Find.findChannel(name);
                if (theCh > 0) {
                    MapleCharacter invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(name);
                    MapleParty party = c.getPlayer().getParty();
                    if ((invited != null) && (invited.getParty() == null) && (party != null) && (party.getExpeditionId() > 0)) {
                        MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                        if ((me != null) && (me.getAllMembers() < me.getType().maxMembers) && (invited.getLevel() <= me.getType().maxLevel) && (invited.getLevel() >= me.getType().minLevel)) {
                            c.sendPacket(CWvsContext.ExpeditionPacket.expeditionError(7, invited.getName()));
                            invited.getClient().sendPacket(CWvsContext.ExpeditionPacket.expeditionInvite(c.getPlayer(), me.getType().exped));
                        } else {
                            c.sendPacket(CWvsContext.ExpeditionPacket.expeditionError(3, invited.getName()));
                        }
                    } else {
                        c.sendPacket(CWvsContext.ExpeditionPacket.expeditionError(2, name));
                    }
                } else {
                    c.sendPacket(CWvsContext.ExpeditionPacket.expeditionError(0, name));
                }
                break;
            }
            // 수락
            case 0x41: {
                name = slea.readMapleAsciiString();
                final int action = slea.readInt();
                final int theChh = World.Find.findChannel(name);
                if (theChh > 0) {
                    final MapleCharacter cfrom = ChannelServer.getInstance(theChh).getPlayerStorage().getCharacterByName(name);
                    if (cfrom != null && cfrom.getParty() != null && cfrom.getParty().getExpeditionId() > 0) {
                        MapleParty party = cfrom.getParty();
                        exped = World.Party.getExped(party.getExpeditionId());
                        if (exped != null && action == 8) {
                            if (c.getPlayer().getLevel() <= exped.getType().maxLevel && c.getPlayer().getLevel() >= exped.getType().minLevel && exped.getAllMembers() < exped.getType().maxMembers) {
                                int partyId = exped.getFreeParty();
                                if (partyId < 0) {
                                    c.sendPacket(CWvsContext.PartyPacket.partyStatusMessage(21, null));
                                } else if (partyId == 0) {
                                    party = World.Party.createPartyAndAdd(new MaplePartyCharacter(c.getPlayer()), exped.getId());
                                    c.getPlayer().setParty(party);
                                    c.sendPacket(CWvsContext.PartyPacket.partyCreated(party.getId()));
                                    c.sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                    World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                    World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                                } else {
                                    c.getPlayer().setParty(World.Party.getParty(partyId));
                                    World.Party.updateParty(partyId, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                                    c.getPlayer().receivePartyMemberHP();
                                    c.getPlayer().updatePartyMemberHP();
                                    c.sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(exped, false, false));
                                    World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                }
                                PartySearch ps = World.Party.getSearchByExped(exped.getId());
                                if (ps != null) {
                                    if (exped.getAllMembers() == 30) {
                                        //removeSearch(ps, "원정대가 꽉 차서 파티 광고가 삭제되었습니다.");
                                    }
                                    //World.Broadcast.broadcastMessage(MaplePacketCreator.getPartyListing(ps.getType()));
                                }
                            } else {
                                c.sendPacket(CWvsContext.ExpeditionPacket.expeditionError(3, cfrom.getName()));
                            }
                        } else if (action == 9) {
                            cfrom.dropMessage(1, "'" + c.getPlayer().getName() + "'님이 원정대 초대를 거절하였습니다.");
                        }
                    }
                }
                break;
            }
            // 탈퇴
            case 0x42: {
                part = c.getPlayer().getParty();
                if (part != null && part.getExpeditionId() > 0) {
                    exped = World.Party.getExped(part.getExpeditionId());
                    if (exped != null) {
                        for (int i : exped.getParties()) {
                            final MapleParty par = World.Party.getParty(i);
                            if (par != null) {
                                final MaplePartyCharacter id = (par.getMemberById(exped.getLeader() == c.getPlayer().getId() ? exped.getLeader() : part.getLeader().getId() == c.getPlayer().getId() ? part.getLeader().getId() : c.getPlayer().getId()));
                                final PartyOperation type = (exped.getLeader() == c.getPlayer().getId() ? PartyOperation.DISBAND : PartyOperation.EXPEL);
                                if (id != null) {
                                    //World.Party.updateParty(i, type, id);
                                    if (exped.getLeader() == c.getPlayer().getId()) {
                                        World.Party.disbandExped(exped.getId());
                                        for (MapleCharacter user : c.getPlayer().getMap().getCharactersThreadsafe()) {
                                            if (user.getEventInstance() != null) {
                                                user.getEventInstance().dispose();
                                            }
                                        }
                                    } else {
                                        c.sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(exped, false, false));
                                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeft(c.getPlayer().getName()), null);
                                        if (c.getPlayer().getEventInstance() != null) {
                                            c.getPlayer().getEventInstance().dispose();
                                        }
                                    }
                                    World.Party.updateParty(i, type, id);
                                }
                            }
                        }
                        c.getPlayer().setParty(null);
                    }
                }
                break;
            }
            // 추방
            case 0x43: {
                part = c.getPlayer().getParty();
                if (part != null && part.getExpeditionId() > 0) {
                    exped = World.Party.getExped(part.getExpeditionId());
                    if (exped != null && exped.getLeader() == c.getPlayer().getId()) {
                        cid = slea.readInt();
                        for (int i : exped.getParties()) {
                            final MapleParty par = World.Party.getParty(i);
                            if (par != null) {
                                final MaplePartyCharacter expelled = par.getMemberById(cid);
                                if (expelled != null) {
                                    World.Party.updateParty(i, PartyOperation.EXPEL, expelled);
                                    int channel = World.Find.findChannel(expelled.getName());
                                    if (channel > 0) {
                                        MapleCharacter user = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(expelled.getName());
                                        if (user != null) {
                                            if (user.getEventInstance() != null) {
                                                user.getEventInstance().dispose();
                                            }
                                        }
                                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeft(expelled.getName()), null);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            // 원정대장 교체
            case 0x44: {
                part = c.getPlayer().getParty(); // 현재 원정대장의 파티
                if (part != null && part.getExpeditionId() > 0) { // 원정대장의 파티 != null && 원정대
                    exped = World.Party.getExped(part.getExpeditionId()); // 원정대 : 현재 파티의 원정대
                    if (exped != null && exped.getLeader() == c.getPlayer().getId()) { // 현재 파티의 원정대 != null && 원정대장
                        for (int i : exped.getParties()) { // 원정대의 모든 parties
                            MapleParty par = World.Party.getParty(i); // 해당 원정대의 모든 파티
                            if (par != null) { // 파티 != null
                                final MaplePartyCharacter newleader = par.getMemberById(slea.readInt());
                                if (newleader != null) {
                                    World.Party.updateParty(i, PartyOperation.CHANGE_LEADER, newleader);
                                    exped.setLeader(newleader.getId());
                                    World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeaderChanged(0), null);
                                }
                            }
                        }
                    }
                }
                break;
            }
            // 파티장 교체
            case 0x45: {
                part = c.getPlayer().getParty();
                if (part != null && part.getExpeditionId() > 0) {
                    exped = World.Party.getExped(part.getExpeditionId());
                    if (exped != null && exped.getLeader() == c.getPlayer().getId()) {
                        cid = slea.readInt();
                        for (int i : exped.getParties()) {
                            final MapleParty par = World.Party.getParty(i);
                            if (par != null) {
                                final MaplePartyCharacter newleader = par.getMemberById(cid);
                                if (newleader != null && par.getId() != part.getId()) {
                                    World.Party.updateParty(par.getId(), PartyOperation.CHANGE_LEADER, newleader);
                                }
                            }
                        }
                    }
                }
                break;
            }
            // 원정대원 분배
            case 0x46: {
                part = c.getPlayer().getParty();
                if (part != null && part.getExpeditionId() > 0) {
                    exped = World.Party.getExped(part.getExpeditionId());
                    if (exped != null && exped.getLeader() == c.getPlayer().getId()) {
                        final int partyIndexTo = slea.readInt();
                        if (partyIndexTo < exped.getType().maxParty && partyIndexTo <= exped.getParties().size()) {
                            cid = slea.readInt();
                            for (int i : exped.getParties()) {
                                final MapleParty par = World.Party.getParty(i);
                                if (par != null) {
                                    final MaplePartyCharacter expelled = par.getMemberById(cid);
                                    if (expelled != null && expelled.isOnline()) {
                                        final MapleCharacter chr = World.getStorage(expelled.getChannel()).getCharacterById(expelled.getId());
                                        if (chr == null) {
                                            break;
                                        }
                                        if (partyIndexTo < exped.getParties().size()) {
                                            MapleParty party = World.Party.getParty(exped.getParties().get(partyIndexTo));
                                            if (party == null || party.getMembers().size() >= 6) {
                                                c.getPlayer().dropMessage(1, "알 수 없는 오류가 발생하였습니다.");
                                                break;
                                            }
                                        }
                                        World.Party.updateParty(i, PartyOperation.EXPEL, expelled);
                                        if (partyIndexTo < exped.getParties().size()) {
                                            MapleParty party = World.Party.getParty(exped.getParties().get(partyIndexTo));
                                            if (party != null && party.getMembers().size() < 6) {
                                                World.Party.updateParty(party.getId(), PartyOperation.JOIN, expelled);
                                                chr.receivePartyMemberHP();
                                                chr.updatePartyMemberHP();
                                                chr.getClient().sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                            }
                                        } else {
                                            MapleParty party = World.Party.createPartyAndAdd(expelled, exped.getId());
                                            chr.setParty(party);
                                            chr.getClient().sendPacket(CWvsContext.PartyPacket.partyCreated(party.getId()));
                                            chr.getClient().sendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                            World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            default:
                if (!c.getPlayer().isGM()) {
                    break;
                }
                System.out.println("Unknown Expedition : " + mode + "\n" + slea);
        }
    }
}
