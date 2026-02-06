package tools.packet;

import client.MapleCharacter;
import server.maps.MapleMap;
import tools.data.OutPacket;

public class CMonsterCarnival {

    public static byte[] setMonsterCarnival(final MapleCharacter user) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 403);
        oPacket.EncodeByte(0/*user.getCarnivalParty().getTeam()*/);
        oPacket.EncodeInt(0);   // availableCP
        oPacket.EncodeInt(0);   // totalCP
        oPacket.EncodeInt(0);   // availableCP
        oPacket.EncodeInt(0);   // totalCP
        oPacket.EncodeLong(0);  // unk
        return oPacket.getPacket();
    }

    public static byte[] updateCP(int available_cp, int total_cp) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 404);
        oPacket.EncodeInt(available_cp);
        oPacket.EncodeInt(total_cp);
        return oPacket.getPacket();
    }

    public static byte[] updateScore(int red_team_score, int blue_team_score) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 405); // 405
        oPacket.EncodeInt(red_team_score);
        oPacket.EncodeInt(blue_team_score);
        return oPacket.getPacket();
    }

    public static byte[] updateCooldown(int tab_01, int tab_02, int tab_03, int tab_04) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 406);
        oPacket.EncodeInt(1000 * tab_01);
        oPacket.EncodeInt(1000 * tab_02);
        oPacket.EncodeInt(1000 * tab_03);
        oPacket.EncodeInt(1000 * tab_04);
        return oPacket.getPacket();
    }

    public static byte[] showCPMessage(byte tab, byte number, MapleCharacter user) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 407);
        oPacket.EncodeByte(tab);
        oPacket.EncodeByte(number);
        oPacket.EncodeString(user.getName());
        return oPacket.getPacket();
    }

    public static byte[] showCPMessage(int type) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 408);
        /*
        1   : CP가 부족하여 실행 할 수 없습니다.
        2   : 더 이상 몬스터를 소환 할 수 없습니다.
        3   : 더 이상 수호물을 소환 할 수 없습니다
        4   : 이미 소환되어 있는 수호물입니다.
        5   : 쿨타임 적용으로 아직 실행 할 수 없습니다.
        6   : 알 수 없는 이유로 요청이 실패하였습니다.
         */
        oPacket.EncodeByte(type);
        return oPacket.getPacket();
    }

    public static byte[] showDieMessage(int team, String name, int lost_cp) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 409);
        oPacket.EncodeByte(team);
        oPacket.EncodeString(name);
        /*
        lostCP > 0  : [%s]팀의 [%s]님이 전투 불가 상태가 되어, CP %d 잃었습니다.
        lostCP < 1  : [%s]팀의 [%s]님이 전투 불가 상태가 되었으나, 남은 CP가 없어서 CP가 감소하지 않습니다.
         */
        oPacket.EncodeByte(lost_cp);
        return oPacket.getPacket();
    }

    public static byte[] showLeaveMessage(boolean check_leader, int team, String name) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 410);
        /*
        7   : [%s]팀의 파티장이 몬스터 카니발을 중단하여, [%s]님이 새로운 파티장이 되었습니다.
        0   : [%s]팀의 [%s]님이 몬스터 카니발을 중단하였습니다.
         */
        oPacket.EncodeByte(check_leader ? 7 : 0);
        oPacket.EncodeByte(team);
        oPacket.EncodeString(name);
        return oPacket.getPacket();
    }

    public static byte[] showEndMessage(int type) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 411);
        /*
        9   : 몬스터 카니발에서 승리하였습니다. 잠시 후 자동으로 이동하니 잠시만 기다려 주세요.
        10  : 몬스터 카니발에서 안타깝게도 지고 말았습니다. 잠시 후 자동으로 이동하니 잠시만 기다려 주세요.
        11  : 연장전이 종료될 때 까지 승패가 결정되이 않았습니다. 잠시 후 자동으로 이동하니 잠시만 기다려 주세요.
        12  : 상대팀이 너무 일찍 퇴장하여 몬스터 카니발이 종료됩니다. 잠시 후 자동으로 이동하니 잠시만 기다려 주세요.
         */
        oPacket.EncodeByte(type);
        return oPacket.getPacket();
    }

    public static byte[] showUserRank(MapleMap map) {
        OutPacket oPacket = new OutPacket();
        oPacket.EncodeShort((short) 412);

        oPacket.EncodeShort(3);

        oPacket.EncodeInt(1);
        oPacket.EncodeString("이노시스");
        oPacket.EncodeLong(999999);
        oPacket.EncodeByte(0);
        
        oPacket.EncodeInt(2);
        oPacket.EncodeString("버스기사");
        oPacket.EncodeLong(899999);
        oPacket.EncodeByte(0);
        
        oPacket.EncodeInt(3);
        oPacket.EncodeString("버스승객");
        oPacket.EncodeLong(799999);
        oPacket.EncodeByte(0);
        /*
        int userSize = map.getAllCharactersThreadsafe().size();
        oPacket.EncodeShort(userSize);
        for (MapleCharacter user : map.getAllCharactersThreadsafe()) {
            for (int i = 0; i < userSize; i++) {
                oPacket.EncodeInt(user.getId());
                oPacket.EncodeString(user.getName());
                oPacket.EncodeInt(123456); // private score
                oPacket.EncodeByte(0);
            }
        }
         */
        return oPacket.getPacket();
    }
}
