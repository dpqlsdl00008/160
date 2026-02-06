package constants;

import client.MapleCharacter;
import client.MapleClient;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.status.MonsterTemporaryStat;
import handling.channel.ChannelServer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.StructItemOption;
import server.life.MapleMonster;
import server.maps.AramiaFireWorks;
import server.maps.MapleMapObjectType;
import tools.FileoutputUtil;
import tools.Pair;
import tools.packet.CField;

public class GameConstants {

    public static final List<MapleMapObjectType> rangedMapobjectTypes = Collections.unmodifiableList(Arrays.asList(
            MapleMapObjectType.ITEM,
            MapleMapObjectType.MONSTER,
            MapleMapObjectType.DOOR,
            MapleMapObjectType.REACTOR,
            MapleMapObjectType.SUMMON,
            MapleMapObjectType.NPC,
            MapleMapObjectType.MIST,
            MapleMapObjectType.EXTRACTOR,
            MapleMapObjectType.PLAYER));
    private static final int[] exp = {0, 15, 34, 57, 92, 135, 372, 560, 840, 1242,
        1540, 1910, 2368, 2936, 3641, 4515, 5599, 6943, 8609, 10675, 13237, 16414, 20353, 25238, 31295, 38806, 48119, 59668, 73988, 91745, 101286, 111820, 123449, 136288, 150462, 166110, 183385, 202457, 223513, 246758, 272421, 300753, 332031, 366562, 404684, 446771, 493235, 544531, 601162, 663683, 700385, 739116, 779989, 823122, 868641, 916677, 967369, 1020865, 1077319, 1136895, 1199765, 1266112, 1336128, 1410016, 1487990, 1570276, 1657112, 1748750, 1845456, 1947510, 2055207, 2168860, 2288798, 2415369, 2548939, 2689895, 2838646, 2995623, 3161281, 3336100, 3520586, 3715275, 3920730, 4137546, 4366353, 4607813, 4862625, 5131528, 5415302, 5714768, 6030795, 6364298, 6716244, 7087653, 7479600, 7893222, 8329717, 8790350, 9276456, 9789444, 10330800, 10902094, 11504980, 12141206, 12812616, 13521154, 14268874, 15057944, 15890648, 16769402, 17696750, 18675380, 19708128, 20797988, 21948116, 23161846, 24442696, 25794378, 27220808, 28726118, 30314672, 31991074, 33760180, 35627116, 37597296, 39676428, 41870536, 44185976, 46629460, 49208068, 51929276, 54800964, 57831456, 61029536, 64404468, 67966032, 71724552, 75690920, 79876624, 84293800, 88955248, 93874472, 99065728, 104544064, 110325352, 116426344, 122864720, 129659136, 136829280, 144395936, 152381024, 160807696, 169700368, 179084800, 188988192, 199439232, 210468224, 222107120, 234389648, 247351392, 261029920, 275464864, 290698080, 306773696, 323738272, 341640992, 360533728, 380471232, 401511296, 423714880, 447146304, 471873504, 497968096, 525505728, 554566208, 585233728, 617597120, 651750208, 687792000, 725826880, 765965120, 808323008, 853023296, 900195456, 949976256, 1002509952, 1057948736, 1116453248, 1178193152, 1243347200, 1312104320, 1384663680, 1461235584, 1542041856, 1627316736, 1717307392, 1812274432, 1912493184, 2018254080, 2129863552};
    //1.2.183 KMS Exp Table
    private static final int[] closeness = {
        0, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1224, 1642, 2161, 2793,
        3557, 4467, 5542, 6801, 8263, 9950, 11882, 14084, 16578, 19391, 22547, 26074, 30000};
    private static final int[] setScore = {0, 10, 100, 300, 600, 1000, 2000, 4000, 7000, 10000};
    private static final int[] cumulativeTraitExp = {
        0, // 1
        20, // 2
        46, // 3
        80, // 4
        124, // 5
        181, // 6
        255, // 7
        351, // 8
        476, // 9
        639, // 10
        851, // 11
        1084, // 12
        1340, // 13
        1622, // 14
        1932, // 15
        2273, // 16
        2648, // 17
        3061, // 18
        3515, // 19
        4014, // 20
        4563, 5128,
        5710, 6309, 6926, 7562, 8217, 8892, 9587, 10303, 11040, 11788,
        12547, 13307, 14089, 14883, 15689, 16507, 17337, 18179, 19034, 19902,
        20783, 21677, 22584, 23505, 24440, 25399, 26362, 27339, 28331, 29338,
        30360, 31397, 32450, 33519, 34604, 35705, 36823, 37958, 39110, 40279,
        41466, 32671, 43894, 45135, 46395, 47674, 48972, 50289, 51626, 52967,
        54312, 55661, 57014, 58371, 59732, 61097, 62466, 63839, 65216, 66597,
        67982, 69371, 70764, 72161, 73562, 74967, 76376, 77789, 79206, 80627,
        82052, 83481, 84914, 86351, 87792, 89237, 90686, 92139, 93596, 96000};
    private static final int[] pvpExp = {0, 3000, 6000, 12000, 24000, 48000, 960000, 192000, 384000, 768000};
    private static final int[] guildexp = {0, 20000, 160000, 540000, 1280000, 2500000, 4320000, 6860000, 10240000, 14580000};
    private static final int[] mountexp = {0, 6, 25, 50, 105, 134, 196, 254, 263, 315, 367, 430, 543, 587, 679, 725, 897, 1146, 1394, 1701, 2247,
        2543, 2898, 3156, 3313, 3584, 3923, 4150, 4305, 4550};
    public static final int JAIL = 180000001, MAX_BUFFSTAT = 8;
    public static final int[] rankC = {70000000, 70000001, 70000002, 70000003, 70000004, 70000005, 70000006, 70000007, 70000008, 70000009, 70000010, 70000011, 70000012, 70000013};
    public static final int[] rankB = {70000014, 70000015, 70000016, 70000017, 70000018, 70000021, 70000022, 70000023, 70000024, 70000025, 70000026};
    public static final int[] rankA = {70000027, 70000028, 70000029, 70000030, 70000031, 70000032, 70000033, 70000034, 70000035, 70000036, 70000039, 70000040, 70000041, 70000042};
    public static final int[] rankS = {70000043, 70000044, 70000045, 70000047, 70000048, 70000049, 70000050, 70000051, 70000052, 70000053, 70000054, 70000055, 70000056, 70000057, 70000058, 70000059, 70000060, 70000061, 70000062};
    public static final int[] circulators = {2700000, 2700100, 2700200, 2700300, 2700400, 2700500, 2700600, 2700700, 2700800, 2700900, 2701000};
    public static final String[] stats = {"tuc", "reqLevel", "reqJob", "reqSTR", "reqDEX", "reqINT", "reqLUK", "reqPOP", "cash", "cursed", "success", "setItemID", "equipTradeBlock", "durability", "randOption", "randStat", "masterLevel", "reqSkillLevel", "elemDefault", "incRMAS", "incRMAF", "incRMAI", "incRMAL", "canLevel", "skill", "charmEXP", "pickupAll", "pickupItem", "sweepForDrop", "longRange", "consumeHP", "consumeMP", "noRevive", "autoBuff", "giantPet", "multiPet", "expireOnLogout"};
    public static final int[] hyperTele = {310000000, 220000000, 100000000, 250000000, 240000000, 104000000, 103000000, 102000000, 101000000, 120000000, 260000000, 200000000, 230000000};
    public static boolean showPacket = false;
    private static final int[] azwanRecipes = {2510483, 2510484, 2510485, 2510486, 2510487, 2510488, 2510489, 2510490, 2510491, 2510492, 2510493, 2510494, 2510495, 2510496, 2510497, 2510498, 2510499, 2510500, 2510501, 2510502, 2510503, 2510504, 2510505, 2510506, 2510507, 2510508, 2510509, 2510510, 2510511, 2510512, 2510513, 2510514, 2510515, 2510516, 2510517, 2510518, 2510519, 2510520, 2510521, 2510522, 2510523, 2510524, 2510525, 2510526, 2510527, 2511153, 2511154, 2511155};
    private static final int[] azwanScrolls = {2046060, 2046061, 2046062, 2046063, 2046064, 2046065, 2046066, 2046067, 2046068, 2046069, 2046141, 2046142, 2046143, 2046144, 2046145, 2046519, 2046520, 2046521, 2046522, 2046523, 2046524, 2046525, 2046526, 2046527, 2046528, 2046529, 2046530, 2046701, 2046702, 2046703, 2046704, 2046705, 2046706, 2046707, 2046708, 2046709, 2046710, 2046711, 2046712};
    private static final Pair[] useItems = {new Pair<>(2002010, 500), new Pair<>(2002006, 600), new Pair<>(2002007, 600), new Pair<>(2002008, 600), new Pair<>(2002009, 600), new Pair<>(2022003, 770), new Pair<>(2022000, 1155), new Pair<>(2001001, 2300), new Pair<>(2001002, 4000), new Pair<>(2020012, 4680), new Pair<>(2020013, 5824), new Pair<>(2020014, 8100), new Pair<>(2020015, 10200), new Pair<>(2000007, 5), new Pair<>(2000000, 5), new Pair<>(2000008, 48), new Pair<>(2000001, 48), new Pair<>(2000009, 96), new Pair<>(2000002, 96), new Pair<>(2000010, 20), new Pair<>(2000003, 20), new Pair<>(2000011, 186), new Pair<>(2000006, 186), new Pair<>(2050000, 200), new Pair<>(2050001, 200), new Pair<>(2050002, 300), new Pair<>(2050003, 500)};

    public static boolean isWarpGate(int npcId) {
        switch (npcId) {
            case 2300005:
            case 3000012:
                return true;
        }
        return false;
    }

    public static boolean isNoEnableSkill(int skillid) {
        switch (skillid) {
            case 3121004: // 폭풍의 시
            case 3100001: // 파이널 어택
            case 5221004: // 래피드 파이어
            case 13111002: // 폭풍의 시
            case 31111005: // 데모닉 브레스
            case 33121009: // 와일드 발칸
            case 33100009: // 파이널 어택
                return true;
        }
        return false;
    }

    public enum Cubes {
        MIRACLE(5062000),
        PREMIUM(5062001),
        SUPER(5062002),
        REVOLUTIONARY(5062003),
        ENLIGHTENING(5062005),
        PLATINUM(5062006),
        RED(5062009),
        BLACK(5062010),
        VIOLET(5062024),
        MEMORY(5062090),
        BONUS(5062500);
        private int itemId = -1;

        Cubes(int itemId) {
            this.itemId = itemId;
        }

        public int getItemId() {
            return itemId;
        }

    }

    public static boolean isRackGate(int npcId) {
        switch (npcId) {
            case 9000018:
                //case 3000012:
                return true;
        }
        return false;
    }

    public static List<Integer> getPercentHP() {
        List<Integer> ret = new ArrayList<>();
        ret.add(1220036);
        ret.add(3220036);
        ret.add(35120036);
        ret.add(21120036);
        ret.add(23120036);
        ret.add(65120036);
        ret.add(31220036);
        ret.add(2320036);
        ret.add(1120036);
        ret.add(33120036);
        ret.add(4220036);
        ret.add(32120036);
        ret.add(61120036);
        ret.add(5220036);
        ret.add(5320036);
        ret.add(36120036);
        ret.add(5120036);
        ret.add(4340036);
        ret.add(24120036);
        ret.add(51120036);
        ret.add(22170036);
        ret.add(4120036);
        ret.add(2120036);
        ret.add(31120036);
        ret.add(1320036);
        ret.add(27120036);
        ret.add(3120036);
        ret.add(2220036);
        ret.add(22131001);//에반 매직실드
        ret.add(31000003);//데몬슬레이어HP증가
        //ret.add(32121010);//배틀 레이지
        ret.add(61100007);//이너 블레이즈
        ret.add(60000222);//아이언 윌
        ret.add(61110007);//어드밴스드 이너블레이즈
        ret.add(80000006);//아이언 윌 : 링크
        ret.add(15000008);
        return ret;
    }

    public static List<Integer> getPercentMP() {
        List<Integer> ret = new ArrayList<>();
        ret.add(1220037);
        ret.add(3220037);
        ret.add(35120037);
        ret.add(21120037);
        ret.add(23120037);
        ret.add(65120037);
        ret.add(31220037);
        ret.add(2320037);
        ret.add(1120037);
        ret.add(33120037);
        ret.add(4220037);
        ret.add(32120037);
        ret.add(61120037);
        ret.add(5220037);
        ret.add(5320037);
        ret.add(36120037);
        ret.add(5120037);
        ret.add(4340037);
        ret.add(24120037);
        ret.add(51120037);
        ret.add(22170037);
        ret.add(4120037);
        ret.add(2120037);
        ret.add(31120037);
        ret.add(1320037);
        ret.add(27120037);
        ret.add(3120037);
        ret.add(2220037);
        //ret.add(32121010);//배틀 레이지        
        return ret;
    }

    public static int[] getInnerSkillbyRank(int rank) {
        if (rank == 0) {
            return rankC;
        } else if (rank == 1) {
            return rankB;
        } else if (rank == 2) {
            return rankA;
        } else if (rank == 3) {
            return rankS;
        } else {
            return null;
        }
    }

    public static int getBonusAttackCount(int skillid) {
        switch (skillid) {
            case 3211006:
                return 3220010;
            default:
                return 0;
        }
    }

    public static int[] getAzwanRecipes() {
        return azwanRecipes;
    }

    public static int[] getAzwanScrolls() {
        return azwanScrolls;
    }

    public static Pair[] getUseItems() {
        return useItems;
    }

    public static int[] getCirculators() {
        return circulators;
    }

    public static int getExpNeededForLevel(final int level) {
        if (level < 0 || level >= exp.length) {
            return Integer.MAX_VALUE;
        }
        return exp[level];
    }

    public static int getGuildExpNeededForLevel(final int level) {
        if (level < 0 || level >= guildexp.length) {
            return Integer.MAX_VALUE;
        }
        return guildexp[level];
    }

    public static int getPVPExpNeededForLevel(final int level) {
        if (level < 0 || level >= pvpExp.length) {
            return Integer.MAX_VALUE;
        }
        return pvpExp[level];
    }

    public static int getClosenessNeededForLevel(final int level) {
        return closeness[level - 1];
    }

    public static int getMountExpNeededForLevel(final int level) {
        return mountexp[level - 1];
    }

    public static int getTraitExpNeededForLevel(final int level) {
        if (level < 0 || level >= cumulativeTraitExp.length) {
            return Integer.MAX_VALUE;
        }
        return cumulativeTraitExp[level];
    }

    public static int getSetExpNeededForLevel(final int level) {
        if (level < 0 || level >= setScore.length) {
            return Integer.MAX_VALUE;
        }
        return setScore[level];
    }

    public static int getOtherRequiredEXP(final int level) {
        return (int) (70.0d * Math.pow(Math.E, 0.1d * level));
    }

    public static int getProfessionEXP(final int level) {
        return ((100 * level * level) + (level * 400)) / 2;
    }

    public static boolean isHarvesting(final int itemId) {
        return itemId >= 1500000 && itemId < 1520000;
    }

    public static double maxViewRangeSq() {
        //return Double.POSITIVE_INFINITY;
        return 2000000; // 1024
    }

    public static int maxViewRangeSq_Half() {
        return 500000; // 800 * 800
    }

    public static boolean isKOC(final int job) {
        return job >= 1000 && job < 2000;
    }

    public static boolean isEvan(final int job) {
        return job == 2001 || (job / 100 == 22);
    }

    public static boolean isMercedes(final int job) {
        return job == 2002 || (job / 100 == 23);
    }

    public static boolean isPhantom(final int job) {
        return job == 2003 || (job / 100 == 24);
    }

    public static boolean isAngelicBuster(int job) {
        return job == 6500 || (job >= 6500 && job <= 6512);
    }

    public static boolean isMihile(final int job) {
        return job == 5000 || (job >= 5100 && job <= 5112);
    }

    public static boolean isWildHunter(final int job) {
        return (job >= 3300 && job <= 3312);
    }

    public static boolean isDemonAvenger(int job) {
        return job == 3101 || (job >= 3120 && job <= 3122);
    }

    public static boolean isHayato(int job) {
        return job == 6000 || (job >= 6100 && job <= 6112);
    }

    public static boolean isKanna(int job) {
        return job == 6001 || (job >= 6200 && job <= 6212);
    }

    public static boolean isBeastTamer(int job) {
        return job == 4001 || (job >= 4200 && job <= 4212);
    }

    public static boolean isLynn(int job) {
        return job == 5001 || (job >= 5200 && job <= 5212);
    }

    public static boolean isMukhyun(int job) {
        return job == 6002 || (job >= 6500 && job <= 6512);
    }

    public static boolean isDemon(final int job) {
        return job == 3001 || (job >= 3100 && job <= 3112);
    }

    public static boolean isXenon(final int job) {
        return job == 3002 || (job >= 3600 && job <= 3612);
    }

    public static boolean isAran(final int job) {
        return job >= 2000 && job <= 2112 && job != 2001 && job != 2002 && job != 2003;
    }

    public static boolean isResist(final int job) {
        return (job / 1000 == 3) && !isDemon(job);
    }

    public static boolean isAdventurer(final int job) {
        return (job >= 0 && job < 1000) && !isDualBlade(job) && !isCannon(job);
    }

    public static boolean isCannon(final int job) {
        return job == 1 || job == 501 || (job >= 530 && job <= 532);
    }

    public static boolean isDualBlade(final int job) {
        return (job >= 430 && job <= 434);
    }

    public static boolean isLuminous(final int job) {
        return job == 2004 || (job >= 2700 && job <= 2712);
    }

    public static boolean isLightSkills(int skillid) {
        switch (skillid) {
            case 20041226:
            case 27001100:
            case 27101100:
            case 27111100:
            case 27121100:
                return true;
        }
        return false;
    }

    public static boolean isDarkSkills(int skillid) {
        switch (skillid) {
            case 27001201:
            case 27101202:
            case 27111202:
            case 27121201:
            case 27121202:
                return true;
        }
        return false;
    }

    public static int isLightSkillsGaugeCheck(int skillid) {
        switch (skillid) {
            case 20041226:
                return 110;
            case 27001100:
                return 123;
            case 27101100:
                return 225;
            case 27101101:
                return 30;
            case 27111100:
                return 155;
            case 27111101:
                return 50;
            case 27121100:
                return 264;
            default:
                return 0;
        }
    }

    public static int isDarkSkillsGaugeCheck(int skillid) {
        switch (skillid) {
            case 27001201:
                return 140;
            case 27101202:
                return 73;
            case 27111202:
                return 210;
            case 27121201:
                return 10;
            case 27121202:
                return 397;
            default:
                return 0;
        }
    }

