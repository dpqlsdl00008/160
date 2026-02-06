package handling.login.handler;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Calendar;

import client.inventory.Item;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.PartTimeJob;
import client.Skill;
import client.SkillEntry;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import client.SkillFactory;
import database.DatabaseConnection;
import handling.UIType;
import handling.channel.ChannelServer;
import handling.channel.handler.DueyHandler;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.packet.CField;
import tools.packet.CLogin;
import tools.data.LittleEndianAccessor;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.PacketHelper;

public class CharLoginHandler {

    private static final boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        if (c.loginAttempt > 5) {
            return true;
        }
        return false;
    }

    public static final void login(final LittleEndianAccessor slea, final MapleClient c) {
        slea.skip(22);
        String login = slea.readMapleAsciiString();
        String pwd = slea.readMapleAsciiString();
        int loginok = 0;
        final boolean ipBan = c.hasBannedIP();

        final boolean gender = login.contains("F^") || login.contains("f^") || login.contains("f_") || login.contains("F_");
        final boolean checkip = LoginAutoRegister.checkIP(c.getSessionIPAddress());
        loginok = c.login(login, pwd, ipBan);
        final Calendar tempbannedTill = c.getTempBanCalendar();
        if (ipBan || loginok == 3) {
            c.sendPacket(CLogin.getLoginFailed(3));
            return;
        }
        if (loginok == 5 && !ipBan/* && checkip == false*/) {
            LoginAutoRegister.registerAccount(login, pwd, c.getSessionIPAddress(), gender);
            //c.sendPacket(CWvsContext.serverNotice(1, "[자동가입] 자동가입이 완료 되었습니다."));
            c.sendPacket(CLogin.getLoginFailed(20));
            return;
        }
        if (loginok == 5 && !ipBan/* && checkip == true*/) {
            c.sendPacket(CWvsContext.serverNotice(1, "계정생성 가능한 횟수를 초과 하였습니다."));
            c.sendPacket(CLogin.getLoginFailed(18));
        }
        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                c.sendPacket(CLogin.getLoginFailed(loginok));
            } else {
                c.getSession().close();
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                c.sendPacket(CLogin.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getGBanReason()));
            } else {
                c.getSession().close();
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }

    public static final void ServerListRequest(final MapleClient c) {
        c.getSession().write(CLogin.getLoginWelcome());

        c.getSession().write(CLogin.getServerList(0, LoginServer.getLoad()));
        //   c.getSession().write(LoginPacket.getServerList(1, LoginServer.getLoad()));
        c.getSession().write(CLogin.getEndOfServerList());
        c.getSession().write(CLogin.enableRecommended());
        c.getSession().write(CLogin.sendRecommended(0, LoginServer.getEventMessage()));
    }

    public static final void ServerStatusRequest(final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.getSession().write(CLogin.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write(CLogin.getServerStatus(1));
        } else {
            c.getSession().write(CLogin.getServerStatus(0));
        }
    }

    public static final void CharlistRequest(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        slea.readByte(); //2?
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        c.setWorld(server);
        c.setChannel(channel);
        final List<MapleCharacter> chars = c.loadCharacters(server);
        c.sendPacket(CLogin.getCharList(c.getSecondPassword(), chars, c.getCharacterSlots()));
    }

    public static final void updateCCards(final LittleEndianAccessor slea, final MapleClient c) {
        final Map<Integer, Integer> cids = new LinkedHashMap<>();
        for (int i = 1; i <= 6; i++) { // 6 chars
            final int charId = slea.readInt();
            cids.put(i, charId);
        }
        c.updateCharacterCards(cids);
    }

    public static final void CheckCharName(final String name, final MapleClient c) {
        c.getSession().write(CLogin.charNameResponse(name, !(MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()))));
    }

    public static void CreateChar(final LittleEndianAccessor slea, final MapleClient c) {
        final String name = slea.readMapleAsciiString();
        JobType jobType = JobType.getByType(slea.readInt());
        final short db = slea.readShort();
        final byte gender = slea.readByte();
        byte skinColor = slea.readByte();
        byte itemcount = slea.readByte();
        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        final MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        switch (jobType) {
            case Hayato: {
                slea.skip(24);
                newchar.setFace(gender == 0 ? 20073 : 24063);
                newchar.setHair(gender == 0 ? 40070 : 38890);
                Item item = li.getEquipById(1003567);
                item.setPosition((byte) -1);
                equip.addFromDB(item);
                item = li.getEquipById(1052473);
                item.setPosition((byte) -5);
                equip.addFromDB(item);
                item = li.getEquipById(1072678);
                item.setPosition((byte) -7);
                equip.addFromDB(item);
                item = li.getEquipById(1542048);
                item.setPosition((byte) -11);
                equip.addFromDB(item);
                break;
            }
            case Kanna: {
                slea.skip(24);
                newchar.setFace(gender == 0 ? 23065 : 21068);
                newchar.setHair(gender == 0 ? 35820 : 41170);
                Item item = li.getEquipById(gender == 0 ? 1000064 : 1003568);
                item.setPosition((byte) -1);
                equip.addFromDB(item);
                item = li.getEquipById(1052472);
                item.setPosition((byte) -5);
                equip.addFromDB(item);
                item = li.getEquipById(1072674);
                item.setPosition((byte) -7);
                equip.addFromDB(item);
                item = li.getEquipById(1552044);
                item.setPosition((byte) -11);
                equip.addFromDB(item);
                break;
            }
            case Mukhyun: {
                slea.skip(24);
                newchar.setFace(gender == 0 ? 50046 : 51059);
                newchar.setHair(gender == 0 ? 60050 : 61040);
                Item item = li.getEquipById(gender == 0 ? 1050575 : 1051647);
                item.setPosition((byte) -5);
                equip.addFromDB(item);
                item = li.getEquipById(1073510);
                item.setPosition((byte) -7);
                equip.addFromDB(item);
                item = li.getEquipById(1403029);
                item.setPosition((byte) -11);
                equip.addFromDB(item);
                break;
            }
            default: {
                for (int i = 0; i < itemcount; i++) {
                    int itemid = slea.readInt();
                    if (GameConstants.isFace(itemid)) {
                        newchar.setFace(itemid);
                    } else if (GameConstants.isHair(itemid)) {
                        newchar.setHair(itemid);
                    } else if (GameConstants.isAccessory(itemid)) {
                        newchar.setDemonMarking(itemid);
                    } else {
                        byte wherepot = GameConstants.EqitemPostionById(itemid);
                        Item item = li.getEquipById(itemid);
                        item.setPosition((byte) wherepot);
                        equip.addFromDB(item);
                    }
                }
                break;
            }
        }

        newchar.setWorld((byte) c.getWorld());
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor(skinColor);
        switch (jobType) {
            case CannonShooter:
                final Map<Skill, SkillEntry> ss0 = new HashMap<>();
                ss0.put(SkillFactory.getSkill(110), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(ss0, false);
                break;
            case Cygnus: // Cygnus            
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
                final Map<Skill, SkillEntry> ss2 = new HashMap<>();
                newchar.changeSkillLevel_Skip(ss2, false);
                break;
            case Mercedes:
                final Map<Skill, SkillEntry> ss5 = new HashMap<>();
                ss5.put(SkillFactory.getSkill(20020109), new SkillEntry((byte) 1, (byte) 1, -1));//엘프의회복
                ss5.put(SkillFactory.getSkill(20021110), new SkillEntry((byte) 1, (byte) 1, -1));//엘프의축복
                ss5.put(SkillFactory.getSkill(20020111), new SkillEntry((byte) 1, (byte) 1, -1));//스타일리쉬무브
                ss5.put(SkillFactory.getSkill(20020112), new SkillEntry((byte) 1, (byte) 1, -1));//왕의자격
                newchar.changeSkillLevel_Skip(ss5, false);
                if (newchar != null) {
                    final Item item = li.getEquipById(1352000);
                    item.setPosition((byte) -10); // 메르세데스
                    equip.addFromDB(item);
                }
                break;
            case Demon: //Demon
                final Map<Skill, SkillEntry> ss6 = new HashMap<>();
                ss6.put(SkillFactory.getSkill(30010185), new SkillEntry((byte) 1, (byte) 1, -1));//데모닉블러드
                ss6.put(SkillFactory.getSkill(30010112), new SkillEntry((byte) 1, (byte) 1, -1));//데몬스퓨리
                ss6.put(SkillFactory.getSkill(30010111), new SkillEntry((byte) 1, (byte) 1, -1));//데쓰커스
                ss6.put(SkillFactory.getSkill(30010110), new SkillEntry((byte) 1, (byte) 1, -1));//데몬점프
                ss6.put(SkillFactory.getSkill(30011109), new SkillEntry((byte) 1, (byte) 1, -1));//데빌윙즈
                newchar.changeSkillLevel_Skip(ss6, false);
                break;
            case Phantom:
                final Map<Skill, SkillEntry> ss7 = new HashMap<>();
                ss7.put(SkillFactory.getSkill(20031203), new SkillEntry((byte) 1, (byte) 1, -1));//리턴오브팬텀
                ss7.put(SkillFactory.getSkill(20030204), new SkillEntry((byte) 1, (byte) 1, -1));//데들리인스팅트
                ss7.put(SkillFactory.getSkill(20031205), new SkillEntry((byte) 1, (byte) 1, -1));//팬텀슈라우드
                ss7.put(SkillFactory.getSkill(20030206), new SkillEntry((byte) 1, (byte) 1, -1));//하이덱스터러티
                ss7.put(SkillFactory.getSkill(20031207), new SkillEntry((byte) 1, (byte) 1, -1));//스틸스킬
                ss7.put(SkillFactory.getSkill(20031208), new SkillEntry((byte) 1, (byte) 1, -1));//스킬매니저먼트
                ss7.put(SkillFactory.getSkill(20031209), new SkillEntry((byte) 1, (byte) 1, -1));//저지먼트
                newchar.changeSkillLevel_Skip(ss7, false);
                final Item item = li.getEquipById(1352100);
                item.setPosition((byte) -10);
                equip.addFromDB(item);
                break;
            case Mihile:
                final Map<Skill, SkillEntry> ss8 = new HashMap<>();
                ss8.put(SkillFactory.getSkill(50001214), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(ss8, false);
                break;
            case Hayato:
                final Map<Skill, SkillEntry> hayato = new HashMap<>();
                hayato.put(SkillFactory.getSkill(60000000), new SkillEntry((byte) 1, (byte) 1, -1));
                hayato.put(SkillFactory.getSkill(60001002), new SkillEntry((byte) 1, (byte) 1, -1));
                hayato.put(SkillFactory.getSkill(60000067), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(hayato, false);

                Item v1 = new Item(2431876, (short) 1, (short) 1, (byte) 0);
                v1.setPosition((byte) 1);
                newchar.getInventory(MapleInventoryType.USE).addFromDB(v1);

                Item v2 = new Item(2431877, (short) 1, (short) 1, (byte) 0);
                v2.setPosition((byte) 2);
                newchar.getInventory(MapleInventoryType.USE).addFromDB(v2);

                break;
            case Kanna:
                final Map<Skill, SkillEntry> kanna = new HashMap<>();
                kanna.put(SkillFactory.getSkill(60010000), new SkillEntry((byte) 1, (byte) 1, -1));
                kanna.put(SkillFactory.getSkill(60010001), new SkillEntry((byte) 1, (byte) 1, -1));
                kanna.put(SkillFactory.getSkill(60010109), new SkillEntry((byte) 1, (byte) 1, -1));
                kanna.put(SkillFactory.getSkill(60011023), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(kanna, false);

                Item v3 = new Item(2431876, (short) 1, (short) 1, (byte) 0);
                v3.setPosition((byte) 1);
                newchar.getInventory(MapleInventoryType.USE).addFromDB(v3);

                Item v4 = new Item(2431877, (short) 1, (short) 1, (byte) 0);
                v4.setPosition((byte) 2);
                newchar.getInventory(MapleInventoryType.USE).addFromDB(v4);

                break;
            case Mukhyun:
                final Map<Skill, SkillEntry> mukhyun = new HashMap<>();
                mukhyun.put(SkillFactory.getSkill(60020001), new SkillEntry((byte) 1, (byte) 1, -1));
                newchar.changeSkillLevel_Skip(mukhyun, false);

                Item v5 = new Item(2431876, (short) 1, (short) 1, (byte) 0);
                v5.setPosition((byte) 1);
                newchar.getInventory(MapleInventoryType.USE).addFromDB(v5);

                Item v6 = new Item(2431877, (short) 1, (short) 1, (byte) 0);
                v6.setPosition((byte) 2);
                newchar.getInventory(MapleInventoryType.USE).addFromDB(v6);

                break;

            case BeastTamer:
                
                Item v7 = new Item(2431876, (short) 1, (short) 1, (byte) 0);
                v7.setPosition((byte) 1);
                newchar.getInventory(MapleInventoryType.USE).addFromDB(v7);

                Item v8 = new Item(2431877, (short) 1, (short) 1, (byte) 0);
                v8.setPosition((byte) 2);
                newchar.getInventory(MapleInventoryType.USE).addFromDB(v8);
                break;
        }
        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) && (c.isGm() || c.canMakeCharacter(c.getWorld()))) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, db);
            c.getSession().write(CLogin.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.getSession().write(CLogin.addNewCharEntry(newchar, false));
        }
    }

    public static final void CreateUltimate(final LittleEndianAccessor slea, final MapleClient c) { //궁극의 모험가
        if (!c.isLoggedIn()
                || c.getPlayer() == null
                || c.getPlayer().getLevel() < 120
                || c.getPlayer().getMapId() != 130000000
                || !c.canMakeCharacter(c.getPlayer().getWorld())) {
            c.sendPacket(CUserLocal.ultimateCharacterResult(3));
            return;
        }
        final String name = slea.readMapleAsciiString();
        final int job = slea.readInt();
        if (job < 110 || job > 520 || job % 10 > 0 || (job % 100 != 10 && job % 100 != 20 && job % 100 != 30) || job == 430) {
            c.sendPacket(CUserLocal.ultimateCharacterResult(0));
            return;
        }
        final int face = slea.readInt();
        final int hair = slea.readInt();

        final int hat = slea.readInt();
        final int top = slea.readInt();
        final int glove = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getPlayer().getGender();
        JobType jobType = JobType.UltimateAdventurer;

        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld((byte) c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3); //troll
        newchar.setLevel((short) 50);
        newchar.getStat().str = (short) 4;
        newchar.getStat().dex = (short) 4;
        newchar.getStat().int_ = (short) 4;
        newchar.getStat().luk = (short) 4;
        newchar.setRemainingAp((short) 254); //49*5 + 25 - 16
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122); //2 from job advancements. 120 from leveling. (mages get +6)
        newchar.getStat().maxhp += 150; //Beginner 10 levels
        newchar.getStat().maxmp += 125;
        newchar.getStat().hp += 150; //Beginner 10 levels
        newchar.getStat().mp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600; //Job Advancement
                newchar.getStat().maxhp += 2000; //Levelup 40 times
                newchar.getStat().maxmp += 200;
                newchar.getStat().hp += 600; //Job Advancement
                newchar.getStat().hp += 2000; //Levelup 40 times
                newchar.getStat().mp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500; //Levelup 40 times
                newchar.getStat().maxmp += 2000;
                newchar.getStat().mp += 600;
                newchar.getStat().hp += 500; //Levelup 40 times
                newchar.getStat().mp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900; //Levelup 40 times
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500;
                newchar.getStat().mp += 250;
                newchar.getStat().hp += 900; //Levelup 40 times
                newchar.getStat().mp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450; //Levelup 20 times
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800; //Levelup 20 times
                newchar.getStat().maxmp += 400;
                newchar.getStat().hp += 500;
                newchar.getStat().mp += 250;
                newchar.getStat().hp += 450; //Levelup 20 times
                newchar.getStat().mp += 300;
                newchar.getStat().hp += 800; //Levelup 20 times
                newchar.getStat().mp += 400;
                break;
            default:
                return;
        }
        for (int i = 2490; i < 2507; i++) {
            newchar.setQuestAdd(MapleQuest.getInstance(i), (byte) 2, null);
        }
        newchar.setQuestAdd(MapleQuest.getInstance(29947), (byte) 2, null);
        newchar.setQuestAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER), (byte) 0, c.getPlayer().getName());

        final Map<Skill, SkillEntry> ss = new HashMap<>();
        ss.put(SkillFactory.getSkill(1074 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(1195 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(80), new SkillEntry((byte) 1, (byte) 1, -1));
        newchar.changeSkillLevel_Skip(ss, false);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        final int[] items = {1142257, hat, top, shoes, glove, weapon/*, hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1*/}; //brilliant = fine+1
        for (byte i = 0; i < items.length; i++) {
            final MapleInventory equipped = newchar.getInventory(MapleInventoryType.EQUIPPED);
            Item eqp = li.getEquipById(items[i]);
            eqp.setPosition(GameConstants.EqitemPostionById(items[i]));
            equipped.addFromDB(eqp);
        }

        final int[] v2 = {hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1};
        for (byte j = 0; j < v2.length; j++) {
            Item item = li.getEquipById(v2[j]);
            item.setPosition((byte) (j + 1));
            newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
        }

        for (byte k = 0; k < 8; k++) {
            Item v3 = new Item(2430443 + k, (short) 1, (short) 1, (byte) 0);
            v3.setPosition((byte) (k + 1));
            newchar.getInventory(MapleInventoryType.USE).addFromDB(v3);
        }

        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name))) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, (short) 0);
            MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.sendPacket(CUserLocal.ultimateCharacterResult(0));
            c.sendPacket(CUserLocal.closeUI(UIType.CREATE_PREMIUMADVENTURER));
        } else {
            c.sendPacket(CUserLocal.ultimateCharacterResult(1));
        }
    }

    public static final void CharnameChangeCheck(final LittleEndianAccessor slea, final MapleClient c) {

        final int Character_ID = slea.readInt();
        String name1 = slea.readMapleAsciiString();//오리지널네임
        String name2 = slea.readMapleAsciiString();//바꿀닉네임
        System.out.println(MapleCharacterUtil.getNameById(Character_ID));
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET name = ? WHERE id = ?");
            ps.setString(1, name2);
            ps.setInt(2, Character_ID);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("SQLException 캐릭터 정보가 존재하지 않습니다.");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        final List<MapleCharacter> chars = c.loadCharacters(0);
        c.getSession().write(CLogin.getCharListtest(c.getSecondPassword(), chars, c.getCharacterSlots()));//안쏘면 병신댐
        c.getSession().write(CWvsContext.serverNotice(1, "캐릭터 닉네임이 " + name2 + "로 변경되었습니다."));
    }

    public static final void DeleteChar(final LittleEndianAccessor slea, final MapleClient c) {
        String Secondpw_Client = slea.readMapleAsciiString();
        final int Character_ID = slea.readInt();
        byte state = 0;
        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (Secondpw_Client == null) { // Client's hacking
                c.getSession().close();
                return;
            } else {
                if (!c.CheckSecondPassword(Secondpw_Client)) { // Wrong Password
                    state = 20;
                }
            }
        }
        if (state == 0) {
            state = (byte) c.deleteCharacter(Character_ID);
        }
        c.getSession().write(CLogin.deleteCharResponse(Character_ID, state));
    }

    public static void checkSecondPassword(final LittleEndianAccessor slea, final MapleClient c) {
        String spw = slea.readMapleAsciiString();
        c.getSession().write(CLogin.getSecondPasswordConfirm(c.CheckSecondPassword(spw)));
    }

    public static final void Character_RegisterSecondPassword(final LittleEndianAccessor slea, final MapleClient c) {
        final String SP = slea.readMapleAsciiString();
        c.setSecondPassword(SP);
        c.updateSecondPassword();
        c.getSession().write(CLogin.secondPw(true));
    }

    public static final void Change_SecondPassword(final LittleEndianAccessor slea, final MapleClient c) {
        final String oldPic = slea.readMapleAsciiString();
        final String newPic = slea.readMapleAsciiString();
        if (c.CheckSecondPassword(oldPic)) {
            c.setSecondPassword(newPic);
            c.updateSecondPassword();
            c.updatGender();
            c.sendPacket(CLogin.secondPw(true));
        } else {
            c.sendPacket(CLogin.secondPwError());
        }
    }

    public static final void Character_WithSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean view) {
        final String password = slea.readMapleAsciiString();
        final int charId = slea.readInt();
        if (!c.isLoggedIn() || loginFailCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null) { // TODOO: MULTI WORLDS
            c.getSession().close();
            return;
        }

        if (c.CheckSecondPassword(password)) {
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }
            final String s = c.getSessionIPAddress();
            LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel());
            //   LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP());

            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
            c.getSession().write(CField.getServerIP(Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
        } else {
            c.getSession().write(CLogin.getSecondPasswordConfirm(false));
        }
    }

    public static void PartJob(LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer() != null || !c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final byte mode = slea.readByte();
        final int cid = slea.readInt();
        if (mode == 1) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            final byte job = slea.readByte();
            if (job < 0 || job > 5 || partTime.getReward() > 0 || (partTime.getJob() > 0 && partTime.getJob() <= 5)) {
                c.getSession().close();
                return;
            }
            partTime.setTime(PacketHelper.getTime(System.currentTimeMillis()));
            partTime.setJob(job);
            c.getSession().write(CLogin.updatePartTimeJob(partTime));
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
        } else if (mode == 2) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            if (partTime.getReward() > 0
                    || partTime.getJob() < 0 || partTime.getJob() > 5) {
                c.getSession().close();
                return;
            }

            long distance = (System.currentTimeMillis() - partTime.getTime()) / (60 * 60 * 1000L);
            if (distance > 1) {
                //  partTime.setReward((int) (((partTime.getJob() + 1) * 1000L) + distance));
                if (distance > 3) {
                    distance = 3;
                }
                DueyHandler.addNewItemToDb(4310000, (short) distance, 0, cid, "[보상]", "보상지급 ", true);
                partTime.setReward(0);

            } else {
                partTime.setJob((byte) 0);
                partTime.setReward(0);
            }
            partTime.setTime(System.currentTimeMillis());
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
            c.sendPacket(CLogin.updatePartTimeJob(partTime));

        }
    }
}
