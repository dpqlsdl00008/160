package tools.packet;

import client.InnerSkillValueHolder;
import client.CharacterTemporaryStat;
import client.MapleCharacter;
import client.MapleCharacterCards;
import client.MapleClient;
import client.MapleCoolDownValueHolder;
import client.MapleQuestStatus;
import client.MapleTrait;
import client.PartTimeJob;
import client.Skill;
import client.SkillEntry;
import client.inventory.Equip;
import client.inventory.EquipSpecialStat;
import client.inventory.EquipStat;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.Buffstat;
import handling.channel.ChannelServer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SimpleTimeZone;

import server.MapleItemInformationProvider;
import server.MapleShop;
import server.MapleShopItem;
import server.Randomizer;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.AbstractPlayerStore;
import server.shops.IMaplePlayerShop;
import server.shops.MapleMiniGame;
import tools.BitTools;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;
import tools.data.OutPacket;

public class PacketHelper {

    public static final long FT_UT_OFFSET = 116445060000000000L;
    public final static long MAX_TIME = 150842304000000000L; //00 80 05 BB 46 E6 17 02
    public final static long ZERO_TIME = 94354848000000000L; //00 40 E0 FD 3B 37 4F 01
    public final static long PERMANENT = 150841440000000000L; // 00 C0 9B 90 7D E5 17 02

    public static final long getKoreanTimestamp(long realTimestamp) {
        return getTime(realTimestamp);
    }

    public static final long getTime(long realTimestamp) {
        if (realTimestamp == -1L) {
            return MAX_TIME;
        }
        if (realTimestamp == -2L) {
            return ZERO_TIME;
        }
        if (realTimestamp == -3L) {
            return PERMANENT;
        }
        return (realTimestamp * 10000L) + FT_UT_OFFSET;
    }

    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (SimpleTimeZone.getDefault().inDaylightTime(new Date())) {
            timeStampinMillis -= 3600000L;
        }
        long time;

