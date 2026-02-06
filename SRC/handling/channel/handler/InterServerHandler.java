package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;

import client.MapleQuestStatus;
import client.MapleTrait;
import client.Skill;
import client.SkillFactory;
import client.inventory.MapleInventoryType;
import client.linkskill.LinkSkillInfo;
import constants.GameConstants;
import constants.ServerConstants;
import handling.ChatType;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.CharacterIdChannelPair;
import handling.world.CharacterTransfer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import handling.world.exped.MapleExpedition;
import handling.world.guild.MapleGuild;

import java.util.List;
import java.util.Map.Entry;
import server.MapleInventoryManipulator;
import server.Start;

import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CField;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuddylistPacket;
import tools.packet.CWvsContext.FamilyPacket;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.CCashShop;
import tools.packet.CLogin;
import tools.packet.CUserLocal;

public class InterServerHandler {

    public static final void EnterCS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.getSession().write(CField.serverBlocked(2));
            c.getSession().write(CWvsContext.enableActions());
            c.getPlayer().dropMessage(5, "11");
            return;
        }
        if (ServerConstants.BLOCK_CS && !chr.isGM()) {
            chr.dropMessage(1, "캐시샵 현재 점검중입니다.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final ChannelServer ch = ChannelServer.getInstance(c.getChannel());

        chr.changeRemoval();

        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), mts ? -20 : -10);
        ch.removePlayer(chr);
        c.updateLoginState(MapleClient.CHANGE_CHANNEL, c.getSessionIPAddress());
        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.getSession().write(CField.getChannelChange(Integer.parseInt(CashShopServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static final void EnterMTS(final MapleClient c, final MapleCharacter chr) {
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            chr.dropMessage(1, "Please try again later.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        /*if (chr.getLevel() < 15) {
            chr.dropMessage(1, "You may not enter the Free Market until level 15.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (chr.getMapId() >= 910000000 && chr.getMapId() <= 910000022) {
            chr.dropMessage(1, "You are already in the Free Market.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        chr.dropMessage(5, "You will be transported to the Free Market Entrance.");
        chr.saveLocation(SavedLocationType.fromString("FREE_MARKET"));
        final MapleMap warpz = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(910000000);
        if (warpz != null) {
            chr.changeMap(warpz, warpz.getPortal("st00"));
        } else {
            chr.dropMessage(5, "Please try again later.");
        }*/
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void Loggedin(final int playerid, final MapleClient c) {
        final MapleCharacter player;
        CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            transfer = cserv.getPlayerStorage().getPendingCharacter(playerid);
            if (transfer != null) {
                c.setChannel(cserv.getChannel());
                break;
            } else {
            }
        }
        if (transfer == null) {
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
            Triple<String, String, Integer> ip = LoginServer.getLoginAuth(playerid);
            String s = c.getSessionIPAddress();
            if (ip == null/* || !s.substring(s.indexOf('/') + 1, s.length()).equals(ip.left)*/) {
                if (ip != null) {
                    LoginServer.putLoginAuth(playerid, ip.left, ip.mid, ip.right);
                }
                c.getSession().close();
                return;
            }
            c.setTempIP(ip.mid);
            c.setChannel(ip.right);
        } else {
            player = MapleCharacter.ReconstructChr(transfer, c, true);
        }
        final ChannelServer channelServer = c.getChannelServer();
        c.setPlayer(player);
        c.setAccID(player.getAccountID());
        c.loadKeyValues();
        c.loadLinkSkillStorage();
        c.loadCustomDatas();
        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL || state == MapleClient.LOGIN_NOTLOGGEDIN) {
            allowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
        }
        if (!allowLogin) {
            c.setPlayer(null);
            System.out.println("allowLogin");
            c.getSession().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        channelServer.addPlayer(player);
        player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
        player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
        player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));
        c.sendPacket(CField.getCharInfo(player));

        c.sendPacket(CWvsContext.OnClaimSvrStatusChanged(true));
        //c.sendPacket(CWvsContext.OnSetPotionDiscountRate(10));

        //c.sendPacket(CUserLocal.dodgeSkillReady());
        //c.sendPacket(InfoPacket.setQuestTime());
        //c.sendPacket(UIPacket.getEventList());
        int[] skills = {80000000, 80000001, 80001040};
        for (int skill : skills) {
            player.changeSingleSkillLevel(SkillFactory.getSkill(skill), 0, (byte) 0, -1);
        }
        for (Entry<Integer, LinkSkillInfo> v : player.getClient().getLinkSkills().entrySet()) {
            if (v.getValue().getOchrId() == player.getId()) {
                int skillid = -1;
                switch (v.getValue().getSkillId()) {
                    case 80000000:
                        skillid = 110;
                        break;
                    case 80000001:
                        skillid = 30010112;
                        break;
                    case 80001040:
                        skillid = 20021110;
                        break;
                }
                if (skillid == -1) {
                    continue;
                }
                player.changeSingleSkillLevel(SkillFactory.getSkill(skillid), v.getValue().getRchrId(), (byte) 1, -1);
            }
            if (v.getValue().getRchrId() == player.getId()) {
                player.changeSingleSkillLevel(SkillFactory.getSkill(v.getValue().getSkillId()), v.getValue().getOchrId(), (byte) 1, -1);
            }
        }
        c.getSession().write(CCashShop.enableCSUse());
        Start.startGC(System.currentTimeMillis());
        c.getSession().write(CWvsContext.temporaryStats_Reset());

        player.getMap().addPlayer(player);
        try {
            // Start of buddylist
            final int buddyIds[] = player.getBuddylist().getBuddyIds();
            World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
            if (player.getParty() != null) {
                final MapleParty party = player.getParty();
                World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
                if (party != null && party.getExpeditionId() > 0) {
                    final MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                    if (me != null) {
                        c.getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(me, false, true));
                    }
                }
            }
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                player.getBuddylist().get(onlineBuddy.getCharacterId()).setChannel(onlineBuddy.getChannel());
            }
            c.getSession().write(BuddylistPacket.updateBuddylist(player.getBuddylist().getBuddies()));

            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }

            // Start of Guild and alliance
            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.getSession().write(GuildPacket.showGuildInfo(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<byte[]> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (byte[] pack : packetList) {
                            if (pack != null) {
                                c.getSession().write(pack);
                            }
                        }
                    }
                } else { //guild not found, change guild id
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            }

            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            c.getSession().write(FamilyPacket.getFamilyData());
            c.getSession().write(FamilyPacket.getFamilyInfo(player));
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Login_Error, e);
        }
        //player.getClient().getSession().write(MTSCSPacket.showMapleTokens(c.getPlayer()));
        player.getClient().getSession().write(CWvsContext.serverMessage(channelServer.getServerMessage()));
        player.sendMacros();
        player.showNote();
        player.sendImp();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);
        c.getSession().write(CField.getKeymap(player.getKeyLayout()));
        player.updatePetAuto();
        player.checkSelfRecovery();
        player.checkLifeTidal();
        player.expirationTask(true, transfer == null);
        if (player.getJob() == 132) {
            player.checkBerserk();
        }
        player.spawnSavedPets();
        DueyHandler.checkReceivePackage(player);
        if (player.getPremiumTime() > System.currentTimeMillis() || player.isGM()) {
            player.getClient().getSession().write(CField.UIPacket.getPCRoomCheck((byte) 2, (int) (player.getPremiumTime() - System.currentTimeMillis()) / 1000 / 60));
        }
        //player.spawnbuff();
        if (player.getStat().equippedSummon > 0) {
            SkillFactory.getSkill(player.getStat().equippedSummon).getEffect(1).applyTo(player);
        }
        MapleQuestStatus stat = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        c.getSession().write(CWvsContext.pendantSlot(stat != null && stat.getCustomData() != null && Long.parseLong(stat.getCustomData()) > System.currentTimeMillis()));
        stat = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.QUICK_SLOT));

        c.getSession().write(CField.quickSlot(stat != null && stat.getCustomData() != null ? stat.getCustomData() : null));
        player.applyInners();
        if (GameConstants.isDemon(player.getJob()) == true) {
            if (player.getTotalSkillLevel(30010185) > 0) {
                if (player.getTrait(MapleTrait.MapleTraitType.will).getExp() < 4563) {
                    player.getTrait(MapleTrait.MapleTraitType.will).addExp(4563);
                }
                if (player.getTrait(MapleTrait.MapleTraitType.charisma).getExp() < 4563) {
                    player.getTrait(MapleTrait.MapleTraitType.charisma).addExp(4563);
                }
            }
        }
        //player.getClient().sendPacket(CField.showEffect("Gollux/863010100"));

        if (player.getFamilyBuffExpEffect() > 0) {
            final String duration = StringUtil.getReadableMillis(System.currentTimeMillis(), c.getPlayer().getFamilyBuffExpDuration());
            player.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "나만의 경험치 1.2배를 사용 중에 있습니다. (잔여 시간 : " + duration + ")"));
        }
        if (player.getFamilyBuffDropEffect() > 0) {
            final String duration = StringUtil.getReadableMillis(System.currentTimeMillis(), c.getPlayer().getFamilyBuffDropDuration());
            player.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "나만의 드롭율 1.2배를 사용 중에 있습니다. (잔여 시간 : " + duration + ")"));
        }

        if (player.getLevel() < 100) {
            player.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "@명령어를 통해 인 게임 내 사용 할 수 있는 각 종 명령어를 확인 할 수 있습니다."));
        }
        if (player.getLevel() > 9) {
            boolean v1 = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
            if (!v1) {
                player.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "고용 상점을 개설 시에 [고용 상점 개설 보너스 경험치 10%]를 획득 할 수 있습니다."));
            }
        }

        if (player.getOneInfoQuest(20250227, "macro_potion").equals("")) {
            player.updateOneInfoQuest(20250227, "macro_potion", "0");
        }
        if (Long.parseLong(player.getOneInfoQuest(20250227, "macro_potion")) > System.currentTimeMillis()) {
            final String eDuration = StringUtil.getReadableMillis(System.currentTimeMillis(), Long.parseLong(player.getOneInfoQuest(20250227, "macro_potion")));
            player.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "[MVP의 물약] 효과를 적용 중에 있습니다. (잔여 시간 : " + eDuration + ")"));
        }

        if (player.getLevel() == 120) {
            if (player.getGuild() != null) {
                if (c.getPlayer().getGuildId() < 7) {
                    player.getGuild().leaveGuild(c.getPlayer().getMGC());
                    player.dropMessage(5, "120 레벨을 달성하여 " + ServerConstants.server_Name_Source + " 초보자 길드에서 추방되었습니다.");
                }
            }
        }

        if (player.getQuestStatus(34302) == 1) {
            MapleQuest q34302 = MapleQuest.getInstance(34302);
            q34302.forfeit(player);
        }
        if (player.getQuestStatus(6500) < 2) {
            MapleQuest q6500 = MapleQuest.getInstance(6500);
            q6500.forceComplete(player, 2007);
        }

        if (!GameConstants.isDualBlade(player.getJob())) {
            player.setSubcategory(0);
        }
        
        player.handleSymbolStat();

        //player.setSoulEnchantSkill();
        if (player.isGM()) {
            //player.getClient().sendPacket(UIPacket.saintSaver(player));
        }
    }

    public static final void ChangeChannel(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean room) {
        if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap() || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 20) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in a less than a minute.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final int chc = slea.readByte() + 1;
        int mapid = 0;
        if (room) {
            mapid = slea.readInt();
        }
        chr.updateTick(slea.readInt());
        if (chr.getCheatTracker().canChannel()) {
            chr.changeChannel(chc);
        } else {
            chr.dropMessage(1, "3초 후 이동할 수 있습니다.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (!World.isChannelAvailable(chc)) {
            chr.dropMessage(1, "The channel is full at the moment.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (room && (mapid < 910000001 || mapid > 910000022)) {
            chr.dropMessage(1, "The channel is full at the moment.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (room) {
            if (chr.getMapId() == mapid) {
                if (c.getChannel() == chc) {
                    chr.dropMessage(1, "You are already in " + chr.getMap().getMapName());
                    c.getSession().write(CWvsContext.enableActions());
                } else { // diff channel
                    chr.changeChannel(chc);
                }
            } else { // diff map
                if (c.getChannel() != chc) {
                    chr.changeChannel(chc);
                }
                final MapleMap warpz = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                if (warpz != null) {
                    chr.changeMap(warpz, warpz.getPortal("out00"));
                } else {
                    chr.dropMessage(1, "The channel is full at the moment.");
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
        } else {
            chr.changeChannel(chc);
        }
    }
}
