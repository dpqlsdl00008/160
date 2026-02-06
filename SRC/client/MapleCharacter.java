package client;

import client.MapleTrait.MapleTraitType;
import constants.GameConstants;
import client.inventory.MapleInventoryType;
import client.inventory.MapleInventory;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
import client.inventory.ItemFlag;
import client.inventory.MapleRing;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Deque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serializable;

import handling.login.LoginInformationProvider.JobType;
import client.anticheat.CheatTracker;
import client.anticheat.ReportType;
import client.inventory.Equip;
import client.inventory.MapleAndroid;
import client.inventory.MapleImp;
import client.inventory.MapleImp.ImpFlag;
import client.inventory.MapleInventoryIdentifier;
import client.messages.MessageType;
import client.status.MonsterTemporaryStat;
import client.status.MonsterTemporaryStatEffect;
import constants.*;
import constants.shop.Shop;
import database.DatabaseConnection;
import database.DatabaseException;
import handling.ChatType;
import handling.QuestType;
import handling.UIType;
import handling.cashshop.CashShopServer;

import handling.channel.ChannelServer;
import handling.channel.handler.DueyHandler;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.MapleCharacterLook;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.PlayerBuffValueHolder;
import handling.world.World;
import handling.world.exped.MapleExpedition;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildCharacter;

import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

import scripting.EventInstanceManager;
import scripting.NPCScriptManager;
import server.MapleAchievements;
import server.MaplePortal;
import server.MapleShop;
import server.MapleStatEffect;
import server.MapleStorage;
import server.MapleTrade;
import server.Randomizer;
import server.RandomRewards;
import server.MapleCarnivalParty;
import server.MapleItemInformationProvider;
import server.life.MapleMonster;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleDoor;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import server.maps.FieldLimitType;
import server.maps.SavedLocationType;
import server.quest.MapleQuest;
import server.shops.IMaplePlayerShop;
import server.CashShop;
import tools.Pair;
import tools.packet.CCashShop;
import tools.packet.CMobPool;
import tools.packet.CPet;
import tools.packet.CMonsterCarnival;
import server.MapleCarnivalChallenge;
import server.MapleInventoryManipulator;
import server.MapleShopFactory;
import server.MapleShopItem;
import server.MapleStatEffect.CancelEffectAction;
import server.Timer;
import server.Timer.BuffTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.maps.*;
import server.movement.LifeMovementFragment;
import tools.ConcurrentEnumMap;
import tools.FileoutputUtil;
import static tools.FileoutputUtil.getDCurrentTime;
import tools.StringUtil;
import tools.Triple;
import tools.data.OutPacket;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CField;
import tools.packet.CField.SummonPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuddylistPacket;
import tools.packet.CWvsContext.BuffPacket;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.CWvsContext.InventoryPacket;
import tools.packet.CPlayerShop;

public class MapleCharacter extends AnimatedMapleMapObject implements Serializable, MapleCharacterLook {

    private static final long serialVersionUID = 845748950829L;
    private String name, chalktext, BlessOfFairy_Origin, BlessOfEmpress_Origin, teleportname;
    private long lastCombo, lastfametime, keydown_skill, nextConsume, pqStartTime, lastDragonBloodTime,
            lastBerserkTime, lastRecoveryTime, lastSummonTime, mapChangeTime, lastFishingTime, lastFairyTime,
            lastHPTime, lastMPTime, lastFamiliarEffectTime, lastDOTTime;
    private byte gmLevel, gender, initialSpawnPoint, skinColor, guildrank = 5, allianceRank = 5,
            world, fairyExp, subcategory, cardStack, runningStack, flipthecoin, trinityCount, rechargeCount, rechargeMobCount;
    private short level, job, mulung_energy, combo, force, gage, availableCP, fatigue, totalCP, hpApUsed, scrolledPosition, hcMode;
    private int powerTransfer = 1000;
    private int accountid, id, exp, meso, hair, face, demonMarking, mapid, fame, pvpExp, pvpPoints, totalWins, totalLosses,
            guildid = 0, fallcounter, maplepoints, acash, nxcredit, chair, itemEffect, recommend, Fanclub,
            rank = 1, rankMove = 0, jobRank = 1, jobRankMove = 0, marriageId, marriageItemId, dotHP, forgeEXP, mSkillPoints,
            currentrep, totalrep, coconutteam, followid, battleshipHP, challenge, guildContribution = 0, remainingAp,
            honourExp, honourLevel, chatType = -3, azwanCoinsAvail, azwanCoinsRedeemed, azwanECoinsAvail, azwanCCoinsAvail, todaycharm, todaycraft, todaycharisma, todaywill, todaysense, todayinsight, todayrep, saintsaver;
    private int glass_plusMorph = 1;
    private int glass_minusMorph = 9999;
    private Point old;
    private int[] wishlist, rocks, savedLocations, regrocks, hyperrocks, remainingSp = new int[10];
    private transient AtomicInteger inst, insd;
    private transient List<LifeMovementFragment> lastres;
    private List<Integer> lastmonthfameids, lastmonthbattleids, extendedSlots;
    private List<MapleDoor> doors;
    private List<MechDoor> mechDoors;
    private List<MaplePet> pets;
    private MaplePet[] petz = new MaplePet[3];
    private List<MapleSummon> summonlist;
    private List<Item> rebuy;
    private List<byte[]> buffStorage = new ArrayList<>();
    // public  List<int[]> corelist = new ArrayList<>();
    public List<Pair<Integer, Integer>> corelist = new ArrayList<Pair<Integer, Integer>>();
    private MapleShop azwanShopList;
    private MapleImp[] imps;
    private List<Pair<Integer, Boolean>> stolenSkills = new ArrayList<Pair<Integer, Boolean>>();
    private transient WeakReference<MapleCharacter>[] clones;
    public transient Set<MapleMonster> controlled;
    private transient Set<MapleMapObject> visibleMapObjects;
    private transient ReentrantReadWriteLock visibleMapObjectsLock;
    private transient ReentrantReadWriteLock summonsLock;
    private transient ReentrantReadWriteLock controlledLock;
    private transient MapleAndroid android;
    private Map<MapleQuest, MapleQuestStatus> quests;
    private Map<Integer, String> questinfo;
    private Map<Skill, SkillEntry> skills;
    private Map<String, String> CustomValues = new HashMap<String, String>();
    private transient Map<CharacterTemporaryStat, MapleBuffStatValueHolder> effects;
    //private transient Map<MapleBuffStat, MapleBuffStatValueHolder> effects;
    //private transient Map<MapleBuffStat, List<MapleBuffStatStackedValueHolder>> StackedBuff = new LinkedHashMap<MapleBuffStat, List<MapleBuffStatStackedValueHolder>>();
    public transient List<MapleSummon> summons;
    private transient Map<Integer, MapleCoolDownValueHolder> coolDowns;
    private transient Map<MonsterSkill, MapleDiseaseValueHolder> diseases;
    private transient Map<MonsterSkill, Pair<Integer, Integer>> MS_Mark;
    private transient Map<MonsterSkill, Pair<Integer, Integer>> MS_Poison;
    private transient Map<MonsterSkill, Pair<Integer, Integer>> MS_UserBomb;
    private Map<ReportType, Integer> reports;
    private CashShop cs;
    private transient Deque<MapleCarnivalChallenge> pendingCarnivalRequests;
    private transient MapleCarnivalParty carnivalParty;
    private BuddyList buddylist;
    private MonsterBook monsterbook;
    private transient CheatTracker anticheat;
    private MapleClient client;
    private transient MapleParty party;
    private PlayerStats stats;
    private MapleCharacterCards characterCard;
    private transient MapleMap map;
    private transient MapleShop shop;
    private transient MapleDragon dragon;
    private transient MapleExtractor extractor;
    private transient RockPaperScissors rps;
    private MapleStorage storage;
    private transient MapleTrade trade;
    private MapleMount mount;
    private int sp;
    private List<Integer> finishedAchievements;
    private MapleMessenger messenger;
    private byte[] petStore;
    private transient IMaplePlayerShop playerShop;
    private boolean invincible, canTalk, clone, followinitiator, followon, smega, hasSummon;
    private MapleGuildCharacter mgc;
    private MapleFamilyCharacter mfc;
    private transient EventInstanceManager eventInstance;
    private MapleInventory[] inventory;
    private SkillMacro[] skillMacros = new SkillMacro[5];
    private EnumMap<MapleTraitType, MapleTrait> traits;
    private MapleKeyLayout keylayout;
    private transient ScheduledFuture<?> mapTimeLimitTask;
    private transient Event_PyramidSubway pyramidSubway = null;
    private transient List<Integer> pendingExpiration = null;
    private transient Map<Skill, SkillEntry> pendingSkills = null;
    private transient Map<Integer, Integer> linkMobs;
    public List<InnerSkillValueHolder> innerSkills;
    private int headtitle = 0;
    private int Roulette_Item = 0;
    private boolean changed_wishlist, changed_trocklocations, changed_regrocklocations, changed_hyperrocklocations, changed_skillmacros, changed_achievements,
            changed_savedlocations, changed_questinfo, changed_skills, changed_reports, changed_extendedSlots, update_skillswipe, whiteEnabled = true, innerskill_changed = false, soul_stone = false,
            dressup, scene;
    public boolean isFinalFiguration = false;
    private byte SurPlus = 0;
    private short mobKilledNo;
    private long loginTime;
    private int[] friendshippoints = new int[4];
    private java.util.Timer healTimer;
    private java.util.Timer DFRecoveryTimer;
    private boolean petAutoFeed = false;
    private Map<MapleCharacterTimer, java.util.Timer> timers = new EnumMap<>(MapleCharacterTimer.class);
    private int str;
    private int luk;
    private int int_;
    private int dex;
    private int arcaneAim = 0, exeedCount = 0, exeedAttackCount = 0, DonatorPoints = 0;
    public boolean keyvalue_changed = false, changed_quest = false;
    public transient ScheduledFuture<?> SurPlusTask = null;
    private transient ScheduledFuture<?> DiaboliqueRecoveryTask = null;
    private transient ScheduledFuture<?> selfRecoveryTask = null;
    private transient CalcDamage calcDamage;
    private transient SpeedQuiz sq;
    private long lastSpeedQuiz;
    public long cooltime = 0, rankatt = 0;
    public int RandomHair, recommendchr, 귀찮음 = 0;
    private Pair<Integer, Integer> say;
    private static boolean status = false;
    private static int flow = 0;
    private long startChairTime;
    private byte nowChairCount;
    private transient MapleLieDetector antiMacro;

    private int PQReward;

    private long familyBuffExpDuration;
    private int familyBuffExpEffect;
    private long familyBuffDropDuration;
    private int familyBuffDropEffect;
    private long healBuffDuration;
    private long takeDamageDelay;
    private int pierreHat;
    private long traningDamage = 0;
    public long MulungTime;
    public int deathCount = 0;
    private long lUPTime;

    public List<MapleShopItem> shop_special_item = new LinkedList<MapleShopItem>();

    private MapleCharacter(final boolean ChannelServer) {
        setStance(0);
        setPosition(new Point(0, 0));

        inventory = new MapleInventory[MapleInventoryType.values().length];
        for (MapleInventoryType type : MapleInventoryType.values()) {
            inventory[type.ordinal()] = new MapleInventory(type);
        }
        quests = new LinkedHashMap<MapleQuest, MapleQuestStatus>(); // Stupid erev quest.
        skills = new LinkedHashMap<Skill, SkillEntry>(); //Stupid UAs.
        stats = new PlayerStats();
        innerSkills = new LinkedList<InnerSkillValueHolder>();
        azwanShopList = null;
        characterCard = new MapleCharacterCards();
        for (int i = 0; i < remainingSp.length; i++) {
            remainingSp[i] = 0;
        }
        traits = new EnumMap<MapleTraitType, MapleTrait>(MapleTraitType.class);
        for (MapleTraitType t : MapleTraitType.values()) {
            traits.put(t, new MapleTrait(t));
        }
        if (ChannelServer) {
            changed_reports = false;
            changed_skills = false;
            changed_achievements = false;
            changed_wishlist = false;
            changed_trocklocations = false;
            changed_regrocklocations = false;
            changed_hyperrocklocations = false;
            changed_skillmacros = false;
            changed_savedlocations = false;
            changed_extendedSlots = false;
            changed_questinfo = false;
            update_skillswipe = false;
            changed_quest = false;
            scrolledPosition = 0;
            lastCombo = 0;
            mulung_energy = 0;
            /*
             * START OF CUSTOM FEATURES
             */
            loginTime = 0;
            forgeEXP = 0;
            mSkillPoints = 0;
            /*
             * END OF CUSTOM FEATURES
             */
            combo = 0;
            force = 0;
            keydown_skill = 0;
            nextConsume = 0;
            pqStartTime = 0;
            fairyExp = 0;
            cardStack = 0;
            runningStack = 1;
            mapChangeTime = 0;
            lastRecoveryTime = 0;
            lastDragonBloodTime = 0;
            lastBerserkTime = 0;
            lastFishingTime = 0;
            lastFairyTime = 0;
            lastHPTime = 0;
            lastMPTime = 0;
            lastFamiliarEffectTime = 0;
            old = new Point(0, 0);
            coconutteam = 0;
            followid = 0;
            battleshipHP = 0;
            flipthecoin = 0;
            trinityCount = 0;
            rechargeCount = 0;
            powerTransfer = 1000;
            SurPlus = 0;
            rechargeMobCount = 0;
            marriageItemId = 0;
            fallcounter = 0;
            challenge = 0;
            dotHP = 0;
            lastSummonTime = 0;
            soul_stone = false;
            hasSummon = false;
            invincible = false;
            canTalk = true;
            clone = false;
            followinitiator = false;
            followon = false;
            rebuy = new ArrayList<Item>();
            linkMobs = new HashMap<Integer, Integer>();
            finishedAchievements = new ArrayList<Integer>();
            reports = new EnumMap<ReportType, Integer>(ReportType.class);
            teleportname = "";
            smega = true;
            petStore = new byte[3];
            for (int i = 0; i < petStore.length; i++) {
                petStore[i] = (byte) -1;
            }
            wishlist = new int[10];
            rocks = new int[10];
            regrocks = new int[5];
            hyperrocks = new int[13];
            imps = new MapleImp[3];
            clones = new WeakReference[5]; //for now
            for (int i = 0; i < clones.length; i++) {
                clones[i] = new WeakReference<MapleCharacter>(null);
            }
            extendedSlots = new ArrayList<Integer>();
            effects = new ConcurrentEnumMap<>(CharacterTemporaryStat.class);
            coolDowns = new LinkedHashMap<>();
            diseases = new ConcurrentEnumMap<>(MonsterSkill.class);
            MS_Mark = new EnumMap<>(MonsterSkill.class);
            MS_Poison = new EnumMap<>(MonsterSkill.class);
            MS_UserBomb = new EnumMap<>(MonsterSkill.class);
            inst = new AtomicInteger(0);// 1 = NPC/ Quest, 2 = Duey, 3 = Hired Merch store, 4 = Storage
            insd = new AtomicInteger(-1);
            keylayout = new MapleKeyLayout();
            doors = new ArrayList<MapleDoor>();
            mechDoors = new ArrayList<MechDoor>();
            summonlist = new ArrayList<MapleSummon>();
            controlled = new LinkedHashSet<MapleMonster>();
            controlledLock = new ReentrantReadWriteLock();
            summons = new LinkedList<MapleSummon>();
            summonsLock = new ReentrantReadWriteLock();
            visibleMapObjects = new LinkedHashSet<MapleMapObject>();
            visibleMapObjectsLock = new ReentrantReadWriteLock();
            pendingCarnivalRequests = new LinkedList<MapleCarnivalChallenge>();

            savedLocations = new int[SavedLocationType.values().length];
            for (int i = 0; i < SavedLocationType.values().length; i++) {
                savedLocations[i] = -1;
            }
            questinfo = new LinkedHashMap<Integer, String>();
            pets = new ArrayList<MaplePet>();
            friendshippoints = new int[4];
        }
    }

    public static MapleCharacter getDefault(final MapleClient client, final JobType type) {
        MapleCharacter ret = new MapleCharacter(false);
        ret.client = client;
        ret.map = null;
        ret.exp = 0;
        ret.gmLevel = 0;
        ret.job = (short) type.id;
        ret.meso = 0;
        ret.level = 1;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.buddylist = new BuddyList((byte) 20);

        ret.stats.str = 12;
        ret.stats.dex = 5;
        ret.stats.int_ = 4;
        ret.stats.luk = 4;
        ret.stats.maxhp = 50;
        ret.stats.hp = 50;
        ret.stats.maxmp = 50;
        ret.stats.mp = 50;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, ret.accountid);
            rs = ps.executeQuery();

            if (rs.next()) {
                ret.client.setAccountName(rs.getString("name"));
                ret.nxcredit = rs.getInt("nxCredit");
                ret.acash = rs.getInt("ACash");
                ret.maplepoints = rs.getInt("mPoints");
                ret.recommend = rs.getInt("recommend");
                ret.Fanclub = rs.getInt("Fanclub");
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return ret;
    }

    public final static MapleCharacter ReconstructChr(final CharacterTransfer ct, final MapleClient client, final boolean isChannel) {
        final MapleCharacter ret = new MapleCharacter(true); // Always true, it's change channel
        ret.client = client;
        if (!isChannel) {
            ret.client.setChannel(ct.channel);
        }
        ret.id = ct.characterid;
        ret.name = ct.name;
        ret.level = ct.level;
        ret.fame = ct.fame;

        ret.stats.str = ct.str;
        ret.stats.dex = ct.dex;
        ret.stats.int_ = ct.int_;
        ret.stats.luk = ct.luk;
        ret.stats.maxhp = ct.maxhp;
        ret.stats.maxmp = ct.maxmp;
        ret.stats.hp = ct.hp;
        ret.stats.mp = ct.mp;

        ret.characterCard.setCards(ct.cardsInfo);

        ret.chalktext = ct.chalkboard;
        ret.gmLevel = ct.gmLevel;
        ret.exp = ct.exp;
        ret.hpApUsed = ct.hpApUsed;
        ret.remainingSp = ct.remainingSp;
        ret.remainingAp = ct.remainingAp;
        ret.meso = ct.meso;
        ret.stolenSkills = ct.stolenSkills;
        ret.skinColor = ct.skinColor;
        ret.gender = ct.gender;
        ret.job = ct.job;
        ret.hair = ct.hair;
        ret.face = ct.face;
        ret.premium = ct.premium;
        ret.demonMarking = ct.demonMarking;
        ret.hcMode = ct.hcMode;
        ret.forgeEXP = ct.forgeEXP;
        ret.mSkillPoints = ct.mSkillPoints;
        ret.accountid = ct.accountid;
        ret.totalWins = ct.totalWins;
        ret.totalLosses = ct.totalLosses;
        client.setAccID(ct.accountid);
        ret.mapid = ct.mapid;
        ret.initialSpawnPoint = ct.initialSpawnPoint;
        ret.world = ct.world;
        ret.guildid = ct.guildid;
        ret.guildrank = ct.guildrank;
        ret.guildContribution = ct.guildContribution;
        ret.allianceRank = ct.alliancerank;
        ret.fairyExp = ct.fairyExp;
        ret.cardStack = ct.cardStack;
        ret.marriageId = ct.marriageId;
        ret.currentrep = ct.currentrep;
        ret.totalrep = ct.totalrep;
        ret.todayrep = ct.todayrep;
        ret.pvpExp = ct.pvpExp;
        ret.pvpPoints = ct.pvpPoints;
        ret.honourExp = ct.honourexp;
        ret.honourLevel = ct.honourlevel;
        ret.innerSkills = (LinkedList<InnerSkillValueHolder>) ct.innerSkills;
        ret.azwanShopList = (MapleShop) ct.azwanShopList;
        /*
         * Start of Custom Feature
         */
        ret.healTimer = ct.healTimer;
        ret.DFRecoveryTimer = ct.DFRecoveryTimer;
        ret.petAutoFeed = ct.petAutoFeed;

        ret.familyBuffExpDuration = ct.familyBuffExpDuration;
        ret.familyBuffExpEffect = ct.familyBuffExpEffect;
        ret.familyBuffDropDuration = ct.familyBuffDropDuration;
        ret.familyBuffDropEffect = ct.familyBuffDropEffect;
        ret.healBuffDuration = ct.healBuffDuration;
        ret.takeDamageDelay = ct.takeDamageDelay;

        ret.pierreHat = ct.pierreHat;
        ret.MulungTime = ct.MulungTime;
        ret.deathCount = ct.deathCount;

        ret.makeMFC(ct.familyid, ct.seniorid, ct.junior1, ct.junior2);
        if (ret.guildid > 0) {
            ret.mgc = new MapleGuildCharacter(ret);
        }
        ret.fatigue = ct.fatigue;
        ret.buddylist = new BuddyList(ct.buddysize);
        ret.subcategory = ct.subcategory;

        if (isChannel) {
            final MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
            ret.map = mapFactory.getMap(ret.mapid);
            //if (client.getChannel() == 10 || client.getChannel() == 11 || client.getChannel() == 12)
            //ret.map = mapFactory.getMap(272010200);
            if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
                ret.map = mapFactory.getMap(100000000);
            } else {
                if (ret.map.getForcedReturnId() != 999999999 && ret.map.getForcedReturnMap() != null) {
                    ret.map = ret.map.getForcedReturnMap();
                }
            }
            MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
            if (portal == null) {
                portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                ret.initialSpawnPoint = 0;
            }
            ret.setPosition(portal.getPosition());

            final int messengerid = ct.messengerid;
            if (messengerid > 0) {
                ret.messenger = World.Messenger.getMessenger(messengerid);
            }
        } else {

            ret.messenger = null;
        }
        int partyid = ct.partyid;
        if (partyid >= 0) {
            MapleParty party = World.Party.getParty(partyid);
            if (party != null && party.getMemberById(ret.id) != null) {
                ret.party = party;
            }
        }

        MapleQuestStatus queststatus_from;
        for (final Map.Entry<Integer, Object> qs : ct.Quest.entrySet()) {
            queststatus_from = (MapleQuestStatus) qs.getValue();
            queststatus_from.setQuest(qs.getKey());
            ret.quests.put(queststatus_from.getQuest(), queststatus_from);
        }
        for (final Map.Entry<Integer, SkillEntry> qs : ct.Skills.entrySet()) {
            ret.skills.put(SkillFactory.getSkill(qs.getKey()), qs.getValue());
        }
        for (final Integer zz : ct.finishedAchievements) {
            ret.finishedAchievements.add(zz);
        }
        for (Entry<MapleTraitType, Integer> t : ct.traits.entrySet()) {
            ret.traits.get(t.getKey()).setExp(t.getValue());
        }
        ret.todaycharisma = ct.todaycharisma;
        ret.todayinsight = ct.todayinsight;
        ret.todaywill = ct.todaywill;
        ret.todaycraft = ct.todaycraft;
        ret.todaysense = ct.todaysense;
        ret.todaycharm = ct.todaycharm;
        ret.traningDamage = ct.traningDamage;
        for (final Map.Entry<Byte, Integer> qs : ct.reports.entrySet()) {
            ret.reports.put(ReportType.getById(qs.getKey()), qs.getValue());
        }
        ret.monsterbook = new MonsterBook(ct.mbook, ret);
        ret.inventory = (MapleInventory[]) ct.inventorys;
        ret.BlessOfFairy_Origin = ct.BlessOfFairy;
        ret.BlessOfEmpress_Origin = ct.BlessOfEmpress;
        ret.skillMacros = (SkillMacro[]) ct.skillmacro;
        ret.petz = ct.petz;
        ret.petStore = ct.petStore;
        ret.keylayout = new MapleKeyLayout(ct.keymap);
        ret.questinfo = ct.InfoQuest;
        ret.savedLocations = ct.savedlocation;
        ret.wishlist = ct.wishlist;
        ret.rocks = ct.rocks;
        ret.regrocks = ct.regrocks;
        ret.hyperrocks = ct.hyperrocks;
        ret.buddylist.loadFromTransfer(ct.buddies); // 확인
        ret.keydown_skill = 0; // Keydown skill can't be brought over
        ret.lastfametime = ct.lastfametime;
        ret.lastmonthfameids = ct.famedcharacters;
        ret.lastmonthbattleids = ct.battledaccs;
        ret.extendedSlots = ct.extendedSlots;
        ret.storage = (MapleStorage) ct.storage;
        ret.cs = (CashShop) ct.cs;
        client.setAccountName(ct.accountname);
        ret.nxcredit = ct.nxCredit;
        ret.acash = ct.ACash;
        ret.maplepoints = ct.MaplePoints;
        ret.recommend = ct.recommend;
        ret.Fanclub = ct.Fanclub;
        ret.headtitle = ct.headtitle;
        ret.Roulette_Item = ct.Roulette_Item;
        ret.imps = ct.imps;
        ret.anticheat = (CheatTracker) ct.anticheat;
        ret.anticheat.start(ret);
        ret.rebuy = ct.rebuy;
        ret.mount = new MapleMount(ret, ct.mount_itemid, 80001000, ct.mount_Fatigue, ct.mount_level, ct.mount_exp);
        ret.expirationTask(false, false);
        ret.stats.recalcLocalStats(true, ret, false);
        ret.isFinalFiguration = ct.isFinalFiguration;
        ret.glass_minusMorph = ct.glass_minusMorph;
        ret.glass_plusMorph = ct.glass_plusMorph;
        ret.dressup = ct.dressup;
        ret.exeedCount = ct.exeedCount;
        ret.exeedAttackCount = ct.exeedAttackCount;
        ret.arcaneAim = ct.arcaneAim;
        ret.calcDamage = new CalcDamage();
        ret.flipthecoin = ct.flipthecoin;
        ret.trinityCount = ct.trinityCount;
        ret.rechargeCount = ct.rechargeCount;
        ret.powerTransfer = ct.powerTransfer;
        ret.SurPlus = ct.SurPlus;
        ret.rechargeMobCount = ct.rechargeMobCount;
        client.setTempIP(ct.tempIP);
        ret.antiMacro = new MapleLieDetector(ret);
        return ret;
    }

    public static MapleCharacter loadCharFromDB(int charid, MapleClient client, boolean channelserver) {
        return loadCharFromDB(charid, client, channelserver, null);
    }

    public static MapleCharacter loadCharFromDB(int charid, MapleClient client, boolean channelserver, final Map<Integer, CardData> cads) {
        final MapleCharacter ret = new MapleCharacter(channelserver);
        ret.client = client;
        ret.id = charid;
        ret.calcDamage = new CalcDamage();
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                con.close();
                throw new RuntimeException("Loading the Char Failed (char not found)");
            }
            ret.name = rs.getString("name");
            ret.level = rs.getShort("level");
            ret.fame = rs.getInt("fame");

            ret.stats.str = rs.getShort("str");
            ret.stats.dex = rs.getShort("dex");
            ret.stats.int_ = rs.getShort("int");
            ret.stats.luk = rs.getShort("luk");
            ret.stats.maxhp = rs.getInt("maxhp");
            ret.stats.maxmp = rs.getInt("maxmp");
            ret.stats.hp = rs.getInt("hp");
            ret.stats.mp = rs.getInt("mp");
            ret.job = rs.getShort("job");
            ret.gmLevel = rs.getByte("gm");
            ret.exp = rs.getInt("exp");
            ret.hpApUsed = rs.getShort("hpApUsed");
            final String[] sp = rs.getString("sp").split(",");
            for (int i = 0; i < ret.remainingSp.length; i++) {
                ret.remainingSp[i] = Integer.parseInt(sp[i]);
            }
            ret.remainingAp = rs.getShort("ap");
            ret.meso = rs.getInt("meso");
            ret.skinColor = rs.getByte("skincolor");
            ret.gender = rs.getByte("gender");

            ret.hair = rs.getInt("hair");
            ret.face = rs.getInt("face");
            ret.demonMarking = rs.getInt("demonMarking");
            ret.hcMode = rs.getShort("hcMode");
            ret.forgeEXP = rs.getInt("forgeEXP");
            ret.mSkillPoints = rs.getInt("mSkillPoints");
            ret.accountid = rs.getInt("accountid");
            client.setAccID(ret.accountid);
            ret.mapid = rs.getInt("map");
            ret.initialSpawnPoint = rs.getByte("spawnpoint");
            ret.world = rs.getByte("world");
            ret.guildid = rs.getInt("guildid");
            ret.guildrank = rs.getByte("guildrank");
            ret.allianceRank = rs.getByte("allianceRank");
            ret.guildContribution = rs.getInt("guildContribution");
            ret.totalWins = rs.getInt("totalWins");
            ret.totalLosses = rs.getInt("totalLosses");
            ret.currentrep = rs.getInt("currentrep");
            ret.totalrep = rs.getInt("totalrep");
            ret.todayrep = rs.getInt("todayrep");
            ret.makeMFC(rs.getInt("familyid"), rs.getInt("seniorid"), rs.getInt("junior1"), rs.getInt("junior2"));
            if (ret.guildid > 0) {
                ret.mgc = new MapleGuildCharacter(ret);
            }
            ret.buddylist = new BuddyList(rs.getByte("buddyCapacity"));
            ret.subcategory = rs.getByte("subcategory");
            ret.mount = new MapleMount(ret, 0, 80001000, (byte) 0, (byte) 1, 0);
            ret.rank = rs.getInt("rank");
            ret.rankMove = rs.getInt("rankMove");
            ret.jobRank = rs.getInt("jobRank");
            ret.jobRankMove = rs.getInt("jobRankMove");
            ret.marriageId = rs.getInt("marriageId");
            ret.fatigue = rs.getShort("fatigue");
            ret.pvpExp = rs.getInt("pvpExp");
            ret.pvpPoints = rs.getInt("pvpPoints");
            ret.chatType = rs.getInt("chatType");
            ret.honourExp = rs.getInt("honourExp");
            ret.honourLevel = rs.getInt("honourLevel");
            ret.azwanCoinsAvail = rs.getInt("azwanCoinsAvail");
            ret.azwanCoinsRedeemed = rs.getInt("azwanCoinsRedeemed");
            ret.azwanECoinsAvail = rs.getInt("azwanECoinsAvail");
            ret.azwanCCoinsAvail = rs.getInt("azwanCCoinsAvail");
            ret.todaycharm = rs.getInt("todaycharm");
            ret.todaycraft = rs.getInt("todaycraft");
            ret.todaycharisma = rs.getInt("todaycharisma");
            ret.todaywill = rs.getInt("todaywill");
            ret.todaysense = rs.getInt("todaysense");
            ret.todayinsight = rs.getInt("todayinsight");
            ret.traningDamage = rs.getLong("traningDamage");
            for (MapleTrait t : ret.traits.values()) {
                t.setExp(rs.getInt(t.getType().name()));
            }
            if (channelserver) {
                ret.anticheat = new CheatTracker(ret);
                MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
                ret.map = mapFactory.getMap(ret.mapid);
                if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
                    ret.map = mapFactory.getMap(100000000);
                }
                MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
                if (portal == null) {
                    portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                    ret.initialSpawnPoint = 0;
                }
                ret.setPosition(portal.getPosition());

                int partyid = rs.getInt("party");
                if (partyid >= 0) {
                    MapleParty party = World.Party.getParty(partyid);
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                }
                final String[] pets = rs.getString("pets").split(",");
                for (int i = 0; i < ret.petStore.length; i++) {
                    ret.petStore[i] = Byte.parseByte(pets[i]);
                }
                final String[] friendshippoints = rs.getString("friendshippoints").split(",");
                for (int i = 0; i < 4; i++) {
                    ret.friendshippoints[i] = Integer.parseInt(friendshippoints[i]);
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM achievements WHERE accountid = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.finishedAchievements.add(rs.getInt("achievementid"));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM reports WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (ReportType.getById(rs.getByte("type")) != null) {
                        ret.reports.put(ReportType.getById(rs.getByte("type")), rs.getInt("count"));
                    }
                }

            }
            rs.close();
            ps.close();

            if (cads != null) { // so that we load only once.
                ret.characterCard.setCards(cads);
            } else { // load
                ret.characterCard.loadCards(client, channelserver);
            }

            ps = con.prepareStatement("SELECT * FROM queststatus WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            pse = con.prepareStatement("SELECT * FROM queststatusmobs WHERE queststatusid = ?");
            while (rs.next()) {
                final int id = rs.getInt("quest");
                final MapleQuest q = MapleQuest.getInstance(id);
                final byte stat = rs.getByte("status");
                if ((stat == 1 || stat == 2) && channelserver && (q == null || q.isBlocked())) { //bigbang
                    continue;
                }
                if (stat == 1 && channelserver && !q.canStart(ret, null)) { //bigbang
                    continue;
                }
                final MapleQuestStatus status = new MapleQuestStatus(q, stat);
                final long cTime = rs.getLong("time");
                if (cTime > -1) {
                    status.setCompletionTime(cTime * 1000);
                }
                status.setForfeited(rs.getInt("forfeited"));
                status.setCustomData(rs.getString("customData"));
                ret.quests.put(q, status);
                pse.setInt(1, rs.getInt("queststatusid"));
                final ResultSet rsMobs = pse.executeQuery();

                while (rsMobs.next()) {
                    status.setMobKills(rsMobs.getInt("mob"), rsMobs.getInt("count"));
                }
                rsMobs.close();
            }
            rs.close();
            pse.close();
            ps.close();
            if (channelserver) {
                ret.monsterbook = MonsterBook.loadCards(ret.accountid, ret);
                ps = con.prepareStatement("SELECT * FROM inventoryslot where characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    throw new RuntimeException("No Inventory slot column found in SQL. [inventoryslot]");
                } else {
                    ret.getInventory(MapleInventoryType.EQUIP).setSlotLimit(rs.getByte("equip"));
                    ret.getInventory(MapleInventoryType.USE).setSlotLimit(rs.getByte("use"));
                    ret.getInventory(MapleInventoryType.SETUP).setSlotLimit(rs.getByte("setup"));
                    ret.getInventory(MapleInventoryType.ETC).setSlotLimit(rs.getByte("etc"));
                    ret.getInventory(MapleInventoryType.CASH).setSlotLimit(rs.getByte("cash"));
                }
                rs.close();
                ps.close();

                for (Pair<Item, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(false, charid).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                    if (mit.getLeft().getPet() != null) {
                        ret.pets.add(mit.getLeft().getPet());
                    }
                }
                ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                if (rs.next()) {
                    ret.getClient().setAccountName(rs.getString("name"));
                    ret.nxcredit = rs.getInt("nxCredit");
                    ret.acash = rs.getInt("ACash");
                    ret.maplepoints = rs.getInt("mPoints");
                    ret.recommend = rs.getInt("recommend");
                    ret.Fanclub = rs.getInt("Fanclub");
                    ret.premium = rs.getTimestamp("premium");
                    if (rs.getTimestamp("lastlogon") != null) {
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(rs.getTimestamp("lastlogon").getTime());
                    }
                    if (rs.getInt("banned") > 0) {
                        rs.close();
                        ps.close();
                        ret.getClient().getSession().close();
                        throw new RuntimeException("Loading a banned character");
                    }
                    rs.close();
                    ps.close();

                    ps = con.prepareStatement("UPDATE accounts SET lastlogon = CURRENT_TIMESTAMP() WHERE id = ?");
                    ps.setInt(1, ret.accountid);
                    ps.executeUpdate();
                } else {
                    rs.close();
                }
                ps.close();

                ps = con.prepareStatement("SELECT * FROM questinfo WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.questinfo.put(rs.getInt("quest"), rs.getString("customData"));
                }
                rs.close();
                ps.close();
                /*
                ps = con.prepareStatement("SELECT * FROM questinfo_acc WHERE accountid = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.questinfo.put(rs.getInt("quest"), rs.getString("customData"));
                }
                rs.close();
                ps.close();
                 */
                ps = con.prepareStatement("SELECT skillid, skilllevel, masterlevel, expiration FROM skills WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                Skill skil;
                while (rs.next()) {
                    final int skid = rs.getInt("skillid");
                    skil = SkillFactory.getSkill(skid);
                    int skl = rs.getInt("skilllevel");
                    byte msl = rs.getByte("masterlevel");

                    if (skil != null && GameConstants.isApplicableSkill(skid)) {
                        if (skl > skil.getMaxLevel() && skid < 92000000) {
                            if (!skil.isBeginnerSkill() && skil.canBeLearnedBy(ret.job) && !skil.isSpecialSkill()) {
                                ret.remainingSp[GameConstants.getSkillBookForSkill(skid)] += (skl - skil.getMaxLevel());
                            }
                            skl = (byte) skil.getMaxLevel();
                        }
                        if (msl > skil.getMaxLevel()) {
                            msl = (byte) skil.getMaxLevel();
                        }
                        ret.skills.put(skil, new SkillEntry(skl, msl, rs.getLong("expiration")));
                    } else if (skil == null) { //doesnt. exist. e.g. bb
                        if (!GameConstants.isBeginnerJob(skid / 10000) && skid / 10000 != 900 && skid / 10000 != 800 && skid / 10000 != 9000) {
                            ret.remainingSp[GameConstants.getSkillBookForSkill(skid)] += skl;
                        }
                    }
                }
                rs.close();
                ps.close();
                ret.expirationTask(false, true); //do it now
                // Bless of Fairy handling
                ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ? ORDER BY level DESC");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                int maxlevel_ = 0, maxlevel_2 = 0;
                while (rs.next()) {
                    if (rs.getInt("id") != charid) { // Not this character
                        if (GameConstants.isKOC(rs.getShort("job")) || GameConstants.isMihile(rs.getShort("job"))) {
                            int maxlevel = (rs.getShort("level") / 5);

                            if (maxlevel > 24) {
                                maxlevel = 24;
                            }
                            if (maxlevel > maxlevel_2 || maxlevel_2 == 0) {
                                maxlevel_2 = maxlevel;
                                ret.BlessOfEmpress_Origin = rs.getString("name");
                            }
                        }
                        int maxlevel = (rs.getShort("level") / 10);

                        if (maxlevel > 20) {
                            maxlevel = 20;
                        }
                        if (maxlevel > maxlevel_ || maxlevel_ == 0) {
                            maxlevel_ = maxlevel;
                            ret.BlessOfFairy_Origin = rs.getString("name");
                        }

                    }
                }
                if (ret.BlessOfFairy_Origin == null) {
                    ret.BlessOfFairy_Origin = ret.name;
                }
                ret.skills.put(SkillFactory.getSkill(GameConstants.getBOF_ForJob(ret.job)), new SkillEntry(maxlevel_, (byte) 0, -1));
                if (SkillFactory.getSkill(GameConstants.getEmpress_ForJob(ret.job)) != null) {
                    if (ret.BlessOfEmpress_Origin == null) {
                        ret.BlessOfEmpress_Origin = ret.BlessOfFairy_Origin;
                    }
                    ret.skills.put(SkillFactory.getSkill(GameConstants.getEmpress_ForJob(ret.job)), new SkillEntry(maxlevel_2, (byte) 0, -1));
                }
                rs.close();
                ps.close();
                // END
                ps = con.prepareStatement("SELECT skill_id, skill_level, max_level, rank FROM inner_ability_skills WHERE player_id = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.innerSkills.add(new InnerSkillValueHolder(rs.getInt("skill_id"), rs.getByte("skill_level"), rs.getByte("max_level"), rs.getByte("rank")));
                }
                ps = con.prepareStatement("SELECT * FROM skillmacros WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int position;
                while (rs.next()) {
                    position = rs.getInt("position");
                    SkillMacro macro = new SkillMacro(rs.getInt("skill1"), rs.getInt("skill2"), rs.getInt("skill3"), rs.getString("name"), rs.getInt("shout"), position);
                    ret.skillMacros[position] = macro;
                }
                rs.close();
                ps.close();
                /*
                 * ps = con.prepareStatement("SELECT victimid, skillid,
                 * skilllevel, slot, category FROM stolen_skills WHERE chrid =
                 * ?"); ps.setInt(1, charid); rs = ps.executeQuery(); int slot;
                 * while (rs.next()) { slot = rs.getInt("slot"); SkillSwipe ss =
                 * new SkillSwipe(rs.getInt("victimid"), rs.getInt("skillid"),
                 * rs.getInt("skilllevel"), rs.getInt("category"), slot);
                 * ret.skillSwipe[slot] = ss; } rs.close(); ps.close();
                 */

                ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                final Map<Integer, Pair<Byte, Integer>> keyb = ret.keylayout.Layout();
                while (rs.next()) {
                    keyb.put(Integer.valueOf(rs.getInt("key")), new Pair<Byte, Integer>(rs.getByte("type"), rs.getInt("action")));
                }
                rs.close();
                ps.close();
                ret.keylayout.unchanged();

                ps = con.prepareStatement("SELECT `locationtype`,`map` FROM savedlocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.savedLocations[rs.getInt("locationtype")] = rs.getInt("map");
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                ret.lastfametime = 0;
                ret.lastmonthfameids = new ArrayList<Integer>(31);
                while (rs.next()) {
                    ret.lastfametime = Math.max(ret.lastfametime, rs.getTimestamp("when").getTime());
                    ret.lastmonthfameids.add(Integer.valueOf(rs.getInt("characterid_to")));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT `accid_to`,`when` FROM battlelog WHERE accid = ? AND DATEDIFF(NOW(),`when`) < 30");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                ret.lastmonthbattleids = new ArrayList<Integer>();
                while (rs.next()) {
                    ret.lastmonthbattleids.add(Integer.valueOf(rs.getInt("accid_to")));
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT `itemId` FROM extendedSlots WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.extendedSlots.add(Integer.valueOf(rs.getInt("itemId")));
                }
                rs.close();
                ps.close();

                ret.buddylist.loadFromDb(charid);
                ret.storage = MapleStorage.loadStorage(ret.accountid);
                ret.cs = new CashShop(ret.accountid, charid, ret.getJob());
                ps = con.prepareStatement("SELECT sn FROM wishlist WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int i = 0;
                while (rs.next()) {
                    ret.wishlist[i] = rs.getInt("sn");
                    i++;
                }
                while (i < 10) {
                    ret.wishlist[i] = 0;
                    i++;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT mapid FROM trocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int r = 0;
                while (rs.next()) {
                    ret.rocks[r] = rs.getInt("mapid");
                    r++;
                }
                while (r < 10) {
                    ret.rocks[r] = 999999999;
                    r++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT mapid FROM regrocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                r = 0;
                while (rs.next()) {
                    ret.regrocks[r] = rs.getInt("mapid");
                    r++;
                }
                while (r < 5) {
                    ret.regrocks[r] = 999999999;
                    r++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT mapid FROM hyperrocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                r = 0;
                while (rs.next()) {
                    ret.hyperrocks[r] = rs.getInt("mapid");
                    r++;
                }
                while (r < 13) {
                    ret.hyperrocks[r] = 999999999;
                    r++;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * from stolen WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.stolenSkills.add(new Pair<Integer, Boolean>(rs.getInt("skillid"), rs.getInt("chosen") > 0));
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * FROM imps WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                r = 0;
                while (rs.next()) {
                    ret.imps[r] = new MapleImp(rs.getInt("itemid"));
                    ret.imps[r].setLevel(rs.getByte("level"));
                    ret.imps[r].setState(rs.getByte("state"));
                    ret.imps[r].setCloseness(rs.getShort("closeness"));
                    ret.imps[r].setFullness(rs.getShort("fullness"));
                    r++;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * FROM mountdata WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    con.close();
                    throw new RuntimeException("No mount data found on SQL column");
                }
                final Item mount = ret.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
                ret.mount = new MapleMount(ret, mount != null ? mount.getItemId() : 0, 80001000, rs.getByte("Fatigue"), rs.getByte("Level"), rs.getInt("Exp"));
                rs.close();
                ps.close();
                ret.stats.recalcLocalStats(true, ret, false);
                ret.antiMacro = new MapleLieDetector(ret);
                con.close();
            } else { // Not channel server
                for (Pair<Item, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(true, charid).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                }
                ret.stats.recalcPVPRank(ret);
                rs.close();
                pse.close();
                ps.close();
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (pse != null) {
                    pse.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return ret;
    }

    public static void saveNewCharToDB(final MapleCharacter chr, final JobType type, short db) {
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);

            ps = con.prepareStatement("INSERT INTO characters (level, str, dex, luk, `int`, hp, mp, maxhp, maxmp, sp, ap, skincolor, gender, job, hair, face, demonMarking, map, meso, party, buddyCapacity, pets, subcategory, accountid, name, world, chatType, hcMode, forgeEXP, mSkillPoints, honourExp, honourLevel, friendshippoints, azwanCoinsAvail, azwanCoinsRedeemed, azwanECoinsAvail, azwanCCoinsAvail, craft, insight, charisma, will, traningDamage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS);
            ps.setInt(1, type.type >= 19 ? 10 : chr.level);
            final PlayerStats stat = chr.stats;
            ps.setInt(2, type.type >= 19 ? 54 : stat.getStr()); // Str
            ps.setInt(3, type.type >= 19 ? 4 : stat.getDex()); // Dex
            ps.setInt(4, type.type >= 19 ? 4 : stat.getInt()); // Luk
            ps.setInt(5, type.type >= 19 ? 4 : stat.getLuk()); // Int
            ps.setInt(6, type.type >= 19 ? 300 : stat.getHp()); // HP
            ps.setInt(7, type.type >= 19 ? 200 : stat.getMp());
            ps.setInt(8, type.type >= 19 ? 300 : stat.getMaxHp()); // MP
            ps.setInt(9, type.type >= 19 ? 200 : stat.getMaxMp());
            final StringBuilder sps = new StringBuilder();
            for (int i = 0; i < chr.remainingSp.length; i++) {
                sps.append(chr.remainingSp[i]);
                sps.append(",");
            }
            final String sp = sps.toString();
            if (type.type >= 19) {
                ps.setString(10, "3,0,0,0,0,0,0,0,0,0");
            } else {
                ps.setString(10, sp.substring(0, sp.length() - 1));
            }
            ps.setShort(11, (short) chr.remainingAp); // Remaining AP
            ps.setByte(12, chr.skinColor);
            ps.setByte(13, chr.gender);
            ps.setInt(14, chr.job);
            ps.setInt(15, chr.hair);
            ps.setInt(16, chr.face);
            ps.setInt(17, chr.demonMarking);
            if (db < 0 || db > 10) { //todo legend
                db = 0; //What is this, subcategory?
            }
            if (type == type.DualBlade) {
                ps.setInt(18, type.map);
            } else {
                ps.setInt(18, db == 2 ? 3000600 : type.map);
            }
            ps.setInt(19, chr.meso); // Meso
            ps.setInt(20, -1); // Party
            ps.setByte(21, chr.buddylist.getCapacity()); // Buddylist
            ps.setString(22, "-1,-1,-1");
            if (type == type.DualBlade) {
                ps.setInt(23, 1);
            } else {
                ps.setInt(23, db); //for now
            }
            ps.setInt(24, chr.getAccountID());
            ps.setString(25, chr.name);
            ps.setByte(26, chr.world);
            ps.setInt(27, -3);//chatType defaulting to Normal Chat
            ps.setInt(28, chr.hcMode); //Heartcore flag
            ps.setInt(29, chr.forgeEXP); //Star Forge EXP
            ps.setInt(30, chr.mSkillPoints); //Master Skill Points
            ps.setInt(31, chr.honourExp); //Honour Exp.
            ps.setInt(32, chr.honourLevel); //Honour Level;
            ps.setString(33, chr.friendshippoints[0] + "," + chr.friendshippoints[1] + "," + chr.friendshippoints[2] + "," + chr.friendshippoints[3]);
            ps.setInt(34, chr.azwanCoinsAvail);
            ps.setInt(35, chr.azwanCoinsRedeemed);
            ps.setInt(36, chr.azwanECoinsAvail);
            ps.setInt(37, chr.azwanCCoinsAvail);
            ps.setInt(38, type.type == 7 ? 4563 : 0); // 손재주 20 레벨 (팬텀 : 하이 덱스터러티)
            ps.setInt(39, type.type == 7 ? 4563 : 0); // 통찰력 20 레벨 (팬텀 : 하이 덱스터러티)
            ps.setInt(40, type.type == 6 ? 4563 : 0); // 의지 20 레벨 (데몬 슬레이어 : 데모닉 블러드)
            ps.setInt(41, type.type == 6 ? 4563 : 0); // 카리스마 20 레벨 (데몬 슬레이어 : 데모닉 블러드)
            ps.setLong(42, chr.traningDamage);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                chr.id = rs.getInt(1);
            } else {
                rs.close();
                ps.close();
                throw new DatabaseException("Inserting char failed.");
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (final MapleQuestStatus q : chr.quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus());
                ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.execute();
                rs = ps.getGeneratedKeys();
                if (q.hasMobKills()) {
                    rs.next();
                    for (int mob : q.getMobKills().keySet()) {
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q.getMobKills(mob));
                        pse.execute();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();
            ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (final Entry<Skill, SkillEntry> skill : chr.skills.entrySet()) {
                if (GameConstants.isApplicableSkill(skill.getKey().getId())) { //do not save additional skills
                    ps.setInt(2, skill.getKey().getId());
                    ps.setInt(3, skill.getValue().skillevel);
                    ps.setByte(4, skill.getValue().masterlevel);
                    ps.setLong(5, skill.getValue().expiration);
                    ps.execute();
                }
            }
            ps.close();
            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 32); // Eq
            ps.setByte(3, (byte) 32); // Use
            ps.setByte(4, (byte) 32); // Setup
            ps.setByte(5, (byte) 32); // ETC
            ps.setByte(6, (byte) 60); // Cash
            ps.execute();
            ps.close();
            ps = con.prepareStatement("INSERT INTO mountdata (characterid, `Level`, `Exp`, `Fatigue`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 1);
            ps.setInt(3, 0);
            ps.setByte(4, (byte) 0);
            ps.execute();
            ps.close();
            final int[] array1 = {2, 3, 64, 4, 65, 5, 6, 7, 17, 16, 19, 18, 21, 20, 23, 22, 25, 24, 27, 26, 29, 31, 34, 35, 33, 38, 39, 37, 43, 40, 41, 46, 47, 44, 45, 51, 50, 48, 59, 57, 56, 63, 62, 61, 60};
            final int[] array2 = {4, 4, 6, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 6, 5, 5, 6, 6, 6, 6};
            final int[] array3 = {10, 12, 105, 13, 106, 18, 23, 28, 5, 8, 4, 0, 30, 27, 1, 32, 19, 24, 15, 14, 52, 2, 17, 11, 25, 20, 26, 3, 9, 16, 22, 6, 31, 50, 51, 33, 7, 29, 100, 54, 53, 104, 103, 102, 101};

            ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (int i = 0; i < array1.length; i++) {
                ps.setInt(2, array1[i]);
                ps.setInt(3, array2[i]);
                ps.setInt(4, array3[i]);
                ps.execute();
            }
            ps.close();

            List<Pair<Item, MapleInventoryType>> listing = new ArrayList<Pair<Item, MapleInventoryType>>();
            for (final MapleInventory iv : chr.inventory) {
                for (final Item item : iv.list()) {
                    listing.add(new Pair<Item, MapleInventoryType>(item, iv.getType()));
                }
            }
            ItemLoader.INVENTORY.saveItems(listing, con, chr.id);

            con.commit();
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
            e.printStackTrace();
            System.err.println("[charsave] Error saving character data");
            try {
                con.rollback();
            } catch (SQLException ex) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
                ex.printStackTrace();
                System.err.println("[charsave] Error Rolling Back");
            }
        } finally {
            try {
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                e.printStackTrace();
                System.err.println("[charsave] Error going back to autocommit mode");
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (pse != null) {
                    pse.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
    }

    public void saveToDB(boolean dc, boolean fromcs) {
        if (remainingAp > 99999) {
            remainingAp = 0;
        }
        if (isClone()) {
            return;
        }
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);
            try {
                keylayout.saveKeys(id);
                mount.saveMount(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ps = con.prepareStatement("UPDATE characters SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, exp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, ap = ?, gm = ?, skincolor = ?, gender = ?, job = ?, hair = ?, face = ?, demonMarking = ?, map = ?, meso = ?, hpApUsed = ?, spawnpoint = ?, party = ?, buddyCapacity = ?, pets = ?, subcategory = ?, marriageId = ?, currentrep = ?, totalrep = ?, fatigue = ?, charm = ?, craft = ?, charisma = ?, insight = ?, sense = ?, will = ?, totalwins = ?, totallosses = ?, pvpExp = ?, pvpPoints = ?, name = ?, chatType = ?, hcMode = ?, forgeEXP = ?, mSkillPoints = ?,honourExp = ?, honourLevel = ?, friendshippoints = ?, azwanCoinsAvail = ?, azwanCoinsRedeemed = ?, azwanECoinsAvail = ?, azwanCCoinsAvail = ?, todaycharm = ?, todaycraft = ?, todaycharisma = ?, todaywill = ?, todaysense = ?, todayinsight = ?, todayrep = ?, traningDamage = ?  WHERE id = ?", DatabaseConnection.RETURN_GENERATED_KEYS);
            ps.setInt(1, level);
            ps.setInt(2, fame);
            ps.setInt(3, stats.getStr());
            ps.setInt(4, stats.getDex());
            ps.setInt(5, stats.getLuk());
            ps.setInt(6, stats.getInt());
            ps.setInt(7, exp);
            ps.setInt(8, stats.getHp() < 1 ? 50 : stats.getHp());
            ps.setInt(9, stats.getMp());
            ps.setInt(10, stats.getMaxHp());
            ps.setInt(11, stats.getMaxMp());
            final StringBuilder sps = new StringBuilder();
            for (int i = 0; i < remainingSp.length; i++) {
                sps.append(remainingSp[i]);
                sps.append(",");
            }
            final String sp = sps.toString();
            ps.setString(12, sp.substring(0, sp.length() - 1));
            ps.setInt(13, remainingAp);
            ps.setByte(14, gmLevel);
            ps.setByte(15, skinColor);
            ps.setByte(16, gender);
            ps.setInt(17, job);
            ps.setInt(18, hair);
            ps.setInt(19, face);
            ps.setInt(20, demonMarking);
            if (!fromcs && map != null) {
                if (map.getForcedReturnId() != 999999999 && map.getForcedReturnMap() != null) {
                    ps.setInt(21, map.getForcedReturnId());
                } else {
                    ps.setInt(21, stats.getHp() < 1 ? map.getReturnMapId() : map.getId());
                }
            } else {
                ps.setInt(21, mapid);
            }
            ps.setInt(22, meso);
            ps.setShort(23, hpApUsed);
            if (map == null) {
                ps.setByte(24, (byte) 0);
            } else {
                final MaplePortal closest = map.findClosestSpawnpoint(getTruePosition());
                ps.setByte(24, (byte) (closest != null ? closest.getId() : 0));
            }
            ps.setInt(25, party == null ? -1 : party.getId());
            ps.setShort(26, buddylist.getCapacity());
            final StringBuilder petz = new StringBuilder();
            int petLength = 0;
            for (final MaplePet pet : pets) {
                if (pet.getSummoned()) {
                    pet.saveToDb();
                    petz.append(pet.getInventoryPosition());
                    petz.append(",");
                    petLength++;
                }
            }
            while (petLength < 3) {
                petz.append("-1,");
                petLength++;
            }
            final String petstring = petz.toString();
            ps.setString(27, petstring.substring(0, petstring.length() - 1));
            ps.setByte(28, subcategory);
            ps.setInt(29, marriageId);
            ps.setInt(30, currentrep);
            ps.setInt(31, totalrep);
            ps.setShort(32, fatigue);
            ps.setInt(33, traits.get(MapleTraitType.charm).getTotalExp());
            ps.setInt(34, traits.get(MapleTraitType.craft).getTotalExp());
            ps.setInt(35, traits.get(MapleTraitType.charisma).getTotalExp());
            ps.setInt(36, traits.get(MapleTraitType.insight).getTotalExp());
            ps.setInt(37, traits.get(MapleTraitType.sense).getTotalExp());
            ps.setInt(38, traits.get(MapleTraitType.will).getTotalExp());
            ps.setInt(39, totalWins);
            ps.setInt(40, totalLosses);
            ps.setInt(41, pvpExp);
            ps.setInt(42, pvpPoints);
            ps.setString(43, name);
            ps.setInt(44, chatType);
            ps.setInt(45, hcMode);
            ps.setInt(46, forgeEXP);
            ps.setInt(47, mSkillPoints);
            ps.setInt(48, honourExp);
            ps.setInt(49, honourLevel);
            ps.setString(50, friendshippoints[0] + "," + friendshippoints[1] + "," + friendshippoints[2] + "," + friendshippoints[3]);
            ps.setInt(51, azwanCoinsAvail);
            ps.setInt(52, azwanCoinsRedeemed);
            ps.setInt(53, azwanECoinsAvail);
            ps.setInt(54, azwanCCoinsAvail);
            ps.setInt(55, todaycharm);
            ps.setInt(56, todaycraft);
            ps.setInt(57, todaycharisma);
            ps.setInt(58, todaywill);
            ps.setInt(59, todaysense);
            ps.setInt(60, todayinsight);
            ps.setInt(61, todayrep);
            ps.setLong(62, traningDamage);
            ps.setInt(63, id);
            if (ps.executeUpdate() < 1) {
                ps.close();
                con.close();
                throw new DatabaseException("Character not in database (" + id + ")");
            }
            ps.close();
            deleteWhereCharacterId(con, "DELETE FROM stolen WHERE characterid = ?");
            for (Pair<Integer, Boolean> st : stolenSkills) {
                ps = con.prepareStatement("INSERT INTO stolen (characterid, skillid, chosen) VALUES (?, ?, ?)");
                ps.setInt(1, id);
                ps.setInt(2, st.left);
                ps.setInt(3, st.right ? 1 : 0);
                ps.execute();
                ps.close();
            }
            if (changed_skillmacros) {
                deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?");
                for (int i = 0; i < 5; i++) {
                    final SkillMacro macro = skillMacros[i];
                    if (macro != null) {
                        ps = con.prepareStatement("INSERT INTO skillmacros (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        ps.setInt(1, id);
                        ps.setInt(2, macro.getSkill1());
                        ps.setInt(3, macro.getSkill2());
                        ps.setInt(4, macro.getSkill3());
                        ps.setString(5, macro.getName());
                        ps.setInt(6, macro.getShout());
                        ps.setInt(7, i);
                        ps.execute();
                        ps.close();
                    }
                }
            }

            deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            ps.setByte(2, getInventory(MapleInventoryType.EQUIP).getSlotLimit());
            ps.setByte(3, getInventory(MapleInventoryType.USE).getSlotLimit());
            ps.setByte(4, getInventory(MapleInventoryType.SETUP).getSlotLimit());
            ps.setByte(5, getInventory(MapleInventoryType.ETC).getSlotLimit());
            ps.setByte(6, getInventory(MapleInventoryType.CASH).getSlotLimit());
            ps.execute();
            ps.close();

            saveInventory(con); // 보류

            if (changed_questinfo) {
                deleteWhereCharacterId(con, "DELETE FROM questinfo WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO questinfo (`characterid`, `quest`, `customData`) VALUES (?, ?, ?)");
                ps.setInt(1, id);
                for (final Entry<Integer, String> q : questinfo.entrySet()) {
                    ps.setInt(2, q.getKey());
                    ps.setString(3, q.getValue());
                    ps.execute();
                }
                ps.close();
            }
            /*
            if (changed_questinfo) {
                deleteWhereAccountId(con, "DELETE FROM questinfo_acc WHERE accountid = ?");
                ps = con.prepareStatement("INSERT INTO questinfo_acc (`accountid`, `quest`, `customData`) VALUES (?, ?, ?)");
                ps.setInt(1, accountid);
                for (final Entry<Integer, String> q : questinfo.entrySet()) {
                    ps.setInt(2, q.getKey());
                    ps.setString(3, q.getValue());
                    ps.execute();
                }
                ps.close();
            }
             */
            if (changed_quest) {
                deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS);
                pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
                ps.setInt(1, id);
                for (final MapleQuestStatus q : quests.values()) {
                    ps.setInt(2, q.getQuest().getId());
                    ps.setInt(3, q.getStatus());
                    ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                    ps.setInt(5, q.getForfeited());
                    ps.setString(6, q.getCustomData());
                    ps.execute();
                    rs = ps.getGeneratedKeys();
                    if (q.hasMobKills()) {
                        rs.next();
                        for (int mob : q.getMobKills().keySet()) {
                            pse.setInt(1, rs.getInt(1));
                            pse.setInt(2, mob);
                            pse.setInt(3, q.getMobKills(mob));
                            pse.execute();
                        }
                    }
                    rs.close();
                }
                ps.close();
                pse.close();
            }
            if (changed_skills) {
                deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)");
                ps.setInt(1, id);

                for (final Entry<Skill, SkillEntry> skill : skills.entrySet()) {
                    if (GameConstants.isApplicableSkill(skill.getKey().getId())) { //do not save additional skills
                        ps.setInt(2, skill.getKey().getId());
                        ps.setInt(3, skill.getValue().skillevel);
                        ps.setByte(4, skill.getValue().masterlevel);
                        ps.setLong(5, skill.getValue().expiration);
                        ps.execute();
                    }
                }
                ps.close();
            }
            if (innerskill_changed) {
                if (innerSkills != null) {
                    deleteWhereCharacterId(con, "DELETE FROM inner_ability_skills WHERE player_id = ?");
                    ps = con.prepareStatement("INSERT INTO inner_ability_skills (player_id, skill_id, skill_level, max_level, rank) VALUES (?, ?, ?, ?, ?)");
                    ps.setInt(1, id);

                    for (int i = 0; i < innerSkills.size(); ++i) {
                        ps.setInt(2, innerSkills.get(i).getSkillId());
                        ps.setInt(3, innerSkills.get(i).getSkillLevel());
                        ps.setInt(4, innerSkills.get(i).getMaxLevel());
                        ps.setInt(5, innerSkills.get(i).getRank());
                        ps.executeUpdate();
                        innerskill_changed = false;
                    }
                    ps.close();
                }
            }
            List<MapleCoolDownValueHolder> cd = getCooldowns();
            if (dc && cd.size() > 0) {
                ps = con.prepareStatement("INSERT INTO skills_cooldowns (charid, SkillID, StartTime, length) VALUES (?, ?, ?, ?)");
                ps.setInt(1, getId());
                for (final MapleCoolDownValueHolder cooling : cd) {
                    ps.setInt(2, cooling.skillId);
                    ps.setLong(3, cooling.startTime);
                    ps.setLong(4, cooling.length);
                    ps.execute();
                }
                ps.close();
            }

            if (changed_savedlocations) {
                deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO savedlocations (characterid, `locationtype`, `map`) VALUES (?, ?, ?)");
                ps.setInt(1, id);
                for (final SavedLocationType savedLocationType : SavedLocationType.values()) {
                    if (savedLocations[savedLocationType.getValue()] != -1) {
                        ps.setInt(2, savedLocationType.getValue());
                        ps.setInt(3, savedLocations[savedLocationType.getValue()]);
                        ps.execute();
                    }
                }
                ps.close();
            }

            if (changed_achievements) {
                ps = con.prepareStatement("DELETE FROM achievements WHERE accountid = ?");
                ps.setInt(1, accountid);
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("INSERT INTO achievements(charid, achievementid, accountid) VALUES(?, ?, ?)");
                for (Integer achid : finishedAchievements) {
                    ps.setInt(1, id);
                    ps.setInt(2, achid);
                    ps.setInt(3, accountid);
                    ps.execute();
                }
                ps.close();
            }

            if (changed_reports) {
                deleteWhereCharacterId(con, "DELETE FROM reports WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO reports VALUES(DEFAULT, ?, ?, ?)");
                for (Entry<ReportType, Integer> achid : reports.entrySet()) {
                    ps.setInt(1, id);
                    ps.setByte(2, achid.getKey().i);
                    ps.setInt(3, achid.getValue());
                    ps.execute();
                }
                ps.close();
            }

            if (buddylist.changed()) {
                deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `pending`) VALUES (?, ?, ?)");
                ps.setInt(1, id);
                for (BuddylistEntry entry : buddylist.getBuddies()) {
                    ps.setInt(2, entry.getCharacterId());
                    ps.setInt(3, entry.isVisible() ? 0 : 1);
                    ps.execute();
                }
                ps.close();
                buddylist.setChanged(false);
            }

            if (client != null) {
                client.saveKeyValueToDB(con);
                if (!fromcs) {
                    client.saveLinkedSkill(con);
                }
                client.saveCustomDataToDB(con);
            }

            ps = con.prepareStatement("UPDATE accounts SET `nxCredit` = ?, `ACash` = ?, `mPoints` = ?, `recommend` = ? , `Fanclub` = ?, `premium` = ? WHERE id = ?");
            ps.setInt(1, nxcredit);
            ps.setInt(2, acash);
            ps.setInt(3, maplepoints);
            ps.setInt(4, recommend);
            ps.setInt(5, Fanclub);
            ps.setTimestamp(6, premium);
            ps.setInt(7, client.getAccID());
            ps.executeUpdate();
            ps.close();

            if (storage != null) {
                storage.saveToDB(con);
            }
            if (cs != null) {
                cs.save(); // 확인
            }
            //PlayerNPC.updateByCharId(this); // 확인
            //keylayout.saveKeys(id);
            //mount.saveMount(id);
            //monsterbook.saveCards(accountid);

            deleteWhereCharacterId(con, "DELETE FROM imps WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO imps (characterid, itemid, closeness, fullness, state, level) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            for (int i = 0; i < imps.length; i++) {
                if (imps[i] != null) {
                    ps.setInt(2, imps[i].getItemId());
                    ps.setShort(3, imps[i].getCloseness());
                    ps.setShort(4, imps[i].getFullness());
                    ps.setByte(5, imps[i].getState());
                    ps.setByte(6, imps[i].getLevel());
                    ps.executeUpdate();
                }
            }
            ps.close();
            if (changed_wishlist) {
                deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?");
                for (int i = 0; i < getWishlistSize(); i++) {
                    ps = con.prepareStatement("INSERT INTO wishlist(characterid, sn) VALUES(?, ?) ");
                    ps.setInt(1, getId());
                    ps.setInt(2, wishlist[i]);
                    ps.execute();
                    ps.close();
                }
            }
            if (changed_trocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?");
                for (int i = 0; i < rocks.length; i++) {
                    if (rocks[i] != 999999999) {
                        ps = con.prepareStatement("INSERT INTO trocklocations(characterid, mapid) VALUES(?, ?) ");
                        ps.setInt(1, getId());
                        ps.setInt(2, rocks[i]);
                        ps.execute();
                        ps.close();
                    }
                }
            }

            if (changed_regrocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM regrocklocations WHERE characterid = ?");
                for (int i = 0; i < regrocks.length; i++) {
                    if (regrocks[i] != 999999999) {
                        ps = con.prepareStatement("INSERT INTO regrocklocations(characterid, mapid) VALUES(?, ?) ");
                        ps.setInt(1, getId());
                        ps.setInt(2, regrocks[i]);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            if (changed_hyperrocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM hyperrocklocations WHERE characterid = ?");
                for (int i = 0; i < hyperrocks.length; i++) {
                    if (hyperrocks[i] != 999999999) {
                        ps = con.prepareStatement("INSERT INTO hyperrocklocations(characterid, mapid) VALUES(?, ?) ");
                        ps.setInt(1, getId());
                        ps.setInt(2, hyperrocks[i]);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            if (changed_extendedSlots) {
                deleteWhereCharacterId(con, "DELETE FROM extendedSlots WHERE characterid = ?");
                for (int i : extendedSlots) {
                    if (getInventory(MapleInventoryType.ETC).findById(i) != null) { //just in case
                        ps = con.prepareStatement("INSERT INTO extendedSlots(characterid, itemId) VALUES(?, ?) ");
                        ps.setInt(1, getId());
                        ps.setInt(2, i);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            changed_wishlist = false;
            changed_quest = false;
            changed_trocklocations = false;
            changed_regrocklocations = false;
            changed_hyperrocklocations = false;
            changed_skillmacros = false;
            changed_savedlocations = false;
            changed_questinfo = false;
            changed_achievements = false;
            changed_extendedSlots = false;
            changed_skills = false;
            changed_reports = false;
            con.commit();
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
            e.printStackTrace();
            System.err.println(MapleClient.getLogMessage(this, "[charsave] Error saving character data") + e);
            try {
                con.rollback();
            } catch (SQLException ex) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
                System.err.println(MapleClient.getLogMessage(this, "[charsave] Error Rolling Back") + e);
            }
        } finally {
            try {
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                System.err.println(MapleClient.getLogMessage(this, "[charsave] Error going back to autocommit mode") + e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (pse != null) {
                    pse.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
    }

    private void deleteWhereCharacterName(Connection con, String sql) throws SQLException {
        deleteWhereCharacterName(con, sql, this.getName());
    }

    public static void deleteWhereCharacterName(Connection con, String sql, String name) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, name);
        ps.execute();
        ps.close();
    }

    private void deleteWhereCharacterId(Connection con, String sql) throws SQLException {
        deleteWhereCharacterId(con, sql, id);
    }

    public static void deleteWhereCharacterId(Connection con, String sql, int id) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    private void deleteWhereAccountId(Connection con, String sql) throws SQLException {
        deleteWhereAccountId(con, sql, accountid);
    }

    public static void deleteWhereAccountId(Connection con, String sql, int accid) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, accid);
        ps.executeUpdate();
        ps.close();
    }

    public static void deleteWhereCharacterId_NoLock(Connection con, String sql, int id) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.execute();
        ps.close();
    }

    public void saveInventory(final Connection con) throws SQLException {
        List<Pair<Item, MapleInventoryType>> listing = new ArrayList<Pair<Item, MapleInventoryType>>();
        for (final MapleInventory iv : inventory) {
            for (final Item item : iv.list()) {
                listing.add(new Pair<Item, MapleInventoryType>(item, iv.getType()));
            }
        }
        if (con != null) {
            ItemLoader.INVENTORY.saveItems(listing, con, id);
        } else {
            ItemLoader.INVENTORY.saveItems(listing, id);
        }
    }

    public final PlayerStats getStat() {
        return stats;
    }

    public final void QuestInfoPacket(final OutPacket mplew) {
        mplew.writeShort(questinfo.size()); // // Party Quest data (quest needs to be added in the quests list)
        for (final Entry<Integer, String> q : questinfo.entrySet()) {
            mplew.writeShort(q.getKey());
            mplew.writeMapleAsciiString(q.getValue() == null ? "" : q.getValue());
        }
    }

    public final void updateInfoQuest(final int questid, final String data) {
        questinfo.put(questid, data);
        changed_questinfo = true;
        changed_quest = true;
        client.getSession().write(InfoPacket.updateInfoQuest(questid, data));
    }

    public final String getInfoQuest(final int questid) {
        if (questinfo.containsKey(questid)) {
            return questinfo.get(questid);
        }
        return "";
    }

    public final int getNumQuest() {
        int i = 0;
        for (final MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !(q.isCustom())) {
                i++;
            }
        }
        return i;
    }

    public final byte getQuestStatus(final int quest) {
        final MapleQuest qq = MapleQuest.getInstance(quest);
        if (getQuestNoAdd(qq) == null) {
            return 0;
        }
        return getQuestNoAdd(qq).getStatus();
    }

    public final MapleQuestStatus getQuest(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, (byte) 0);
        }
        return quests.get(quest);
    }

    public final void setQuestAdd(final MapleQuest quest, final byte status, final String customData) {
        if (!quests.containsKey(quest)) {
            final MapleQuestStatus stat = new MapleQuestStatus(quest, status);
            stat.setCustomData(customData);
            quests.put(quest, stat);
        }
    }

    public final MapleQuestStatus getQuestNAdd(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            final MapleQuestStatus status = new MapleQuestStatus(quest, (byte) 0);
            quests.put(quest, status);
            return status;
        }
        return quests.get(quest);
    }

    public final MapleQuestStatus getQuestNoAdd(final MapleQuest quest) {
        return quests.get(quest);
    }

    public final MapleQuestStatus getQuestRemove(final MapleQuest quest) {
        return quests.remove(quest);
    }

    public final void updateQuest(final MapleQuestStatus quest) {
        updateQuest(quest, false);
    }

    public final void updateQuest(final MapleQuestStatus q, final boolean update) {
        changed_quest = true;
        quests.put(q.getQuest(), q);
        if (!(q.isCustom())) {
            client.getSession().write(InfoPacket.updateQuest(q));
            if (q.getStatus() == 1 && !update) {
                if (q.getQuest().getId() == 1047) { //if (q.getCustomData().startsWith("time_")) {
                    client.sendPacket(CUserLocal.questResult(QuestType.QuestRes_Start_QuestTimer, q.getQuest().getId(), 0, 0));
                } else {
                    client.sendPacket(CUserLocal.questResult(QuestType.QuestRes_Act_Success, q.getQuest().getId(), q.getNpc(), 0));
                }
            } else {
                if (q.getQuest().getId() == 1047) { //if (q.getCustomData().startsWith("time_")) {
                    client.sendPacket(CUserLocal.questResult(QuestType.QuestRes_End_QuestTimer, q.getQuest().getId(), 0, 0));
                }
            }
        }

        String qInfo = null;
        int qChapter = 0;
        boolean qStartEffect = false;
        switch (q.getQuest().getId()) {
            case 1620: {
                qInfo = "m0=2";
                qChapter = 0;
                qStartEffect = true;
                break;
            }
            case 1621: {
                qInfo = "m0=2;m1=2";
                qChapter = 0;
                break;
            }
            case 1623: {
                qInfo = "m0=2;m1=2;m2=2";
                qChapter = 0;
                break;
            }
            case 1625: {
                qInfo = "m0=2;m1=2;m2=2;m3=2";
                qChapter = 0;
                break;
            }
            case 1627: {
                qInfo = "m0=2;m1=2;m2=2;m3=2;m4=2";
                qChapter = 0;
                break;
            }
            case 1629: {
                qInfo = "m0=2";
                qChapter = 1;
                break;
            }
            case 1631: {
                qInfo = "m0=2;m1=2";
                qChapter = 1;
                break;
            }
            case 1632: {
                qInfo = "m0=2;m1=2;m2=2;m3=2";
                qChapter = 1;
                break;
            }
            case 1634: {
                qInfo = "m0=2;m1=2;m2=2;m3=2;m4=2";
                qChapter = 1;
                break;
            }
            case 1655: {
                qInfo = "m0=2";
                qChapter = 2;
                qStartEffect = true;
                break;
            }
            case 1657: {
                qInfo = "m0=2;m1=2";
                qChapter = 2;
                break;
            }
            case 1658: {
                qInfo = "m0=2;m1=2;m2=2";
                qChapter = 2;
                break;
            }
            case 1660: {
                qInfo = "m0=2;m1=2;m2=2;m3=2";
                qChapter = 2;
                break;
            }
            case 1662: {
                qInfo = "m0=2;m1=2;m2=2;m3=2;m4=2";
                qChapter = 2;
                break;
            }
            case 1666: {
                qInfo = "m0=2";
                qChapter = 3;
                qStartEffect = true;
                break;
            }
            case 1668: {
                qInfo = "m0=2;m1=2";
                qChapter = 3;
                break;
            }
            case 1670: {
                qInfo = "m0=2;m1=2;m2=2";
                qChapter = 3;
                break;
            }
            case 1671: {
                qInfo = "m0=2;m1=2;m2=2;m3=2";
                qChapter = 3;
                break;
            }
            case 1672: {
                qInfo = "m0=2;m1=2;m2=2;m3=2;m4=2";
                qChapter = 3;
                break;
            }
        }
        if (q.getStatus() == 1 && qStartEffect == true) {
            this.getClient().sendPacket(CField.showEffect("crossHunter/chapter/start" + (qChapter + 1)));
        }
        if (q.getStatus() == 2 && qInfo != null) {
            this.updateInfoQuest(1648 + qChapter, qInfo);
            this.getClient().sendPacket(CWvsContext.crossHunterQuestResult((byte) 0, (short) qChapter));
        }
    }

    public final void updateQuest(int id, int stat, String s) {
        final MapleQuestStatus q = getQuestNAdd(MapleQuest.getInstance(id));
        q.setStatus((byte) stat);
        q.setCustomData(s);
        updateQuest(q, true);
        client.sendPacket(InfoPacket.updateQuest(q));
        client.sendPacket(CUserLocal.questResult(QuestType.QuestRes_Act_Success, q.getQuest().getId(), q.getNpc(), 0));
    }

    public final void updateQuest(int id, String s) {
        final MapleQuestStatus status = getQuestNAdd(MapleQuest.getInstance(id));
        status.setCustomData(s);
        status.setStatus((byte) 1);
        updateQuest(status, true);
    }

    public void updateOneInfoQuest(int questid, String key, String value) {
        String allValues = getInfoQuest(questid);
        if (!allValues.equals("")) {
            Map<String, String> values = new HashMap<String, String>();
            String[] keyvalues = allValues.split(";");
            for (int i = 0; i < keyvalues.length; i++) {
                String[] keyandvalue = keyvalues[i].split("=");
                values.put(keyandvalue[0], keyandvalue[1]);
            }
            if (values.containsKey(key)) {
                values.remove(key);
            }
            values.put(key, value);
            allValues = "";
            int size = 1;
            for (Entry<String, String> e : values.entrySet()) {
                allValues += e.getKey() + "=" + e.getValue();
                if (size < values.size()) {
                    allValues += ";";
                }
                size++;
            }
        } else {
            allValues = key + "=" + value;
        }
        updateInfoQuest(questid, allValues);
    }

    public String getOneInfoQuest(int questid, String key) {
        String allValues = getInfoQuest(questid);
        if (!allValues.equals("")) {
            Map<String, String> values = new HashMap<>();
            String[] keyvalues = allValues.split(";");
            for (int i = 0; i < keyvalues.length; i++) {
                String[] keyandvalue = keyvalues[i].split("=");
                values.put(keyandvalue[0], keyandvalue[1]);
            }
            if (values.containsKey(key)) {
                return values.get(key);
            }
        }
        return "";
    }

    public final Map<Integer, String> getInfoQuest_Map() {
        return questinfo;
    }

    public final Map<MapleQuest, MapleQuestStatus> getQuest_Map() {
        return quests;
    }

    public Integer getBuffedValue(CharacterTemporaryStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : Integer.valueOf(mbsvh.value);
    }

    public final Integer getBuffedSkill_X(final CharacterTemporaryStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getX();
    }

    public final Integer getBuffedSkill_Y(final CharacterTemporaryStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getY();
    }

    public boolean isBuffFrom(CharacterTemporaryStat stat, Skill skill) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null || mbsvh.effect == null) {
            return false;
        }
        return mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skill.getId();
    }

    public int getBuffSource(CharacterTemporaryStat stat) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        return mbsvh == null ? -1 : mbsvh.effect.getSourceId();
    }

    public int getTrueBuffSource(CharacterTemporaryStat stat) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        return mbsvh == null ? -1 : (mbsvh.effect.isSkill() ? mbsvh.effect.getSourceId() : -mbsvh.effect.getSourceId());
    }

    public int getItemQuantity(int itemid, boolean checkEquipped) {
        int possesed = inventory[GameConstants.getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }

    public void setBuffedValue(CharacterTemporaryStat effect, int value) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }

    public void setBuffedValue(CharacterTemporaryStat effect, int value, int skillid) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        if (skillid == -1) {
            if (effects.get(effect) != null) {
                effects.get(effect).value = value;
            }
        } else {
            if (mbsvh.effect.getSourceId() == skillid) {
                mbsvh.value = value;
            }
        }
    }

    public void setSchedule(CharacterTemporaryStat effect, ScheduledFuture<?> sched) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.schedule.cancel(false);
        mbsvh.schedule = sched;
    }

    public Long getBuffedStarttime(CharacterTemporaryStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : Long.valueOf(mbsvh.startTime);
    }

    public MapleStatEffect getStatForBuff(CharacterTemporaryStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.effect;
    }

    public void doDragonBlood() {
        final MapleStatEffect bloodEffect = getStatForBuff(CharacterTemporaryStat.DragonBlood);
        if (bloodEffect == null) {
            lastDragonBloodTime = 0;
            return;
        }
        prepareDragonBlood();
        if (stats.getHp() - bloodEffect.getX() <= 1) {
            cancelBuffStats(true, CharacterTemporaryStat.DragonBlood);
        } else {
            addHP(-bloodEffect.getX());
            client.getSession().write(EffectPacket.showOwnBuffEffect(bloodEffect.getSourceId(), 7, getLevel(), bloodEffect.getLevel()));
            map.broadcastMessage(MapleCharacter.this, EffectPacket.showBuffeffect(getId(), bloodEffect.getSourceId(), 7, getLevel(), bloodEffect.getLevel()), false);
        }
    }

    public final boolean canBlood(long now) {
        return lastDragonBloodTime > 0 && lastDragonBloodTime + 4000 < now;
    }

    private void prepareDragonBlood() {
        lastDragonBloodTime = System.currentTimeMillis();
    }

    public void doRecovery() {
        MapleStatEffect bloodEffect = getStatForBuff(CharacterTemporaryStat.Regen);
        if (bloodEffect == null) {
            bloodEffect = getStatForBuff(CharacterTemporaryStat.Mechanic);
            if (bloodEffect == null) {
                lastRecoveryTime = 0;
                return;
            }
        } else {
            prepareRecovery();
            if (stats.getHp() >= stats.getCurrentMaxHp()) {
                cancelEffectFromBuffStat(CharacterTemporaryStat.Regen);
            } else {
                healHP(bloodEffect.getX());
            }
        }
    }

    public final boolean canRecover(long now) {
        return lastRecoveryTime > 0 && lastRecoveryTime + 5000 < now;
    }

    private void prepareRecovery() {
        lastRecoveryTime = System.currentTimeMillis();
    }

    public void startMapTimeLimitTask(int time, final MapleMap to) {
        if (time < 1) {
            time = 1;
        }
        client.sendPacket(CField.getClock(time));
        time *= 1000;
        mapTimeLimitTask = MapTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                changeMap(to, to.getPortal(0));
            }
        }, time, time);
    }

    public boolean canDOT(long now) {
        return lastDOTTime > 0 && lastDOTTime + 8000 < now;
    }

    public boolean hasDOT() {
        return dotHP > 0;
    }

    public void doDOT() {
        addHP(-(dotHP * 4));
        dotHP = 0;
        lastDOTTime = 0;
    }

    public void setDOT(int d, int source, int sourceLevel) {
        this.dotHP = d;
        addHP(-(dotHP * 4));
        map.broadcastMessage(CField.getPVPMist(id, source, sourceLevel, d));
        lastDOTTime = System.currentTimeMillis();
    }

    public void startFishingTask() {
        cancelFishingTask();
        lastFishingTime = System.currentTimeMillis();
    }

    public boolean canFish(long now) {
        return lastFishingTime > 0 && lastFishingTime + GameConstants.getFishingTime(false, isGM()) < now;
    }

    public void doFish(long now) {
        lastFishingTime = now;
        if (client == null || client.getPlayer() == null || !client.isReceiving() || (!haveItem(2270008, 1, false, true)) || !GameConstants.isFishingMap(getMapId()) || chair <= 0) {
            cancelFishingTask();
            return;
        }
        MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, 2270008, 1, false, false);
        boolean passed = false;
        while (!passed) {
            int randval = RandomRewards.getFishingReward();
            switch (randval) {
                case 0: // Meso
                    final int money = Randomizer.rand(10, 50000);
                    gainMeso(money, true);
                    passed = true;
                    break;
                case 1: // EXP
                    final int experi = Math.min(Randomizer.nextInt(Math.abs((int) getNeededExp() / 200) + 1), 500000);
                    gainExp(experi, true, false, true);
                    passed = true;
                    break;
                default:
                    if (MapleItemInformationProvider.getInstance().itemExists(randval)) {
                        MapleInventoryManipulator.addById(client, randval, (short) 1, "Fishing" + " on " + FileoutputUtil.CurrentReadable_Date());
                        passed = true;
                    }
                    break;
            }
        }
    }

    public void cancelMapTimeLimitTask() {
        if (mapTimeLimitTask != null) {
            mapTimeLimitTask.cancel(false);
            mapTimeLimitTask = null;
        }
    }

    public int getNeededExp() {
        return GameConstants.getExpNeededForLevel(level);
    }

    public void cancelFishingTask() {
        lastFishingTime = 0;
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, int from) {
        registerEffect(effect, starttime, schedule, effect.getStatups(), false, effect.getDuration(), from);
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, Map<CharacterTemporaryStat, Integer> statups, boolean silent, final int localDuration, final int cid) {
        if (effect.isHide()) {
            map.broadcastMessage(this, CField.OnUserEnterField(this), false);
        }
        /*if (effect.isDragonBlood()) {
            prepareDragonBlood();
        } else */
        if (effect.isRecovery()) {
            prepareRecovery();
        } else if (effect.isBerserk()) {
            checkBerserk();
        } else if (effect.isMonsterRiding_()) {
            getMount().startSchedule();
        }
        for (Entry<CharacterTemporaryStat, Integer> statup : statups.entrySet()) {
            int value = statup.getValue().intValue();
            if (statup.getKey() == CharacterTemporaryStat.MonsterRiding) {
                if (effect.getSourceId() == 5221006 && battleshipHP <= 0) {
                    battleshipHP = maxBattleshipHP(effect.getSourceId());
                }
            }
            effects.put(statup.getKey(), new MapleBuffStatValueHolder(effect, starttime, schedule, value, localDuration, cid));
        }
        if (!silent) {
            stats.recalcLocalStats(this);
        }
    }

    public final void checkLifeTidal() {
        if (getJob() == 2711 || getJob() == 2712) {
            if (getSkillLevel(27110007) > 0) { // 라이프 타이달
                Skill skill = SkillFactory.getSkill(27110007);
                int critical = getSkillLevel(skill);
                if ((getStat().getHp() / getStat().getCurrentMaxHp()) * 100 < (getStat().getMp() / getStat().getCurrentMaxMp(getJob())) * 100) {
                    client.getSession().write(BuffPacket.giveLifeTidal(false, skill.getEffect(critical).getX()));
                } else if ((getStat().getHp() / getStat().getCurrentMaxHp()) * 100 > (getStat().getMp() / getStat().getCurrentMaxMp(getJob())) * 100) {
                    if (critical > 0) {
                        client.getSession().write(BuffPacket.giveLifeTidal(true, skill.getEffect(critical).getProb()));
                    }
                } else if ((getStat().getHp() / getStat().getCurrentMaxHp()) * 100 == (getStat().getMp() / getStat().getCurrentMaxMp(getJob())) * 100) {
                    if (critical > 0) {
                        client.getSession().write(BuffPacket.giveLifeTidal(true, skill.getEffect(critical).getProb()));
                    }
                }
            }
        }
    }

    public final void startDiaboliqueRecovery(final MapleStatEffect eff) {
        BuffTimer tMan = BuffTimer.getInstance();
        if (DiaboliqueRecoveryTask != null) {
            DiaboliqueRecoveryTask.cancel(true);
            DiaboliqueRecoveryTask = null;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                int regenHP = (int) (getStat().getCurrentMaxHp() * (eff.getX() / 100.0D));
                if (isAlive()) {
                    addHP(regenHP);
                    if (getStat().getHp() < getStat().getCurrentMaxHp()) {
                        client.getSession().write(EffectPacket.showOwnRecoverHP(Math.min(getStat().getCurrentMaxHp() - getStat().getHp(), regenHP)));
                    }
                }
            }
        };
        DiaboliqueRecoveryTask = tMan.register(r, eff.getW() * 1000);
        tMan.schedule(new Runnable() {
            @Override
            public void run() {
                if (DiaboliqueRecoveryTask != null) {
                    DiaboliqueRecoveryTask.cancel(true);
                    DiaboliqueRecoveryTask = null;
                }
            }
        }, eff.getDuration());
    }

    public void setNullSelfRecovery() {
        selfRecoveryTask = null;
    }

    public void checkSelfRecovery() {
        if (getSkillLevel(61110006) > 0) {
            if (selfRecoveryTask == null) {
                final MapleStatEffect eff = SkillFactory.getSkill(61110006).getEffect(getSkillLevel(61110006));
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        int recoverhp = 0;
                        int recovermp = 0;
                        recoverhp = (int) (getStat().getCurrentMaxHp() * ((double) eff.getX() / 100));
                        recovermp = (int) (getStat().getCurrentMaxMp(getJob()) * ((double) eff.getX() / 100));
                        if (isAlive()) {
                            addHP(recoverhp);
                            addMP(recovermp);
                            if (getStat().getHp() != getStat().getCurrentMaxHp() && !(getStat().getHp() > getStat().getCurrentMaxHp())) {
                                client.getSession().write(EffectPacket.showOwnRecoverHP(Math.min(getStat().getCurrentMaxHp() - getStat().getHp(), recoverhp)));
                            }
                        }
                    }
                };
                BuffTimer tMan = BuffTimer.getInstance();
                selfRecoveryTask = tMan.register(r, 4000);
            }
        }
    }

    public List<CharacterTemporaryStat> getBuffStats(final MapleStatEffect effect, final long startTime) {
        final List<CharacterTemporaryStat> bstats = new ArrayList<>();
        final Map<CharacterTemporaryStat, MapleBuffStatValueHolder> allBuffs = new EnumMap<>(effects);
        for (Entry<CharacterTemporaryStat, MapleBuffStatValueHolder> stateffect : allBuffs.entrySet()) {
            final MapleBuffStatValueHolder mbsvh = stateffect.getValue();
            if (mbsvh.effect.sameSource(effect) && (startTime == -1 || startTime == mbsvh.startTime || stateffect.getKey().canStack())) {
                bstats.add(stateffect.getKey());
            }
        }
        return bstats;
    }

    private void deregisterBuffStats(List<CharacterTemporaryStat> stats) {
        List<MapleBuffStatValueHolder> effectsToCancel = new ArrayList<>(stats.size());
        for (CharacterTemporaryStat stat : stats) {
            final MapleBuffStatValueHolder mbsvh = effects.remove(stat);
            if (mbsvh != null) {
                boolean addMbsvh = true;
                for (MapleBuffStatValueHolder contained : effectsToCancel) {
                    if (mbsvh.startTime == contained.startTime && contained.effect == mbsvh.effect) {
                        addMbsvh = false;
                    }
                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if (stat == CharacterTemporaryStat.Summon || (stat == CharacterTemporaryStat.Summon && mbsvh.effect.getSourceId() != 33101008) || stat == CharacterTemporaryStat.Puppet || stat == CharacterTemporaryStat.Reaper || stat == CharacterTemporaryStat.Beholder || stat == CharacterTemporaryStat.HowlingAttackDamage || stat == CharacterTemporaryStat.RainingMines || stat == CharacterTemporaryStat.IndiePad) {
                    final int summonId = mbsvh.effect.getSourceId();
                    final List<MapleSummon> toRemove = new ArrayList<>();
                    visibleMapObjectsLock.writeLock().lock();
                    summonsLock.writeLock().lock();
                    try {
                        for (MapleSummon summon : summons) {
                            if (summon.getSkill() == summonId || summon.getSkill() == 5320011 || (stat == CharacterTemporaryStat.RainingMines && summonId == 33101008) || (summonId == 35121009 && summon.getSkill() == 35121011) || ((summonId == 86 || summonId == 88 || summonId == 91) && summon.getSkill() == summonId + 999) || ((summonId == 1085 || summonId == 1087 || summonId == 1090 || summonId == 1179) && summon.getSkill() == summonId - 999)) { //removes bots n tots

                                map.broadcastMessage(SummonPacket.removeSummon(summon, true));
                                map.removeMapObject(summon);
                                for (MapleCharacter player : map.getCharacters()) {
                                    player.removeSummonList(summon);
                                }
                                visibleMapObjects.remove(summon);
                                toRemove.add(summon);
                            }
                        }
                        for (MapleSummon s : toRemove) {
                            summons.remove(s);
                        }
                    } finally {
                        summonsLock.writeLock().unlock();
                        visibleMapObjectsLock.writeLock().unlock();
                    }
                    if (summonId == 3111005 || summonId == 3211005) {
                        this.cancelBuffStats(false, CharacterTemporaryStat.SpiritInfusion);
                        //cancelEffectFromBuffStat(CharacterTemporaryStat.SpiritLink);
                    }
                    //} else if (stat == CharacterTemporaryStat.DragonBlood) {
                    //lastDragonBloodTime = 0;
                } else if (stat == CharacterTemporaryStat.Regen || mbsvh.effect.getSourceId() == 35121005) {
                    lastRecoveryTime = 0;
                } else if (stat == CharacterTemporaryStat.GuidedBullet || stat == CharacterTemporaryStat.ArcaneAim) {
                    linkMobs.clear();
                }
            }
            for (MapleBuffStatValueHolder cancelEffectCancelTasks : effectsToCancel) {
                if (getBuffStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).isEmpty()) {
                    if (cancelEffectCancelTasks.schedule != null) {
                        cancelEffectCancelTasks.schedule.cancel(false);
                    }
                }
            }
        }
    }

    public void cancelEffect(final MapleStatEffect effect, final long startTime) {
        if (effect == null) {
            return;
        }
        cancelEffect(effect, false, startTime, effect.getStatups());
    }

    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime) {
        if (effect == null) {
            return;
        }
        cancelEffect(effect, overwrite, startTime, effect.getStatups());
    }

    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime, Map<CharacterTemporaryStat, Integer> statups) {
        //this.dropMessage(5, "cancelEffect");
        if (effect == null) {
            return;
        }
        List<CharacterTemporaryStat> buffstats;
        if (!overwrite) {
            buffstats = getBuffStats(effect, startTime);
        } else {
            buffstats = new ArrayList<>(statups.keySet());
        }
        if (buffstats.size() <= 0) {
            return;
        }
        switch (effect.getSourceId()) {
            case 2120010:
            case 2220010:
            case 2320011: {
                this.setArcaneAim(0);
                break;
            }
            case 35121013: {
                SkillFactory.getSkill(35121005).getEffect(getTotalSkillLevel(35121005)).applyTo(this);
                return;
            }
            case 35111004: {
                //case 35121003: {
                return;
            }
            case 62101002:
            case 62120011: {
                map.broadcastMessage(CField.deactivateAndroid(this.id));
                if (android != null) {
                    this.setAndroid(android);
                }
                break;
            }
        }
        /*
        if (effect.isInfinity() && getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
            int duration = Math.max(effect.getDuration(), effect.alchemistModifyVal(this, effect.getDuration(), false));
            final long start = getBuffedStarttime(CharacterTemporaryStat.Infinity);
            duration += (int) ((start - System.currentTimeMillis()));
            if (duration > 0) {
                final int neworbcount = getBuffedValue(CharacterTemporaryStat.Infinity) + effect.getDamage();
                final Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.Infinity, neworbcount);
                setBuffedValue(CharacterTemporaryStat.Infinity, neworbcount);
                client.getSession().write(BuffPacket.giveBuff(effect.getSourceId(), duration, stat, effect));
                addHP((int) (effect.getHpR() * this.stats.getCurrentMaxHp()));
                addMP((int) (effect.getMpR() * this.stats.getCurrentMaxMp(this.getJob())));
                setSchedule(CharacterTemporaryStat.Infinity, BuffTimer.getInstance().schedule(new CancelEffectAction(this, effect, start, stat), effect.alchemistModifyVal(this, 4000, false)));
                return;
            }
        }
         */
        if (effect.getSourceId() == 35111005 || effect.getSourceId() == 35111011) {
            final List<MapleSummon> toRemove = new ArrayList<MapleSummon>();
            for (MapleSummon summon : summons) {
                if (summon.getSkill() == effect.getSourceId()) {
                    map.broadcastMessage(SummonPacket.removeSummon(summon, true));
                    map.removeMapObject(summon);
                    for (MapleCharacter player : map.getCharacters()) {
                        player.removeSummonList(summon);
                    }
                    visibleMapObjects.remove(summon);
                    toRemove.add(summon);
                }
            }
            for (MapleSummon s : toRemove) {
                summons.remove(s);
            }
        }
        //deregisterBuffStats(buffstats);
        if (effect.isMagicDoor()) {
            if (!getDoors().isEmpty()) {
                removeDoor();
                silentPartyUpdate();
            }
        } else if (effect.isMechDoor()) {
            if (!getMechDoors().isEmpty()) {
                removeMechDoor();
            }
        } else if (effect.isMonsterRiding_()) {
            getMount().cancelSchedule();
        } else if (effect.isAranCombo()) {
            combo = 0;
        }
        deregisterBuffStats(buffstats); // ?
        cancelPlayerBuffs(buffstats, overwrite);
        if (!overwrite) {
            if (effect.isHide() && client.getChannelServer().getPlayerStorage().getCharacterById(this.getId()) != null) {
                map.broadcastMessage(this, CField.OnUserEnterField(this), false);
                sendTemporaryStats();
                for (final MaplePet pet : pets) {
                    if (pet.getSummoned()) {
                        map.broadcastMessage(this, CPet.showPet(this, pet, false, false), false);
                    }
                }
            }
        }
    }

    public void cancelBuffStats(boolean recalcStat, CharacterTemporaryStat... stat) {
        List<CharacterTemporaryStat> buffStatList = Arrays.asList(stat);
        deregisterBuffStats(buffStatList);
        cancelPlayerBuffs(buffStatList, recalcStat);
    }

    public void cancelEffectFromBuffStat(CharacterTemporaryStat stat) {
        if (effects.get(stat) != null) {
            cancelEffect(effects.get(stat).effect, false, -1);
        }
    }

    public void cancelEffectFromBuffStat(CharacterTemporaryStat stat, int from) {
        if (effects.get(stat) != null && effects.get(stat).cid == from) {
            cancelEffect(effects.get(stat).effect, false, -1);
        }
    }

    public void cancelEffectFromCid(CharacterTemporaryStat stat, int from) {
        if (effects.get(stat) != null && effects.get(stat).cid == from) {
            cancelEffect(effects.get(stat).effect, false, -1);
        }
    }

    private void cancelPlayerBuffs(List<CharacterTemporaryStat> buffstats, boolean overwrite) {
        boolean write = client != null && client.getChannelServer() != null && client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null;
        if (buffstats.contains(CharacterTemporaryStat.GuidedBullet)) {
            client.getSession().write(BuffPacket.cancelHoming());
        } else {
            if (overwrite) {
                List<CharacterTemporaryStat> z = new ArrayList<>();
                for (CharacterTemporaryStat s : buffstats) {
                    if (s.canStack()) {
                        z.add(s);
                    }
                }
                if (z.size() > 0) {
                    buffstats = z;
                } else {
                    return;
                }
            } else if (write) {
                stats.recalcLocalStats(this);
            }
            client.getSession().write(BuffPacket.cancelBuff(buffstats));
            map.broadcastMessage(this, BuffPacket.cancelForeignBuff(getId(), buffstats), false);
        }
    }

    public void dispel() {
        if (!isHidden()) {
            final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
            for (MapleBuffStatValueHolder mbsvh : allBuffs) {
                if (mbsvh.effect.isSkill() && mbsvh.schedule != null && !mbsvh.effect.isMorph() && !mbsvh.effect.isGmBuff() && !mbsvh.effect.isMonsterRiding() && !mbsvh.effect.isMechChange() && !mbsvh.effect.isEnergyCharge() && !mbsvh.effect.isAranCombo()) {
                    cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                }
            }
        }
    }

    public void dispelSkill(int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    public void dispelSummons() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.getSummonMovementType() != null) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }
        }
    }

    public void dispelBuff(int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.getSourceId() == skillid) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    public void cancelAllBuffs_() {
        effects.clear();
    }

    public void cancelAllBuffs() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }

    public void cancelMorphs() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            switch (mbsvh.effect.getSourceId()) {
                case 5111005:
                case 5121003:
                case 15111002:
                case 13111005:
                    return;
                default:
                    if (mbsvh.effect.isMorph()) {
                        cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                        continue;
                    }
            }
        }
    }

    public int getMorphState() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMorph()) {
                return mbsvh.effect.getSourceId();
            }
        }
        return -1;
    }

    public void silentGiveBuffs(List<PlayerBuffValueHolder> buffs) {
        if (buffs == null) {
            return;
        }
        for (PlayerBuffValueHolder mbsvh : buffs) {
            mbsvh.effect.silentApplyBuff(this, mbsvh.startTime, mbsvh.localDuration, mbsvh.statup, mbsvh.cid);
        }
    }

    public List<PlayerBuffValueHolder> getAllBuffs() {
        final List<PlayerBuffValueHolder> ret = new ArrayList<>();
        final Map<Pair<Integer, Byte>, Integer> alreadyDone = new HashMap<>();
        final LinkedList<Entry<CharacterTemporaryStat, MapleBuffStatValueHolder>> allBuffs = new LinkedList<>(effects.entrySet());
        for (Entry<CharacterTemporaryStat, MapleBuffStatValueHolder> mbsvh : allBuffs) {
            final Pair<Integer, Byte> key = new Pair<>(mbsvh.getValue().effect.getSourceId(), mbsvh.getValue().effect.getLevel());
            if (alreadyDone.containsKey(key)) {
                ret.get(alreadyDone.get(key)).statup.put(mbsvh.getKey(), mbsvh.getValue().value);
            } else {
                alreadyDone.put(key, ret.size());
                final EnumMap<CharacterTemporaryStat, Integer> list = new EnumMap<>(CharacterTemporaryStat.class
                );
                list.put(mbsvh.getKey(), mbsvh.getValue().value);
                ret.add(new PlayerBuffValueHolder(mbsvh.getValue().startTime, mbsvh.getValue().effect, list, mbsvh.getValue().localDuration, mbsvh.getValue().cid));
            }
        }
        return ret;
    }

    public void cancelMagicDoor() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMagicDoor()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    public int getSkillLevel(int skillid) {
        return getSkillLevel(SkillFactory.getSkill(skillid));
    }

    public int getTotalSkillLevel(int skillid) {
        return getTotalSkillLevel(SkillFactory.getSkill(skillid));
    }

    public final void handleEnergyCharge(final int skillid, final int targets) {
        final Skill echskill = SkillFactory.getSkill(skillid);
        final int skilllevel = getTotalSkillLevel(echskill);
        if (skilllevel > 0) {
            final MapleStatEffect echeff = echskill.getEffect(skilllevel);
            if (targets > 0) {
                if (getBuffedValue(CharacterTemporaryStat.EnergyCharge) == null) {
                    echeff.applyEnergyBuff(this, true);
                } else {
                    Integer energyLevel = getBuffedValue(CharacterTemporaryStat.EnergyCharge);
                    if (energyLevel < 10000) {
                        energyLevel += (echeff.getX() * targets);
                        client.getSession().write(EffectPacket.showOwnBuffEffect(skillid, 2, getLevel(), skilllevel));
                        map.broadcastMessage(this, EffectPacket.showBuffeffect(id, skillid, 2, getLevel(), skilllevel), false);
                        if (energyLevel >= 10000) {
                            energyLevel = 10000;
                        }
                        client.getSession().write(BuffPacket.giveEnergyChargeTest(energyLevel, echeff.getDuration() / 1000));
                        setBuffedValue(CharacterTemporaryStat.EnergyCharge, Integer.valueOf(energyLevel));
                    } else if (energyLevel == 10000) {
                        //client.getSession().write(BuffPacket.giveEnergyChargeTest(energyLevel, echeff.getDuration() / 1000));
                        setBuffedValue(CharacterTemporaryStat.EnergyCharge, Integer.valueOf(10001));
                    }
                }
            }
        }
    }

    public final void handleBattleshipHP(int damage) {
        if (damage < 0) {
            final MapleStatEffect effect = getStatForBuff(CharacterTemporaryStat.MonsterRiding);
            if (effect != null && effect.getSourceId() == 5221006) {
                battleshipHP += damage;
                client.sendPacket(CUserLocal.skillCooltimeSet(5221999, battleshipHP / 10));
                if (battleshipHP <= 0) {
                    battleshipHP = 0;
                    client.sendPacket(CUserLocal.skillCooltimeSet(5221006, effect.getCooldown(this)));
                    addCooldown(5221006, System.currentTimeMillis(), effect.getCooldown(this) * 1000);
                    cancelEffectFromBuffStat(CharacterTemporaryStat.MonsterRiding);
                }
            }
        }
    }

    public final void handleOrbgain() {
        int orbcount = getBuffedValue(CharacterTemporaryStat.ComboCounter);
        Skill comboh;
        Skill advcombo;
        switch (getJob()) {
            case 1110:
            case 1111:
            case 1112:
                comboh = SkillFactory.getSkill(11111001);
                advcombo = SkillFactory.getSkill(11110005);
                break;
            default:
                comboh = SkillFactory.getSkill(1111002);
                advcombo = SkillFactory.getSkill(1120003);
                break;
        }

        MapleStatEffect ceffect;
        int advComboSkillLevel = getTotalSkillLevel(advcombo);
        if (advComboSkillLevel > 0) {
            ceffect = advcombo.getEffect(advComboSkillLevel);
        } else if (getSkillLevel(comboh) > 0) {
            ceffect = comboh.getEffect(getTotalSkillLevel(comboh));
        } else {
            return;
        }
        if (orbcount < ceffect.getX() + 1) {
            int neworbcount = orbcount + 1;
            if (advComboSkillLevel > 0 && ceffect.makeChanceResult()) {
                if (neworbcount < ceffect.getX() + 1) {
                    neworbcount++;

                }
            }
            EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
            stat.put(CharacterTemporaryStat.ComboCounter, neworbcount);
            setBuffedValue(CharacterTemporaryStat.ComboCounter, neworbcount);
            //int duration = ceffect.getDuration();
            //duration += (int) ((getBuffedStarttime(CharacterTemporaryStat.ComboCounter) - System.currentTimeMillis()));
            int duration = Integer.MAX_VALUE;
            client.getSession().write(BuffPacket.giveBuff(comboh.getId(), duration, stat, ceffect));
            map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, ceffect), false);
        }
    }

    public void handleOrbconsume(int howmany) {
        Skill comboh;
        switch (getJob()) {
            case 1110:
            case 1111:
            case 1112:
                comboh = SkillFactory.getSkill(11111001);
                break;
            default:
                comboh = SkillFactory.getSkill(1111002);
                break;
        }
        if (getSkillLevel(comboh) <= 0) {
            return;
        }
        MapleStatEffect ceffect = getStatForBuff(CharacterTemporaryStat.ComboCounter);
        if (ceffect == null) {
            return;

        }
        EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class
        );
        stat.put(CharacterTemporaryStat.ComboCounter, Math.max(1, getBuffedValue(CharacterTemporaryStat.ComboCounter) - howmany));
        setBuffedValue(CharacterTemporaryStat.ComboCounter, Math.max(1, getBuffedValue(CharacterTemporaryStat.ComboCounter) - howmany));
        int duration = ceffect.getDuration();
        duration += (int) ((getBuffedStarttime(CharacterTemporaryStat.ComboCounter) - System.currentTimeMillis()));
        client.getSession().write(BuffPacket.giveBuff(comboh.getId(), duration, stat, ceffect));
        map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, ceffect), false);
    }

    public void silentEnforceMaxHpMp() {
        stats.setMp(stats.getMp(), this);
        stats.setHp(stats.getHp(), true, this);
    }

    public void enforceMaxHpMp() {
        Map<MapleStat, Integer> statups = new EnumMap<MapleStat, Integer>(MapleStat.class
        );
        if (stats.getMp() > stats.getCurrentMaxMp(this.getJob())) {
            stats.setMp(stats.getMp(), this);
            statups.put(MapleStat.MP, Integer.valueOf(stats.getMp()));
        }
        if (stats.getHp() > stats.getCurrentMaxHp()) {
            stats.setHp(stats.getHp(), this);
            statups.put(MapleStat.HP, Integer.valueOf(stats.getHp()));
        }
        if (statups.size() > 0) {
            client.getSession().write(CWvsContext.OnPlayerStatChanged(statups, this));
        }
        /*
        if (GameConstants.isHayato(job)) {
            final Skill aSkill = SkillFactory.getSkill(60000067);
            if (aSkill != null) {
                int aLevel = this.getTotalSkillLevel(aSkill);
                if (aLevel > 0) {
                    final MapleStatEffect aEffect = aSkill.getEffect(aLevel);
                    if (aEffect != null) {
                        int aDamR = (aEffect.getX());
                        aDamR *= (int) (this.getStat().wdef / 100);
                        if (aDamR > 20) {
                            aDamR = 20;
                        }
                        int aDuration = 2100000000;
                        EnumMap<CharacterTemporaryStat, Integer> localstatups = new EnumMap<>(CharacterTemporaryStat.class);
                        localstatups.put(CharacterTemporaryStat.ArcaneAim, aDamR);
                        this.getClient().sendPacket(BuffPacket.giveBuff(60000067, aDuration, localstatups, aEffect));
                    }
                }
            }
        }
         */
    }

    public MapleMap getMap() {
        return map;
    }

    public MonsterBook getMonsterBook() {
        return monsterbook;
    }

    public void setMap(MapleMap newmap) {
        this.map = newmap;
    }

    public void setMap(int PmapId) {
        this.mapid = PmapId;
    }

    public int getMapId() {
        if (map != null) {
            return map.getId();
        }
        return mapid;
    }

    public byte getInitialSpawnpoint() {
        return initialSpawnPoint;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final String getBlessOfFairyOrigin() {
        return this.BlessOfFairy_Origin;
    }

    public final String getBlessOfEmpressOrigin() {
        return this.BlessOfEmpress_Origin;
    }
//the sp isnt fixed oh hold on

    public final short getLevel() {
        return level;
    }

    public final int getFame() {
        return fame;
    }

    public final int getFallCounter() {
        return fallcounter;
    }

    public final MapleClient getClient() {
        return client;
    }

    public final void setClient(final MapleClient client) {
        this.client = client;
    }

    public int getExp() {
        return exp;
    }

    public int getRemainingAp() {
        return remainingAp;
    }

    public int getRemainingSp() {
        return remainingSp[GameConstants.getSkillBook(job)]; //default
    }

    public int getRemainingSp(final int skillbook) {
        return remainingSp[skillbook];
    }

    public int[] getRemainingSps() {
        return remainingSp;
    }

    public int getRemainingSpSize() {
        int ret = 0;
        for (int i = 0; i < remainingSp.length; i++) {
            if (remainingSp[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public int getRemainingSpin() {
        int ret = 0;
        for (int i = 0; i < remainingSp.length; i++) {
            if (remainingSp[i] > 0) {
                ret = (ret + remainingSp[i]);
            }
        }
        return ret;
    }

    public void setRemainingSps(int[] s) {
        remainingSp = s;
    }

    public short getHpApUsed() {
        return hpApUsed;
    }

    public void setHidden(boolean f) {
        hide = f;
        client.getSession().write(CField.GmHide(hide));
        if (hide) {
            map.broadcastMessage(this, CField.removePlayerFromMap(getId()), false);
        } else {
            map.broadcastMessage(this, CField.OnUserEnterField(this), false);
            sendTemporaryStats();
            for (final MaplePet pet : pets) {
                if (pet.getSummoned()) {
                    map.broadcastMessage(this, CPet.showPet(this, pet, false, false), false);
                }
            }
        }
    }

    boolean hide = false;

    public boolean isHidden() {
        if (this.getBuffedValue(CharacterTemporaryStat.DarkSight) != null) {
            if (this.getBuffSource(CharacterTemporaryStat.DarkSight) == 9001004) {
                return true;
            }
        }
        return false;
    }

    public void setHpApUsed(short hpApUsed) {
        this.hpApUsed = hpApUsed;
    }

    public byte getSkin() {
        return skinColor;
    }

    public void setSkinColor(byte skinColor) {
        this.skinColor = skinColor;
    }

    public short getJob() {
        return job;
    }

    public byte getGender() {
        return gender;
    }

    public int getHair() {
        return hair;
    }

    public int getFace() {
        return face;
    }

    public int getDemonMarking() {
        return demonMarking;
    }

    public short getHcMode() {
        return hcMode;
    }

    public void setHcMode(short mode) {
        this.hcMode = mode;
    }

    public int getForgeEXP() {
        return forgeEXP;
    }

    public void setForgeEXP(int xp) {
        this.forgeEXP = xp;
    }

    public void setDemonMarking(int mark) {
        this.demonMarking = mark;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setHair(int hair) {
        this.hair = hair;
        //  updateSingleStat(MapleStat.HAIR, hair);
    }

    public void setFace(int face) {
        this.face = face;
        //    updateSingleStat(MapleStat.FACE, face);
    }

    public void setFame(int fame) {
        this.fame = fame;
    }

    public void setFallCounter(int fallcounter) {
        this.fallcounter = fallcounter;
    }

    public Point getOldPosition() {
        return old;
    }

    public void setOldPosition(Point x) {
        this.old = x;
    }

    public void setRemainingAp(int remainingAp) {
        this.remainingAp = remainingAp;
    }

    public void setRemainingSp(int remainingSp) {
        this.remainingSp[GameConstants.getSkillBook(job)] = remainingSp; //default
    }

    public void setRemainingSp(int remainingSp, final int skillbook) {
        this.remainingSp[skillbook] = remainingSp;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public void setInvincible(boolean invinc) {
        invincible = invinc;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public CheatTracker getCheatTracker() {
        return anticheat;
    }

    public BuddyList getBuddylist() {
        return buddylist;
    }

    public void addFame(int famechange) {
        this.fame += famechange;
        updateSingleStat(MapleStat.Fame, this.fame);
        /*
        getTrait(MapleTraitType.charm).addLocalExp(famechange);
        if (this.fame >= 50) {
            finishAchievement(7);
        }
         */
    }

    public void updateFame() {
        updateSingleStat(MapleStat.Fame, this.fame);
    }

    public void changeMapBanish(final int mapid, final String portal, final String msg) {
        dropMessage(5, msg);
        final MapleMap map = client.getChannelServer().getMapFactory().getMap(mapid);
        changeMap(map, map.getPortal(portal));
    }

    public void changeMap(final MapleMap to, final String portal) {
        final MapleMap map = client.getChannelServer().getMapFactory().getMap(mapid);
        changeMap(map, map.getPortal(portal));
    }

    public void changeMap(final MapleMap to, final Point pos) {
        changeMapInternal(to, pos, CField.getWarpToMap(to, 0x80, this), null);
    }

    public void changeMap(final MapleMap to) {
        changeMapInternal(to, to.getPortal(0).getPosition(), CField.getWarpToMap(to, 0, this), to.getPortal(0));
    }

    public void changeMap(final MapleMap to, final MaplePortal pto) {
        changeMapInternal(to, pto.getPosition(), CField.getWarpToMap(to, pto.getId(), this), null);
    }

    public void changeMapPortal(final MapleMap to, final MaplePortal pto) {
        changeMapInternal(to, pto.getPosition(), CField.getWarpToMap(to, pto.getId(), this), pto);
    }

    private void changeMapInternal(MapleMap to, final Point pos, byte[] warpPacket, final MaplePortal pto) {
        if (to == null) {
            return;
        }
        int nowmapid = map.getId();
        if (eventInstance != null) {
            eventInstance.changedMap(this, to.getId());
        }
        final boolean pyramid = pyramidSubway != null;
        if (map.getId() == nowmapid) {
            client.getSession().write(warpPacket);
            if (isHidden()) {
                client.getSession().write(CField.GmHide(isHidden()));
            }
            final boolean shouldChange = !isClone() && client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null;//트루
            final boolean shouldState = map.getId() == to.getId();
            MapleMap oldMap = map;
            if (shouldChange) {
                map = to;
                oldMap.removePlayer(this);
                setPosition(pos);
                setStance(0);
                clearSummonList();
                to.addPlayer(this);
                stats.relocHeal(this);
            } else {
                map.removePlayer(this);
            }
        }
        if (pyramid && pyramidSubway != null) { //checks if they had pyramid before AND after changing
            pyramidSubway.onChangeMap(this, to.getId());
        }
    }

    public void cancelChallenge() {
        if (challenge != 0 && client.getChannelServer() != null) {
            final MapleCharacter chr = client.getChannelServer().getPlayerStorage().getCharacterById(challenge);
            if (chr != null) {
                chr.dropMessage(6, getName() + " has denied your request.");
                chr.setChallenge(0);
            }
            dropMessage(6, "Denied the challenge.");
            challenge = 0;
        }
    }

    public void leaveMap(MapleMap map) {
        controlledLock.writeLock().lock();
        visibleMapObjectsLock.writeLock().lock();
        try {
            for (MapleMonster mons : controlled) {
                if (mons != null) {
                    mons.setController(null);
                    mons.setControllerHasAggro(false);
                    map.updateMonsterController(mons);
                }
            }
            controlled.clear();
            visibleMapObjects.clear();
        } finally {
            controlledLock.writeLock().unlock();
            visibleMapObjectsLock.writeLock().unlock();
        }
        if (chair != 0) {
            chair = 0;
        }
        clearLinkMid();
        cancelMapTimeLimitTask();
        cancelChallenge();
        if (!getMechDoors().isEmpty()) {
            removeMechDoor();
        }
        if (getTrade() != null) {
            MapleTrade.cancelTrade(getTrade(), client, this);
        }
    }

    public void changeJob(short newJob) {
        try {
            if (!GameConstants.isDualBlade(newJob)) {
                this.setSubcategory(0);
            }
            cancelEffectFromBuffStat(CharacterTemporaryStat.ShadowPartner);
            int oldJob = this.job;
            this.job = newJob;
            updateSingleStat(MapleStat.Job, newJob);
            if (!isGM() && GameConstants.isBeginnerJob(oldJob)) {
                resetStatsByJob(true);
                if (!GameConstants.isEvan(newJob)) {
                } else if (newJob == 2200) {
                    MapleQuest.getInstance(22100).forceStart(this, 0, null);
                    MapleQuest.getInstance(22100).forceComplete(this, 0);
                    expandInventory((byte) 1, 4);
                    expandInventory((byte) 2, 4);
                    expandInventory((byte) 3, 4);
                    expandInventory((byte) 4, 4);
                    client.getSession().write(NPCPacket.getEvanTutorial("UI/tutorial/evan/14/0"));
                    dropMessage(5, "The baby Dragon hatched and appears to have something to tell you. Click the baby Dragon to start a conversation.");
                }
            }
            if (!GameConstants.isBeginnerJob(newJob)) {
                if (GameConstants.isWildHunter(newJob)) {
                    this.getClient().getSession().write(CWvsContext.updateJaguar(this));
                }
                if (newJob == 3500) {
                    final Skill skil = SkillFactory.getSkill(30001068);
                    changeSingleSkillLevel(skil, 1, (byte) skil.getMaxLevel());
                }
                if (GameConstants.isEvan(newJob)) {
                    if (GameConstants.isPhantom(job)) {
                        if (job == 2412) {
                            final Skill skil1 = SkillFactory.getSkill(20031209);
                            changeSingleSkillLevel(skil1, 0, (byte) 0);
                            final Skill skil2 = SkillFactory.getSkill(20031210);
                            changeSingleSkillLevel(skil2, 1, (byte) skil2.getMaxLevel());
                        }
                        client.sendPacket(CUserLocal.incJudgementStack((byte) 0));
                        resetRunningStack();
                    }
                    int changeSp = (newJob == 2200 || newJob == 2210 || newJob == 2211 || newJob == 2213 ? 3 : 5);
                    if (GameConstants.isResist(job) && newJob != 3100 && newJob != 3200 && newJob != 3300 && newJob != 3500) {
                        changeSp = 3;
                    }
                    remainingSp[GameConstants.getSkillBook(newJob, level)] += changeSp;
                    client.getSession().write(InfoPacket.getSPMsg((byte) changeSp, (short) newJob));
                }
                if (newJob % 10 >= 1 && level >= 70) {
                    remainingAp += 5;
                    updateSingleStat(MapleStat.AP, remainingAp);
                }
                updateSingleStat(MapleStat.SP, 0);
            }

            int maxhp = stats.getMaxHp(), maxmp = stats.getMaxMp();

            switch (job) {
                case 100: // Warrior
                case 1100: // Soul Master
                case 2100: // Aran
                case 3200:
                case 5100: // Mihile
                case 6100:
                case 6500:
                    maxhp += Randomizer.rand(200, 250);
                    break;
                case 3100:
                    maxhp += Randomizer.rand(200, 250);
                    break;
                case 3110:
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 200: // Magician
                case 2200: //evan
                case 2210: //evan
                case 6200:
                case 4200:
                    maxmp += Randomizer.rand(100, 150);
                    break;
                case 300: // Bowman
                case 400: // Thief
                case 500: // Pirate
                case 2300:
                case 3300:
                case 3500:
                    maxhp += Randomizer.rand(100, 150);
                    maxmp += Randomizer.rand(25, 50);
                    break;
                case 110: // Fighter
                case 120: // Page
                case 130: // Spearman
                case 1110: // Soul Master
                case 2110: // Aran
                case 3210:
                case 5110: // Mihile
                case 6110:
                case 6510:
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 210: // FP
                case 220: // IL
                case 230: // Cleric
                case 6210:
                case 4210:
                    maxmp += Randomizer.rand(400, 450);
                    break;
                case 310: // Bowman
                case 320: // Crossbowman
                case 410: // Assasin
                case 420: // Bandit
                case 430: // Semi Dualer
                case 510:
                case 520:
                case 530:
                case 2310:
                case 1310: // Wind Breaker
                case 1410: // Night Walker
                case 3310:
                case 3510:
                    maxhp += Randomizer.rand(200, 250);
                    maxhp += Randomizer.rand(150, 200);
                    break;
                case 900: // GM
                case 800: // Manager
                    maxhp += 99999;
                    maxmp += 99999;
                    break;
                default:
                    maxhp += Randomizer.rand(50, 100);
                    maxhp += Randomizer.rand(50, 100);
                    break;
            }
            if (maxhp >= 99999) {
                maxhp = 99999;
            }
            if (maxmp >= 99999) {
                maxmp = 99999;
            }
            if (GameConstants.isDemon(job)) {
                maxmp = GameConstants.getMPByJob(this);
            }
            stats.setInfo(maxhp, maxmp, maxhp, maxmp);
            Map<MapleStat, Integer> statup = new EnumMap<MapleStat, Integer>(MapleStat.class
            );

            if (!client.getChannelServer().getAutoSkillMaster()) {
                int v1 = job % 100;
                int v2 = (v1 == 0 ? 0 : v1 == 10 ? 1 : v1 == 11 ? 2 : 3);
                if (GameConstants.isEvan(newJob) == false) {
                    if (GameConstants.isAdventurer(newJob) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isDualBlade(newJob) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isCannon(newJob) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isKOC(newJob) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isAran(newJob) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isResist(newJob) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isMercedes(newJob) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isDemon(newJob) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isPhantom(newJob) == true) {
                        remainingSp[v2] += 3;
                        client.sendPacket(CUserLocal.incJudgementStack((byte) 0));
                        resetRunningStack();
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isMihile(newJob) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isHayato(newJob) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isKanna(newJob) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isMukhyun(newJob) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isBeastTamer(newJob) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isLynn(newJob) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                }
            } else {
                Iterator<Integer> it = SkillFactory.getSkillsByJob(job).iterator();
                while (it.hasNext()) {
                    int skillID = it.next();
                    Skill nSkill = SkillFactory.getSkill(skillID);
                    if (nSkill != null) {
                        this.changeSingleSkillLevel(nSkill, nSkill.getMaxLevel(), (byte) nSkill.getMaxLevel());
                    }
                }
            }

            statup.put(MapleStat.MaxHP, Integer.valueOf(maxhp));
            statup.put(MapleStat.MaxMP, Integer.valueOf(maxmp));
            statup.put(MapleStat.HP, Integer.valueOf(maxhp));
            statup.put(MapleStat.MP, Integer.valueOf(maxmp));
            client.getSession().write(CWvsContext.OnPlayerStatChanged(statup, this));
            characterCard.recalcLocalStats(this);
            stats.recalcLocalStats(this, true);
            map.broadcastMessage(this, EffectPacket.showForeignEffect(getId(), 11), false);
            silentPartyUpdate();
            guildUpdate();
            familyUpdate();
            if (dragon
                    != null) {
                map.broadcastMessage(CField.removeDragon(this.id));
                dragon = null;
            }
            baseSkills();
            if (newJob >= 2200 && newJob
                    <= 2218) { //make new
                if (getBuffedValue(CharacterTemporaryStat.MonsterRiding) != null) {
                    cancelBuffStats(true, CharacterTemporaryStat.MonsterRiding);
                }
                makeDragon();
            }
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e); //all jobs throw errors :(
        }
        changeShield();
    }

    public void baseSkills() {
        Map<Skill, SkillEntry> list = new HashMap<>();
        if (GameConstants.getJobNumber(job) >= 3) { //third job.
            List<Integer> skills = SkillFactory.getSkillsByJob(job);
            if (skills != null) {
                for (int i : skills) {
                    final Skill skil = SkillFactory.getSkill(i);
                    if (skil != null && !skil.isInvisible() && skil.isSkillNeedMasterLevel() && getSkillLevel(skil) <= 0 && getMasterLevel(skil) <= 0 && skil.getMasterLevel() > 0) {
                        list.put(skil, new SkillEntry((byte) 0, (byte) skil.getMasterLevel(), SkillFactory.getDefaultSExpiry(skil)));
                    } else if (skil != null && skil.getName() != null && skil.getName().contains("메이플 용사") && getSkillLevel(skil) <= 0 && getMasterLevel(skil) <= 0) {
                        list.put(skil, new SkillEntry((byte) 0, (byte) 10, SkillFactory.getDefaultSExpiry(skil)));
                    }
                }

            }
        }
        Skill skil;
        if (GameConstants.isEvan(job)) { // evan fix magic guard
            skil = SkillFactory.getSkill(22111001);
            if (skil != null) {
                if (getSkillLevel(skil) <= 0) { // no total
                    list.put(skil, new SkillEntry((byte) 0, (byte) 20, -1));
                }
            }
        }
        if (GameConstants.isDualBlade(job)) {
            final int[] ss0 = {4321006, 4331002, 4330009, 4341004, 4341006, 4341007};
            for (int i : ss0) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total 
                        list.put(skil, new SkillEntry((byte) 0, (byte) 10, -1));
                    }
                }
            }

            final int[] ss1 = {4341011, 4341013};
            for (int i : ss1) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total 
                        list.put(skil, new SkillEntry((byte) 0, (byte) 30, -1));
                    }
                }
            }
            final int[] ss2 = {4331000, 4331006};
            for (int i : ss2) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total 
                        list.put(skil, new SkillEntry((byte) -1, (byte) 20, -1));
                    }
                }
            }
            skil = SkillFactory.getSkill(4311003);
            if (skil != null) {
                if (getSkillLevel(skil) <= 0) { // no total 
                    list.put(skil, new SkillEntry((byte) -1, (byte) 5, -1));
                }
            }
        }
        if (GameConstants.isWildHunter(job)) {
            final int[] ss = {30001061, 30001062};
            for (int i : ss) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total
                        list.put(skil, new SkillEntry((byte) 1, (byte) 1, -1));
                    }
                }
            }
        }
        if (GameConstants.isCannon(job)) {
            final int[] ss = {5321000, 5321012, 5321001, 5321010, 5320009, 5320008};
            for (int i : ss) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) < 1) {
                        list.put(skil, new SkillEntry((byte) 0, (byte) 10, -1));
                    }
                }
            }
        }
        if (GameConstants.isMercedes(job)) {
            final int[] ss = {20021000, 20021001, 20020002, 20020109, 20021110, 20020111, 20020112};
            for (int i : ss) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total
                        if (skil.getId() == 20021000 || skil.getId() == 20021001 || skil.getId() == 20020002) {
                            list.put(skil, new SkillEntry((byte) 3, (byte) 3, -1));
                        } else {
                            list.put(skil, new SkillEntry((byte) 1, (byte) 1, -1));
                        }
                    }
                }
            }
            skil = SkillFactory.getSkill(20021181);
            if (skil != null) {
                if (getSkillLevel(skil) <= 0) { // no total
                    list.put(skil, new SkillEntry((byte) -1, (byte) 0, -1));
                }
            }
            skil = SkillFactory.getSkill(20021166);
            if (skil != null) {
                if (getSkillLevel(skil) <= 0) { // no total
                    list.put(skil, new SkillEntry((byte) -1, (byte) 0, -1));
                }
            }
        }
        if (GameConstants.isDemon(job)) {
            final int[] ss1 = {30010185, 30010112, 30010111, 30010110, 30011109};
            for (int i : ss1) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total
                        list.put(skil, new SkillEntry((byte) 1, (byte) 1, -1));
                    }
                }
            }
            final int[] ss2 = {30011170, 30011169, 30011168, 30011167, 30010166, 30010184, 30010183, 30010186};
            for (int i : ss2) {
                skil = SkillFactory.getSkill(i);
                if (skil != null) {
                    if (getSkillLevel(skil) <= 0) { // no total
                        list.put(skil, new SkillEntry((byte) -1, (byte) -1, -1));
                    }
                }
            }
        }
        if (!list.isEmpty()) {
            changeSkillsLevel(list);
        }
    }

    public void makeDragon() {
        dragon = new MapleDragon(this);
        map.broadcastMessage(CField.spawnDragon(dragon));
    }

    public MapleDragon getDragon() {
        return dragon;
    }

    public void gainAp(short ap) {
        this.remainingAp += ap;
        updateSingleStat(MapleStat.AP, this.remainingAp);
    }

    public void gainSP(int sp) {
        //  this.remainingSp[GameConstants.getSkillBook(job)] += sp; //default
        this.remainingSp[GameConstants.getSkillBook(job)] += sp; // default
        updateSingleStat(MapleStat.SP, 0); // we don't care the value here
        client.getSession().write(InfoPacket.getSPMsg((byte) sp, (short) job));
    }

    public void gainSP(int sp, final int skillbook) {
        this.remainingSp[skillbook] += sp; //default
        updateSingleStat(MapleStat.SP, 0); // we don't care the value here
        client.getSession().write(InfoPacket.getSPMsg((byte) sp, (short) 0));
    }

    public void resetSP(int sp) {
        for (int i = 0; i < remainingSp.length; i++) {
            this.remainingSp[i] = sp;
        }
        updateSingleStat(MapleStat.SP, 0); // we don't care the value here
    }

    public void resetAPSP() {
        resetSP(0);
        gainAp((short) -this.remainingAp);
    }

    public List<Integer> getProfessions() {
        List<Integer> prof = new ArrayList<>();
        for (int i = 9200; i <= 9204; i++) {
            if (getProfessionLevel(id * 10000) > 0) {
                prof.add(i);
            }
        }
        return prof;
    }

    public byte getProfessionLevel(int id) {
        int ret = getSkillLevel(id);
        if (ret <= 0) {
            return 0;
        }
        return (byte) ((ret >>> 24) & 0xFF);
    }

    public short getProfessionExp(int id) {
        int ret = getSkillLevel(id);
        if (ret <= 0) {
            return 0;
        }
        return (short) (ret & 0xFFFF);
    }

    public boolean addProfessionExp(int skillID, int expGain) {
        int cLevel = this.getProfessionLevel(skillID);
        if (cLevel < 1 || cLevel > 9) {
            return false;
        }
        int aExp = 0;
        switch (cLevel) {
            case 2: {
                aExp = 100;
                break;
            }
            case 3: {
                aExp = 300;
                break;
            }
            case 4: {
                aExp = 600;
                break;
            }
            case 5: {
                aExp = 1000;
                break;
            }
            case 6: {
                aExp = 1500;
                break;
            }
            case 7: {
                aExp = 2100;
                break;
            }
            case 8: {
                aExp = 2800;
                break;
            }
            case 9: {
                aExp = 3600;
                break;
            }
        }
        int reqExp = (cLevel * 250) + aExp;
        int cExp = (this.getProfessionExp(skillID) + expGain);
        if (cExp > reqExp) {
            String cType = "";
            switch (skillID) {
                case 92000000: {
                    cType = "약초 채집";
                    break;
                }
                case 92010000: {
                    cType = "채광";
                    break;
                }
                case 92020000: {
                    cType = "장비 제작";
                    break;
                }
                case 92030000: {
                    cType = "장신구 제작";
                    break;
                }
                case 92040000: {
                    cType = "연금술";
                    break;
                }
            }
            if (this.getProfessionExp(skillID) != reqExp) {
                this.changeProfessionLevelExp(skillID, cLevel, reqExp);
                this.dropMessage(1, cType + " 숙련도가 모두 적립되었습니다. 마을의 NPC를 찾아가 레벨업 하세요.");
            }
            this.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "레벨업을 하지 않아 " + cType + " 숙련도가 적립되지 않았습니다. 마을의 NPC에게서 레벨업하세요."));
            return false;
        }
        this.changeProfessionLevelExp(skillID, cLevel, cExp);
        return true;
    }

    public void changeProfessionLevelExp(int id, int level, int exp) {
        changeSingleSkillLevel(SkillFactory.getSkill(id), ((level & 0xFF) << 24) + (exp & 0xFFFF), (byte) 10);
    }

    public void changeSingleSkillLevel(final Skill skill, int newLevel, byte newMasterlevel) { //1 month
        if (skill == null) {
            return;
        }
        changeSingleSkillLevel(skill, newLevel, newMasterlevel, SkillFactory.getDefaultSExpiry(skill));
    }

    public void changeSingleSkillLevel(final Skill skill, int newLevel, byte newMasterlevel, long expiration) {
        final Map<Skill, SkillEntry> list = new HashMap<>();
        boolean hasRecovery = false, recalculate = false;
        if (changeSkillData(skill, newLevel, newMasterlevel, expiration)) {
            list.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration));
            if (GameConstants.isRecoveryIncSkill(skill.getId())) {
                hasRecovery = true;
            }
            if (skill.getId() < 80000000) {
                recalculate = true;
            }
        }
        if (list.isEmpty()) {
            return;
        }
        switch (skill.getId()) {
            case 110:
            case 1214:
            case 10000255:
            case 10000256:
            case 10000257:
            case 10000258:
            case 10000259:
            case 20000297:
            case 20010294:
            case 20021110:
            case 20030204:
            case 20040218:
            case 20050286:
            case 30000074:
            case 30000075:
            case 30000076:
            case 30000077:
            case 30010112:
            case 30010241:
            case 30020233:
            case 40010001:
            case 40020002:
            case 50001214:
            case 60000222:
            case 60011219:
            case 60020218:
            case 100000271:
            case 110000800:
            case 140000292:
            case 150000017:
            case 150010241:
                client.getSession().write(CWvsContext.updateLinkSkills(skill.getId(), newLevel, newMasterlevel, -1));
                reUpdateStat(hasRecovery, recalculate);
                break;
            default: {
                client.getSession().write(CWvsContext.updateSkills(list));
                reUpdateStat(hasRecovery, recalculate);
                break;
            }
        }
    }

    public void changeSkillsLevel(final Map<Skill, SkillEntry> ss) {
        if (ss.isEmpty()) {
            return;
        }
        final Map<Skill, SkillEntry> list = new HashMap<>();
        boolean hasRecovery = false, recalculate = false;
        for (final Entry<Skill, SkillEntry> data : ss.entrySet()) {
            if (changeSkillData(data.getKey(), data.getValue().skillevel, data.getValue().masterlevel, data.getValue().expiration)) {
                list.put(data.getKey(), data.getValue());
                if (GameConstants.isRecoveryIncSkill(data.getKey().getId())) {
                    hasRecovery = true;
                }
                if (data.getKey().getId() < 80000000) {
                    recalculate = true;
                }
            }
        }
        if (list.isEmpty()) { // nothing is changed
            return;
        }
        client.getSession().write(CWvsContext.updateSkills(list));
        reUpdateStat(hasRecovery, recalculate);
    }

    public void reUpdateStat(boolean hasRecovery, boolean recalculate) {
        changed_skills = true;
        if (hasRecovery) {
            stats.relocHeal(this);
        }
        if (recalculate) {
            stats.recalcLocalStats(this);
        }
    }

    public boolean changeSkillData(final Skill skill, int newLevel, byte newMasterlevel, long expiration) {
        if (skill == null || (!GameConstants.isApplicableSkill(skill.getId()) && !GameConstants.isApplicableSkill_(skill.getId()))) {
            return false;
        }
        if (newLevel == 0 && newMasterlevel == 0) {
            if (skills.containsKey(skill)) {
                skills.remove(skill);
            } else {
                return false; //nothing happen
            }
        } else {
            skills.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration));
        }
        return true;
    }

    public void changeSkillLevel_Skip(final Map<Skill, SkillEntry> skill, final boolean write) { // only used for temporary skills (not saved into db)
        if (skill.isEmpty()) {
            return;
        }
        final Map<Skill, SkillEntry> newL = new HashMap<>();
        for (final Entry<Skill, SkillEntry> z : skill.entrySet()) {
            if (z.getKey() == null) {
                continue;
            }
            newL.put(z.getKey(), z.getValue());
            if (z.getValue().skillevel == 0 && z.getValue().masterlevel == 0) {
                if (skills.containsKey(z.getKey())) {
                    skills.remove(z.getKey());
                } else {
                    continue;
                }
            } else {
                skills.put(z.getKey(), z.getValue());
            }
        }
        if (write && !newL.isEmpty()) {
            client.getSession().write(CWvsContext.updateSkills(newL));
        }
    }

    public void changeSkillLevel_NoSkip(final Map<Skill, SkillEntry> skill, final boolean write) { // only used for temporary skills (not saved into db)
        if (skill.isEmpty()) {
            return;
        }
        final Map<Skill, SkillEntry> newL = new HashMap<>();
        for (final Entry<Skill, SkillEntry> z : skill.entrySet()) {
            if (z.getKey() == null) {
                continue;
            }
            newL.put(z.getKey(), z.getValue());
            if ((z.getValue().skillevel == 0 && z.getValue().masterlevel == 0) || (z.getValue().skillevel == -1 && z.getValue().masterlevel == -1)) {
                if (skills.containsKey(z.getKey())) {
                    skills.remove(z.getKey());
                } else {
                    continue;
                }
            } else {
                skills.put(z.getKey(), z.getValue());
            }
        }
        if (write && !newL.isEmpty()) {
            client.getSession().write(CWvsContext.activeupdateSkills(newL));
        }
    }

    public void playerDead() {

        if (getBuffedValue(CharacterTemporaryStat.SoulStone) != null) {
            this.setSoulStone(true);
        }

        /*
        cancelEffectFromBuffStat(CharacterTemporaryStat.ShadowPartner);
        cancelEffectFromBuffStat(CharacterTemporaryStat.Morph);
        cancelEffectFromBuffStat(CharacterTemporaryStat.Flying);
        cancelEffectFromBuffStat(CharacterTemporaryStat.MonsterRiding);
        cancelEffectFromBuffStat(CharacterTemporaryStat.Mechanic);
        cancelEffectFromBuffStat(CharacterTemporaryStat.Regen);
        cancelEffectFromBuffStat(CharacterTemporaryStat.IndieMaxHpR);
        cancelEffectFromBuffStat(CharacterTemporaryStat.IndieMaxMpR);
        cancelEffectFromBuffStat(CharacterTemporaryStat.IndieMaxHp);
        cancelEffectFromBuffStat(CharacterTemporaryStat.IndieMaxMp);
        cancelEffectFromBuffStat(CharacterTemporaryStat.EnhancedMaxHp);
        cancelEffectFromBuffStat(CharacterTemporaryStat.EnhancedMaxMp);
        cancelEffectFromBuffStat(CharacterTemporaryStat.MaxHp);
        cancelEffectFromBuffStat(CharacterTemporaryStat.MaxMp);
         */
        dispelSummons();
        checkFollow();
        dotHP = 0;
        lastDOTTime = 0;
        if (!GameConstants.isBeginnerJob(job) && !inPVP()) {
            int tempCharm = getItemQuantity(5130000, false);
            int permCharm = getItemQuantity(5130004, false);
            if (tempCharm > 0 || permCharm > 0 || this != null) {
                tempCharm--;
                if (tempCharm > 0xFF) {
                    tempCharm = 0xFF;
                }
                //client.getSession().write(EffectPacket.useCharm((byte) tempCharm, (byte) 0, true));
            } else {
                float diepercentage = 0.0f;
                int expforlevel = getNeededExp();
                if (map.isTown() || FieldLimitType.RegularExpLoss.check(map.getFieldLimit())) {
                    diepercentage = 0.01f;
                } else {
                    diepercentage = (float) (0.1f - ((traits.get(MapleTraitType.charisma).getLevel() / 20) / 100f) - (stats.expLossReduceR / 100f));
                }
                int v10 = (int) (exp - (long) ((double) expforlevel * diepercentage));
                if (v10 < 0) {
                    v10 = 0;
                }
                this.exp = v10;
            }
            this.updateSingleStat(MapleStat.Exp, this.exp);
        }
        if (!stats.checkEquipDurabilitys(this, -10000)) {
            dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
        }
        if (pyramidSubway != null) {
            stats.setHp((short) 50, this);
            pyramidSubway.fail(this);
        }
    }

    public void updatePartyMemberHP() {
        if (party != null && client.getChannelServer() != null) {
            final int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar != null && partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    final MapleCharacter other = client.getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        other.getClient().getSession().write(CField.updatePartyMemberHP(getId(), stats.getHp(), stats.getCurrentMaxHp()));
                    }
                }
            }
        }
    }

    public void receivePartyMemberHP() {
        if (party == null) {
            return;
        }
        int channel = client.getChannel();
        for (MaplePartyCharacter partychar : party.getMembers()) {
            if (partychar != null && partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                MapleCharacter other = client.getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                if (other != null) {
                    client.getSession().write(CField.updatePartyMemberHP(other.getId(), other.getStat().getHp(), other.getStat().getCurrentMaxHp()));
                }
            }
        }
    }

    public void healHP(int delta) {
        addHP(delta);
        //client.getSession().write(EffectPacket.showOwnHpHealed(delta));
        //getMap().broadcastMessage(this, EffectPacket.showHpHealed(getId(), delta), false);
    }

    public void healMP(int delta) {
        addMP(delta);//
        //client.getSession().write(EffectPacket.showOwnHpHealed(delta));
        //getMap().broadcastMessage(this, EffectPacket.showHpHealed(getId(), delta), false); //WHY SHOW HP HEALED?!
    }

    /**
     * Convenience function which adds the supplied parameter to the current hp
     * then directly does a updateSingleStat.
     *
     * @param delta
     * @see MapleCharacter#setHp(int)
     */
    public void addHP(int delta) {
        if (stats.setHp(stats.getHp() + delta, this)) {
            updateSingleStat(MapleStat.HP, stats.getHp());
        }
    }

    /**
     * Convenience function which adds the supplied parameter to the current mp
     * then directly does a updateSingleStat.
     *
     * @param delta
     * @see MapleCharacter#setMp(int)
     */
    public void addMP(int delta) {
        addMP(delta, false);
    }

    public void addMP(int delta, boolean ignore) {
        //if ((delta < 0 && GameConstants.isDemon(getJob())) || !GameConstants.isDemon(getJob()) || ignore) {
        if (stats.setMp(stats.getMp() + delta, this)) {
            updateSingleStat(MapleStat.MP, stats.getMp());
        }
        //}
    }

    public void addMPHP(int hpDiff, int mpDiff) {
        Map<MapleStat, Integer> statups = new EnumMap<MapleStat, Integer>(MapleStat.class
        );
        if (stats.setHp(stats.getHp() + hpDiff, this)) {
            statups.put(MapleStat.HP, Integer.valueOf(stats.getHp()));
        }
        if ((mpDiff
                < 0 && GameConstants.isDemon(getJob())) || !GameConstants.isDemon(getJob())) {
            if (stats.setMp(stats.getMp() + mpDiff, this)) {
                statups.put(MapleStat.MP, Integer.valueOf(stats.getMp()));
            }
        }

        if (statups.size() > 0) {
            client.getSession().write(CWvsContext.OnPlayerStatChanged(statups, this));
        }
    }

    public void updateSingleStat(MapleStat stat, int newval) {
        updateSingleStat(stat, newval, false);
    }

    /**
     * Updates a single stat of this MapleCharacter for the client. This method
     * only creates and sends an update packet, it does not update the stat
     * stored in this MapleCharacter instance.
     *
     * @param stat
     * @param newval
     * @param itemReaction
     */
    public void updateSingleStat(MapleStat stat, int newval, boolean itemReaction) {
        Map<MapleStat, Integer> statup = new EnumMap<>(MapleStat.class
        );
        statup.put(stat, newval);

        client.getSession()
                .write(CWvsContext.OnPlayerStatChanged(statup, itemReaction, this));
        //System.out.println("여기래요 10");
    }

    public void gainExp(final int total, final boolean show, final boolean inChat, final boolean white) {
        if (GameConstants.isBeginnerJob(getJob()) && getLevel() == 10) {
            return;
        }
        try {
            int prevexp = getExp();
            int needed = getNeededExp();
            int newtotal = 0;
            if (isIntern()) {
                newtotal = (int) (total);
                boolean leveled = false;
                long tot = exp + newtotal;
                if (tot >= needed) {
                    exp += newtotal;
                    levelUp();
                    leveled = true;
                    if (exp >= needed) {
                        setExp(needed - 1);
                    }
                } else {
                    exp += newtotal;
                }
                if (newtotal > 0) {
                    familyRep(prevexp, needed, leveled);
                }
            }
            if (total > 0) {
                stats.checkEquipLevels(this, total); //gms like
            }
            newtotal = (int) (total);
            boolean leveled = false;
            long tot = exp + newtotal;
            if (tot >= needed) {
                exp += newtotal;
                levelUp();
                leveled = true;
                if (exp >= needed) {
                    setExp(needed - 1);
                }
            } else {
                exp += newtotal;
            }
            if (newtotal > 0) {
                familyRep(prevexp, needed, leveled);
            }

            if (newtotal != 0) {
                if (exp < 0) { // After adding, and negative
                    if (newtotal > 0) {
                        setExp(needed);
                    } else if (newtotal < 0) {
                        setExp(0);
                    }
                }
                updateSingleStat(MapleStat.Exp, getExp());
                if (show) {
                    this.getClient().sendPacket(InfoPacket.GainEXP_Others(total, inChat, white));
                }
            }
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e); //all jobs throw errors :(
        }
    }

    public void setGmLevel(byte level) {
        this.gmLevel = level;
    }

    public void familyRep(int prevexp, int needed, boolean leveled) {
        if (mfc != null) {
            int onepercent = needed / 100;
            if (onepercent <= 0) {
                return;
            }
            int percentrep = (getExp() / onepercent - prevexp / onepercent);
            if (leveled) {
                percentrep = 100 - percentrep + (level / 2);
            }
            if (percentrep > 0) {
                int sensen = World.Family.setRep(mfc.getFamilyId(), mfc.getSeniorId(), percentrep * 10, level, name);
                if (sensen > 0) {
                    World.Family.setRep(mfc.getFamilyId(), sensen, percentrep * 5, level, name); //and we stop here
                }
            }
        }
    }

    public void gainExpMonster(int gain, final boolean show, final boolean white, final byte pty, int Class_Bonus_EXP, int ItemBonusExp, int PremiumIPBonusExp, boolean partyBonusMob, final int partyBonusRate) {
        if (!isAlive()) {
            return;
        }
        if (GameConstants.isBeginnerJob(getJob()) && getLevel() == 10) {
            return;
        }
        //int rate = client.getChannelServer().getExpRate();
        final int SelectedMobBonusExp = 0;
        int PartyBonusPercentage = 0;
        final int WeddingBonusExp = 0;
        int PartyBonusExp = 0;
        //int ItemBonusExp = 0;
        //int PremiumIPBonusExp = 0;
        int RainbowWeekEventBonusExp = 0;
        int BoomUpEventBonusExp = 0;
        final int PlusExpBuffBonusExp = 0;
        int PsdBonusExpRate = 0;
        final int IndieBonusExp = 0;
        final int RelaxBonusExp = 0;
        int InstallItemBonusExp = 0;
        int AswanWinnerBonusExp = 0;
        final int ExpByIncExpR = 0;
        final int ValuePackBonusExp = 0;
        final int ExpByIncPQExpR = 0;
        final int BaseAddExp = 0, burninginc = 0;
        final int BloodAllianceBonusExp = 0;
        final int FreezeHotEventBonusExp = 0;
        final int UserHPRateBonusExp = 0;
        mobKilledNo++;
        int total = gain + Class_Bonus_EXP + ItemBonusExp + PremiumIPBonusExp;
        long Trio_Bonus_EXP = 0;
        short percentage = 0;
        double hoursFromLogin = 0.0;
        if (mobKilledNo == 3 && ServerConstants.TRIPLE_TRIO) {
            hoursFromLogin = ((System.currentTimeMillis() - loginTime) / (1000 * 60 * 60));
            if (hoursFromLogin >= 1 && hoursFromLogin < 2) {
                percentage = 30;
            } else if (hoursFromLogin >= 2 && hoursFromLogin < 3) {
                percentage = 100;
            } else if (hoursFromLogin >= 3 && hoursFromLogin < 4) {
                percentage = 150;
            } else if (hoursFromLogin >= 4 && hoursFromLogin < 5) {
                percentage = 180;
            } else if (hoursFromLogin >= 5) {
                percentage = 200;
            }
            Trio_Bonus_EXP = (long) ((float) ((gain / 100) * percentage));
            total += Trio_Bonus_EXP;
            mobKilledNo = 0;
        }

        //열렙타임
        /*int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
         int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

         if (hour >= 18 && hour < 22) {
         BoomUpEventBonusExp += gain * 15 / 100; // 15퍼
         }

         //레인보우 위크
         if (week == Calendar.MONDAY || week == Calendar.WEDNESDAY || week == Calendar.FRIDAY || week == Calendar.SUNDAY) {
         RainbowWeekEventBonusExp += gain * 25 / 100; // 25퍼
         }*/
        boolean cMerchant = World.hasMerchant(getAccountID(), getId());

        int partyinc = 0;
        int prevexp = getExp();
        if (pty > 1) {
            final double rate = (partyBonusRate > 0 ? (partyBonusRate / 100.0) : (map == null || !partyBonusMob || map.getPartyBonusRate() <= 0 ? 0.02 : (map.getPartyBonusRate() / 100.0)));
            partyinc = (int) (((float) (gain * rate)) * (pty + (rate > 0.02 ? -1 : 1)));
            total += partyinc;
        }
        if (getSkillLevel(20021110) > 0) { //엘프의 축복
            PsdBonusExpRate += (int) (total * ((getSkillLevel(20021110) == 1 ? 10 : 15) / 100.0D));
            total += PsdBonusExpRate;
        } else if (getSkillLevel(80001040) > 0) { //엘프의 축복
            PsdBonusExpRate += (int) (total * ((getSkillLevel(80001040) == 1 ? 10 : 15) / 100.0D));
            total += PsdBonusExpRate;
        }

        if (gain > 0 && total < gain) { //just in case
            total = Integer.MAX_VALUE;
        }
        if (total > 0) {
            stats.checkEquipLevels(this, total); //gms like
        }

        /* 파티 보너스 경험치 */
        double pExp = 0.0;
        if (this.getParty() != null) {
            if (this.getPartyMembers().size() > 1) {
                pExp = (gain * 0.05 * this.getPartyMembers().size());
                PartyBonusExp = (int) Math.ceil(pExp);
            }
        }

        /* 프리미엄 PC방 보너스 경험치 */
        if (this.getPremiumTime() > System.currentTimeMillis()) {
            PremiumIPBonusExp += (int) (gain * 0.5);
            total += (int) (gain * 0.5);
        }

        /* 고용 상점 보너스 경험치 */
        if (cMerchant) {
            InstallItemBonusExp += (int) (gain * 0.1);
            total += (gain * 0.1);
        }

        /* 길드 보너스 경험치 */
        if (this.getGuild() != null) {
            if (this.getGuildId() > 6) {
                float v1 = ((float) this.getGuild().getLevel() / 100);
                double v2 = (0.05 + v1);
                AswanWinnerBonusExp += (int) (gain * v2);
                total += (gain * v2);
            }
        }

        /* 혈맹의 반지 보너스 경험치 */
        final MapleInventory mi = this.getInventory(MapleInventoryType.EQUIPPED);
        if (mi != null) {
            if (mi.findById(1114000) != null) {
                double v1 = 0.05;
                if (this.getParty() != null) {
                    int pSize = this.getPartyMembers().size();
                    if (pSize > 1) {
                        v1 *= pSize;
                    }
                }
                v1 = 5;
                BoomUpEventBonusExp += (int) (gain * v1);
                total += (gain * v1);
            }
        }

        int familyExp = 0;
        if (this.getFamilyBuffExpEffect() > 0) {
            total += (gain * 0.2);
            familyExp = (int) (gain * 0.2);
        }

        int needed = getNeededExp();

        Date nDate = new Date();
        if (nDate.getDay() == 0) {
            total += (int) (gain * 0.5);
        }
        if (nDate.getDay() == 6) {
            total += (int) (gain * 0.5);
        }

        /* 이노시스 시즌 패스 티켓 */
        if (this.haveItem(2430117)) {
            total += (int) (gain * 0.05);
        }
        /* 후원 포인트 심볼 */
        if (this.haveItem(3980004)) {
            total += (int) (gain * 0.1);
        }
        /* 홍보 포인트 심볼 */
        if (this.haveItem(3980010)) {
            total += (int) (gain * 0.1);
        }
        /* 보스 원정대 심볼 */
        if (this.haveItem(3980016)) {
            total += (int) (gain * 0.1);
        }

        boolean leveled = false;
        if (level < 255) {
            if (exp + total >= needed || exp >= needed) {
                exp += total;
                this.levelUp();
                if (level > 100) {
                    this.honourLevelUp();
                }
                leveled = true;
                if (level == 255) {
                    setExp(0);
                } else {
                    needed = getNeededExp();
                    if (exp >= needed) {
                        setExp(needed);
                    }
                }
            } else {
                exp += total;
            }
            if (total > 0) {
                familyRep(prevexp, needed, leveled);
            } else {
                return;
            }
            if (gain != 0) {
                if (exp < 0) { // After adding, and negative
                    if (gain > 0) {
                        setExp(getNeededExp());
                    } else if (gain < 0) {
                        setExp(0);
                    }
                }
                updateSingleStat(MapleStat.Exp, getExp());
                if (show) { // still show the expgain even if it's not there
                    client.getSession().write(InfoPacket.GainEXP_Monster(gain, false, white,
                            SelectedMobBonusExp,
                            PartyBonusPercentage,
                            WeddingBonusExp,
                            PartyBonusExp,
                            ItemBonusExp,
                            PremiumIPBonusExp,
                            RainbowWeekEventBonusExp,
                            BoomUpEventBonusExp,
                            PlusExpBuffBonusExp,
                            PsdBonusExpRate,
                            IndieBonusExp,
                            RelaxBonusExp,
                            InstallItemBonusExp,
                            AswanWinnerBonusExp,
                            ExpByIncExpR,
                            ValuePackBonusExp,
                            ExpByIncPQExpR,
                            BaseAddExp,
                            BloodAllianceBonusExp,
                            FreezeHotEventBonusExp,
                            UserHPRateBonusExp, burninginc, 0));
                    if (this.getFamilyBuffExpEffect() > 0) {
                        client.sendPacket(CField.EffectPacket.showBlueGatherMsg("패밀리 보너스 경험치", 0, familyExp, 0, 0));
                    }
                    if (nDate.getDay() == 0) {
                        client.sendPacket(CField.EffectPacket.showBlueGatherMsg("일요일 보너스 경험치", 0, (int) (gain * 0.5), 0, 0));
                    }
                    if (nDate.getDay() == 6) {
                        client.sendPacket(CField.EffectPacket.showBlueGatherMsg("토요일 보너스 경험치", 0, (int) (gain * 0.5), 0, 0));
                    }
                }
            }
        }
    }

    public void forceReAddItem_NoUpdate(Item item, MapleInventoryType type) {
        getInventory(type).removeSlot(item.getPosition());
        getInventory(type).addFromDB(item);
    }

    public void forceReAddItem(Item item, MapleInventoryType type) { //used for stuff like durability, item exp/level, probably owner?
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            client.getSession().write(InventoryPacket.updateSpecialItemUse(item, type == MapleInventoryType.EQUIPPED ? (byte) 1 : type.getType(), this));
        }
    }

    public void forceReAddItem_Flag(Item item, MapleInventoryType type) { //used for flags
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            client.getSession().write(InventoryPacket.updateSpecialItemUse_(item, type == MapleInventoryType.EQUIPPED ? (byte) 1 : type.getType(), this));
        }
    }

    public void silentPartyUpdate() {
        if (party != null) {
            World.Party.updateParty(party.getId(), PartyOperation.SILENT_UPDATE, new MaplePartyCharacter(this));
        }
    }

    public void setChatType(int chatType) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET chatType = ? WHERE id = ?");
            ps.setInt(1, chatType);
            ps.setInt(2, client.getPlayer().getId());
            ps.execute();
        } catch (SQLException e) {
            System.err.println("ERROR changing ChatType Values for character [ " + client.getPlayer().getName() + " ] error: " + e);
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
    }

    public int getChatType() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT chatType FROM characters WHERE id = ?");
            ps.setInt(1, client.getPlayer().getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                chatType = rs.getInt("chatType");
            }
        } catch (SQLException e) {
            System.err.println("Error getting chatType for character: " + client.getPlayer().getName() + ". error: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return chatType;
    }

    public boolean isSuperGM() {
        return gmLevel >= 3;
    }

    public boolean isIntern() {
        return gmLevel >= 1;
    }

    public boolean isGM() {
        return gmLevel >= 2;
    }

    public boolean isAdmin() {
        return gmLevel >= 4;
    }

    public int getGMLevel() {
        return gmLevel;
    }

    public void setGMLevel(byte level) {
        this.gmLevel = level;
    }

    public boolean hasGmLevel(int level) {
        return gmLevel >= level;
    }

    public final MapleInventory getInventory(MapleInventoryType type) {
        return inventory[type.ordinal()];
    }

    public final MapleInventory[] getInventorys() {
        return inventory;
    }

    public final void expirationTask(boolean pending, boolean firstLoad) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (pending) {
            if (pendingExpiration != null) {
                for (Integer z : pendingExpiration) {
                    client.getSession().write(InfoPacket.itemExpired(z.intValue()));
                    if (!firstLoad) {
                        final Pair<Integer, String> replace = ii.replaceItemInfo(z.intValue());
                        if (replace != null && replace.left > 0 && replace.right.length() > 0) {
                            dropMessage(5, replace.right);
                        }
                        if (z.intValue() == 2430117) {
                            this.updateOneInfoQuest(10000010, "nGift_Date", "0");
                            this.updateOneInfoQuest(10000011, "nGift_Value", "0");
                        }
                    }
                }
            }
            pendingExpiration = null;
            if (pendingSkills != null) {
                client.getSession().write(CWvsContext.updateSkills(pendingSkills));
                for (Skill z : pendingSkills.keySet()) {
                    client.getSession().write(CWvsContext.serverNotice(5, "[" + SkillFactory.getSkillName(z.getId()) + "] skill has expired and will not be available for use."));
                }
            } //not real msg
            pendingSkills = null;
            return;
        }
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        long expiration;
        final List<Integer> ret = new ArrayList<Integer>();
        final long currenttime = System.currentTimeMillis();
        final List<Triple<MapleInventoryType, Item, Boolean>> toberemove = new ArrayList<Triple<MapleInventoryType, Item, Boolean>>(); // This is here to prevent deadlock.
        final List<Item> tobeunlock = new ArrayList<Item>(); // This is here to prevent deadlock.

        for (final MapleInventoryType inv : MapleInventoryType.values()) {
            for (final Item item : getInventory(inv)) {
                expiration = item.getExpiration();

                if ((expiration != -1 && !GameConstants.isPet(item.getItemId()) && currenttime > expiration) || (firstLoad && ii.isLogoutExpire(item.getItemId()))) {
                    if (ItemFlag.Locked.check(item.getFlag())) {
                        tobeunlock.add(item);
                    } else if (currenttime > expiration) {
                        toberemove.add(new Triple<MapleInventoryType, Item, Boolean>(inv, item, false));
                    }
                } else if (item.getItemId() == 5000054 && item.getPet() != null && item.getPet().getSecondsLeft() <= 0) {
                    toberemove.add(new Triple<MapleInventoryType, Item, Boolean>(inv, item, false));
                } else if (item.getPosition() == -59) {
                    if (stat == null || stat.getCustomData() == null || Long.parseLong(stat.getCustomData()) < currenttime) {
                        toberemove.add(new Triple<MapleInventoryType, Item, Boolean>(inv, item, true));
                    }
                }
            }
        }
        Item item;
        for (final Triple<MapleInventoryType, Item, Boolean> itemz : toberemove) {
            item = itemz.getMid();
            getInventory(itemz.getLeft()).removeItem(item.getPosition(), item.getQuantity(), false);
            if (itemz.getRight() && getInventory(GameConstants.getInventoryType(item.getItemId())).getNextFreeSlot() > -1) {
                item.setPosition(getInventory(GameConstants.getInventoryType(item.getItemId())).getNextFreeSlot());
                getInventory(GameConstants.getInventoryType(item.getItemId())).addFromDB(item);
            } else {
                ret.add(item.getItemId());
            }
            if (!firstLoad) {
                final Pair<Integer, String> replace = ii.replaceItemInfo(item.getItemId());
                if (replace != null && replace.left > 0) {
                    Item theNewItem = null;
                    if (GameConstants.getInventoryType(replace.left) == MapleInventoryType.EQUIP) {
                        theNewItem = ii.getEquipById(replace.left);
                        theNewItem.setPosition(item.getPosition());
                    } else {
                        theNewItem = new Item(replace.left, item.getPosition(), (short) 1, (byte) 0);
                    }
                    getInventory(itemz.getLeft()).addFromDB(theNewItem);
                }
            }
        }
        for (final Item itemz : tobeunlock) {
            itemz.setExpiration(-1);
            itemz.setFlag((byte) (itemz.getFlag() - ItemFlag.Locked.getValue()));
        }
        this.pendingExpiration = ret;

        final Map<Skill, SkillEntry> skilz = new HashMap<>();
        final List<Skill> toberem = new ArrayList<Skill>();
        for (Entry<Skill, SkillEntry> skil : skills.entrySet()) {
            if (skil.getValue().expiration != -1 && currenttime > skil.getValue().expiration) {
                toberem.add(skil.getKey());
            }
        }
        for (Skill skil : toberem) {
            skilz.put(skil, new SkillEntry(0, (byte) 0, -1));
            this.skills.remove(skil);
            changed_skills = true;
        }
        this.pendingSkills = skilz;
        if (stat != null && stat.getCustomData() != null && Long.parseLong(stat.getCustomData()) < currenttime) { //expired bro
            quests.remove(MapleQuest.getInstance(7830));
            quests.remove(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        }
    }

    public void refreshBattleshipHP() {
        if (getJob() == 422) {
            Skill skill = SkillFactory.getSkill(4221013);
            int skillLevel = getTotalSkillLevel(GameConstants.getLinkedAranSkill(4221013));
            MapleStatEffect effect = skill.getEffect(skillLevel);
            EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class
            );
            stat.put(CharacterTemporaryStat.KillingPoint, currentBattleshipHP());
            getMap().broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, effect), false);
            client.getSession().write(CWvsContext.giveKilling(currentBattleshipHP()));
        }
    }

    public MapleShop getShop() {
        return shop;
    }

    public void setShop(MapleShop shop) {
        this.shop = shop;
    }

    public int getMeso() {
        return meso;
    }

    public final int[] getSavedLocations() {
        return savedLocations;
    }

    public int getSavedLocation(SavedLocationType type) {
        return savedLocations[type.getValue()];
    }

    public void saveLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = getMapId();
        changed_savedlocations = true;
    }

    public void saveLocation(SavedLocationType type, int mapz) {
        savedLocations[type.getValue()] = mapz;
        changed_savedlocations = true;
    }

    public void clearSavedLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = -1;
        changed_savedlocations = true;
    }

    public void gainItem(int code, int quantity) {
        MapleInventoryManipulator.addById(client, code, (short) quantity, "");
    }

    public void gainMeso(int gain, boolean show) {
        gainMeso(gain, show, false);
    }

    public void gainMeso(int gain, boolean show, boolean inChat) {
        if ((meso + gain < 0) || (meso + gain) > Integer.MAX_VALUE) {
            meso -= 2000000000;
            this.gainItem(4310116, 20);
            String say = "소지 중인 메소가 2,147,483,647 메소를 초과하여 [2,000,000,000 메소]가 [메소 돈 주머니 20개]로 변환되었습니다.";
            this.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, say));
        }
        //  if (m == true) {
        meso += gain;
        updateSingleStat(MapleStat.Meso, meso, false);
        //client.getSession().write(CWvsContext.enableActions());
        if (show) {
            client.getSession().write(InfoPacket.showMesoGain(gain, inChat));
        }
        //  } else {
        //     meso -= gain;
        //     updateSingleStat(MapleStat.MESO, meso, false);
        //     //client.getSession().write(CWvsContext.enableActions());
        //     if (show) {
        //         client.getSession().write(InfoPacket.showMesoGain(gain, inChat));
        //     }
        //  }
    }

    // 백업 여기가 문제임
    public void controlMonster(MapleMonster monster, boolean aggro) {
        if (monster == null) {
            return;
        }
        monster.setController(this);
        controlledLock.writeLock().lock();
        try {
            controlled.add(monster);
        } finally {
            controlledLock.writeLock().unlock();
        }
        client.sendPacket(CMobPool.controlMonster(monster, false, aggro, false));
        monster.sendStatus(client);
    }

    public void stopControllingMonster(MapleMonster monster) {
        if (clone || monster == null) {
            return;
        }
        controlledLock.writeLock().lock();
        try {
            if (controlled.contains(monster)) {
                controlled.remove(monster);
            }
        } finally {
            controlledLock.writeLock().unlock();
        }
    }

    public void checkMonsterAggro(MapleMonster monster) {
        if (clone || monster == null) {
            return;
        }
        if (monster.getController() == this) {
            monster.setControllerHasAggro(true);
        } else {
            monster.switchController(this, true);
        }
    }

    public int getControlledSize() {
        return controlled.size();
    }

    public int getAccountID() {
        return accountid;
    }

    public void mobKilled(final int id, final int skillID) {
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() != 1 || !q.hasMobKills()) {
                continue;
            }
            //System.out.println("11111");
            if (q.mobKilled(id, skillID)) {
                client.getSession().write(InfoPacket.updateQuestMobKills(q));
                //System.out.println("aaa");
                if (q.getQuest().canComplete(this, null)) {
                    client.getSession().write(CWvsContext.getShowQuestCompletion(q.getQuest().getId()));
                }
            }
        }
    }

    public final List<MapleQuestStatus> getStartedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<MapleQuestStatus>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 1 && !q.isCustom() && !q.getQuest().isBlocked()) {
                ret.add(q);
            }
        }
        return ret;
    }

    public final List<MapleQuestStatus> getCompletedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<MapleQuestStatus>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !q.isCustom() && !q.getQuest().isBlocked()) {
                ret.add(q);
            }
        }
        return ret;
    }

    public final List<Pair<Integer, Long>> getCompletedMedals() {
        List<Pair<Integer, Long>> ret = new ArrayList<Pair<Integer, Long>>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !q.isCustom() && !q.getQuest().isBlocked() && q.getQuest().getMedalItem() > 0 && GameConstants.getInventoryType(q.getQuest().getMedalItem()) == MapleInventoryType.EQUIP) {
                ret.add(new Pair<Integer, Long>(q.getQuest().getId(), q.getCompletionTime()));
            }
        }
        return ret;
    }

    public Map<Skill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    public int getTotalSkillLevel(final Skill skill) {
        if (skill == null) {
            return 0;
        }
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return Math.min(skill.getTrueMax(), ret.skillevel + (skill.isBeginnerSkill() ? 0 : (stats.combatOrders + (skill.getMaxLevel() > 10 ? stats.incAllskill : 0) + stats.getSkillIncrement(skill.getId()))));
    }

    public int getAllSkillLevels() {
        int rett = 0;
        for (Entry<Skill, SkillEntry> ret : skills.entrySet()) {
            if (!ret.getKey().isBeginnerSkill() && !ret.getKey().isSpecialSkill() && ret.getValue().skillevel > 0) {
                rett += ret.getValue().skillevel;
            }
        }
        return rett;
    }

    public long getSkillExpiry(final Skill skill) {
        if (skill == null) {
            return 0;
        }
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return ret.expiration;
    }

    public int getSkillLevel(final Skill skill) {
        if (skill == null) {
            return 0;
        }
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return ret.skillevel;
    }

    public byte getMasterLevel(final int skill) {
        return getMasterLevel(SkillFactory.getSkill(skill));
    }

    public byte getMasterLevel(final Skill skill) {
        final SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }

    public void levelUpTo10() {
        for (int i = level; i < 10; i++) {
            levelUp();
        }
    }

    public void forfeitQuest(int idd) {
        MapleQuest.getInstance(idd).forfeit(this);
    }

    public void openScript(int npcid, String js) {//포스용
        NPCScriptManager.getInstance().start(client, npcid, js);
    }

    public void levelUp() {

        if (GameConstants.isKOC(job) && level <= 70) {
            remainingAp += 6;
        } else {
            remainingAp += 5;
        }

        int mHp = stats.getMaxHp();
        int mMp = stats.getMaxMp();
        if (GameConstants.isBeginnerJob(job)) {
            mHp += 15;
            mMp += 10;
        } else if (job >= 3100 && job <= 3112) {
            mHp += 50;
        } else if ((job >= 100 && job <= 132) || (job >= 1100 && job <= 1111) || (job >= 2100 && job <= 2112)) {
            mHp += 50;
            mMp += 5;
        } else if ((job >= 200 && job <= 232) || (job >= 1200 && job <= 1211) || (job >= 2200 && job <= 2218)) {
            mHp += 12;
            mMp += 50;
        } else if (job >= 3200 && job <= 3212) {
            mHp += 22;
            mMp += 43;
        } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1311) || (job >= 1400 && job <= 1411) || (job >= 3300 && job <= 3312) || (job >= 2300 && job <= 2312)) { // Bowman, Thief, Wind Breaker and Night Walker
            mHp += 22;
            mMp += 15;
        } else if ((job >= 510 && job <= 512) || (job >= 1510 && job <= 1512)) {
            mHp += 39;
            mMp += 20;
        } else if ((job >= 500 && job <= 532) || (job >= 3500 && job <= 3512) || job == 1500) {
            mHp += 22;
            mMp += 20;
        } else {
            mHp += 40;
            mMp += 23;
        }

        exp -= getNeededExp();
        level += 1;

        if (GameConstants.isKOC(job) && level < 120 && level > 10) {
            exp += getNeededExp() / 10;
        }

        if (level == 255 || (level == 255 && GameConstants.isKOC(getJob()) == true)) {
            int map = 0;
            int npc = 0;
            int number = 0;
            if (GameConstants.isAdventurer(getJob()) == true) {
                number = 10;
                if ((getJob() / 100) == 1) {
                    npc = 9901701;
                    map = 102000005;
                }
                if ((getJob() / 100) == 2) {
                    npc = 9901710;
                    map = 101000005;
                }
                if ((getJob() / 100) == 3) {
                    npc = 9901720;
                    map = 100000205;
                }
                if ((getJob() / 100) == 4) {
                    npc = 9901730;
                    map = 103000009;
                }
                if ((getJob() / 100) == 5) {
                    npc = 9901740;
                    map = 120000105;
                }
            }
            if (GameConstants.isKOC(getJob()) == true) {
                npc = 9901500;
                map = 130000100;
                number = 20;
            }
            if (GameConstants.isAran(getJob()) == true) {
                npc = 9901600;
                map = 140010110;
                number = 17;
            }
            if (GameConstants.isEvan(getJob()) == true) {
                npc = 9901910;
                map = 100030301;
                number = 10;
            }
            if (GameConstants.isResist(getJob()) == true) {
            }
            MapleMap nmap = getClient().getChannelServer().getMapFactory().getMap(map);
            int count = 0;
            for (PlayerNPC pnpc : getClient().getChannelServer().getAllPlayerNPC()) {
                if (pnpc.getMapId() == map) {
                    ++count;
                }
            }
            if (count < number) {
                npc += count;
                MapleNPC npctemplate = nmap.getNPCById(npc);
                if (null != npctemplate) {
                    PlayerNPC newpnpc = new PlayerNPC(this, this.getName(), npc, nmap, npctemplate.getTruePosition().x, npctemplate.getTruePosition().y, npctemplate.getF(), npctemplate.getFh());
                    newpnpc.addToServer();
                    PlayerNPC.sendBroadcastModifiedNPC(this, nmap, npc, false);
                }
            }
        }

        this.exp = 0;

        mHp = Math.min(99999, Math.abs(mHp));
        mMp = Math.min(99999, Math.abs(mMp));

        if (GameConstants.isDemon(job)) {
            mMp = GameConstants.getMPByJob(this);
        }

        final Map<MapleStat, Integer> statup = new EnumMap<>(MapleStat.class
        );
        statup.put(MapleStat.MaxHP, mHp);
        statup.put(MapleStat.MaxMP, mMp);
        statup.put(MapleStat.HP, mHp);
        statup.put(MapleStat.MP, mMp);
        statup.put(MapleStat.Exp, exp);
        statup.put(MapleStat.Level, (int) level);
        updateSingleStat(MapleStat.SP, 0);

        if (level < 10) {
            stats.str += remainingAp;
            remainingAp = 0;
            statup.put(MapleStat.Str, stats.getStr());
        }

        if (client.getChannelServer().getAutoChangeJob()) {
            switch (level) {
                case 20: {
                    switch (job) {
                        case 400: {
                            if (this.getSubcategory() != 0) {
                                this.changeJob((short) 430);
                            }
                            break;
                        }
                    }
                    break;
                }
                case 30: {
                    switch (job) {
                        case 430: {
                            this.changeJob((short) 431);
                            break;
                        }
                        case 1100: {
                            this.changeJob((short) 1110);
                            break;
                        }
                        case 1200: {
                            this.changeJob((short) 1210);
                            break;
                        }
                        case 1300: {
                            this.changeJob((short) 1310);
                            break;
                        }
                        case 1400: {
                            this.changeJob((short) 1410);
                            break;
                        }
                        case 1500: {
                            this.changeJob((short) 1510);
                            break;
                        }
                        case 3200: {
                            this.changeJob((short) 3210);
                            break;
                        }
                        case 3300: {
                            this.changeJob((short) 3310);
                            break;
                        }
                        case 3500: {
                            this.changeJob((short) 3510);
                            break;
                        }
                    }
                    break;
                }
                case 55: {
                    switch (job) {
                        case 431: {
                            this.changeJob((short) 432);
                            break;
                        }
                    }
                    break;
                }
                case 70: {
                    switch (job) {
                        case 432: {
                            this.changeJob((short) 433);
                            break;
                        }
                        case 110: {
                            this.changeJob((short) 111);
                            break;
                        }
                        case 120: {
                            this.changeJob((short) 121);
                            break;
                        }
                        case 130: {
                            this.changeJob((short) 131);
                            break;
                        }
                        case 210: {
                            this.changeJob((short) 211);
                            break;
                        }
                        case 220: {
                            this.changeJob((short) 221);
                            break;
                        }
                        case 230: {
                            this.changeJob((short) 231);
                            break;
                        }
                        case 310: {
                            this.changeJob((short) 311);
                            break;
                        }
                        case 320: {
                            this.changeJob((short) 321);
                            break;
                        }
                        case 410: {
                            this.changeJob((short) 411);
                            break;
                        }
                        case 420: {
                            this.changeJob((short) 421);
                            break;
                        }
                        case 510: {
                            this.changeJob((short) 511);
                            break;
                        }
                        case 520: {
                            this.changeJob((short) 521);
                            break;
                        }
                        case 1110: {
                            this.changeJob((short) 1111);
                            break;
                        }
                        case 1210: {
                            this.changeJob((short) 1211);
                            break;
                        }
                        case 1310: {
                            this.changeJob((short) 1311);
                            break;
                        }
                        case 1410: {
                            this.changeJob((short) 1411);
                            break;
                        }
                        case 1510: {
                            this.changeJob((short) 1511);
                            break;
                        }
                        case 3210: {
                            this.changeJob((short) 3211);
                            break;
                        }
                        case 3310: {
                            this.changeJob((short) 3311);
                            break;
                        }
                        case 3510: {
                            this.changeJob((short) 3511);
                            break;
                        }
                    }
                    break;
                }
                case 120: {
                    switch (job) {
                        case 433: {
                            this.changeJob((short) 434);
                            break;
                        }
                        case 111: {
                            this.changeJob((short) 112);
                            break;
                        }
                        case 121: {
                            this.changeJob((short) 122);
                            break;
                        }
                        case 131: {
                            this.changeJob((short) 132);
                            break;
                        }
                        case 211: {
                            this.changeJob((short) 212);
                            break;
                        }
                        case 221: {
                            this.changeJob((short) 222);
                            break;
                        }
                        case 231: {
                            this.changeJob((short) 232);
                            break;
                        }
                        case 311: {
                            this.changeJob((short) 312);
                            break;
                        }
                        case 321: {
                            this.changeJob((short) 322);
                            break;
                        }
                        case 411: {
                            this.changeJob((short) 412);
                            break;
                        }
                        case 421: {
                            this.changeJob((short) 422);
                            break;
                        }
                        case 511: {
                            this.changeJob((short) 512);
                            break;
                        }
                        case 521: {
                            this.changeJob((short) 522);
                            break;
                        }
                        case 1111: {
                            this.changeJob((short) 1112);
                            break;
                        }
                        case 1211: {
                            this.changeJob((short) 1212);
                            break;
                        }
                        case 1311: {
                            this.changeJob((short) 1312);
                            break;
                        }
                        case 1411: {
                            this.changeJob((short) 1412);
                            break;
                        }
                        case 1511: {
                            this.changeJob((short) 1512);
                            break;
                        }
                        case 3211: {
                            this.changeJob((short) 3212);
                            break;
                        }
                        case 3311: {
                            this.changeJob((short) 3312);
                            break;
                        }
                        case 3511: {
                            this.changeJob((short) 3512);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        if (GameConstants.isMercedes(job) == true) {
            switch (level) {
                case 70: {
                    this.changeJob((short) 2311);
                    break;
                }
                case 120: {
                    this.changeJob((short) 2312);
                    break;
                }
            }
        }

        if (GameConstants.isHayato(job) == true) {
            switch (level) {
                case 30: {
                    this.changeJob((short) 6110);
                    break;
                }
                case 70: {
                    this.changeJob((short) 6111);
                    break;
                }
                case 120: {
                    this.changeJob((short) 6112);
                    break;
                }
            }
        }

        if (GameConstants.isKanna(job) == true) {
            switch (level) {
                case 30: {
                    this.changeJob((short) 6210);
                    break;
                }
                case 70: {
                    this.changeJob((short) 6211);
                    break;
                }
                case 120: {
                    this.changeJob((short) 6212);
                    break;
                }
            }
        }

        if (GameConstants.isMukhyun(job) == true) {
            switch (level) {
                case 30: {
                    this.changeJob((short) 6510);
                    break;
                }
                case 70: {
                    this.changeJob((short) 6511);
                    break;
                }
                case 120: {
                    this.changeJob((short) 6512);
                    break;
                }
            }
        }

        if (GameConstants.isEvan(job) == true) {
            int monsterRiding = 0;
            switch (level) {
                case 10: {
                    this.changeJob((short) 2200);
                    this.resetStats(4, 4, 4, 4);
                    String args[] = {"UI/tutorial/evan/14/0"};
                    client.sendPacket(CField.NPCPacket.tutorialUI(args));
                    this.dropMessage(5, "부화기에 들어있던 알에서 아기 드래곤이 깨어났다.");
                    this.dropMessage(5, "드래곤의 스킬을 올릴 수 있는 SP 3을 획득했다.");
                    this.dropMessage(5, "인벤토리의 슬롯이 늘어났다.");
                    this.dropMessage(5, "아기 드래곤이 뭔가 하고 싶은 말이 있는 것 같다. 아기 드래곤을 클릭해 말을 걸어 보자.");
                    this.forceCompleteQuest(22100);
                    break;
                }
                case 20: {
                    this.changeJob((short) 2210);
                    this.forceCompleteQuest(22101);
                    break;
                }
                case 30: {
                    this.changeJob((short) 2211);
                    this.forceCompleteQuest(22102);
                    break;
                }
                case 40: {
                    this.changeJob((short) 2212);
                    break;
                }
                case 50: {
                    this.changeJob((short) 2213);
                    this.forceCompleteQuest(22104);
                    monsterRiding = 1902040;
                    break;
                }
                case 60: {
                    this.changeJob((short) 2214);
                    break;
                }
                case 80: {
                    this.changeJob((short) 2215);
                    this.forceCompleteQuest(22106);
                    monsterRiding = 1902041;
                    break;
                }
                case 100: {
                    this.changeJob((short) 2216);
                    break;
                }
                case 120: {
                    this.changeJob((short) 2217);
                    this.forceCompleteQuest(22108);
                    monsterRiding = 1902042;
                    break;
                }
                case 160: {
                    this.changeJob((short) 2218);
                    break;
                }
            }
            if (monsterRiding > 0) {
                if (getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18) != null) {
                    getInventory(MapleInventoryType.EQUIPPED).removeSlot((short) -18);
                }
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final Item item = ii.getEquipById(monsterRiding);
                item.setPosition((short) -18);
                getInventory(MapleInventoryType.EQUIPPED).addFromDB(item);
                equipChanged();
                client.getSession().write(CWvsContext.InventoryPacket.updateSpecialItemUse_(item, (byte) 1, this));
            }
        }

        if (!client.getChannelServer().getAutoSkillMaster()) {
            if (level > 10) {
                int v1 = job % 100;
                int v2 = (v1 == 0 ? 0 : v1 == 10 ? 1 : v1 == 11 ? 2 : 3);
                if (GameConstants.isEvan(job)) {
                    remainingSp[GameConstants.getSkillBook(job, level)] += 3;
                    statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job, level)]);
                } else {
                    if (GameConstants.isAdventurer(job) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isDualBlade(job) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isCannon(job) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isKOC(job) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isAran(job) == true) {
                        remainingSp[0] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isResist(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isMercedes(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isDemon(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isPhantom(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isMihile(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isHayato(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isKanna(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isMukhyun(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isBeastTamer(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                    if (GameConstants.isLynn(job) == true) {
                        remainingSp[v2] += 3;
                        statup.put(MapleStat.SP, remainingSp[GameConstants.getSkillBook(job)]);
                    }
                }
            }
        }

        statup.put(MapleStat.AP, remainingAp);
        stats.setInfo(mHp, mMp, mHp, mMp);
        client.getSession().write(CWvsContext.OnPlayerStatChanged(statup, this));
        map.broadcastMessage(this, EffectPacket.showForeignEffect(getId(), 0), false);
        characterCard.recalcLocalStats(this);
        stats.recalcLocalStats(this, true);

        silentPartyUpdate();
        guildUpdate();
        familyUpdate();

        int beatriceGuildId[] = {1, 2, 3, 4, 5, 6};
        if (this.getLevel() == 15) {
            for (int i : beatriceGuildId) {
                this.addGuildMember(i, false);
            }
            if (getGuildId() != 0) {
                this.dropMessage(5, "" + ServerConstants.server_Name_Source + " 초보자 길드에 가입 되었습니다.");
            } else {
                this.dropMessage(5, "모든 " + ServerConstants.server_Name_Source + " 초보자 길드의 정원이 초과 되었습니다.");
            }
        }
        /* 주의 */
        if (!this.isGM()) {
            if (this.getLevel() > 30) {
                long nTime = ((System.currentTimeMillis() / 1000) - lUPTime);
                if (nTime < 15) {
                    String nStr = "(주의) 캐릭터 : (Lv." + this.getLevel() + ") " + this.getName() + " | 맵 : " + this.getMap().getMapName() + " | 시간 : " + nTime + "초";
                    World.Broadcast.broadcastGMMessage(CUserLocal.chatMsg(ChatType.GroupParty, nStr));
                }
                lUPTime = (System.currentTimeMillis() / 1000);
            }
        }
    }

    public boolean addGuildMember(int gid, boolean check) {
        if (this.getGuildId() > 0) {
            return false;
        } else {
            int guildId = gid;
            int cid = this.getId();
            if (cid != this.getId()) {
                return false;
            } else {
                this.setGuildId(guildId);
                this.setGuildRank((byte) 5);
                int s = World.Guild.addGuildMember(getMGC());
                if (s == 0 && check) {
                    return false;
                } else if (s == 0 && !check) {
                    this.setGuildId(0);
                    return false;
                }
                this.getClient().sendPacket(CWvsContext.GuildPacket.showGuildInfo(this));
                final MapleGuild gs = World.Guild.getGuild(guildId);
                for (byte[] pack : World.Alliance.getAllianceInfo(gs.getAllianceId(), true)) {
                    if (pack != null) {
                        this.getClient().getSession().write(pack);
                    }
                }
                this.saveGuildStatus();
                this.getMap().broadcastMessage(CField.loadGuildName(this));
                this.getMap().broadcastMessage(CField.loadGuildIcon(this));
            }
        }
        return true;
    }

    public void maxskill(int i) {
        MapleData data = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Skill.wz")).getData(StringUtil.getLeftPaddedStr("" + i, '0', 3) + ".img");
        final Skill skills = null;
        byte maxLevel = 0;
        for (MapleData skill : data) {
            if (skill != null) {

                for (MapleData skillId : skill.getChildren()) {
                    if (!skillId.getName().equals("icon")) {
                        maxLevel = (byte) MapleDataTool.getIntConvert("maxLevel", skillId.getChildByPath("common"), 0);
                        if (MapleDataTool.getIntConvert("invisible", skillId, 0) == 0) { //스킬창에 안보이는 스킬은 올리지않음
                            if (getLevel() >= MapleDataTool.getIntConvert("reqLev", skillId, 0)) {
                                changeSkillLevel_NoSkip(SkillFactory.getSkill(Integer.parseInt(skillId.getName())), (byte) 0, (byte) 10);
                            }
                        }
                    }
                }
            }
        }
    }

    public void changeShield() {
        int shieldId = 0;
        if (GameConstants.isDemon(job)) {
            shieldId = (getLevel() > 119 ? 1099004 : getLevel() > 69 ? 1099003 : getLevel() > 29 ? 1099002 : getLevel() > 9 ? 1099000 : 0);
        }
        if (GameConstants.isMercedes(job)) {
            shieldId = (getLevel() > 119 ? 1352003 : getLevel() > 69 ? 1352002 : getLevel() > 29 ? 1352001 : getLevel() > 9 ? 1352000 : 0);
        }
        if (GameConstants.isPhantom(job)) {
            shieldId = (getLevel() > 119 ? 1352103 : getLevel() > 69 ? 1352102 : getLevel() > 29 ? 1352101 : getLevel() > 9 ? 1352100 : 0);
        }
        if (GameConstants.isMihile(job)) {
            shieldId = (getLevel() > 119 ? 1098003 : getLevel() > 69 ? 1098002 : getLevel() > 29 ? 1098001 : getLevel() > 9 ? 1098000 : 0);
        }
        if (shieldId != 0) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            Equip nextShield = (Equip) ii.getEquipById(shieldId);
            Equip prevShield = (Equip) this.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
            if (prevShield != null) {
                if (prevShield.getItemId() != 0) {
                    if (prevShield.getPotentialByLine(0) != 0) {
                        nextShield.setPotentialByLine(0, prevShield.getPotentialByLine(0));
                        this.dropMessage(5, "<" + ii.getName(prevShield.getItemId()) + "> 의 잠재 능력이 <" + ii.getName(shieldId) + "> 로 전수되었습니다.");
                    }
                    if (prevShield.getPotentialByLine(1) != 0) {
                        nextShield.setPotentialByLine(1, prevShield.getPotentialByLine(1));
                    }
                    if (prevShield.getPotentialByLine(2) != 0) {
                        nextShield.setPotentialByLine(2, prevShield.getPotentialByLine(2));
                    }
                }
            }
            nextShield.setPosition((short) -10);
            getInventory(MapleInventoryType.EQUIPPED).addFromDB(nextShield);
            equipChanged();
            client.getSession().write(CWvsContext.InventoryPacket.updateSpecialItemUse_(nextShield, (byte) 1, this));
        }
    }

    public void changeKeybinding(int key, byte type, int action) {
        if (type != 0) {
            keylayout.Layout().put(Integer.valueOf(key), new Pair<Byte, Integer>(type, action));
        } else {
            keylayout.Layout().remove(Integer.valueOf(key));
        }
    }

    public void sendMacros() {
        for (int i = 0; i < 5; i++) {
            this.getClient().sendPacket(CField.getMacros(skillMacros));
            break;
        }
    }

    public void updateMacros(int position, SkillMacro updateMacro) {
        skillMacros[position] = updateMacro;
        changed_skillmacros = true;
    }

    public final SkillMacro[] getMacros() {
        return skillMacros;
    }

    public void tempban(MapleCharacter user, Calendar duration) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
            ps.setString(1, user.getClient().getSessionIPAddress());
            ps.execute();
            ps.close();
            user.getClient().sclose();
            ps = con.prepareStatement("UPDATE accounts SET banned = ? WHERE id = ?");
            ps.setInt(1, 1);
            ps.setInt(2, user.getAccountID());
            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            System.err.println("Error while tempbanning" + ex);
        }
    }

    public int getMaxHp() {
        return getStat().getMaxHp();
    }

    public int getMaxMp() {
        return getStat().getMaxMp();
    }

    public void setHp(int amount) {
        getStat().setHp(amount, this);
    }

    public void setMp(int amount) {
        getStat().setMp(amount, this);
    }

    /**
     * Oid of players is always = the cid
     */
    @Override
    public int getObjectId() {
        return getId();
    }

    /**
     * Throws unsupported operation exception, oid of players is read only
     */
    @Override
    public void setObjectId(int id) {
        throw new UnsupportedOperationException();
    }

    public MapleStorage getStorage() {
        return storage;
    }

    public void addVisibleMapObject(MapleMapObject mo) {
        visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.add(mo);
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public void removeVisibleMapObject(MapleMapObject mo) {
        if (clone) {
            return;
        }
        visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.remove(mo);
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public boolean isMapObjectVisible(MapleMapObject mo) {
        visibleMapObjectsLock.readLock().lock();
        try {
            return !clone && visibleMapObjects.contains(mo);
        } finally {
            visibleMapObjectsLock.readLock().unlock();
        }
    }

    public List<byte[]> getPendingBuffPackets() {
        return buffStorage;
    }

    public void setPendingBuffPackets(List<byte[]> b) {
        buffStorage = b;
    }

    public Collection<MapleMapObject> getAndWriteLockVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().lock();
        return visibleMapObjects;
    }

    public void unlockWriteVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().unlock();
    }

    public boolean isAlive() {
        return stats.getHp() > 0;
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(CField.removePlayerFromMap(this.getObjectId()));
        if (SurPlusTask != null) {
            // startXenonSupply(false);
        }
        //don't need this, client takes care of it
        /*
         * if (dragon != null) {
         * client.getSession().write(CField.removeDragon(this.getId())); } if
         * (android != null) {
         * client.getSession().write(CField.deactivateAndroid(this.getId())); }
         * if (summonedFamiliar != null) {
         * client.getSession().write(CField.removeFamiliar(this.getId())); }
         */
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (client.getPlayer().allowedToTarget(this)) {
            client.sendPacket(CField.OnUserEnterField(this));
            sendTemporaryStats();
            client.getPlayer().receivePartyMemberHP();
            for (final MaplePet pet : pets) {
                if (pet.getSummoned()) {
                    //       pet.setPos(getPosition());
                    client.getSession().write(CPet.showPet(this, pet, false, false));
                    client.getSession().write(CPet.loadPetPickupExceptionList(this, pet));
                }
            }
            if (dragon != null) {
                client.getSession().write(CField.spawnDragon(dragon));
            }
            if (android != null) {
                client.getSession().write(CField.spawnAndroid(this, android));
            }
            if (summons != null && summons.size() > 0) {
                summonsLock.readLock().lock();
                try {
                    for (final MapleSummon summon : summons) {
                        // 소환수
                        if (client != getClient() && !client.getPlayer().getSummonLists().contains(summon)) {
                            client.getPlayer().addSummonList(summon);
                            client.getSession().write(SummonPacket.spawnSummon(summon, true));
                        }
                        //client.getSession().write(SummonPacket.spawnSummon(summon, false));
                    }
                } finally {
                    summonsLock.readLock().unlock();
                }
            }
            if (followid > 0 && followon) {
                client.getSession().write(CField.followEffect(followinitiator ? followid : id, followinitiator ? id : followid, null));
            }
        }
    }

    public final void equipChanged() {
        if (map == null) {
            return;
        }
        map.broadcastMessage(this, CField.updateCharLook(this), false);
        stats.recalcLocalStats(this);
        if (getMessenger() != null) {
            World.Messenger.updateMessenger(getMessenger().getId(), getName(), client.getChannel());
        }
    }

    public MaplePet getPet(final int index) {
        byte count = 0;
        for (final MaplePet pet : pets) {
            if (pet.getSummoned()) {
                if (count == index) {
                    return pet;
                }
                count++;
            }
        }
        return null;
    }

    public void removePetCS(MaplePet pet) {
        pets.remove(pet);
    }

    public void addPet(final MaplePet pet) {
        if (pets.contains(pet)) {
            pets.remove(pet);
        }
        pets.add(pet);

        // So that the pet will be at the last
        // Pet index logic :(
    }

    public void addPetz(final MaplePet pet) {
        if (pets.contains(pet)) {
            pets.remove(pet);
        }
        pets.add(pet);
        for (int i = 0; i < 3; ++i) {
            if (petz[i] == null) {
                petz[i] = pet;
                return;
            }
        }

        // So that the pet will be at the last
        // Pet index logic :(
    }

    public void removePet(MaplePet pet, boolean shiftLeft) {
        pet.setSummoned(0);
        int slot = -1;
        for (int i = 0; i < 3; i++) {
            if (petz[i] != null) {
                if (petz[i].getUniqueId() == pet.getUniqueId()) {
                    petz[i] = null;
                    slot = i;
                    break;
                }
            }
        }
        if (shiftLeft) {
            if (slot > -1) {
                for (int i = slot; i < 3; i++) {
                    if (i != 2) {
                        petz[i] = petz[i + 1];
                    } else {
                        petz[i] = null;
                    }
                }
            }
        }
    }

    public final byte getPetIndex(final MaplePet petz) {
        for (byte i = 0; i < 3; i++) {
            if (this.petz[i] != null) {
                if (this.petz[i].getUniqueId() == petz.getUniqueId()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public final byte getPetIndex(final int petId) {
        for (byte i = 0; i < 3; i++) {
            if (this.petz[i] != null) {
                if (this.petz[i].getPetItemId() == petId) {
                    return i;
                }
            }
        }
        return -1;
    }

    public final List<MaplePet> getSummonedPets() {
        List<MaplePet> ret = new ArrayList<MaplePet>();
        for (final MaplePet pet : pets) {
            if (pet.getSummoned()) {
                ret.add(pet);
            }
        }
        return ret;
    }

    public final byte getPetById(final int petId) {
        byte count = 0;
        for (final MaplePet pet : pets) {
            if (pet.getSummoned()) {
                if (pet.getPetItemId() == petId) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    public final MaplePet[] getPetz() {
        return petz;
    }

    public final List<MaplePet> getPets() {
        return pets;
    }

    public final void unequipAllPets() {
        for (final MaplePet pet : pets) {
            if (pet != null) {
                unequipPet(pet, true, false);
            }
        }
    }

    public void unequipPet(MaplePet pet, boolean shiftLeft, boolean hunger) {
        if (pet.getSummoned()) {
            client.getSession().write(CPet.updatePet(pet, getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), false));
            if (map != null) {
                map.broadcastMessage(this, CPet.showPet(this, pet, true, hunger), true);
            }
            removePet(pet, shiftLeft);
            //client.getSession().write(PetPacket.petStatUpdate(this));
            client.getSession().write(CWvsContext.enableActions());
        }
    }

    public void shiftPetsRight() {
        if (petz[2] == null) {
            petz[2] = petz[1];
            petz[1] = petz[0];
            petz[0] = null;
        }
    }

    public final long getLastFameTime() {
        return lastfametime;
    }

    public final List<Integer> getFamedCharacters() {
        return lastmonthfameids;
    }

    public final List<Integer> getBattledCharacters() {
        return lastmonthbattleids;
    }

    public FameStatus canGiveFame(MapleCharacter from) {
        if (lastfametime >= System.currentTimeMillis() - 60 * 60 * 24 * 1000) {
            return FameStatus.NOT_TODAY;
        } else if (from == null || lastmonthfameids == null || lastmonthfameids.contains(Integer.valueOf(from.getId()))) {
            return FameStatus.NOT_THIS_MONTH;
        }
        return FameStatus.OK;
    }

    public void hasGivenFame(MapleCharacter to) {
        lastfametime = System.currentTimeMillis();
        lastmonthfameids.add(Integer.valueOf(to.getId()));
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)");
            ps.setInt(1, getId());
            ps.setInt(2, to.getId());
            ps.execute();
        } catch (SQLException e) {
            System.err.println("ERROR writing famelog for char " + getName() + " to " + to.getName() + e);
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
    }

    public boolean canBattle(MapleCharacter to) {
        if (to == null || lastmonthbattleids == null || lastmonthbattleids.contains(Integer.valueOf(to.getAccountID()))) {
            return false;
        }
        return true;
    }

    public void hasBattled(MapleCharacter to) {
        lastmonthbattleids.add(Integer.valueOf(to.getAccountID()));
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("INSERT INTO battlelog (accid, accid_to) VALUES (?, ?)");
            ps.setInt(1, getAccountID());
            ps.setInt(2, to.getAccountID());
            ps.execute();
        } catch (SQLException e) {
            System.err.println("ERROR writing battlelog for char " + getName() + " to " + to.getName() + e);
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
    }

    public final MapleKeyLayout getKeyLayout() {
        return this.keylayout;
    }

    public MapleParty getParty() {
        if (party == null) {
            return null;
        } else if (party.isDisbanded()) {
            party = null;
        }
        return party;
    }

    public List<MapleCharacter> getPartyMembers() {
        if (getParty() == null) {
            return null;
        }
        List<MapleCharacter> chars = new LinkedList<MapleCharacter>(); // creates an empty array full of shit..
        for (MaplePartyCharacter chr : getParty().getMembers()) {
            for (ChannelServer channel : ChannelServer.getAllInstances()) {
                MapleCharacter ch = channel.getPlayerStorage().getCharacterById(chr.getId());
                if (ch != null) { // double check <3
                    chars.add(ch);
                }
            }
        }
        return chars;
    }

    public long getRankingatt() {
        return rankatt;
    }

    public byte getWorld() {
        return world;
    }

    public void setWorld(byte world) {
        this.world = world;
    }

    public void setParty(MapleParty party) {
        this.party = party;
    }

    public MapleTrade getTrade() {
        return trade;
    }

    public void setTrade(MapleTrade trade) {
        this.trade = trade;
    }

    public EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public int getStarForce() {
        int starForce = 0;
        final Iterator<Item> itera = this.getInventory(MapleInventoryType.EQUIPPED).newList().iterator();
        while (itera.hasNext()) {
            final Equip equip = (Equip) itera.next();
            starForce += equip.getEnhance();
        }
        return starForce;
    }

    public void addDoor(MapleDoor door) {
        doors.add(door);
    }

    public void clearDoors() {
        doors.clear();
    }

    public List<MapleDoor> getDoors() {
        return new ArrayList<MapleDoor>(doors);
    }

    public void addMechDoor(MechDoor door) {
        mechDoors.add(door);
    }

    public void clearMechDoors() {
        mechDoors.clear();
    }

    public List<MechDoor> getMechDoors() {
        return new ArrayList<MechDoor>(mechDoors);
    }

    public void setSmega() {
        if (smega) {
            smega = false;
            dropMessage(6, "You will no longer be able to view Megaphones.");
        } else {
            smega = true;
            dropMessage(6, "You will now be able to view Megaphones.");
        }
    }

    public boolean getSmega() {
        return smega;
    }

    public List<MapleSummon> getSummonsReadLock() {
        summonsLock.readLock().lock();
        return summons;
    }

    public int getSummonsSize() {
        return summons.size();
    }

    public void unlockSummonsReadLock() {
        summonsLock.readLock().unlock();
    }

    public void addSummon(MapleSummon s) {
        summonsLock.writeLock().lock();
        try {
            summons.add(s);
        } finally {
            summonsLock.writeLock().unlock();
        }
    }

    public void removeSummon(MapleSummon s) {
        summonsLock.writeLock().lock();
        try {
            summons.remove(s);
        } finally {
            summonsLock.writeLock().unlock();
        }
    }

    public int getChair() {
        return chair;
    }

    public int getItemEffect() {
        return itemEffect;
    }

    public void setChair(int chair) {
        this.chair = chair;
        stats.relocHeal(this);
    }

    public void setItemEffect(int itemEffect) {
        this.itemEffect = itemEffect;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    public int getFamilyId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getFamilyId();
    }

    public int getSeniorId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getSeniorId();
    }

    public int getJunior1() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior1();
    }

    public int getJunior2() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior2();
    }

    public int getCurrentRep() {
        return currentrep;
    }

    public int getTotalRep() {
        return totalrep;
    }

    public void setCurrentRep(int _rank) {
        currentrep = _rank;
        if (mfc != null) {
            mfc.setCurrentRep(_rank);
        }
    }

    public void setTotalRep(int _rank) {
        totalrep = _rank;
        if (mfc != null) {
            mfc.setTotalRep(_rank);
        }
    }

    public int getTodayRep() {
        return totalrep;
    }

    public void setTodayRep(int _rank) {
        todayrep = _rank;
        if (mfc != null) {
            mfc.setTodayRep(_rank);
        }
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public void increaseTotalWins() {
        totalWins++;
    }

    public void increaseTotalLosses() {
        totalLosses++;
    }

    public int getGuildId() {
        return guildid;
    }

    public byte getGuildRank() {
        return guildrank;
    }

    public int getGuildContribution() {
        return guildContribution;
    }

    public void setGuildId(int _id) {
        guildid = _id;
        if (guildid > 0) {
            if (mgc == null) {
                mgc = new MapleGuildCharacter(this);
            } else {
                mgc.setGuildId(guildid);
            }
        } else {
            mgc = null;
            guildContribution = 0;
        }
    }

    public void setGuildRank(byte _rank) {
        guildrank = _rank;
        if (mgc != null) {
            mgc.setGuildRank(_rank);
        }
    }

    public void setGuildContribution(int _c) {
        this.guildContribution = _c;
        if (mgc != null) {
            mgc.setGuildContribution(_c);
        }
    }

    public MapleGuildCharacter getMGC() {
        return mgc;
    }

    public void setAllianceRank(byte rank) {
        allianceRank = rank;
        if (mgc != null) {
            mgc.setAllianceRank(rank);
        }
    }

    public byte getAllianceRank() {
        return allianceRank;
    }

    public MapleGuild getGuild() {
        if (getGuildId() <= 0) {
            return null;
        }
        return World.Guild.getGuild(getGuildId());
    }

    public void setJob(int j) {
        this.job = (short) j;
    }

    public void guildUpdate() {
        if (guildid <= 0) {
            return;
        }
        mgc.setLevel((short) level);
        mgc.setJobId(job);
        World.Guild.memberLevelJobUpdate(mgc);
    }

    public void saveGuildStatus() {
        MapleGuild.setOfflineGuildStatus(guildid, guildrank, guildContribution, allianceRank, id);
    }

    public void familyUpdate() {
        if (mfc == null) {
            return;
        }
        World.Family.memberFamilyUpdate(mfc, this);
    }

    public void saveFamilyStatus() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET familyid = ?, seniorid = ?, junior1 = ?, junior2 = ? WHERE id = ?");
            if (mfc == null) {
                ps.setInt(1, 0);
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                ps.setInt(4, 0);
            } else {
                ps.setInt(1, mfc.getFamilyId());
                ps.setInt(2, mfc.getSeniorId());
                ps.setInt(3, mfc.getJunior1());
                ps.setInt(4, mfc.getJunior2());
            }
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (SQLException se) {
            System.out.println("SQLException: " + se);
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
    }

    public void modifyCSPoints(int type, int quantity) {
        modifyCSPoints(type, quantity, false);
    }

    public void modifyCSPoints(int type, int quantity, boolean show) {

        switch (type) {
            case 1:
                if (nxcredit + quantity < 0) {
                    if (show) {
                        dropMessage(-1, " 캐쉬를 획득하였습니다.");
                    }
                    return;
                }
                nxcredit += quantity;
                break;
            case 4:
                if (acash + quantity < 0) {
                    if (show) {
                        dropMessage(-1, " 캐쉬를 획득하였습니다.");
                    }
                    return;
                }
                acash += quantity;
                break;
            case 2:
                if (maplepoints + quantity < 0) {
                    if (show) {
                        dropMessage(-1, " 캐쉬를 획득하였습니다.");
                    }
                    return;
                }
                maplepoints += quantity;
                break;
            default:
                break;
        }
        if (show && quantity != 0) {
            //    dropMessage(3994595, " "+ quantity + (type == 1 ? " 캐시" : type == 4 ? " 캐시" : " 메이플포인트") + "를 " + (quantity > 0 ? "얻었습니다.  " : "잃었습니다. "));
            //    client.getSession().write(CWvsContext.getTopMsg("캐시를 " + (quantity > 0 ? "얻었습니다.  " : "잃었습니다. ") + quantity + (type == 1 ? " 캐시." : type == 4 ? " 캐시" : " 메이플포인트")));
            client.getSession().write(EffectPacket.showForeignEffect(20)); // 이건가?? 주석쳐봣엇는데아니엿음
            //  client.getSession().write(MTSCSPacket.showMapleTokens(this)); // 인벤토리 캐시 ㄹㄹ 
        }
    }

    public int getCSPoints(int type) {
        switch (type) {
            case 1:
                return nxcredit;
            case 4:
                return acash;
            case 2:
                return maplepoints;

            default:
                return 0;
        }
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(final int recommend) {
        this.recommend = recommend;
    }

    public int getFanclub() {
        return Fanclub;
    }

    public void setFanclub(final int recommend) {
        this.Fanclub = recommend;
    }

    public final boolean hasEquipped(int itemid) {
        return inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid) >= 1;
    }

    private void sendTemporaryStats() {
        MapleStatEffect eff;
        Integer val = getBuffedValue(CharacterTemporaryStat.EnergyCharge);
        if (val != null) {
            map.broadcastMessage(this, CWvsContext.BuffPacket.giveEnergyChargeTest(id, 10000, 50), false);
        }
        eff = getStatForBuff(CharacterTemporaryStat.DashSpeed);
        if (eff != null) {
            map.broadcastMessage(this, BuffPacket.giveForeignPirate((Map<CharacterTemporaryStat, Integer>) Collections.singletonMap(CharacterTemporaryStat.DashSpeed, getBuffedValue(CharacterTemporaryStat.DashSpeed)), eff.getDuration() / 1000, id, eff.getSourceId()), false);
        }
        eff = getStatForBuff(CharacterTemporaryStat.Aura);

        if (eff != null) {
            EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class
            );
            stat.put(CharacterTemporaryStat.Aura, 1);
            stat.put(CharacterTemporaryStat.DarkAura, 1);
            stat.put(CharacterTemporaryStat.BlueAura, 1);
            stat.put(CharacterTemporaryStat.YellowAura, 1);
            map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, eff), false);
        }
        eff = getStatForBuff(CharacterTemporaryStat.Larkness);

        if (eff != null) {
            EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class
            );
            stat.put(CharacterTemporaryStat.Larkness, 1);
            eff.setSourceId(this.getBuffedValue(CharacterTemporaryStat.Larkness));
            map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, eff), false);
        }
        eff = getStatForBuff(CharacterTemporaryStat.StackBuff);

        if (eff != null) {
            EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class
            );
            stat.put(CharacterTemporaryStat.StackBuff, 1);
            map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, eff), false);
        }
        eff = getStatForBuff(CharacterTemporaryStat.BlessOfDarkness);

        if (eff != null) {
            EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class
            );
            stat.put(CharacterTemporaryStat.BlessOfDarkness, (int) currentFTC());
            map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, eff), false);
        }
    }

    public final boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        int possesed = inventory[type.ordinal()].countById(itemid);
        if (checkEquipped && type == MapleInventoryType.EQUIP) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        } else {
            return possesed == quantity;
        }
    }

    public final boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, true, true);
    }

    public final boolean haveItem(int itemid) {
        return haveItem(itemid, 1, true, true);
    }

    public static boolean tempban(String reason, Calendar duration, int greason, int accountid) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?");
            Timestamp TS = new Timestamp(duration.getTimeInMillis());
            ps.setTimestamp(1, TS);
            ps.setString(2, reason);
            ps.setInt(3, greason);
            ps.setInt(4, accountid);
            ps.executeUpdate();
            ps.close();
            con.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
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
        return false;
    }

    public void setGM(byte level) {
        this.gmLevel = level;
    }

    public void changeSkillLevel(Skill skill, int maxLevel, byte b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addGuildMember(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    public static enum FameStatus {

        OK, NOT_TODAY, NOT_THIS_MONTH
    }

    public byte getBuddyCapacity() {
        return buddylist.getCapacity();
    }

    public void setBuddyCapacity(byte capacity) {
        buddylist.setCapacity(capacity);
        client.sendPacket(BuddylistPacket.updateBuddyCapacity(capacity));
    }

    public MapleMessenger getMessenger() {
        return messenger;
    }

    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    public void addCooldown(int skillId, long startTime, long length) {
        coolDowns.put(Integer.valueOf(skillId), new MapleCoolDownValueHolder(skillId, startTime, length));
    }

    public void removeCooldown(int skillId) {
        if (coolDowns.containsKey(Integer.valueOf(skillId))) {
            coolDowns.remove(Integer.valueOf(skillId));
        }
    }

    public boolean skillisCooling(int skillId) {
        return coolDowns.containsKey(Integer.valueOf(skillId));
    }

    public void giveCoolDowns(final int skillid, long starttime, long length) {
        addCooldown(skillid, starttime, length);
    }

    public void setMulungTime(long clearTime) {
        this.MulungTime = clearTime;
    }

    public long getMulungTime() {
        return MulungTime;
    }

    public void updateMulungRanks(int clearTime) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            deleteWhereCharacterName(con, "DELETE FROM dojo_ranks WHERE name = ?");
            ps = con.prepareStatement("INSERT INTO dojo_ranks (name, time) VALUES (?, ?)");
            ps.setString(1, this.getName());
            ps.setInt(2, clearTime);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println(System.err);
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
    }

    public void giveCoolDowns(final List<MapleCoolDownValueHolder> cooldowns) {
        int time;
        if (cooldowns != null) {
            for (MapleCoolDownValueHolder cooldown : cooldowns) {
                coolDowns.put(cooldown.skillId, cooldown);
            }
        } else {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("SELECT SkillID,StartTime,length FROM skills_cooldowns WHERE charid = ?");
                ps.setInt(1, getId());
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getLong("length") + rs.getLong("StartTime") - System.currentTimeMillis() <= 0) {
                        continue;
                    }
                    giveCoolDowns(rs.getInt("SkillID"), rs.getLong("StartTime"), rs.getLong("length"));
                }
                rs.close();
                ps.close();
                deleteWhereCharacterId(con, "DELETE FROM skills_cooldowns WHERE charid = ?");
                con.close();
            } catch (SQLException e) {
                System.err.println("Error while retriving cooldown from SQL storage");
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
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
        }
    }

    public int getCooldownSize() {
        return coolDowns.size();
    }

    public int getDiseaseSize() {
        return diseases.size();
    }

    public List<MapleCoolDownValueHolder> getCooldowns() {
        List<MapleCoolDownValueHolder> ret = new ArrayList<MapleCoolDownValueHolder>();
        for (MapleCoolDownValueHolder mc : coolDowns.values()) {
            if (mc != null) {
                ret.add(mc);
            }
        }
        return ret;
    }

    public final List<MapleDiseaseValueHolder> getAllDiseases() {
        return new ArrayList<MapleDiseaseValueHolder>(diseases.values());
    }

    public final boolean hasDisease(final MonsterSkill dis) {
        return diseases.containsKey(dis);
    }

    public void giveDebuff(MonsterSkill disease, MobSkill skill, int option) {
        giveDebuff(disease, skill.getX(), skill.getDuration(), skill.getSkillId(), skill.getSkillLevel(), option);
    }

    public void giveDebuff(MonsterSkill disease, int x, long duration, int skillid, int level, int option) {
        if (this.isGM() == true) {
            //this.dropMessage(6, "- giveDebuff | mobSkillID : " + disease + " | skillID : " + skillid + " | skillLevel : " + level);
        }
        if ((this.map != null) && (!hasDisease(disease))) {
            int mC = getBuffSource(CharacterTemporaryStat.Summon);
            if (mC == 35121003) {
                return;
            }
            if ((this.stats.ASR > 0) && (Randomizer.nextInt(100) < this.stats.ASR)) {
                //return;
            }
            if (this.getBuffedValue(CharacterTemporaryStat.AntiMagicShell) != null) {
                final CharacterTemporaryStat cts = CharacterTemporaryStat.AntiMagicShell;
                int ctv = (this.getBuffedValue(cts) - 1);
                int skillID = this.getBuffSource(cts);
                int skillLv = 1;
                final MapleStatEffect mse = SkillFactory.getSkill(skillID).getEffect(skillLv);
                int pDuration = Integer.MAX_VALUE;
                if (ctv < 1) {
                    this.cancelBuffStats(false, cts);
                    if (skillID == 2311012) {
                        this.cancelBuffStats(false, CharacterTemporaryStat.SaintSaver);
                    }
                } else {
                    this.setTemporaryStat(cts, ctv, mse, skillID, pDuration);
                }
                if (skillID != 2311012) {
                    this.getClient().sendPacket(EffectPacket.showOwnBuffEffect(skillID, 7, 1, 1));
                    map.broadcastMessage(this, EffectPacket.showBuffeffect(this.getId(), skillID, 7, 1, 1), this.getTruePosition());
                }
                return;
            }
            if (this.getBuffedValue(CharacterTemporaryStat.SaintSaver) != null) {
                if (this.getBuffSource(CharacterTemporaryStat.SaintSaver) == 2211012) {
                    return;
                }
            }

            //if (this.getBuffedValue(CharacterTemporaryStat.Unk_0x2_4) != null) {
            //this.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "[용사의 의지] 스킬로부터 [상태 이상]을 보호하였습니다."));
            //return;
            //}
            if (disease == MonsterSkill.UserBomb) {
                MS_UserBomb.put(MonsterSkill.UserBomb, new Pair<>(Integer.valueOf(x), Integer.valueOf((int) level)));
            }
            if (disease == MonsterSkill.DeathMark) {
                if (MS_Mark.isEmpty()) {
                    MS_Mark.put(MonsterSkill.DeathMark, new Pair<>(Integer.valueOf(0), Integer.valueOf((int) duration)));
                }
                if (MS_Mark.get(MonsterSkill.DeathMark).left == 2) {
                    MS_Mark.put(MonsterSkill.DeathMark, new Pair<>(Integer.valueOf(3), Integer.valueOf((int) duration)));
                }
                if (MS_Mark.get(MonsterSkill.DeathMark).left == 1) {
                    MS_Mark.put(MonsterSkill.DeathMark, new Pair<>(Integer.valueOf(2), Integer.valueOf((int) duration)));
                }
                if (MS_Mark.get(MonsterSkill.DeathMark).left == 0) {
                    MS_Mark.put(MonsterSkill.DeathMark, new Pair<>(Integer.valueOf(1), Integer.valueOf((int) duration)));
                }
                duration = 9999;
                x = MS_Mark.get(MonsterSkill.DeathMark).left;
            }
            if (disease == MonsterSkill.PainMark) {
                duration = 9999;
                x = 1;
                MS_Mark.put(MonsterSkill.PainMark, new Pair<>(1, level));
                if (this.getMapId() == 450013910) {
                    this.getClient().sendPacket(CField.floatNotice("신에 가까운 자의 저주가 발현된다. 창조와 파괴, 둘 중 하나의 저주가 깃든다.", 5120203, true));
                }
            }

            if (disease == MonsterSkill.VampDeath) {
                final MapleMonster v1 = MapleLifeFactory.getMonster(8870201);
                this.getMap().spawnMonsterOnGroundBelow(v1, this.getTruePosition());
                x = (int) duration;
                final MonsterSkill morph = MonsterSkill.VampDeathSummon;
                this.diseases.put(morph, new MapleDiseaseValueHolder(morph, System.currentTimeMillis(), duration - this.stats.decreaseDebuff));
                this.client.getSession().write(CWvsContext.BuffPacket.giveDebuff(morph, x, 183, 1, (int) duration, option));
                this.map.broadcastMessage(this, CWvsContext.BuffPacket.giveForeignDebuff(this.id, morph, 183, 1, x), false);
            }

            this.diseases.put(disease, new MapleDiseaseValueHolder(disease, System.currentTimeMillis(), duration - this.stats.decreaseDebuff));
            this.client.getSession().write(CWvsContext.BuffPacket.giveDebuff(disease, x, skillid, level, (int) duration, option));
            this.map.broadcastMessage(this, CWvsContext.BuffPacket.giveForeignDebuff(this.id, disease, skillid, level, x), false);

            if (disease == MonsterSkill.VenomSnake) {
                final MonsterSkill morph = MonsterSkill.UserMorph;
                this.diseases.put(morph, new MapleDiseaseValueHolder(morph, System.currentTimeMillis(), duration - this.stats.decreaseDebuff));
                this.client.getSession().write(CWvsContext.BuffPacket.giveDebuff(morph, 130, 172, 2, (int) duration, option));
                this.map.broadcastMessage(this, CWvsContext.BuffPacket.giveForeignDebuff(this.id, morph, 172, 2, 130), false);
            }

            if (disease == MonsterSkill.Magnet) {
                final MonsterSkill morph = MonsterSkill.MagnetArea;
                this.diseases.put(morph, new MapleDiseaseValueHolder(morph, System.currentTimeMillis(), duration - this.stats.decreaseDebuff));
                this.client.getSession().write(CWvsContext.BuffPacket.giveDebuff(morph, 1, 182, 1, (int) duration, option));
                this.map.broadcastMessage(this, CWvsContext.BuffPacket.giveForeignDebuff(this.id, morph, 182, 1, 1), false);
            }

            if ((x > 0) && (disease == MonsterSkill.Poison)) {
                MS_Poison.put(MonsterSkill.Poison, new Pair<>(x, Integer.valueOf((int) duration)));
            }
        }
    }

    public Map<MonsterSkill, MapleDiseaseValueHolder> getDiseases() {
        return this.diseases;
    }

    public Map<MonsterSkill, Pair<Integer, Integer>> getMark() {
        return this.MS_Mark;
    }

    public Map<MonsterSkill, Pair<Integer, Integer>> getPoison() {
        return this.MS_Poison;
    }

    public Map<MonsterSkill, Pair<Integer, Integer>> getUserBomb() {
        return this.MS_UserBomb;
    }

    public final void giveSilentDebuff(final List<MapleDiseaseValueHolder> ld) {
        if (ld != null) {
            for (final MapleDiseaseValueHolder disease : ld) {
                diseases.put(disease.disease, disease);
            }
        }
    }

    public void dispelDebuff(MonsterSkill debuff) {
        if (debuff == MonsterSkill.UserBomb) {
            final MonsterSkill morph = MonsterSkill.Explosion;
            int sLevel = this.getUserBomb().get(MonsterSkill.UserBomb).right;
            int x = this.getUserBomb().get(MonsterSkill.UserBomb).left;
            this.diseases.put(morph, new MapleDiseaseValueHolder(morph, System.currentTimeMillis(), 1000));
            this.client.getSession().write(CWvsContext.BuffPacket.giveDebuff(morph, x, 171, sLevel, (int) 1000, 0));
            this.map.broadcastMessage(this, CWvsContext.BuffPacket.giveForeignDebuff(this.id, morph, 171, sLevel, x), false);
        }
        if (debuff == MonsterSkill.VampDeath) {
            final MapleMonster m = this.getMap().getMonsterById(8870201);
            if (m != null) {
                this.getMap().killMonster(m);
            }
        }
        if (hasDisease(debuff)) {
            client.getSession().write(BuffPacket.cancelDebuff(debuff));
            map.broadcastMessage(this, BuffPacket.cancelForeignDebuff(id, debuff), false);
            diseases.remove(debuff);
        }
    }

    public void dispelDebuffs() {
        if (!MS_Mark.isEmpty()) {
            MS_Mark.put(MonsterSkill.DeathMark, new Pair<>(Integer.valueOf(0), Integer.valueOf((int) -1)));
        }
        if (!MS_Poison.isEmpty()) {
            MS_Poison.put(MonsterSkill.Poison, new Pair<>(Integer.valueOf(0), Integer.valueOf((int) -1)));
        }
        List<MonsterSkill> diseasess = new ArrayList<MonsterSkill>(diseases.keySet());
        for (MonsterSkill d : diseasess) {
            dispelDebuff(d);
        }
    }

    public void cancelAllDebuffs() {
        diseases.clear();
    }

    public void setLevel(final short level) {
        this.level = level;
    }

    public void sendNote(String to, String msg) {
        sendNote(to, msg, 0);
    }

    public void sendNote(String to, String msg, int fame) {
        MapleCharacterUtil.sendNote(to, getName(), msg, fame);
    }

    public void showNote() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, getName());
            rs = ps.executeQuery();
            rs.last();
            int count = rs.getRow();
            rs.first();
            client.getSession().write(CCashShop.showNotes(rs, count));
        } catch (SQLException e) {
            System.err.println("Unable to show note" + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
    }

    public void deleteNote(int id, int fame) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT gift FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("gift") == fame && fame > 0) { //not exploited! hurray
                    addFame(fame);
                    updateSingleStat(MapleStat.Fame, getFame());
                    client.getSession().write(InfoPacket.getShowFameGain(fame));
                }
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            System.err.println("Unable to delete note" + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
    }

    public int getMulungEnergy() {
        return mulung_energy;
    }

    public void mulung_EnergyModify(boolean inc) {
        if (inc) {
            if (mulung_energy + 100 > 10000) {
                mulung_energy = 10000;
            } else {
                mulung_energy += 100;
            }
        } else {
            mulung_energy = 0;
        }
        client.getSession().write(CWvsContext.MulungEnergy(mulung_energy));
    }

    public void writeMulungEnergy() {
        client.getSession().write(CWvsContext.MulungEnergy(mulung_energy));
    }

    public void writeEnergy(String type, String inc) {
        client.getSession().write(CWvsContext.sendPyramidEnergy(type, inc));
    }

    public void writeStatus(String type, String inc) {
        client.getSession().write(CWvsContext.sendGhostStatus(type, inc));
    }

    public void writePoint(String type, String inc) {
        client.getSession().write(CWvsContext.sendGhostPoint(type, inc));
    }

    public final short getCombo() {
        return combo;
    }

    public void setCombo(final short combo) {
        this.combo = combo;
    }

    public final long getLastCombo() {
        return lastCombo;
    }

    public void setLastCombo(final long combo) {
        this.lastCombo = combo;
    }

    public final long getKeyDownSkill_Time() {
        return keydown_skill;
    }

    public void setKeyDownSkill_Time(final long keydown_skill) {
        this.keydown_skill = keydown_skill;
    }

    public void checkBerserk() { //berserk is special in that it doesn't use worldtimer :)
        if (job != 132 || lastBerserkTime < 0 || lastBerserkTime + 10000 > System.currentTimeMillis()) {
            return;
        }
        final Skill BerserkX = SkillFactory.getSkill(1320006);
        final int skilllevel = getTotalSkillLevel(BerserkX);
        if (skilllevel >= 1 && map != null) {
            lastBerserkTime = System.currentTimeMillis();
            final MapleStatEffect ampStat = BerserkX.getEffect(skilllevel);
            stats.Berserk = stats.getHp() * 100 / stats.getCurrentMaxHp() >= ampStat.getX();
            client.getSession().write(EffectPacket.showOwnBuffEffect(1320006, 1, getLevel(), skilllevel, (byte) (stats.Berserk ? 1 : 0)));
            map.broadcastMessage(this, EffectPacket.showBuffeffect(getId(), 1320006, 1, getLevel(), skilllevel, (byte) (stats.Berserk ? 1 : 0)), false);
        } else {
            lastBerserkTime = -1; // somebody thre? O_O
        }
    }

    public void setChalkboard(String text) {
        this.chalktext = text;
        if (map != null) {
            map.broadcastMessage(CCashShop.useChalkboard(getId(), text));
        }
    }

    public String getChalkboard() {
        return chalktext;
    }

    public MapleMount getMount() {
        return mount;
    }

    public int[] getWishlist() {
        return wishlist;
    }

    public void clearWishlist() {
        for (int i = 0; i < 10; i++) {
            wishlist[i] = 0;
        }
        changed_wishlist = true;
    }

    public int getWishlistSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (wishlist[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public void setWishlist(int[] wl) {
        this.wishlist = wl;
        changed_wishlist = true;
    }

    public int[] getRocks() {
        return rocks;
    }

    public int getRockSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (rocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRocks(int map) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == map) {
                rocks[i] = 999999999;
                changed_trocklocations = true;
                break;
            }
        }
    }

    public void addRockMap() {
        if (getRockSize() >= 10) {
            return;
        }
        rocks[getRockSize()] = getMapId();
        changed_trocklocations = true;
    }

    public boolean isRockMap(int id) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getRegRocks() {
        return regrocks;
    }

    public int getRegRockSize() {
        int ret = 0;
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRegRocks(int map) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == map) {
                regrocks[i] = 999999999;
                changed_regrocklocations = true;
                break;
            }
        }
    }

    public void addRegRockMap() {
        if (getRegRockSize() >= 5) {
            return;
        }
        regrocks[getRegRockSize()] = getMapId();
        changed_regrocklocations = true;
    }

    public boolean isRegRockMap(int id) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getHyperRocks() {
        return hyperrocks;
    }

    public int getHyperRockSize() {
        int ret = 0;
        for (int i = 0; i < 13; i++) {
            if (hyperrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromHyperRocks(int map) {
        for (int i = 0; i < 13; i++) {
            if (hyperrocks[i] == map) {
                hyperrocks[i] = 999999999;
                changed_hyperrocklocations = true;
                break;
            }
        }
    }

    public void addHyperRockMap() {
        if (getRegRockSize() >= 13) {
            return;
        }
        hyperrocks[getHyperRockSize()] = getMapId();
        changed_hyperrocklocations = true;

    }

    public boolean isHyperRockMap(int id) {
        for (int i = 0; i < 13; i++) {
            if (hyperrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public List<LifeMovementFragment> getLastRes() {
        return lastres;
    }

    public void setLastRes(List<LifeMovementFragment> lastres) {
        this.lastres = lastres;
    }

    public void customMessage(int type, String message) {
        switch (type) {
            /*Global Dark Pink*/
            //case -10:
            //World.Broadcast.broadcastMessage(CField.getGameMessage(message, false));
            //break;
            /*Mid Teal*/
            case -7:
                client.getPlayer().getMap().broadcastMessage(CWvsContext.staticScreenMessage(message, false));
                break;
            /*WhiteBG*/
            case -6:
                client.getPlayer().getMap().broadcastMessage(CField.getGameMessage(message, true));
                break;
            /*DarkPink*/
            case -5:
                client.getPlayer().getMap().broadcastMessage(CField.getGameMessage(message, false));
                break;
            /*Chat hide*/
            case -4:
                client.getPlayer().getMap().broadcastMessage(CField.getChatText(getId(), message, false, 1));
                break;
            /*Char Talk*/
            case -3:
                client.getPlayer().getMap().broadcastMessage(CField.getChatText(getId(), message, false, 0));
                break;
            /*Top Yellow*/
            case -1:
                client.getPlayer().getMap().broadcastMessage(CWvsContext.getTopMsg(message));
                break;
            /*[Notice]*/
            case 0:
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(type, message));
                break;
            /*Popup Box*/
            case 1:
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(type, message));
                break;
            /*Light Blue*/
            case 2:
                client.getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(type, message));
                break;
            /*Top Notice*/
            case 4:
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(type, message));
                break;
            /*Light Pink*/
            case 5:
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(type, message));
                break;
            /*[System]*/
            case 6:
                World.Broadcast.broadcastMessage(CWvsContext.serverNotice(type, message));
                break;
        }
    }

    public void dropMessage(int type, String message) {
        /* -9 Clears Mid Teal Messages
         * -8 - Mid Teal Perm. Message
         * -7 - Mid Teal fadeaway message
         * -6 - White BG
         * -5 - Dark Pink
         * -4 - Character talk w/ message hidden in chat.
         * -3 - Character talk.
         * -2 - Shop Chat?
         * -1 - Top Yellow Message
         * 0 - [Notice]
         * 1 - Blue popup
         * 2 - Light blue (Seperated with : )
         * 3 - Crashes
         * 4 - Top Server Notice
         * 5 - Light Pink
         * 6 - [System]
         * 7 - Crashes
         */

        if (type == -1) {
            client.getSession().write(CWvsContext.getTopMsg(message));
        } else if (type == -2) {
            client.getSession().write(CPlayerShop.shopChat(message, 0)); //0 or what
        } else if (type == -3) {
            client.getSession().write(CField.getChatText(getId(), message, isSuperGM(), 0)); //1 = hide
        } else if (type == -4) {
            client.getSession().write(CField.getChatText(getId(), message, isSuperGM(), 1)); //1 = hide
        } else if (type == -5) {
            client.getSession().write(CField.getGameMessage(message, false)); //pink
        } else if (type == -6) {
            client.getSession().write(CField.getGameMessage(message, true)); //white bg
        } else if (type == -7) {
            client.getSession().write(CWvsContext.staticScreenMessage(message, false));
        } else if (type == -8) {
            client.getSession().write(CWvsContext.staticScreenMessage(message, true));
        } else if (type == -9) {
            client.getSession().write(CWvsContext.offStaticScreenMessage());
        } else if (type >= 1000000) { // 아이템 메시지
            client.getSession().write(CWvsContext.staticScreenMessage(message, false));
        } else {
            client.getSession().write(CWvsContext.serverNotice(type, message));
        }
    }

    public final void showInstruction(final String msg, final int width, final int height) {
        client.sendPacket(CUserLocal.balloonMsg(msg, width, height));
    }

    public IMaplePlayerShop getPlayerShop() {
        return playerShop;
    }

    public void setPlayerShop(IMaplePlayerShop playerShop) {
        this.playerShop = playerShop;
    }

    public int getConversation() {
        return inst.get();
    }

    public void setConversation(int inst) {
        this.inst.set(inst);
    }

    public int getDirection() {
        return insd.get();
    }

    public void setDirection(int inst) {
        this.insd.set(inst);
    }

    public MapleCarnivalParty getCarnivalParty() {
        return carnivalParty;
    }

    public void setCarnivalParty(MapleCarnivalParty party) {
        carnivalParty = party;
    }

    public void addCP(int ammount) {
        totalCP += ammount;
        availableCP += ammount;
    }

    public void useCP(int ammount) {
        availableCP -= ammount;
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public int getTotalCP() {
        return totalCP;
    }

    public void resetCP() {
        totalCP = 0;
        availableCP = 0;
    }

    public void addCarnivalRequest(MapleCarnivalChallenge request) {
        pendingCarnivalRequests.add(request);
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return pendingCarnivalRequests.pollLast();
    }

    public void clearCarnivalRequests() {
        pendingCarnivalRequests = new LinkedList<MapleCarnivalChallenge>();
    }

    public void startMonsterCarnival(final int enemyavailable, final int enemytotal) {
        //client.getSession().write(MonsterCarnivalPacket.startMonsterCarnival(this, enemyavailable, enemytotal));
    }

    public void CPUpdate(final boolean party, final int available, final int total, final int team) {
        //client.getSession().write(MonsterCarnivalPacket.CPUpdate(party, available, total, team));
    }

    public void playerDiedCPQ(final String name, final int lostCP, final int team) {
        //client.getSession().write(MonsterCarnivalPacket.playerDiedMessage(name, lostCP, team));
    }

    public java.util.Timer getHealTimer() {
        return healTimer;
    }

    public void setHealTimer(java.util.Timer timer) {
        healTimer = timer;
    }

    public java.util.Timer getDFRecoveryTimer() {
        return DFRecoveryTimer;
    }

    public void setDFRecoveryTimer(java.util.Timer timer) {
        DFRecoveryTimer = timer;
    }

    public void setAchievementFinished(int id) {
        if (!finishedAchievements.contains(id)) {
            finishedAchievements.add(id);
            changed_achievements = true;
        }
    }

    public boolean achievementFinished(int achievementid) {
        return finishedAchievements.contains(achievementid);
    }

    public void finishAchievement(int id) {
        if (!achievementFinished(id)) {
            if (isAlive() && !isClone()) {
                MapleAchievements.getInstance().getById(id).finishAchievement(this);
            }
        }
    }

    public List<Integer> getFinishedAchievements() {
        return finishedAchievements;
    }

    public boolean getCanTalk() {
        return this.canTalk;
    }

    public void canTalk(boolean talk) {
        this.canTalk = talk;
    }

    public double getEXPMod() {
        return stats.expMod;
    }

    public int getDropMod() {
        return stats.dropMod;
    }

    public int getCashMod() {
        return stats.cashMod;
    }

    public int getNX() {
        return nxcredit;
    }

    public int getGMlevel() {
        return gmLevel;
    }

    public CashShop getCashInventory() {
        return cs;
    }

    public void removeItem(int id, int quantity) {
        MapleInventoryManipulator.removeById(client, GameConstants.getInventoryType(id), id, -quantity, true, false);
        client.getSession().write(InfoPacket.getShowItemGain(id, (short) quantity, true));
    }

    public void removeAll(int id) {
        removeAll(id, true);
    }

    public void removeAll(int id, boolean show) {
        MapleInventoryType type = GameConstants.getInventoryType(id);
        int possessed = getInventory(type).countById(id);
        if (possessed > 0) {
            MapleInventoryManipulator.removeById(getClient(), type, id, possessed, true, false);
            if (show) {
                getClient().getSession().write(InfoPacket.getShowItemGain(id, (short) -possessed, true));
            }
        }
        /*
        if (type == MapleInventoryType.EQUIP) {
            possessed = this.getInventory(MapleInventoryType.EQUIPPED).countById(id);
            Equip equip = (Equip) this.getInventory(MapleInventoryType.EQUIPPED).findById(id);
            if (equip != null) {
                if (possessed > 0) {
                    MapleInventoryManipulator.removeById(getClient(), type, id, possessed, true, false);
                    this.equipChanged();
                    this.getClient().sendPacket(CWvsContext.InventoryPacket.updateSpecialItemUse_(equip, (byte) 1, this));
                }
            }
        }
         */
    }

    public Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> getRings(boolean equip) {
        MapleInventory iv = getInventory(MapleInventoryType.EQUIPPED);
        List<Item> equipped = iv.newList();
        Collections.sort(equipped);
        List<MapleRing> crings = new ArrayList<MapleRing>(), frings = new ArrayList<MapleRing>(), mrings = new ArrayList<MapleRing>();
        MapleRing ring;
        for (Item ite : equipped) {
            Equip item = (Equip) ite;
            if (item.getRing() != null) {
                ring = item.getRing();
                ring.setEquipped(true);
                if (GameConstants.isEffectRing(item.getItemId())) {
                    if (equip) {
                        if (GameConstants.isCrushRing(item.getItemId())) {
                            crings.add(ring);
                        } else if (GameConstants.isFriendshipRing(item.getItemId())) {
                            frings.add(ring);
                        } else if (GameConstants.isMarriageRing(item.getItemId())) {
                            mrings.add(ring);
                        }
                    } else {
                        if (crings.size() == 0 && GameConstants.isCrushRing(item.getItemId())) {
                            crings.add(ring);
                        } else if (frings.size() == 0 && GameConstants.isFriendshipRing(item.getItemId())) {
                            frings.add(ring);
                        } else if (mrings.size() == 0 && GameConstants.isMarriageRing(item.getItemId())) {
                            mrings.add(ring);
                        } //for 3rd person the actual slot doesnt matter, so we'll use this to have both shirt/ring same?
                        //however there seems to be something else behind this, will have to sniff someone with shirt and ring, or more conveniently 3-4 of those
                    }
                }
            }
        }
        if (equip) {
            iv = getInventory(MapleInventoryType.EQUIP);
            for (Item ite : iv.list()) {
                Equip item = (Equip) ite;
                if (item.getRing() != null && GameConstants.isCrushRing(item.getItemId())) {
                    ring = item.getRing();
                    ring.setEquipped(false);
                    if (GameConstants.isFriendshipRing(item.getItemId())) {
                        frings.add(ring);
                    } else if (GameConstants.isCrushRing(item.getItemId())) {
                        crings.add(ring);
                    } else if (GameConstants.isMarriageRing(item.getItemId())) {
                        mrings.add(ring);
                    }
                }
            }
        }
        Collections.sort(frings, new MapleRing.RingComparator());
        Collections.sort(crings, new MapleRing.RingComparator());
        Collections.sort(mrings, new MapleRing.RingComparator());
        return new Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>>(crings, frings, mrings);
    }

    public int getFH() {
        MapleFoothold fh = getMap().getFootholds().findBelow(getTruePosition());
        if (fh != null) {
            return fh.getId();
        }
        return 0;
    }

    public void startFairySchedule(boolean exp) {
        startFairySchedule(exp, false);
    }

    public void startFairySchedule(boolean exp, boolean equipped) {
        cancelFairySchedule(exp || stats.equippedFairy == 0);
        if (fairyExp <= 0) {
            fairyExp = (byte) stats.equippedFairy;
        }
        if (equipped && fairyExp < stats.equippedFairy * 3 && stats.equippedFairy > 0) {
            dropMessage(5, "The Fairy Pendant's experience points will increase to " + (fairyExp + stats.equippedFairy) + "% after one hour.");
        }
        lastFairyTime = System.currentTimeMillis();
    }

    public final boolean canFairy(long now) {
        return lastFairyTime > 0 && lastFairyTime + (60 * 60 * 1000) < now;
    }

    public final boolean canHP(long now) {
        if (lastHPTime + 5000 < now) {
            lastHPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canMP(long now) {
        if (lastMPTime + 5000 < now) {
            lastMPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canHPRecover(long now) {
        if (stats.hpRecoverTime > 0 && lastHPTime + stats.hpRecoverTime < now) {
            lastHPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canMPRecover(long now) {
        if (stats.mpRecoverTime > 0 && lastMPTime + stats.mpRecoverTime < now) {
            lastMPTime = now;
            return true;
        }
        return false;
    }

    public void cancelFairySchedule(boolean exp) {
        lastFairyTime = 0;
        if (exp) {
            this.fairyExp = 0;
        }
    }

    public void doFairy() {
        if (fairyExp < stats.equippedFairy * 3 && stats.equippedFairy > 0) {
            fairyExp += stats.equippedFairy;
            dropMessage(5, "The Fairy Pendant's EXP was boosted to " + fairyExp + "%.");
        }
        if (getGuildId() > 0) {
            World.Guild.gainGP(getGuildId(), 20, id);
            client.getSession().write(InfoPacket.getGPContribution(20));
        }
        traits.get(MapleTraitType.will).addExp(5, this); //willpower every hour
        startFairySchedule(false, true);
    }

    public byte getFairyExp() {
        return fairyExp;
    }

    public int getTeam() {
        return coconutteam;
    }

    public void setTeam(int v) {
        this.coconutteam = v;
    }

    public void spawnPet(byte slot) {
        spawnPet(slot, false, true);
    }

    public void spawnPet(byte slot, boolean lead) {
        spawnPet(slot, lead, true);
    }

    public void spawnPet(byte slot, boolean lead, boolean broadcast) {
        final Item item = getInventory(MapleInventoryType.CASH).getItem(slot);
        if (item == null || item.getItemId() >= 5010000 || item.getItemId() < 5000000) {
            return;
        }
        Map<String, Integer> multipet = MapleItemInformationProvider.getInstance().getEquipStats(item.getItemId());
        int petBuffindex = 0;
        final MaplePet pet = item.getPet();
        if (getPet(0) != null && getPet(1) != null && getPet(2) != null && getPetIndex(pet) == -1) {
            client.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (pet != null) {
            int petbuffid;
            List<Integer> petbufflist = new ArrayList<>();
            List<Integer> petbufflists = new ArrayList<>();
            if (getPetIndex(pet) != -1) {
                for (MaplePet pet_ : getPetz()) {
                    if (pet_ != null) {
                        petbuffid = GameConstants.getPetBuff(pet_.getPetItemId());
                        if (petbuffid == 0) {
                            continue;
                        }
                        petbufflists.add(petbuffid);
                    }
                }
                petbuffid = GameConstants.getPetBuff(pet.getPetItemId());
                if (petbufflists.size() == 3) {
                    for (MaplePet pet_ : getPetz()) {
                        if (pet_ != null && pet_.getUniqueId() != pet.getUniqueId()) {
                            if (GameConstants.getPetBuff(pet_.getPetItemId()) - petbuffid == 0) {
                                ++petBuffindex;
                            }
                        }
                    }
                } else if (petbufflists.size() == 2) {
                    for (MaplePet pet_ : getPetz()) {
                        if (pet_ != null && pet_.getUniqueId() != pet.getUniqueId()) {
                            if (GameConstants.getPetBuff(pet_.getPetItemId()) - petbuffid == 0) {
                                ++petBuffindex;
                            }
                        }
                    }
                } else if (petbufflists.size() == 1) {
                    for (MaplePet pet_ : getPetz()) {
                        if (pet_ != null && pet_.getUniqueId() != pet.getUniqueId()) {
                            if (GameConstants.getPetBuff(pet_.getPetItemId()) - petbuffid == 0) {
                                ++petBuffindex;
                            }
                        }
                    }
                }
                if (petbuffid != 0) {
                    petbufflist.add(petbuffid + petBuffindex);
                }
                for (Integer skill : petbufflist) {
                    changeSkillLevel_NoSkip(SkillFactory.getSkill(80000000 + skill), (byte) -1, (byte) -1);
                }
                unequipPet(pet, false, false);
            } else {
                //if (!(multipet.containsKey("multiPet") && multipet.get("multiPet") == 1) && getPet(0) != null) {
                //    unequipPet(getPet(0), false, false);
                //}
                //shiftPetsRight();
                final Point pos = getPosition();
                pet.setPos(pos);
                try {
                    pet.setFh(getMap().getFootholds().findBelow(pos).getId());
                } catch (NullPointerException e) {
                    pet.setFh(0);
                }
                pet.setStance(0);
                pet.setSummoned(1);
                addPetz(pet);
                pet.setSummoned(getPetIndex(pet) + 1); //then get the index
                if (broadcast && getMap() != null) {
                    //System.out.println(getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()));
                    client.getSession().write(CPet.updatePet(pet, getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
                    client.getSession().write(CPet.petStatUpdate(this));
                    //client.getSession().write(CWvsContext.updatePlayerStats(Collections.singletonMap(MapleStat.PET, pet.getUniqueId()), this));
                    getMap().broadcastMessage(this, CPet.showPet(this, pet, false, false), true);
                    client.getSession().write(CPet.loadPetPickupExceptionList(this, pet));
                }
                for (MaplePet pet_ : getPetz()) {
                    if (pet_ != null) {
                        petbuffid = GameConstants.getPetBuff(pet_.getPetItemId());
                        if (petbuffid == 0) {
                            continue;
                        }
                        petbufflists.add(petbuffid);
                    }
                }
                petbuffid = GameConstants.getPetBuff(pet.getPetItemId());
                if (petbufflists.size() == 3) {
                    for (MaplePet pet_ : getPetz()) {
                        if (pet_ != null && pet_.getUniqueId() != pet.getUniqueId()) {
                            if (GameConstants.getPetBuff(pet_.getPetItemId()) - petbuffid == 0) {
                                ++petBuffindex;
                            }
                        }
                    }
                } else if (petbufflists.size() == 2) {
                    for (MaplePet pet_ : getPetz()) {
                        if (pet_ != null && pet_.getUniqueId() != pet.getUniqueId()) {
                            if (GameConstants.getPetBuff(pet_.getPetItemId()) - petbuffid == 0) {
                                ++petBuffindex;
                            }
                        }
                    }
                } else if (petbufflists.size() == 1) {
                    for (MaplePet pet_ : getPetz()) {
                        if (pet_ != null && pet_.getUniqueId() != pet.getUniqueId()) {
                            if (GameConstants.getPetBuff(pet_.getPetItemId()) - petbuffid == 0) {
                                ++petBuffindex;
                            }
                        }
                    }
                }
                if (petbuffid != 0) {
                    petbufflist.add(petbuffid + petBuffindex);
                }
                for (Integer skill : petbufflist) {
                    changeSkillLevel_NoSkip(SkillFactory.getSkill(80000000 + skill), (byte) 1, (byte) 1);
                }

            }
            client.getSession().write(CWvsContext.enableActions());
        }
    }

    public void clearLinkMid() {
        linkMobs.clear();
        cancelEffectFromBuffStat(CharacterTemporaryStat.GuidedBullet);
        cancelEffectFromBuffStat(CharacterTemporaryStat.ArcaneAim);
    }

    public int getFirstLinkMid() {
        for (Integer lm : linkMobs.keySet()) {
            return lm.intValue();
        }
        return 0;
    }

    public Map<Integer, Integer> getAllLinkMid() {
        return linkMobs;
    }

    public void setLinkMid(int lm, int x) {
        linkMobs.put(lm, x);
    }

    public int getDamageIncrease(int lm) {
        if (linkMobs.containsKey(lm)) {
            return linkMobs.get(lm);
        }
        return 0;
    }

    public boolean isClone() {
        return clone;
    }

    public void setClone(boolean c) {
        this.clone = c;
    }

    public void setGage(short g) {
        this.gage = g;
    }

    public WeakReference<MapleCharacter>[] getClones() {
        return clones;
    }

    public MapleCharacter cloneLooks() {

        final int minus = (getId() + Randomizer.nextInt(Integer.MAX_VALUE - getId())); // really randomize it, dont want it to fail

        MapleCharacter ret = new MapleCharacter(true);
        ret.id = minus;
        ret.exp = 0;
        ret.meso = 0;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.anticheat = anticheat;
        ret.name = name;
        ret.level = level;
        ret.fame = fame;
        ret.job = job;
        ret.hair = hair;
        ret.face = face;
        ret.demonMarking = demonMarking;
        ret.hcMode = hcMode;
        ret.skinColor = skinColor;
        ret.monsterbook = monsterbook;
        ret.mount = mount;
        ret.gmLevel = gmLevel;
        ret.gender = gender;
        ret.mapid = map.getId();
        ret.map = map;
        ret.setStance(getStance());
        ret.chair = chair;
        ret.itemEffect = itemEffect;
        ret.guildid = guildid;
        ret.currentrep = currentrep;
        ret.totalrep = totalrep;
        ret.stats = stats;
        ret.effects.putAll(effects);
        ret.dispelSummons();
        ret.guildrank = guildrank;
        ret.guildContribution = guildContribution;
        ret.allianceRank = allianceRank;
        ret.setPosition(getTruePosition());
        for (Item equip : getInventory(MapleInventoryType.EQUIPPED).newList()) {
            ret.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip.copy());
        }
        ret.skillMacros = skillMacros;
        ret.keylayout = keylayout;
        ret.questinfo = questinfo;
        ret.savedLocations = savedLocations;
        ret.wishlist = wishlist;
        ret.buddylist = buddylist;
        ret.keydown_skill = 0;
        ret.lastmonthfameids = lastmonthfameids;
        ret.lastfametime = lastfametime;
        /*
         * START OF CUSTOM FEATURES
         */
        ret.loginTime = loginTime;
        /*
         * END OF CUSTOM FEATURES
         */
        ret.storage = storage;
        ret.cs = this.cs;
        ret.client.setAccountName(client.getAccountName());
        ret.nxcredit = nxcredit;
        ret.acash = acash;
        ret.maplepoints = maplepoints;
        ret.recommend = recommend;
        ret.Fanclub = Fanclub;
        ret.clone = true;
        ret.client.setChannel(this.client.getChannel());
        while (map.getCharacterById(ret.id) != null || client.getChannelServer().getPlayerStorage().getCharacterById(ret.id) != null) {
            ret.id++;
        }
        ret.client.setPlayer(ret);
        return ret;
    }

    public void setDragon(MapleDragon d) {
        this.dragon = d;
    }

    public MapleExtractor getExtractor() {
        return extractor;
    }

    public void setExtractor(MapleExtractor me) {
        removeExtractor();
        this.extractor = me;
    }

    public void removeExtractor() {
        if (extractor != null) {
            map.broadcastMessage(CField.removeExtractor(this.id));
            map.removeMapObject(extractor);
            extractor = null;
        }
    }

    public final void spawnbuff() {
        MapleQuestStatus marr = this.getQuestNAdd(MapleQuest.getInstance(GameConstants.rate2));
        if (marr.getCustomData() == null) {
            marr.setCustomData("0");
        }
        if (Long.parseLong(marr.getCustomData()) > System.currentTimeMillis()) {
            System.out.print("경험치 쿠폰 남은시간 :" + (Long.parseLong(marr.getCustomData()) - System.currentTimeMillis()));
            MapleItemInformationProvider.getInstance().getItemEffect(2450064).applyTo(this, (int) (Long.parseLong(marr.getCustomData()) - System.currentTimeMillis()));
        } else {
            marr.setCustomData("0");
        }
    }

    public final void spawnSavedPets() {
        for (int i = 0; i < petStore.length; i++) {
            if (petStore[i] > -1) {
                spawnPet(petStore[i], false, true);
            }
        }
        petStore = new byte[]{-1, -1, -1};
    }

    public final byte[] getPetStores() {
        return petStore;
    }

    public void resetStats(final int str, final int dex, final int int_, final int luk) {
        Map<MapleStat, Integer> stat = new EnumMap<MapleStat, Integer>(MapleStat.class
        );
        int total = stats.getStr() + stats.getDex() + stats.getLuk() + stats.getInt() + getRemainingAp();

        total -= str;
        stats.str = (int) str;

        total -= dex;
        stats.dex = (int) dex;

        total -= int_;
        stats.int_ = (int) int_;

        total -= luk;
        stats.luk = (int) luk;
        setRemainingAp(
                (int) total
        );
        stats.recalcLocalStats(
                this);
        stat.put(MapleStat.Str, str);

        stat.put(MapleStat.Dex, dex);

        stat.put(MapleStat.Int, int_);

        stat.put(MapleStat.Luk, luk);

        stat.put(MapleStat.AP, total);

        client.getSession()
                .write(CWvsContext.OnPlayerStatChanged(stat, false, this));
    }

    public Event_PyramidSubway getPyramidSubway() {
        return pyramidSubway;
    }

    public void setPyramidSubway(Event_PyramidSubway ps) {
        this.pyramidSubway = ps;
    }

    public byte getSubcategory() {
        if (job >= 430 && job <= 434) {
            return 1;
        }
        if (GameConstants.isCannon(job) || job == 1) {
            return 2;
        }
        if (job != 0 && job != 400) {
            return 0;
        }
        return subcategory;
    }

    public void setSubcategory(int z) {
        this.subcategory = (byte) z;
    }

    public int itemQuantity(final int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).countById(itemid);
    }

    public void setRPS(RockPaperScissors rps) {
        this.rps = rps;
    }

    public RockPaperScissors getRPS() {
        return rps;
    }

    public SpeedQuiz getSpeedQuiz() { // 스피드퀴즈
        return sq;
    }

    public void setSpeedQuiz(SpeedQuiz sq) {
        this.sq = sq;
    }

    public long getLastSpeedQuiz() {
        return lastSpeedQuiz;
    }

    public void setLastSpeedQuiz(final long t) {
        this.lastSpeedQuiz = t;
    }

    public long getNextConsume() {
        return nextConsume;
    }

    public void setNextConsume(long nc) {
        this.nextConsume = nc;
    }

    public int getRank() {
        return rank;
    }

    public int getRankMove() {
        return rankMove;
    }

    public boolean inAzwan() {
        return mapid / 1000000 == 262;
    }

    public int getJobRank() {
        return jobRank;
    }

    public int getJobRankMove() {
        return jobRankMove;
    }

    public void changeChannel(final int channel) {
        final ChannelServer toch = ChannelServer.getInstance(channel);

        if (channel == client.getChannel() || toch == null || toch.isShutdown()) {
            client.getSession().write(CField.serverBlocked(1));
            return;
        }
        final ChannelServer ch = ChannelServer.getInstance(client.getChannel());
        if (getMessenger() != null) {
            World.Messenger.silentLeaveMessenger(getMessenger().getId(), new MapleMessengerCharacter(this));
        }
        changeRemoval();
        PlayerBuffStorage.addBuffsToStorage(getId(), getAllBuffs());
        PlayerBuffStorage.addDiseaseToStorage(getId(), getAllDiseases());
        PlayerBuffStorage.addCooldownsToStorage(getId(), getCooldowns());
        World.ChannelChange_Data(new CharacterTransfer(this), getId(), channel);
        getMap().removePlayer(this);
        ch.removePlayer(this);
        saveToDB(false, false);
        client.updateLoginState(MapleClient.CHANGE_CHANNEL, client.getSessionIPAddress());
        final String s = client.getSessionIPAddress();
        LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
        client.sendPacket(CField.getChannelChange(Integer.parseInt(toch.getIP().split(":")[1])));
        client.setPlayer(null);
        client.setReceiving(false);
    }

    public void expandInventory(byte type, int amount) {
        final MapleInventory inv = getInventory(MapleInventoryType.getByType(type));
        inv.addSlot((byte) amount);
        client.getSession().write(InventoryPacket.getSlotUpdate(type, (byte) inv.getSlotLimit()));
    }

    public boolean allowedToTarget(MapleCharacter other) {
        return other != null && (!other.isHidden() || getGMLevel() >= other.getGMLevel());
    }

    public int getFollowId() {
        return followid;
    }

    public void setFollowId(int fi) {
        this.followid = fi;
        if (fi == 0) {
            this.followinitiator = false;
            this.followon = false;
        }
    }

    public void setFollowInitiator(boolean fi) {
        this.followinitiator = fi;
    }

    public void setFollowOn(boolean fi) {
        this.followon = fi;
    }

    public boolean isFollowOn() {
        return followon;
    }

    public boolean isFollowInitiator() {
        return followinitiator;
    }

    public void checkFollow() {
        if (followid <= 0) {
            return;
        }
        if (followon) {
            map.broadcastMessage(CField.followEffect(id, 0, null));
            map.broadcastMessage(CField.followEffect(followid, 0, null));
        }
        MapleCharacter tt = map.getCharacterById(followid);
        client.getSession().write(CField.getFollowMessage("Follow canceled."));
        if (tt != null) {
            tt.setFollowId(0);
            tt.getClient().getSession().write(CField.getFollowMessage("Follow canceled."));
        }
        setFollowId(0);
    }

    public int getMarriageId() {
        return marriageId;
    }

    public void setMarriageId(final int mi) {
        this.marriageId = mi;
    }

    public int getMarriageItemId() {
        return marriageItemId;
    }

    public void setMarriageItemId(final int mi) {
        this.marriageItemId = mi;
    }

    public boolean isStaff() {
        return this.gmLevel >= 7;
    }

    public boolean isDonator() {
        return this.gmLevel >= 2;
    }

    // TODO: gvup, vic, lose, draw, VR
    public boolean startPartyQuest(final int questid) {
        boolean ret = false;
        MapleQuest q = MapleQuest.getInstance(questid);
        if (q == null) {
            return false;
        }
        if (!quests.containsKey(q) || !questinfo.containsKey(questid)) {
            final MapleQuestStatus status = getQuestNAdd(q);
            status.setStatus((byte) 1);
            updateQuest(status);
            switch (questid) {
                case 1300:
                case 1301:
                case 1302: //carnival, ariants.
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0;gvup=0;vic=0;lose=0;draw=0");
                    break;
                case 1303: //ghost pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0;vic=0;lose=0");
                    break;
                case 1204: //herb town pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;have2=0;have3=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                case 1206: //ellin pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                default:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
            }
            ret = true;
        } //started the quest.
        return ret;
    }

    public String getOneInfo(final int questid, final String key) {
        if (!questinfo.containsKey(questid) || key == null || MapleQuest.getInstance(questid) == null) {
            return null;
        }
        final String[] split = questinfo.get(questid).split(";");
        for (String x : split) {
            final String[] split2 = x.split("="); //should be only 2
            if (split2.length == 2 && split2[0].equals(key)) {
                return split2[1];
            }
        }
        return null;
    }

    public void updateOneInfo(final int questid, final String key, final String value) {
        if (!questinfo.containsKey(questid) || key == null || value == null || MapleQuest.getInstance(questid) == null) {
            return;
        }
        final String[] split = questinfo.get(questid).split(";");
        boolean changed = false;
        final StringBuilder newQuest = new StringBuilder();
        for (String x : split) {
            final String[] split2 = x.split("="); //should be only 2
            if (split2.length != 2) {
                continue;
            }
            if (split2[0].equals(key)) {
                newQuest.append(key).append("=").append(value);
            } else {
                newQuest.append(x);
            }
            newQuest.append(";");
            changed = true;
        }

        updateInfoQuest(questid, changed ? newQuest.toString().substring(0, newQuest.toString().length() - 1) : newQuest.toString());
    }

    public void resetStatsByJob(boolean beginnerJob) {
        resetStats(4, 4, 4, 4); //Stat requirements no longer affected by jobs.
    }

    public boolean hasSummon() {
        return hasSummon;
    }

    public void setHasSummon(boolean summ) {
        this.hasSummon = summ;
    }

    public void removeDoor() {
        final MapleDoor door = getDoors().iterator().next();
        for (final MapleCharacter chr : door.getTarget().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final MapleCharacter chr : door.getTown().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final MapleDoor destroyDoor : getDoors()) {
            door.getTarget().removeMapObject(destroyDoor);
            door.getTown().removeMapObject(destroyDoor);
        }
        clearDoors();
    }

    public void removeMechDoor() {
        for (final MechDoor destroyDoor : getMechDoors()) {
            for (final MapleCharacter chr : getMap().getCharactersThreadsafe()) {
                destroyDoor.sendDestroyData(chr.getClient());
            }
            getMap().removeMapObject(destroyDoor);
        }
        clearMechDoors();
    }

    public void changeRemoval() {
        changeRemoval(false);
    }

    public void changeRemoval(boolean dc) {
        if (getCheatTracker() != null && dc) {
            getCheatTracker().dispose();
        }
        //dispelSummons(); // 소환수?
        if (!dc) {
            cancelEffectFromBuffStat(CharacterTemporaryStat.Flying);
            //cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            //cancelEffectFromBuffStat(MapleBuffStat.MECH_CHANGE);
            cancelEffectFromBuffStat(CharacterTemporaryStat.Regen);
        }
        if (getPyramidSubway() != null) {
            getPyramidSubway().dispose(this);
        }
        if (playerShop != null && !dc) {
            playerShop.removeVisitor(this);
            if (playerShop.isOwner(this)) {
                playerShop.setOpen(true);
            }
        }
        if (!getDoors().isEmpty()) {
            removeDoor();
        }
        if (!getMechDoors().isEmpty()) {
            removeMechDoor();
        }
        NPCScriptManager.getInstance().dispose(client);
        cancelFairySchedule(false);
    }

    public void updateTick(int newTick) {
        anticheat.updateTick(newTick);
    }

    public boolean canUseFamilyBuff(MapleFamilyBuff buff) {
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(buff.questID));
        if (stat == null) {
            return true;
        }
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Long.parseLong(stat.getCustomData()) + (24 * 3600000) < System.currentTimeMillis();
    }

    public void useFamilyBuff(MapleFamilyBuff buff) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(buff.questID));
        stat.setCustomData(String.valueOf(System.currentTimeMillis()));
    }

    public List<Integer> usedBuffs() {
        //assume count = 1
        List<Integer> used = new ArrayList<Integer>();
        MapleFamilyBuff[] z = MapleFamilyBuff.values();
        for (int i = 0; i < z.length; i++) {
            if (!canUseFamilyBuff(z[i])) {
                used.add(i);
            }
        }
        return used;
    }

    public String getTeleportName() {
        return teleportname;
    }

    public void setTeleportName(final String tname) {
        teleportname = tname;
    }

    public int getNoJuniors() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getNoJuniors();
    }

    public MapleFamilyCharacter getMFC() {
        return mfc;
    }

    public void makeMFC(final int familyid, final int seniorid, final int junior1, final int junior2) {
        if (familyid > 0) {
            MapleFamily f = World.Family.getFamily(familyid);
            if (f == null) {
                mfc = null;
            } else {
                mfc = f.getMFC(id);
                if (mfc == null) {
                    mfc = f.addFamilyMemberInfo(this, seniorid, junior1, junior2);
                }
                if (mfc.getSeniorId() != seniorid) {
                    mfc.setSeniorId(seniorid);
                }
                if (mfc.getJunior1() != junior1) {
                    mfc.setJunior1(junior1);
                }
                if (mfc.getJunior2() != junior2) {
                    mfc.setJunior2(junior2);
                }
            }
        } else {
            mfc = null;
        }
    }

    public void setFamily(final int newf, final int news, final int newj1, final int newj2) {
        if (mfc == null || newf != mfc.getFamilyId() || news != mfc.getSeniorId() || newj1 != mfc.getJunior1() || newj2 != mfc.getJunior2()) {
            makeMFC(newf, news, newj1, newj2);
        }
    }

    public int maxBattleshipHP(int skillid) {
        return (getTotalSkillLevel(skillid) * 5000) + ((getLevel() - 120) * 3000);
    }

    public int currentBattleshipHP() {
        return battleshipHP;
    }

    public void setBattleshipHP(int v) {
        this.battleshipHP = v;
    }

    public int currentTitle() {
        return headtitle;
    }

    public void setTitle(int v) {
        this.headtitle = v;
    }

    public int getRouletteItem() {
        return Roulette_Item;
    }

    public void setRouletteItem(int v) {
        this.Roulette_Item = v;
    }

    public void decreaseBattleshipHP() {
        this.battleshipHP--;
    }

    public byte currentFTC() {
        return flipthecoin;
    }

    public void setFTC(byte v) {
        this.flipthecoin = v;
    }

    public byte getTrinityCount() {
        return trinityCount;
    }

    public void setTrinityCount(byte v) {
        this.trinityCount = v;
    }

    public byte getRechargeCount() {
        return rechargeCount;
    }

    public void setRechargeCount(byte v) {
        this.rechargeCount = v;
    }

    public byte getRechargeMobCount() {
        return rechargeMobCount;
    }

    public void setRechargeMobCount(byte v) {
        this.rechargeMobCount = v;
    }

    public int getPowerTransfer() {
        return powerTransfer;
    }

    public void setPowerTransfer(int v) {
        this.powerTransfer = v;
    }

    public byte getSurPlus() {
        return SurPlus;
    }

    public void setSurPlus(byte v) {
        this.SurPlus = v;
    }

    public boolean currentSoulStone() {
        return soul_stone;
    }

    public void setSoulStone(boolean v) {
        this.soul_stone = v;
    }

    public boolean isDressUp() {
        return dressup;
    }

    public void setDressUp(boolean v) {
        this.dressup = v;
    }

    public boolean isInBlockedMap() {
        if (!isAlive() || getPyramidSubway() != null || getEventInstance() != null) {
            return true;
        }
        if ((getMapId() >= 680000210 && getMapId() <= 680000502) || (getMapId() / 10000 == 92502 && getMapId() >= 925020100) || (getMapId() / 10000 == 92503) || getMapId() == GameConstants.JAIL) {
            return true;
        }
        for (int i : GameConstants.blockedMaps) {
            if (getMapId() == i) {
                return true;
            }
        }
        return false;
    }

    public boolean isInTownMap() {
        if (hasBlockedInventory() || !getMap().isTown() || FieldLimitType.VipRock.check(getMap().getFieldLimit()) || getEventInstance() != null) {
            return false;
        }
        for (int i : GameConstants.blockedMaps) {
            if (getMapId() == i) {
                return false;
            }
        }
        return true;
    }

    public boolean hasBlockedInventory() {
        for (int i : GameConstants.blockedMaps) {
            if (getMapId() == i) {
                return false;
            }
        }
        return !isAlive() || getTrade() != null || getConversation() > 0 || getDirection() >= 0 || getPlayerShop() != null || map == null;
    }

    public void startPartySearch(final List<Integer> jobs, final int maxLevel, final int minLevel, final int membersNeeded) {
        for (MapleCharacter chr : map.getCharacters()) {
            if (chr.getId() != id && chr.getParty() == null && chr.getLevel() >= minLevel && chr.getLevel() <= maxLevel && (jobs.isEmpty() || jobs.contains(Integer.valueOf(chr.getJob()))) && (isGM() || !chr.isGM())) {
                if (party != null && party.getMembers().size() < 6 && party.getMembers().size() < membersNeeded) {
                    chr.setParty(party);
                    World.Party.updateParty(party.getId(), PartyOperation.JOIN, new MaplePartyCharacter(chr));
                    chr.receivePartyMemberHP();
                    chr.updatePartyMemberHP();
                } else {
                    break;
                }
            }
        }
    }

    public int getChallenge() {
        return challenge;
    }

    public void setChallenge(int c) {
        this.challenge = c;
    }

    public short getFatigue() {
        return fatigue;
    }

    public void setFatigue(int j) {
        this.fatigue = (short) Math.max(0, j);
        updateSingleStat(MapleStat.Fatigue, this.fatigue);
    }

    public void fakeRelog() {
        client.getSession().write(CField.getCharInfo(this));
        final MapleMap mapp = getMap();
        mapp.removePlayer(this);
        mapp.addPlayer(this);
    }

    public boolean canSummon() {
        return canSummon(5000);
    }

    public boolean canSummon(int g) {
        if (lastSummonTime + g < System.currentTimeMillis()) {
            lastSummonTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public int getIntNoRecord(int questID) {
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(questID));
        if (stat == null || stat.getCustomData() == null) {
            return 0;
        }
        return Integer.parseInt(stat.getCustomData());
    }

    public int getIntRecord(int questID) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Integer.parseInt(stat.getCustomData());
    }

    public String getStringRecord(int questID) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return stat.getCustomData();
    }

    public void updatePetAuto() {
        if (getIntNoRecord(GameConstants.HP_ITEM) > 0) {
            client.sendPacket(CField.petAutoHP(getIntRecord(GameConstants.HP_ITEM)));
        }
        if (getIntNoRecord(GameConstants.MP_ITEM) > 0) {
            client.sendPacket(CField.petAutoMP(getIntRecord(GameConstants.MP_ITEM)));
        }
    }

    public void sendEnglishQuiz(String msg) {
        client.getSession().write(NPCPacket.getEnglishQuiz(9010000, (byte) 0, 9010000, msg, "00 00"));
    }

    public void setChangeTime() {
        mapChangeTime = System.currentTimeMillis();
    }

    public long getChangeTime() {
        return mapChangeTime;
    }

    public Map<ReportType, Integer> getReports() {
        return reports;
    }

    public void addReport(ReportType type) {
        Integer value = reports.get(type);
        reports.put(type, value == null ? 1 : (value + 1));
        changed_reports = true;
    }

    public void clearReports(ReportType type) {
        reports.remove(type);
        changed_reports = true;
    }

    public void clearReports() {
        reports.clear();
        changed_reports = true;
    }

    public final int getReportPoints() {
        int ret = 0;
        for (Integer entry : reports.values()) {
            ret += entry;
        }
        return ret;
    }

    public final String getReportSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<Pair<ReportType, Integer>> offenseList = new ArrayList<Pair<ReportType, Integer>>();
        for (final Entry<ReportType, Integer> entry : reports.entrySet()) {
            offenseList.add(new Pair<ReportType, Integer>(entry.getKey(), entry.getValue()));
        }
        Collections.sort(offenseList, new Comparator<Pair<ReportType, Integer>>() {
            @Override
            public final int compare(final Pair<ReportType, Integer> o1, final Pair<ReportType, Integer> o2) {
                final int thisVal = o1.getRight();
                final int anotherVal = o2.getRight();
                return (thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1));
            }
        });
        for (int x = 0; x < offenseList.size(); x++) {
            ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(x).left.name()));
            ret.append(": ");
            ret.append(offenseList.get(x).right);
            ret.append(" ");
        }
        return ret.toString();
    }

    public short getScrolledPosition() {
        return scrolledPosition;
    }

    public void setScrolledPosition(short s) {
        this.scrolledPosition = s;
    }

    public MapleTrait getTrait(MapleTraitType t) {
        return traits.get(t);
    }

    public void forceCompleteQuest(int id) {
        MapleQuest.getInstance(id).forceComplete(this, 9270035); //troll
    }

    public List<Integer> getExtendedSlots() {
        return extendedSlots;
    }

    public int getExtendedSlot(int index) {
        if (extendedSlots.size() <= index || index < 0) {
            return -1;
        }
        return extendedSlots.get(index);
    }

    public void changedExtended() {
        changed_extendedSlots = true;
    }

    public MapleAndroid getAndroid() {
        return android;
    }

    public void removeAndroid() {
        if (map != null) {
            map.broadcastMessage(CField.deactivateAndroid(this.id));
        }
        android = null;
    }

    /*  public void setAndroid(MapleAndroid a) {
     this.android = a;
     if (map != null && a != null) {
     map.broadcastMessage(CField.spawnAndroid(this, a));
     map.broadcastMessage(CField.showAndroidEmotion(this.getId(), Randomizer.nextInt(17) + 1));
     }
     }*/
    public void setAndroid(MapleAndroid and) {
        this.android = and;
        if (map != null && and != null) { //Set
            android.setStance(0);
            android.setPos(getPosition());
            map.broadcastMessage(this, CField.spawnAndroid(this, android), true);
            map.broadcastMessage(this, CField.showAndroidEmotion(this.getId(), Randomizer.nextInt(17) + 1), true);
        } else if (map != null && and == null) { //Remove
            map.broadcastMessage(this, CField.deactivateAndroid(this.getId()), true);
        }
    }

    public void updateAndroid() {
        if (map != null && android != null) { //Set
            map.broadcastMessage(this, CField.spawnAndroid(this, android), true);
        } else if (map != null && android == null) { //Remove
            map.broadcastMessage(this, CField.deactivateAndroid(this.getId()), true);
        }
    }

    public List<Item> getRebuy() {
        return rebuy;
    }

    public MapleImp[] getImps() {
        return imps;
    }

    public void sendImp() {
        for (int i = 0; i < imps.length; i++) {
            if (imps[i] != null) {
                client.sendPacket(CWvsContext.itemPotChanged(imps[i], ImpFlag.SUMMONED.getValue(), i, true));
            }
        }
    }

    public int getBattlePoints() {
        return pvpPoints;
    }

    public int getTotalBattleExp() {
        return pvpExp;
    }

    public void setBattlePoints(int p) {
        if (p != pvpPoints) {
            client.getSession().write(InfoPacket.getBPMsg(p - pvpPoints));
            updateSingleStat(MapleStat.PvpPoint, p);
        }
        this.pvpPoints = p;
    }

    public void setTotalBattleExp(int p) {
        final int previous = pvpExp;
        this.pvpExp = p;
        if (p != previous) {
            stats.recalcPVPRank(this);

            updateSingleStat(MapleStat.PvpExp, stats.pvpExp);
            updateSingleStat(MapleStat.PvpRank, stats.pvpRank);
        }
    }

    public void changeTeam(int newTeam) {
        this.coconutteam = newTeam;

        if (inPVP()) {
            client.getSession().write(CField.getPVPTransform(newTeam + 1));
            map.broadcastMessage(CField.changeTeam(id, newTeam + 1));
        } else {
            client.getSession().write(CField.showEquipEffect(newTeam));
        }
    }

    public void disease(int type, int level, int option) {
        if (MonsterSkill.getBySkill(type) == null) {
            return;
        }
        chair = 0;
        client.sendPacket(CUserLocal.setChair(false, 0));
        map.broadcastMessage(this, CField.showChair(id, 0), false);
        giveDebuff(MonsterSkill.getBySkill(type), MobSkillFactory.getMobSkill(type, level), option);
    }

    public boolean inPVP() {
        return eventInstance != null && eventInstance.getName().startsWith("PVP");
    }

    public void clearAllCooldowns() {
        for (MapleCoolDownValueHolder m : getCooldowns()) {
            final int skil = m.skillId;
            removeCooldown(skil);
            client.sendPacket(CUserLocal.skillCooltimeSet(skil, 0));
        }
    }

    public Pair<Double, Boolean> modifyDamageTaken(double damage, MapleMapObject attacke) {
        return modifyDamageTaken(damage, attacke, false);
    }

    public Pair<Double, Boolean> mofifyTakeDamage(double damage, MapleMonster monster, boolean isDeadly, boolean isDeath) {
        Pair<Double, Boolean> ret = new Pair<>(damage, false);
        final Integer BlessingArmor = this.getBuffedValue(CharacterTemporaryStat.BlessingArmor);
        if (BlessingArmor != null) {
            if (BlessingArmor < 1) {
                cancelEffectFromBuffStat(CharacterTemporaryStat.BlessingArmor);
            } else {
                setBuffedValue(CharacterTemporaryStat.BlessingArmor, BlessingArmor - 1);
                damage = 0;
            }
        }
        final Integer HolyMagicShell = this.getBuffedValue(CharacterTemporaryStat.HolyMagicShell);
        if (HolyMagicShell != null) {
            if (HolyMagicShell < 1) {
                cancelEffectFromBuffStat(CharacterTemporaryStat.HolyMagicShell);
            } else {
                setBuffedValue(CharacterTemporaryStat.HolyMagicShell, HolyMagicShell - 1);
                damage = 0;
            }
        }
        MapleStatEffect ComboBarrier = getStatForBuff(CharacterTemporaryStat.ComboBarrier);
        if (ComboBarrier != null) {
            damage *= (1.0 - (ComboBarrier.getT() / 100.0));
        }
        MapleStatEffect MagicShield = getStatForBuff(CharacterTemporaryStat.MagicShield);
        if (MagicShield != null) {
            damage *= (1.0 - (MagicShield.getX() / 100.0));
        }
        MapleStatEffect DamAbsorbShield = getStatForBuff(CharacterTemporaryStat.DamAbsorbShield);
        if (DamAbsorbShield != null) {
            damage *= (1.0 - (DamAbsorbShield.getX() / 100.0));
        }
        if (damage > 0) {
            switch (this.getJob()) {
                case 122: {
                    if (this.getTotalSkillLevel(1220013) > 0) {
                        MapleStatEffect effect_1220013 = SkillFactory.getSkill(1220013).getEffect(getTotalSkillLevel(1220013));
                        if (effect_1220013.makeChanceResult()) {
                            effect_1220013.applyTo(this);
                        }
                    }
                    break;
                }

            }
        }
        ret.left = damage;
        return ret;
    }

    public Pair<Double, Boolean> modifyDamageTaken(double damage, MapleMapObject attacke, boolean magic) {
        Pair<Double, Boolean> ret = new Pair<Double, Boolean>(damage, false);
        if (damage <= 0) {
            return ret;
        }
        if (stats.ignoreDAMr > 0 && Randomizer.nextInt(100) < stats.ignoreDAMr_rate) {
            damage -= Math.floor((stats.ignoreDAMr * damage) / 100.0f);
        }
        if (stats.ignoreDAM > 0 && Randomizer.nextInt(100) < stats.ignoreDAM_rate) {
            damage -= stats.ignoreDAM;
        }
        List<Integer> attack = attacke instanceof MapleMonster || attacke == null ? null : (new ArrayList<>());

        if (damage > 0) {
            switch (this.getJob()) {
                case 122: {
                    if (this.getTotalSkillLevel(1220013) > 0) {
                        MapleStatEffect effect_1220013 = SkillFactory.getSkill(1220013).getEffect(getTotalSkillLevel(1220013));
                        if (effect_1220013.makeChanceResult()) {
                            effect_1220013.applyTo(this);
                        }
                    }
                    break;
                }
            }
            if (getBuffedValue(CharacterTemporaryStat.SafetyProc) != null && getBuffedValue(CharacterTemporaryStat.SafetyAbsorb) != null && getBuffedValue(CharacterTemporaryStat.Puppet) != null) {
                double buff = getBuffedValue(CharacterTemporaryStat.SafetyProc).doubleValue();
                double buffz = getBuffedValue(CharacterTemporaryStat.SafetyAbsorb).doubleValue();
                if ((int) ((buff / 100.0) * getStat().getMaxHp()) <= damage) {
                    damage -= ((buffz / 100.0) * damage);
                    cancelEffectFromBuffStat(CharacterTemporaryStat.Puppet);
                }
            } else if (getJob() == 433 || getJob() == 434) {
                final Skill advDarkSight = SkillFactory.getSkill(4330001);
                if (getTotalSkillLevel(advDarkSight) > 0 && getBuffedValue(CharacterTemporaryStat.DarkSight) == null && !skillisCooling(advDarkSight.getId())) {
                    final MapleStatEffect advMSE = advDarkSight.getEffect(getTotalSkillLevel(advDarkSight));
                    if (Randomizer.nextInt(100) < advMSE.getX()) {
                        advMSE.applyTo(this);
                    }
                }
            } else if ((getJob() == 512 || getJob() == 522) && getBuffedValue(CharacterTemporaryStat.CounterAttack) == null) {
                final Skill divine = SkillFactory.getSkill(getJob() == 512 ? 5120011 : 5220012);
                if (getTotalSkillLevel(divine) > 0 && !skillisCooling(divine.getId())) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        divineShield.applyTo(this);
                        client.sendPacket(CUserLocal.skillCooltimeSet(divine.getId(), divineShield.getCooldown(this)));
                        addCooldown(divine.getId(), System.currentTimeMillis(), divineShield.getCooldown(this) * 1000);
                        this.getClient().sendPacket(EffectPacket.showOwnBuffEffect(divine.getId(), 2, 1, 1));
                    }
                }
            }
            if (attacke != null) {
                final int damr = (Randomizer.nextInt(100) < getStat().DAMreflect_rate ? getStat().DAMreflect : 0) + (getBuffedValue(CharacterTemporaryStat.Guard) != null ? getBuffedValue(CharacterTemporaryStat.Guard) : 0);
                final int bouncedam_ = damr + (getBuffedValue(CharacterTemporaryStat.Guard) != null ? getBuffedValue(CharacterTemporaryStat.Guard) : 0);
                final int damr1 = (Randomizer.nextInt(100) < getStat().DAMreflect_rate ? getStat().DAMreflect : 0) + (getBuffedValue(CharacterTemporaryStat.PowerGuard) != null ? getBuffedValue(CharacterTemporaryStat.PowerGuard) : 0);
                final int bouncedam_1 = damr1 + (getBuffedValue(CharacterTemporaryStat.PowerGuard) != null ? getBuffedValue(CharacterTemporaryStat.PowerGuard) : 0);
                double bouncedamage = 0;
                if (bouncedam_ > 0) {
                    /*
                    MapleStatEffect eff = SkillFactory.getSkill(getBuffSource(CharacterTemporaryStat.Guard)).getEffect(getTotalSkillLevel(getBuffSource(CharacterTemporaryStat.Guard)));
                    if (getBuffSource(CharacterTemporaryStat.Guard) == 35101007) {
                        bouncedamage = (damage * eff.getX() / 100);
                    } else if (getBuffSource(CharacterTemporaryStat.Guard) != 1201007) {
                        double bouncer = (double) (damage * eff.getX() / 100);
                        if (bouncer <= 1) {
                            bouncer = 1;
                            damage = 1;
                        } else {
                            damage -= bouncer;
                        }
                        bouncedamage = (bouncer * eff.getY() / 100);
                    } else {
                        double bouncer = (double) (damage * eff.getX() / 100);
                        if (bouncer <= 1) {
                            bouncer = 1;
                            damage = 1;
                        } else {
                            damage -= bouncer;

                        }
                        bouncedamage = (bouncer * eff.getY() / 100);
                    }
                     */
                    if (attacke instanceof MapleMonster) {
                        final MapleMonster attacker = (MapleMonster) attacke;
                        if (getBuffSource(CharacterTemporaryStat.Guard) != 35101007) {
                            if (bouncedamage >= attacker.getMobMaxHp() / 2) {
                                bouncedamage = attacker.getMobMaxHp() / 2;
                            }
                            getMap().broadcastMessage(CMobPool.damaged(attacker.getObjectId(), (long) bouncedamage));
                        }
                        attacker.damage(this, (long) bouncedamage, true);
                    }
                    ret.right = true;
                } else if (bouncedam_1 > 0) {
                    MapleStatEffect eff = SkillFactory.getSkill(getBuffSource(CharacterTemporaryStat.PowerGuard)).getEffect(getTotalSkillLevel(getBuffSource(CharacterTemporaryStat.PowerGuard)));
                    bouncedamage = (damage * eff.getY() / 100);
                    if (attacke instanceof MapleMonster) {
                        final MapleMonster attacker = (MapleMonster) attacke;
                        attacker.damage(this, (long) bouncedamage, true);
                        if (getBuffSource(CharacterTemporaryStat.PowerGuard) == 31101003) {
                            if (Randomizer.nextInt(100) < eff.getProb()) {
                                attacker.applyStatus(this, new MonsterTemporaryStatEffect(MonsterTemporaryStat.Stun, 1, eff.getSourceId(), null, false), false, eff.getSubTime(), true, eff);
                            }
                        }
                    }
                    ret.right = true;
                }
                if (getJob() == 2218) {
                    if (getBuffSource(CharacterTemporaryStat.OnixWill) == 22181004) {
                        MapleStatEffect eff = SkillFactory.getSkill(getBuffSource(CharacterTemporaryStat.OnixWill)).getEffect(getTotalSkillLevel(getBuffSource(CharacterTemporaryStat.OnixWill)));
                        double rducedamage = (damage * eff.getX() / 100);
                        damage -= rducedamage;
                    }
                }
                /*
                if ((getJob() == 411 || getJob() == 412 || getJob() == 421 || getJob() == 422 || getJob() == 132) && (getBuffedValue(CharacterTemporaryStat.Summon) != null || getBuffedValue(CharacterTemporaryStat.Beholder) != null) && attacke != null) {
                    final List<MapleSummon> ss = getSummonsReadLock();
                    try {
                        for (MapleSummon sum : ss) {
                            if (sum.getTruePosition().distanceSq(getTruePosition()) < 400000.0 && (sum.getSkill() == 4111007 || sum.getSkill() == 4211007)) {
                                final List<Pair<Integer, Integer>> allDamage = new ArrayList<>();
                                if (attacke instanceof MapleMonster) {
                                    final MapleMonster attacker = (MapleMonster) attacke;
                                    final int theDmg = (int) (SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getX() * damage / 100.0);
                                    allDamage.add(new Pair<>(attacker.getObjectId(), theDmg));
                                    getMap().broadcastMessage(SummonPacket.summonAttackSpecial(sum.getOwnerId(), sum.getObjectId(), (byte) 0x84, allDamage, getLevel(), (byte) 17, true));
                                    attacker.damage(this, theDmg, true);
                                    checkMonsterAggro(attacker);
                                    if (!attacker.isAlive()) {
                                        getClient().getSession().write(CMobPool.killMonster(attacker.getObjectId(), 1));
                                    }
                                } else {
                                    final MapleCharacter chr = (MapleCharacter) attacke;
                                    final int dmg = SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getX();
                                    chr.addHP(-dmg);
                                    attack.add(dmg);
                                }
                            } else if (sum.getSkill() == 1321007) {
                                final Skill divine = SkillFactory.getSkill(1320011);
                                if (getTotalSkillLevel(divine) > 0 && getBuffSource(CharacterTemporaryStat.Beholder) == 1321007) {
                                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                                    if (divineShield.makeChanceResult()) {
                                        final List<Pair<Integer, Integer>> allDamage = new ArrayList<>();
                                        if (attacke instanceof MapleMonster) {
                                            final MapleMonster attacker = (MapleMonster) attacke;
                                            int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0);
                                            allDamage.add(new Pair<>(attacker.getObjectId(), theDmg));
                                            if (!attacker.getStats().isBoss()) {
                                                if (Randomizer.isSuccess(divineShield.getZ())) {
                                                    theDmg = (int) attacker.getHp();
                                                }
                                            }
                                            int recoverHP = (int) Math.round(theDmg * (divineShield.getX() / 100.d));
                                            addHP(recoverHP);
                                            getMap().broadcastMessage(CField.EffectPacket.showOwnRecoverHP(recoverHP));
                                            getMap().broadcastMessage(SummonPacket.summonAttackSpecial(sum.getOwnerId(), sum.getObjectId(), (byte) 0x84, allDamage, getLevel(), (byte) 1, true));
                                            attacker.damage(this, theDmg, true);
                                            checkMonsterAggro(attacker);
                                            if (!attacker.isAlive()) {
                                                getClient().getSession().write(CMobPool.killMonster(attacker.getObjectId(), 1));
                                            }
                                            getMap().broadcastMessage(CUserLocal.skillCooltimeSet(divine.getId(), divineShield.getCooldown(this)));
                                            addCooldown(divine.getId(), System.currentTimeMillis(), divineShield.getCooldown(this) * 1000);//쿨타임받아야함                        
                                        }
                                    }
                                }
                            }
                        }
                    } finally {
                        unlockSummonsReadLock();
                    }
                }
                 */
            }
        }
        if (attack != null && attack.size() > 0 && attacke != null) {
            getMap().broadcastMessage(CField.pvpCool(attacke.getObjectId(), attack));
        }
        ret.left = damage;
        return ret;
    }

    public void onAttack(long maxhp, int maxmp, int skillid, int oid, long totDamage, int critCount) {
        if (stats.hpRecoverProp > 0) {
            if (Randomizer.nextInt(100) <= stats.hpRecoverProp) {
                if (stats.hpRecover > 0) {
                    healHP(stats.hpRecover);
                }
                if (stats.hpRecoverPercent > 0) {
                    addHP(((int) Math.min(maxhp, Math.min(((int) ((double) totDamage * (double) stats.hpRecoverPercent / 100.0)), stats.getMaxHp() / 2))));
                }
            }
        }
        if (stats.mpRecoverProp > 0 && !GameConstants.isDemon(getJob())) {
            if (Randomizer.nextInt(100) <= stats.mpRecoverProp) {
                healMP(stats.mpRecover);
            }
        }
        if (this.job == 321 || this.job == 322) {
            if (this.getSkillLevel(3210004) > 0) {
                final CharacterTemporaryStat cts = CharacterTemporaryStat.SaintSaver;
                int ctv = 1;
                int skillID = 3210004;
                int skillLv = this.getTotalSkillLevel(3210004);
                final MapleStatEffect mse = SkillFactory.getSkill(skillID).getEffect(skillLv);
                int duration = 1000 * 8;
                this.setTemporaryStat(cts, ctv, mse, skillID, duration);
            }
        }
        if (skillid == 5311002) {
            if (Randomizer.rand(0, 100) < 20) {
                final CharacterTemporaryStat cts = CharacterTemporaryStat.KeyDownTimeIgnore;
                int ctv = 1;
                int skillID = 5310008;
                int skillLv = this.getTotalSkillLevel(5311002);
                final MapleStatEffect mse = SkillFactory.getSkill(skillID).getEffect(skillLv);
                int duration = 1000 * 15;
                this.setTemporaryStat(cts, ctv, mse, skillID, duration);
            }
        }
        if (getBuffedValue(CharacterTemporaryStat.ComboDrain) != null && getBuffSource(CharacterTemporaryStat.ComboDrain) != 32101004) {
            addHP(((int) Math.min(maxhp, Math.min(((int) ((double) totDamage * (double) getStatForBuff(CharacterTemporaryStat.ComboDrain).getX() / 100.0)), stats.getMaxHp() / 2))));
        } else if (getBuffSource(CharacterTemporaryStat.ComboDrain) == 32101004) {
            int recoverHp = (int) Math.min((double) totDamage * (double) getStatForBuff(CharacterTemporaryStat.ComboDrain).getX() / 100.0, stats.getMaxHp() * 0.1d);
            addHP(recoverHp);
            getMap().broadcastMessage(CField.EffectPacket.showOwnRecoverHP(recoverHp));
        }
        if (getBuffSource(CharacterTemporaryStat.ComboDrain) == 23101003) {
            addMP(((int) Math.min(maxmp, Math.min(((int) ((double) totDamage * (double) getStatForBuff(CharacterTemporaryStat.ComboDrain).getX() / 100.0)), stats.getMaxMp() / 2))));
        }
        if (skillid > 0) {
            final Skill skil = SkillFactory.getSkill(skillid);
            final MapleStatEffect effect = skil.getEffect(getTotalSkillLevel(skil));
            switch (skillid) {
                case 15111001:
                case 3111008:
                case 1078:
                case 31111003:
                case 11078:
                case 14101006:
                case 33111006:
                case 4101005:
                case 5111004: {
                    addHP(((int) Math.min(maxhp, Math.min(((int) ((double) totDamage * (double) effect.getX() / 100.0)), stats.getMaxHp() / 2))));
                    break;
                }
                case 5221015:
                case 22151002: {
                    clearLinkMid();
                    setLinkMid(oid, effect.getX());
                    break;
                }
                case 33101007: {
                    clearLinkMid();
                    break;
                }
            }
        }
    }

    public void OnUseMPHPCon(int nHP, int nMP, int nOID, int nSkillID, int nDemonForce, boolean nPrimary, boolean nHeal) {
        if (nOID > 0) {
            this.getClient().sendPacket(CField.createForceAtom(true, 0, this.getId(), 0, 1, 1, Randomizer.rand(35, 50), Randomizer.rand(5, 7), 0));
        }
        final Map<MapleStat, Integer> hpmpupdate = new EnumMap<>(MapleStat.class);
        if (nHP != 0) {
            if (!nPrimary && this.getId() != this.getId() && nHeal) {
                int realHealedHp = Math.max(0, Math.min(stats.getCurrentMaxHp() - stats.getHp(), nHP));
                if (realHealedHp > 0) {
                    int maxmp = this.getStat().getCurrentMaxMp(this.getJob()) / 256;
                    int expa = 20 * (realHealedHp) / (8 * maxmp + 190);
                    this.gainExp(expa, true, false, true);
                }
            }
            stats.setHp((stats.getHp() + nHP), this);
            hpmpupdate.put(MapleStat.HP, Integer.valueOf(stats.getHp()));
        }
        if (GameConstants.isPrepareSkill(nSkillID)) {
            if (nMP != 0) {
                int mpCon = SkillFactory.getSkill(nSkillID).getEffect(this.getSkillLevel(nSkillID)).getMpCon();
                this.addMP(-mpCon);
            }
        } else {
            if (!GameConstants.isNotPrepareBombSkill(nSkillID)) {
                if (nMP != 0) {
                    stats.setMp(stats.getMp() + nMP, this);
                    hpmpupdate.put(MapleStat.MP, Integer.valueOf(stats.getMp()));
                }
                this.getClient().sendPacket(CWvsContext.OnPlayerStatChanged(hpmpupdate, true, this));
            }
        }
    }

    public void handleForceGain(int mobObjectID, int skillID, int inc) {
        this.addMP(inc, true);
        this.getClient().sendPacket(CField.createForceAtom(true, skillID, mobObjectID, 0, 1, inc, Randomizer.rand(35, 50), Randomizer.rand(5, 7), 0));
    }

    public void afterAttack(int mobCount, int attackCount, int skillid) {
        switch (getJob()) {
            case 6100:
            case 6110:
            case 6111:
            case 6112: {
                handleEnergyCharge(61000001, mobCount * attackCount);
            }
            case 511:
            case 512: {
                handleEnergyCharge(5110001, mobCount * attackCount);
            }
            case 1510:
            case 1511:
            case 1512: {
                handleEnergyCharge(15100004, mobCount * attackCount);
                break;
            }
            case 111:
            case 112:
            case 1111:
            case 1112:
            case 2411:
            case 2412:
                if (skillid != 1111008 & getBuffedValue(CharacterTemporaryStat.ComboCounter) != null) { // shout should not give orbs
                    handleOrbgain();
                }
                break;
        }
        if (getBuffedValue(CharacterTemporaryStat.DrawBack) != null) {
            if (currentBattleshipHP() > 0) {
                decreaseBattleshipHP();
            }
            if (currentBattleshipHP() <= 0) {
                cancelEffectFromBuffStat(CharacterTemporaryStat.DrawBack);
            }
        }
        cancelEffectFromBuffStat(CharacterTemporaryStat.WindWalk);
        cancelEffectFromBuffStat(CharacterTemporaryStat.Infltrate);
        final MapleStatEffect ds = getStatForBuff(CharacterTemporaryStat.DarkSight);
        if (ds != null) {
            if (GameConstants.isDualBlade(this.getJob()) == true) {
                if (this.getSkillLevel(4330001) > 0) {
                    int chance = (9 + (4 * this.getSkillLevel(4330001)));
                    if (Randomizer.rand(0, 100) > chance) {
                        cancelEffectFromBuffStat(CharacterTemporaryStat.DarkSight);
                    }
                }
            }
            /*
             if (!ds.makeChanceResult()) {
             cancelEffectFromBuffStat(MapleBuffStat.DARKSIGHT);
             }
             */
        }
    }

    public void applyIceGage(int x) {
        updateSingleStat(MapleStat.IceGuage, x);
    }

    public Rectangle getBounds() {
        return new Rectangle(getTruePosition().x - 25, getTruePosition().y - 75, 50, 75);
    }

    public final Map<Short, Integer> getEquips() {
        final Map<Short, Integer> eq = new HashMap<>();
        MapleInventory equip = getInventory(MapleInventoryType.EQUIPPED);
        for (final Item item : equip.list()) {
            Equip item_ = (Equip) item;
            eq.put((short) item.getPosition(), item_.getFusionAnvil() > 0 ? Integer.parseInt(((Integer) item_.getItemId()).toString().substring(0, 3) + ((Integer) item_.getFusionAnvil()).toString()) : item.getItemId());
        }
        return eq;
    }

    public static void removePartTime(int cid) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("DELETE FROM parttime where cid = ?");
            ps.setInt(1, cid);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Failed to remove part time job: " + ex);
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
    }

    public static void addPartTime(PartTimeJob partTime) {
        if (partTime.getCharacterId() < 1) {
            return;
        }
        addPartTime(partTime.getCharacterId(), partTime.getJob(), partTime.getTime(), partTime.getReward());
    }

    public static void addPartTime(int cid, byte job, long time, int reward) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("INSERT INTO parttime (cid, job, time, reward) VALUES (?, ?, ?, ?)");
            ps.setInt(1, cid);
            ps.setByte(2, job);
            ps.setLong(3, time);
            ps.setInt(4, reward);
            ps.execute();
        } catch (SQLException ex) {
            System.out.println("Failed to remove part time job: " + ex);
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
    }

    public static PartTimeJob getPartTime(int cid) {
        PartTimeJob partTime = new PartTimeJob(cid);

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM parttime WHERE cid = ?");
            ps.setInt(1, cid);
            rs = ps.executeQuery();
            if (rs.next()) {
                partTime.setJob(rs.getByte("job"));
                partTime.setTime(rs.getLong("time"));
                partTime.setReward(rs.getInt("reward"));
            }
        } catch (SQLException ex) {
            System.out.println("Failed to remove part time job: " + ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return partTime;
    }

    public void handleAtomAttack() {
        boolean nAttack = false;
        int nSkillID = 0;
        int nKey = 0;
        int nHigh = 0;
        int nSpeed = 0;
        switch (this.job) {
            case 421:
            case 422: {
                if (this.getSkillLevel(4211016) > 0) {
                    nAttack = true;
                    nSkillID = 4211016;
                    nKey = 8;
                    nSpeed = 2;
                }
                break;
            }
            case 1212: {
                if (this.getSkillLevel(12121001) > 0) {
                    nAttack = true;
                    nSkillID = 12121001;
                    nKey = 12;
                    nSpeed = 1;
                }
                break;
            }
            case 1312: {
                if (this.getSkillLevel(13120017) > 0) {
                    nAttack = true;
                    nSkillID = 13120017;
                    nKey = 4;
                    nSpeed = 9;
                }
                break;
            }
        }
        if (nAttack) {
            if (nSkillID == 4211016) {
                int mobCount = 6;
                for (MapleMapObject mmo : this.getMap().getMapObjectsInRange(this.getTruePosition(), Integer.MAX_VALUE, Arrays.asList(MapleMapObjectType.ITEM))) {
                    if (mmo != null) {
                        final MapleMapItem mm = (MapleMapItem) mmo;
                        if (mm.getMeso() > 0) {
                            mobCount++;
                            //map.removeMapObject(mm);
                            //map.broadcastMessage(CField.removeItemFromMap(mm.getObjectId(), 1, this.getId()));
                            //mm.setPickedUp(true);
                        }
                    }
                }
                if (mobCount > 10) {
                    mobCount = 10;
                }
                for (int i = 0; i < mobCount; i++) {
                    nHigh = Randomizer.rand(-40, 40);
                    this.getMap().broadcastMessage(CField.createForceAtom(false, nSkillID, this.getId(), 1, 1, nKey, nHigh, nSpeed, 0));
                }
            }
            if (nSkillID == 12121001) {
                for (int i = 0; i < 8; i++) {
                    nHigh = Randomizer.rand(-40, 40);
                    this.getMap().broadcastMessage(CField.createForceAtom(false, nSkillID, this.getId(), 1, 1, nKey, nHigh, nSpeed, 0));
                }
            }
            if (nSkillID == 13120017) {
                for (int i = 0; i < 2; i++) {
                    nHigh = Randomizer.rand(-50, 50);
                    this.getMap().broadcastMessage(CField.createForceAtom(false, nSkillID, this.getId(), 1, 1, nKey, nHigh, nSpeed, 0));
                }
            }
        }
    }

    public void handleCardStack() {
        Skill noir = SkillFactory.getSkill(24120002);
        Skill blanc = SkillFactory.getSkill(24100003);
        MapleStatEffect ceffect = null;
        int advSkillLevel = getTotalSkillLevel(noir);
        boolean isAdv = false;
        if (advSkillLevel > 0) {
            ceffect = noir.getEffect(advSkillLevel);
            isAdv = true;
        } else if (getSkillLevel(blanc) > 0) {
            ceffect = blanc.getEffect(getTotalSkillLevel(blanc));
        } else {
            return;
        }
        if (ceffect.makeChanceResult()) {
            if (this.cardStack < (getJob() == 2412 ? 40 : 20)) {
                this.cardStack = (byte) (this.cardStack + 1);
            }
            this.runningStack += 1;
            this.getMap().broadcastMessage(CField.createForceAtom(false, ceffect.getSourceId(), this.getId(), 1, 1, isAdv ? 2 : 1, Randomizer.rand(35, 50), Randomizer.rand(5, 7), 0));
            this.client.sendPacket(CUserLocal.incJudgementStack(this.cardStack));
        }
    }

    public void resetRunningStack() {
        this.runningStack = 0;
    }

    public int getRunningStack() {
        return this.runningStack;
    }

    public void addRunningStack(int s) {
        this.runningStack += s;
    }

    public void setCardStack(byte amount) {
        this.cardStack = amount;
    }

    public byte getCardStack() {
        return cardStack;
    }

    public final MapleCharacterCards getCharacterCard() {
        return characterCard;
    }

    public final boolean canHold(final int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long login) {
        this.loginTime = login;
    }

//prefix..
    /*
     * End of Custom Feature
     */
    ;

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
        stats.recalcLocalStats(this);
    }

    public int getInt() {
        return int_;
    }

    public void setInt(int int_) {
        this.int_ = int_;
        stats.recalcLocalStats(this);
    }

    public int getLuk() {
        return luk;
    }

    public int getDex() {
        return dex;
    }

    public void setLuk(int luk) {
        this.luk = luk;
        stats.recalcLocalStats(this);
    }

    public void setDex(int dex) {
        this.dex = dex;
        stats.recalcLocalStats(this);
    }

    public static String makeMapleReadable(String in) {
        String i = in.replace('I', 'i');
        i = i.replace('l', 'L');
        i = i.replace("rn", "Rn");
        i = i.replace("vv", "Vv");
        i = i.replace("VV", "Vv");
        return i;
    }

    public void ban(String reason, boolean autoban) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?");
            ps.setString(1, reason);
            ps.setInt(2, accountid);
            ps.executeUpdate();
            ps.close();
            final String ip = client.getSessionIPAddress();
            ps = con.prepareStatement("SELECT ip FROM ipbans WHERE ip = ?");
            ps.setString(1, ip);
            rs = ps.executeQuery();
            if (!rs.next()) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, ip);
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        client.getSession().close();
    }

    public void ban(String reason) {
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ?");
            ps.setInt(1, 1);
            ps.setString(2, reason);
            ps.setInt(3, accountid);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
            String[] ipSplit = client.getSessionIPAddress().split(":");
            ps.setString(1, ipSplit[0]);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
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
        client.getSession().close();
    }

    public static boolean ban(String id, String reason, boolean accountId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psb = null;
        try {
            con = DatabaseConnection.getConnection();
            if (id.matches("/[0-9]{1,3}\\..*")) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, id);
                ps.executeUpdate();
                ps.close();
                con.close();
                return true;
            }
            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            } else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            }
            boolean ret = false;
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                psb = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ?");
                psb.setString(1, reason);
                psb.setInt(2, rs.getInt(1));
                psb.executeUpdate();
                psb.close();
                ret = true;
            }
            rs.close();
            ps.close();
            con.close();
            return ret;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (psb != null) {
                    psb.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return false;
    }

    public static boolean unban(String name) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int accountid = -1;
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                accountid = rs.getInt("accountid");
            }
            rs.close();
            ps.close();
            if (accountid == -1) {
                return false;
            }
            ps = con.prepareStatement("UPDATE accounts SET banned = -1 WHERE id = ?");
            ps.setInt(1, accountid);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return true;
    }

    public void changeMap(int map, int portal) {
        MapleMap warpMap = client.getChannelServer().getMapFactory().getMap(map);
        changeMap(warpMap, warpMap.getPortal(portal));
    }

    //Provides the ability for Inner Abilities to be updated whenever they are changed.
    public void applyInners() {
        stats.recalcLocalStats(this);
        innerskill_changed = true;
    }

    public void setHonourExp(int exp) {
        this.honourExp = exp;
    }

    public int getHonourExp() {
        return honourExp;
    }

    public void setHonourLevel(int level) {
        this.honourLevel = level;
    }

    public int getHonourLevel() {
        if (honourLevel == 0) {
            honourLevel++;
        }
        return honourLevel;
    }

    public List<InnerSkillValueHolder> getInnerSkills() {
        return innerSkills;
    }

    public void addHonourExp(int amount) {
        int newHonourExp = getHonourExp() + amount;
        if (getHonourLevel() == 0) {
            setHonourLevel(1);
        }
        do {
            honourLevelUp();
            newHonourExp = (newHonourExp) - ((getHonourLevel() - 1) * 500);
            setHonourExp(newHonourExp);
        } while (newHonourExp >= getHonourLevel() * 500);
        setHonourExp(getHonourExp() + amount);
        client.getSession().write(CWvsContext.characterHonorExp(getHonourLevel(), getHonourExp(), true));
        client.getSession().write(CWvsContext.professionInfo("honorLeveling", 0, getHonourLevel(), true, getHonourNextExp()));
    }

    public int getHonourNextExp() {
        if (getHonourLevel() == 0) {
            return 0;
        }
        return (getHonourLevel() + 1) * 500;
    }

    public void honourLevelUp() {

        final Map<MapleStat, Integer> statup = new EnumMap<>(MapleStat.class);
        remainingAp += 5;
        statup.put(MapleStat.AP, remainingAp);
        client.sendPacket(CWvsContext.OnPlayerStatChanged(statup, this));

        this.setHonourLevel(this.getHonourLevel() + 1);
        client.sendPacket(CWvsContext.characterHonorExp(getHonourLevel(), getHonourExp(), true));
        InnerSkillValueHolder inner = null;
        byte ability = 0;
        switch (getHonourLevel()) {
            case 2:
                inner = InnerAbillity.getInstance().renewSkill(0, -1);
                ability = 1;
                break;
            case 30:
                inner = InnerAbillity.getInstance().renewSkill(Randomizer.rand(0, 2), -1);
                ability = 2;
                break;
            case 70:
                inner = InnerAbillity.getInstance().renewSkill(Randomizer.rand(1, 3), -1);
                ability = 3;
                break;
        }
        if (getHonourLevel() == 2 || getHonourLevel() == 30 || getHonourLevel() == 70 && inner != null && ability >= 1 && ability <= 3) {
            innerSkills.add(inner);
            this.changeSingleSkillLevel(SkillFactory.getSkill(inner.getSkillId()), inner.getSkillLevel(), inner.getSkillLevel());
            client.sendPacket(CWvsContext.characterPotentialSet(ability, inner.getSkillId(), inner.getSkillLevel(), inner.getRank(), ability == 3));
            applyInners();
        }

        final Skill nHSkill = SkillFactory.getSkill(8000090);
        int nSLV = this.getHonourLevel();
        if (nSLV > 100) {
            nSLV = 100;
        }
        if (nHSkill != null) {
            this.changeSingleSkillLevel(nHSkill, nSLV, (byte) nSLV);
        }
        String nStr = "축하드립니다. " + this.getName() + "님이 명예 레벨 Lv." + this.getHonourLevel() + "을 달성하였습니다.";
        World.Broadcast.broadcastMessage(CUserLocal.chatMsg(ChatType.GroupParty, nStr));

        //makeNewAzwanShop();
    }

    public final int[] getFriendShipPoints() {
        return friendshippoints;
    }

    public final void setFriendShipPoints(int joejoe, int hermoninny, int littledragon, int ika) {
        this.friendshippoints[0] = joejoe;
        this.friendshippoints[1] = hermoninny;
        this.friendshippoints[2] = littledragon;
        this.friendshippoints[3] = ika;
    }

    public void makeNewAzwanShop() {
        azwanShopList = new MapleShop(100000000 + getId(), 2182002);
        int itemid = GameConstants.getAzwanRecipes()[(int) Math.floor(Math.random() * GameConstants.getAzwanRecipes().length)];
        azwanShopList.addItem(new MapleShopItem((short) 1, itemid, 1, 4310038, 70, (byte) 0, 0, 0, 0));
        itemid = GameConstants.getAzwanScrolls()[(int) Math.floor(Math.random() * GameConstants.getAzwanScrolls().length)];
        azwanShopList.addItem(new MapleShopItem((short) 1, itemid, 1, 4310036, 15, (byte) 0, 0, 0, 0));
        itemid = GameConstants.getAzwanScrolls()[(int) Math.floor(Math.random() * GameConstants.getAzwanScrolls().length)];
        azwanShopList.addItem(new MapleShopItem((short) 1, itemid, 1, 4310036, 15, (byte) 0, 0, 0, 0));
        itemid = (Integer) GameConstants.getUseItems()[(int) Math.floor(Math.random() * GameConstants.getUseItems().length)].getLeft();
        int price = (Integer) GameConstants.getUseItems()[(int) Math.floor(Math.random() * GameConstants.getUseItems().length)].getRight();
        azwanShopList.addItem(new MapleShopItem((short) 1, itemid, 1, price, 0, (byte) 0, 0, 0, 0));
        itemid = GameConstants.getCirculators()[(int) Math.floor(Math.random() * GameConstants.getCirculators().length)];
        price = InnerAbillity.getInstance().getCirculatorRank(itemid) * 10;
        if (InnerAbillity.getInstance().getCirculatorRank(itemid) > 3) {
            price *= 2;
        }
        azwanShopList.addItem(new MapleShopItem((short) 1, itemid, 1, InnerAbillity.getInstance().getCirculatorRank(itemid) > 5 ? 4310038 : 4310036, 0, (byte) 0, 0, 0, 0));
        if (getCheatTracker().canJeanPierreWhisper()) {
            client.getSession().write(CField.getWhisper("Jean Pierre", client.getChannel(), "Psst! I got some new items in stock! Come take a look! Oh, but if your Honor Level increased, why not wait until you get a Circulator?"));
        }
    }

    public MapleShop getAzwanShop() {
        return azwanShopList;
    }

    public void openAzwanShop() {
        if (azwanShopList == null) {
            MapleShopFactory.getInstance().getShop(2182002).sendShop(client);
        } else {
            getAzwanShop().sendShop(client);
        }
    }

    public void rewardAzwanPrize(int mobsKilled) {
        int gainedHonourExp = mobsKilled;
        if (getLevel() < 100) {
            gainedHonourExp *= 30;
        } else if (getLevel() >= 100 && getLevel() <= 200) {
            gainedHonourExp *= 40;
        } else if (getLevel() >= 201) {
            gainedHonourExp *= 50;
        }
        int availCCoins = getAzwanCCoinsAvail();
        int availECoins = getAzwanECoinsAvail();
        if (mobsKilled >= 0 && mobsKilled < 25) {
            addHonourExp(gainedHonourExp);
            dropMessage(5, "Your party has thwarted " + mobsKilled + " minions! You have received " + gainedHonourExp + " Honor experience!");
        } else if (mobsKilled >= 25 && mobsKilled < 100) {
            setAzwanCCoinsAvail(availCCoins + 3);
            addHonourExp(gainedHonourExp);
            dropMessage(5, "Your party has thwarted " + mobsKilled + " minions! You have received " + 3 + " Conqueror's Coins and " + gainedHonourExp + " Honor experience!");
        } else if (mobsKilled >= 100 && mobsKilled < 250) {
            setAzwanCCoinsAvail(availCCoins + 15);
            addHonourExp(gainedHonourExp);
            dropMessage(5, "Your party has thwarted " + mobsKilled + " minions! You have received " + 15 + " Conqueror's Coins and " + gainedHonourExp + " Honor experience!");
        } else if (mobsKilled >= 250 && mobsKilled < 450) {
            setAzwanECoinsAvail(availECoins + 2);
            addHonourExp(gainedHonourExp);
            dropMessage(5, "Your party has thwarted " + mobsKilled + " minions! You have received " + 2 + " Emperor's Coins and " + gainedHonourExp + " Honor experience!");
        } else if (mobsKilled >= 450 && mobsKilled != 9999) {
            setAzwanECoinsAvail(availECoins + 4);
            addHonourExp(gainedHonourExp);
            dropMessage(5, "Your party has thwarted " + mobsKilled + " minions! You have received " + 4 + " Emperor's Coins and " + gainedHonourExp + " Honor experience!");
        } else if (mobsKilled == 9999) {
            setAzwanECoinsAvail(availECoins + 7);
            setAzwanCCoinsAvail(availCCoins + 25);
            addHonourExp(gainedHonourExp);
        }
    }

    public int getAzwanCoinsAvail() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT azwanCoinsAvail FROM characters WHERE id = ?");
            ps.setInt(1, client.getPlayer().getId());
            rs = ps.executeQuery();

            if (rs.next()) {
                azwanCoinsAvail = rs.getInt("azwanCoinsAvail");
            }
        } catch (SQLException e) {
            System.err.println("Error getting azwanCoinsAvail for character: " + client.getPlayer().getName() + ". error: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return azwanCoinsAvail;
    }

    public int getAzwanCCoinsAvail() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT azwanCCoinsAvail FROM characters WHERE id = ?");
            ps.setInt(1, client.getPlayer().getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                azwanECoinsAvail = rs.getInt("azwanCCoinsAvail");
            }
        } catch (SQLException e) {
            System.err.println("Error getting azwanCCoinsAvail for character: " + client.getPlayer().getName() + ". error: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return azwanECoinsAvail;
    }

    public int getAzwanECoinsAvail() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT azwanECoinsAvail FROM characters WHERE id = ?");
            ps.setInt(1, client.getPlayer().getId());
            rs = ps.executeQuery();

            if (rs.next()) {
                azwanCCoinsAvail = rs.getInt("azwanECoinsAvail");
            }
        } catch (SQLException e) {
            System.err.println("Error getting azwanECoinsAvail for character: " + client.getPlayer().getName() + ". error: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return azwanCCoinsAvail;
    }

    public int getAzwanCoinsRedeemed() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT azwanCoinsRedeemed FROM characters WHERE id = ?");
            ps.setInt(1, client.getPlayer().getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                azwanCoinsRedeemed = rs.getInt("azwanCoinsRedeemed");
            }
        } catch (SQLException e) {
            System.err.println("Error getting azwanCoinsRedeemed for character: " + client.getPlayer().getName() + ". error: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return azwanCoinsRedeemed;
    }

    public void setAzwanCoinsAvailM(int quantity) {
        this.azwanCoinsAvail = quantity;
    }

    public void setAzwanCoinsAvail(int quantity) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET azwanCoinsAvail = ? WHERE id = ?");
            ps.setInt(1, quantity);
            ps.setInt(2, client.getPlayer().getId());
            ps.execute();
        } catch (SQLException ex) {
            System.err.println("Error setting azwanCoinsAvail for character: " + client.getPlayer().getName() + ". error: " + ex);
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
    }

    public void setAzwanECoinsAvail(int quantity) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET azwanECoinsAvail = ? WHERE id = ?");
            ps.setInt(1, quantity);
            ps.setInt(2, client.getPlayer().getId());
            ps.execute();
        } catch (SQLException e) {
            System.err.println("Error setting azwanECoinsAvail for character: " + client.getPlayer().getName() + ". error: " + e);
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
    }

    public void setAzwanCCoinsAvail(int quantity) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET azwanCCoinsAvail = ? WHERE id = ?");
            ps.setInt(1, quantity);
            ps.setInt(2, client.getPlayer().getId());
            ps.execute();
        } catch (SQLException e) {
            System.err.println("Error setting azwanCCoinsAvail for character: " + client.getPlayer().getName() + ". error: " + e);
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
    }

    public void setAzwanCoinsRedeemedM(int quantity) {
        this.azwanCoinsRedeemed = quantity;
    }

    public void setAzwanCoinsRedeemed(int quantity) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("UPDATE characters SET azwanCoinsRedeemed = ? WHERE id = ?");
            ps.setInt(1, quantity);
            ps.setInt(2, client.getPlayer().getId());
            ps.execute();
        } catch (SQLException e) {
            System.err.println("Error setting azwanCoinsRedeemed for character: " + client.getPlayer().getName() + ". error: " + e);
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
    }

    public boolean checkArdentmill() {
        boolean inDBAlready = false;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT characterID FROM ardentmill WHERE characterID = ?");
            ps.setInt(1, client.getPlayer().getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                inDBAlready = true;
            }
        } catch (SQLException e) {
            System.err.println("Error checking checkArdentmill for character: " + client.getPlayer().getName() + ". error: " + e);
            inDBAlready = false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return inDBAlready;
    }

    public void setArdentmillPortal(String Portal, int quantity) {
        Connection con = null;
        PreparedStatement ps = null;
        if (checkArdentmill()) {
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("UPDATE ardentmill SET " + Portal + " = ? WHERE characterID = ?");
                ps.setInt(1, quantity);
                ps.setInt(2, client.getPlayer().getId());
                ps.execute();
            } catch (SQLException e) {
                System.err.println("Error setting ArdentmillPortal #1" + Portal + "(" + quantity + ") for character: " + client.getPlayer().getName() + ". error: " + e);
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
        } else {
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("INSERT INTO ardentmill (characterID, " + Portal + ") VALUES (?, ?)");
                ps.setInt(1, client.getPlayer().getId());
                ps.setInt(2, quantity);
                ps.execute();
            } catch (SQLException e) {
                System.err.println("Error setting ArdentmillPortal #2" + Portal + "(" + quantity + ") for character: " + client.getPlayer().getName() + ". error: " + e);
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
        }
    }

    public int getArdentmillPortal(String Portal) {
        int timesEnteredDaily = 0;
        if (!checkArdentmill()) {
            return 0;
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT " + Portal + " FROM ardentmill WHERE characterID = ?");
            ps.setInt(1, client.getPlayer().getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                timesEnteredDaily = rs.getInt(Portal);
            }
        } catch (SQLException e) {
            System.err.println("Error getting ArdentmillPortal for character: " + client.getPlayer().getName() + ". error: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return timesEnteredDaily;
    }

    public int getTodayCharisma() {
        return todaycharisma;
    }

    public void setTodayCharisma(short tca) {
        this.todaycharisma = tca;
    }

    public int getTodayInsight() {
        return todayinsight;
    }

    public void setTodayInsight(short tin) {
        this.todayinsight = tin;
    }

    public int getTodayWillpower() {
        return todaywill;
    }

    public void setTodayWillpower(short twi) {
        this.todaywill = twi;
    }

    public int getTodayCraft() {
        return todaycraft;
    }

    public void setTodayCraft(short tcr) {
        this.todaycraft = tcr;
    }

    public int getTodaySense() {
        return todaysense;
    }

    public void setTodaySense(short tse) {
        this.todaysense = tse;
    }

    public int getTodayCharm() {
        return todaycharm;
    }

    public void setTodayCharm(short tch) {
        this.todaycharm = tch;
    }

    public void setTodayTrait(short tch) {
        this.todaycharm = tch;
        this.todaycraft = tch;
        this.todaycharisma = tch;
        this.todaywill = tch;
        this.todaysense = tch;
        this.todayinsight = tch;
        this.todayrep = tch;
    }

    public boolean getpetAutoFeed() {
        return petAutoFeed;
    }

    public void unchooseStolenSkill(int skillID) { //base skill
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "[Loadout] The skill is under cooldown. Please wait.");
            return;
        }
        final int job = GameConstants.getJobNumber(skillID / 10000);
        boolean changed = false;
        for (Pair<Integer, Boolean> sk : stolenSkills) {
            if (sk.right && GameConstants.getJobNumber(sk.left / 10000) == job) {
                cancelStolenSkill(sk.left);
                sk.right = false;
                changed = true;
            }
        }
        if (changed) {
            final Skill skil = SkillFactory.getSkill(skillID);
            int v1 = Math.max(skil.getMaxLevel(), skil.getMasterLevel());
            changeSkillLevel_Skip(skil, getSkillLevel(skil), (byte) v1);
            client.sendPacket(CWvsContext.resultSetStealSkill(GameConstants.getStealSkill(job), 0));
        }
    }

    public void cancelStolenSkill(int skillID) {
        final Skill skk = SkillFactory.getSkill(skillID);
        final MapleStatEffect eff = skk.getEffect(getTotalSkillLevel(skk));
        if (eff.isMonsterBuff() || (eff.getStatups().isEmpty() && !eff.getMonsterStati().isEmpty())) {
            for (MapleMonster mons : map.getAllMonstersThreadsafe()) {
                for (MonsterTemporaryStat b : eff.getMonsterStati().keySet()) {
                    if (mons.isBuffed(b) && mons.getBuff(b).getFromID() == this.id) {
                        mons.cancelStatus(b);
                    }
                }
            }
        } else if (eff.getDuration() > 0 && !eff.getStatups().isEmpty()) {
            for (MapleCharacter chr : map.getCharactersThreadsafe()) {
                chr.cancelEffect(eff, false, -1);
            }
        }
    }

    public void chooseStolenSkill(int skillID) {
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(5, "[Loadout] The skill is under cooldown. Please wait.");
            return;
        }
        final Pair<Integer, Boolean> dummy = new Pair<Integer, Boolean>(skillID, false);
        if (stolenSkills.contains(dummy)) {
            unchooseStolenSkill(skillID);
            stolenSkills.get(stolenSkills.indexOf(dummy)).right = true;
            final Skill skil = SkillFactory.getSkill(skillID);
            int v1 = Math.max(skil.getMaxLevel(), skil.getMasterLevel());
            changeSkillLevel_Skip(skil, getSkillLevel(skil), (byte) v1);
            client.sendPacket(CWvsContext.resultSetStealSkill(GameConstants.getStealSkill(GameConstants.getJobNumber(skillID / 10000)), skillID));
        }
    }

    public void addStolenSkill(int skillID, int skillLevel) {
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "[Loadout] The skill is under cooldown. Please wait.");
            return;
        }
        final Pair<Integer, Boolean> dummy = new Pair<Integer, Boolean>(skillID, true);
        final Skill skil = SkillFactory.getSkill(skillID);
        if (!stolenSkills.contains(dummy)/* && GameConstants.canSteal(skil)*/) {
            dummy.right = false;
            skillLevel = Math.min(skil.getMaxLevel(), skillLevel);
            final int job = GameConstants.getJobNumber(skillID / 10000);
            if (!stolenSkills.contains(dummy) && getSkillLevel(GameConstants.getStealSkill(job)) > 0) {
                int count = 0;
                skillLevel = Math.min(getSkillLevel(GameConstants.getStealSkill(job)), skillLevel);
                for (Pair<Integer, Boolean> sk : stolenSkills) {
                    if (GameConstants.getJobNumber(sk.left / 10000) == job) {
                        count++;
                    }
                }
                if (count < GameConstants.getNumSteal(job)) {
                    stolenSkills.add(dummy);
                    changed_skills = true;
                    changeSkillLevel_Skip(skil, skillLevel, (byte) skillLevel);
                    client.getSession().write(CField.addStolenSkill(job, count, skillID, skillLevel));
                    //client.getSession().write(MaplePacketCreator.updateStolenSkills(this, job));
                }
            }
        }
    }

    public void setLinkageSkill() {
        switch (this.getJob()) {
            case 2100:
            case 2110:
            case 2111:
            case 2112: {
                if (this.getSkillLevel(21001010) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001049), this.getSkillLevel(21001010), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001050), this.getSkillLevel(21001010), (byte) -1);
                }
                if (this.getSkillLevel(21111032) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001051), this.getSkillLevel(21111032), (byte) -1);
                }
                if (this.getSkillLevel(21121022) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001052), this.getSkillLevel(21121022), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001053), this.getSkillLevel(21121022), (byte) -1);
                }
                break;
            }
            case 5200:
            case 5210:
            case 5211:
            case 5212: {
                if (this.getSkillLevel(52101000) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001043), this.getSkillLevel(52101000), (byte) -1);
                }
                if (this.getSkillLevel(52111000) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001044), this.getSkillLevel(52111000), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001045), this.getSkillLevel(52111000), (byte) -1);
                }
                if (this.getSkillLevel(52121000) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001046), this.getSkillLevel(52121000), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001047), this.getSkillLevel(52121000), (byte) -1);
                }
                break;
            }
            case 6100:
            case 6110:
            case 6111:
            case 6112: {
                if (this.getSkillLevel(61001000) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001002), this.getSkillLevel(61001000), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001003), this.getSkillLevel(61001000), (byte) -1);
                }
                if (this.getSkillLevel(61001002) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001004), this.getSkillLevel(61001002), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001005), this.getSkillLevel(61001002), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001006), this.getSkillLevel(61001002), (byte) -1);
                }
                if (this.getSkillLevel(61101000) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001007), this.getSkillLevel(61101000), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001008), this.getSkillLevel(61101000), (byte) -1);
                }
                if (this.getSkillLevel(61111000) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001010), this.getSkillLevel(61111000), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001011), this.getSkillLevel(61111000), (byte) -1);
                }
                if (this.getSkillLevel(61121000) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001012), this.getSkillLevel(61121000), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001013), this.getSkillLevel(61121000), (byte) -1);
                }
                break;
            }
            case 6200:
            case 6210:
            case 6211:
            case 6212: {
                if (this.getSkillLevel(62001000) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001014), this.getSkillLevel(62001000), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001015), this.getSkillLevel(62001000), (byte) -1);
                }
                if (this.getSkillLevel(62101005) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001016), this.getSkillLevel(62101005), (byte) -1);
                }
                break;
            }
            case 6500:
            case 6510:
            case 6511:
            case 6512: {
                if (this.getSkillLevel(65101001) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001017), this.getSkillLevel(65101001), (byte) -1);
                }
                if (this.getSkillLevel(65101003) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001018), this.getSkillLevel(65101003), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001019), this.getSkillLevel(65101003), (byte) -1);
                }
                if (this.getSkillLevel(65111001) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001020), this.getSkillLevel(65111001), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001021), this.getSkillLevel(65111001), (byte) -1);
                }
                if (this.getSkillLevel(65111002) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001022), this.getSkillLevel(65111002), (byte) -1);
                }
                if (this.getSkillLevel(65121001) > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001023), this.getSkillLevel(65121001), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001024), this.getSkillLevel(65121001), (byte) -1);
                    this.changeSingleSkillLevel(SkillFactory.getSkill(8001025), this.getSkillLevel(65121001), (byte) -1);
                }
                break;
            }
        }
    }

    public void removeStolenSkill(int skillID) {
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "[Loadout] The skill is under cooldown. Please wait.");
            return;
        }
        final int job = GameConstants.getJobNumber(skillID / 10000);
        final Pair<Integer, Boolean> dummy = new Pair<Integer, Boolean>(skillID, false);
        int count = -1, cc = 0;
        for (int i = 0; i < stolenSkills.size(); i++) {
            if (stolenSkills.get(i).left == skillID) {
                if (stolenSkills.get(i).right) {
                    unchooseStolenSkill(skillID);
                }
                count = cc;
                break;
            } else if (GameConstants.getJobNumber(stolenSkills.get(i).left / 10000) == job) {
                cc++;
            }
        }
        if (count >= 0) {
            cancelStolenSkill(skillID);
            stolenSkills.remove(dummy);
            dummy.right = true;
            stolenSkills.remove(dummy);
            changed_skills = true;
            changeSkillLevel_Skip(SkillFactory.getSkill(skillID), 0, (byte) 0);
            //hacky process begins here
            client.sendPacket(CWvsContext.resultSetStealSkill(GameConstants.getStealSkill(job), 0));
            for (int i = 0; i < GameConstants.getNumSteal(job); i++) {
                client.getSession().write(CField.removeStolenSkill(job, i));
            }
            count = 0;
            for (Pair<Integer, Boolean> sk : stolenSkills) {
                if (GameConstants.getJobNumber(sk.left / 10000) == job) {
                    client.getSession().write(CField.addStolenSkill(job, count, sk.left, getSkillLevel(sk.left)));
                    if (sk.right) {
                        client.sendPacket(CWvsContext.resultSetStealSkill(GameConstants.getStealSkill(job), sk.left));
                    }
                    count++;
                }
            }
            client.getSession().write(CField.removeStolenSkill(job, count));
        }
    }

    public List<Pair<Integer, Boolean>> getStolenSkills() {
        return stolenSkills;
    }

    public void changeSkillLevel_Skip(Skill skil, int skilLevel, byte masterLevel) {
        final Map<Skill, SkillEntry> enry = new HashMap<Skill, SkillEntry>(1);
        enry.put(skil, new SkillEntry(skilLevel, masterLevel, -1L));
        changeSkillLevel_Skip(enry, true);
    }

    public void changeSkillLevel_NoSkip(Skill skil, int skilLevel, byte masterLevel) {
        final Map<Skill, SkillEntry> enry = new HashMap<Skill, SkillEntry>(1);
        enry.put(skil, new SkillEntry(skilLevel, masterLevel, -1L));
        changeSkillLevel_NoSkip(enry, true);
    }

    public String getKeyValue(String key) {
        if (CustomValues.containsKey(key)) {
            return CustomValues.get(key);
        }
        return null;
    }

    public void setKeyValue(String key, String values) {
        if (CustomValues.containsKey(key)) {
            CustomValues.remove(key);
        }
        CustomValues.put(key, values);
        keyvalue_changed = true;
    }

    public void setCData(int questid, int points) {
        final MapleQuestStatus record = client.getPlayer().getQuestNAdd(MapleQuest.getInstance(questid));

        if (record.getCustomData() != null) {
            record.setCustomData(String.valueOf(points + Integer.parseInt(record.getCustomData())));
        } else {
            record.setCustomData(String.valueOf(points)); // First time
        }
    }

    public int getCData(MapleCharacter sai, int questid) {
        final MapleQuestStatus record = sai.getQuestNAdd(MapleQuest.getInstance(questid));
        if (record.getCustomData() != null) {
            return Integer.parseInt(record.getCustomData());
        }
        return 0;
    }

    public boolean getPetAutoFeed() {
        return petAutoFeed;
    }

    public void setPetAutoFeed(boolean toggle) {
        if (toggle) {
            this.dropMessage(MessageType.SYSTEM, "Pet Autofeed has been enabled.");
        } else {
            this.dropMessage(MessageType.SYSTEM, "Pet Autofeed has been disabled.");
        }
        petAutoFeed = toggle;
    }

    public void addTimer(MapleCharacterTimer t, java.util.Timer timer) {
        removeTimer(t);
        timers.put(t, timer);
    }

    public void removeTimer(MapleCharacterTimer t) {
        if (timers.containsKey(t)) {
            timers.get(t).cancel();
            timers.get(t).purge();
            timers.remove(t);
        }
    }

    public java.util.Timer getTimer(MapleCharacterTimer t) {
        return timers.get(t);
    }

    public Map<MapleCharacterTimer, java.util.Timer> getAllTimers() {
        return timers;
    }

    public int addPlusOfGlassMorph(int amount) {
        glass_plusMorph += amount;
        if (glass_plusMorph >= 10000) {
            glass_plusMorph = 10000;
        }
        return glass_plusMorph;
    }

    public int addMinusOfGlassMorph(int amount) {
        glass_minusMorph -= amount;
        if (glass_minusMorph <= 1) {
            glass_minusMorph = 1;
        }

        return glass_minusMorph;
    }

    public int getPlusOfGlassMorph() {
        return glass_plusMorph;
    }

    public int getMinusOfGlassMorph() {
        return glass_minusMorph;
    }

    public int getExeedCount() {
        return exeedCount;
    }

    public void setExeedCount(int v1) {
        this.exeedCount = v1;
    }

    public int getExeedAttackCount() {
        return exeedAttackCount;
    }

    public void setExeedAttackCount(int v1) {
        this.exeedAttackCount = v1;
    }

    public int getArcaneAim() {
        return arcaneAim;
    }

    public void setArcaneAim(int v1) {
        this.arcaneAim = v1;
    }

    public boolean isEquilibrium() {
        if (getBuffedValue(CharacterTemporaryStat.Larkness) != null) {
            switch (this.getBuffedValue(CharacterTemporaryStat.Larkness)) {
                case 20040218:
                case 20040219: {
                    return true;
                }
            }
        }
        return false;
    }

    public final void getSunfireBuffedValue(int skillid, int attackSkill, Integer Gauge) {
        final Skill sunfireid = SkillFactory.getSkill(skillid);
        final int skilllevel = getSkillLevel(attackSkill);
        if (skilllevel > 0) {
            final MapleStatEffect sunfireBuff = sunfireid.getEffect(skilllevel);
            if (attackSkill > 0) {
                if (getBuffedValue(CharacterTemporaryStat.Larkness) == null || getBuffedValue(CharacterTemporaryStat.Larkness) == -1) {
                    sunfireBuff.applySunfireBuff(this, false, attackSkill);
                } else {
                    //if (getBuffedValue(CharacterTemporaryStat.Larkness) != 20040219) {
                    sunfireBuff.applySunfireBuff(this, true, attackSkill);
                    /*
                        if (getMinusOfGlassMorph() <= 1) {
                            final MapleStatEffect equilibriumBuff = SkillFactory.getSkill(20040220).getEffect(1);
                            equilibriumBuff.applyequilibriumBuff(this, true);
                        } else {
                            sunfireBuff.applySunfireBuff(this, true, attackSkill);
                        }
                     */
                    //}
                }
            }
        }
    }

    public final void getEclipseBuffedValue(int skillid, int attackSkill, Integer Gauge) {
        final Skill eclipseid = SkillFactory.getSkill(skillid);
        final int skilllevel = getSkillLevel(attackSkill);
        if (skilllevel > 0) {
            final MapleStatEffect eclipseBuff = eclipseid.getEffect(skilllevel);
            if (attackSkill > 0) {
                if (getBuffedValue(CharacterTemporaryStat.Larkness) == null || getBuffedValue(CharacterTemporaryStat.Larkness) == -1) {
                    eclipseBuff.applyEclipseBuff(this, false, attackSkill);
                } else {
                    //if (getBuffedValue(CharacterTemporaryStat.Larkness) != 20040218) {
                    eclipseBuff.applyEclipseBuff(this, true, attackSkill);
                    /*
                        if (getPlusOfGlassMorph() >= 10000) {
                            final MapleStatEffect equilibriumBuff = SkillFactory.getSkill(20040219).getEffect(1);
                            equilibriumBuff.applyequilibriumBuff(this, false);
                        } else {
                            eclipseBuff.applyEclipseBuff(this, true, attackSkill);
                        }
                     */
                    //}
                }
            }
        }
    }

    public final void addSaintSaver(int amount) {
        this.dropMessage(5, "saintSaver");
        MapleCharacter hpz = this;
        if (hpz != null) {
            //if (hpz.getBuffedValue(CharacterTemporaryStat.SaintSaver) == null) {
            saintsaver = Math.min(500, saintsaver + amount);
            if (saintsaver > 499) {
                saintsaver = 1;
            }
            this.getClient().getSession().write(UIPacket.saintSaver(hpz));
            //}
        }
    }

    public void gainPet(int id, String name, int level, int closeness, int fullness, long period, short flags) {
        if (id > 5000200 || id < 5000000) {
            id = 5000000;
        }
        if (level > 30) {
            level = 30;
        }
        if (closeness > 30000) {
            closeness = 30000;
        }
        if (fullness > 100) {
            fullness = 100;
        }
        try {
            MapleInventoryManipulator.addById(getClient(), id, (short) 1, "", MaplePet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5000054 ? (int) period : 0, flags, 0), 45, " 메이플캐릭터메서드 " + " on " + FileoutputUtil.CurrentReadable_Date());
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public final int getSaintSaver() {
        return saintsaver;
    }

    public CalcDamage getCalcDamage() {
        return calcDamage;
    }

    public void addEquip(int pos, int itemid, int watk, int wdef, int mdef, int upslot, int hp, int mp) {
        MapleInventory equip = getInventory(MapleInventoryType.EQUIPPED);
        Equip eq = new Equip(itemid, (short) pos, (byte) 0);
        eq.setWatk((short) watk);
        eq.setWdef((short) wdef);
        eq.setMdef((short) mdef);
        eq.setMp((short) mp);
        eq.setHp((short) hp);
        if (itemid == 1099004) {
            eq.setStr((short) 12);
            eq.setDex((short) 12);
        }
        if (itemid == 1098002) {
            eq.setStr((short) 7);
            eq.setDex((short) 7);
        }
        if (itemid == 1098003) {
            eq.setStr((short) 12);
            eq.setDex((short) 12);
        }
        eq.setUpgradeSlots((byte) upslot);
        eq.setExpiration(-1);
        equip.addFromDB(eq.copy());
    }

    public String getDateKey(String key) {
        Calendar ocal = Calendar.getInstance();
        int year = ocal.get(ocal.YEAR);
        int month = ocal.get(ocal.MONTH) + 1;
        int day = ocal.get(ocal.DAY_OF_MONTH);
        return getKeyValue1(year + "" + month + "" + day + "_" + key);
    }

    public void setDateKey(String key, String value) {
        Calendar ocal = Calendar.getInstance();
        int year = ocal.get(ocal.YEAR);
        int month = ocal.get(ocal.MONTH) + 1;
        int day = ocal.get(ocal.DAY_OF_MONTH);
        setKeyValue(year + "" + month + "" + day + "_" + key, value, true);
    }

    public void setKeyValue(String key, String value, boolean a) {
        Connection con = null;
        PreparedStatement ps = null;
        if (getKeyValue1(key) == null) {
            try {
                con = DatabaseConnection.getConnection();
                String query = "INSERT into `acheck` (`cid`, `keya`, `value`, `day`) VALUES ('";
                query = new StringBuilder().append(query).append(id).toString();
                query = new StringBuilder().append(query).append("', '").toString();
                query = new StringBuilder().append(query).append(key).toString();
                query = new StringBuilder().append(query).append("', '").toString();
                query = new StringBuilder().append(query).append(value).toString();
                if (a) {
                    query = new StringBuilder().append(query).append("', '").toString();
                    query = new StringBuilder().append(query).append("1").toString();
                    query = new StringBuilder().append(query).append("')").toString();
                } else {
                    query = new StringBuilder().append(query).append("', '").toString();
                    query = new StringBuilder().append(query).append("0").toString();
                    query = new StringBuilder().append(query).append("')").toString();
                }
                ps = con.prepareStatement(query);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
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
        } else {
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("UPDATE acheck SET value = ? WHERE cid = ? AND keya = ?");
                ps.setString(1, value);
                ps.setInt(2, id);
                ps.setString(3, key);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
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
        }
    }

    public String getKeyValue1(String key) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM acheck WHERE cid = ? and keya = ?");
            ps.setInt(1, id);
            ps.setString(2, key);
            rs = ps.executeQuery();
            while (rs.next()) {
                String ret = rs.getString("value");
                rs.close();
                ps.close();
                con.close();
                return ret;
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return null;
    }

    public String getDateKeyS(String key) {
        Calendar ocal = Calendar.getInstance();
        int year = ocal.get(ocal.YEAR);
        int month = ocal.get(ocal.MONTH) + 1;
        int day = ocal.get(ocal.DAY_OF_MONTH);
        return getKeyValue1S(year + "" + month + "" + day + "_" + key);
    }

    public void setDateKeyS(String key, String value) {
        Calendar ocal = Calendar.getInstance();
        int year = ocal.get(ocal.YEAR);
        int month = ocal.get(ocal.MONTH) + 1;
        int day = ocal.get(ocal.DAY_OF_MONTH);
        setKeyValueS(year + "" + month + "" + day + "_" + key, value, true);
    }

    public void setKeyValueS(String key, String value, boolean a) {
        Connection con = null;
        PreparedStatement ps = null;
        if (getKeyValue1S(key) == null) {
            try {
                con = DatabaseConnection.getConnection();
                String query = "INSERT into `achecks` (`cid`, `keya`, `value`, `day`) VALUES ('";
                query = new StringBuilder().append(query).append(getAccountID()).toString();
                query = new StringBuilder().append(query).append("', '").toString();
                query = new StringBuilder().append(query).append(key).toString();
                query = new StringBuilder().append(query).append("', '").toString();
                query = new StringBuilder().append(query).append(value).toString();
                if (a) {
                    query = new StringBuilder().append(query).append("', '").toString();
                    query = new StringBuilder().append(query).append("1").toString();
                    query = new StringBuilder().append(query).append("')").toString();
                } else {
                    query = new StringBuilder().append(query).append("', '").toString();
                    query = new StringBuilder().append(query).append("0").toString();
                    query = new StringBuilder().append(query).append("')").toString();
                }
                ps = con.prepareStatement(query);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
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
        } else {
            try {
                con = DatabaseConnection.getConnection();
                ps = con.prepareStatement("UPDATE achecks SET value = ? WHERE cid = ? AND keya = ?");
                ps.setString(1, value);
                ps.setInt(2, getAccountID());
                ps.setString(3, key);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
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
        }
    }

    public String getKeyValue1S(String key) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM achecks WHERE cid = ? and keya = ?");
            ps.setInt(1, getAccountID());
            ps.setString(2, key);
            rs = ps.executeQuery();
            while (rs.next()) {
                String ret = rs.getString("value");
                rs.close();
                ps.close();
                con.close();
                return ret;
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
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
        return null;
    }

    public void send(Object ob) {
        getClient().getSession().write((byte[]) ob);
    }

    public void removeKeyValue(int type) {
        MapleQuest quest = MapleQuest.getInstance(type);
        final MapleQuestStatus stat = quests.get(quest);
        if (stat == null) { // key ?곗씠?곌?  null
            return;
        }
        if (stat.getCustomData() == null) {
            return;
        }
        stat.setCustomData("");
        updateInfoQuest(type, stat.getCustomData());
    }

    public void setKeyValue(int type, String key, String value) {
        MapleQuest quest = MapleQuest.getInstance(type);
        final MapleQuestStatus stat = quests.get(quest);
        if (stat == null) {
            MapleQuestStatus qs = new MapleQuestStatus(quest, 1);
            qs.setCustomData(key + "=" + value + ";");
            quests.put(quest, qs);
            updateInfoQuest(type, qs.getCustomData());
            return;
        }
        if (stat.getCustomData() == null) {
            return;
        }
        String[] data = stat.getCustomData().split(";");
        for (String s : data) {
            if (s.startsWith(key + "=")) {
                String newkey = stat.getCustomData().replace(s, key + "=" + value);
                stat.setCustomData(newkey);
                updateInfoQuest(type, stat.getCustomData());
                return;
            }
        }
        stat.setCustomData(stat.getCustomData() + key + "=" + value + ";");
        updateInfoQuest(type, stat.getCustomData());
    }

    public long getKeyValue(int type, String key) {
        MapleQuest quest = MapleQuest.getInstance(type);
        MapleQuestStatus q = quests.get(quest);
        if (q == null) {
            return -1;
        }
        if (q.getCustomData() == null) {
            return -1;
        }
        String[] data = q.getCustomData().split(";");
        for (String s : data) {
            if (s.startsWith(key + "=")) {
                String newkey = s.replace(key + "=", "");
                String newkey2 = newkey.replace(";", "");
                long dd = Long.valueOf(newkey2);
                return dd;
            }
        }
        return -1; // 誘몄〈?ъ떆 -1
    }

    public Pair<Integer, Integer> getEndSay() {
        return say;
    }

    public void setEndSay(Pair<Integer, Integer> say) {
        this.say = say;
    }

    public void setEndFlow(int flow) {
        if (flow == 0) {
            this.flow = 0;
        } else {
            this.flow += flow;
        }
    }

    public int getEndFlow() {
        return flow;
    }

    public void setEndStatus(boolean status) {
        this.status = status;
    }

    public boolean getEndStatus() {
        return status;
    }

    public String getNum(long dd) {
        String df = new DecimalFormat("###,###,###,###,###,###").format(dd);
        return df;
    }

    public void addACDamage(long damage) {
        if (getKeyValue(468432, "damage") == -1) {
            setKeyValue(468432, "damage", "0");
        }
        setKeyValue(468432, "damage", getKeyValue(468432, "damage") + damage + "");
    }

    public void setACDamage(long damage) {
        setKeyValue(468432, "damage", damage + "");
    }

    public long getACDamage() {
        if (getKeyValue(468432, "damage") == -1) {
            setKeyValue(468432, "damage", "0");
        }
        return getKeyValue(468432, "damage");
    }

    public long getStartChairTime() {
        return startChairTime;
    }

    public void gainsSartChairTime(long a) {
        startChairTime += a;
    }

    public void setNowChairCount(byte time) {
        nowChairCount = time;
    }

    public void setStartChairTime(long time) {
        startChairTime = time;
    }

    public void gainNowChairCount(byte a) {
        nowChairCount += a;
    }

    public byte getNowChairCount() {
        return nowChairCount;
    }

    public void setHealBuffDuration(long duration) {
        this.healBuffDuration = duration;
    }

    public long getHealBuffDuration() {
        return healBuffDuration;
    }

    public void updateHealBuff() {
        if (this.getBuffedValue(CharacterTemporaryStat.MagicGuard) != null) {
            if ((System.currentTimeMillis() / 1000) > this.getHealBuffDuration() + 4) {
                int v1 = this.getTotalSkillLevel(52001001);
                int v2 = ((this.getStat().getMaxHp() / 100) * v1);
                this.addHP(v2);
                this.setHealBuffDuration((System.currentTimeMillis() / 1000));
            }
        }
    }

    public void setHayatoBuff() {
        if (this.getBuffSource(CharacterTemporaryStat.ArcaneAim) != 60000067) {
            final CharacterTemporaryStat cts = CharacterTemporaryStat.ArcaneAim;
            int ctv = (1 + (this.getStat().wdef / 100));
            if (ctv > 10) {
                ctv = 10;
            }
            int skillID = 60000067;
            int skillLv = this.getTotalSkillLevel(60000067);
            final MapleStatEffect mse = SkillFactory.getSkill(skillID).getEffect(skillLv);
            int duration = Integer.MAX_VALUE;
            this.setTemporaryStat(cts, ctv, mse, skillID, duration);
        }
        if (this.getBuffSource(CharacterTemporaryStat.Stance) != 61000001) {
            final CharacterTemporaryStat cts = CharacterTemporaryStat.Stance;
            int skillID = 61000001;
            int skillLv = this.getTotalSkillLevel(61000001);
            final MapleStatEffect mse = SkillFactory.getSkill(skillID).getEffect(skillLv);
            int ctv = mse.getProb();
            int duration = Integer.MAX_VALUE;
            this.setTemporaryStat(cts, ctv, mse, skillID, duration);
        }
    }

    public void setHakuBuff() {
        if (this.getBuffedValue(CharacterTemporaryStat.PvPScoreBonus) != null) {
            int nType = this.getBuffSource(CharacterTemporaryStat.PvPScoreBonus);
            int nBuff = (nType == 62120011 ? 4 : 0);
            int cRand = Randomizer.rand(8001080, 8001083);
            int pBuff = (cRand + nBuff);
            Skill nSkill = SkillFactory.getSkill(pBuff);
            boolean canUsed = true;
            if (this.getBuffedValue(CharacterTemporaryStat.WeaknessMdamage) != null) {
                canUsed = false;
            }
            if (this.getBuffedValue(CharacterTemporaryStat.PowerGuard) != null) {
                canUsed = false;
            }
            if (this.getBuffedValue(CharacterTemporaryStat.ArcaneAim) != null) {
                canUsed = false;
            }
            if (this.getBuffedValue(CharacterTemporaryStat.DamR) != null) {
                canUsed = false;
            }
            if (canUsed) {
                if (nSkill != null) {
                    int nSLV = 1;
                    MapleStatEffect nEff = nSkill.getEffect(nSLV);
                    if (nEff != null) {
                        int nDuration = 1000 * 10;
                        this.cancelBuffStats(false, CharacterTemporaryStat.SaintSaver);
                        nEff.applyBuffEffect(this, this, false, nDuration);
                        this.getClient().sendPacket(EffectPacket.showOwnBuffEffect(pBuff, 1, this.getLevel(), 1, (byte) 1));
                        this.getMap().broadcastMessage(CField.playSound("kanna/" + pBuff));
                    }
                }
            }
        }
    }

    public void doStartChair() {
        if (getMapId() == 910000000 && client.getChannel() == 1) {
            int startClock = 100;
            if (getChair() == 0) {
                setNowChairCount((byte) 0);
            }
            if (getChair() > 0) {
                long now_time = System.currentTimeMillis();
                if (getStartChairTime() == 0) {
                    setStartChairTime(now_time);
                }
                StringBuilder sb;
                int width = 180;
                sb = new StringBuilder("#e보상 획득 : #b" + (startClock - getNowChairCount()) + "");
                if ((getStartChairTime() + startClock) < now_time) {
                    if (getNowChairCount() < (startClock - 1)) {
                        setStartChairTime(now_time);
                        gainNowChairCount((byte) 1);
                        sb.append("#k초#n");
                    } else {
                        setStartChairTime(now_time);
                        setNowChairCount((byte) 0);
                        int nItem = 0;
                        int aRand = Randomizer.rand(0, 100);
                        int tRand = Randomizer.rand(0, 20);
                        if (aRand < 2) {
                            nItem = (tRand < 1 ? 2530000 : tRand < 5 ? 4310115 : 4310114);
                        } else {
                            if (aRand < 10) {
                                nItem = 2431256;
                            }
                        }
                        if (nItem != 0) {
                            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                            sb = new StringBuilder("#e보상 획득 : #b" + ii.getName(nItem) + "#k#n");
                            width = 180;
                            client.sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "자유 시장 입구 보상이 지급 되었습니다. (" + ii.getName(nItem) + " 1개)"));
                            MapleInventoryManipulator.addById(client, nItem, (short) 1, "");
                        } else {
                            int aMeso = Randomizer.rand(5000, 10000);
                            sb = new StringBuilder("#e보상 획득 : #b" + this.getNum(aMeso) + " 메소#k#n");
                            width = 180;
                            client.sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "자유 시장 입구 보상이 지급 되었습니다. (" + this.getNum(aMeso) + " 메소)"));
                            this.gainMeso(aMeso, true);
                        }
                    }
                }
                getClient().sendPacket(CUserLocal.balloonMsg(sb.toString(), width, 1));
            }
        }
    }

    public final String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String time = sdf.format(Calendar.getInstance().getTime());
        return time;
    }

    private Timestamp premium;

    public void setPremium(long time) {
        this.premium = new java.sql.Timestamp(System.currentTimeMillis() + 1000 * 60 * time);
    }

    public Timestamp getPremium() {
        return premium;
    }

    public long getPremiumTime() {
        return premium.getTime();
    }

    public final MapleLieDetector getAntiMacro() {
        return antiMacro;
    }

    public void setSecen(boolean check) {
        this.scene = check;
    }

    public boolean getSecen() {
        return scene;
    }

    public MapleCharacter getWarpExpeditionID() {
        MapleParty part = getParty();
        if (part != null && part.getExpeditionId() > 0) {
            final MapleExpedition expedition = World.Party.getExped(part.getExpeditionId());
            if (expedition != null) {
                for (int i : expedition.getParties()) {
                    final MapleParty par = World.Party.getParty(i);
                    if (par != null) {
                        for (MaplePartyCharacter pUser : par.getMembers()) {
                            if (pUser.isOnline()) {
                                int ch = World.Find.findChannel(pUser.getId());
                                if (ch > 0) {
                                    final MapleCharacter user = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(pUser.getId());
                                    if (user != null && user.getEventInstance() != null) {
                                        return user;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void addSummonList(MapleSummon summon) {
        summonlist.add(summon);
    }

    public void removeSummonList(MapleSummon summon) {
        summonlist.remove(summon);
    }

    public void clearSummonList() {
        summonlist.clear();
    }

    public List<MapleSummon> getSummonLists() {
        return new ArrayList<MapleSummon>(summonlist);
    }

    public void setPQReward(int PQReward) {
        this.PQReward = PQReward;
    }

    public int getPQReward() {
        return PQReward;
    }

    public void setFamilyBuffExpDuration(long duration) {
        this.familyBuffExpDuration = duration;
    }

    public long getFamilyBuffExpDuration() {
        return familyBuffExpDuration;
    }

    public void setFamilyBuffExpEffect(int value) {
        this.familyBuffExpEffect = value;
    }

    public int getFamilyBuffExpEffect() {
        return familyBuffExpEffect;
    }

    public void setFamilyBuffDropDuration(long duration) {
        this.familyBuffDropDuration = duration;
    }

    public long getFamilyBuffDropDuration() {
        return familyBuffDropDuration;
    }

    public void setFamilyBuffDropEffect(int value) {
        this.familyBuffDropEffect = value;
    }

    public int getFamilyBuffDropEffect() {
        return familyBuffDropEffect;
    }

    public List<MapleShopItem> getSpecialItem() {
        return this.shop_special_item;
    }

    public void setFamilyBuffDayByDay(int buffType) {
        int cValue = 20330000;
        String cDate = Calendar.getInstance().get(Calendar.YEAR) % 100 + "/" + StringUtil.getLeftPaddedStr(Calendar.getInstance().get(Calendar.MONTH) + "", '0', 2) + "/" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String buffString = null;
        switch (buffType) {
            case 2: {
                buffString = "dropBuff";
                break;
            }
            case 3: {
                buffString = "expBuff";
                break;
            }
            case 4: {
                buffString = "allBuff";
                break;
            }
        }
        if (buffString != null) {
            this.updateOneInfoQuest(cValue, buffString, "1");
            this.updateOneInfoQuest(cValue, buffString + "_date", cDate);
        }
    }

    public int getFamilyBuffDayByDay(int buffType) {
        int cValue = 20330000;
        String cDate = Calendar.getInstance().get(Calendar.YEAR) % 100 + "/" + StringUtil.getLeftPaddedStr(Calendar.getInstance().get(Calendar.MONTH) + "", '0', 2) + "/" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String buffString = null;
        switch (buffType) {
            case 2: {
                buffString = "dropBuff";
                break;
            }
            case 3: {
                buffString = "expBuff";
                break;
            }
            case 4: {
                buffString = "allBuff";
                break;
            }
        }
        if (buffString != null) {
            if (this.getOneInfoQuest(cValue, buffString).equals("1") && this.getOneInfoQuest(cValue, buffString + "_date").equals(cDate)) {
                return 1;
            }
        }
        return 0;
    }

    public void recalcPartyQuestRank(final int questid) {
        if (MapleQuest.getInstance(questid) == null) {
            return;
        }
        //if (!startPartyQuest(questid)) {
        String newRank = "F";
        String oldRank = getOneInfoQuest(questid, "rank");
        if (oldRank == null) {
            updateOneInfoQuest(questid, "rank", newRank);
        }
        updateOneInfoQuest(questid, "rank", newRank);
        this.dropMessage(5, "-3번");
        /*
            if (oldRank.equals("S")) {
                this.dropMessage(5, "-4번");
                return;
            }
         */
        this.dropMessage(5, "-2번");
        if (oldRank != null) {
            if (oldRank.equals("A")) {
                newRank = "S";
            } else if (oldRank.equals("B")) {
                newRank = "A";
            } else if (oldRank.equals("C")) {
                newRank = "B";
            } else if (oldRank.equals("D")) {
                newRank = "C";
            } else if (oldRank.equals("F")) {
                newRank = "D";
            } else {
                this.dropMessage(5, "-3ㄴㅇㄴㅁㅇ번");
                return;
            }
        }
        this.dropMessage(5, "-3ㄴㅇㄴㅁㅇ번");
        final List<Pair<String, Pair<String, Integer>>> questInfo = MapleQuest.getInstance(questid).getInfoByRank(newRank);
        if (questInfo == null) {
            this.dropMessage(5, "01번");
            return;
        }
        if (questid == 1200 || questid == 1203 || questid == 1204) {
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                this.dropMessage(5, "0번");
                final String val = getOneInfoQuest(questid, q.right.left);
                //dropMessage(6, "val" + val);
                this.dropMessage(5, "1번");
                if (val == null) {
                    this.dropMessage(5, "??dd?");
                    return;
                }
                int vall = 0;
                this.dropMessage(5, "2번");
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }//시간 아이템 갯수 횟수체크순으로 다시 봐보장
                if (q.left.equals("less")) {//시간통과
                    found = vall < q.right.right;
                    //dropMessage(6, "1: " + found + " vall: " + vall + " q.right.right: " + q.right.right);
                } else if (q.left.equals("more")) {
                    found = vall >= q.right.right;//"클리어횟수"
                } else if (q.left.equals("equal")) {
                    found = vall == q.right.right;//"HAVE"
                }
                if (!found) {
                    return;
                }
            }
        } else if (questid == 1201/*커파*/ || questid == 1202/*루파*/ || questid == 1205/*rnj*/ || questid == 1206) {
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                final String val = getOneInfo(questid, q.right.left);
                //dropMessage(6, "val" + val);
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }//시간 아이템 갯수 횟수체크순으로 다시 봐보장
                if (q.left.equals("less")) {//시간통과
                    found = vall < q.right.right;
                    //dropMessage(6, "1: " + found + " vall: " + vall + " q.right.right: " + q.right.right);
                } else if (q.left.equals("more")) {
                    found = vall == q.right.right;//"HAVE"
                    found = vall >= q.right.right;//"클리어횟수"
                } else if (q.left.equals("equal")) {
                }
                if (!found) {
                    return;
                }
            }
        } else if (questid == 1209/*렉스*/) {
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                final String val = getOneInfo(questid, q.right.left);
                //dropMessage(6, "val" + val);
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }//시간 아이템 갯수 횟수체크순으로 다시 봐보장
                if (q.left.equals("less")) {//시간통과
                    found = vall < q.right.right;
                    //dropMessage(6, "1: " + found + " vall: " + vall + " q.right.right: " + q.right.right);
                } else if (q.left.equals("more")) {
                    found = vall >= q.right.right;//"클리어횟수"
                } else if (q.left.equals("equal")) {
                }
                if (!found) {
                    return;
                }
            }
        } else if (questid == 1210/*드래곤라이더*/) {
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                final String val = getOneInfo(questid, q.right.left);
                //dropMessage(6, "val" + val);
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }//시간 아이템 갯수 횟수체크순으로 다시 봐보장
                if (q.left.equals("less")) {//시간통과
                    //dropMessage(6, "1: " + found + " vall: " + vall + " q.right.right: " + q.right.right);
                } else if (q.left.equals("more")) {
                    found = vall >= q.right.right;//"클리어횟수"
                } else if (q.left.equals("equal")) {
                }
                if (!found) {
                    return;
                }
            }
        } else if (questid == 1211/*피라미드*/ || questid == 1212/*임차장*/ || questid == 1213/*무릉도장*/) {
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                final String val = getOneInfo(questid, q.right.left);
                //dropMessage(6, "val" + val);
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }
                if (q.left.equals("less")) {//시간통과
                } else if (q.left.equals("more")) {
                    found = vall >= q.right.right;//"클리어횟수"
                } else if (q.left.equals("equal")) {
                    found = vall == q.right.right;//"해브"
                }
                if (!found) {
                    return;
                }
            }
        } else if (questid == 1303/*안개바다*/) {
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                final String val = getOneInfo(questid, q.right.left);
                //dropMessage(6, "val" + val);
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }
                if (q.left.equals("less")) {//시간통과
                } else if (q.left.equals("more")) {
                    found = vall >= q.right.right;//"클리어횟수"
                } else if (q.left.equals("equal")) {
                    found = vall == q.right.right;//"해브"
                }
                if (!found) {
                    return;
                }
            }
        }
        //perfectly safe
        updateOneInfoQuest(questid, "rank", newRank);
        //}
    }

    public void tryPartyQuest(final int questid) {
        if (MapleQuest.getInstance(questid) == null) {
            return;
        }
        try {
            startPartyQuest(questid);
            pqStartTime = System.currentTimeMillis();
            updateOneInfoQuest(questid, "try", String.valueOf(Integer.parseInt(getOneInfoQuest(questid, "try")) + 1));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("tryPartyQuest error");
        }
    }

    public void endPartyQuest(final int questid) {
        endPartyQuest(questid, 1);
    }

    public void endPartyQuest(final int questid, int result) {
        if (MapleQuest.getInstance(questid) == null) {
            return;
        }
        try {
            startPartyQuest(questid);
            if (isGM() && pqStartTime == 0) {
                pqStartTime = System.currentTimeMillis() - 5;
            }
            if (pqStartTime > 0) {
                final long changeTime = System.currentTimeMillis() - pqStartTime;
                final int mins = (int) (changeTime / 1000 / 60), secs = (int) (changeTime / 1000 % 60);
                //final int mins2 = Integer.parseInt(getOneInfoQuest(questid, "min"));
                //if (mins2 <= 0 || mins < mins2) {
                updateOneInfoQuest(questid, "min", String.valueOf(mins));
                updateOneInfoQuest(questid, "sec", String.valueOf(secs));
                updateOneInfoQuest(questid, "date", FileoutputUtil.CurrentReadable_Date());
                //}
                switch (questid) { // 95 기준으로 한건데 카니발 같이 vs 개념인 애들은 여기다 case 걸어야함
                    case 1300: // 아리안트 ver 95
                    case 1301: // 카니발 ver 95
                    case 1302: // 카니발2 ver 95
                    case 1303: // 안개바다의 유령선 ver 109 << 따로추가했는데 다른파퀘 확인필요
                        int newVic = Integer.parseInt(getOneInfoQuest(questid, "vic"));
                        int newLose = Integer.parseInt(getOneInfoQuest(questid, "lose"));
                        int newDraw = Integer.parseInt(getOneInfoQuest(questid, "draw"));
                        switch (result) {
                            case 1:
                                updateOneInfoQuest(questid, "vic", String.valueOf(++newVic));
                                break;
                            case 2:
                                updateOneInfoQuest(questid, "lose", String.valueOf(++newLose));
                                break;
                            case 3:
                                updateOneInfoQuest(questid, "draw", String.valueOf(++newDraw));
                                break;
                            default:
                                break;
                        }
                        updateOneInfoQuest(questid, "VR", String.valueOf((int) (newVic * 100.0 / (newVic + newLose + newDraw))));
                        break;
                    default:
                        if (getOneInfo(questid, "cmp") == null) {
                            updateOneInfoQuest(questid, "cmp", "0");
                        }
                        int newCmp = Integer.parseInt(getOneInfoQuest(questid, "cmp")) + 1;
                        updateOneInfoQuest(questid, "cmp", String.valueOf(newCmp));
                        updateOneInfoQuest(questid, "CR", String.valueOf((int) Math.ceil((newCmp * 100.0) / Integer.parseInt(getOneInfoQuest(questid, "try")))));
                        break;
                }
                recalcPartyQuestRank(questid);
                pqStartTime = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("endPartyQuest error");
        }
    }

    public int getCmp(int questid) {
        final int newCmp = Integer.parseInt(getOneInfoQuest(questid, "cmp"));
        return newCmp;
    }

    public void havePartyQuest(final int itemId) {
        int questid = 0, index = -1;
        switch (itemId) {
            case 1002798:
                questid = 1200; //henesys
                break;
            case 1072369:
                questid = 1201; //kerning
                break;
            case 1022073:
                questid = 1202; //ludi
                break;
            case 1082232:
                questid = 1203; //orbis
                break;
            case 1002571:
            case 1002572:
            case 1002573:
            case 1002574:
                questid = 1204; //herbtown
                index = itemId - 1002571;
                break;
            case 1102226:
                questid = 1303; //ghost
                break;
            case 1102227:
                questid = 1303; //ghost
                index = 1;
                break;
            case 1122010:
                questid = 1205; //magatia
                break;
            case 1032061:
            case 1032060:
                questid = 1206; //ellin
                index = itemId - 1032060;
                break;
            case 3010018:
                questid = 1300; //ariant
                break;
            case 1122007:
                questid = 1301; //carnival
                break;
            case 1122058:
                questid = 1302; //carnival2
                break;
            default:
                return;
        }
        if (MapleQuest.getInstance(questid) == null) {
            return;
        }
        startPartyQuest(questid);
        updateOneInfoQuest(questid, "have" + (index == -1 ? "" : index), "1");
        if (itemId / 10000 == 521) {
            stats.recalcLocalStats(this);
        }
        if (itemId / 10000 == 410) {
            stats.recalcLocalStats(this);
        }
    }

    public void setSoulEnchantEffect() {
        if (this.getOneInfoQuest(10000000, "soul_enchant_effect").equals("")) {
            this.updateOneInfoQuest(10000000, "soul_enchant_effect", "1");
        }
        if (this.getOneInfoQuest(10000000, "soul_enchant_effect").equals("0")) {
            if (this.getBuffedValue(CharacterTemporaryStat.EventRate) != null) {
                this.cancelBuffStats(false, CharacterTemporaryStat.EventRate);
                this.cancelBuffStats(false, CharacterTemporaryStat.SaintSaver);
            }
        } else {
            final Item weapon = this.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
            if (weapon == null) {
                if (this.getBuffedValue(CharacterTemporaryStat.EventRate) != null) {
                    this.cancelBuffStats(false, CharacterTemporaryStat.EventRate);
                    this.cancelBuffStats(false, CharacterTemporaryStat.SaintSaver);
                }
            } else {
                final String owner = weapon.getOwner();
                if (owner == null) {
                    if (this.getBuffedValue(CharacterTemporaryStat.EventRate) != null) {
                        this.cancelBuffStats(false, CharacterTemporaryStat.EventRate);
                        this.cancelBuffStats(false, CharacterTemporaryStat.SaintSaver);
                    }
                } else {
                    if (owner.equals("")) {
                        if (this.getBuffedValue(CharacterTemporaryStat.EventRate) != null) {
                            this.cancelBuffStats(false, CharacterTemporaryStat.EventRate);
                            this.cancelBuffStats(false, CharacterTemporaryStat.SaintSaver);
                        }
                    }
                    int buff = 0;
                    switch (owner.toString()) {
                        case "시그너스": {
                            buff = 8001060;
                            break;
                        }
                        case "발록": {
                            buff = 8001061;
                            break;
                        }
                        case "반 레온": {
                            buff = 8001062;
                            break;
                        }
                        case "핑크빈": {
                            buff = 8001063;
                            break;
                        }
                        case "아카이럼": {
                            buff = 8001064;
                            break;
                        }
                        case "피아누스": {
                            buff = 8001065;
                            break;
                        }
                        case "힐라": {
                            buff = 8001066;
                            break;
                        }
                        case "자쿰": {
                            buff = 8001067;
                            break;
                        }
                        case "블랙 슬라임": {
                            buff = 8001068;
                            break;
                        }
                        case "매그너스": {
                            buff = 8001069;
                            break;
                        }
                        case "무르무르": {
                            buff = 8001070;
                            break;
                        }
                        case "피에르": {
                            buff = 8001076;
                            break;
                        }
                        case "반반": {
                            buff = 8001077;
                            break;
                        }
                        case "블러디 퀸": {
                            buff = 8001078;
                            break;
                        }
                        case "벨룸": {
                            buff = 8001079;
                            break;
                        }
                        case "스우": {
                            buff = 8001680;
                            break;
                        }
                        case "교도관 아니": {
                            buff = 8001682;
                            break;
                        }
                        case "드래곤라이더": {
                            buff = 8001683;
                            break;
                        }
                        case "락 스피릿": {
                            buff = 8001684;
                            break;
                        }
                        case "무공": {
                            buff = 8001685;
                            break;
                        }
                        case "데미안": {
                            buff = 8001686;
                            break;
                        }
                        case "루시드": {
                            buff = 8001688;
                            break;
                        }
                        case "파풀라투스": {
                            buff = 8001690;
                            break;
                        }
                        case "윌": {
                            buff = 8001691;
                            break;
                        }
                        case "진 힐라": {
                            buff = 8001693;
                            break;
                        }
                        case "듄켈": {
                            buff = 8001695;
                            break;
                        }
                    }
                    if (buff > 0) {
                        if (this.getBuffedValue(CharacterTemporaryStat.EventRate) != null) {
                            if (this.getBuffSource(CharacterTemporaryStat.EventRate) != buff) {
                                this.cancelBuffStats(false, CharacterTemporaryStat.EventRate);
                                this.cancelBuffStats(false, CharacterTemporaryStat.SaintSaver);
                            }
                        }
                        if (this.getBuffedValue(CharacterTemporaryStat.EventRate) == null) {
                            final MapleStatEffect ms = SkillFactory.getSkill(buff).getEffect(1);
                            ms.applyTo(this);
                        }
                    } else {
                        if (this.getBuffedValue(CharacterTemporaryStat.EventRate) != null) {
                            this.cancelBuffStats(false, CharacterTemporaryStat.EventRate);
                            this.cancelBuffStats(false, CharacterTemporaryStat.SaintSaver);
                        }
                    }
                }
            }
        }
    }

    public void resetSoulEnchantSkill() {
        for (int i = 8000011; i < 8000015; i++) {
            final Skill skill = SkillFactory.getSkill(i);
            this.changeSingleSkillLevel(skill, 0, (byte) 0);
        }
    }

    public void setSoulEnchantSkill() {
        final Item weapon = this.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
        if (weapon == null) {
            this.resetSoulEnchantSkill();
        } else {
            final String owner = weapon.getOwner();
            if (owner == null) {
                this.resetSoulEnchantSkill();
            } else {
                this.resetSoulEnchantSkill();
                int sk = 0;
                int lv = 0;
                switch (owner.toString()) {
                    case "시그너스": {
                        sk = 8000014;
                        lv = 1;
                        break;
                    }
                    case "발록": {
                        sk = 8000012;
                        lv = 1;
                        break;
                    }
                    case "반 레온": {
                        sk = 8000012;
                        lv = 5;
                        break;
                    }
                    case "핑크빈": {
                        sk = 8000014;
                        lv = 1;
                        break;
                    }
                    case "아카이럼": {
                        sk = 8000012;
                        lv = 5;
                        break;
                    }
                    case "피아누스": {
                        sk = 8000011;
                        lv = 2;
                        break;
                    }
                    case "힐라": {
                        sk = 8000012;
                        lv = 5;
                        break;
                    }
                    case "자쿰": {
                        sk = 8000012;
                        lv = 2;
                        break;
                    }
                    case "블랙 슬라임": {
                        sk = 8000011;
                        lv = 1;
                        break;
                    }
                    case "매그너스": {
                        sk = 8000014;
                        lv = 1;
                        break;
                    }
                    case "무르무르": {
                        sk = 8000011;
                        lv = 1;
                        break;
                    }
                    case "피에르": {
                        sk = 8000014;
                        lv = 2;
                        break;
                    }
                    case "반반": {
                        sk = 8000014;
                        lv = 2;
                        break;
                    }
                    case "블러디 퀸": {
                        sk = 8000014;
                        lv = 2;
                        break;
                    }
                    case "벨룸": {
                        sk = 8000014;
                        lv = 2;
                        break;
                    }
                    case "스우": {
                        sk = 8000014;
                        lv = 3;
                        break;
                    }
                    case "교도관 아니": {
                        sk = 8000011;
                        lv = 2;
                        break;
                    }
                    case "드래곤라이더": {
                        sk = 8000011;
                        lv = 3;
                        break;
                    }
                    case "락 스피릿": {
                        sk = 8000011;
                        lv = 1;
                        break;
                    }
                    case "무공": {
                        sk = 8000013;
                        lv = 2;
                        break;
                    }
                    case "데미안": {
                        sk = 8000014;
                        lv = 3;
                        break;
                    }
                    case "루시드": {
                        sk = 8000014;
                        lv = 4;
                        break;
                    }
                    case "파풀라투스": {
                        sk = 8000011;
                        lv = 4;
                        break;
                    }
                    case "윌": {
                        sk = 8000014;
                        lv = 4;
                        break;
                    }
                    case "진 힐라": {
                        sk = 8000014;
                        lv = 5;
                        break;
                    }
                    case "듄켈": {
                        sk = 8000014;
                        lv = 5;
                        break;
                    }
                }
                if (sk > 0) {
                    this.changeSingleSkillLevel(SkillFactory.getSkill(sk), (byte) lv, (byte) lv);
                }
            }
        }
    }

    public void setTemporaryStat(CharacterTemporaryStat cts, int ctv, MapleStatEffect mse, int skillID, int duration) {
        EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
        if (skillID == 2311012) {
            stat.put(CharacterTemporaryStat.SaintSaver, 1);
        }
        stat.put(cts, ctv);
        if (skillID != 80001000) {
            this.getClient().sendPacket(BuffPacket.giveBuff(skillID, duration, stat, mse));
            map.broadcastMessage(this, BuffPacket.giveForeignBuff(this.getId(), stat, mse), false);
        } else {
            this.getClient().sendPacket(BuffPacket.giveMount(ctv, skillID, stat));
            map.broadcastMessage(this, BuffPacket.showMonsterRiding(this.getId(), stat, ctv, 80001000), false);
        }
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(this, mse, starttime, stat);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, duration);
        this.registerEffect(mse, starttime, schedule, stat, false, duration, this.getId());
    }

    public int setPercentDamage(int mobID) {
        switch (mobID) {
            case 8880165:
            case 8880168:
            case 8880169:
            case 8880175:
            case 8880178:
            case 8880179: { // 악몽의 나비
                return 10;
            }
            case 8880160:
            case 8880161:
            case 8880170:
            case 8880171:
            case 8880180:
            case 8880181:
            case 8880182:
            case 8880183:
            case 8880186:
            case 8880187:
            case 8880188:
            case 8880189: { // 악몽의 골렘
                return 20;
            }
            case 8920004: { // 하트 폭탄
                return 10;
            }
        }
        return 0;
    }

    public long getTraningDamage() {
        return traningDamage;
    }

    public void setTraningDamage(long traningDamage) {
        this.traningDamage = traningDamage;
    }

    public void setTakeDamageDelay(long duration) {
        this.takeDamageDelay = duration;
    }

    public long getTakeDamageDelay() {
        return takeDamageDelay;
    }

    public void setPierreHat(int pierreHat) {
        this.pierreHat = pierreHat;
    }

    public int getPierreHat() {
        return pierreHat;
    }

    public void setPapulatusClock() {
        MapleMonster mPapulatus = this.getMap().getMonsterById(8500001);
        MapleMonster fPapulatus = this.getMap().getMonsterById(8500002);
        if (this.getMap().getMonsterById(8500002) != null) {
            mPapulatus = fPapulatus;
        }
        if (mPapulatus != null) {
            if (mPapulatus.getPapulatusClock() > -1) {
                if ((System.currentTimeMillis() / 1000) > (mPapulatus.getPapulatusBindDuration() + 19)) {
                    byte papulatusClock = (byte) (mPapulatus.getPapulatusClock() + 1);
                    mPapulatus.setPapulatusClock(papulatusClock);
                    map.broadcastMessage(CField.achievementRatio(papulatusClock));
                } else {
                    if (this != null) {
                        int reduceHP = ((this.getStat().getMaxHp() / 100) * 10);
                        this.getClient().sendPacket(CField.OnDamageByUser(this.getId(), 1, reduceHP));
                        map.broadcastMessage(CField.OnDamageByUser(this.getId(), 1, reduceHP));
                    }
                }
            }
            if (mPapulatus.getPapulatusClock() > 99) {
                mPapulatus.setPapulatusClock((byte) 0);
                mPapulatus.setPapulatusBindDuration(System.currentTimeMillis() / 1000);
                map.broadcastMessage(CField.playSound("Papulatus/Alarm"));
                map.floatNotice("파풀라투스의 알람이 울립니다! 체력이 지속적으로 감소하니 조심하세요!", 5120177, false);
                for (int i = -900; i < 1000; i += 200) {
                    MapleMonster a1 = MapleLifeFactory.getMonster(8500034);
                    MapleMonster a2 = MapleLifeFactory.getMonster(8500035);
                    this.getMap().spawnMonsterOnGroundBelow(a1, new Point(i, 179));
                    this.getMap().spawnMonsterOnGroundBelow(a2, new Point(i, 179));
                }
                Map<MonsterTemporaryStat, Integer> mts = new EnumMap<>(MonsterTemporaryStat.class);
                List<Integer> reflection = new LinkedList<>();
                reflection.add(999999);
                mts.put(MonsterTemporaryStat.ExchangeAttack, 1);
                MobSkill ms = MobSkillFactory.getMobSkill(200, 221);
                mPapulatus.applyMonsterBuff(mts, 200, 1000, ms, reflection, 0);
            }
        } else {
            map.broadcastMessage(CField.achievementRatio(0));
        }
    }

    public void removePierreHat() {
        if (this.getPierreHat() > 0) {
            final MapleInventory iv = this.getInventory(MapleInventoryType.EQUIPPED);
            Equip equip = (Equip) iv.getItem((byte) -101);
            if (equip == null) {
                this.getClient().sendPacket(CWvsContext.InventoryPacket.removearanpol((byte) -101));
            } else {
                this.getClient().sendPacket(CWvsContext.InventoryPacket.updateSpecialItemUse_(equip, (byte) 1, this));
            }
            this.removeAll(1003727, false);
            this.removeAll(1003728, false);
            this.setPierreHat(0);
        }
    }

    public void setBanBanClock() {
        final MapleMap map_105200110 = ChannelServer.getInstance(this.getClient().getChannel()).getMapFactory().getMap(105200110);
        final MapleMap map_105200120 = ChannelServer.getInstance(this.getClient().getChannel()).getMapFactory().getMap(105200120);
        final MapleMonster mob_105200110 = map_105200110.getMonsterById(8910000);
        final MapleMonster mob_105200120 = map_105200120.getMonsterById(8910001);
        if (mob_105200110 != null) {
            if (mob_105200110.getBanbanClock() > -1) {
                byte banbanClock = (byte) (mob_105200110.getBanbanClock() + 1);
                mob_105200110.setBanbanClock(banbanClock);
                if (mob_105200110.getBanbanClock() > 99) {
                    if (mob_105200120 != null) {
                        for (MapleCharacter user_105200110 : map_105200110.getAllCharactersThreadsafe()) {
                            user_105200110.getClient().sendPacket(CField.showEffect("ScreenAttack/banban"));
                        }
                        for (MapleCharacter user_105200120 : map_105200120.getAllCharactersThreadsafe()) {
                            user_105200120.getClient().sendPacket(CField.showEffect("ScreenAttack/banban"));
                        }
                        if (mob_105200110.isBuffed(MonsterTemporaryStat.ExchangeAttack)) {
                            mob_105200110.cancelStatus(MonsterTemporaryStat.ExchangeAttack);
                        }
                        mob_105200110.setBanbanClock((byte) -1);
                    }
                }
            }
        }
    }

    public void setBellumBreath() {
        final MapleMonster mob_105200410 = this.getMap().getMonsterById(8930000);
        if (mob_105200410 != null) {
            if (mob_105200410.isBuffed(MonsterTemporaryStat.ExchangeAttack)) {
                int bBreath = mob_105200410.getBellumBreath();
                if (bBreath > -1) {
                    if (this.isAlive()) {
                        if (bBreath == 0) {
                            if (this.getTruePosition().x > -900) {
                                this.getClient().sendPacket(CField.OnDamageByUser(this.getId(), 15, 99999));
                                this.getMap().broadcastMessage(CField.OnDamageByUser(this.getId(), 15, 99999));
                            }
                        } else {
                            if (this.getTruePosition().x < 100) {
                                this.getClient().sendPacket(CField.OnDamageByUser(this.getId(), 15, 99999));
                                this.getMap().broadcastMessage(CField.OnDamageByUser(this.getId(), 15, 99999));
                            }
                        }
                    }
                }
            }
        }
    }

    public void setDeathCount(int v1) {
        this.deathCount = v1;
        this.getClient().sendPacket(CMonsterCarnival.updateScore((v1 + 1), 0));
    }

    public int getDeathCount() {
        return deathCount;
    }

    public final void handleClearByMonster(int mobID) {
        int nQRecord = 30000000;
        String nDate = Calendar.getInstance().get(Calendar.YEAR) % 100 + "/" + StringUtil.getLeftPaddedStr(Calendar.getInstance().get(Calendar.MONTH) + "", '0', 2) + "/" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int pCount = 1;
        String nType = "";
        switch (mobID) {
            case 8800002:   // 자쿰
            case 8800102: { // 카오스 자쿰
                pCount = 3;
                nType = "zakum";
                break;
            }
            case 8810018:   // 혼테일
            case 8810122: { // 카오스 혼테일
                pCount = 3;
                nType = "horntail";
                break;
            }
            case 8820001:   // 핑크빈
            case 8820101: { // 카오스 핑크빈
                pCount = 3;
                nType = "pinkbean";
                break;
            }
            case 8840000: { // 반 레온
                pCount = 3;
                nType = "vonleon";
                break;
            }
            case 8850011: { // 시그너스
                pCount = 3;
                nType = "cygnus";
                break;
            }
            case 8860000: { // 아카이럼
                pCount = 3;
                nType = "akayrum";
                break;
            }
            case 8870000: { // 힐라
                pCount = 3;
                nType = "hillah";
                break;
            }
            case 8880000: { // 매그너스
                pCount = 3;
                nType = "magnus";
                break;
            }
            case 8900000: { // 피에르
                pCount = 3;
                nType = "pierre";
                break;
            }
            case 8910000: { // 반반
                pCount = 3;
                nType = "banban";
                break;
            }
            case 8920000: { // 블러디 퀸
                pCount = 3;
                nType = "blodyqueen";
                break;
            }
            case 8930000: { // 벨룸
                pCount = 3;
                nType = "bellum";
                break;
            }
            case 8240099: { // 스우
                nType = "lotus";
                break;
            }
            case 8880101: { // 데미안
                nType = "demian";
                break;
            }
            case 8880156: { // 루시드
                nType = "lucid";
                break;
            }
            case 8880302: { // 윌
                nType = "will";
                break;
            }
            case 8880405: { // 진 힐라
                nType = "jinhillah";
                break;
            }
            case 8645009: { // 듄켈
                nType = "dunkel";
                break;
            }
            case 8880518: { // 검은 마법사
                nType = "blackmage";
                break;
            }
            case 9421586: { // 모리 란 마루
                nType = "ranmaru";
                break;
            }
            case 9450066: { // 노히메
                nType = "nohime";
                break;
            }
            case 9601511: { // 아케치 미츠히데
                nType = "mitsuhide";
                break;
            }
            case 9390600: { // 골럭스
                nType = "gollux";
                break;
            }
            case 8880600: { // 선택받은 세렌
                nType = "seren";
                break;
            }
            case 8880808: { // 감시자 칼로스
                nType = "kalos";
                break;
            }
            case 8880845: { // 카링
                nType = "karing";
                break;
            }
        }
        if (!nType.equals("")) {
            if (this.getOneInfoQuest(nQRecord, nType + "_enter") == null) {
                this.updateOneInfoQuest(nQRecord, nType + "_enter", "0");
            }
            if (this.getOneInfoQuest(nQRecord, nType + "_enter").equals("")) {
                this.updateOneInfoQuest(nQRecord, nType + "_enter", "0");
            }
            if (this.getOneInfoQuest(nQRecord, nType + "_date").equals(nDate) == false) {
                this.updateOneInfoQuest(nQRecord, nType + "_enter", "1");
                this.updateOneInfoQuest(nQRecord, nType + "_date", nDate);
            } else {
                int nCount = Integer.parseInt(this.getOneInfoQuest(nQRecord, nType + "_enter"));
                this.updateOneInfoQuest(nQRecord, nType + "_enter", (nCount + 1) + "");
            }
            this.getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, MapleLifeFactory.getMonster(mobID).getName() + " 원정대 클리어 횟수 : " + this.getOneInfoQuest(nQRecord, nType + "_enter") + " / " + pCount + ""));
            if (this.getParty() != null) {
                if (this.getParty().getLeader().getId() == this.getId()) {
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[" + ServerConstants.server_Name_Source + "] " + MapleLifeFactory.getMonster(mobID).getName() + "(이)가 격파되어 검은 천국의 야망이 흩어집니다."));
                    String nUser = "(Lv." + this.getLevel() + ") " + this.getName();
                    if (map.getCharactersSize() > 1) {
                        for (MapleCharacter tUser : map.getAllCharactersThreadsafe()) {
                            if (tUser != null) {
                                if (tUser != this) {
                                    nUser += ", (Lv." + tUser.getLevel() + ") " + tUser.getName();
                                }
                            }
                        }
                    }
                    World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "- 격파자 : " + nUser));
                }
            }
        }
    }

    public void handleResetSkillByChangeJob() {
        Iterator<Skill> it = SkillFactory.getAllSkills().iterator();
        while (it.hasNext()) {
            final Skill nSkill = it.next();
            if (nSkill != null) {
                if (!nSkill.isBeginnerSkill() && (nSkill.getId() / 10000) != 800) {
                    changeSingleSkillLevel(nSkill, 0, (byte) 0);
                }
            }
        }
    }

    public void handleSkillMasterByChangeJob(int nJob) {
        Iterator<Integer> it = SkillFactory.getSkillsByJob(nJob).iterator();
        while (it.hasNext()) {
            final int tSkill = it.next();
            final Skill nSkill = SkillFactory.getSkill(tSkill);
            if (nSkill != null) {
                if (!nSkill.isBeginnerSkill() && (nSkill.getId() / 10000) != 800) {
                    changeSingleSkillLevel(nSkill, nSkill.getMaxLevel(), (byte) nSkill.getMaxLevel());
                }
            }
        }
    }

    public void handleSymbolStat() {
        int v1 = 0;
        int v2 = 0;
        int v3 = 0;
        int v4 = 0;
        if (this.haveItem(3980000)) {
            v1++;
        }
        if (this.haveItem(3980006)) {
            v1++;
        }
        if (this.haveItem(3980012)) {
            v1++;
        }
        if (this.haveItem(3980001)) {
            v2++;
        }
        if (this.haveItem(3980007)) {
            v2++;
        }
        if (this.haveItem(3980013)) {
            v2++;
        }
        if (this.haveItem(3980002)) {
            v3++;
        }
        if (this.haveItem(3980008)) {
            v3++;
        }
        if (this.haveItem(3980014)) {
            v3++;
        }
        if (this.haveItem(3980003)) {
            v4++;
        }
        if (this.haveItem(3980009)) {
            v4++;
        }
        if (this.haveItem(3980015)) {
            v4++;
        }
        final Skill a1 = SkillFactory.getSkill(8000092);
        final Skill a2 = SkillFactory.getSkill(8000093);
        final Skill a3 = SkillFactory.getSkill(8000094);
        final Skill a4 = SkillFactory.getSkill(8000095);
        if (a1 != null) {
            this.changeSingleSkillLevel(a1, v1, (byte) v1);
        }
        if (a2 != null) {
            this.changeSingleSkillLevel(a2, v1, (byte) v1);
        }
        if (a3 != null) {
            this.changeSingleSkillLevel(a3, v1, (byte) v1);
        }
        if (a4 != null) {
            this.changeSingleSkillLevel(a4, v1, (byte) v1);
        }
    }

    public final String handleTimeLimit(String nType) {
        int nQRecord = 30000000;
        if (this.getOneInfoQuest(nQRecord, nType + "_time") == null) {
            this.updateOneInfoQuest(nQRecord, nType + "_time", "0");
        }
        if (this.getOneInfoQuest(nQRecord, nType + "_time").equals("")) {
            this.updateOneInfoQuest(nQRecord, nType + "_time", "0");
        }
        String nSay = "";
        long nCurrent = System.currentTimeMillis();
        long nEnd = Long.parseLong(this.getOneInfoQuest(nQRecord, nType + "_time"));
        long pSeconds = ((nEnd - nCurrent) / 1000);
        long pSecs = (pSeconds % 60);
        long pMinutes = (pSeconds / 60);
        long pMins = (pMinutes % 60);
        long pHrs = (pMinutes / 60);
        long pHours = (pHrs % 24);
        if (pHours > 0) {
            boolean mins = pMins > 0;
            nSay += pHours + "";
            nSay += "시간 ";
            if (mins) {
                boolean secs = pSecs > 0;
                nSay += pMins + "";
                nSay += "분 ";
                if (secs) {
                    nSay += pSecs + "";
                    nSay += "초";
                }
            }
        } else if (pMinutes > 0) {
            boolean secs = pSecs > 0;
            nSay += pMinutes + "";
            nSay += "분 ";
            if (secs) {
                nSay += pSecs + "";
                nSay += "초";
            }
        } else if (pSeconds > 0) {
            nSay += pSeconds + "";
            nSay += "초";
        } else {
            nSay += "";
        }
        return nSay;
    }
}