        if (roundToMinutes) {
            time = timeStampinMillis / 1000L / 60L * 600000000L;
        } else {
            time = timeStampinMillis * 10000L;
        }
        return time + 116444592000000000L;
    }

    public static void addQuestInfo(OutPacket mplew, MapleCharacter chr) {
        final List<MapleQuestStatus> started = chr.getStartedQuests();
        mplew.write(1);

        mplew.writeShort(started.size());
        for (final MapleQuestStatus q : started) {
            mplew.writeShort(q.getQuest().getId());
            if (q.hasMobKills()) {
                StringBuilder sb = new StringBuilder();
                for (Iterator i$ = q.getMobKills().values().iterator(); i$.hasNext();) {
                    int kills = ((Integer) i$.next()).intValue();
                    sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
                }
                mplew.writeMapleAsciiString(sb.toString());
            } else {
                if (q.getCustomData() != null) {
                    if (q.getCustomData().startsWith("time_")) {
                        mplew.writeShort(9);
                        mplew.write(1);
                        mplew.writeLong(PacketHelper.getTime(Long.parseLong(q.getCustomData().substring(5))));
                    } else {
                        mplew.writeMapleAsciiString(q.getCustomData());
                    }
                } else {
                    mplew.writeZeroBytes(2);
                }
            }
        }
    }

    public static void addQuestClearInfo(OutPacket mplew, MapleCharacter chr) {
        //( v316 & 0x4000 )
        mplew.write(1);
        final List<MapleQuestStatus> completed = chr.getCompletedQuests();
        mplew.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            mplew.writeShort(q.getQuest().getId());
            mplew.writeLong(getTime(q.getCompletionTime()));
        }
    }

    public static final void addSkillInfo(OutPacket mplew, MapleCharacter chr) {
        final Map<Skill, SkillEntry> skills = chr.getSkills();
        mplew.write(1);
        mplew.writeShort(skills.size());
        for (Map.Entry skill : skills.entrySet()) {
            if (skill != null) {
                if ((Skill) skill.getKey() != null) {
                    mplew.writeInt(((Skill) skill.getKey()).getId());
                    mplew.writeInt(((SkillEntry) skill.getValue()).skillevel);
                    addExpirationTime(mplew, ((SkillEntry) skill.getValue()).expiration);
                    if (((Skill) skill.getKey()).isSkillNeedMasterLevel()) {
                        mplew.writeInt(((SkillEntry) skill.getValue()).masterlevel);
                    }
                }
            }
        }
    }

    public static final void addCoolDownInfo(OutPacket mplew, MapleCharacter chr) {
        final List<MapleCoolDownValueHolder> cd = chr.getCooldowns();
        mplew.writeShort(cd.size());
        for (MapleCoolDownValueHolder cooling : cd) {
            mplew.writeInt(cooling.skillId);
            mplew.writeInt((int) (cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
        }
    }

    public static final void addRocksInfo(OutPacket mplew, MapleCharacter chr) {
        int[] mapz = chr.getRegRocks();
        for (int i = 0; i < 5; i++) {
            mplew.writeInt(mapz[i]);
        }

        int[] map = chr.getRocks();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(map[i]);
        }

        int[] maps = chr.getHyperRocks();
        for (int i = 0; i < 13; i++) {
            mplew.writeInt(maps[i]);
        }
    }

    public static final void addMinigameInfo(OutPacket mplew, MapleCharacter chr) {
        int minigameinfosize = 0;
        mplew.writeShort(minigameinfosize);
        if (minigameinfosize > 0) {
            do {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                --minigameinfosize;
            } while (minigameinfosize > 0);
        }
    }

    public static final void addRingInfo(OutPacket mplew, MapleCharacter chr) {

        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
        List<MapleRing> cRing = aRing.getLeft();
        mplew.writeShort(cRing.size());
        for (MapleRing ring : cRing) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(ring.getPartnerName(), 13);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
        }
        List<MapleRing> fRing = aRing.getMid();
        mplew.writeShort(fRing.size());
        for (MapleRing ring : fRing) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(ring.getPartnerName(), 13);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
        List<MapleRing> mRing = aRing.getRight();
        mplew.writeShort(mRing.size());
        int marriageId = 30000;
        for (MapleRing ring : mRing) {
            mplew.writeInt(marriageId);
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeShort(3);
            mplew.writeInt(ring.getItemId());
            mplew.writeInt(ring.getItemId());
            mplew.writeAsciiString(chr.getName(), 13);
            mplew.writeAsciiString(ring.getPartnerName(), 13);
        }
    }

    public static void addInventoryInfo(OutPacket mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getMeso());

        MapleInventory ivCon = chr.getInventory(MapleInventoryType.USE);
        List<Item> consumed = ivCon.newList();
        int count = 0;
        int[][] expConsumeList = {
            {2230000, 1, 30, 470940, GameConstants.consume_2230000},
            {2230001, 30, 70, 30570854, GameConstants.consume_2230001},
            {2230002, 1, 30, 470940, GameConstants.consume_2230002},
            {2230003, 30, 70, 30570854, GameConstants.consume_2230003},
            {2230004, 12, 18, 26002, GameConstants.consume_2230004},};
        for (Item item : consumed) {
            if ((item.getItemId() / 10000) == 223) {
                count++;
            }
        }
        mplew.writeInt(count);
        if (count > 0) {
            for (Item item : consumed) {
                for (int i = 0; i < expConsumeList.length; i++) {
                    MapleQuestStatus qr = chr.getQuest(MapleQuest.getInstance(expConsumeList[i][4]));
                    if (qr.getCustomData() == null) {
                        qr.setCustomData(expConsumeList[i][3] + "");
                    }
                    if (Integer.parseInt(qr.getCustomData()) < 0) {
                        qr.setCustomData(expConsumeList[i][3] + "");
                    }
                    int showExp = 0;
                    if (item.getItemId() == expConsumeList[i][0]) {
                        showExp = (Integer.parseInt(qr.getCustomData()) - expConsumeList[i][3]);
                        mplew.writeInt(expConsumeList[i][0]);
                        mplew.writeInt(expConsumeList[i][1]);
                        mplew.writeInt(expConsumeList[i][2]);
                        mplew.writeInt(showExp);
                    }
                }
            }
        }

        /*
        mplew.writeInt(0); // itemsize
        mplew.writeInt(2230004); // itemid
        mplew.writeInt(12); // minLevel
        mplew.writeInt(18); // maxLevel
        mplew.writeInt(-10675); // - exp
         */
        mplew.write(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());

        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122700));
        if ((stat != null) && (stat.getCustomData() != null) && (Long.parseLong(stat.getCustomData()) > System.currentTimeMillis())) {
            mplew.writeLong(getTime(Long.parseLong(stat.getCustomData())));
        } else {
            mplew.writeLong(getTime(-2L));
        }
        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        List<Item> equipped = iv.newList();
        Collections.sort(equipped);

        //장착중인아이템
        for (Item item : equipped) {
            if ((item.getPosition() < 0) && (item.getPosition() > -100)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0);
        //장착중인캐시아이템
        for (Item item : equipped) {
            if ((item.getPosition() <= -100) && (item.getPosition() > -1000)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }

        }
        mplew.writeShort(0);
        //장비인벤토리
        iv = chr.getInventory(MapleInventoryType.EQUIP);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.writeShort(0);
        //에반
        for (Item item : equipped) {
            if ((item.getPosition() <= -1000) && (item.getPosition() > -1100)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0);
        //메카닉
        for (Item item : equipped) {
            if ((item.getPosition() <= -1100) && (item.getPosition() > -1200)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0);
        //안드로이드
        for (Item item : equipped) {
            if (item.getPosition() <= -1200) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }

        /* mplew.writeShort(0);
        for (Item item : equipped) {
            if (item.getPosition() <= -1300) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }*/
        // mplew.writeShort(0);
        // mplew.writeShort(0);
        mplew.writeShort(0);
        //소비창
        iv = chr.getInventory(MapleInventoryType.USE);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
            if (GameConstants.isCore(item.getItemId())) {
                chr.corelist.add(new Pair<Integer, Integer>(item.getItemId(), (int) item.getQuantity()));
            }
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.ETC);
        for (Item item : iv.list()) {
            if (item.getPosition() < 100) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.CASH);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.write(0);
        for (int i = 0; i < chr.getExtendedSlots().size(); i++) {
            mplew.writeInt(i);
            mplew.writeInt(chr.getExtendedSlot(i));
            for (Item item : chr.getInventory(MapleInventoryType.ETC).list()) {
                if (item.getPosition() > (i * 100 + 100) && item.getPosition() < (i * 100 + 200)) {
                    addItemPosition(mplew, item, false, true);
                    addItemInfo(mplew, item, chr);
                }
            }
            mplew.writeInt(-1);
        }
        mplew.writeInt(-1);

        // v284 & 0x1000000
        int v156 = 0;
        mplew.writeInt(0);
        for (int i = 0; i < v156; i++) {
            mplew.writeInt(0);
            mplew.writeLong(0);
        }

        // v284 & 0x40000000
        int v159 = 0;
        mplew.writeInt(v159);
        for (int i = 0; i < v159; i++) {
            mplew.writeLong(0);
            mplew.writeLong(0);
        }

        // itemPot
        int v165 = 0;
        mplew.write(v165);
        for (byte i = 0; i < v165; i++) {
            if (v165 < 0) {
                mplew.write(0);
            }
        }
    }

    public static final void addCharStats(OutPacket mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getId());
        mplew.writeAsciiString(chr.getName(), 13);

        mplew.write(chr.getGender());
        mplew.write(chr.getSkin());
        mplew.writeInt(chr.getFace());
        mplew.writeInt(chr.getHair());

        mplew.EncodeByte(chr.getLevel()); // 레벨 제한
        mplew.writeShort(chr.getJob());

        chr.getStat().connectData(mplew);

        mplew.writeShort(chr.getRemainingAp());
        int a1 = chr.getJob(); //RemaingSP 테트라기준 코딩예정
        if (a1 / 1000 == 3
                || a1 / 100 == 22 || a1 == 2001
                || a1 / 100 == 23 || a1 == 2002
                || a1 / 100 == 24 || a1 == 2003
                || a1 / 100 == 51 || a1 / 1000 == 5
                || a1 / 100 == 27 || a1 == 2004
                || a1 / 100 == 61 || a1 == 6000
                || a1 / 100 == 62 || a1 == 6001
                || a1 / 100 == 65 || a1 == 6002
                || a1 / 100 == 42 || a1 == 4000
                || a1 / 100 == 52 || a1 == 5001) {
            int size = chr.getRemainingSpSize();
            mplew.write(size);
            for (int i = 0; i < chr.getRemainingSps().length; i++) {
                if (chr.getRemainingSp(i) > 0) {
                    mplew.write(i + 1);
                    mplew.write(chr.getRemainingSp(i)); //여기ㅇㅇ
                }
            }
        } else {
            mplew.writeShort((short) chr.getRemainingSp()); // 이게 확실한데.
        }
        mplew.writeInt(chr.getExp());
        mplew.writeInt(chr.getFame());
        mplew.writeInt(chr.getMapId());
        mplew.write(chr.getInitialSpawnpoint());
        mplew.writeShort(chr.getSubcategory());
        if (a1 / 100 == 31 || a1 == 3001 || a1 / 100 == 36 || a1 == 3002) {
            mplew.writeInt(chr.getDemonMarking());
        }
        mplew.write(chr.getFatigue());
        mplew.writeInt(GameConstants.getCurrentDate());
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            mplew.writeInt(chr.getTrait(t).getTotalExp());
        }
        mplew.writeShort(chr.getTodayCharisma());
        mplew.writeShort(chr.getTodayInsight());
        mplew.writeShort(chr.getTodayWillpower());
        mplew.writeShort(chr.getTodayCraft());
        mplew.writeShort(chr.getTodaySense());
        mplew.writeShort(chr.getTodayCharm());
        mplew.write(0);
        mplew.writeLong(0);
        mplew.writeInt(chr.getStat().getStarForce()); // 스타 포스
        mplew.write(chr.getStat().pvpRank);

        int arcaneForce = 0;
        if (chr.getQuestStatus(1466) == 2) {
            arcaneForce += 10;
        }
        mplew.writeInt(arcaneForce); // 아케인 포스

        mplew.write(5);
        mplew.write(0);
        mplew.writeInt(0);
        addPartTimeJob(mplew, MapleCharacter.getPartTime(chr.getId()));
        /*
        public static void addPartTimeJob(OutPacket mplew, PartTimeJob parttime) {
        mplew.write(parttime.getJob());
        mplew.writeReversedLong(parttime.getTime());
        mplew.writeInt(parttime.getReward());
        mplew.write(parttime.getReward() > 0);
    }
         */
        MapleCharacterCards.connectData(mplew);
        /*
        for (int a=0; a<9; a++)
        {
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(0);
            System.out.println(a);
        }
         */
        //  FileTime.dwHighDateTime = CInPacket::Decodeint(a2);
        //  FileTime.dwLowDateTime = CInPacket::Decodeint(a2);
        mplew.writeReversedLong(getTime(System.currentTimeMillis())); //dunno      
    }

    public static final void addCharLook(OutPacket mplew, MapleCharacter chr, boolean mega) {
        addCharLook(mplew, chr, mega, false);
    }

    public static final void addCharLook(OutPacket mplew, MapleCharacter chr, boolean mega, boolean isTransForm) {
        //if (!isTransForm) {
        mplew.write(chr.getGender());
        mplew.write(chr.getSkin());
        mplew.writeInt(chr.getFace());
        mplew.writeInt(chr.getJob());
        mplew.write(mega ? 0 : 1);
        mplew.writeInt(chr.getHair());
        //}
        final Map<Short, Integer> myEquip = new LinkedHashMap<>();
        final Map<Short, Integer> maskedEquip = new LinkedHashMap<>();
        final Map<Short, Integer> equip = chr.getEquips();
        if (isTransForm) {
            equip.put(Short.valueOf((short) -1305), 1051291);
        } else {
            //
        }

        for (final Entry<Short, Integer> item : equip.entrySet()) {
            if (item.getKey() < -127) {
                continue;
            }
            short pos = (short) (item.getKey() * -1);
            boolean angelicBuster = (pos >= 1300 && pos <= 1305);
            if ((pos < 100) && (myEquip.get(Short.valueOf(pos)) == null) && (!angelicBuster)) {
                if (isTransForm) {
                    if (GameConstants.isHat(item.getValue()) || GameConstants.isForeHead(item.getValue()) || GameConstants.isShoe(item.getValue()) || GameConstants.isGlove(item.getValue())) {
                        maskedEquip.put(Short.valueOf(pos), item.getValue());
                    } else {
                        myEquip.put(Short.valueOf(pos), item.getValue());
                    }
                } else {
                    myEquip.put(Short.valueOf(pos), item.getValue());
                }
            } else if ((pos > 100) && (pos != 111) && (!angelicBuster)) {
                pos = (byte) (pos - 100);
                if (myEquip.get(Short.valueOf(pos)) != null) {
                    maskedEquip.put(Short.valueOf(pos), myEquip.get(Short.valueOf(pos)));
                }
                myEquip.put(Short.valueOf(pos), item.getValue());
            } else if (myEquip.get(Short.valueOf(pos)) != null && (!angelicBuster)) {
                maskedEquip.put(Short.valueOf(pos), item.getValue());
            }
        }
        for (final Entry<Short, Integer> item : equip.entrySet()) { //엔젤릭버스터 체크하장
            if (!isTransForm) {
                continue;
            }
            short pos = (short) (item.getKey() * -1);
            boolean angelicBuster = (pos >= 1300 && pos <= 1305);
            if (pos > 100 && pos != 111 && !angelicBuster) {
                pos -= 100;
            }
            if (myEquip.get(Short.valueOf(pos)) != null && !angelicBuster) {
                if (myEquip.get(Short.valueOf(pos)) != null && MapleItemInformationProvider.getInstance().isCash(myEquip.get(Short.valueOf(pos)))) {
                    myEquip.remove(Short.valueOf(pos));
                }
            }
        }
        for (final Entry<Short, Integer> item : equip.entrySet()) { //엔젤릭버스터 체크하장
            if (!isTransForm) {
                continue;
            }
            short pos = (short) (item.getKey() * -1);
            boolean angelicBuster = (pos >= 1300 && pos <= 1305);
            if (angelicBuster) {
                switch (pos) {
                    case 1300:
                        pos = 1;
                        break;
                    case 1301:
                        pos = 9;
                        break;
                    case 1302:
                        pos = 2;
                        break;
                    case 1304:
                        pos = 8;
                        break;
                    case 1305:
                        pos = 5;
                        break;
                }
                myEquip.put(Short.valueOf(pos), item.getValue());
            }
        }

        for (Map.Entry entry : myEquip.entrySet()) {
            short v1 = ((Short) entry.getKey());
            mplew.write(v1);
            MapleCharacter v2 = MapleCharacter.loadCharFromDB(chr.getId(), chr.getClient(), true);
            Equip v3 = (Equip) v2.getInventory(MapleInventoryType.EQUIPPED).getItem((short) (v1 * -1));
            if (v3 != null && v3.getFusionAnvil() > 0) {
                int v4 = (v3.getItemId() / 10000);
                int v5 = (v4 * 10000);
                int v6 = (v5 + v3.getFusionAnvil());
                mplew.writeInt(v6);
            } else {
                mplew.writeInt((Integer) entry.getValue());
            }
        }
        mplew.write(0xFF);

        for (Map.Entry entry : maskedEquip.entrySet()) {
            mplew.write(((Short) entry.getKey()));
            mplew.writeInt(((Integer) entry.getValue()));
        }
        mplew.write(0xFF);

        Integer cWeapon = equip.get(Short.valueOf((byte) -111));
        Integer weapon = equip.get(Short.valueOf((byte) -11));
        Integer sWeapon = equip.get(Short.valueOf((byte) -10));

        mplew.writeInt(cWeapon != null ? cWeapon : 0);
        mplew.writeInt(weapon != null ? weapon : 0);
        mplew.writeInt(sWeapon != null ? sWeapon : 0);
        mplew.write(0); // GameConstants.isMercedes(chr.getJob()) ? 1 : 
        //펫부분
        //  for (int i = 0; i < 3; i++) {
        //       mplew.writeInt(0);
        //   }
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        if (GameConstants.isDemon(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob())) {
            mplew.writeInt(chr.getDemonMarking());
        }
    }

    public static void addPartTimeJob(OutPacket mplew, PartTimeJob parttime) {
        mplew.write(parttime.getJob());
        mplew.writeReversedLong(parttime.getTime());
        mplew.writeInt(parttime.getReward());
        mplew.write(parttime.getReward() > 0);
    }

    public static final void addExpirationTime(OutPacket mplew, long time) {
        mplew.writeLong(getTime(time));
    }

    public static void addItemPosition(OutPacket mplew, Item item, boolean trade, boolean bagSlot) {
        if (item == null) {
            mplew.write(0);
            return;
        }
        short pos = item.getPosition();
        if (pos <= -1) {
            pos = (short) (pos * -1);
            if ((pos > 100) && (pos < 1000)) {
                pos = (short) (pos - 100);
            }
        }
        if (bagSlot) {
            mplew.writeInt(pos % 100 - 1);
        } else if ((!trade) && (item.getType() == 1)) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
    }

    public static final void addItemInfo(OutPacket mplew, Item item) {
        addItemInfo(mplew, item, null);
    }

    public static final void addItemInfo(OutPacket mplew, Item item, MapleCharacter chr) {
        mplew.write(item.getPet() != null ? 3 : item.getType());
        mplew.writeInt(item.getItemId());
        boolean hasUniqueId = (item.getUniqueId() > -1) && (!GameConstants.isMarriageRing(item.getItemId())) && (item.getItemId() / 10000 != 166);
        mplew.write(hasUniqueId ? 1 : 0); // isCash
        if (hasUniqueId) {
            mplew.writeLong(item.getUniqueId());
        }
        if (item.getPet() != null) {
            addPetItemInfo(mplew, item, item.getPet(), true);
        } else {
            addExpirationTime(mplew, item.getExpiration());
            mplew.writeInt(chr == null ? -1 : chr.getExtendedSlots().indexOf(Integer.valueOf(item.getItemId())));
            if (item.getType() == 1) {
                final Equip equip = (Equip) item;
                mplew.write(equip.getUpgradeSlots());
                mplew.write(equip.getLevel());
                mplew.writeShort(equip.getStr());
                mplew.writeShort(equip.getDex());
                mplew.writeShort(equip.getInt());
                mplew.writeShort(equip.getLuk());
                mplew.writeShort(equip.getHp());
                mplew.writeShort(equip.getMp());
                mplew.writeShort(equip.getWatk());
                mplew.writeShort(equip.getMatk());
                mplew.writeShort(equip.getWdef());
                mplew.writeShort(equip.getMdef());
                mplew.writeShort(equip.getAcc());
                mplew.writeShort(equip.getAvoid());
                mplew.writeShort(equip.getHands());
                mplew.writeShort(equip.getSpeed());
                mplew.writeShort(equip.getJump());
                mplew.writeMapleAsciiString(equip.getOwner());
                mplew.writeShort(equip.getFlag());
                mplew.write(equip.getIncSkill() > 0 ? 1 : 0);
                mplew.write(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // ?
                if (GameConstants.isWeapon(equip.getItemId())) {
                    mplew.writeInt(equip.getExpPercentage() * 100000);
                } else {
                    if (equip.getItemId() == 1112908) {
                        mplew.writeInt(equip.getEquipExp() / 100);
                    } else {
                        mplew.writeInt(equip.getExpPercentage() * 1000);
                    }
                }
                mplew.writeInt(equip.getDurability());
                mplew.writeInt(equip.getViciousHammer());
                mplew.writeShort(equip.getPVPDamage());

                mplew.write(equip.getStateByPotential(equip.getPotential()));

                mplew.write(equip.getEnhance());

                mplew.writeShort(equip.getPotentialByLine(0));
                mplew.writeShort(equip.getPotentialByLine(1));
                mplew.writeShort(equip.getPotentialByLine(2));

                mplew.writeShort(0); // ?

                mplew.writeShort(equip.getFusionAnvil()); // 신비의 모루

                if (!hasUniqueId) {
                    mplew.writeLong(equip.getInventoryId() < 1 ? -1 : equip.getInventoryId());
                }
                mplew.writeLong(getTime(-2));
                mplew.writeInt(-1); //?
            } else {
                mplew.writeShort(item.getQuantity());
                mplew.writeMapleAsciiString(item.getOwner());
                boolean aTrade = ((int) (item.getItemId() / 10000) == 506);
                mplew.writeShort(aTrade ? (byte) 2 : item.getFlag());
                if ((GameConstants.isThrowingStar(item.getItemId())) || (GameConstants.isBullet(item.getItemId())) || (item.getItemId() / 10000 == 287)) {
                    mplew.writeLong(item.getInventoryId() <= 0L ? -1L : item.getInventoryId());
                }
            }
        }
    }

    public static void addEquipStats(OutPacket mplew, Equip equip) {
        int head = 0;
        if (equip.getStats().size() > 0) {
            for (EquipStat stat : equip.getStats()) {
                head |= stat.getValue();
            }
        }
        mplew.writeInt(head);
        if (head != 0) {
            if (equip.getStats().contains(EquipStat.SLOTS)) {
                mplew.write(equip.getUpgradeSlots());
            }
            if (equip.getStats().contains(EquipStat.LEVEL)) {
                mplew.write(equip.getLevel());
            }
            if (equip.getStats().contains(EquipStat.STR)) {
                mplew.writeShort(equip.getStr());
            }
            if (equip.getStats().contains(EquipStat.DEX)) {
                mplew.writeShort(equip.getDex());
            }
            if (equip.getStats().contains(EquipStat.INT)) {
                mplew.writeShort(equip.getInt());
            }
            if (equip.getStats().contains(EquipStat.LUK)) {
                mplew.writeShort(equip.getLuk());
            }
            if (equip.getStats().contains(EquipStat.MHP)) {
                mplew.writeShort(equip.getHp());
            }
            if (equip.getStats().contains(EquipStat.MMP)) {
                mplew.writeShort(equip.getMp());
            }
            if (equip.getStats().contains(EquipStat.WATK)) {
                mplew.writeShort(equip.getWatk());
            }
            if (equip.getStats().contains(EquipStat.MATK)) {
                mplew.writeShort(equip.getMatk());
            }
            if (equip.getStats().contains(EquipStat.WDEF)) {
                mplew.writeShort(equip.getWdef());
            }
            if (equip.getStats().contains(EquipStat.MDEF)) {
                mplew.writeShort(equip.getMdef());
            }
            if (equip.getStats().contains(EquipStat.ACC)) {
                mplew.writeShort(equip.getAcc());
            }
            if (equip.getStats().contains(EquipStat.AVOID)) {
                mplew.writeShort(equip.getAvoid());
            }
            if (equip.getStats().contains(EquipStat.HANDS)) {
                mplew.writeShort(equip.getHands());
            }
            if (equip.getStats().contains(EquipStat.SPEED)) {
                mplew.writeShort(equip.getSpeed());
            }
            if (equip.getStats().contains(EquipStat.JUMP)) {
                mplew.writeShort(equip.getJump());
            }
            if (equip.getStats().contains(EquipStat.FLAG)) {
                mplew.writeShort(equip.getFlag());
            }
            if (equip.getStats().contains(EquipStat.INC_SKILL)) {
                mplew.write(equip.getIncSkill() > 0 ? 1 : 0);
            }
            if (equip.getStats().contains(EquipStat.ITEM_LEVEL)) {
                mplew.write(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // Item level
            }
            if (equip.getStats().contains(EquipStat.ITEM_EXP)) {
                mplew.writeInt(equip.getExpPercentage() * 100000); // Item Exp... 10000000 = 100%
            }
            if (equip.getStats().contains(EquipStat.DURABILITY)) {
                mplew.writeInt(equip.getDurability());
            }
            if (equip.getStats().contains(EquipStat.VICIOUS_HAMMER)) {
                mplew.writeInt(equip.getViciousHammer());
            }
            if (equip.getStats().contains(EquipStat.PVP_DAMAGE)) {
                mplew.writeShort(equip.getPVPDamage());
            }
            if (equip.getStats().contains(EquipStat.ENHANCT_BUFF)) {
                mplew.writeShort(equip.getEnhanctBuff());
            }
            if (equip.getStats().contains(EquipStat.DURABILITY_SPECIAL)) {
                mplew.writeInt(equip.getDurability());
            }
            if (equip.getStats().contains(EquipStat.REQUIRED_LEVEL)) {
                mplew.write(equip.getReqLevel());
            }
            if (equip.getStats().contains(EquipStat.YGGDRASIL_WISDOM)) {
                mplew.write(equip.getYggdrasilWisdom());
            }
            if (equip.getStats().contains(EquipStat.FINAL_STRIKE)) {
                mplew.write(equip.getFinalStrike() ? 1 : 0);
            }
            if (equip.getStats().contains(EquipStat.BOSS_DAMAGE)) {
                mplew.write(equip.getBossDamage());
            }
            if (equip.getStats().contains(EquipStat.IGNORE_PDR)) {
                mplew.write(equip.getIgnorePDR());
            }
        }
        addEquipSpecialStats(mplew, equip);
    }

    public static void addEquipSpecialStats(OutPacket mplew, Equip equip) {
        int head = 0;
        if (equip.getSpecialStats().size() > 0) {
            for (EquipSpecialStat stat : equip.getSpecialStats()) {
                head |= stat.getValue();
            }
        }
        mplew.writeInt(head);
        if (head != 0) {
            if (equip.getSpecialStats().contains(EquipSpecialStat.TOTAL_DAMAGE)) {
                mplew.write(equip.getTotalDamage());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.ALL_STAT)) {
                mplew.write(equip.getAllStat());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.KARMA_COUNT)) {
                mplew.write(equip.getKarmaCount());
            }
        }
    }

    public static final void serializeMovementList(OutPacket lew, List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }

    public static final void addAnnounceBox(OutPacket mplew, MapleCharacter chr) {
        if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr) && chr.getPlayerShop().getShopType() != 1 && chr.getPlayerShop().isAvailable()) {
            addInteraction(mplew, chr.getPlayerShop());
        } else {
            mplew.write(0);
        }
    }

    public static final void addInteraction(final OutPacket mplew, IMaplePlayerShop shop) {
        mplew.write(shop.getGameType());
        mplew.writeInt(((AbstractPlayerStore) shop).getObjectId());
        mplew.writeMapleAsciiString(shop.getDescription());
        if (shop.getShopType() != 1) {
            mplew.write(shop.getPassword().length() > 0 ? 1 : 0); //password = false
        }
        if (shop.getItemId() == 4080100) {
            mplew.write(((MapleMiniGame) shop).getPieceType());
            mplew.write(shop.getSize()); //current size
            mplew.write(2);
        } else if (shop.getItemId() >= 4080000 && shop.getItemId() < 4080100) {
            mplew.write(((MapleMiniGame) shop).getPieceType());
            mplew.write(shop.getSize()); //current size
            mplew.write(2);
        } else {
            mplew.write(shop.getItemId() % 10);
            mplew.write(shop.getSize()); //current size
            mplew.write(shop.getMaxSize()); //full slots... 4 = 4-1=3 = has slots, 1-1=0 = no slots
        }
        if (shop.getShopType() != 1) {
            mplew.write(shop.isOpen() ? 0 : 1);
        }
    }

    public static final void addCharacterInfo(OutPacket mplew, MapleCharacter chr) {
        mplew.writeLong(-1);
        mplew.writeZeroBytes(7);
        // v285 & 1
        addCharStats(mplew, chr);
        mplew.write(chr.getBuddylist().getCapacity());
        if (chr.getBlessOfFairyOrigin() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
        } else {
            mplew.write(0);
        }
        if (chr.getBlessOfEmpressOrigin() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
        } else {
            mplew.write(0);
        }
        MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
            mplew.write(1);
            mplew.writeMapleAsciiString(ultExplorer.getCustomData());
        } else {
            mplew.write(0);
        }
        addInventoryInfo(mplew, chr);
        addSkillInfo(mplew, chr);       // v283 & 0x100
        addCoolDownInfo(mplew, chr);    // v283 & 0x8000
        addQuestInfo(mplew, chr);       // v283 & 0x200
        addQuestClearInfo(mplew, chr);  // v283 & 0x4000
        addMinigameInfo(mplew, chr);    // v283 & 0x400
        addRingInfo(mplew, chr);        // v283 & 0x800
        addRocksInfo(mplew, chr);       // v283 & 0x1000
        chr.QuestInfoPacket(mplew);     // v283 & 0x40000
        if (chr.getJob() / 100 == 33) {
            addJaguarInfo(mplew, chr);  // v283 & 0x200000
        }
        addUNK(mplew);                  // v283 & 0x400000
        addUNK2(mplew);                 // v283 & 0x4000000
        addStealSkills(mplew, chr);     // v283 & 0x20000000
        addChosenSkills(mplew, chr);    // v283 & 0x10000000
        addInnerStats(mplew, chr);      // v283 < 0
        addHonourInfo(mplew, chr);      // v284 & 1
    }

    public static final int getSkillBook(final int i) {
        switch (i) {
            case 1:
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
        }
        return 0;
    }

    public static final void addUNK(final OutPacket mplew) {
        int v253 = 0;
        mplew.writeShort(v253);
        for (int i = v253; i > 0; --i) {
            mplew.writeShort(0);
            mplew.writeLong(0);
        }
    }

    public static final void addUNK2(final OutPacket mplew) {
        int v264 = 0;
        mplew.writeShort(v264);
        if (v264 > 0) {
            /*      while ( 2 )
      {
        v300 = (unsigned __int16)CInPacket::Decode2(a2);
        v266 = CInPacket::Decode4(a2);
        v267 = v266;
        if ( v300 && v266 )
        {
          v309 = 0;
          sub_55CD00(&v308);
          LOBYTE(v324) = 26;
          if ( v309 )
          {
            *(_DWORD *)v309 = v267;
            v306 = (char *)v267;
            sub_5565E0(v254 + 1811, (int)&v306, (int)&v308);
          }
          for ( ; v300 > 0; --v300 )
          {
            v314 = 0;
            sub_5594B0(&v313);
            v268 = v314;
            LOBYTE(v324) = 27;
            if ( v314 )
            {
              *(_DWORD *)v314 = CInPacket::Decode4(a2);
              *(_WORD *)(v268 + 4) = CInPacket::Decode2(a2);
              *(_DWORD *)(v268 + 6) = CInPacket::Decode4(a2);
              *(_DWORD *)(v268 + 10) = (unsigned __int16)CInPacket::Decode2(a2);
              if ( !*(_DWORD *)(v268 + 6) || !*(_DWORD *)v268 )
              {
                LOBYTE(v324) = 26;
LABEL_571:
                v269 = v268 - 16;
                if ( !InterlockedDecrement((volatile LONG *)(v269 + 4)) )
                {
                  InterlockedIncrement((volatile LONG *)(v269 + 4));
                  if ( v269 )
                    (**(void (__thiscall ***)(_DWORD, _DWORD))v269)(v269, 1);
                }
                v314 = 0;
                continue;
              }
              if ( v309 )
              {
                v306 = (char *)*(_WORD *)(v268 + 4);
                sub_556440(v309 + 4, (int)&v306, (int)&v313);
              }
            }
            else
            {
              CInPacket::Decode4(a2);
              CInPacket::Decode2(a2);
              CInPacket::Decode4(a2);
              CInPacket::Decode2(a2);
            }
            LOBYTE(v324) = 26;
            if ( v268 )
              goto LABEL_571;
          }
          LOBYTE(v324) = 5;
          if ( v309 )
          {
            v270 = v309 - 16;
            v271 = (volatile LONG *)(v309 - 16 + 4);
            v299 = v309 - 16 + 4;
            if ( !InterlockedDecrement((volatile LONG *)v299) )
            {
              InterlockedIncrement(v271);
              if ( v270 )
                (**(void (__thiscall ***)(_DWORD, _DWORD))v270)(v270, 1);
            }
            v309 = 0;
          }
        }
        v254 = (int)v304;
        --mm;
        if ( mm <= 0 )
          break;
        continue;
      }
    }*/
        }
    }

    public static final void addInnerStats(final OutPacket mplew, MapleCharacter chr) {
        final List<InnerSkillValueHolder> skills = chr.getInnerSkills();
        if (skills != null) {
            mplew.writeShort(skills.size());
            for (int i = 0; i < skills.size(); ++i) {
                mplew.write(i + 1); // key
                mplew.writeInt(skills.get(i).getSkillId()); //d 7000000 id ++, 71 = char cards
                mplew.write(skills.get(i).getSkillLevel()); // level
                mplew.write(skills.get(i).getRank()); //rank, C, B, A, and S
            }
        }
    }

    public static final void addHonourInfo(final OutPacket mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getHonourLevel()); //honor lvl
        mplew.writeInt(chr.getHonourExp()); //honor exp     
    }

    public static final void addCoreAura(OutPacket mplew, MapleCharacter chr) {
        mplew.writeInt(0);
        mplew.writeLong(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeLong(getTime(System.currentTimeMillis() + 86400000));
        mplew.writeInt(0);
        mplew.write(0);
    }

    public static void addStolenSkills(OutPacket mplew, MapleCharacter chr, int jobNum, boolean writeJob) {
        if (writeJob) {
            mplew.writeInt(jobNum);
        }
        int count = 0;
        if (chr.getStolenSkills() != null) {
            for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                if (GameConstants.getJobNumber(sk.left / 10000) == jobNum) {
                    mplew.writeInt(sk.left);
                    count++;
                    if (count >= GameConstants.getNumSteal(jobNum)) {
                        break;
                    }
                }
            }
        }
        while (count < GameConstants.getNumSteal(jobNum)) { //for now?
            mplew.writeInt(0);
            count++;
        }
    }

    public static void addChosenSkills(OutPacket mplew, MapleCharacter chr) {
        for (int i = 1; i <= 4; i++) {
            boolean found = false;
            if (chr.getStolenSkills() != null) {
                for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                    if (GameConstants.getJobNumber(sk.left / 10000) == i && sk.right) {
                        mplew.writeInt(sk.left);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                mplew.writeInt(0);
            }
        }
    }

    public static final void addStealSkills(final OutPacket mplew, final MapleCharacter chr) {
        for (int i = 1; i <= 4; i++) {
            addStolenSkills(mplew, chr, i, false);
        }
    }

    public static final void addMonsterBookInfo(OutPacket mplew, MapleCharacter chr) {
        mplew.writeInt(0);

        if (chr.getMonsterBook().getSetScore() > 0) {
            chr.getMonsterBook().writeFinished(mplew);
        } else {
            chr.getMonsterBook().writeUnfinished(mplew);
        }

        mplew.writeInt(chr.getMonsterBook().getSet());
    }

    public static final void addPetItemInfo(OutPacket mplew, Item item, MaplePet pet, boolean active) {
        if (item == null) {
            mplew.writeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            PacketHelper.addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1 : item.getExpiration());
        }
        mplew.writeInt(-1);
        mplew.writeAsciiString(pet.getName(), 13);
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        if (item == null) {
            mplew.writeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        mplew.writeShort(0);
        mplew.writeShort(pet.getFlags());
        mplew.writeInt((pet.getPetItemId() == 5000054) && (pet.getSecondsLeft() > 0) ? pet.getSecondsLeft() : 0);
        mplew.writeShort(0);
        mplew.write(active ? (pet.getSummoned() ? pet.getSummonedValue() : 0) : 0);
        mplew.writeInt(active ? pet.getBuffSkill() : 0);//펫버프
        //mplew.writeInt(-1);//펫색 write(unequip ? 0 : player.getPetIndex(pet)+1); 160에선 사용안함
        //mplew.writeShort(100);
    }

    public static final void addShopInfo(OutPacket mplew, MapleShop shop, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        mplew.writeShort(shop.getItems().size() + c.getPlayer().getRebuy().size());
        for (MapleShopItem item : shop.getItems()) {
            addShopItemInfo(mplew, item, shop, ii, null);
        }
    }

    public static final void addAdminShopInfo(OutPacket mplew, MapleShop shop, MapleClient c) {
        mplew.writeShort(shop.getItems().size());
        for (MapleShopItem item : shop.getItems()) {
            //  addShopItemInfo(mplew, item, shop, ii, null);
            mplew.writeInt(item.getRank());// nSN 임시로 랭크
            mplew.writeInt(item.getItemId());// nItemID 아이템코드
            mplew.writeInt(item.getPrice());// nPrice 가격 -는 매입
            mplew.write(item.getReqItem());// nSaleState, probably CommodityState 판매상황
            mplew.writeShort(item.getReqItemQ());// nMaxPerSlot 최대갯수
        }

        // for (Item i : c.getPlayer().getRebuy()) {
        //     addShopItemInfo(mplew, new MapleShopItem(i.getItemId(), (int) ii.getPrice(i.getItemId()), i.getQuantity()), shop, ii, i);
        // }
    }

    /*    private short buyable;
     private int itemId;
     private int price;
     private int reqItem;
     private int reqItemQ;
     private int category;
     private int minLevel;
     private int expiration;
     private byte rank;*/
    public static final void addShopItemInfo(OutPacket mplew, MapleShopItem item, MapleShop shop, MapleItemInformationProvider ii, Item i) {
        mplew.writeInt(item.getItemId());
        mplew.writeInt(item.getPrice() < 1 ? 0 : item.getPrice());
        mplew.writeInt(item.getReqItem());
        mplew.writeInt(item.getReqItemQ());
        int eDuration = 0;
        switch (item.getItemId()) {
            case 1112427:
            case 1112428:
            case 1112429: {
                eDuration = 4320;
                break;
            }
            
            case 2430117: // 이노시스 시즌 패스 티켓 (7일)
            
            case 1132112:
            case 1132113:
            case 1132114:
            case 1132115:
            case 1022135:
            case 1022136:

            case 1132013:
            case 1072619:
            case 1112682:

            case 5044002: {
                eDuration = 10080;
                break;
            }
        }
        mplew.writeInt(eDuration); //시간 정보인 레후~~ 1440 * 365
        mplew.writeInt(0); // 0 to show confirmation box, 1 to hide it.
        mplew.writeInt(0); //No visible effects besides if value is > 0 then it hides the buyable items.
        mplew.writeInt(item.getRank());

        byte ePotential = 0;
        if (item.getRank() == 13) {
            switch (shop.getId()) {
                case 504:
                case 506: {
                    ePotential = 1;
                    break;
                }
            }
        }

        mplew.write(0/*ePotential*/);

        mplew.writeInt(shop.getNpcId() == 1064010 || item.getItemId() == 2430695 ? 1 : 0);// 히든상점 1회 구매가능 2430695
        if ((!GameConstants.isThrowingStar(item.getItemId())) && (!GameConstants.isBullet(item.getItemId()))) {
            int stack = 1;
            if (item.getRank() == 14) {
                if (shop.getId() == 501) {
                    switch (item.getItemId()) {
                        case 2000004:
                        case 2000005:
                        case 2050004: {
                            stack = 100;
                            break;
                        }
                    }
                }
            }
            mplew.writeShort(stack);
            mplew.writeShort(999);
        } else {
            mplew.writeAsciiString("333333");
            mplew.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
            mplew.writeShort(ii.getSlotMax(item.getItemId()));
        }

        mplew.write(i == null ? 0 : 1);
        if (i != null) {
            addItemInfo(mplew, i);
        }
    }

    public static final void addJaguarInfo(OutPacket mplew, MapleCharacter chr) {
        mplew.write(chr.getIntNoRecord(111112));
        mplew.writeInt(chr.getIntNoRecord(111113)); //probably mobID of the 5 mobs that can be captured.
        mplew.writeInt(chr.getIntNoRecord(111114));
        mplew.writeInt(chr.getIntNoRecord(111115));
        mplew.writeInt(chr.getIntNoRecord(111116));
        mplew.writeInt(chr.getIntNoRecord(111117));
    }

    public static <E extends Buffstat> void writeSingleMask(OutPacket mplew, E statup) {
        for (int i = GameConstants.MAX_BUFFSTAT; i >= 1; i--) {
            mplew.writeInt(i == statup.getPosition() ? statup.getValue() : 0);
        }
    }

    public static <E extends Buffstat> void writeMask(OutPacket mplew, Collection<E> statups) {
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        for (E statup : statups) {
            mask[statup.getPosition() - 1] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[i - 1]);
        }
    }

    public static <E extends Buffstat> void writeBuffMask(OutPacket mplew, Collection<Pair<E, Integer>> statups) {
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        // public static final int JAIL = 180000001, MAX_BUFFSTAT = 10;
        for (Pair<E, Integer> statup : statups) {
            mask[statup.left.getPosition() - 1] |= statup.left.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[i - 1]);
            //System.out.println("mask[i - 1] :" + mask[i - 1]);
        }
    }

    public static <E extends Buffstat> void writeBuffMask(OutPacket mplew, Map<E, Integer> statups) {
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        for (E statup : statups.keySet()) {
            mask[statup.getPosition() - 1] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[i - 1]);
        }
    }
}
