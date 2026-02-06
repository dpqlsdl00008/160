package handling.channel.handler;

import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.messages.CommandProcessor;
import constants.ServerConstants.CommandType;
import handling.ChatType;
import handling.channel.ChannelServer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.World;
import server.log.DBLogger;
import server.log.LogType;
import tools.packet.CField;
import tools.data.LittleEndianAccessor;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;

public class ChatHandler {

    public static final void GeneralChat(String text, final byte unk, final MapleClient c, final MapleCharacter chr) {

        if (text.length() > 0 && chr != null && chr.getMap() != null && !CommandProcessor.processCommand(c, text, CommandType.NORMAL)) {
            if (!chr.isIntern() && text.length() >= 80) {
                return;
            }
            if (chr.getCanTalk() || chr.isStaff()) {
                if (chr.isHidden()) {
                    if (chr.isIntern() && !chr.isSuperGM()) {
                        chr.getMap().broadcastGMMessage(chr, CWvsContext.serverNotice(2, chr.getName() + " : " + text), true);
                    } else {
                        chr.getMap().broadcastGMMessage(chr, CField.getChatText(chr.getId(), text, c.getPlayer().isSuperGM(), unk), true);
                    }
                    //chatCount.setCustomData(Integer.parseInt(chatCount.getCustomData()) + 1 + "");
                } else {
                    chr.getCheatTracker().checkMsg();
                    if (chr.isIntern() && !chr.isSuperGM()) {
                        chr.getMap().broadcastMessage(CWvsContext.serverNotice(2, chr.getName() + " : " + text), c.getPlayer().getTruePosition());
                    } else {
                        if (text.startsWith("~")) {
                            if (chr.getCheatTracker().canChat()) {

                                text = text.replaceAll("~", "");

                                final StringBuilder sb = new StringBuilder();
                                InventoryHandler.addMedalString(c.getPlayer(), sb);
                                sb.append(c.getPlayer().getName());
                                sb.append(" : ");
                                sb.append(text);

                                World.Broadcast.broadcastMessage(CUserLocal.chatMsg(ChatType.unk_2, sb.toString()));

                            } else {
                                chr.dropMessage(5, "5초 후 전체 채팅을 사용 할 수 있습니다.");
                                chr.getClient().sendPacket(CWvsContext.enableActions());
                            }
                        } else {
                            chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, c.getPlayer().isSuperGM(), unk), c.getPlayer().getTruePosition());
                        }
                    }
                    //chatCount.setCustomData(Integer.parseInt(chatCount.getCustomData()) + 1 + "");
                }
                //FileoutputUtil.log("log/[" + getDCurrentTime() + "] chat.txt", "[" + CurrentReadable_Time() + "]\r\n유저 : " + chr.getName() + "\r\n대화 : [전체 채팅] " + text + "\r\n");
                //DBLogger.getInstance().logChat(LogType.Chat.General, c.getPlayer().getId(), c.getPlayer().getName(), text, "");
                //DBLogger.getInstance().logChat(LogType.Chat.General, c.getPlayer().getId(), c.getPlayer().getName(), text, "");
            } else {
                chr.getClient().sendPacket(CWvsContext.yellowChat("대화 금지 상태이므로 채팅이 불가능합니다."));
            }
        }
    }

    public static final void Others(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int type = slea.readByte();
        final byte numRecipients = slea.readByte();
        if (numRecipients <= 0) {
            return;
        }
        int recipients[] = new int[numRecipients];

        for (byte i = 0; i < numRecipients; i++) {
            recipients[i] = slea.readInt();
        }
        final String chattext = slea.readMapleAsciiString();
        if (chr == null || !chr.getCanTalk()) {
            c.getSession().write(CWvsContext.serverNotice(6, "You have been muted and are therefore unable to talk."));
            return;
        }

        if (c.isMonitored()) {
            String chattype = "Unknown";
            switch (type) {
                case 0:
                    chattype = "Buddy";
                    break;
                case 1:
                    chattype = "Party";
                    break;
                case 2:
                    chattype = "Guild";
                    break;
                case 3:
                    chattype = "Alliance";
                    break;
                case 4:
                    chattype = "Expedition";
                    break;
            }
            World.Broadcast.broadcastGMMessage(
                    CWvsContext.serverNotice(6, "[GM Message] " + MapleCharacterUtil.makeMapleReadable(chr.getName())
                            + " said (" + chattype + "): " + chattext));

        }
        if (chattext.length() <= 0) {
            return;
        }
        chr.getCheatTracker().checkMsg();
        switch (type) {
            case 0:
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                DBLogger.getInstance().logChat(LogType.Chat.Buddy, c.getPlayer().getId(), c.getPlayer().getName(), chattext, "");
                break;
            case 1:
                if (chr.getParty() == null) {
                    break;
                }
                World.Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                DBLogger.getInstance().logChat(LogType.Chat.Party, c.getPlayer().getId(), c.getPlayer().getName(), chattext, "");
                break;
            case 2:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                DBLogger.getInstance().logChat(LogType.Chat.Guild, c.getPlayer().getId(), c.getPlayer().getName(), chattext, "");
                break;
            case 3:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                DBLogger.getInstance().logChat(LogType.Chat.Alliance, c.getPlayer().getId(), c.getPlayer().getName(), chattext, "");
                break;
            case 4:
                if (chr.getParty() == null || chr.getParty().getExpeditionId() <= 0) {
                    break;
                }
                World.Party.expedChat(chr.getParty().getExpeditionId(), chattext, chr.getName());
                break;
        }
    }

    public static final void Messenger(final LittleEndianAccessor slea, final MapleClient c) {
        String input;
        MapleMessenger messenger = c.getPlayer().getMessenger();

        switch (slea.readByte()) {
            case 0x00: // open
                if (messenger == null) {
                    int messengerid = slea.readInt();
                    if (messengerid == 0) { // create
                        c.getPlayer().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    } else { // join
                        messenger = World.Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 4) {
                                c.getPlayer().setMessenger(messenger);
                                World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                            }
                        }
                    }
                }
                break;
            case 0x02: // exit
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                }
                break;
            case 0x03: // invite

                if (messenger != null) {
                    final int position = messenger.getLowestPosition();
                    if (position <= -1 || position >= 4) {
                        return;
                    }
                    input = slea.readMapleAsciiString();
                    final MapleCharacter target = ChannelServer.getInstance(World.Find.findChannel(input)).getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null) {
                            if (!target.isIntern() || c.getPlayer().isIntern()) {
                                c.getSession().write(CField.messengerNote(input, 4, 1));
                                target.getClient().getSession().write(CField.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                            } else {
                                c.getSession().write(CField.messengerNote(input, 4, 0));
                            }
                        } else {
                            c.getSession().write(CField.messengerChat(c.getPlayer().getName(), " : " + target.getName() + " is already using Maple Messenger."));
                        }
                    } else {
                        if (World.isConnected(input)) {
                            World.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isIntern());
                        } else {
                            c.getSession().write(CField.messengerNote(input, 4, 0));
                        }
                    }
                }
                break;
            case 0x05: // decline
                final String targeted = slea.readMapleAsciiString();
                final MapleCharacter target = ChannelServer.getInstance(World.Find.findChannel(targeted)).getPlayerStorage().getCharacterByName(targeted);
                if (target != null) { // This channel
                    if (target.getMessenger() != null) {
                        target.getClient().getSession().write(CField.messengerNote(c.getPlayer().getName(), 5, 0));
                    }
                } else { // Other channel
                    if (!c.getPlayer().isIntern()) {
                        World.Messenger.declineChat(targeted, c.getPlayer().getName());
                    }
                }
                break;
            case 0x06: // message
                if (messenger != null) {
                    final String charname = slea.readMapleAsciiString();
                    final String text = slea.readMapleAsciiString();
                    final String chattext = charname + "" + text;
                    World.Messenger.messengerChat(messenger.getId(), charname, text, c.getPlayer().getName());
                    if (messenger.isMonitored() && chattext.length() > c.getPlayer().getName().length() + 3) { //name : NOT name0 or name1
                        World.Broadcast.broadcastGMMessage(
                                CWvsContext.serverNotice(
                                        6, "[GM Message] " + MapleCharacterUtil.makeMapleReadable(c.getPlayer().getName()) + "(Messenger: "
                                        + messenger.getMemberNamesDEBUG() + ") said: " + chattext));
                    }
                }
                break;
        }
    }

    public static final void Whisper_Find(final LittleEndianAccessor slea, final MapleClient c) {
        final byte mode = slea.readByte();
        slea.readInt(); //ticks
        switch (mode) {
            case 68: //buddy
            case 5: { // Find
                final String recipient = slea.readMapleAsciiString();
                int nCH = World.Find.findChannel(recipient);
                if (nCH == -10) {
                    String nStr = "'" + recipient + "'님의 현재 위치는 [CASH SHOP] 입니다.";
                    c.getPlayer().getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, nStr));
                    return;
                }
                MapleCharacter player = ChannelServer.getInstance(nCH).getPlayerStorage().getCharacterByName(recipient);
                if (player != null) {
                    if (!player.isIntern() || c.getPlayer().isIntern() && player.isIntern()) {
                        String pCH = (nCH == 1 ? "1 채널" : nCH == 2 ? "20세 이상 채널" : (nCH - 1) + " 채널");
                        String nStr = "'" + player.getName() + "'님의 현재 위치는 " + pCH + "의 [" + player.getMap().getStreetName() + " - " + player.getMap().getMapName() + "] 입니다.";
                        c.getPlayer().getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, nStr));
                        //c.getSession().write(CField.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
                    } else {
                        c.getSession().write(CField.getWhisperReply(recipient, (byte) 0));
                    }
                } else {
                    c.getSession().write(CField.getWhisperReply(recipient, (byte) 0));
                }
                break;
            }
            case 6: { // Whisper
                if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
                    return;
                }
                if (!c.getPlayer().getCanTalk()) {
                    c.getSession().write(CWvsContext.serverNotice(6, "You have been muted and are therefore unable to talk."));
                    return;
                }
                c.getPlayer().getCheatTracker().checkMsg();
                final String recipient = slea.readMapleAsciiString();
                final String text = slea.readMapleAsciiString();
                final int ch = World.Find.findChannel(recipient);
                if (ch > 0) {
                    MapleCharacter player = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                    if (player == null) {
                        break;
                    }
                    player.getClient().getSession().write(CField.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                    if (!c.getPlayer().isIntern() && player.isIntern()) {
                        c.getSession().write(CField.getWhisperReply(recipient, (byte) 0));
                    } else {
                        c.getSession().write(CField.getWhisperReply(recipient, (byte) 1));
                    }

                    DBLogger.getInstance().logChat(LogType.Chat.Whisper, c.getPlayer().getId(), c.getPlayer().getName(), text, recipient + "");

                    if (c.isMonitored()) {
                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, c.getPlayer().getName() + " whispered " + recipient + " : " + text));
                    } else if (player.getClient().isMonitored()) {
                        World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, c.getPlayer().getName() + " whispered " + recipient + " : " + text));
                    }
                } else {
                    c.getSession().write(CField.getWhisperReply(recipient, (byte) 0));
                }
            }
            break;
        }
    }
}
