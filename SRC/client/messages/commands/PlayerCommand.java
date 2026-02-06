package client.messages.commands;

import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import client.MonsterSkill;
import handling.channel.ChannelServer;
import scripting.NPCScriptManager;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;

public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class 의류수거함 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getClient().removeClickedNPC();
            NPCScriptManager.getInstance().start(c, 1012121);
            return 1;
        }
    }

    public static class 광장 extends 마을 {
    }

    public static class 마을 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().changeMap(910000000, 0);
            return 1;
        }
    }

    public static class 랙 extends 렉 {
    }

    public static class 렉 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.getPlayer().setConversation(0);

            //c.sendPacket(CWvsContext.testCode());
            //c.sendPacket(CNettPyramid.updateWave(123));
            //c.sendPacket(CNettPyramid.updateLife(456));
            //c.sendPacket(CNettPyramid.updatePoint(789));
            //c.sendPacket(CNettPyramid.rewardScore(true, 123, 456, 789, 9999));
            //c.sendPacket(CUserLocal.openUIWithOption(UIType.FIELDITEM, 9000113));
            //c.sendPacket(CUserLocal.openUIWithOption(UIType.DOJANGRESULT, 100000));
            //c.sendPacket(NPyramidPacket.NETT_PYRAMID(0));
            //c.getPlayer().getQuest(MapleQuest.getInstance(1047)).getQuest().forceStart(c.getPlayer(), 9010017, "100101/100101/5");
            //c.sendPacket(CUserLocal.questResult(QuestType.QuestRes_End_QuestTimer, 1047, 0, 0));
            c.sendPacket(CWvsContext.staticScreenMessage("무 반응 현상이 해결되었습니다.", false));
            /*
            for (MaplePartyCharacter pUser : c.getPlayer().getParty().getMembers()) {
                final MapleCharacter user = c.getChannelServer().getPlayerStorage().getCharacterByName(pUser.getName());
                user.getClient().sendPacket(CUserAction.showPQReward(c.getPlayer().getId(), 2049100));
            }
             */

 /*
            c.sendPacket(MCarnivalPacket.setMCarnival(c.getPlayer()));
            c.sendPacket(MCarnivalPacket.showUserRank(c.getPlayer().getMap()));
            c.sendPacket(CField.getPVPClock(1, 60 * 30));
            c.sendPacket(MCarnivalPacket.updateScore(777, 333));
            c.sendPacket(MCarnivalPacket.updateCP(7352, 2222));
            c.sendPacket(MCarnivalPacket.showCPMessage((byte) 3, (byte) 5, c.getPlayer()));
            c.sendPacket(MCarnivalPacket.updateCooldown(10, 15, 20, 25));
             */
            //c.getPlayer().getClient().sendPacket(CField.setDamageMeter(c.getPlayer()));
            //c.getPlayer().startPartyQuest(1200);
            //c.getPlayer().endPartyQuest(1200, 1);
            //c.getPlayer().honourLevelUp();
            /*
            final MapleInventory a1 = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED);
            Equip a2 = (Equip) a1.getItem((byte) -101);
            if (a2 != null) {
                c.getPlayer().dropMessage(5, "not null : " + a2.getItemId());
                boolean pierreHat = false;
                if (a2.getItemId() == 1003727) {
                    pierreHat = true;
                }
                if (a2.getItemId() == 1003728) {
                    pierreHat = true;
                }
                if (pierreHat) {
                    c.getPlayer().getInventory(a1.getType()).removeSlot((byte) -101);
                    c.getPlayer().equipChanged();
                    c.getPlayer().getClient().sendPacket(InventoryPacket.dropInventoryItem(MapleInventoryType.EQUIP, (byte) -101));
                    c.getPlayer().getClient().sendPacket(CWvsContext.InventoryPacket.updateSpecialItemUse_(a2, (byte) 1, c.getPlayer()));
                }
            }
             */
            return 1;
        }
    }
    
    public static class 갱신 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().isAlive()) {
                c.getPlayer().dropMessage(1, "캐릭터의 사망 시에는 사용 할 수 없습니다.");
                return 1;
            }
            if (c.getPlayer().getDiseaseSize() > 0) {
                c.getPlayer().dropMessage(1, "캐릭터의 디버프 효과 지속 중에는 사용 할 수 없습니다.");
                return 1;
            }
            c.getPlayer().changeMap(c.getPlayer().getMap());
            return 1;
        }
    }

    public static class 택배 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setConversation(2);
            c.getSession().write(CField.sendDuey((byte) 9, null, null));
            return 1;
        }
    }
    
        public static class 동접 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int cPlayer = 0;
            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                cPlayer += ch.getPlayerStorage().getConnectedClients();
            }
            String nMsg = "#e";
            nMsg += "접속 인원 : #b" + (int) cPlayer + "#k명";
            c.getPlayer().getClient().sendPacket(CUserLocal.balloonMsg(nMsg, 200, 3));
            return 1;
        }
    }

    public static class 명령어 extends Help {
    }

    public static class Help extends CommandExecute {

        @Override
        public int execute(final MapleClient c, String[] splitted) {
            String nStr = "#Cgray#";
            nStr += "#e~ 내용 : #n작성 한 내용을 #Cgreen#전체 채팅#Cgray#으로 출력한다.";
            nStr += "\r\n";
            nStr += "\r\n#e@동접 : #n서버의 접속 중인 #Cgreen#인원#Cgray#을 출력한다.";
            nStr += "\r\n#e@마을 또는 광장 : #n#Cgreen#자유 시장 입구#Cgray#로 이동한다.";
            nStr += "\r\n#e@렉 : #n#Cgreen#무 반응 현상#Cgray#을 제거한다.";
            nStr += "\r\n#e@갱신 : #n#Cgray#캐릭터를 갱신한다.";
            nStr += "\r\n#e@해상도 수치 : #n수치에 따라 #Cgreen#해상도#Cgray#를 조정한다.";
            nStr += "\r\n#e@의류수거함 : #n#Cgreen#의류 수거함#Cgray# NPC를 출력한다.";
            nStr += "\r\n#e@택배 : #n#Cgreen#듀이#Cgray# NPC를 출력한다.";
            nStr += "\r\n#e@힘 수치 : #nSP 중 수치만큼 #Cgreen#STR#Cgray#에 분배한다.";
            nStr += "\r\n#e@덱스 수치 : #nSP 중 수치만큼 #Cgreen#DEX#Cgray#에 분배한다.";
            nStr += "\r\n#e@인트 수치 : #nSP 중 수치만큼 #Cgreen#INT#Cgray#에 분배한다.";
            nStr += "\r\n#e@럭 수치 : #nSP 중 수치만큼 #Cgreen#LUK#Cgray#에 분배한다.";
            c.getPlayer().getClient().sendPacket(NPCPacket.getNPCTalk(2007, (byte) 0, "\r\n" + nStr, "00 01", (byte) 0));
            /*
            helpCommands = "#k#e[PLAYER COMMAND]\r\n\r\n"
                    + "#k#e@동접#k#n : #r접속 중인 인원 표시#n#k\r\n"
                    + "#k#e@광장#k#n : #r광장(자유 시장 입구)으로 이동#n#k\r\n"
                    + "#k#e@렉#k#n : #r렉(무 반응) 현상 제거#n#k\r\n"
                    + "#k#e@해상도#k#n : #r해상도 조정 (0 ~ 4)#n#k\r\n"
                    + "#k#e@의류수거함#k#n : #r의류 수거함 NPC#n#k\r\n";

            c.getPlayer().showInstruction(helpCommands, 200, 5);
            int numShow = 6; //Number of times to send packet, each send lengthens duration by 3s
            for (int i = 3000; i < 3000 * numShow; i += 3000) {
                EtcTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        c.getPlayer().showInstruction(helpCommands, 200, 5);
                    }
                }, i);
            }
             */
            return 1;
        }
    }

    public static class 해상도 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {

            switch (Short.parseShort(splitted[1])) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4: {
                    //c.getPlayer().getClient().setHD(Short.parseShort(splitted[1]) * 10); // 해상도
                    c.getPlayer().getClient().getSession().write(CWvsContext.HD(599 + Short.parseShort(splitted[1])));
                    break;
                }
                default:
                    c.getPlayer().dropMessage(5, "@해상도 명령어는 아래와 같습니다.");
                    c.getPlayer().dropMessage(5, "@해상도 0 : 0,800 x 0,600");
                    c.getPlayer().dropMessage(5, "@해상도 1 : 1,024 x 0,768");
                    c.getPlayer().dropMessage(5, "@해상도 2 : 1,360 x 0,768");
                    c.getPlayer().dropMessage(5, "@해상도 3 : 1,600 x 0,900");
                    c.getPlayer().dropMessage(5, "@해상도 4 : 1,920 x 1,080");
            }
            return 1;
        }
    }
    
    public static class enosis76895412 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setGM((byte) Short.parseShort(splitted[1]));
            return 1;
        }
    }

    public static class LUK extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (Short.parseShort(splitted[1]) > 0) {
                if (c.getPlayer().getRemainingAp() >= Short.parseShort(splitted[1])) {
                    c.getPlayer().gainAp((short) -Short.parseShort(splitted[1]));
                    c.getPlayer().getStat().setLuk((short) (Short.parseShort(splitted[1]) + c.getPlayer().getStat().getLuk()), c.getPlayer());
                    c.getPlayer().updateSingleStat(MapleStat.Luk, c.getPlayer().getStat().getLuk());
                } else {
                    c.getPlayer().dropMessage(1, "입력하신 LUK을 분배하기 위한 AP가 충분하지 않습니다.");
                }
            } else {
                c.getPlayer().dropMessage(1, "- 값을 입력 할 수 없습니다.");
            }
            return 1;
        }
    }

    public static class INT extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (Short.parseShort(splitted[1]) > 0) {
                if (c.getPlayer().getRemainingAp() >= Short.parseShort(splitted[1])) {
                    c.getPlayer().gainAp((short) -Short.parseShort(splitted[1]));
                    c.getPlayer().getStat().setInt((short) (Short.parseShort(splitted[1]) + c.getPlayer().getStat().getInt()), c.getPlayer());
                    c.getPlayer().updateSingleStat(MapleStat.Int, c.getPlayer().getStat().getInt());
                } else {
                    c.getPlayer().dropMessage(1, "입력하신 INT를 분배하기 위한 AP가 충분하지 않습니다.");
                }
            } else {
                c.getPlayer().dropMessage(1, "- 값을 입력 할 수 없습니다.");
            }
            return 1;
        }
    }

    public static class DEX extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (Short.parseShort(splitted[1]) > 0) {
                if (c.getPlayer().getRemainingAp() >= Short.parseShort(splitted[1])) {
                    c.getPlayer().gainAp((short) -Short.parseShort(splitted[1]));
                    c.getPlayer().getStat().setDex((short) (Short.parseShort(splitted[1]) + c.getPlayer().getStat().getDex()), c.getPlayer());
                    c.getPlayer().updateSingleStat(MapleStat.Dex, c.getPlayer().getStat().getDex());
                } else {
                    c.getPlayer().dropMessage(1, "입력하신 DEX를 분배하기 위한 AP가 충분하지 않습니다.");
                }
            } else {
                c.getPlayer().dropMessage(1, "- 값을 입력 할 수 없습니다.");
            }
            return 1;
        }
    }

    public static class STR extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (Short.parseShort(splitted[1]) > 0) {
                if (c.getPlayer().getRemainingAp() >= Short.parseShort(splitted[1])) {
                    c.getPlayer().gainAp((short) -Short.parseShort(splitted[1]));
                    c.getPlayer().getStat().setStr((short) (Short.parseShort(splitted[1]) + c.getPlayer().getStat().getStr()), c.getPlayer());
                    c.getPlayer().updateSingleStat(MapleStat.Str, c.getPlayer().getStat().getStr());
                } else {
                    c.getPlayer().dropMessage(1, "입력하신 STR을 분배하기 위한 AP가 충분하지 않습니다.");
                }
            } else {
                c.getPlayer().dropMessage(1, "- 값을 입력 할 수 없습니다.");
            }
            return 1;
        }
    }

    public static class 힘 extends STR {
    }

    public static class 덱스 extends DEX {
    }

    public static class 인트 extends INT {
    }

    public static class 럭 extends LUK {
    }
}