    public static boolean isRecoveryIncSkill(final int id) {
        switch (id) {
            case 1110000:
            case 2000000:
            case 1210000:
            case 11110000:
            case 4100002:
            case 4200001:
                return true;
        }
        return false;
    }

    public static boolean isLinkedAranSkill(final int id) {
        return getLinkedAranSkill(id) != id;
    }

    public static int getLinkedAranSkill(final int id) {
        switch (id) {
            case 21110007:
            case 21110008:
                return 21110002;
            case 21120009:
            case 21120010:
                return 21120002;
            case 4321001:
                return 4321000;
            case 33101006:
            case 33101007:
                return 33101005;
            //case 33101008:
            //return 33101004;
            case 35101009:
            case 35101010:
                return 35100008;
            case 35111009:
            case 35111010:
                return 35111001;
            case 35121013:
                return 35111004;
            case 35121011:
                return 35121009;
            case 32001007:
            case 32001008:
            case 32001009:
            case 32001010:
            case 32001011:
                return 32001001;
            case 5300007:
                return 5301001;
            case 5320011:
                return 5321004;
            case 23101007:
                return 23101001;
            case 23111010:
            case 23111009:
                return 23111008;
            case 31001006:
            case 31001007:
            case 31001008:
                return 31000004;
            case 30010183:
            case 30010184:
            case 30010186:
                return 30010110;
            case 5710012:
                return 5711002;
            case 31121010:
                return 31121000;
            case 5211015:
            case 5211016:
                return 5211011;
            case 24111008:
                return 24111006;
            case 24121010:
                return 24121003;
            case 5001008:
                return 5200010;
            case 5001009:
                return 5101004;
            case 2121055://메기도플레임
                return 2121052;
            case 24120055:
                return 24121052;
            case 27120211:
                return 27121201; //모닝 스타폴                
            case 32120055://배틀킹바
                return 32120052;
            case 31211011://쉴드차지
                return 31211002;
            case 31221014:
                return 31221001;
            case 31011004://엑시드스킬들
            case 31011005:
            case 31011006:
            case 31011007:
                return 31011000;
            case 31201007:
            case 31201008:
            case 31201009:
            case 31201010:
                return 31201000;
            case 31211007:
            case 31211008:
            case 31211009:
            case 31211010:
                return 31211000;
            case 31221009:
            case 31221010:
            case 31221011:
            case 31221012:
                return 31221000;
            case 36111009://컴뱃스위칭
            case 36111010:
                return 36111000;
            case 36121011://퍼지롭 매스커레이드
            case 36121012:
                return 36121001;
            case 36101008://퀵실버소드
            case 36101009:
                return 36101000;
            case 36121013:
            case 36121014:
                return 36121002;
            case 61110009:
                return 61111003; //리게인 스트렝스           
            case 61001004:
            case 61001005:
            case 61110212:
            case 61120219:
                return 61001000; //드래곤슬래시   
            case 61120018:
                return 61121105;
            case 61110211:
            case 61120007:
            case 61121217:
                return 61101002; //윌오브소드   
            case 61121201:
                return 61121100; //기가슬래셔     
            case 61121203:
                return 61121102; //블루 스트릭        
            case 65101006: // 스팅 익스플로전 (폭발)
                return 65101100; // 스팅 익스플로전
        }
        return id;
    }
    
    public final static boolean isAtomSkill(int nSkillID) {
        switch (nSkillID) {
            case 13120017: // 트라이플링 윔
            case 24100003: // 블랑 카르트
            case 24120002: // 느와르 카르트
            case -1: {
                return true;
            }
        }
        return false;
    }
    
    public final static boolean isForceIncrease(int skillid) {
        switch (skillid) {
            case 24100003:
            case 24120002:
            case 31000004:
            case 31001006:
            case 31001007:
            case 31001008:
            case 30010166:
            case 30011167:
            case 30011168:
            case 30011169:
            case 30011170:
                return true;
        }
        return false;
    }

    public static int getBOF_ForJob(final int job) {
        return PlayerStats.getSkillByJob(12, job);
    }

    public static int getEmpress_ForJob(final int job) {
        return PlayerStats.getSkillByJob(73, job);
    }

    public static int getSkillByJob(final int skillID, final int job) { // test 라이딩 관련

        return skillID + (GameConstants.getBeginnerJob((short) job) * 10000);
    }

