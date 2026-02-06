package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import client.PartTimeJob;
import handling.SendPacketOpcode;
import handling.login.LoginServer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import server.Randomizer;
import tools.HexTool;
import tools.data.OutPacket;

public class CLogin {

    private static final String version;
    private static final int vBeatrice = 6969;

    static {
        int ret = 0;
        ret ^= (vBeatrice & 0x7FFF);
        ret ^= (1 << 15);
        ret ^= ((1 & 0xFF) << 16); //마이너버전체크
        version = String.valueOf(ret);
    }

    public static byte[] getHello(byte[] sendIv, byte[] recvIv) {
        OutPacket mplew = new OutPacket();
        int packetsize = 13 + version.length();
        mplew.writeShort(packetsize);
        mplew.writeShort(291); //KMS Static
        mplew.writeMapleAsciiString(version);
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(1); // 1 = KMS, 2 = KMST, 7 = MSEA, 8 = GlobalMS, 5 = Test Server
        //System.out.println(HexTool.toString(mplew.getPacket()));

        return mplew.getPacket();
    }

    /*public static final byte[] getHello(short mapleVersion, byte[] sendIv, byte[] recvIv) {
        OutPacket mplew = new OutPacket();
        byte kms = 1;
        int ret = 0;
        ret ^= (mapleVersion & 0x7FFF);
        ret ^= (1 << 15);
        ret ^= ((2 & 0xFF) << 16); //마이너버전체크
        String version = String.valueOf(ret);
        int packetsize = 13 + version.length();
        short checkclient = 291;
    
        mplew.writeShort(packetsize);
        mplew.writeShort(checkclient);
        mplew.writeMapleAsciiString(version);
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(kms);
        System.out.print("겟헬로확인");
        return mplew.getPacket();
    }*/
    public static final byte[] getPing() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PING.getValue());
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    /* 00 정상
           01 무반응
           02 지워지거나 접속중지된 아이디
           03 지워지거나 접속중지된 아이디
           04 비밀번호가 일치하지않습니다.
           05 등록되지 않은 아이디
           06 시스템 오류로 접속x
           07 현접-> 로그인창으로가짐
           08 시스템 오류로 접속x
           09 시스템 오류로 접속x
           10 현재 서버에 접속 요청이 많아 처리~
           11 20세 이상만 접속할 수 있습니다.
           12 2차비번 패스
           13 현재 IP로는 마스터 로그인이 불가능합니다.
           14 이 계정은 임시가입 기간이 만료되어 게임이용이 중지됨.
           15 이 계정의 넥슨 아이디는 존재하지 않는 넥슨 아이디.
           16 무반응
           17 웹 인증정보가 일치하지 않습니다.
           18 무반응
           19 현재 임시 차단된 IP를 통해서 접속하셨습니다. -> 후 튕김
           20 ~ 23 무반응
           24 일회용 OTP 무효
           25 일회용 OTP 인증횟수 초과
           26 일회용 OTP 시스템 점검 이용불가
           27 무반응
           28 OTP 체험시간 종료
           29 ~ 33 무반응
           34 월드에 이용자가 많아 접속지연.
           35 OTP 발급신청, 재발급신청상태
           36 OTP 발급신청, 재발급신청상태
     */
    public static final byte[] getAuthSuccessRequest(MapleClient client) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(0);//로그인 status..
        mplew.writeInt(client.getAccID());
        mplew.write(client.getGender());
        mplew.write(0); // Admin byte client.isGm() ? 1 : 0
        mplew.write(0); // Admin byte client.isGm() ? 32 : 0
        mplew.writeInt(0); // vip grade
        mplew.writeInt(0); // nBlockReason
        mplew.writeInt(0); // purchase exp
        mplew.writeShort(0); // pBlockReason
        //mplew.writeLong(0);//클라이언트 ID값
        mplew.write(1);//0과 1체크 1일시 밑에 string 노필요 0일시 필요 ..
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeMapleAsciiString("");// 게이버?
        mplew.writeInt(1);
        mplew.writeInt(1); //캐슈 활성화
        mplew.write(1); //메르 활성화
        mplew.write(1); //데몬 활성화

        return mplew.getPacket();
    }

    public static final byte[] getLoginFailed(int reason) {
        OutPacket mplew = new OutPacket(16);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(reason);
        mplew.write(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static final byte[] getPermBan(byte reason) {
        OutPacket mplew = new OutPacket(16);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(2);
        mplew.writeInt(0);
        mplew.writeShort(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return mplew.getPacket();
    }

    public static final byte[] getTempBan(long timestampTill, byte reason) {
        OutPacket mplew = new OutPacket(17);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(2);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(reason);
        mplew.writeLong(timestampTill);

        return mplew.getPacket();
    }

    public static final byte[] getSecondAuthSuccess(MapleClient client) {
        OutPacket mplew = new OutPacket();

//     mplew.writeShort(SendPacketOpcode.LOGIN_SECOND.getValue());
        mplew.write(0);
        mplew.writeInt(client.getAccID());
        mplew.writeZeroBytes(5);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeLong(2L);
        mplew.writeZeroBytes(3);
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeInt(28);
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeInt(Randomizer.nextInt());
        mplew.write(1);

        return mplew.getPacket();
    }

    public static final byte[] deleteCharResponse(int cid, int state) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        mplew.writeInt(cid);
        /*
        06  : 시스템 오류로 접속할 수 없습니다.
        10  : 현재 서버에 접속 요청이 많아 처리하지 못했습니다.
        16  : 주민번호 뒷자리가 맞지 않습니다.
        18  : 길드 마스터는 삭제 할 수 없습니다.
        20  : 추가 비밀번호가 일치하지 않습니다.
        21  : 약혼 또는 결혼 절차가 진행 중인 캐릭터는 삭제 할 수 없습니다.
        24  : 일회용 비밀번호(U-OTP)가 일치하지 않습니다.
        25  : 일회용 비밀번호(U-OTP)의 인증 오류 횟수를 초과하였습니다.
        26  : 일회용 비밀번호(U-OTP) 시스템 점검으로 인해 서비스를 이용하실 수 없습니다.
        27  : 
        28  : U-OTP 체험 시간이 종료 되었습니다.
        29  : 패밀리가 있는 캐릭터는 삭제할 수 없습니다.
        35  : 일회용 비밀번호(U-OTP) 시스템 점검으로 인해 서비스를 이용하실 수 없습니다.
        36  : 일회용 비밀번호(U-OTP) 시스템 점검으로 인해 서비스를 이용하실 수 없습니다.
        38  : 자유 시장에 고용 상점이 열려 있어서 캐릭터를 삭제 할 수 없습니다.
        44  : 해제시 해킹 및 기타 위험 요소에 노출 될 뿐만 아니라 해킹 추적 서비스 이용이 제한됩니다.
         */
        mplew.write(state);
        mplew.write(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static final byte[] secondPasswordResult(byte op) {
        final OutPacket mplew = new OutPacket(3);
        mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        mplew.write(op);
        return mplew.getPacket();
    }

    public static final byte[] getSecondPasswordConfirm(boolean success) {
        final OutPacket mplew = new OutPacket(3);

        mplew.writeShort(SendPacketOpcode.CHECK_SPW.getValue());
        mplew.write(success ? 0 : 20);
        return mplew.getPacket();
    }

    public static final byte[] secondPw(final boolean success) {
        final OutPacket mplew = new OutPacket(3);
        mplew.writeShort(SendPacketOpcode.REGISTER_SECONDPW.getValue());
        mplew.write(success ? 0 : 0x14);

        return mplew.getPacket();
    }

    public static final byte[] secondPwError() {
        OutPacket mplew = new OutPacket(3);

        mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] enableRecommended() {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.ENABLE_RECOMMENDED.getValue());
        mplew.writeInt(0); //월드ID 캐릭터수가제일많은..
        return mplew.getPacket();
    }

    public static byte[] sendRecommended(int world, String message) {
        OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.SEND_RECOMMENDED.getValue());
        mplew.write(message != null ? 1 : 0);
        if (message != null) {
            mplew.writeInt(world);
            mplew.writeMapleAsciiString(message);
        }
        return mplew.getPacket();
    }

    public static final byte[] getServerList(int serverId, Map<Integer, Integer> channelLoad) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(serverId); // 0 = Aquilla, 1 = bootes, 2 = cass, 3 = delphinus
        mplew.writeMapleAsciiString(LoginServer.getServerName());
        mplew.write(LoginServer.getFlag());
        mplew.writeMapleAsciiString(LoginServer.getEventMessage());
        mplew.writeShort(100);
        mplew.writeShort(100);
        int lastChannel = 1;
        Set channels = channelLoad.keySet();
        for (int i = 30; i > 0; i--) {
            if (channels.contains(Integer.valueOf(i))) {
                lastChannel = i;
                break;
            }
        }
        mplew.write(lastChannel);

        for (int i = 1; i <= lastChannel; i++) {
            int load;
            if (channels.contains(Integer.valueOf(i))) {
                load = (channelLoad.get(Integer.valueOf(i)));
            } else {
                load = 1200;
            }
            mplew.writeMapleAsciiString(LoginServer.getServerName() + "-" + (i == 1 ? "1" : i == 2 ? "20세이상" : i - 1));
            mplew.writeInt(load);
            mplew.write(serverId);
            mplew.write(i - 1);
            mplew.write(0);
        }
        mplew.writeShort(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static final byte[] getEndOfServerList() {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.writeShort(255);

        return mplew.getPacket();
    }

    public static final byte[] getLoginWelcome() {
        List flags = new LinkedList();

        return CField.spawnFlags(flags);
    }

    public static final byte[] getServerStatus(int status) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        mplew.writeShort(status);

        return mplew.getPacket();
    }

    public static final byte[] getChannelSelected() {
        OutPacket mplew = new OutPacket();

//     mplew.writeShort(SendPacketOpcode.CHANNEL_SELECTED.getValue());
        mplew.writeZeroBytes(3);

        return mplew.getPacket();
    }

    public static final byte[] getCharListtest(String secondpw, List<MapleCharacter> chars, int charslots) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        mplew.write(1);
        mplew.write(chars.size());
        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, (!chr.isGM()) && (chr.getLevel() >= 30), false);
        }
        mplew.write(secondpw == null ? 0 : 1); // second pw request
        mplew.write(0);
        mplew.writeInt(charslots);
        mplew.writeInt(0);
        //  mplew.writeInt(-1);
        mplew.writeReversedLong(PacketHelper.getTime(System.currentTimeMillis()));
        //   mplew.write(0);//캐릭터네임체인지
        return mplew.getPacket();
    }

    public static final byte[] getCharList(String secondpw, List<MapleCharacter> chars, int charslots) {
        OutPacket mplew = new OutPacket();
        /* 00 정상
           01 무반응
           02 지워지거나 접속중지된 아이디
           03 지워지거나 접속중지된 아이디
           04 비밀번호가 일치하지않습니다.
           05 등록되지 않은 아이디
           06 시스템 오류로 접속x
           07 현접-> 로그인창으로가짐
           08 시스템 오류로 접속x
           09 시스템 오류로 접속x
           10 현재 서버에 접속 요청이 많아 처리~
           11 20세 이상만 접속할 수 있습니다.
           12 2차비번 패스
           13 현재 IP로는 마스터 로그인이 불가능합니다.
           14 이 계정은 임시가입 기간이 만료되어 게임이용이 중지됨.
           15 이 계정의 넥슨 아이디는 존재하지 않는 넥슨 아이디.
           16 무반응
           17 웹 인증정보가 일치하지 않습니다.
           18 무반응
           19 현재 임시 차단된 IP를 통해서 접속하셨습니다. -> 후 튕김
           20 ~ 23 무반응
           24 일회용 OTP 무효
           25 일회용 OTP 인증횟수 초과
           26 일회용 OTP 시스템 점검 이용불가
           27 무반응
           28 OTP 체험시간 종료
           29 ~ 33 무반응
           34 월드에 이용자가 많아 접속지연.
           35 OTP 발급신청, 재발급신청상태
           36 OTP 발급신청, 재발급신청상태
         */
        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        mplew.write(0);
        mplew.write(chars.size());
        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, (!chr.isGM()) && (chr.getLevel() >= 30), false);
        }
        mplew.write(secondpw == null ? 0 : 1); // second pw request
        mplew.write(1);
        mplew.writeInt(charslots);
        mplew.writeInt(0);//캐시캐릭터슬롯
        //  mplew.writeInt(255);
        mplew.writeReversedLong(PacketHelper.getTime(System.currentTimeMillis()));
        //   mplew.write(0);//캐릭터네임체인지
        return mplew.getPacket();
    }

    public static final byte[] addNewCharEntry(MapleCharacter chr, boolean worked) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        mplew.write(worked ? 0 : 1);
        addCharEntry(mplew, chr, false, false);
        return mplew.getPacket();
    }

    public static final byte[] charNameResponse(String charname, boolean nameUsed) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    private static final void addCharEntry(OutPacket mplew, MapleCharacter chr, boolean ranking, boolean viewAll) {
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.write(0);
        mplew.write(ranking ? 1 : 0);
        if (ranking) {
            mplew.writeInt(chr.getRank());
            mplew.writeInt(chr.getRankMove());
            mplew.writeInt(chr.getJobRank());
            mplew.writeInt(chr.getJobRankMove());
        }
    }

    public static byte[] updatePartTimeJob(PartTimeJob partTime) {
        OutPacket mplew = new OutPacket(21);
        mplew.writeShort(SendPacketOpcode.PART_TIME.getValue());
        mplew.writeInt(partTime.getCharacterId());
        mplew.write(0);
        PacketHelper.addPartTimeJob(mplew, partTime);
        //System.out.println(mplew.)
        return mplew.getPacket();
    }

    public static byte[] enableSpecialCreation(int accid, boolean enable) {
        OutPacket mplew = new OutPacket();

        // mplew.writeShort(SendPacketOpcode.SPECIAL_CREATION.getValue());
        mplew.writeInt(accid);
        mplew.write(enable ? 0 : 1);
        mplew.write(0);

        return mplew.getPacket();
    }
}