    public static short getBeginnerJob(final short job) {
        if (job % 1000 < 10) {
            return job;
        }
        switch (job / 100) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return 0;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return 1000;
            case 20:
                return 2000;
            case 21:
                return 2000;
            case 22:
                return 2001;
            case 23:
                return 2002;
            case 24:
                return 2003;
            case 27:
                return 2004;
            case 31:
                return 3001;
            case 36:
                return 3002;
            case 30:
            case 32:
            case 33:
            case 35:
                return 3000;
            case 41:
                return 4001;
            case 42:
                return 4002;
            case 50:
            case 51:
                return 5000;
            case 60:
            case 61:
                return 6000;
            case 65:
                return 6001;
            case 100:
            case 110:
                return 10000;
        }
        return 0;
    }

    public static boolean isElementAmp_Skill(final int skill) {
        switch (skill) {
            case 2110001:
            case 2210001:
            case 12110001:
            case 22150000:
                return true;
        }
        return false;
    }

    public static int getMPEaterForJob(final int job) {
        switch (job) {
            case 210:
            case 211:
            case 212:
                return 2100000;
            case 220:
            case 221:
            case 222:
                return 2200000;
            case 230:
            case 231:
            case 232:
                return 2300000;
        }
        return 2100000; // Default, in case GM
    }

    public static boolean isPyramidSkill(final int skill) {
        switch (skill) {
            case 1020:
            case 20001020:
            case 30001020:
            case 60001020:
            case 30021020:
            case 72000034:
            case 50001020:
            case 20031020:
            case 20011020:
            case 10001020:
            case 20041020:
            case 20021020:
            case 30011020:
                //case DUAL_BOW: //magic arrow
                return true;
            default:
                return false;
        } // isBeginnerJob(skill / 10000) && skill % 10000 == 1020 
    }

    public static boolean isInflationSkill(final int skill) {
        return (isBeginnerJob(skill / 10000)) && (skill % 10000 == 1095 || skill % 10000 == 1094 || skill % 10000 == 1092);
    }

    public static boolean isMulungSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && (skill % 10000 == 1009 || skill % 10000 == 1010 || skill % 10000 == 1011);
    }

    public static boolean isIceKnightSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && (skill % 10000 == 1098 || skill % 10000 == 99 || skill % 10000 == 100 || skill % 10000 == 103 || skill % 10000 == 104 || skill % 10000 == 1105);
    }

    public static boolean isThrowingStar(final int itemId) {
        return itemId / 10000 == 207;
    }

    public static boolean isBullet(final int itemId) {
        return itemId / 10000 == 233;
    }

    public static boolean isRechargable(final int itemId) {
        return isThrowingStar(itemId) || isBullet(itemId);
    }

    public static boolean isExtractorBag(final int itemId) {
        return itemId / 10000 == 433;
    }

    public static boolean isOverall(final int itemId) {
        return itemId / 10000 == 105;
    }

    public static final boolean isPet(final int itemId) {
        return itemId >= 5000000 && itemId <= 5000999;
    }

    public static final int getPetBuff(final int itemId) {
        switch (itemId) {
            //����
            case 5000228:
            case 5000229:
            case 5000230:
            case 5000231:
            case 5000232:
            case 5000233:
                return 7;
            case 5000243:
            case 5000244:
            case 5000245:
                return 20;
            case 5000249:
            case 5000250:
            case 5000251:
                return 26;
            case 5000256:
            case 5000257:
            case 5000258:
                return 29;
            case 5000271:
            case 5000272:
            case 5000273:
                return 56;
            case 5000275:
            case 5000276:
            case 5000277:
                return 63;
            case 5000281:
            case 5000282:
            case 5000283:
                return 73;
            case 5000290:
            case 5000291:
            case 5000292:
                return 77;
            case 5000239:
                return 13;
            case 5000240:
                return 17;
            case 5000293:
            case 5000294:
            case 5000295:
                return 81;
            case 5000296:
            case 5000297:
            case 5000298:
                return 98;
            case 5000309:
            case 5000310:
            case 5000311:
                return 101;
            case 5000352:
            case 5000353:
            case 5000354:
                return 166;
            case 5000365:
            case 5000366:
            case 5000367:
                return 288;
            case 5000375:
            case 5000376:
            case 5000377:
                return 295;
        }
        return 0;
    }

    public static boolean isArrowForCrossBow(final int itemId) {
        return itemId >= 2061000 && itemId < 2062000;
    }

    public static boolean isArrowForBow(final int itemId) {
        return itemId >= 2060000 && itemId < 2061000;
    }

    public static boolean isMagicWeapon(final int itemId) {
        final int s = itemId / 10000;
        return s == 137 || s == 138; // wand, staff
    }

    public static boolean isWeapon(final int itemID) {
        return ((itemID >= 1212000 && itemID < 1500000) || (itemID >= 1520000 && itemID < 1560000)) && itemID != 1342069;
    }

    public static boolean isHair(final int itemId) {
        return itemId / 10000 == 3;
    }

    public static boolean isFace(final int itemId) {
        return itemId / 10000 == 2;
    }

    public static MapleInventoryType getInventoryType(final int itemId) {
        final byte type = (byte) (itemId / 1000000);
        if (type < 1 || type > 5) {
            return MapleInventoryType.UNDEFINED;
        }
        return MapleInventoryType.getByType(type);
    }

    public static boolean isInBag(final int slot, final byte type) {
        return ((slot >= 101 && slot <= 512) && type == MapleInventoryType.ETC.getType());
    }

    public static MapleWeaponType getWeaponType(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        switch (cat) { // 39, 50, 51 ??
            case 21:
                return MapleWeaponType.PLANE;
            case 30:
                return MapleWeaponType.SWORD1H;
            case 31:
                return MapleWeaponType.AXE1H;
            case 32:
                return MapleWeaponType.BLUNT1H;
            case 33:
                return MapleWeaponType.DAGGER;
            case 34:
                return MapleWeaponType.KATARA;
            case 35:
                return MapleWeaponType.MAGIC_ARROW; // can be magic arrow or cards
            case 36:
                return MapleWeaponType.CANE;
            case 37:
                return MapleWeaponType.WAND;
            case 38:
                return MapleWeaponType.STAFF;
            case 40:
                return MapleWeaponType.SWORD2H;
            case 41:
                return MapleWeaponType.AXE2H;
            case 42:
                return MapleWeaponType.BLUNT2H;
            case 43:
                return MapleWeaponType.SPEAR;
            case 44:
                return MapleWeaponType.POLE_ARM;
            case 45:
                return MapleWeaponType.BOW;
            case 46:
                return MapleWeaponType.CROSSBOW;
            case 47:
                return MapleWeaponType.CLAW;
            case 48:
                return MapleWeaponType.KNUCKLE;
            case 49:
                return MapleWeaponType.GUN;
            case 52:
                return MapleWeaponType.DUAL_BOW;
            case 53:
                return MapleWeaponType.CANNON;
        }
        //System.out.println("Found new Weapon: " + cat + ", ItemId: " + itemId);
        return MapleWeaponType.NOT_A_WEAPON;
    }

    public static boolean isShield(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        return (cat == 9 && itemId < 1093200);
    }

    public static boolean isHat(int itemid) {
        return itemid / 10000 == 100;
    }

    public static boolean isForeHead(int itemid) {
        return itemid / 10000 == 101;
    }

    public static boolean isShoe(int itemid) {
        return itemid / 10000 == 107;
    }

    public static boolean isGlove(int itemid) {
        return itemid / 10000 == 108;
    }

    public static boolean isCape(int itemid) {
        return itemid / 10000 == 110;
    }

    public static boolean isEquip(final int itemId) {
        return itemId / 1000000 == 1;
    }

    public static boolean isCleanSlate(int itemId) {
        return itemId / 100 == 20490;
    }

    public static boolean isAccessoryScroll(int itemId) {
        return itemId / 100 == 20492;
    }

    public static boolean isChaosScroll(int itemId) {
        if (itemId > 2049179) {
            return false;
        }
        if (itemId >= 2049105 && itemId <= 2049110) {
            return false;
        }
        return itemId / 100 == 20491 || itemId == 2040126;
    }

    public static int getChaosNumber(int itemId) {
        return (itemId == 2049122 || itemId == 2049116) ? 6 : 5;
    }

    public static boolean isEquipScroll(int scrollId) {
        return scrollId / 100 == 20493;
    }

    public static boolean isPotentialScroll(int scrollId) {
        return scrollId / 100 == 20494 || scrollId / 100 == 20497 || scrollId == 5534000;
    }
    
    public static boolean isStamp(int itemID) {
        return (itemID / 100 == 20495);
    }
    
    public static boolean isMagnify(int itemID) {
        return (itemID / 10000 == 246);
    }
    
    public static boolean isCube(int itemID) {
        return (itemID / 1000 == 5062) && itemID != 5062400;
    }

    public static boolean isAzwanScroll(int scrollId) {
        //return MapleItemInformationProvider.getInstance().getEquipStats(scroll.getItemId()).containsKey("tuc");
        //should add this ^ too.
        return scrollId >= 2046060 && scrollId <= 2046069 || scrollId >= 2046141 && scrollId <= 2046145 || scrollId >= 2046519 && scrollId <= 2046530 || scrollId >= 2046701 && scrollId <= 2046712;
    }

    public static boolean isSpecialScroll(final int scrollId) {
        switch (scrollId) {
            case 2040727: // Spikes on show
            case 2041058: // Cape for Cold protection

            case 2049600:
            case 2049601:
            case 2049602:
            case 2049603:
            case 2049604:
            case 2049605:
            case 2049606:

            case 2530000:
            case 2530001:
            case 2530002:
            case 2531000:
            case 2532000:

            case 5063100:
            case 5064000:
            case 5064002:
            case 5064100:
            case 5064101:
            case 5064200:
            case 5064201:
            case 5064300:
            case 5064301:
            case 5063000:
            case 5063101:
                return true;
        }
        return false;
    }

    public static boolean isTwoHanded(final int itemId) {
        switch (getWeaponType(itemId)) {
            case AXE2H:
            case GUN:
            case KNUCKLE:
            case BLUNT2H:
            case BOW:
            case CLAW:
            case CROSSBOW:
            case POLE_ARM:
            case SPEAR:
            case SWORD2H:
            case CANNON:
                //case DUAL_BOW: //magic arrow
                return true;
            default:
                return false;
        }
    }

    public static boolean isTownScroll(final int id) {
        return id >= 2030000 && id < 2040000;
    }

    public static boolean isUpgradeScroll(final int id) {
        return id >= 2040000 && id < 2050000;
    }

    public static boolean isGun(final int id) {
        return id >= 1492000 && id < 1500000;
    }

    public static boolean isUse(final int id) {
        return id >= 2000000 && id < 3000000;
    }

    public static boolean isSummonSack(final int id) {
        return id / 10000 == 210;
    }

    public static boolean isMonsterCard(final int id) {
        return id / 10000 == 238;
    }

    public static boolean isSpecialCard(final int id) {
        return id / 1000 >= 2388;
    }

    public static int getCardShortId(final int id) {
        return id % 10000;
    }

    public static boolean isGem(final int id) {
        return id >= 4250000 && id <= 4251402;
    }

    public static boolean isCore(final int id) {
        return id >= 3600000 && id <= 3604011;
    }

    public static boolean isOtherGem(final int id) {
        switch (id) {
            case 4001174:
            case 4001175:
            case 4001176:
            case 4001177:
            case 4001178:
            case 4001179:
            case 4001180:
            case 4001181:
            case 4001182:
            case 4001183:
            case 4001184:
            case 4001185:
            case 4001186:
            case 4031980:
            case 2041058:
            case 2040727:
            case 1032062:
            case 4032334:
            case 4032312:
            case 1142156:
            case 1142157:
                return true; //mostly quest items
        }
        return false;
    }

    public static byte EqitemPostionById(final int itemId) {
        switch (itemId / 10000) {
            case 100:
                return -1;
            case 104://코트
            case 105://롱코트
                return -5;
            case 106://바지
                return -6;
            case 107://신발
                return -7;
            case 108:
                return -8;
            case 110:
                return -9;
            case 109:
                return -10;
            case 112:
                return -17;
            case 114:
                return -21;
            case 121:
            case 122:
            case 123:
            case 124:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 183:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
                return -11;
        }
        return 1;
    }

    public static boolean isValidLinkSkillForJob(int skillID, short jobID) {
        switch (jobID) {
            case 2312: //Mercedes
            case 2311:
                return skillID == 20021110;
            case 2412: //Phantom
            case 2411:
                return skillID == 20030204;
            case 3112: //Demon Slayer
            case 3111:
                return skillID == 30010112;
            case 532: //Cannoneer
            case 531:
                return skillID == 110;
            case 5112:
            case 5111:
                return skillID == 50001214;
        }
        return false;
    }

    //Link Skills work in a pretty weird way -- you don't give the skill itself, but rather a different skill in the 8000xxxx range.
    public static int getRelatedLinkSkill(final int skillID) {
        switch (skillID) {
            case 20021110:
                return 80001040;
            case 110:
                return 80000000;
            case 30010112:
                return 80000001;
            case 20030204:
                return 80000002;
            case 50001214:
                return 80001140;
        }
        return 0;
    }

    public static int getLinkSkill(final int skillID) {
        switch (skillID) {
            case 80001040:
                return 20021110;
            case 80000000:
                return 110;
            case 80000001:
                return 30010112;
            case 80000002:
                return 20030204;
            case 80001140:
                return 50001214;
        }
        return 0;
    }

    public static boolean isCustomQuest(final int id) {
        return id > 99999;
    }

    public static int getTaxAmount(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.06 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.05 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.04 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.018 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.008 * meso);
        }
        return 0;
    }

    public static int EntrustedStoreTax(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.025 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.02 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.015 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.009 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.004 * meso);
        }
        return 0;
    }

    public static byte gachaponRareItem(final int id) {
        switch (id) {
            case 2340000: // White Scroll
            case 2049100: // Chaos Scroll
            case 2049000: // Reverse Scroll
            case 2049001: // Reverse Scroll
            case 2049002: // Reverse Scroll
            case 2040006: // Miracle
            case 2040007: // Miracle
            case 2040303: // Miracle
            case 2040403: // Miracle
            case 2040506: // Miracle
            case 2040507: // Miracle
            case 2040603: // Miracle
            case 2040709: // Miracle
            case 2040710: // Miracle
            case 2040711: // Miracle
            case 2040806: // Miracle
            case 2040903: // Miracle
            case 2041024: // Miracle
            case 2041025: // Miracle
            case 2043003: // Miracle
            case 2043103: // Miracle
            case 2043203: // Miracle
            case 2043303: // Miracle
            case 2043703: // Miracle
            case 2043803: // Miracle
            case 2044003: // Miracle
            case 2044103: // Miracle
            case 2044203: // Miracle
            case 2044303: // Miracle
            case 2044403: // Miracle
            case 2044503: // Miracle
            case 2044603: // Miracle
            case 2044908: // Miracle
            case 2044815: // Miracle
            case 2044019: // Miracle
            case 2044703: // Miracle
                return 2;
            //1 = wedding msg o.o
        }
        return 0;
    }

    public final static int[] goldrewards = {
        2049400, 1, 2049401, 2, 2049301, 2, 2340000, 1, 2070007, 2, 2070016, 1, 2330007, 1, 2070018, 1, 1402037, 1,
        2290096, 1, 2290049, 1, 2290041, 1, 2290047, 1, 2290095, 1, 2290017, 1, 2290075, 1, 2290085, 1, 2290116, 1,
        1302059, 3, 2049100, 1, 1092049, 1, 1102041, 1, 1432018, 3, 1022047, 3, 3010051, 1, 3010020, 1, 2040914, 1,
        1432011, 3, 1442020, 3, 1382035, 3, 1372010, 3, 1332027, 3, 1302056, 3, 1402005, 3, 1472053, 3, 1462018, 3,
        1452017, 3, 1422013, 3, 1322029, 3, 1412010, 3, 1472051, 1, 1482013, 1, 1492013, 1, 1382049, 1, 1382050, 1,
        1382051, 1, 1382052, 1, 1382045, 1, 1382047, 1, 1382048, 1, 1382046, 1, 1372035, 1, 1372036, 1, 1372037, 1,
        1372038, 1, 1372039, 1, 1372040, 1, 1372041, 1, 1372042, 1, 1332032, 8, 1482025, 7, 4001011, 8, 4001010, 8,
        4001009, 8, 2047000, 1, 2047001, 1, 2047002, 1, 2047100, 1, 2047101, 1, 2047102, 1, 2047200, 1, 2047201, 1,
        2047202, 1, 2047203, 1, 2047204, 1, 2047205, 1, 2047206, 1, 2047207, 1, 2047208, 1, 2047300, 1, 2047301, 1,
        2047302, 1, 2047303, 1, 2047304, 1, 2047305, 1, 2047306, 1, 2047307, 1, 2047308, 1, 2047309, 1, 2046004, 1,
        2046005, 1, 2046104, 1, 2046105, 1, 2046208, 1, 2046209, 1, 2046210, 1, 2046211, 1, 2046212, 1, 1132014, 3,
        1132015, 2, 1132016, 1, 1002801, 2, 1102205, 2, 1332079, 2, 1332080, 2, 1402048, 2, 1402049, 2, 1402050, 2,
        1402051, 2, 1462052, 2, 1462054, 2, 1462055, 2, 1472074, 2, 1472075, 2, 1332077, 1, 1382082, 1, 1432063, 1,
        1452087, 1, 1462053, 1, 1472072, 1, 1482048, 1, 1492047, 1, 2030008, 5, 1442018, 3, 2040900, 4, 2049100, 10,
        2000005, 10, 2000004, 10, 4280000, 8, 2430144, 10, 2290285, 10, 2028061, 10, 2028062, 10, 2530000, 5, 2531000, 5};
    public final static int[] silverrewards = {
        2049401, 2, 2049301, 2, 3010041, 1, 1002452, 6, 1002455, 6, 2290084, 1, 2290048, 1, 2290040, 1, 2290046, 1,
        2290074, 1, 2290064, 1, 2290094, 1, 2290022, 1, 2290056, 1, 2290066, 1, 2290020, 1, 1102082, 1, 1302049, 1,
        2340000, 1, 1102041, 1, 1452019, 2, 4001116, 3, 4001012, 3, 1022060, 2, 2430144, 5, 2290285, 5, 2028062, 5,
        2028061, 5, 2530000, 1, 2531000, 1, 2041100, 1, 2041101, 1, 2041102, 1, 2041103, 1, 2041104, 1, 2041105, 1,
        2041106, 1, 2041107, 1, 2041108, 1, 2041109, 1, 2041110, 1, 2041111, 1, 2041112, 1, 2041113, 1, 2041114, 1,
        2041115, 1, 2041116, 1, 2041117, 1, 2041118, 1, 2041119, 1, 2041300, 1, 2041301, 1, 2041302, 1, 2041303, 1,
        2041304, 1, 2041305, 1, 2041306, 1, 2041307, 1, 2041308, 1, 2041309, 1, 2041310, 1, 2041311, 1, 2041312, 1,
        2041313, 1, 2041314, 1, 2041315, 1, 2041316, 1, 2041317, 1, 2041318, 1, 2041319, 1, 2049200, 1, 2049201, 1,
        2049202, 1, 2049203, 1, 2049204, 1, 2049205, 1, 2049206, 1, 2049207, 1, 2049208, 1, 2049209, 1, 2049210, 1,
        2049211, 1, 1432011, 3, 1442020, 3, 1382035, 3, 1372010, 3, 1332027, 3, 1302056, 3, 1402005, 3, 1472053, 3,
        1462018, 3, 1452017, 3, 1422013, 3, 1322029, 3, 1412010, 3, 1002587, 3, 1402044, 1, 2101013, 4, 1442046, 1,
        1422031, 1, 1332054, 3, 1012056, 3, 1022047, 3, 3012002, 1, 1442012, 3, 1442018, 3, 1432010, 3, 1432036, 1,
        2000005, 10, 2049100, 10, 2000004, 10, 4280001, 8};
    public final static int[] peanuts = {2430091, 200, 2430092, 200, 2430093, 200, 2430101, 200, 2430102, 200, 2430136, 200, 2430149, 200,//mounts 
        2340000, 1, //rares
        1152000, 5, 1152001, 5, 1152004, 5, 1152005, 5, 1152006, 5, 1152007, 5, 1152008, 5, //toenail only comes when db is out.
        1152064, 5, 1152065, 5, 1152066, 5, 1152067, 5, 1152070, 5, 1152071, 5, 1152072, 5, 1152073, 5,
        3010019, 2, //chairs
        1001060, 10, 1002391, 10, 1102004, 10, 1050039, 10, 1102040, 10, 1102041, 10, 1102042, 10, 1102043, 10, //equips
        1082145, 5, 1082146, 5, 1082147, 5, 1082148, 5, 1082149, 5, 1082150, 5, //wg
        2043704, 10, 2040904, 10, 2040409, 10, 2040307, 10, 2041030, 10, 2040015, 10, 2040109, 10, 2041035, 10, 2041036, 10, 2040009, 10, 2040511, 10, 2040408, 10, 2043804, 10, 2044105, 10, 2044903, 10, 2044804, 10, 2043009, 10, 2043305, 10, 2040610, 10, 2040716, 10, 2041037, 10, 2043005, 10, 2041032, 10, 2040305, 10, //scrolls
        2040211, 5, 2040212, 5, 1022097, 10, //dragon glasses
        2049000, 10, 2049001, 10, 2049002, 10, 2049003, 10, //clean slate
        1012058, 5, 1012059, 5, 1012060, 5, 1012061, 5,//pinocchio nose msea only.
        1332100, 10, 1382058, 10, 1402073, 10, 1432066, 10, 1442090, 10, 1452058, 10, 1462076, 10, 1472069, 10, 1482051, 10, 1492024, 10, 1342009, 10, //durability weapons level 105
        2049400, 1, 2049401, 2, 2049301, 2,
        2049100, 10,
        2430144, 10,
        2290285, 10,
        2028062, 10,
        2028061, 10,
        2530000, 5,
        2531000, 5,
        1032080, 5,
        1032081, 4,
        1032082, 3,
        1032083, 2,
        1032084, 1,
        1112435, 5,
        1112436, 4,
        1112437, 3,
        1112438, 2,
        1112439, 1,
        1122081, 5,
        1122082, 4,
        1122083, 3,
        1122084, 2,
        1122085, 1,
        1132036, 5,
        1132037, 4,
        1132038, 3,
        1132039, 2,
        1132040, 1,
        //source
        1092070, 5,
        1092071, 4,
        1092072, 3,
        1092073, 2,
        1092074, 1,
        1092075, 5,
        1092076, 4,
        1092077, 3,
        1092078, 2,
        1092079, 1,
        1092080, 5,
        1092081, 4,
        1092082, 3,
        1092083, 2,
        1092084, 1,
        1092087, 1,
        1092088, 1,
        1092089, 1,
        1302143, 5,
        1302144, 4,
        1302145, 3,
        1302146, 2,
        1302147, 1,
        1312058, 5,
        1312059, 4,
        1312060, 3,
        1312061, 2,
        1312062, 1,
        1322086, 5,
        1322087, 4,
        1322088, 3,
        1322089, 2,
        1322090, 1,
        1332116, 5,
        1332117, 4,
        1332118, 3,
        1332119, 2,
        1332120, 1,
        1332121, 5,
        1332122, 4,
        1332123, 3,
        1332124, 2,
        1332125, 1,
        1342029, 5,
        1342030, 4,
        1342031, 3,
        1342032, 2,
        1342033, 1,
        1372074, 5,
        1372075, 4,
        1372076, 3,
        1372077, 2,
        1372078, 1,
        1382095, 5,
        1382096, 4,
        1382097, 3,
        1382098, 2,
        1392099, 1,
        1402086, 5,
        1402087, 4,
        1402088, 3,
        1402089, 2,
        1402090, 1,
        1412058, 5,
        1412059, 4,
        1412060, 3,
        1412061, 2,
        1412062, 1,
        1422059, 5,
        1422060, 4,
        1422061, 3,
        1422062, 2,
        1422063, 1,
        1432077, 5,
        1432078, 4,
        1432079, 3,
        1432080, 2,
        1432081, 1,
        1442107, 5,
        1442108, 4,
        1442109, 3,
        1442110, 2,
        1442111, 1,
        1452102, 5,
        1452103, 4,
        1452104, 3,
        1452105, 2,
        1452106, 1,
        1462087, 5,
        1462088, 4,
        1462089, 3,
        1462090, 2,
        1462091, 1,
        1472113, 5,
        1472114, 4,
        1472115, 3,
        1472116, 2,
        1472117, 1,
        1482075, 5,
        1482076, 4,
        1482077, 3,
        1482078, 2,
        1482079, 1,
        1492075, 5,
        1492076, 4,
        1492077, 3,
        1492078, 2,
        1492079, 1,
        1132012, 2,
        1132013, 1,
        1942002, 2,
        1952002, 2,
        1962002, 2,
        1972002, 2,
        1612004, 2,
        1622004, 2,
        1632004, 2,
        1642004, 2,
        1652004, 2,
        2047000, 1,
        2047001, 1,
        2047002, 1,
        2047100, 1,
        2047101, 1,
        2047102, 1,
        2047200, 1,
        2047201, 1,
        2047202, 1,
        2047203, 1,
        2047204, 1,
        2047205, 1,
        2047206, 1,
        2047207, 1,
        2047208, 1,
        2047300, 1,
        2047301, 1,
        2047302, 1,
        2047303, 1,
        2047304, 1,
        2047305, 1,
        2047306, 1,
        2047307, 1,
        2047308, 1,
        2047309, 1,
        2046004, 1,
        2046005, 1,
        2046104, 1,
        2046105, 1,
        2046208, 1,
        2046209, 1,
        2046210, 1,
        2046211, 1,
        2046212, 1,
        2049200, 1,
        2049201, 1,
        2049202, 1,
        2049203, 1,
        2049204, 1,
        2049205, 1,
        2049206, 1,
        2049207, 1,
        2049208, 1,
        2049209, 1,
        2049210, 1,
        2049211, 1,
        //ele wand
        1372035, 1,
        1372036, 1,
        1372037, 1,
        1372038, 1,
        //ele staff
        1382045, 1,
        1382046, 1,
        1382047, 1,
        1382048, 1,
        1382049, 1,
        1382050, 1, // Blue Dragon Staff
        1382051, 1,
        1382052, 1,
        1372039, 1,
        1372040, 1,
        1372041, 1,
        1372042, 1,
        2070016, 1,
        2070007, 2,
        2330007, 1,
        2070018, 1,
        2330008, 1,
        2070023, 1,
        2070024, 1,
        2028062, 5,
        2028061, 5};
    public static int[] eventCommonReward = {
        0, 10,
        1, 10,
        4, 5,
        5060004, 25,
        4170024, 25,
        4280000, 5,
        4280001, 6,
        5490000, 5,
        5490001, 6
    };
    public static int[] eventUncommonReward = {
        1, 4, 2, 8, 3, 8, 2022179, 5, 5062000, 20, 2430082, 20, 2430092, 20, 2022459, 2, 2022460, 1, 2022462, 1,
        2430103, 2, 2430117, 2, 2430118, 2, 2430201, 4, 2430228, 4, 2430229, 4, 2430283, 4, 2430136, 4, 2430476, 4,
        2430511, 4, 2430206, 4, 2430199, 1, 1032062, 5, 5220000, 28, 2022459, 5, 2022460, 5, 2022461, 5, 2022462, 5,
        2022463, 5, 5050000, 2, 4080100, 10, 4080000, 10, 2049100, 10, 2430144, 10, 2290285, 10, 2028062, 10,
        2028061, 10, 2530000, 5, 2531000, 5, 2041100, 1, 2041101, 1, 2041102, 1, 2041103, 1, 2041104, 1, 2041105, 1,
        2041106, 1, 2041107, 1, 2041108, 1, 2041109, 1, 2041110, 1, 2041111, 1, 2041112, 1, 2041113, 1, 2041114, 1,
        2041115, 1, 2041116, 1, 2041117, 1, 2041118, 1, 2041119, 1, 2041300, 1, 2041301, 1, 2041302, 1, 2041303, 1,
        2041304, 1, 2041305, 1, 2041306, 1, 2041307, 1, 2041308, 1, 2041309, 1, 2041310, 1, 2041311, 1, 2041312, 1,
        2041313, 1, 2041314, 1, 2041315, 1, 2041316, 1, 2041317, 1, 2041318, 1, 2041319, 1, 2049200, 1, 2049201, 1,
        2049202, 1, 2049203, 1, 2049204, 1, 2049205, 1, 2049206, 1, 2049207, 1, 2049208, 1, 2049209, 1, 2049210, 1,
        2049211, 1};
    public static int[] eventRareReward = {
        2049100, 5, 2430144, 5, 2290285, 5, 2028062, 5, 2028061, 5, 2530000, 2, 2531000, 2, 2049116, 1, 2049401, 10,
        2049301, 20, 2049400, 3, 2340000, 1, 3010130, 5, 3010131, 5, 3010132, 5, 3010133, 5, 3010136, 5, 3010116, 5,
        3010117, 5, 3010118, 5, 1112405, 1, 1112445, 1, 1022097, 1, 2040211, 1, 2040212, 1, 2049000, 2, 2049001, 2,
        2049002, 2, 2049003, 2, 1012058, 2, 1012059, 2, 1012060, 2, 1012061, 2, 2022460, 4, 2022461, 3, 2022462, 4,
        2022463, 3, 2040041, 1, 2040042, 1, 2040334, 1, 2040430, 1, 2040538, 1, 2040539, 1, 2040630, 1, 2040740, 1,
        2040741, 1, 2040742, 1, 2040829, 1, 2040830, 1, 2040936, 1, 2041066, 1, 2041067, 1, 2043023, 1, 2043117, 1,
        2043217, 1, 2043312, 1, 2043712, 1, 2043812, 1, 2044025, 1, 2044117, 1, 2044217, 1, 2044317, 1, 2044417, 1,
        2044512, 1, 2044612, 1, 2044712, 1, 2046000, 1, 2046001, 1, 2046004, 1, 2046005, 1, 2046100, 1, 2046101, 1,
        2046104, 1, 2046105, 1, 2046200, 1, 2046201, 1, 2046202, 1, 2046203, 1, 2046208, 1, 2046209, 1, 2046210, 1,
        2046211, 1, 2046212, 1, 2046300, 1, 2046301, 1, 2046302, 1, 2046303, 1, 2047000, 1, 2047001, 1, 2047002, 1,
        2047100, 1, 2047101, 1, 2047102, 1, 2047200, 1, 2047201, 1, 2047202, 1, 2047203, 1, 2047204, 1, 2047205, 1,
        2047206, 1, 2047207, 1, 2047208, 1, 2047300, 1, 2047301, 1, 2047302, 1, 2047303, 1, 2047304, 1, 2047305, 1,
        2047306, 1, 2047307, 1, 2047308, 1, 2047309, 1, 1112427, 5, 1112428, 5, 1112429, 5, 1012240, 10, 1022117, 10,
        1032095, 10, 1112659, 10, 2070007, 10, 2330007, 5, 2070016, 5, 2070018, 5, 1152038, 1, 1152039, 1, 1152040, 1,
        1152041, 1, 1122090, 1, 1122094, 1, 1122098, 1, 1122102, 1, 1012213, 1, 1012219, 1, 1012225, 1, 1012231, 1,
        1012237, 1, 2070023, 5, 2070024, 5, 2330008, 5, 2003516, 5, 2003517, 1, 1132052, 1, 1132062, 1, 1132072, 1,
        1132082, 1, 1112585, 1, 1072502, 1, 1072503, 1, 1072504, 1, 1072505, 1, 1072506, 1, 1052333, 1, 1052334, 1,
        1052335, 1, 1052336, 1, 1052337, 1, 1082305, 1, 1082306, 1, 1082307, 1, 1082308, 1, 1082309, 1, 1003197, 1,
        1003198, 1, 1003199, 1, 1003200, 1, 1003201, 1, 1662000, 1, 1662001, 1, 1672000, 1, 1672001, 1, 1672002, 1,
        1112583, 1, 1032092, 1, 1132084, 1, 2430290, 1, 2430292, 1, 2430294, 1, 2430296, 1, 2430298, 1, 2430300, 1,
        2430302, 1, 2430304, 1, 2430306, 1, 2430308, 1, 2430310, 1, 2430312, 1, 2430314, 1, 2430316, 1, 2430318, 1,
        2430320, 1, 2430322, 1, 2430324, 1, 2430326, 1, 2430328, 1, 2430330, 1, 2430332, 1, 2430334, 1, 2430336, 1,
        2430338, 1, 2430340, 1, 2430342, 1, 2430344, 1, 2430347, 1, 2430349, 1, 2430351, 1, 2430353, 1, 2430355, 1,
        2430357, 1, 2430359, 1, 2430361, 1, 2430392, 1, 2430512, 1, 2430536, 1, 2430477, 1, 2430146, 1, 2430148, 1,
        2430137, 1,};
    public static int[] eventSuperReward = {
        2022121, 10,
        4031307, 50,
        3010127, 10,
        3010128, 10,
        3010137, 10,
        3010157, 10,
        2049300, 10,
        2040758, 10,
        1442057, 10,
        2049402, 10,
        2049304, 1,
        2049305, 1,
        2040759, 7,
        2040760, 5,
        2040125, 10,
        2040126, 10,
        1012191, 5,
        1112514, 1, //untradable/tradable
        1112531, 1,
        1112629, 1,
        1112646, 1,
        1112515, 1, //untradable/tradable
        1112532, 1,
        1112630, 1,
        1112647, 1,
        1112516, 1, //untradable/tradable
        1112533, 1,
        1112631, 1,
        1112648, 1,
        2040045, 10,
        2040046, 10,
        2040333, 10,
        2040429, 10,
        2040542, 10,
        2040543, 10,
        2040629, 10,
        2040755, 10,
        2040756, 10,
        2040757, 10,
        2040833, 10,
        2040834, 10,
        2041068, 10,
        2041069, 10,
        2043022, 12,
        2043120, 12,
        2043220, 12,
        2043313, 12,
        2043713, 12,
        2043813, 12,
        2044028, 12,
        2044120, 12,
        2044220, 12,
        2044320, 12,
        2044520, 12,
        2044513, 12,
        2044613, 12,
        2044713, 12,
        2044817, 12,
        2044910, 12,
        2046002, 5,
        2046003, 5,
        2046102, 5,
        2046103, 5,
        2046204, 10,
        2046205, 10,
        2046206, 10,
        2046207, 10,
        2046304, 10,
        2046305, 10,
        2046306, 10,
        2046307, 10,
        2040006, 2,
        2040007, 2,
        2040303, 2,
        2040403, 2,
        2040506, 2,
        2040507, 2,
        2040603, 2,
        2040709, 2,
        2040710, 2,
        2040711, 2,
        2040806, 2,
        2040903, 2,
        2040913, 2,
        2041024, 2,
        2041025, 2,
        2044815, 2,
        2044908, 2,
        1152046, 1,
        1152047, 1,
        1152048, 1,
        1152049, 1,
        1122091, 1,
        1122095, 1,
        1122099, 1,
        1122103, 1,
        1012214, 1,
        1012220, 1,
        1012226, 1,
        1012232, 1,
        1012238, 1,
        1032088, 1,
        1032089, 1,
        1032090, 1,
        1032091, 1,
        1132053, 1,
        1132063, 1,
        1132073, 1,
        1132083, 1,
        1112586, 1,
        1112593, 1,
        1112597, 1,
        1662002, 1,
        1662003, 1,
        1672003, 1,
        1672004, 1,
        1672005, 1,
        //130, 140 weapons
        1092088, 1,
        1092089, 1,
        1092087, 1,
        1102275, 1,
        1102276, 1,
        1102277, 1,
        1102278, 1,
        1102279, 1,
        1102280, 1,
        1102281, 1,
        1102282, 1,
        1102283, 1,
        1102284, 1,
        1082295, 1,
        1082296, 1,
        1082297, 1,
        1082298, 1,
        1082299, 1,
        1082300, 1,
        1082301, 1,
        1082302, 1,
        1082303, 1,
        1082304, 1,
        1072485, 1,
        1072486, 1,
        1072487, 1,
        1072488, 1,
        1072489, 1,
        1072490, 1,
        1072491, 1,
        1072492, 1,
        1072493, 1,
        1072494, 1,
        1052314, 1,
        1052315, 1,
        1052316, 1,
        1052317, 1,
        1052318, 1,
        1052319, 1,
        1052329, 1,
        1052321, 1,
        1052322, 1,
        1052323, 1,
        1003172, 1,
        1003173, 1,
        1003174, 1,
        1003175, 1,
        1003176, 1,
        1003177, 1,
        1003178, 1,
        1003179, 1,
        1003180, 1,
        1003181, 1,
        1302152, 1,
        1302153, 1,
        1312065, 1,
        1312066, 1,
        1322096, 1,
        1322097, 1,
        1332130, 1,
        1332131, 1,
        1342035, 1,
        1342036, 1,
        1372084, 1,
        1372085, 1,
        1382104, 1,
        1382105, 1,
        1402095, 1,
        1402096, 1,
        1412065, 1,
        1412066, 1,
        1422066, 1,
        1422067, 1,
        1432086, 1,
        1432087, 1,
        1442116, 1,
        1442117, 1,
        1452111, 1,
        1452112, 1,
        1462099, 1,
        1462100, 1,
        1472122, 1,
        1472123, 1,
        1482084, 1,
        1482085, 1,
        1492085, 1,
        1492086, 1,
        1532017, 1,
        1532018, 1,
        //mounts
        2430291, 1,
        2430293, 1,
        2430295, 1,
        2430297, 1,
        2430299, 1,
        2430301, 1,
        2430303, 1,
        2430305, 1,
        2430307, 1,
        2430309, 1,
        2430311, 1,
        2430313, 1,
        2430315, 1,
        2430317, 1,
        2430319, 1,
        2430321, 1,
        2430323, 1,
        2430325, 1,
        2430327, 1,
        2430329, 1,
        2430331, 1,
        2430333, 1,
        2430335, 1,
        2430337, 1,
        2430339, 1,
        2430341, 1,
        2430343, 1,
        2430345, 1,
        2430348, 1,
        2430350, 1,
        2430352, 1,
        2430354, 1,
        2430356, 1,
        2430358, 1,
        2430360, 1,
        2430362, 1,
        //rising sun
        1012239, 1,
        1122104, 1,
        1112584, 1,
        1032093, 1,
        1132085, 1
    };
    public static int[] tenPercent = {
        //10% scrolls
        2040002,
        2040005,
        2040026,
        2040031,
        2040100,
        2040105,
        2040200,
        2040205,
        2040302,
        2040310,
        2040318,
        2040323,
        2040328,
        2040329,
        2040330,
        2040331,
        2040402,
        2040412,
        2040419,
        2040422,
        2040427,
        2040502,
        2040505,
        2040514,
        2040517,
        2040534,
        2040602,
        2040612,
        2040619,
        2040622,
        2040627,
        2040702,
        2040705,
        2040708,
        2040727,
        2040802,
        2040805,
        2040816,
        2040825,
        2040902,
        2040915,
        2040920,
        2040925,
        2040928,
        2040933,
        2041002,
        2041005,
        2041008,
        2041011,
        2041014,
        2041017,
        2041020,
        2041023,
        2041058,
        2041102,
        2041105,
        2041108,
        2041111,
        2041302,
        2041305,
        2041308,
        2041311,
        2043002,
        2043008,
        2043019,
        2043102,
        2043114,
        2043202,
        2043214,
        2043302,
        2043402,
        2043702,
        2043802,
        2044002,
        2044014,
        2044015,
        2044102,
        2044114,
        2044202,
        2044214,
        2044302,
        2044314,
        2044402,
        2044414,
        2044502,
        2044602,
        2044702,
        2044802,
        2044809,
        2044902,
        2045302,
        2048002,
        2048005
    };
    public static int[] fishingReward = {
        0, 100, // Meso
        1, 100, // EXP
        2022179, 1, // Onyx Apple
        1302021, 5, // Pico Pico Hammer
        1072238, 1, // Voilet Snowshoe
        1072239, 1, // Yellow Snowshoe
        2049100, 2, // Chaos Scroll
        2430144, 1,
        2290285, 1,
        2028062, 1,
        2028061, 1,
        2049301, 1, // Equip Enhancer Scroll
        2049401, 1, // Potential Scroll
        1302000, 3, // Sword
        1442011, 1, // Surfboard
        4000517, 8, // Golden Fish
        4000518, 10, // Golden Fish Egg
        4031627, 2, // White Bait (3cm)
        4031628, 1, // Sailfish (120cm)
        4031630, 1, // Carp (30cm)
        4031631, 1, // Salmon(150cm)
        4031632, 1, // Shovel
        4031633, 2, // Whitebait (3.6cm)
        4031634, 1, // Whitebait (5cm)
        4031635, 1, // Whitebait (6.5cm)
        4031636, 1, // Whitebait (10cm)
        4031637, 2, // Carp (53cm)
        4031638, 2, // Carp (60cm)
        4031639, 1, // Carp (100cm)
        4031640, 1, // Carp (113cm)
        4031641, 2, // Sailfish (128cm)
        4031642, 2, // Sailfish (131cm)
        4031643, 1, // Sailfish (140cm)
        4031644, 1, // Sailfish (148cm)
        4031645, 2, // Salmon (166cm)
        4031646, 2, // Salmon (183cm)
        4031647, 1, // Salmon (227cm)
        4031648, 1, // Salmon (288cm)
        4001187, 20,
        4001188, 20,
        4001189, 20,
        4031629, 1 // Pot
    };

    public static boolean isReverseItem(int itemId) {
        switch (itemId) {
            case 1002790:
            case 1002791:
            case 1002792:
            case 1002793:
            case 1002794:
            case 1082239:
            case 1082240:
            case 1082241:
            case 1082242:
            case 1082243:
            case 1052160:
            case 1052161:
            case 1052162:
            case 1052163:
            case 1052164:
            case 1072361:
            case 1072362:
            case 1072363:
            case 1072364:
            case 1072365:

            case 1302086:
            case 1312038:
            case 1322061:
            case 1332075:
            case 1332076:
            case 1372045:
            case 1382059:
            case 1402047:
            case 1412034:
            case 1422038:
            case 1432049:
            case 1442067:
            case 1452059:
            case 1462051:
            case 1472071:
            case 1482024:
            case 1492025:

            case 1342012:
            case 1942002:
            case 1952002:
            case 1962002:
            case 1972002:
            case 1532016:
            case 1522017:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTimelessItem(int itemId) {
        switch (itemId) {
            case 1032031: //shield earring, but technically
            case 1102172:
            case 1002776:
            case 1002777:
            case 1002778:
            case 1002779:
            case 1002780:
            case 1082234:
            case 1082235:
            case 1082236:
            case 1082237:
            case 1082238:
            case 1052155:
            case 1052156:
            case 1052157:
            case 1052158:
            case 1052159:
            case 1072355:
            case 1072356:
            case 1072357:
            case 1072358:
            case 1072359:
            case 1092057:
            case 1092058:
            case 1092059:

            case 1122011:
            case 1122012:

            case 1302081:
            case 1312037:
            case 1322060:
            case 1332073:
            case 1332074:
            case 1372044:
            case 1382057:
            case 1402046:
            case 1412033:
            case 1422037:
            case 1432047:
            case 1442063:
            case 1452057:
            case 1462050:
            case 1472068:
            case 1482023:
            case 1492023:
            case 1342011:
            case 1532015:
            case 1522016:
                //raven.
                return true;
            default:
                return false;
        }
    }

    public static boolean isRing(int itemId) {
        return itemId >= 1112000 && itemId < 1113000;
    }// 112xxxx - pendants, 113xxxx - belts

    //if only there was a way to find in wz files -.-
    public static boolean isEffectRing(int itemid) {
        return isFriendshipRing(itemid) || isCrushRing(itemid) || isMarriageRing(itemid);
    }

    public static boolean isMarriageRing(int itemId) {
        switch (itemId) {
            case 1112803:
            case 1112806:
            case 1112807:
            case 1112809:
                return true;
        }
        return false;
    }

    public static boolean isFriendshipRing(int itemId) {
        switch (itemId) {
            case 1112800:
            case 1112801:
            case 1112802:
            case 1112810: //new
            case 1112811: //new, doesnt work in friendship?
            case 1112812: //new, im ASSUMING it's friendship cuz of itemID, not sure.
            case 1112816: //new, i'm also assuming
            case 1112817:

            case 1049000:
                return true;
        }
        return false;
    }

    public static boolean isCrushRing(int itemId) {
        switch (itemId) {
            case 1112001:
            case 1112002:
            case 1112003:
            case 1112005: //new
            case 1112006: //new
            case 1112007:
            case 1112012:
            case 1112015: //new

            case 1048000:
            case 1048001:
            case 1048002:
                return true;
        }
        return false;
    }

    public static int[] Equipments_Bonus = {1122334};

    public static int Equipment_Bonus_EXP(final int itemid) {
        switch (itemid) {
            case 1122334:
                return 15;
        }
        return 0;
    }

    public static int[] blockedMaps = {180000001, 180000002, 109050000, 280030000, 240060200, 280090000, 280030001, 240060201, 950101100, 950101010, 910340500, 220080001, 551030200, 610030600};
    //If you can think of more maps that could be exploitable via npc,block nao pliz!

    public static int getExpForLevel(int i, int itemId) {
        if (getMaxLevel(itemId) > 0) {
            return getOtherRequiredEXP(i);
        } else if (isWeapon(itemId)) {
            return (int) (70.0d * Math.pow(Math.E, 0.1 * i));
        }
        return 0;
    }

    public static int getMaxLevel(final int itemId) {
        Map<Integer, Map<String, Integer>> inc = MapleItemInformationProvider.getInstance().getEquipIncrements(itemId);
        return inc != null ? (inc.size()) : 0;
    }

    public static int getStatChance() {
        return 25;
    }

    public static MonsterTemporaryStat getStatFromWeapon(final int itemid) {
        switch (itemid) {
            case 1302109:
            case 1312041:
            case 1322067:
            case 1332083:
            case 1372048:
            case 1382064:
            case 1402055:
            case 1412037:
            case 1422041:
            case 1432052:
            case 1442073:
            case 1452064:
            case 1462058:
            case 1472079:
            case 1482035:
                return MonsterTemporaryStat.Blind;
            case 1302108:
            case 1312040:
            case 1322066:
            case 1332082:
            case 1372047:
            case 1382063:
            case 1402054:
            case 1412036:
            case 1422040:
            case 1432051:
            case 1442072:
            case 1452063:
            case 1462057:
            case 1472078:
            case 1482036:
                return MonsterTemporaryStat.Speed;
        }
        return null;
    }

    public static int getXForStat(MonsterTemporaryStat stat) {
        switch (stat) {
            case Blind:
                return -70;
            case Speed:
                return -50;
        }
        return 0;
    }

    public static int getSkillForStat(MonsterTemporaryStat stat) {
        switch (stat) {
            case Blind:
                return 1111003;
            case Speed:
                return 3121007;
        }
        return 0;
    }

    public final static int[] normalDrops = {
        4001009, //real
        4001010,
        4001011,
        4001012,
        4001013,
        4001014, //real
        4001021,
        4001038, //fake
        4001039,
        4001040,
        4001041,
        4001042,
        4001043, //fake
        4001038, //fake
        4001039,
        4001040,
        4001041,
        4001042,
        4001043, //fake
        4001038, //fake
        4001039,
        4001040,
        4001041,
        4001042,
        4001043, //fake
        4000164, //start
        2000000,
        2000003,
        2000004,
        2000005,
        4000019,
        4000000,
        4000016,
        4000006,
        2100121,
        4000029,
        4000064,
        5110000,
        4000306,
        4032181,
        4006001,
        4006000,
        2050004,
        3994102,
        3994103,
        3994104,
        3994105,
        2430007, //end
        4000164, //start
        2000000,
        2000003,
        2000004,
        2000005,
        4000019,
        4000000,
        4000016,
        4000006,
        2100121,
        4000029,
        4000064,
        5110000,
        4000306,
        4032181,
        4006001,
        4006000,
        2050004,
        3994102,
        3994103,
        3994104,
        3994105,
        2430007, //end
        4000164, //start
        2000000,
        2000003,
        2000004,
        2000005,
        4000019,
        4000000,
        4000016,
        4000006,
        2100121,
        4000029,
        4000064,
        5110000,
        4000306,
        4032181,
        4006001,
        4006000,
        2050004,
        3994102,
        3994103,
        3994104,
        3994105,
        2430007}; //end
    public final static int[] rareDrops = {
        2022179,
        2049100,
        2049100,
        2430144,
        2028062,
        2028061,
        2290285,
        2049301,
        2049401,
        2022326,
        2022193,
        2049000,
        2049001,
        2049002};
    public final static int[] superDrops = {
        2040804,
        2049400,
        2028062,
        2028061,
        2430144,
        2430144,
        2430144,
        2430144,
        2290285,
        2049100,
        2049100,
        2049100,
        2049100};

    public static int getSkillBook(final int job) {
        if (job >= 2210 && job <= 2218) {
            return job - 2209;
        }
        switch (job) {
            case 570:
            case 2310:
            case 2410:
            case 3110:
            case 3120:
            case 3210:
            case 3310:
            case 3510:
            case 5110:
            case 2710:
            case 6110:
            case 6210:
            case 6510:
            case 4210:
            case 3610:
            case 12100:
                return 1;
            case 571:
            case 2311:
            case 2411:
            case 3111:
            case 3211:
            case 3121:
            case 3311:
            case 3511:
            case 5111:
            case 2711:
            case 6111:
            case 6211:
            case 6511:
            case 4211:
            case 3611:
                return 2;
            case 572:
            case 2312:
            case 2412:
            case 3112:
            case 3122:
            case 3212:
            case 3312:
            case 3512:
            case 5112:
            case 2712:
            case 6112:
            case 6212:
            case 6512:
            case 4212:
            case 3612:
                return 3;
        }
        return 0;
    }

    public static int getSkillBook(final int job, final int level) {
        if (job >= 2210 && job <= 2218) {
            return job - 2209;
        }
        switch (job) {
            case 508:
            case 570:
            case 571:
            case 572:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
            case 2400:
            case 2410:
            case 2411:
            case 2412:
            case 2700:
            case 2710:
            case 2711:
            case 2712:
            case 3100:
            case 3200:
            case 3300:
            case 3500:
            case 3110:
            case 3210:
            case 3310:
            case 3510:
            case 3111:
            case 3211:
            case 3311:
            case 3511:
            case 3112:
            case 3212:
            case 3312:
            case 3512:
            case 3600:
            case 3610:
            case 3611:
            case 3612:
            case 5100:
            case 5110:
            case 5111:
            case 5112:
            case 6500:
            case 6510:
            case 6511:
            case 6512:
            case 6100:
            case 6110:
            case 6111:
            case 6112:
            case 3120:
            case 3121:
            case 3122:
            case 3101:
            case 12100:
                return (level < 31 ? 0 : (level > 31 && level < 71 ? 1 : (level > 70 && level < 121 ? 2 : (level > 120 ? 3 : 0))));
        }
        return 0;
    }

    public static boolean isSeparatedSp(int job) {
        // return isAdventurer(job) || isZero(job) || isKOC(job) || isEvan(job)
        // || isResist(job) || isMercedes(job) || isJett(job) || isPhantom(job)
        // || isMihile(job) || isNova(job) || isAngelicBuster(job) ||
        // isKaiser(job) || isLuminous(job) || isHayato(job) || isKanna(job) ||
        // isDemonAvenger(job);
        if (isKOC(job)) {
            if (getTrueJobGrade(job) == 2 || getTrueJobGrade(job) == 4) {
                return false;
            }
        }
        if (isAran(job)) {
            return false;
        }
        return true;
    }

    public static int getTrueJobGrade(int job) {
        int result;
        int jobGrade = job % 1000 / 100;
        if (job / 100 == 27) {
            jobGrade = 2;
        }
        result = 4;
        if (job / 100 != 36) {
            result = jobGrade;
        }
        return result;
    }

    public static int getSkillBookForSkill(final int skillid) {
        return getSkillBook(skillid / 10000);
    }

    public static int getLinkedMountItem(final int sourceid) {
        switch (sourceid % 1000) {
            case 1:
            case 24:
            case 25:
                return 1018;
            case 2:
            case 26:
                return 1019;
            case 3:
                return 1025;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return (sourceid % 1000) + 1023;
            case 9:
            case 10:
            case 11:
                return (sourceid % 1000) + 1024;
            case 12:
                return 1042;
            case 13:
                return 1044;
            case 14:
                return 1049;
            case 15:
            case 16:
            case 17:
                return (sourceid % 1000) + 1036;
            case 18:
            case 19:
                return (sourceid % 1000) + 1045;
            case 20:
                return 1072;
            case 21:
                return 1084;
            case 22:
                return 1089;
            case 23:
                return 1106;
            case 29:
                return 1151;
            case 30:
            case 50:
                return 1054;
            case 31:
            case 51:
                return 1069;
            case 32:
                return 1138;
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
                return (sourceid % 1000) + 1009;
            case 52:
                return 1070;
            case 53:
                return 1071;
            case 54:
                return 1096;
            case 55:
                return 1101;
            case 56:
                return 1102;
            case 58:
                return 1118;
            case 59:
                return 1121;
            case 60:
                return 1122;
            case 61:
                return 1129;
            case 62:
                return 1139;
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
                return (sourceid % 1000) + 1080;
            case 85:
            case 86:
            case 87:
                return (sourceid % 1000) + 928;
            case 88:
                return 1065;

            case 27:
                return 1932049; //airplane
            case 28:
                return 1932050; //airplane
            case 114:
                return 1932099; //bunny buddy
            //33 = hot air
            //37 = bjorn
            //38 = speedy chariot
            //57 = law officer
            //they all have in wz so its ok
        }
        return 0;
    }

    public static int getMountItem(final int sourceid, final MapleCharacter chr) {
        switch (sourceid) {

            /* 실피디아 */
            case 20021160:
            case 20021161: {
                return (sourceid == 20021160 ? 1932086 : 1932087);
            }
            case 33001001: //temp.
                if (chr == null) {
                    return 1932015;
                }
                switch (chr.getIntNoRecord(JAGUAR)) {
                    case 20:
                        return 1932030;
                    case 30:
                        return 1932031;
                    case 40:
                        return 1932032;
                    case 50:
                        return 1932033;
                    case 60:
                        return 1932036;
                    case 70:
                        return 1932100;
                    case 80:
                        return 1932149;
                    case 90:
                        return 1932215;
                }
                return 1932015;
            case 35001002:
            case 35120000:
                return 1932016; // 1932016
        }
        if (!isBeginnerJob(sourceid / 10000)) {
            if (sourceid / 10000 == 8000) { //todoo clean up
                final Skill skil = SkillFactory.getSkill(sourceid);
                if (skil != null && skil.getTamingMob() > 0) {
                    return skil.getTamingMob();
                } else {
                    final int link = getLinkedMountItem(sourceid);
                    if (link > 0) {
                        if (link < 10000) {
                            return getMountItem(link, chr);
                        } else {
                            return link;
                        }
                    }
                }
            }
            return 0;
        }
        switch (sourceid % 10000) {
            case 1013:
            case 1046:
                return 1932001;
            case 1015:
            case 1048:
                return 1932002;
            case 1016:
            case 1017:
            case 1027:
                return 1932007;
            case 1018:
                return 1932003;
            case 1019:
                return 1932005;
            case 1025:
                return 1932006;
            case 1028:
                return 1932008;
            case 1029:
                return 1932009;
            case 1030:
                return 1932011;
            case 1031:
                return 1932010;
            case 1033:
                return 1932013;
            case 1034:
                return 1932014;
            case 1035:
                return 1932012;
            case 1036:
                return 1932017;
            case 1037:
                return 1932018;
            case 1038:
                return 1932019;
            case 1039:
                return 1932020;
            case 1040:
                return 1932021;
            case 1042:
                return 1932022;
            case 1044:
                return 1932023;
            //case 1045:
            //return 1932030; //wth? helicopter? i didnt see one, so we use hog
            case 1049:
                return 1932025;
            case 1050:
                return 1932004;
            case 1051:
                return 1932026;
            case 1052:
                return 1932027;
            case 1053:
                return 1932028;
            case 1054:
                return 1932029;
            case 1063:
                return 1932034;
            case 1064:
                return 1932035;
            case 1065:
                return 1932037;
            case 1069:
                return 1932038;
            case 1070:
                return 1932039;
            case 1071:
                return 1932040;
            case 1072:
                return 1932041;
            case 1084:
                return 1932043;
            case 1089:
                return 1932044;
            case 1096:
                return 1932045;
            case 1101:
                return 1932046;
            case 1102:
                return 1932061;
            case 1106:
                return 1932048;
            case 1118:
                return 1932060;
            case 1115:
                return 1932052;
            case 1121:
                return 1932063;
            case 1122:
                return 1932064;
            case 1123:
                return 1932065;
            case 1128:
                return 1932066;
            case 1130:
                return 1932072;
            case 1136:
                return 1932078;
            case 1138:
                return 1932080;
            case 1139:
                return 1932081;
            //FLYING
            case 1143:
            case 1144:
            case 1145:
            case 1146:
            case 1147:
            case 1148:
            case 1149:
            case 1150:
            case 1151:
            case 1152:
            case 1153:
            case 1154:
            case 1155:
            case 1156:
            case 1157:
                return 1992000 + (sourceid % 10000) - 1143;
            default:
                return 0;
        }
    }

    public static boolean isKatara(int itemId) {
        return itemId / 10000 == 134;
    }

    public static boolean isDagger(int itemId) {
        return itemId / 10000 == 133;
    }

    public static boolean isApplicableSkill(int skil) {
        return (skil > 149999999 && skil < 154129999) || (skil < 70000000 && (skil % 10000 < 8000 || skil % 10000 > 8006) && !isAngel(skil) || skil >= 92000000 || skil >= 80000000 && skil < 80010000);
    }

    public static boolean isApplicableSkill_(int skil) { //not applicable to saving but is more of temporary
        for (int i : PlayerStats.pvpSkills) {
            if (skil == i) {
                return true;
            }
        }
        return (skil > 149999999 && skil < 154129999) || (skil >= 90000000 && skil < 92000000 || skil % 10000 >= 8000 && skil % 10000 <= 8003 || isAngel(skil));
    }

    public static boolean isTablet(int itemId) {
        return itemId >= 2047000 && itemId <= 2047309; //itemId / 1000 == 2047;
    }

    public static boolean isGeneralScroll(int itemId) {
        return itemId / 1000 == 2046;
    }

    public static int getSuccessTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) { //2047_2_00 = armor, 2047_3_00 = accessory
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 55;
                case 2:
                    return 43;
                case 3:
                    return 33;
                case 4:
                    return 26;
                case 5:
                    return 20;
                case 6:
                    return 16;
                case 7:
                    return 12;
                case 8:
                    return 10;
                default:
                    return 7;
            }
        } else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 35;
                case 2:
                    return 18;
                case 3:
                    return 12;
                default:
                    return 7;
            }
        } else {
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 50; //-20
                case 2:
                    return 36; //-14
                case 3:
                    return 26; //-10
                case 4:
                    return 19; //-7
                case 5:
                    return 14; //-5
                case 6:
                    return 10; //-4
                default:
                    return 7;  //-3
            }
        }
    }

    public static int getCurseTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) { //2047_2_00 = armor, 2047_3_00 = accessory
            switch (level) {
                case 0:
                    return 10;
                case 1:
                    return 12;
                case 2:
                    return 16;
                case 3:
                    return 20;
                case 4:
                    return 26;
                case 5:
                    return 33;
                case 6:
                    return 43;
                case 7:
                    return 55;
                case 8:
                    return 70;
                default:
                    return 100;
            }
        } else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0:
                    return 12;
                case 1:
                    return 18;
                case 2:
                    return 35;
                case 3:
                    return 70;
                default:
                    return 100;
            }
        } else {
            switch (level) {
                case 0:
                    return 10;
                case 1:
                    return 14; //+4
                case 2:
                    return 19; //+5
                case 3:
                    return 26; //+7
                case 4:
                    return 36; //+10
                case 5:
                    return 50; //+14
                case 6:
                    return 70; //+20
                default:
                    return 100;  //+30
            }
        }
    }

    public static int getSuccessRate(final int itemId) {
        switch (itemId) {
            case 2048305:
                return 70;
            case 2048306:
            case 2048307:
                return 100;
            case 2048308:
                return 50;
            case 2048309:
            case 2048310:
                return 60;
            case 2048311:
                return 50;
        }
        return 0;
    }

    public static int getOptionType(final int itemId) {
        int id = itemId / 10000;
        if (isWeapon(itemId) || ((int) (itemId / 1000)) == 1099 || (itemId > 1093199 && itemId < 194999)) {
            return 10; //무기
        } else if (id == 109 || id == 110 || id == 113) {
            return 20; //방패 & 망토 & 벨트
        } else if (isAccessory(itemId)) {
            return 40; //악세사리
        } else if (id == 100) {
            return 51; //투구
        } else if (id == 104 || id == 106) {
            return 52; //상의, 한벌옷
        } else if (id == 105) {
            return 53; //하의
        } else if (id == 108) {
            return 54; //장갑
        } else if (id == 107) {
            return 55;
        }
        return 0;
    }

    public static boolean isAccessory(final int itemId) {
        return (itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1153000) || (itemId >= 1112000 && itemId < 1113000);
    }

    public static boolean potentialIDFits(final int potentialID, final int newstate, final int i) {
        if (potentialID > 59999 || potentialID < 10000) {
            return false;
        }
        int lowerBound = (newstate - 16) * 10000;
        int upperBound = lowerBound + 9999;
        int secondLowerBound = lowerBound - 10000;
        if (i == 0 && potentialID <= upperBound && potentialID >= lowerBound) {
            return true;
        } else if (i > 0 && potentialID <= upperBound && potentialID >= secondLowerBound) {
            return true;
        }
        return false;
    }

    public static boolean optionTypeFits(final int optionType, final int itemId) {
        if (itemId / 10000 == 135 && optionType == 10) {
            return true;
        }
        if (itemId / 10000 == 109 && optionType == 10) {
            return true;
        }
        switch (optionType) {
            case 10: // weapons
                return isWeapon(itemId);
            case 11: // all equipment except weapons
                return !isWeapon(itemId);
            case 20: // all armors
                return !isAccessory(itemId) && !isWeapon(itemId);
            case 40: // accessories
                return isAccessory(itemId);
            case 51: // hat
                return itemId / 10000 == 100;
            case 52: // top and overall
                return itemId / 10000 == 104 || itemId / 10000 == 105;
            case 53: // bottom and overall
                return itemId / 10000 == 106 || itemId / 10000 == 105;
            case 54: // glove
                return itemId / 10000 == 108;
            case 55: // shoe
                return itemId / 10000 == 107;
            default:
                return true;
        }
    }

    public static final boolean isMountItemAvailable(final int mountid, final int jobid) {
        if (jobid != 900 && mountid / 10000 == 190) {
            switch (mountid) {
                case 1902000:
                case 1902001:
                case 1902002:
                    return isAdventurer(jobid);
                case 1902005:
                case 1902006:
                case 1902007:
                    return isKOC(jobid);
                case 1902015:
                case 1902016:
                case 1902017:
                case 1902018:
                    return isAran(jobid);
                case 1902040:
                case 1902041:
                case 1902042:
                    return isEvan(jobid);
            }

            if (isResist(jobid)) {
                return false; //none lolol
            }
        }
        if (mountid / 10000 != 190) {
            return false;
        }
        return true;
    }

    public static boolean isMechanicItem(final int itemId) {
        return itemId >= 1610000 && itemId < 1660000;
    }

    public static boolean isEvanDragonItem(final int itemId) {
        return itemId >= 1940000 && itemId < 1980000; //194 = mask, 195 = pendant, 196 = wings, 197 = tail
    }

    public static boolean canScroll(final int itemId) {
        if (itemId <= 1672999 && itemId >= 1672000) {
            return true; //Android hearts
        }
        return itemId / 100000 != 19 && itemId / 100000 != 16; //no mech/taming/dragon
    }

    public static boolean canHammer(final int itemId) {
        switch (itemId) {
            case 1122000:
            case 1122076: //ht, chaos ht
                return false;
        }
        if (!canScroll(itemId)) {
            return false;
        }
        return true;
    }

    public static int[] owlItems = new int[]{
        1082002, // work gloves
        2070005,
        2070006,
        1022047,
        1102041,
        2044705,
        2340000, // white scroll
        2040017,
        1092030,
        2040804};

    public static int getMasterySkill(final int job) {
        if (job >= 1410 && job <= 1412) {
            return 14100000;
        } else if (job >= 410 && job <= 412) {
            return 4100000;
        } else if (job >= 520 && job <= 522) {
            return 5200000;
        }
        return 0;
    }

    public static int getExpRate_Below10(final int job) {
        if (GameConstants.isEvan(job)) {
            return 1;
        } else if (GameConstants.isAran(job) || GameConstants.isKOC(job) || GameConstants.isResist(job)) {
            return 5;
        }
        return 10;
    }

    public static int getExpRate_Quest() {
        return ChannelServer.getQuestRate();
    }

    public static int getCustomReactItem(final int rid, final int original) {
        if (rid == 2008006) { //orbis pq LOL
            return (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 4001055);
            //4001056 = sunday. 4001062 = saturday
        } else {
            return original;
        }
    }

    public static int getJobNumber(int jobz) {
        int job = (jobz % 1000);
        if (jobz > 999) {
            job = (jobz % 10000);
        }
        if (job / 100 == 0 || isBeginnerJob(jobz)) {
            return 0; //beginner
        } else if ((job / 10) % 10 == 0 || job == 501) {
            return 1;
        } else {
            return 2 + (job % 10);
        }
    }

    public static boolean isBeginnerJob(final int job) {
        return job == 0 || job == 1 || job == 1000 || job == 2000 || job == 2001 || job == 3000 || job == 3001
                || job == 2002 || job == 2003 || job == 5000 || job == 2004 || job == 4001 || job == 4002 || job == 6000
                || job == 6001 || job == 6002 || job == 3002 || job == 4000 || job == 5001;
    }

    public static boolean isForceRespawn(int mapid) { //엘린숲 추가해야될거같은데 너무느려~~
        switch (mapid) {
            case 103000800: //kerning PQ crocs
            case 925100100: //crocs and stuff
                return true;
            default:
                return mapid / 100000 == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100);
        }
    }

    public static int getFishingTime(boolean vip, boolean gm) {
        return gm ? 1000 : (vip ? 30000 : 60000);
    }

    public static boolean canForfeit(int questid) {
        switch (questid) {
            case 20010:
                return false;
            default:
                return true;
        }
    }

    public static double getAttackRange(MapleStatEffect def, int rangeInc) {
        double defRange = ((400.0 + rangeInc) * (400.0 + rangeInc));
        if (def != null) {
            defRange += def.getMaxDistanceSq() + (def.getRange() * def.getRange());
        }
        //rangeInc adds to X
        //400 is approximate, screen is 600.. may be too much
        //200 for y is also too much
        //default 200000
        return defRange + 120000.0;
    }

    public static double getAttackRange(Point lt, Point rb) {
        double defRange = (400.0 * 400.0);
        final int maxX = Math.max(Math.abs(lt == null ? 0 : lt.x), Math.abs(rb == null ? 0 : rb.x));
        final int maxY = Math.max(Math.abs(lt == null ? 0 : lt.y), Math.abs(rb == null ? 0 : rb.y));
        defRange += (maxX * maxX) + (maxY * maxY);
        //rangeInc adds to X
        //400 is approximate, screen is 600.. may be too much
        //200 for y is also too much
        //default 200000
        return defRange + 120000.0;
    }

    public static boolean isNoDelaySkill(int skillId) {
        switch (skillId) {
            case 65111007:
            case 65111100:
            case 5110001:
            case 21101003:
            case 15100004:
            case 33101004:
            case 32111010:
            case 2111007:
            case 2211007:
            case 2311007:
            case 31121005:
            case 32121003:
            case 35121005:
            case 35111004:
            case 35121013:
            case 35121003:
            case 22150004:
            case 22181004:
            case 11101002:
            case 23100006:
            case 23120012:
            case 33100009:
            case 51120002:
            case 21100010:
            case 21120012:
            case 1300002:
            case 1120013:
            case 1100002:
            case 51100002:

            case 3120008:
            case 3100001:
            case 1200002:
            case 13101002:
            case 24121000:
            case 22161005:
            case 2121055://메기도플레임더미
            case 2121054://파이어오라
            //case 2301002://힐
            case 4221052://베일 오브 섀도우
            case 36110004:
            case 27121201:
            case 27120211:
            case 61110008:
            case 62100007:
            case 65121007:
            case 61000001:
            case 15100027:
                return true;
        }
        return false;
    }

    public static boolean isNoSpawn(int mapID) {
        return mapID == 809040100 || mapID == 925020010 || mapID == 925020011 || mapID == 925020012 || mapID == 925020013 || mapID == 925020014 || mapID == 980010000 || mapID == 980010100 || mapID == 980010200 || mapID == 980010300 || mapID == 980010020;
    }

    public static int getExpRate(int job, int def) {
        return def;
    }

    public static int getModifier(int itemId, int up) {
        if (up <= 0) {
            return 0;
        }
        switch (itemId) {
            case 2022459:
            case 2860179:
            case 2860193:
            case 2860207:
                return 130;
            case 2022460:
            case 2022462:
            case 2022730:
                return 150;
            case 2860181:
            case 2860195:
            case 2860209:
                return 200;
        }
        return 200;
    }

    public static short getSlotMax(int itemId) {
        switch (itemId) {
            case 4030003:
            case 4030004:
            case 4030005:
                return 1;
            case 4001168:
            case 4031306:
            case 4031307:
            case 3993000:
            case 3993002:
            case 3993003:
                return 100;
            case 5220010:
            case 5220013:
                return 1000;
            case 5220020:
                return 2000;
        }
        return 0;
    }

    public static short getStat(int itemId, int def) {
        return (short) def;
    }

    public static short getHpMp(int itemId, int def) {
        return (short) def;
    }

    public static short getATK(int itemId, int def) {
        return (short) def;
    }

    public static short getDEF(int itemId, int def) {
        return (short) def;
    }

    public static boolean isDojo(int mapId) {
        return mapId >= 925020100 && mapId <= 925023814;
    }

    //Looks like you can use this to force the HP of any mob.  Fun!
    public static int getPartyPlayHP(int mobID) {
        switch (mobID) {
            case 4250000:
                return 836000;
            case 4250001:
                return 924000;
            case 5250000:
                return 1100000;
            case 5250001:
                return 1276000;
            case 5250002:
                return 1452000;

            case 9400661:
                return 15000000;
            case 9400660:
                return 30000000;
            case 9400659:
                return 45000000;
            case 9400658:
                return 20000000;
            default: {
                // 몬스터 습격 체력 조정
                for (int i = 0; i < AramiaFireWorks.arrayMob.length; i++) {
                    if (mobID == AramiaFireWorks.arrayMob[i]) {
                        return 999999;
                    }
                }
                break;
            }
        }
        //LHC mobs, doesn't include quest mobs TODO: Fix this up, causes overflows when used?
        /*if(mobID >= 8210000 && mobID <= 8210005){
         //TODO: Just make this return a long, since mob HP is already a long anyway.
         double newhp = (MapleLifeFactory.getMonsterStats(mobID).getHp() * GameConstants.mobHPMultiplier * 2);
         return (int)(newhp > Integer.MAX_VALUE ? Integer.MAX_VALUE : newhp); //MORE HEALTH!  MORE!
         }*/
        return 0;
    }

    public static int getPartyPlayEXP(int mobID) {
        switch (mobID) {
            case 4250000:
                return 5770;
            case 4250001:
                return 6160;
            case 5250000:
                return 7100;
            case 5250001:
                return 7975;
            case 5250002:
                return 8800;

            case 9400661:
                return 40000;
            case 9400660:
                return 70000;
            case 9400659:
                return 90000;
            case 9400658:
                return 50000;
        }
        return 0;
    }

    public static int getPartyPlay(int mapId) {
        switch (mapId) {
            case 300010000:
            case 300010100:
            case 300010200:
            case 300010300:
            case 300010400:
            case 300020000:
            case 300020100:
            case 300020200:
            case 300030000:

            case 683070400:
            case 683070401:
            case 683070402:
                return 25;
        }
        return 0;
    }

    public static int getPartyPlay(int mapId, int def) {
        int dd = getPartyPlay(mapId);
        if (dd > 0) {
            return dd;
        }
        return def / 2;
    }

    public static boolean isHyperTeleMap(int mapId) {
        for (int i : hyperTele) {
            if (i == mapId) {
                return true;
            }
        }
        return false;
    }

    public static int getCurrentDate() {
        final String time = FileoutputUtil.CurrentReadable_Time();
        return Integer.parseInt(new StringBuilder(time.substring(0, 4)).append(time.substring(5, 7)).append(time.substring(8, 10)).append(time.substring(11, 13)).toString());
    }

    public static int getCurrentDate_NoTime() {
        final String time = FileoutputUtil.CurrentReadable_Time();
        return Integer.parseInt(new StringBuilder(time.substring(0, 4)).append(time.substring(5, 7)).append(time.substring(8, 10)).toString());
    }

    public static String resolvePotentialID(final int itemID, final int potID) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int eqLevel = ii.getReqLevel(itemID); //TODO: Check for invalid itemID
        int potLevel;
        final List<StructItemOption> potInfo = MapleItemInformationProvider.getInstance().getPotentialInfo(potID);
        //gets the "real potential level"
        if (eqLevel == 0) {
            potLevel = 1;
        } else {
            potLevel = eqLevel / 10;
            potLevel++;
        }
        if (potLevel > 20) {
            potLevel = 20;
        }
        if (eqLevel % 10 == 0 && potLevel != 0) { //Potential level 17 means 170-179, not 170-180
            potLevel--;
        }
        if (potID == 0) {
            return "No potential";
        } else if (potID < 0) {
            return "Hidden potential";
        }
        StructItemOption st = potInfo.get(potLevel - 1);

        //Initial string.  This is mostly complete already, but the parameter name (ex. #incPDD) needs to be replaced with the actual value.
        String sb = st.opString;
        for (int i = 0; i < st.opString.length(); i++) {
            if (st.opString.charAt(i) == '#') { //found the start of the parameter
                int j = i + 2;
                while ((j < st.opString.length()) && st.opString.substring(i + 1, j).matches("^[a-zA-Z]+$")) { //read the parameter's name
                    j++;
                }
                String curParam = st.opString.substring(i, j);
                String curParamStripped;
                if (j != st.opString.length() || st.opString.charAt(st.opString.length() - 1) == '%') { //remove trailing % if present
                    curParamStripped = curParam.substring(1, curParam.length() - 1);
                } else {
                    curParamStripped = curParam.substring(1);
                }

                String paramValue = Integer.toString(st.get(curParamStripped)); //get the value of the parameter

                if (curParam.charAt(curParam.length() - 1) == '%') { //put back the % if we stripped it earlier
                    paramValue = paramValue.concat("%");
                }
                sb = sb.replace(curParam, paramValue);
            }
        }
        return sb;
    }

    public static String getJobName(int job) {
        Map<Integer, String> jobs = new HashMap<>();
        jobs.put(112, "Hero");
        jobs.put(122, "Paladin");
        jobs.put(132, "Dark Knight");
        jobs.put(212, "Arch Mage (Fire, Poison)");
        jobs.put(222, "Arch Mage (Ice, Lightning)");
        jobs.put(232, "Bishop");
        jobs.put(312, "Bowmaster");
        jobs.put(322, "Marksman");
        jobs.put(412, "Night Lord");
        jobs.put(422, "Shadower");
        jobs.put(434, "Dual Blade");
        jobs.put(512, "Buccaneer");
        jobs.put(522, "Corsair");
        jobs.put(532, "Cannon Master");
        jobs.put(2112, "Aran");
        jobs.put(2217, "Evan (9th Master)");
        jobs.put(2218, "Evan (10th Master)");
        jobs.put(2312, "Mercedes");
        jobs.put(3112, "Demon Slayer");
        jobs.put(3212, "Battle Mage");
        jobs.put(3312, "Wild Hunter");
        jobs.put(3512, "Mechanic");
        jobs.put(2412, "Phantom");
        jobs.put(5112, "Mihile");
        jobs.put(572, "Jett");
        return jobs.get(job);
    }

    public static void achievementRatio(MapleClient c) {
        //PQs not affected: Amoria, MV, CWK, English, Zakum, Horntail(?), Carnival, Ghost, Guild, LudiMaze, Elnath(?) 
        switch (c.getPlayer().getMapId()) {
            case 240080600:
            case 920010000:
            case 930000000:
            case 930000100:
            case 910010000:
            case 922010100:
            case 910340100:
            case 925100000:
            case 926100000:
            case 926110000:
            case 921120005:
            case 932000100:
            case 923040100:
            case 921160100:
            case 240080100:
            case 920010100:
            case 920010400:
                c.getSession().write(CField.achievementRatio(0));
                break;
            case 930000200:
            case 922010200:
            case 922010300:
            case 922010400:
            case 922010401:
            case 922010402:
            case 922010403:
            case 922010404:
            case 922010405:
            case 926100001:
            case 926110001:
            case 921160200:
            case 240080200:
            case 925100100:
            case 920010300:
                c.getSession().write(CField.achievementRatio(20));
                break;
            case 930000300:
            case 910340200:
            case 922010500:
            case 922010600:
            case 925100200:
            case 925100201:
            case 925100202:
            case 926100100:
            case 926110100:
            case 921120100:
            case 932000200:
            case 923040200:
            case 921160300:
            case 921160310:
            case 921160320:
            case 921160330:
            case 921160340:
            case 921160350:
            case 240080300:
            case 920010200:
            case 925100400:
                c.getSession().write(CField.achievementRatio(40));
                break;
            case 930000400:
            case 926100200:
            case 926110200:
            case 926100201:
            case 926110201:
            case 926100202:
            case 926110202:
            case 921160400:
                c.getSession().write(CField.achievementRatio(35));
                break;
            case 910340300:
            case 922010700:
            case 930000500:
            case 925100300:
            case 925100301:
            case 925100302:
            case 926100203:
            case 926110203:
            case 921120200:
            case 932000300:
            case 240080700:
            case 240080800:
            case 923040300:
            case 921160500:
            case 240080400:
            case 920010700:
            case 920010600:
                c.getSession().write(CField.achievementRatio(60));
                break;
            case 910340400:
            case 922010800:
            case 930000600:
            case 926100300:
            case 926110300:
            case 926100301:
            case 926110301:
            case 926100302:
            case 926110302:
            case 926100303:
            case 926110303:
            case 926100304:
            case 926110304:
            case 921120300:
            case 932000400:
            case 923040400:
            case 921160600:
            case 240080500:
                c.getSession().write(CField.achievementRatio(80));
                break;
            case 910340500:
            case 922010900:
            case 930000700:
            case 920010800:
            case 925100500:
            case 926100400:
            case 926110400:
            case 926100401:
            case 926110401:
            case 921120400:
            case 921160700:
                c.getSession().write(CField.achievementRatio(80));
                break;
            case 922011000:
            case 922011100:
            case 930000800:
            case 920011000:
            case 920011100:
            case 920011300:
            case 925100600:
            case 926100500:
            case 926110500:
            case 926100600:
            case 926110600:
            case 921120500:
            case 921120600:
                c.getSession().write(CField.achievementRatio(100));
                break;
        }
    }

    public static boolean isAngel(int sourceid) {
        return isBeginnerJob(sourceid / 10000) && (sourceid % 10000 == 1085 || sourceid % 10000 == 1087 || sourceid % 10000 == 1090 || sourceid % 10000 == 1179);
    }

    public static boolean isFishingMap(int mapid) {
        return mapid == 749050500 || mapid == 749050501 || mapid == 749050502 || mapid == 970020000 || mapid == 970020005;
    }

    public static int getRewardPot(int itemid, int closeness) {
        switch (itemid) {
            case 2440000:
                switch (closeness / 10) {
                    case 0:
                    case 1:
                    case 2:
                        return 2028041 + (closeness / 10);
                    case 3:
                    case 4:
                    case 5:
                        return 2028046 + (closeness / 10);
                    case 6:
                    case 7:
                    case 8:
                        return 2028049 + (closeness / 10);
                }
                return 2028057;
            case 2440001:
                switch (closeness / 10) {
                    case 0:
                    case 1:
                    case 2:
                        return 2028044 + (closeness / 10);
                    case 3:
                    case 4:
                    case 5:
                        return 2028049 + (closeness / 10);
                    case 6:
                    case 7:
                    case 8:
                        return 2028052 + (closeness / 10);
                }
                return 2028060;
            case 2440002:
                return 2028069;
            case 2440003:
                return 2430278;
            case 2440004:
                return 2430381;
            case 2440005:
                return 2430393;
        }
        return 0;
    }

    public static boolean isEventMap(final int mapid) {
        return (mapid >= 109010000 && mapid < 109050000) || (mapid > 109050001 && mapid < 109090000) || (mapid >= 809040000 && mapid <= 809040100);
    }

    public static boolean isMagicChargeSkill(final int skillid) {
        switch (skillid) {
            case 2121001: // Big Bang
            case 2221001:
            case 2321001:
                //case 22121000: //breath
                //case 22151001:
                return true;
        }
        return false;
    }

    public static boolean isPrepareSkill(int skillID) {
        switch (skillID) {
            case 3100001:   // 파이널 어택 : 활
            case 3120008:   // 어드밴스드 파이널 어택 : 활
            case 3121004:   // 폭풍의 시
            case 4341014:   // 아수라
            case 4341019:   // 블레이드 스톰
            case 5221004:   // 래피드 파이어
            case 5221025:   // 불릿 파티
            case 12111022:  // 마엘스트롬
            case 12121006:  // 드래곤 슬레이브
            case 13111002:  // 폭풍의 시
            case 13121001:  // 천공의 노래
            case 23121000:  // 이슈타르의 링
            case 24121000:  // 얼티밋 드라이브
            case 33121009:  // 와일드 발칸
            case 35001001:  // 플레임 런처
            case 35101009:  // 강화된 플레임 런처
            case 52121004:  // 페니 토네이도
            case 62111007:  // 노파의 분노
            case 62121000:  // 파사연격부
            case 65111002:  // 난권연격
            case -1: {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotPrepareBombSkill(int skillID) {
        switch (skillID) {
            case 2221012:  // 프로즌 오브
            case 2321015:  // 디바인 퍼니시먼트
            case 11121029: // 코스믹 샤워
            case 12121007: // 인피니티 플레임 서클
            case 14121003: // 다크니스 오멘
            case 27121201: // 모닝 스타폴
            case 27120211: // 모닝 스타폴
            case 21121068: // 마하의 영역
            case 35121052: // 디스토션 필드
            case 62111008: // 식신초래
            case 62121001: // 요회해방
            case 62121011: // 설녀
            case -1: {
                return true;
            }
        }
        return false;
    }

    public static boolean isBindSkill(int skillID) {
        switch (skillID) {
            case 2221011:   // 프리징 브레스
            case 11121004:  // 소울 페네트레이션
            case 14121004:  // 쉐도우 스티치
            case 21121029:  // 헌터즈 타겟팅
            case 31121006:  // 다크 바인드
            case 33121017:  // 재규어 소울
            case 62121004:  // 퇴마 유성부
            case 65111004:  // 무영각
            case -1: {
                return true;
            }
        }
        return false;
    }

    public static boolean isTeamMap(final int mapid) {
        return mapid == 109080000 || mapid == 109080001 || mapid == 109080002 || mapid == 109080003 || mapid == 109080010 || mapid == 109080011 || mapid == 109080012 || mapid == 109090300 || mapid == 109090301 || mapid == 109090302 || mapid == 109090303 || mapid == 109090304 || mapid == 910040100 || mapid == 960020100 || mapid == 960020101 || mapid == 960020102 || mapid == 960020103 || mapid == 960030100 || mapid == 689000000 || mapid == 689000010;
    }

    public static final boolean isRedLeaf(int mapid) {
        return mapid / 1000000 == 744;
    }

    public static int getDiceDef(int buffid, int stat) {
        if (buffid == stat) {
            return 30;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 != stat) {
            return 30;
        } else if (buffid != stat && buffid / 10 != stat && buffid % 10 == stat) {
            return 30;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 == stat) {
            return 40;
        }
        return 0;
    }

    public static int getDiceHPMP(int buffid, int stat) {
        if (buffid == stat) {
            return 20;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 != stat) {
            return 20;
        } else if (buffid != stat && buffid / 10 != stat && buffid % 10 == stat) {
            return 20;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 == stat) {
            return 30;
        }
        return 0;
    }

    public static int getDiceCrit(int buffid, int stat) {
        if (buffid == stat) {
            return 15;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 != stat) {
            return 15;
        } else if (buffid != stat && buffid / 10 != stat && buffid % 10 == stat) {
            return 15;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 == stat) {
            return 25;
        }
        return 0;
    }

    public static int getDiceDamage(int buffid, int stat) {
        if (buffid == stat) {
            return 20;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 != stat) {
            return 20;
        } else if (buffid != stat && buffid / 10 != stat && buffid % 10 == stat) {
            return 20;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 == stat) {
            return 30;
        }
        return 0;
    }

    public static int getDiceExp(int buffid, int stat) {
        if (buffid == stat) {
            return 30;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 != stat) {
            return 30;
        } else if (buffid != stat && buffid / 10 != stat && buffid % 10 == stat) {
            return 30;
        } else if (buffid != stat && buffid / 10 == stat && buffid % 10 == stat) {
            return 40;
        }
        return 0;
    }

    public static int getMPByJob(MapleCharacter chr) {
        int job = chr.getJob();
        int mp = 10;
        List<Integer> demonShields = Collections.unmodifiableList(Arrays.asList(1099000, 1099002, 1099003, 1099004));
        Equip shield = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
        if (shield != null && demonShields.contains(shield.getItemId())) {
            mp += shield.getMp();
        }
        /*if (chr.getSkillLevel(31120038) > 0) {
            mp += 50;
        }*/
 /*switch (chr.getJob()) {
            case 3100:
                return 30;
            case 3110:
                return 60;
            case 3111:
                return 90;
            case 3112:
                return 120;
        }*/
        return mp; // beginner or 3100
    }

    public static int getSkillLevel(final int level) {
        if (level >= 70 && level < 120) {
            return 2;
        } else if (level >= 120 && level < 200) {
            return 3;
        } else if (level >= 200) {
            return 4;
        }
        return 1;
    }

    public static final boolean isStealSkill(int skillId) {
        switch (skillId) {
            case 24001001:
            case 24101001:
            case 24111001:
            case 24121001:
                return true;
        }
        return false;
    }

    public static final int getStealSkill(int job) {
        switch (job) {
            case 1:
                return 24001001;
            case 2:
                return 24101001;
            case 3:
                return 24111001;
            case 4:
                return 24121001;
        }
        return 0;
    }

    public static final int getNumSteal(int jobNum) {
        switch (jobNum) {
            case 1:
                return 4;
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
        }
        return 0;
    }

    public static List<Integer> getHyperSkill(MapleCharacter chr) {
        List<Integer> ret = new ArrayList<>();
        for (int i = 30; i < 53; i++) {
            ret.add(chr.getJob() * 10000 + i);
        }
        for (int i = 1052; i < 1057; i++) {
            ret.add(chr.getJob() * 10000 + i);
        }
        if (chr.getJob() == 2218) {
            for (int i = 1052; i < 1057; i++) {
                ret.add(2217 * 10000 + i);
            }
        }
        if (chr.getJob() == 10112) {
            ret.add(100000267);
            ret.add(100000276);
            ret.add(100000277);
            ret.add(100001261);
            ret.add(100001274);
            ret.add(100001272);
            ret.add(100001283);
            ret.add(100001005);
        }
        return ret;
    }

    public static boolean isProfessionSkill(int skillid) {
        return skillid >= 92000000 && skillid <= 92999999;
    }

    public static final boolean canSteal(Skill skil) {
        return skil != null && !skil.isMovement() && !isLinkedAranSkill(skil.getId()) && skil.getId() % 10000 >= 1000 && getJobNumber(skil.getId() / 10000) > 0 && !isDualBlade(skil.getId() / 10000) && !isCannon(skil.getId() / 10000) && skil.getId() < 8000000 && skil.getEffect(1) != null && skil.getEffect(1).getSummonMovementType() == null;
    }

    public static long getMagnifyPrice(Equip eq) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (!ii.getEquipStats(eq.getItemId()).containsKey("reqLevel")) {
            return -1;
        }
        int level = ii.getEquipStats(eq.getItemId()).get("reqLevel").intValue();
        long price;
        int v1; // esi@1
        double v2; // st7@7
        int v3; // eax@7
        double v4; // st6@7
        int v5; // eax@12

        v1 = 0;
        if (level > 120) {
            v1 = 20;
        } else if (level > 70) {
            v1 = 5;
        } else if (level > 30) {
            v1 = 1;
        }
        v2 = (double) level;
        v3 = 2;
        v4 = 1.0;
        while (1 != 0) {
            if ((v3 & 1) != 0) {
                v4 = v4 * v2;
            }
            v3 >>= 1;
            if (!(v3 != 0)) {
                break;
            }
            v2 = v2 * v2;
        }
        v5 = (int) Math.ceil(v4);
        price = ((v1 * v5 <= 0 ? 1 : 0) - 1) & v1 * v5;

        return price;
    }

    public static int isCuponCheck(int itemid) {
        switch (itemid) {
            case 5150027:
            case 5150025:
            case 5150022:
            case 5150007:
            case 5150005:
            case 5150003:
            case 5150001: // 헤어샵 고급쿠폰
                return 5150053;
            case 5152007:
            case 5152005:
            case 5152003:
            case 5152001:
            case 5152030:
            case 5152028:
            case 5152024: // 성형외과 고급쿠폰
                return 5152057;
            case 5151007:
            case 5151005:
            case 5151003:
            case 5151001:
            case 5151022:
            case 5151020:
            case 5151015: // 염색 고급쿠폰
                return 5151036;
            case 5150026:
            case 5150024:
            case 5150021:
            case 5150006:
            case 5150004:
            case 5150002:
            case 5150000: // 헤어샵 일반쿠폰
                return 5150052;
            case 5152006:
            case 5152004:
            case 5152002:
            case 5152000:
            case 5152029:
            case 5152027:
            case 5152023: // 성형외과 고급쿠폰
                return 5152056;
            case 5151006:
            case 5151004:
            case 5151002:
            case 5151000:
            case 5151021:
            case 5151019:
            case 5151014: // 염색 고급쿠폰
                return 5151035;
            case 5153006:
            case 5153007:
            case 5153005:
            case 5153002:
            case 5153000:
            case 5153001: // 피부
                return 5153015;
            default:
                return itemid;
        }
    }

    public static int getRandomProfessionReactorByRank(int rank) {
        int base1 = 100000;
        int base2 = 200000;
        if (Randomizer.nextBoolean()) {
            if (rank == 1) {
                base1 += Randomizer.rand(0, 7);
            } else if (rank == 2) {
                base1 += Randomizer.rand(4, 9);
            } else if (rank == 3) {
                if (Randomizer.rand(0, 4) == 1) {
                    base1 += 11;
                } else {
                    base1 += Randomizer.rand(0, 9);
                }
            }
            return base1;
        } else {
            if (rank == 1) {
                base2 += Randomizer.rand(0, 7);
            } else if (rank == 2) {
                base2 += Randomizer.rand(4, 9);
            } else if (rank == 3) {
                if (Randomizer.rand(0, 6) == 1) {
                    base2 += 11;
                } else {
                    base2 += Randomizer.rand(0, 9);
                }
            }
            return base2;
        }
    }

    public static boolean isSpecialCSScroll(final int scrollId) {
        switch (scrollId) {
            case 5064000: // 프로텍트
            case 5064002: // 라이트 프로텍트
            case 5064003: // 슈페리얼
            case 5064100: // 세이프티
            case 5064101: // 라이트 세이프티
            case 5064300: // 리커버리
            case 5064301: // 라이트 리커버리
            case 5063100: // 럭키 프로텍트
            case 5063000: // 행운의 열쇠
            case 5064200: // 이노센트
            case 5064201: // 라이트 이노센트
                return true;
        }
        return false;
    }

    public static boolean isBonusPot(int opID) {
        //bpots always are x2xxx
        return Math.floor((opID / 1000) % 10) == 2;
    }

    /**
     * Returns the chance a given cube can increase the rank (state) of an
     * equip.
     *
     * @param cube
     * @return
     */
    public static int getRankUpChanceByCube(Cubes cube) {
        switch (cube) {
            case RED:
            case BONUS:
                return 10;
            case BLACK:
                return 20;
            default:
                return 0;
        }
    }

    /**
     * Returns the chance a given cube can give a 3rd main potential line to an
     * item that currently has none.
     *
     * @param cube
     * @return
     */
    public static int get3rdLineUpChanceByCube(Cubes cube) {
        switch (cube) {
            case RED:
                return 10;
            case BLACK:
                return 20;
            default:
                return 0;
        }
    }

    public static boolean ExpeditionMap(int mapid) {
        switch (mapid) {
            case 105100300:
            case 105100400:
            case 280030000:
            case 280030001:
            case 240060200:
            case 240060201:
            case 270050100:
            case 270050300:
            case 211070100:
            case 211070110:
            case 807300110:
            case 807300120:
            case 811000500:
            case 271040100:
            case 271040210:
            case 272020200:
            case 272020400:
            case 350060600:
                return true;
        }
        return false;
    }

    /**
     * Gets the maximum rank an equip can achieve by using a certain cube.
     *
     * @param cube
     * @return
     */
    public static int getMaxAvailableState(Cubes cube) {
        switch (cube) {
            case MIRACLE:
                return Equip.UNIQUE;
            default:
                return Equip.LEGENDARY;
        }
    }

    public static final boolean isFinalAttackskill(int skillid) {
        switch (skillid) {
            case 13101002:
            case 11101002:
            case 33100009:
            case 51120002:
            case 21100010:
            case 21120012:
            case 1300002:
            case 1120013:
            case 1100002:
            case 51100002:
            case 3120008:
            case 3100001:
            case 1200002:
            case 61110008:
            case 62100007:
            case 65121007:
            case 15100027:
            case 13121017:
                return true;
        }
        return false;
    }

    public static int getStatDice(int stat) {
        switch (stat) {
            case 2:
                return 30;
            case 3:
                return 20;
            case 4:
                return 15;
            case 5:
                return 20;
            case 6:
                return 30;
        }
        return 0;
    }

    public static int getDiceStat(int buffid, int stat) {
        if (buffid == stat || buffid % 10 == stat || buffid / 10 == stat) {
            return getStatDice(stat);
        } else if (buffid == (stat * 100)) {
            return getStatDice(stat) + 10;
        }
        return 0;
    }

    public static int getJudgmentStat(int buffid, int stat) {
        switch (stat) {
            case 1: // critical
                return buffid == 20031209 ? 5 : 10;
            case 2: // itemdrop
                return buffid == 20031209 ? 10 : 20;
            case 3:
                return 2020;
            case 4:
                return 100;
        }
        return 0;
    }

    public static int getBuffDelay(int skill) {
        switch (skill) {
            case 24111002:
            case 24111003:
            case 24111005:
            case 24121004:
            case 24121008:
                return 1000;
        }
        return 0;
    }

    public static double handleDrop(int itemID, MapleMonster mob) {
        int v1 = (itemID / 10000);
        int v2 = (mob.getId() / 100000);
        if (v1 < 200) {
            return (mob.getStats().isBoss() ? (v2 == 88 ? 50.0 : 5.0) : 0.02);
        }
        switch (v1) {
            case 400: {
                int v3 = itemID / 1000;
                switch (v3) {
                    case 4000: {
                        return 35.0;
                    }
                    case 4003: {
                        return 0.5;
                    }
                    case 4004:
                    case 4007: {
                        return 0.1;
                    }
                    case 4005:
                    case 4006: {
                        return 0.9;
                    }
                    default: {
                        return 0.0;
                    }
                }
            }
            case 401:
            case 402:
            case 413: {
                return 0.1;
            }
            case 200:
            case 201:
            case 202: {
                return (mob.getStats().getCP() > 0 ? 100.0 : 1.0);
            }
            case 204: {
                return (mob.getStats().isBoss() ? (v2 == 88 ? 30.0 : 5.0) : 0.1);
            }
            case 228:
            case 229:
            case 233:
            case 207: {
                return (mob.getStats().isBoss() ? (v2 == 88 ? 30.0 : 1.0) : 0.02);
            }
            default: {
                return 0.0;
            }
        }
    }

    public static int[] scroll_60 = {
        2040001, // 투구 방어력 주문서 60%
        2040004, // 투구 체력 주문서 60%
        2040025, // 투구 지력 주문서 60%
        2040029, // 투구 민첩 주문서 60%
        2040301, // 귀 장식 지력 주문서 60%
        2040317, // 귀 장식 민첩 주문서 60%
        2040321, // 귀 장식 행운 주문서 60%
        2040326, // 귀 장식 체력 주문서 60%
        2040401, // 상의 방어력 주문서 60%
        2040418, // 상의 힘 주문서 60%
        2040421, // 상의 체력 주문서 60%
        2040425, // 상의 행운 주문서 60%
        2040501, // 전신 갑옷 민첩 주문서 60%
        2040504, // 전신 갑옷 방어력 주문서 60%
        2040513, // 전신 갑옷 지력 주문서 60%
        2040516, // 전신 갑옷 행운 주문서 60%
        2040532, // 전신 갑옷 힘 주문서 60%
        2040601, // 하의 방어력 주문서 60%
        2040618, // 하의 점프 주문서 60%
        2040621, // 하의 체력 주문서 60%
        2040625, // 하의 민첩 주문서 60%
        2040701, // 신발 회피 주문서 60%
        2040704, // 신발 점프력 주문서 60%
        2040707, // 신발 이동속도 주문서 60%
        2040801, // 장갑 민첩 주문서 60%
        2040804, // 장갑 공격력 주문서 60%
        2040824, // 장갑 체력 주문서 60%
        2040901, // 방패 방어력 주문서 60%
        2040924, // 방패 행운 주문서 60%
        2040927, // 방패 체력 주문서 60%
        2040931, // 방패 힘 주문서 60%
        2041001, // 망토 마법 방어력 주문서 60%
        2041004, // 망토 물리 방어력 주문서 60%
        2041007, // 망토 체력 주문서 60%
        2041010, // 망토 마나 주문서 60%
        2041013, // 망토 힘 주문서 60%
        2041016, // 망토 지력 주문서 60%
        2041019, // 망토 민첩 주문서 60%
        2041022, // 망토 행운 주문서 60%
        2041101, // 반지 힘 주문서 60%
        2041104, // 반지 지력 주문서 60%
        2041107, // 반지 민첩 주문서 60%
        2041110, // 반지 행운 주문서 60%
        2041301, // 벨트 힘 주문서 60%
        2041304, // 벨트 지력 주문서 60%
        2041307, // 벨트 민첩 주문서 60%
        2041310, // 벨트 행운 주문서 60%
        2043001, // 한손 검 공격력 주문서 60%
        2043017, // 한손 검 명중치 주문서 60%
        2043101, // 한손 도끼 공격력 주문서 60%
        2043112, // 한손 도끼 명중치 주문서 60%
        2043201, // 한손 둔기 공격력 주문서 60%
        2043212, // 한손 둔기 명중치 주문서 60%
        2043301, // 단검 공격력 주문서 60%
        2043401, // 블레이드 공격력 주문서 60%
        2043601, // 케인 공격력 주문서 60%
        2043701, // 완드 마력 주문서 60%
        2043801, // 스태프 마력 주문서 60%
        2044001, // 두손 검 공격력 주문서 60%
        2044012, // 두손 검 명중치 주문서 60%
        2044101, // 두손 도끼 공격력 주문서 60%
        2044112, // 두손 도끼 명중치 주문서 60%
        2044201, // 두손 둔기 공격력 주문서 60%
        2044212, // 두손 둔기 명중치 주문서 60%
        2044301, // 창 공격력 주문서 60%
        2044312, // 창 명중치 주문서 60%
        2044401, // 폴암 공격력 주문서 60%
        2044412, // 폴암 명중치 주문서 60%
        2044501, // 활 공격력 주문서 60%
        2044601, // 석궁 공격력 주문서 60%
        2044701, // 아대 공격력 주문서 60%
        2044801, // 너클 공격력 주문서 60%
        2044807, // 너클 명중치 주문서 60%
        2044901, // 건 공격력 주문서 60%
        2045201, // 듀얼 보우 건 공격력 주문서 60%
        2045301, // 핸드 캐논 공격력 주문서 60%
        2045401, // 카타나 공격력 주문서 60%
        2045501, // 부채 마력 주문서 60%
        2044051, // 무권 공격력 주문서 60%
    };

    public static int[] scroll_10 = {
        2040002, // 투구 방어력 주문서 10%
        2040005, // 투구 체력 주문서 10%
        2040026, // 투구 지력 주문서 10%
        2040031, // 투구 민첩 주문서 10%
        2040302, // 귀 장식 지력 주문서 10%
        2040318, // 귀 장식 민첩 주문서 10%
        2040323, // 귀 장식 행운 주문서 10%
        2040328, // 귀 장식 체력 주문서 10%
        2040402, // 상의 방어력 주문서 10%
        2040419, // 상의 힘 주문서 10%
        2040422, // 상의 체력 주문서 10%
        2040427, // 상의 행운 주문서 10%
        2040502, // 전신 갑옷 민첩 주문서 10%
        2040505, // 전신 갑옷 방어력 주문서 10%
        2040514, // 전신 갑옷 지력 주문서 10%
        2040517, // 전신 갑옷 행운 주문서 10%
        2040534, // 전신 갑옷 힘 주문서 10%
        2040602, // 하의 방어력 주문서 10%
        2040619, // 하의 점프 주문서 10%
        2040622, // 하의 체력 주문서 10%
        2040627, // 하의 민첩 주문서 10%
        2040702, // 신발 회피 주문서 10%
        2040705, // 신발 점프력 주문서 10%
        2040708, // 신발 이동속도 주문서 10%
        2040802, // 장갑 민첩 주문서 10%
        2040805, // 장갑 공격력 주문서 10%
        2040825, // 장갑 체력 주문서 10%
        2040902, // 방패 방어력 주문서 10%
        2040925, // 방패 행운 주문서 10%
        2040928, // 방패 체력 주문서 10%
        2040933, // 방패 힘 주문서 10%
        2041002, // 망토 마법 방어력 주문서 10%
        2041005, // 망토 물리 방어력 주문서 10%
        2041008, // 망토 체력 주문서 10%
        2041011, // 망토 마나 주문서 10%
        2041014, // 망토 힘 주문서 10%
        2041017, // 망토 지력 주문서 10%
        2041020, // 망토 민첩 주문서 10%
        2041023, // 망토 행운 주문서 10%
        2041102, // 반지 힘 주문서 10%
        2041105, // 반지 지력 주문서 10%
        2041108, // 반지 민첩 주문서 10%
        2041111, // 반지 행운 주문서 10%
        2041302, // 벨트 힘 주문서 10%
        2041305, // 벨트 지력 주문서 10%
        2041308, // 벨트 민첩 주문서 10%
        2041311, // 벨트 행운 주문서 10%
        2043002, // 한손 검 공격력 주문서 10%
        2043019, // 한손 검 명중치 주문서 10%
        2043102, // 한손 도끼 공격력 주문서 10%
        2043114, // 한손 도끼 명중치 주문서 10%
        2043202, // 한손 둔기 공격력 주문서 10%
        2043214, // 한손 둔기 명중치 주문서 10%
        2043302, // 단검 공격력 주문서 10%
        2043402, // 블레이드 공격력 주문서 10%
        2043602, // 케인 공격력 주문서 10%
        2043702, // 완드 마력 주문서 10%
        2043802, // 스태프 마력 주문서 10%
        2044002, // 두손 검 공격력 주문서 10%
        2044014, // 두손 검 명중치 주문서 10%
        2044102, // 두손 도끼 공격력 주문서 10%
        2044114, // 두손 도끼 명중치 주문서 10%
        2044202, // 두손 둔기 공격력 주문서 10%
        2044214, // 두손 둔기 명중치 주문서 10%
        2044302, // 창 공격력 주문서 10%
        2044314, // 창 명중치 주문서 10%
        2044402, // 폴암 공격력 주문서 10%
        2044414, // 폴암 명중치 주문서 10%
        2044502, // 활 공격력 주문서 10%
        2044602, // 석궁 공격력 주문서 10%
        2044702, // 아대 공격력 주문서 10%
        2044802, // 너클 공격력 주문서 10%
        2044809, // 너클 명중치 주문서 10%
        2044902, // 건 공격력 주문서 10%
        2045202, // 듀얼 보우 건 공격력 주문서 10%
        2045302, // 핸드 캐논 공격력 주문서 10%
        2045402, // 카타나 공격력 주문서 10%
        2045502, // 부채 마력 주문서 10%
        2044052, // 무권 공격력 주문서 10%
    };

    public static int[] scroll_70 = {
        2040008, // 투구 방어력 주문서 70%
        2040010, // 투구 체력 주문서 70%
        2040012, // 투구 지력 주문서 70%
        2040028, // 투구 민첩 주문서 70%
        2040304, // 귀 장식 지력 주문서 70%
        2040306, // 귀 장식 민첩 주문서 70%
        2040320, // 귀 장식 행운 주문서 70%
        2040325, // 귀 장식 체력 주문서 70%
        2040404, // 상의 방어력 주문서 70%
        2040406, // 상의 힘 주문서 70%
        2040408, // 상의 체력 주문서 70%
        2040424, // 상의 행운 주문서 70%
        2040508, // 전신 갑옷 민첩 주문서 70%
        2040510, // 전신 갑옷 방어력 주문서 70%
        2040518, // 전신 갑옷 지력 주문서 70%
        2040520, // 전신 갑옷 행운 주문서 70%
        2040531, // 전신 갑옷 힘 주문서 70%
        2040604, // 하의 방어력 주문서 70%
        2040606, // 하의 점프 주문서 70%
        2040608, // 하의 체력 주문서 70%
        2040624, // 하의 민첩 주문서 70%
        2040712, // 신발 민첩 주문서 70%
        2040714, // 신발 점프력 주문서 70%
        2040716, // 신발 이동 속도 주문서 70%
        2040808, // 장갑 민첩 주문서 70%
        2040810, // 장갑 공격력 주문서 70%
        2040812, // 장갑 체력 주문서 70%
        2040904, // 방패 방어력 주문서 70%
        2040906, // 방패 행운 주문서 70%
        2040908, // 방패 체력 주문서 70%
        2040930, // 방패 힘 주문서 70%
        2041026, // 망토 마법 방어력 주문서 70%
        2041028, // 망토 물리 방어력 주문서 70%
        2041030, // 망토 체력 주문서 70%
        2041032, // 망토 마나 주문서 70%
        2041034, // 망토 힘 주문서 70%
        2041036, // 망토 지력 주문서 70%
        2041038, // 망토 민첩 주문서 70%
        2041040, // 망토 행운 주문서 70%
        2041112, // 반지 힘 주문서 70%
        2041114, // 방지 지력 주문서 70%
        2041116, // 반지 민첩 주문서 70%
        2041118, // 반지 행운 주문서 70%
        2041312, // 벨트 힘 주문서 70%
        2041314, // 벨트 지력 주문서 70%
        2041316, // 벨트 민첩 주문서 70%
        2041318, // 벨트 행운 주문서 70%
        2043004, // 한손 검 공격력 주문서 70%
        2043016, // 한손 검 명중치 주문서 70%
        2043104, // 한손 도끼 공격력 주문서 70%
        2043204, // 한손 둔기 공격력 주문서 70%
        2043211, // 한손 둔기 명중치 주문서 70%
        2043304, // 단검 공격력 주문서 70%
        2043603, // 케인 공격력 주문서 70%
        2043704, // 완드 마력 주문서 70%
        2043804, // 스태프 마력 주문서 70%
        2044004, // 두손 검 공격력 주문서 70%
        2044011, // 두손 검 명중치 주문서 70%
        2044104, // 두손 도끼 공격력 주문서 70%
        2044111, // 두손 도끼 명중치 주문서 70%
        2044204, // 두손 둔기 공격력 주문서 70%
        2044211, // 두손 둔기 명중치 주문서 70%
        2044304, // 창 공격력 주문서 70%
        2044311, // 창 명중치 주문서 70%
        2044404, // 폴암 공격력 주문서 70%
        2044411, // 폴암 명중치 주문서 70%
        2044504, // 활 공격력 주문서 70%
        2044604, // 석궁 공격력 주문서 70%
        2044704, // 아대 공격력 주문서 70%
        2044803, // 너클 공격력 주문서 70%
        2044806, // 너클 명중치 주문서 70%
        2044903, // 건 공격력 주문서 70%
        2045203, // 듀얼 보우 건 공격력 주문서 70%
        2045303, // 핸드 캐논 공격력 주문서 70%
        2045403, // 카타나 공격력 주문서 70%
        2045503, // 부채 마력 주문서 70%
        2044053, // 무권 공격력 주문서 70%
    };

    public static int[] scroll_30 = {
        2040009, // 투구 방어력 주문서 30%
        2040011, // 투구 체력 주문서 30%
        2040013, // 투구 지력 주문서 30%
        2040030, // 투구 민첩 주문서 30%
        2040305, // 귀 장식 지력 주문서 30%
        2040307, // 귀 장식 민첩 주문서 30%
        2040322, // 귀 장식 행운 주문서 30%
        2040327, // 귀 장식 체력 주문서 30%
        2040405, // 상의 방어력 주문서 30%
        2040407, // 상의 힘 주문서 30%
        2040409, // 상의 체력 주문서 30%
        2040426, // 상의 행운 주문서 30%
        2040509, // 전신 갑옷 민첩 주문서 30%
        2040511, // 전신 갑옷 방어력 주문서 30%
        2040519, // 전신 갑옷 지력 주문서 30%
        2040521, // 전신 갑옷 행운 주문서 30%
        2040532, // 전신 갑옷 힘 주문서 30%
        2040605, // 하의 방어력 주문서 30%
        2040607, // 하의 점프 주문서 30%
        2040609, // 하의 체력 주문서 30%
        2040626, // 하의 민첩 주문서 30%
        2040713, // 신발 민첩 주문서 30%
        2040715, // 신발 점프력 주문서 30%
        2040717, // 신발 이동 속도 주문서 30%
        2040809, // 장갑 민첩 주문서 30%
        2040811, // 장갑 공격력 주문서 30%
        2040813, // 장갑 체력 주문서 30%
        2040905, // 방패 방어력 주문서 30%
        2040907, // 방패 행운 주문서 30%
        2040909, // 방패 체력 주문서 30%
        2040932, // 방패 힘 주문서 30%
        2041027, // 망토 마법 방어력 주문서 30%
        2041029, // 망토 물리 방어력 주문서 30%
        2041031, // 망토 체력 주문서 30%
        2041033, // 망토 마나 주문서 30%
        2041035, // 망토 힘 주문서 30%
        2041037, // 망토 지력 주문서 30%
        2041039, // 망토 민첩 주문서 30%
        2041041, // 망토 행운 주문서 30%
        2041113, // 반지 힘 주문서 30%
        2041115, // 방지 지력 주문서 30%
        2041117, // 반지 민첩 주문서 30%
        2041119, // 반지 행운 주문서 30%
        2041313, // 벨트 힘 주문서 30%
        2041315, // 벨트 지력 주문서 30%
        2041317, // 벨트 민첩 주문서 30%
        2041319, // 벨트 행운 주문서 30%
        2043005, // 한손 검 공격력 주문서 30%
        2043018, // 한손 검 명중치 주문서 30%
        2043105, // 한손 도끼 공격력 주문서 30%
        2043113, // 한손 도끼 명중치 주문서 30%
        2043205, // 한손 둔기 공격력 주문서 30%
        2043213, // 한손 둔기 명중치 주문서 30%
        2043305, // 단검 공격력 주문서 30%
        2043604, // 케인 공격력 주문서 30%
        2043705, // 완드 마력 주문서 30%
        2043805, // 스태프 마력 주문서 30%
        2044005, // 두손 검 공격력 주문서 30%
        2044013, // 두손 검 명중치 주문서 30%
        2044105, // 두손 도끼 공격력 주문서 30%
        2044113, // 두손 도끼 명중치 주문서 30%
        2044205, // 두손 둔기 공격력 주문서 30%
        2044213, // 두손 둔기 명중치 주문서 30%
        2044305, // 창 공격력 주문서 30%
        2044313, // 창 명중치 주문서 30%
        2044405, // 폴암 공격력 주문서 30%
        2044413, // 폴암 명중치 주문서 30%
        2044505, // 활 공격력 주문서 30%
        2044605, // 석궁 공격력 주문서 30%
        2044705, // 아대 공격력 주문서 30%
        2044804, // 너클 공격력 주문서 30%
        2044808, // 너클 명중치 주문서 30%
        2044904, // 건 공격력 주문서 30%
        2045204, // 듀얼 보우 건 공격력 주문서 30%
        2045304, // 핸드 캐논 공격력 주문서 30%
        2045404, // 카타나 공격력 주문서 30%
        2045504, // 부채 마력 주문서 30%
        2044054, // 무권 공격력 주문서 30%
    };

    public static boolean isNotSellCashItem(int itemID) {
        switch (itemID) {
            case 5044000: // [1일] 텔레포트 월드 맵
            case 5044001: // [3일] 텔레포트 월드 맵
            case 5044002: // [7일] 텔레포트 월드 맵
            case 5050100: // AP 초기화 주문서
            case 5051001: // SP 초기화 주문서
            case 5060002: // 프리미엄 부화기
            case 5061000: // 봉인의 자물쇠 : 7일
            case 5061001: // 봉인의 자물쇠 : 30일
            case 5061002: // 봉인의 자물쇠 : 90일
            case 5061003: // 봉인의 자물쇠 : 365일
            case 5062000: // 미라클 큐브
            case 5062002: // 마스터 미라클 큐브
            case 5062005: // 어메이징 미라클 큐브
            case 5062103: // 판타스틱 미라클 큐브
            case 5062200: // B 이너 어빌리티 프로텍트
            case 5062201: // A 이너 어빌리티 프로텍트
            case 5062400: // 신비의 모루
            case 5063100: // 럭키 프로텍트 실드
            case 5064000: // 프로텍트 실드
            case 5064002: // 라이트 프로텍트 실드
            case 5064100: // 세이프티 실드
            case 5064101: // 라이트 세이프티 실드
            case 5064200: // 퍼펙트 이노센트 스크롤
            case 5064201: // 라이트 이노센트 스크롤
            case 5064300: // 리커버리 실드
            case 5064301: // 라이트 리커버리 실드
            case 5060008: // 매지컬 티포트
            case 5121032: // 쁘띠 로즈
            case 5450006: // [1일] 보따리 상인 묘묘
            case 5450007: // [7일] 보따리 상인 묘묘
            case 5450004: // [30일] 보따리 상인 묘묘
            case 5450008: // [1일] 이동 창고 왕 서방
            case 5450009: // [7일] 이동 창고 왕 서방
            case 5450005: // [30일] 이동 창고 왕 서방
            case 5500000: // [1일] 마법의 모래 시계
            case 5500001: // [7일] 마법의 모래 시계
            case 5500002: // [20일] 마법의 모래 시계
            case 5500005: // [50일] 마법의 모래 시계
            case 5500006: // [99일] 마법의 모래 시계
            case 5501001: // [30일] 클로토의 마법 릴
            case 5501002: // [60일] 클로토의 마법 릴
            case 5521000: // 쉐어 네임 택
            case 9102780: // 골드 어메이징 세트
            case 9102800: // 브론즈 어메이징 세트
            case 9102803: // 복실 복실 패키지
            case 9102809: // 환상 무기 상자 패키지
            case 9102783: // 마족 메투스 공격력 패키지
            case 9102784: // 마족 모스 공격력 패키지
            case 9102785: // 마족 디아 공격력 패키지
            case 9102786: // 마족 메투스 마력 패키지
            case 9102787: // 마족 모스 마력 패키지
            case 9102788: // 마족 디아 마력 패키지
            case 9102678: // 핑크빈 공격력 패키지
            case 9102679: // 핑크빈 마력 패키지
            case 9102745: // 구름 양 공격력 패키지
            case 9102746: // 구름 양 마력 패키지
            case 9102774: // 힙합 냥이단 골목 길 패키지 1
            case 9102775: // 힙합 냥이단 골목 길 패키지2
            case 9102267: // 듀얼 블레이드 세트 1
            case 9102268: // 듀얼 블레이드 세트 2
            case 9102269: // 듀얼 블레이드 세트 3
            case 9102720: // 하프 물범 이유식 세트 1
            case 9102721: // 하프 물범 이유식 세트 2
            case 9102747: // 구름 양 구름 사탕 세트 1
            case 9102748: // 구름 양 구름 사탕 세트 2
            case 9102789: // 메투스 공포 패키지 1
            case 9102790: // 모스 침묵 패키지 1
            case 9102791: // 디아 질투 패키지 1
            case 9102792: // 메투스 공포 패키지 2
            case 9102793: // 모스 침묵 패키지 2
            case 9102794: // 디아 질투 패키지 2
            case 9101581: // 스위트 카페 스페셜 패키지
            case 9101618: // 메이플 걸 요술 봉 패키지
            case 9101619: // 메이플 걸 방패 패키지
            case 9101726: // 오로라 반지 패키지
            case 9102680: // 왕 갈비 세트 1
            case 9102681: // 왕 갈비 세트 2
            case 9102651: // 별 사탕 세트 1
            case 9102652: // 별 사탕 세트 2
            case 9102653: // 생선 세트 1
            case 9102654: // 생선 세트 2
            case 9102655: // 블루베리 세트 1
            case 9102656: // 블루베리 세트 2
            case 9102670: // 캣 푸드 세트 1
            case 9102671: // 캣 푸드 세트 2
            case 9102706: // 컵 라면 세트 1
            case 9102707: // 컵 라면 세트 2
            case 9102483: // 실버 미라클 큐브 세트
            case 9102484: // 골드 미라클 큐브 세트
            case 9102570: // 골드 마스터 미라클 큐브 세트
            case 9102571: // 실버 마스터 미라클 큐브 세트
            case 9102796: // 인벤토리 스페셜 세트 1
            case 9102797: // 인벤토리 스페셜 세트 2
            case 9102798: // 인벤토리 스페셜 세트 3
            case 9102799: // 인벤토리 스페셜 세트 4
            case 9102795: // 스페셜 연발 폭죽 패키지
            case 9102772: // 골드 판타스틱 세트
            case 9102773: // 실버 판타스틱 세트
            case 9102781: // 실버 어메이징 세트
            case 9102253: // 블레이드 세트 1
            case 9102254: // 블레이드 세트 2
            case 9102255: // 블레이드 세트 3
            case 9102256: // 블레이드 세트 4
            case 9102257: // 블레이드 세트 5
            case 9102258: // 블레이드 세트 6
            case 9102259: // 슬래시 스톰 마스터리
            case 9102260: // 토네이도 스핀 마스터리
            case 9102261: // 미러 이미징 마스터리
            case 9102262: // 플라잉 어썰터 마스터리
            case 9102263: // 블레이드 퓨리 마스터리
            case 9102264: // 쏜즈 이펙트 마스터리
                return true;
        }
        return false;
    }

    public static int reqMapleForceExp(int v1) {
        return (v1 * 5);
    }

    public static int reqMapleForceMeso(int v1) {
        return (v1 * 9900000);
    }

    public static int reqArcaneForceExp(int v1) {
        int a1 = 0;
        switch (v1) {
            case 1: {
                a1 = 12;
                break;
            }
            case 2: {
                a1 = 15;
                break;
            }
            case 3: {
                a1 = 20;
                break;
            }
            case 4: {
                a1 = 27;
                break;
            }
            case 5: {
                a1 = 36;
                break;
            }
            case 6: {
                a1 = 47;
                break;
            }
            case 7: {
                a1 = 60;
                break;
            }
            case 8: {
                a1 = 75;
                break;
            }
            case 9: {
                a1 = 92;
                break;
            }
            case 10: {
                a1 = 111;
                break;
            }
            case 11: {
                a1 = 132;
                break;
            }
            case 12: {
                a1 = 155;
                break;
            }
            case 13: {
                a1 = 180;
                break;
            }
            case 14: {
                a1 = 207;
                break;
            }
            case 15: {
                a1 = 236;
                break;
            }
            case 16: {
                a1 = 267;
                break;
            }
            case 17: {
                a1 = 300;
                break;
            }
            case 18: {
                a1 = 335;
                break;
            }
            case 19: {
                a1 = 372;
                break;
            }
        }
        return a1;
    }

    public static int reqArcaneForceMeso(int v1) {
        return ((v1 + 1) * 5);
    }

    public static boolean isjobFromSubWeapon(int a1, int a2) {
        if (a1 > 109 && a1 < 115) {
            return (a2 > 1093199 && a2 < 1093210);
        }
        if (a1 > 119 && a1 < 125) {
            return (a2 > 1093209 && a2 < 1093220);
        }
        if (a1 > 129 && a1 < 135) {
            return (a2 > 1093219 && a2 < 1093230);
        }
        if (a1 > 209 && a1 < 215) {
            return (a2 > 1093229 && a2 < 1093240);
        }
        if (a1 > 219 && a1 < 225) {
            return (a2 > 1093239 && a2 < 1093250);
        }
        if (a1 > 229 && a1 < 235) {
            return (a2 > 1093249 && a2 < 1093260);
        }
        if (a1 > 309 && a1 < 315) {
            return (a2 > 1093259 && a2 < 1093270);
        }
        if (a1 > 319 && a1 < 325) {
            return (a2 > 1093269 && a2 < 1093280);
        }
        if (a1 > 409 && a1 < 415) {
            return (a2 > 1093289 && a2 < 1093300);
        }
        if (a1 > 419 && a1 < 425) {
            return (a2 > 1093279 && a2 < 1093290);
        }
        if (a1 > 509 && a1 < 515) {
            return (a2 > 1093899 && a2 < 1093910);
        }
        if (a1 > 519 && a1 < 525) {
            return (a2 > 1093909 && a2 < 1093920);
        }
        if (a1 > 529 && a1 < 535) {
            return (a2 > 1093919 && a2 < 1093930);
        }
        if (a1 > 1099 && a1 < 1515) {
            return (a2 > 1093969 && a2 < 1093980);
        }
        if (a1 > 2099 && a1 < 2115) {
            return (a2 > 1093929 && a2 < 1093940);
        }
        if (a1 > 2199 && a1 < 2219) {
            return (a2 > 1093939 && a2 < 1093950);
        }
        if (a1 > 3199 && a1 < 3215) {
            return (a2 > 1093949 && a2 < 1093960);
        }
        if (a1 > 3299 && a1 < 3315) {
            return (a2 > 1093959 && a2 < 1093970);
        }
        if (a1 > 3499 && a1 < 3515) {
            return (a2 > 1093699 && a2 < 1093710);
        }
        if (a1 > 6099 && a1 < 6115) {
            return (a2 > 1093799 && a2 < 1093810);
        }
        if (a1 > 6499 && a1 < 6515) {
            return (a2 > 1093859 && a2 < 1093870);
        }
        if (a1 > 5199 && a1 < 5215) {
            return (a2 > 1093809 && a2 < 1093820);
        }
        if (a1 > 6199 && a1 < 6215) {
            return (a2 > 1093999 && a2 < 1094010);
        }
        if (a1 > 4199 && a1 < 4215) {
            return (a2 > 1094799 && a2 < 1094810);
        }
        return false;
    }
    
    public static boolean isPotential(int itemID) {
        if (isPotentialScroll(itemID)) {
            return true;
        }
        if (isStamp(itemID)) {
            return true;
        }
        if (isMagnify(itemID)) {
            return true;
        }
        if (isCube(itemID)) {
            return true;
        }
        return false;
    }
    
    public static boolean isExceptional(int itemID) {
        if (itemID > 2049179 && itemID < 2049184) {
            return true;
        }
        return false;
    }

    public static final int consume_2230000 = 2230000;
    public static final int consume_2230001 = 2230001;
    public static final int consume_2230002 = 2230002;
    public static final int consume_2230003 = 2230003;
    public static final int consume_2230004 = 2230004;

    private static final int[] professionexp = {0, 250, 600, 1050, 1600, 2250, 3000, 3850, 4900, 5850, Integer.MAX_VALUE};

    public static final int getProfessionExpNeededForLevel(final int level) {
        return professionexp[level];
    }

    public static final int[] publicNpcIds = {9000090, 9070004, 9010022, 9071003, 9000087, 9000088, 1012000, 9000000};
    public static final int OMOK_SCORE = 122200;
    public static final int MATCH_SCORE = 122210;
    public static final int HP_ITEM = 122221;
    public static final int MP_ITEM = 122223;
    public static final int JAIL_TIME = 123455;
    public static final int JAIL_QUEST = 123456;
    public static final int REPORT_QUEST = 123457;
    public static final int ULT_EXPLORER = 111111;
    public static final int ENERGY_DRINK = 122500;
    public static final int HARVEST_TIME = 122501;
    public static final int PENDANT_SLOT = 122700;
    public static final int CURRENT_SET = 122800;
    public static final int BOSS_PQ = 150001;
    public static final int JAGUAR = 111112;
    public static final int JAGUAR_2 = 111113;
    public static final int JAGUAR_3 = 111114;
    public static final int JAGUAR_4 = 111115;
    public static final int JAGUAR_5 = 111116;
    public static final int JAGUAR_6 = 111117;
    public static final int JAGUAR_7 = 111118;
    public static final int DOJO = 150100;
    public static final int DOJO_RECORD = 150101;
    public static final int PARTY_REQUEST = 122900;
    public static final int PARTY_INVITE = 122901;
    public static final int QUICK_SLOT = 123000;
    public static final int ITEM_TITLE = 124000;
    public static final int NEO_CITY = 220000;
    public static final int ONLINE_TOGGLE_DISPLAY = 230000;
    public static final int rate2 = 2450064;
}
