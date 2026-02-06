package server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import constants.GameConstants;
import client.MapleCharacter;
import client.MapleTrait.MapleTraitType;
import client.inventory.MapleInventoryType;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.WeakHashMap;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import provider.MapleDataType;
import server.StructSetItem.SetItem;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import tools.Pair;
import tools.Triple;
import tools.packet.CWvsContext;

public class MapleItemInformationProvider {

    private final static MapleItemInformationProvider instance = new MapleItemInformationProvider();
    protected final MapleDataProvider chrData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Character.wz"));
    protected final MapleDataProvider etcData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));
    protected final MapleDataProvider itemData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Item.wz"));
    protected final MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
    protected final Map<Integer, ItemInformation> dataCache = new HashMap<Integer, ItemInformation>();
    protected final Map<String, List<Triple<String, Point, Point>>> afterImage = new HashMap<String, List<Triple<String, Point, Point>>>();
    protected final Map<Integer, List<StructItemOption>> potentialCache = new HashMap<Integer, List<StructItemOption>>();
    protected final Map<Integer, Map<Integer, StructItemOption>> socketCache = new WeakHashMap<>(); // Grade, (id, data) 레지존참고
    protected final Map<Integer, MapleStatEffect> itemEffects = new HashMap<Integer, MapleStatEffect>();
    protected final Map<Integer, MapleStatEffect> itemReward = new HashMap<Integer, MapleStatEffect>();
    protected final Map<Integer, MapleStatEffect> itemEffectsEx = new HashMap<Integer, MapleStatEffect>();
    protected final Map<Integer, Integer> mobIds = new HashMap<Integer, Integer>();
    protected final Map<Integer, Pair<Integer, Integer>> potLife = new HashMap<Integer, Pair<Integer, Integer>>(); //itemid to lifeid, levels
    // protected final Map<Integer, Pair<List<Integer>, List<Integer>>> androids = new HashMap<Integer, Pair<List<Integer>, List<Integer>>>();
    protected final Map<Integer, Triple<List<Integer>, List<Integer>, List<Integer>>> androids = new HashMap<>();
    protected final Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> monsterBookSets = new HashMap<Integer, Triple<Integer, List<Integer>, List<Integer>>>();
    protected final Map<Integer, StructSetItem> setItems = new HashMap<Integer, StructSetItem>();
    protected Map<Integer, Integer> scriptedItemCache = new HashMap<Integer, Integer>();
    protected Map<Integer, String> scriptedItemScriptCache = new HashMap<Integer, String>();
    protected List<Integer> itemIdCache = new ArrayList<Integer>();
    protected Map<Integer, List<Integer>> hairCache = new HashMap<>();
    protected Map<Integer, List<Integer>> faceCache = new HashMap<>();

    public int cSTR = 0, cDEX = 0, cINT = 0, cLUK = 0, cPAD = 0, cMAD = 0, cWDEF = 0, cMDEF = 0, cACC = 0, cEVA = 0, cHP = 0, cMP = 0, cSPEED = 0, cJUMP = 0;

    protected final List<Pair<Integer, String>> itemNameCache = new ArrayList<Pair<Integer, String>>();

    public void runEtc() {
        if (!setItems.isEmpty() || !potentialCache.isEmpty() || !socketCache.isEmpty()) {
            return;
        }
        final MapleData setsData = etcData.getData("SetItemInfo.img");
        StructSetItem itemz;
        SetItem itez;
        for (MapleData dat : setsData) {
            itemz = new StructSetItem();
            itemz.setItemID = Integer.parseInt(dat.getName());
            itemz.completeCount = (byte) MapleDataTool.getIntConvert("completeCount", dat, 0);
            for (MapleData level : dat.getChildByPath("ItemID")) {
                if (level.getType() != MapleDataType.INT) {
                    for (MapleData leve : level) {
                        if (!leve.getName().equals("representName") && !leve.getName().equals("typeName")) {
                            itemz.itemIDs.add(MapleDataTool.getInt(leve));
                        }
                    }
                } else {
                    itemz.itemIDs.add(MapleDataTool.getInt(level));
                }
            }
            for (MapleData level : dat.getChildByPath("Effect")) {
                itez = new SetItem();
                itez.incPDD = MapleDataTool.getIntConvert("incPDD", level, 0);
                itez.incMDD = MapleDataTool.getIntConvert("incMDD", level, 0);
                itez.incSTR = MapleDataTool.getIntConvert("incSTR", level, 0);
                itez.incDEX = MapleDataTool.getIntConvert("incDEX", level, 0);
                itez.incINT = MapleDataTool.getIntConvert("incINT", level, 0);
                itez.incLUK = MapleDataTool.getIntConvert("incLUK", level, 0);
                itez.incACC = MapleDataTool.getIntConvert("incACC", level, 0);
                itez.incPAD = MapleDataTool.getIntConvert("incPAD", level, 0);
                itez.incMAD = MapleDataTool.getIntConvert("incMAD", level, 0);
                itez.incSpeed = MapleDataTool.getIntConvert("incSpeed", level, 0);
                itez.incMHP = MapleDataTool.getIntConvert("incMHP", level, 0);
                itez.incMMP = MapleDataTool.getIntConvert("incMMP", level, 0);
                itez.incMHPr = MapleDataTool.getIntConvert("incMHPr", level, 0);
                itez.incMMPr = MapleDataTool.getIntConvert("incMMPr", level, 0);
                itez.incAllStat = MapleDataTool.getIntConvert("incAllStat", level, 0);
                itez.option1 = MapleDataTool.getIntConvert("Option/1/option", level, 0);
                itez.option2 = MapleDataTool.getIntConvert("Option/2/option", level, 0);
                itez.option1Level = MapleDataTool.getIntConvert("Option/1/level", level, 0);
                itez.option2Level = MapleDataTool.getIntConvert("Option/2/level", level, 0);
                itemz.items.put(Integer.parseInt(level.getName()), itez);
            }
            setItems.put(itemz.setItemID, itemz);
        }
        StructItemOption item;
        final MapleData potsData = itemData.getData("ItemOption.img");
        List<StructItemOption> items;
        for (MapleData dat : potsData) {
            items = new LinkedList<>();
            for (MapleData potLevel : dat.getChildByPath("level")) {
                item = new StructItemOption();
                item.opID = Integer.parseInt(dat.getName());
                item.optionType = MapleDataTool.getIntConvert("info/optionType", dat, 0);
                item.reqLevel = MapleDataTool.getIntConvert("info/reqLevel", dat, 0);
                item.opString = MapleDataTool.getString("info/string", dat, "");
                for (final String i : StructItemOption.types) {
                    if (i.equals("face")) {
                        item.face = MapleDataTool.getString("face", potLevel, "");
                    } else {
                        final int level = MapleDataTool.getIntConvert(i, potLevel, 0);
                        if (level > 0) { // Save memory
                            item.data.put(i, level);
                        }
                    }
                }
                switch (item.opID) {
                    case 31001: // Haste
                    case 31002: // Mystic Door
                    case 31003: // Sharp Eyes
                    case 31004: // Hyper Body
                        item.data.put("skillID", (item.opID - 23001));
                        break;
                    case 41005: // Combat Orders
                    case 41006: // Advanced Blessing
                    case 41007: // Speed Infusion
                        item.data.put("skillID", (item.opID - 33001));
                        break;
                }
                items.add(item);
            }
            potentialCache.put(Integer.parseInt(dat.getName()), items);
        }

        final MapleDataDirectoryEntry e = (MapleDataDirectoryEntry) etcData.getRoot().getEntry("Android");
        for (MapleDataEntry d : e.getFiles()) {
            final MapleData iz = etcData.getData("Android/" + d.getName());
            final List<Integer> face = new ArrayList<>(), hair = new ArrayList<>(), skin = new ArrayList<>();
            for (MapleData ds : iz.getChildByPath("costume/skin")) {
                skin.add(MapleDataTool.getInt(ds, 2000));
            }
            for (MapleData ds : iz.getChildByPath("costume/hair")) {
                hair.add(MapleDataTool.getInt(ds, 30000));
            }
            for (MapleData ds : iz.getChildByPath("costume/face")) {
                face.add(MapleDataTool.getInt(ds, 20000));
            }
            androids.put(Integer.parseInt(d.getName().substring(0, 4)), new Triple<>(hair, skin, face));
        }

        final MapleData lifesData = etcData.getData("ItemPotLifeInfo.img");
        for (MapleData d : lifesData) {
            if (d.getChildByPath("info") != null && MapleDataTool.getInt("type", d.getChildByPath("info"), 0) == 1) {
                potLife.put(MapleDataTool.getInt("counsumeItem", d.getChildByPath("info"), 0), new Pair<Integer, Integer>(Integer.parseInt(d.getName()), d.getChildByPath("level").getChildren().size()));
            }
        }
        List<Triple<String, Point, Point>> thePointK = new ArrayList<Triple<String, Point, Point>>();
        List<Triple<String, Point, Point>> thePointA = new ArrayList<Triple<String, Point, Point>>();

        final MapleDataDirectoryEntry a = (MapleDataDirectoryEntry) chrData.getRoot().getEntry("Afterimage");
        for (MapleDataEntry b : a.getFiles()) {
            final MapleData iz = chrData.getData("Afterimage/" + b.getName());
            List<Triple<String, Point, Point>> thePoint = new ArrayList<Triple<String, Point, Point>>();
            Map<String, Pair<Point, Point>> dummy = new HashMap<String, Pair<Point, Point>>();
            for (MapleData i : iz) {
                for (MapleData xD : i) {
                    if (xD.getName().contains("prone") || xD.getName().contains("double") || xD.getName().contains("triple")) {
                        continue;
                    }
                    if ((b.getName().contains("bow") || b.getName().contains("Bow")) && !xD.getName().contains("shoot")) {
                        continue;
                    }
                    if ((b.getName().contains("gun") || b.getName().contains("cannon")) && !xD.getName().contains("shot")) {
                        continue;
                    }
                    if (dummy.containsKey(xD.getName())) {
                        if (xD.getChildByPath("lt") != null) {
                            Point lt = (Point) xD.getChildByPath("lt").getData();
                            Point ourLt = dummy.get(xD.getName()).left;
                            if (lt.x < ourLt.x) {
                                ourLt.x = lt.x;
                            }
                            if (lt.y < ourLt.y) {
                                ourLt.y = lt.y;
                            }
                        }
                        if (xD.getChildByPath("rb") != null) {
                            Point rb = (Point) xD.getChildByPath("rb").getData();
                            Point ourRb = dummy.get(xD.getName()).right;
                            if (rb.x > ourRb.x) {
                                ourRb.x = rb.x;
                            }
                            if (rb.y > ourRb.y) {
                                ourRb.y = rb.y;
                            }
                        }
                    } else {
                        Point lt = null, rb = null;
                        if (xD.getChildByPath("lt") != null) {
                            lt = (Point) xD.getChildByPath("lt").getData();
                        }
                        if (xD.getChildByPath("rb") != null) {
                            rb = (Point) xD.getChildByPath("rb").getData();
                        }
                        dummy.put(xD.getName(), new Pair<Point, Point>(lt, rb));
                    }
                }
            }
            for (Entry<String, Pair<Point, Point>> ez : dummy.entrySet()) {
                if (ez.getKey().length() > 2 && ez.getKey().substring(ez.getKey().length() - 2, ez.getKey().length() - 1).equals("D")) { //D = double weapon
                    thePointK.add(new Triple<String, Point, Point>(ez.getKey(), ez.getValue().left, ez.getValue().right));
                } else if (ez.getKey().contains("PoleArm")) { //D = double weapon
                    thePointA.add(new Triple<String, Point, Point>(ez.getKey(), ez.getValue().left, ez.getValue().right));
                } else {
                    thePoint.add(new Triple<String, Point, Point>(ez.getKey(), ez.getValue().left, ez.getValue().right));
                }
            }
            afterImage.put(b.getName().substring(0, b.getName().length() - 4), thePoint);
        }
        afterImage.put("katara", thePointK); //hackish
        afterImage.put("aran", thePointA); //hackish
    }

    public void runItems() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM wz_itemdata");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemInformation(rs);
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM wz_itemequipdata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemEquipData(rs);
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM wz_itemadddata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemAddData(rs);
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM wz_itemrewarddata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemRewardData(rs);
            }
            rs.close();
            ps.close();
            for (Entry<Integer, ItemInformation> entry : dataCache.entrySet()) {
                if (GameConstants.getInventoryType(entry.getKey()) == MapleInventoryType.EQUIP) {
                    finalizeEquipData(entry.getValue());
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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
        //System.out.println("[Loading Completed] ITEM : <" + dataCache.size() + "> COUNT");
    }

    public final List<StructItemOption> getPotentialInfo(final int potId) {
        return potentialCache.get(potId);
    }

    public final Map<Integer, List<StructItemOption>> getAllPotentialInfo() {
        return potentialCache;
    }

    public final Collection<Integer> getMonsterBookList() {
        return mobIds.values();
    }

    public final Map<Integer, Integer> getMonsterBook() {
        return mobIds;
    }

    public final Pair<Integer, Integer> getPot(int f) {
        return potLife.get(f);
    }

    public static final MapleItemInformationProvider getInstance() {
        return instance;
    }

    public final Collection<ItemInformation> getAllItems() {
        return dataCache.values();
    }

    /* public final Pair<List<Integer>, List<Integer>> getAndroidInfo(int i) {
        return androids.get(i);
    }*/
    public final Triple<List<Integer>, List<Integer>, List<Integer>> getAndroidInfo(int i) {
        return androids.get(i);
    }

    public final Triple<Integer, List<Integer>, List<Integer>> getMonsterBookInfo(int i) {
        return monsterBookSets.get(i);
    }

    public final Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> getAllMonsterBookInfo() {
        return monsterBookSets;
    }

    protected final MapleData getItemData(final int itemId) {
        MapleData ret = null;
        final String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = itemData.getRoot();
        for (final MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            // we should have .img files here beginning with the first 4 IID
            for (final MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
                    if (ret == null) {
                        return null;
                    }
                    ret = ret.getChildByPath(idStr);
                    return ret;
                } else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                    return itemData.getData(topDir.getName() + "/" + iFile.getName());
                }
            }
        }
        //equips dont have item effects :)
        /*
         * root = equipData.getRoot(); for (final MapleDataDirectoryEntry topDir
         * : root.getSubdirectories()) { for (final MapleDataFileEntry iFile :
         * topDir.getFiles()) { if (iFile.getName().equals(idStr + ".img")) {
         * ret = equipData.getData(topDir.getName() + "/" + iFile.getName());
         * return ret; } }
        }
         */

        return ret;
    }

    public Integer getItemSuccessRate(int itemId) {
        final MapleData item = getItemData(itemId);
        int success = 0;
        success = MapleDataTool.getIntConvert("info/success", item, 100);
        return success;
    }

    public Integer getItemIdByMob(int mobId) {
        return mobIds.get(mobId);
    }

    public Integer getSetId(int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return Integer.valueOf(i.cardSet);
    }

    public boolean itemExist(int itemId) {
        if (itemNameCache.size() == 0) {
            getAllStringData();
        }
        return itemIdCache.contains(itemId);
    }

    public List<Pair<Integer, String>> getAllStringData() {
        List<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
        MapleData itemsData;
        itemsData = stringData.getData("Cash.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
            itemIdCache.add(Integer.parseInt(itemFolder.getName()));
        }
        itemsData = stringData.getData("Consume.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
            itemIdCache.add(Integer.parseInt(itemFolder.getName()));
        }
        itemsData = stringData.getData("Eqp.img").getChildByPath("Eqp");
        for (MapleData eqpType : itemsData.getChildren()) {
            for (MapleData itemFolder : eqpType.getChildren()) {
                itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
                itemIdCache.add(Integer.parseInt(itemFolder.getName()));
            }
        }
        itemsData = stringData.getData("Etc.img").getChildByPath("Etc");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
            itemIdCache.add(Integer.parseInt(itemFolder.getName()));
        }
        itemsData = stringData.getData("Ins.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
            itemIdCache.add(Integer.parseInt(itemFolder.getName()));
        }
        itemsData = stringData.getData("Pet.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
            itemIdCache.add(Integer.parseInt(itemFolder.getName()));
        }
        return itemPairs;
    }

    /**
     * returns the maximum of items in one slot
     */
    public final short getSlotMax(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.slotMax;
    }

    public final int getWholePrice(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.wholePrice;
    }

    public final int getOption(final int itemId, final int line) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        if (line == 1) {
            return i.option1;
        } else if (line == 2) {
            return i.option2;
        } else if (line == 3) {
            return i.option3;
        }
        return 0;
    }

    public final double getPrice(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0.0;
        }
        return i.price;
    }

    public final int getLv(int itemID) {
        ItemInformation i = getItemInformation(itemID);
        if (i == null) {
            return 0;
        }
        int lv = 0;
        MapleData item = getItemData(itemID);
        if (item == null) {
            return 0;
        }
        lv = MapleDataTool.getIntConvert("info/lv", item, 0);
        return lv;
    }

    protected int rand(int min, int max) {
        return Math.abs((int) Randomizer.rand(min, max));
    }

    public Equip levelUpEquip(Equip equip, Map<String, Integer> sta) {
        Equip nEquip = (Equip) equip.copy();
        //is this all the stats?
        try {
            for (Entry<String, Integer> stat : sta.entrySet()) {
                if (stat.getKey().equals("STRMin")) {
                    nEquip.setStr((short) (nEquip.getStr() + rand(stat.getValue().intValue(), sta.get("STRMax").intValue())));
                } else if (stat.getKey().equals("DEXMin")) {
                    nEquip.setDex((short) (nEquip.getDex() + rand(stat.getValue().intValue(), sta.get("DEXMax").intValue())));
                } else if (stat.getKey().equals("INTMin")) {
                    nEquip.setInt((short) (nEquip.getInt() + rand(stat.getValue().intValue(), sta.get("INTMax").intValue())));
                } else if (stat.getKey().equals("LUKMin")) {
                    nEquip.setLuk((short) (nEquip.getLuk() + rand(stat.getValue().intValue(), sta.get("LUKMax").intValue())));
                } else if (stat.getKey().equals("PADMin")) {
                    nEquip.setWatk((short) (nEquip.getWatk() + rand(stat.getValue().intValue(), sta.get("PADMax").intValue())));
                } else if (stat.getKey().equals("PDDMin")) {
                    nEquip.setWdef((short) (nEquip.getWdef() + rand(stat.getValue().intValue(), sta.get("PDDMax").intValue())));
                } else if (stat.getKey().equals("MADMin")) {
                    nEquip.setMatk((short) (nEquip.getMatk() + rand(stat.getValue().intValue(), sta.get("MADMax").intValue())));
                } else if (stat.getKey().equals("MDDMin")) {
                    nEquip.setMdef((short) (nEquip.getMdef() + rand(stat.getValue().intValue(), sta.get("MDDMax").intValue())));
                } else if (stat.getKey().equals("ACCMin")) {
                    nEquip.setAcc((short) (nEquip.getAcc() + rand(stat.getValue().intValue(), sta.get("ACCMax").intValue())));
                } else if (stat.getKey().equals("EVAMin")) {
                    nEquip.setAvoid((short) (nEquip.getAvoid() + rand(stat.getValue().intValue(), sta.get("EVAMax").intValue())));
                } else if (stat.getKey().equals("SpeedMin")) {
                    nEquip.setSpeed((short) (nEquip.getSpeed() + rand(stat.getValue().intValue(), sta.get("SpeedMax").intValue())));
                } else if (stat.getKey().equals("JumpMin")) {
                    nEquip.setJump((short) (nEquip.getJump() + rand(stat.getValue().intValue(), sta.get("JumpMax").intValue())));
                } else if (stat.getKey().equals("MHPMin")) {
                    nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue().intValue(), sta.get("MHPMax").intValue())));
                } else if (stat.getKey().equals("MMPMin")) {
                    nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue().intValue(), sta.get("MMPMax").intValue())));
                } else if (stat.getKey().equals("MaxHPMin")) {
                    nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue().intValue(), sta.get("MaxHPMax").intValue())));
                } else if (stat.getKey().equals("MaxMPMin")) {
                    nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue().intValue(), sta.get("MaxMPMax").intValue())));
                }
            }
        } catch (NullPointerException e) {
            //catch npe because obviously the wz have some error XD
            e.printStackTrace();
        }
        return nEquip;
    }

    public Equip levelUpEquipCustom(Equip equip, int levelTo) {
        Equip nEquip = (Equip) equip.copy();
        //is this all the stats?
        int statGain = 0, PADGain = 0, MADGain = 0, PDDGain = 0, MDDGain = 0, accGain = 0, HPGain = 0, MPGain = 0;
        Random rng = new Random();
        //rng.nextInt(upper - lower + 1) + lower
        if (levelTo <= 10) {
            statGain = rng.nextInt(2);
            PADGain = rng.nextInt(2);
            MADGain = rng.nextInt(2);
            PDDGain = rng.nextInt(11);
            MDDGain = rng.nextInt(11);
            accGain = rng.nextInt(11);
            HPGain = rng.nextInt(21);
            MPGain = rng.nextInt(21);
        } else if (levelTo <= 20) {
            statGain = rng.nextInt(3);
            PADGain = rng.nextInt(2);
            MADGain = rng.nextInt(3);
            PDDGain = rng.nextInt(14);
            MDDGain = rng.nextInt(14);
            accGain = rng.nextInt(14);
            HPGain = rng.nextInt(26);
            MPGain = rng.nextInt(26);
        } else if (levelTo <= 30) {
            statGain = rng.nextInt(4);
            PADGain = rng.nextInt(3);
            MADGain = rng.nextInt(4);
            PDDGain = rng.nextInt(21) + 5;
            MDDGain = rng.nextInt(21) + 5;
            accGain = rng.nextInt(21) + 5;
            HPGain = rng.nextInt(31) + 5;
            MPGain = rng.nextInt(31) + 5;
        } else if (levelTo <= 40) {
            statGain = rng.nextInt(3) + 1;
            PADGain = rng.nextInt(2) + 1;
            MADGain = rng.nextInt(3) + 1;
            PDDGain = rng.nextInt(31) + 10;
            MDDGain = rng.nextInt(31) + 10;
            accGain = rng.nextInt(31) + 10;
            HPGain = rng.nextInt(51) + 10;
            MPGain = rng.nextInt(51) + 10;
        } else if (levelTo <= 49) {
            statGain = rng.nextInt(3) + 2;
            PADGain = rng.nextInt(3) + 1;
            MADGain = rng.nextInt(2) + 2;
            PDDGain = rng.nextInt(51) + 20;
            MDDGain = rng.nextInt(51) + 20;
            accGain = rng.nextInt(51) + 20;
            HPGain = rng.nextInt(111) + 30;
            MPGain = rng.nextInt(111) + 30;
        } else if (levelTo == 50) {
            statGain = 20;
            PADGain = 10;
            MADGain = 15;
            PDDGain = 150;
            MDDGain = 150;
            accGain = 200;
            HPGain = 700;
            MPGain = 700;
        }
        nEquip.setStr((short) (nEquip.getStr() + rng.nextInt(2) + (statGain > 0 ? (rng.nextBoolean() ? statGain + 1 : statGain - 1) : statGain)));
        nEquip.setDex((short) (nEquip.getDex() + rng.nextInt(2) + statGain));
        nEquip.setInt((short) (nEquip.getInt() + rng.nextInt(2) + statGain));
        nEquip.setLuk((short) (nEquip.getLuk() + rng.nextInt(2) + statGain));
        nEquip.setWatk((short) (nEquip.getWatk() > 0 ? nEquip.getWatk() + PADGain : 0));
        nEquip.setMatk((short) (nEquip.getMatk() > 0 ? nEquip.getMatk() + MADGain : 0));
        nEquip.setWdef((short) (nEquip.getWdef() + PDDGain));
        nEquip.setMdef((short) (nEquip.getMdef() + MDDGain));
        nEquip.setHp((short) (nEquip.getHp() + HPGain));
        nEquip.setMp((short) (nEquip.getMp() + MPGain));
        nEquip.setAcc((short) (nEquip.getAcc() + accGain));
        nEquip.setAvoid((short) (nEquip.getAvoid() + accGain + rng.nextInt(4) - 1));
        return nEquip;
    }

    public final List<Triple<String, String, String>> getEquipAdditions(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipAdditions;
    }

    public final String getEquipAddReqs(final int itemId, final String key, final String sub) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        for (Triple<String, String, String> data : i.equipAdditions) {
            if (data.getLeft().equals("key") && data.getMid().equals("con:" + sub)) {
                return data.getRight();
            }
        }
        return null;
    }

    public final Map<Integer, Map<String, Integer>> getEquipIncrements(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipIncs;
    }

    public final List<Integer> getEquipSkills(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.incSkill;
    }

    public final Map<String, Integer> getEquipStats(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipStats;
    }

    public final boolean canEquip(final Map<String, Integer> stats, final int itemid, final int level, final int job, final int fame, final int str, final int dex, final int luk, final int int_, final int supremacy) {
        if ((level + supremacy) >= (stats.containsKey("reqLevel") ? stats.get("reqLevel") : 0) && str >= (stats.containsKey("reqSTR") ? stats.get("reqSTR") : 0) && dex >= (stats.containsKey("reqDEX") ? stats.get("reqDEX") : 0) && luk >= (stats.containsKey("reqLUK") ? stats.get("reqLUK") : 0) && int_ >= (stats.containsKey("reqINT") ? stats.get("reqINT") : 0)) {
            final Integer fameReq = stats.get("reqPOP");
            if (fameReq != null && fame < fameReq) {
                return false;
            }
            return true;
        }
        return false;
    }

    public final int getReqLevel(final int itemId) {
        if (getEquipStats(itemId) == null || !getEquipStats(itemId).containsKey("reqLevel")) {
            return 0;
        }
        return getEquipStats(itemId).get("reqLevel");
    }

    public final int getSlots(final int itemId) {
        if (getEquipStats(itemId) == null || !getEquipStats(itemId).containsKey("tuc")) {
            return 0;
        }
        return getEquipStats(itemId).get("tuc");
    }

    public final Integer getSetItemID(final int itemId) {
        if (getEquipStats(itemId) == null || !getEquipStats(itemId).containsKey("setItemID")) {
            return 0;
        }
        return getEquipStats(itemId).get("setItemID");
    }

    public final StructSetItem getSetItem(final int setItemId) {
        return setItems.get(setItemId);
    }

    public final List<Integer> getScrollReqs(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.scrollReqs;
    }

    public final Item scrollEquipWithId(final Item equip, final Item scrollId, final boolean ws, final MapleCharacter chr, final int vegas) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (equip.getType() == 1) { // See Item.java
            final Equip nEquip = (Equip) equip;
            final Map<String, Integer> stats = getEquipStats(scrollId.getItemId());
            final Map<String, Integer> eqstats = getEquipStats(equip.getItemId());
            final int succ = (GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getSuccessTablet(scrollId.getItemId(), nEquip.getLevel()) : ((GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()) || !stats.containsKey("success") ? 0 : stats.get("success"))));
            final int curse = (GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getCurseTablet(scrollId.getItemId(), nEquip.getLevel()) : ((GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()) || !stats.containsKey("cursed") ? 0 : stats.get("cursed"))));
            final int added = (ItemFlag.LuckyDayScroll.check(equip.getFlag()) ? 10 : 0) + (chr.getTrait(MapleTraitType.craft).getLevel() / 10);
            int success = succ + (vegas == 5610000 && succ == 10 ? 20 : (vegas == 5610001 && succ == 60 ? 30 : 0)) + added;
            if (ItemFlag.LuckyDayScroll.check(equip.getFlag()) && !GameConstants.isPotentialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId())) {
                equip.setFlag((short) (equip.getFlag() - ItemFlag.LuckyDayScroll.getValue()));
            }

            if (ii.getName(scrollId.getItemId()).contains("10%")) {
                success = 10;
            } else if (ii.getName(scrollId.getItemId()).contains("30%")) {
                success = 30;
            } else if (ii.getName(scrollId.getItemId()).contains("60%")) {
                success = 60;
            } else if (ii.getName(scrollId.getItemId()).contains("70%")) {
                success = 70;
            }

            if (GameConstants.isPotentialScroll(scrollId.getItemId()) || GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isSpecialScroll(scrollId.getItemId()) || Randomizer.nextInt(100) <= success) {
                switch (scrollId.getItemId()) {
                    case 2049000:
                    case 2049001:
                    case 2049002:
                    case 2049003:
                    case 2049004:
                    case 2049005: {
                        if (eqstats.containsKey("tuc") && nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                            nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 1));
                        }
                        break;
                    }
                    case 2049006:
                    case 2049007:
                    case 2049008: {
                        if (eqstats.containsKey("tuc") && nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                            nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 2));
                        }
                        break;
                    }
                    /*
                    case 2049180:
                    case 2049181: 
                    case 2049182: 
                    case 2049183: {
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.PreventSlipping.getValue();
                        nEquip.setFlag(flag);
                        chr.equipChanged();
                        break;
                    }
                     */
                    case 2041058: { // Cape for Cold protection
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.PreventColdness.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 5063000:
                    case 2530000:
                    case 2530001: {
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.LuckyDayScroll.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 5063100:
                    case 5064000:
                    case 5064002:
                    case 2531000: {
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.ProtectScroll.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2532000: // 세이프티
                    case 2532002:
                    case 2532005: {
                        short flag = nEquip.getFlag();
                        flag += ItemFlag.SafetyScroll.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2533000: // 리커버리
                    {
                        short flag = nEquip.getFlag();
                        flag += ItemFlag.RecoveryScroll.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2643128:
                        if (nEquip.getItemId() == 1114300) {
                            nEquip.addStr((short) 1);
                            nEquip.addDex((short) 1);
                            nEquip.addInt((short) 1);
                            nEquip.addLuk((short) 1);
                            nEquip.addWatk((short) 1);
                            nEquip.addMatk((short) 1);
                            nEquip.addHp((short) 100);
                            nEquip.addMp((short) 100);
                        }
                        break;
                    case 2643130:
                        if (nEquip.getItemId() == 1114303) {
                            nEquip.addStr((short) 1);
                            nEquip.addDex((short) 1);
                            nEquip.addInt((short) 1);
                            nEquip.addLuk((short) 1);
                            nEquip.addWatk((short) 1);
                            nEquip.addMatk((short) 1);
                            nEquip.addHp((short) 100);
                            nEquip.addMp((short) 100);
                        }
                        break;
                    case 2046991:
                    case 2046992:
                    case 2047814: {
                        nEquip.addStr((short) 3);
                        nEquip.addDex((short) 3);
                        nEquip.addInt((short) 3);
                        nEquip.addLuk((short) 3);
                        if (scrollId.getItemId() == 2046992) {
                            nEquip.addMatk((short) 5);
                        } else {
                            nEquip.addWatk((short) 5);
                        }
                        break;
                    }
                    case 2046685:
                    case 2048094:
                    case 2046829: {
                        nEquip.addWatk((short) 5);
                        break;
                    }
                    case 2046686:
                    case 2048095:
                    case 2046830: {
                        nEquip.addMatk((short) 5);
                        break;
                    }
                    case 2046856:
                    case 2046857: {
                        int lucky = 4 + (Randomizer.isSuccess(15) ? 1 : 0);
                        if (scrollId.getItemId() == 2046857) {
                            nEquip.addMatk((short) lucky);
                        } else {
                            nEquip.addWatk((short) lucky);
                        }
                        break;
                    }
                    case 2048804: {
                        nEquip.setWatk((short) (nEquip.getWatk() + 5));
                        break;
                    }
                    case 2048805: {
                        nEquip.setMatk((short) (nEquip.getMatk() + 5));
                        break;
                    }
                    case 2048809: {
                        nEquip.setWatk((short) (nEquip.getWatk() + 2));
                        break;
                    }
                    case 2048810: {
                        nEquip.setMatk((short) (nEquip.getMatk() + 2));
                        break;
                    }
                    case 2645000:
                    case 2645001: {
                        switch (nEquip.getItemId()) {
                            case 1113072:
                            case 1113073:
                            case 1113074:
                            case 1032220:
                            case 1032221:
                            case 1032222:
                            case 1122264:
                            case 1122265:
                            case 1122266:
                            case 1132243:
                            case 1132244:
                            case 1132245:
                                nEquip.addStr((short) 3);
                                nEquip.addDex((short) 3);
                                nEquip.addInt((short) 3);
                                nEquip.addLuk((short) 3);
                                if (scrollId.getItemId() == 2645000) {
                                    nEquip.addWatk((short) 3);
                                } else {
                                    nEquip.addMatk((short) 3);
                                }
                                break;
                        }
                        break;
                    }
                    case 2645002:
                    case 2645003: {
                        switch (nEquip.getItemId()) {
                            case 1113075:
                            case 1032223:
                            case 1122267:
                            case 1132246:
                                nEquip.addStr((short) Randomizer.rand(10, 30));
                                nEquip.addDex((short) Randomizer.rand(10, 30));
                                nEquip.addInt((short) Randomizer.rand(10, 30));
                                nEquip.addLuk((short) Randomizer.rand(10, 30));
                                if (scrollId.getItemId() == 2645002) {
                                    nEquip.addWatk((short) Randomizer.rand(10, 20));
                                } else {
                                    nEquip.addMatk((short) Randomizer.rand(10, 20));
                                }
                                break;
                        }
                        break;
                    }
                    default: {
                        if (GameConstants.isChaosScroll(scrollId.getItemId())) { // 혼돈의주문서
                            final int z = GameConstants.getChaosNumber(scrollId.getItemId());
                            //final boolean goodness = scrollId.getItemId() == 2049122;
                            final boolean goodness = ii.getName(scrollId.getItemId()).contains("놀라운 긍정의 혼돈 주문서");
                            cSTR = 0;
                            if (nEquip.getStr() > 0) {
                                cSTR = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setStr((short) (nEquip.getStr() + cSTR));
                            }
                            cDEX = 0;
                            if (nEquip.getDex() > 0) {
                                cDEX = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setDex((short) (nEquip.getDex() + cDEX));
                            }
                            cINT = 0;
                            if (nEquip.getInt() > 0) {
                                cINT = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setInt((short) (nEquip.getInt() + cINT));
                            }
                            cLUK = 0;
                            if (nEquip.getLuk() > 0) {
                                cLUK = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setLuk((short) (nEquip.getLuk() + cLUK));
                            }
                            cPAD = 0;
                            if (nEquip.getWatk() > 0) {
                                cPAD = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setWatk((short) (nEquip.getWatk() + cPAD));
                            }
                            cWDEF = 0;
                            if (nEquip.getWdef() > 0) {
                                cWDEF = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setWdef((short) (nEquip.getWdef() + cWDEF));
                            }
                            cMAD = 0;
                            if (nEquip.getMatk() > 0) {
                                cMAD = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setMatk((short) (nEquip.getMatk() + cMAD));
                            }
                            cMDEF = 0;
                            if (nEquip.getMdef() > 0) {
                                cMDEF = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setMdef((short) (nEquip.getMdef() + cMDEF));
                            }
                            cACC = 0;
                            if (nEquip.getAcc() > 0) {
                                cACC = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setAcc((short) (nEquip.getAcc() + cACC));
                            }
                            cEVA = 0;
                            if (nEquip.getAvoid() > 0) {
                                cEVA = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setAvoid((short) (nEquip.getAvoid() + cEVA));
                            }
                            cSPEED = 0;
                            if (nEquip.getSpeed() > 0) {
                                cSPEED = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setSpeed((short) (nEquip.getSpeed() + cSPEED));
                            }
                            cJUMP = 0;
                            if (nEquip.getJump() > 0) {
                                cJUMP = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setJump((short) (nEquip.getJump() + cJUMP));
                            }
                            cHP = 0;
                            if (nEquip.getHp() > 0) {
                                cHP = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setHp((short) (nEquip.getHp() + cHP));
                            }
                            cMP = 0;
                            if (nEquip.getMp() > 0) {
                                cMP = (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : (goodness ? 1 : -1));
                                nEquip.setMp((short) (nEquip.getMp() + cMP));
                            }
                            break;
                        } else if (GameConstants.isEquipScroll(scrollId.getItemId())) {
                            int eSucc = (90 - (nEquip.getEnhance() * 10));
                            switch (scrollId.getItemId()) {
                                case 2049300:
                                case 2049303:
                                case 2049306: {
                                    eSucc += 20;
                                    if (nEquip.getEnhance() > 10) {
                                        eSucc = 5;
                                    }
                                    break;
                                }
                                default: {
                                    if (nEquip.getEnhance() > 8) {
                                        eSucc = 5;
                                    }
                                    break;
                                }
                            }
                            final int eRate = chr.isGM() ? 100 : eSucc;
                            if (Randomizer.nextInt(100) > eRate) {
                                if (!ItemFlag.ProtectScroll.check(nEquip.getFlag()) || nEquip.getEnhance() > 11) {
                                    return null;
                                }
                            } else {
                                int sTurn = 1;
                                switch (scrollId.getItemId()) {
                                    case 2049304:
                                    case 2049311: {
                                        sTurn = 3;
                                        break;
                                    }
                                    case 2049305:
                                    case 2049312: {
                                        sTurn = 4;
                                        break;
                                    }
                                    case 2049308:
                                    case 2049313: {
                                        sTurn = 5;
                                        break;
                                    }
                                    case 2049309: {
                                        sTurn = 2;
                                        break;
                                    }
                                    case 2049370: {
                                        sTurn = 12;
                                        break;
                                    }
                                    case 2049371: {
                                        sTurn = 17;
                                        break;
                                    }
                                    case 2049372: {
                                        sTurn = 15;
                                        break;
                                    }
                                    case 2049373: {
                                        sTurn = 10;
                                        break;
                                    }
                                    case 2049374: {
                                        sTurn = 12;
                                        break;
                                    }
                                    case 2049375: {
                                        sTurn = 12;
                                        break;
                                    }
                                    case 2049376: {
                                        sTurn = 20;
                                        break;
                                    }
                                }
                                for (int i = 0; i < sTurn; i++) {
                                    if (nEquip.getStr() > 0 || Randomizer.nextInt(5) == 1) {
                                        nEquip.setStr((short) (nEquip.getStr() + 1));
                                    }
                                    if (nEquip.getDex() > 0 || Randomizer.nextInt(5) == 1) {
                                        nEquip.setDex((short) (nEquip.getDex() + 1));
                                    }
                                    if (nEquip.getInt() > 0 || Randomizer.nextInt(5) == 1) {
                                        nEquip.setInt((short) (nEquip.getInt() + 1));
                                    }
                                    if (nEquip.getLuk() > 0 || Randomizer.nextInt(5) == 1) {
                                        nEquip.setLuk((short) (nEquip.getLuk() + 1));
                                    }
                                    if (nEquip.getWatk() > 0 || nEquip.getEnhance() > 9) {
                                        short atkIncrease = (short) (Math.floor(nEquip.getWatk() / 50) + 1);
                                        nEquip.setWatk((short) (nEquip.getWatk() + atkIncrease));
                                    }
                                    if (nEquip.getWdef() > 0 || Randomizer.nextInt(4) == 1) {
                                        nEquip.setWdef((short) (nEquip.getWdef() + 1));
                                    }
                                    if (nEquip.getMatk() > 0 || nEquip.getEnhance() > 9) {
                                        short atkIncrease = (short) (Math.floor(nEquip.getMatk() / 50) + 1);
                                        nEquip.setMatk((short) (nEquip.getMatk() + atkIncrease));
                                    }
                                    if (nEquip.getMdef() > 0 || Randomizer.nextInt(4) == 1) {
                                        nEquip.setMdef((short) (nEquip.getMdef() + 1));
                                    }
                                    if (nEquip.getAcc() > 0 || Randomizer.nextInt(20) == 1) {
                                        nEquip.setAcc((short) (nEquip.getAcc() + 1));
                                    }
                                    if (nEquip.getAvoid() > 0 || Randomizer.nextInt(20) == 1) {
                                        nEquip.setAvoid((short) (nEquip.getAvoid() + 1));
                                    }
                                    if (nEquip.getSpeed() > 0 || Randomizer.nextInt(10) == 1) {
                                        nEquip.setSpeed((short) (nEquip.getSpeed() + 1));
                                    }
                                    if (nEquip.getJump() > 0 || Randomizer.nextInt(10) == 1) {
                                        nEquip.setJump((short) (nEquip.getJump() + 1));
                                    }
                                    if (nEquip.getHp() > 0 || Randomizer.nextInt(5) == 1) {
                                        nEquip.setHp((short) (nEquip.getHp() + 10));
                                    }
                                    if (nEquip.getMp() > 0 || Randomizer.nextInt(5) == 1) {
                                        nEquip.setMp((short) (nEquip.getMp() + 10));
                                    }
                                    nEquip.setEnhance((byte) (nEquip.getEnhance() + 1));
                                    if (nEquip.getEnhance() > 14) {
                                        for (ChannelServer cs : ChannelServer.getAllInstances()) {
                                            for (MapleMap mm : cs.getMapFactory().getAllLoadedMaps()) {
                                                mm.floatNotice("축하드립니다. " + chr.getName() + "님께서 [" + ii.getName(nEquip.getItemId()) + "] +" + nEquip.getEnhance() + " 강화에 성공하였습니다.", 5120037, false);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        } else if (GameConstants.isPotentialScroll(scrollId.getItemId())) {
                            int pGrade = 17;
                            if ((scrollId.getItemId() / 10) == 204970) {
                                pGrade = 18;
                            }
                            if ((scrollId.getItemId() / 10) == 204975) {
                                pGrade = 19;
                            }
                            if ((scrollId.getItemId() / 10) == 204976) {
                                pGrade = 20;
                            }
                            nEquip.setPotentialOpen(nEquip, pGrade);
                            break;
                        } else {
                            for (Entry<String, Integer> stat : stats.entrySet()) {
                                final String key = stat.getKey();

                                if (key.equals("STR")) {
                                    nEquip.setStr((short) (nEquip.getStr() + stat.getValue().intValue()));
                                } else if (key.equals("DEX")) {
                                    nEquip.setDex((short) (nEquip.getDex() + stat.getValue().intValue()));
                                } else if (key.equals("INT")) {
                                    nEquip.setInt((short) (nEquip.getInt() + stat.getValue().intValue()));
                                } else if (key.equals("LUK")) {
                                    nEquip.setLuk((short) (nEquip.getLuk() + stat.getValue().intValue()));
                                } else if (key.equals("PAD")) {
                                    nEquip.setWatk((short) (nEquip.getWatk() + stat.getValue().intValue()));
                                } else if (key.equals("PDD")) {
                                    nEquip.setWdef((short) (nEquip.getWdef() + stat.getValue().intValue()));
                                } else if (key.equals("MAD")) {
                                    nEquip.setMatk((short) (nEquip.getMatk() + stat.getValue().intValue()));
                                } else if (key.equals("MDD")) {
                                    nEquip.setMdef((short) (nEquip.getMdef() + stat.getValue().intValue()));
                                } else if (key.equals("ACC")) {
                                    nEquip.setAcc((short) (nEquip.getAcc() + stat.getValue().intValue()));
                                } else if (key.equals("EVA")) {
                                    nEquip.setAvoid((short) (nEquip.getAvoid() + stat.getValue().intValue()));
                                } else if (key.equals("Speed")) {
                                    nEquip.setSpeed((short) (nEquip.getSpeed() + stat.getValue().intValue()));
                                } else if (key.equals("Jump")) {
                                    nEquip.setJump((short) (nEquip.getJump() + stat.getValue().intValue()));
                                } else if (key.equals("MHP")) {
                                    nEquip.setHp((short) (nEquip.getHp() + stat.getValue().intValue()));
                                } else if (key.equals("MMP")) {
                                    nEquip.setMp((short) (nEquip.getMp() + stat.getValue().intValue()));
                                }
                            }
                            break;
                        }
                    }
                }
                if (!GameConstants.isExceptional(scrollId.getItemId()) && !GameConstants.isCleanSlate(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isPotentialScroll(scrollId.getItemId())) {
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                    nEquip.setLevel((byte) (nEquip.getLevel() + 1));
                }
            } else {
                if (!ws && !GameConstants.isCleanSlate(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isPotentialScroll(scrollId.getItemId())) {
                    if (ItemFlag.SafetyScroll.check(nEquip.getFlag())) {
                        chr.dropMessage(5, "주문서의 효과로 업그레이드 가능 횟수가 차감되지 않았습니다.");
                    } else {
                        nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                    }
                }
                if (ItemFlag.ProtectScroll.check(nEquip.getFlag()) && nEquip.getEnhance() < 12) {
                } else {
                    if (Randomizer.nextInt(99) < curse) {
                        return null;
                    }
                }
            }
        }
        return equip;
    }

    public final Item getEquipById(final int equipId) {
        return getEquipById(equipId, -1);
    }

    public final Item getEquipById(final int equipId, final int ringId) {
        final ItemInformation i = getItemInformation(equipId);
        if (i == null) {
            return new Equip(equipId, (short) 0, ringId, (byte) 0);

        }
        final Item eq = i.eq.copy();
        if (i.option1 != 0) {
            ((Equip) eq).setPotential1(i.option1);
        }
        if (i.option2 != 0) {
            ((Equip) eq).setPotential2(i.option2);
        }
        if (i.option3 != 0) {
            ((Equip) eq).setPotential3(i.option3);
        }
        eq.setUniqueId(ringId);
        return eq;
    }

    protected final short getRandStatFusion(final short defaultValue, final int value1, final int value2) {
        if (defaultValue == 0) {
            return 0;
        }
        final int range = ((value1 + value2) / 2) - defaultValue;
        final int rand = Randomizer.nextInt(Math.abs(range) + 1);
        return (short) (defaultValue + (range < 0 ? -rand : rand));
    }

    protected final short getRandStat(final short defaultValue, final int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }
        // vary no more than ceil of 10% of stat
        final int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);

        return (short) ((defaultValue - lMaxRange) + Randomizer.nextInt(lMaxRange * 2 + 1));
    }

    protected final short getRandStatAbove(final short defaultValue, final int maxRange) {
        if (defaultValue <= 0) {
            return 0;
        }
        final int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);

        return (short) ((defaultValue) + Randomizer.nextInt(lMaxRange + 1));
    }

    public final Equip randomizeStats(final Equip equip) {
        equip.setStr(getRandStat(equip.getStr(), 5));
        equip.setDex(getRandStat(equip.getDex(), 5));
        equip.setInt(getRandStat(equip.getInt(), 5));
        equip.setLuk(getRandStat(equip.getLuk(), 5));
        equip.setMatk(getRandStat(equip.getMatk(), 5));
        equip.setWatk(getRandStat(equip.getWatk(), 5));
        equip.setAcc(getRandStat(equip.getAcc(), 5));
        equip.setAvoid(getRandStat(equip.getAvoid(), 5));
        equip.setJump(getRandStat(equip.getJump(), 5));
        equip.setHands(getRandStat(equip.getHands(), 5));
        equip.setSpeed(getRandStat(equip.getSpeed(), 5));
        equip.setWdef(getRandStat(equip.getWdef(), 10));
        equip.setMdef(getRandStat(equip.getMdef(), 10));
        equip.setHp(getRandStat(equip.getHp(), 10));
        equip.setMp(getRandStat(equip.getMp(), 10));
        return equip;
    }

    public final Equip randomizeStats_Above(final Equip equip) {
        equip.setStr(getRandStatAbove(equip.getStr(), 5));
        equip.setDex(getRandStatAbove(equip.getDex(), 5));
        equip.setInt(getRandStatAbove(equip.getInt(), 5));
        equip.setLuk(getRandStatAbove(equip.getLuk(), 5));
        equip.setMatk(getRandStatAbove(equip.getMatk(), 5));
        equip.setWatk(getRandStatAbove(equip.getWatk(), 5));
        equip.setAcc(getRandStatAbove(equip.getAcc(), 5));
        equip.setAvoid(getRandStatAbove(equip.getAvoid(), 5));
        equip.setJump(getRandStatAbove(equip.getJump(), 5));
        equip.setHands(getRandStatAbove(equip.getHands(), 5));
        equip.setSpeed(getRandStatAbove(equip.getSpeed(), 5));
        equip.setWdef(getRandStatAbove(equip.getWdef(), 10));
        equip.setMdef(getRandStatAbove(equip.getMdef(), 10));
        equip.setHp(getRandStatAbove(equip.getHp(), 10));
        equip.setMp(getRandStatAbove(equip.getMp(), 10));
        return equip;
    }

    public final Equip fuse(final Equip equip1, final Equip equip2) {
        if (equip1.getItemId() != equip2.getItemId()) {
            return equip1;
        }
        final Equip equip = (Equip) getEquipById(equip1.getItemId());
        equip.setStr(getRandStatFusion(equip.getStr(), equip1.getStr(), equip2.getStr()));
        equip.setDex(getRandStatFusion(equip.getDex(), equip1.getDex(), equip2.getDex()));
        equip.setInt(getRandStatFusion(equip.getInt(), equip1.getInt(), equip2.getInt()));
        equip.setLuk(getRandStatFusion(equip.getLuk(), equip1.getLuk(), equip2.getLuk()));
        equip.setMatk(getRandStatFusion(equip.getMatk(), equip1.getMatk(), equip2.getMatk()));
        equip.setWatk(getRandStatFusion(equip.getWatk(), equip1.getWatk(), equip2.getWatk()));
        equip.setAcc(getRandStatFusion(equip.getAcc(), equip1.getAcc(), equip2.getAcc()));
        equip.setAvoid(getRandStatFusion(equip.getAvoid(), equip1.getAvoid(), equip2.getAvoid()));
        equip.setJump(getRandStatFusion(equip.getJump(), equip1.getJump(), equip2.getJump()));
        equip.setHands(getRandStatFusion(equip.getHands(), equip1.getHands(), equip2.getHands()));
        equip.setSpeed(getRandStatFusion(equip.getSpeed(), equip1.getSpeed(), equip2.getSpeed()));
        equip.setWdef(getRandStatFusion(equip.getWdef(), equip1.getWdef(), equip2.getWdef()));
        equip.setMdef(getRandStatFusion(equip.getMdef(), equip1.getMdef(), equip2.getMdef()));
        equip.setHp(getRandStatFusion(equip.getHp(), equip1.getHp(), equip2.getHp()));
        equip.setMp(getRandStatFusion(equip.getMp(), equip1.getMp(), equip2.getMp()));
        return equip;
    }

    public final int getTotalStat(final Equip equip) { //i get COOL when my defense is higher on gms...
        return equip.getStr() + equip.getDex() + equip.getInt() + equip.getLuk() + equip.getMatk() + equip.getWatk() + equip.getAcc() + equip.getAvoid() + equip.getJump()
                + equip.getHands() + equip.getSpeed() + equip.getHp() + equip.getMp() + equip.getWdef() + equip.getMdef();
    }

    public final MapleStatEffect getItemReward(final int itemId) {
        MapleStatEffect ret = itemReward.get(Integer.valueOf(itemId));
        if (ret == null) {
            System.out.println("0");
            final MapleData item = getItemData(itemId);
            if (item == null || item.getChildByPath("reward") == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("reward"), itemId);
            itemReward.put(Integer.valueOf(itemId), ret);
        }
        return ret;
    }

    public final MapleStatEffect getItemEffect(final int itemId) {
        MapleStatEffect ret = itemEffects.get(Integer.valueOf(itemId));
        if (ret == null) {
            final MapleData item = getItemData(itemId);
            if (item == null || item.getChildByPath("spec") == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
            itemEffects.put(Integer.valueOf(itemId), ret);
        }
        return ret;
    }

    public final MapleStatEffect getItemEffectEX(final int itemId) {
        MapleStatEffect ret = itemEffectsEx.get(Integer.valueOf(itemId));
        if (ret == null) {
            final MapleData item = getItemData(itemId);
            if (item == null || item.getChildByPath("specEx") == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("specEx"), itemId);
            itemEffectsEx.put(Integer.valueOf(itemId), ret);
        }
        return ret;
    }

    public final int getCreateId(final int id) {
        final ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.create;
    }

    public final int getCardMobId(final int id) {
        final ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.monsterBook;
    }

    public final int getBagType(final int id) {
        final ItemInformation i = getItemInformation(id);
        if (id == 4330019) {
            return 5; // 코인 지갑 임시처리
        } else if (i == null) {
            return 0;
        }
        return i.flag & 0xF;
    }

    public final int getWatkForProjectile(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null || i.equipStats == null || i.equipStats.get("incPAD") == null) {
            return 0;
        }
        return i.equipStats.get("incPAD");
    }

    public final boolean canScroll(final int scrollid, final int itemid) {
        return (scrollid / 100) % 100 == (itemid / 10000) % 100;
    }

    public final String getName(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.name;
    }

    public final String getDesc(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.desc;
    }

    public final String getMsg(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.msg;
    }

    public final short getItemMakeLevel(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.itemMakeLevel;
    }

    public final boolean isDropRestricted(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return ((i.flag & 0x200) != 0 || (i.flag & 0x400) != 0) && (itemId == 3012000 || itemId == 3012015 || itemId / 10000 != 301) && itemId != 1122334 && itemId != 2041200 && itemId != 5640000 && itemId != 4170023 && itemId != 2040124 && itemId != 2040125 && itemId != 2040126 && itemId != 2040211 && itemId != 2040212 && itemId != 2040227 && itemId != 2040228 && itemId != 2040229 && itemId != 2040230 && itemId != 1002926 && itemId != 1002906 && itemId != 1002927;
    }

    public final boolean isPickupRestricted(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return ((i.flag & 0x80) != 0) && itemId != 4001168 && itemId != 4031306 && itemId != 4031307;
    }

    public final boolean isAccountShared(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x100) != 0;
    }

    public final int getStateChangeItem(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.stateChange;
    }

    public final int getMeso(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.meso;
    }

    public final boolean isShareTagEnabled(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x800) != 0;
    }

    public final boolean isKarmaEnabled(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return i.karmaEnabled == 1;
    }

    public final boolean isPKarmaEnabled(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return i.karmaEnabled == 2;
    }

    public final boolean isPickupBlocked(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x40) != 0;
    }

    public final boolean isLogoutExpire(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x20) != 0;
    }

    public final boolean cantSell(final int itemId) { //true = cant sell, false = can sell
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x10) != 0;
    }

    public final Pair<Integer, List<StructRewardItem>> getRewardItem(final int itemid) {
        final ItemInformation i = getItemInformation(itemid);
        if (i == null) {
            return null;
        }
        return new Pair<Integer, List<StructRewardItem>>(i.totalprob, i.rewardItems);
    }

    public final boolean isMobHP(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x1000) != 0;
    }

    public final boolean isQuestItem(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x200) != 0 && itemId / 10000 != 301;
    }

    public final Pair<Integer, List<Integer>> questItemInfo(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return new Pair<Integer, List<Integer>>(i.questId, i.questItems);
    }

    public final Pair<Integer, String> replaceItemInfo(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return new Pair<Integer, String>(i.replaceItem, i.replaceMsg);
    }

    public final List<Triple<String, Point, Point>> getAfterImage(final String after) {
        return afterImage.get(after);
    }

    public final String getAfterImage(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.afterImage;
    }

    public final boolean itemExists(final int itemId) {
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.UNDEFINED) {
            return false;
        }
        return getItemInformation(itemId) != null;
    }

    public final boolean isCash(final int itemId) {
        if (getEquipStats(itemId) == null) {
            return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH;
        }
        return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || getEquipStats(itemId).get("cash") != null;
    }

    public final ItemInformation getItemInformation(final int itemId) {
        if (itemId <= 0) {
            return null;
        }
        return dataCache.get(itemId);
    }

    private ItemInformation tmpInfo = null;

    public void initItemRewardData(ResultSet sqlRewardData) throws SQLException {
        final int itemID = sqlRewardData.getInt("itemid");
        if (tmpInfo == null || tmpInfo.itemId != itemID) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemRewardData] Tried to load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.rewardItems == null) {
            tmpInfo.rewardItems = new ArrayList<StructRewardItem>();
        }

        StructRewardItem add = new StructRewardItem();
        add.itemid = sqlRewardData.getInt("item");
        add.period = (add.itemid == 1122017 ? Math.max(sqlRewardData.getInt("period"), 7200) : sqlRewardData.getInt("period"));
        add.prob = sqlRewardData.getInt("prob");
        add.quantity = sqlRewardData.getShort("quantity");
        add.worldmsg = sqlRewardData.getString("worldMsg").length() <= 0 ? null : sqlRewardData.getString("worldMsg");
        add.effect = sqlRewardData.getString("effect");

        tmpInfo.rewardItems.add(add);
    }

    public void initItemAddData(ResultSet sqlAddData) throws SQLException {
        final int itemID = sqlAddData.getInt("itemid");
        if (tmpInfo == null || tmpInfo.itemId != itemID) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemAddData] Tried to load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.equipAdditions == null) {
            tmpInfo.equipAdditions = new LinkedList<>();
        }

        while (sqlAddData.next()) {
            tmpInfo.equipAdditions.add(new Triple<>(sqlAddData.getString("key"), sqlAddData.getString("subKey"), sqlAddData.getString("value")));
        }
    }

    public void initItemEquipData(ResultSet sqlEquipData) throws SQLException {
        final int itemID = sqlEquipData.getInt("itemid");
        if (tmpInfo == null || tmpInfo.itemId != itemID) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemEquipData] Tried to load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.equipStats == null) {
            tmpInfo.equipStats = new HashMap<String, Integer>();
        }

        final int itemLevel = sqlEquipData.getInt("itemLevel");
        if (itemLevel == -1) {
            tmpInfo.equipStats.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
        } else {
            if (tmpInfo.equipIncs == null) {
                tmpInfo.equipIncs = new HashMap<Integer, Map<String, Integer>>();
            }

            Map<String, Integer> toAdd = tmpInfo.equipIncs.get(itemLevel);
            if (toAdd == null) {
                toAdd = new HashMap<String, Integer>();
                tmpInfo.equipIncs.put(itemLevel, toAdd);
            }
            toAdd.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
        }
    }

    public void finalizeEquipData(ItemInformation item) {
        int itemId = item.itemId;

        // Some equips do not have equip data. So we initialize it anyway if not initialized
        // already
        // Credits: Jay :)
        if (item.equipStats == null) {
            item.equipStats = new HashMap<String, Integer>();
        }

        item.eq = new Equip(itemId, (byte) 0, -1, (byte) 0);
        short stats = GameConstants.getATK(itemId, 0);
        if (stats > 0) {
            item.eq.setWatk(stats);
            item.eq.setMatk(stats);
        }
        stats = GameConstants.getHpMp(itemId, 0);
        if (stats > 0) {
            item.eq.setHp(stats);
            item.eq.setMp(stats);
        }
        stats = GameConstants.getDEF(itemId, 0);
        if (stats > 0) {
            item.eq.setWdef(stats);
            item.eq.setMdef(stats);
        }
        if (item.equipStats.size() > 0) {
            for (Entry<String, Integer> stat : item.equipStats.entrySet()) {
                final String key = stat.getKey();

                if (key.equals("STR")) {
                    item.eq.setStr(GameConstants.getStat(itemId, stat.getValue().intValue()));
                } else if (key.equals("DEX")) {
                    item.eq.setDex(GameConstants.getStat(itemId, stat.getValue().intValue()));
                } else if (key.equals("INT")) {
                    item.eq.setInt(GameConstants.getStat(itemId, stat.getValue().intValue()));
                } else if (key.equals("LUK")) {
                    item.eq.setLuk(GameConstants.getStat(itemId, stat.getValue().intValue()));
                } else if (key.equals("PAD")) {
                    item.eq.setWatk(GameConstants.getATK(itemId, stat.getValue().intValue()));
                } else if (key.equals("PDD")) {
                    item.eq.setWdef(GameConstants.getDEF(itemId, stat.getValue().intValue()));
                } else if (key.equals("MAD")) {
                    item.eq.setMatk(GameConstants.getATK(itemId, stat.getValue().intValue()));
                } else if (key.equals("MDD")) {
                    item.eq.setMdef(GameConstants.getDEF(itemId, stat.getValue().intValue()));
                } else if (key.equals("ACC")) {
                    item.eq.setAcc((short) stat.getValue().intValue());
                } else if (key.equals("EVA")) {
                    item.eq.setAvoid((short) stat.getValue().intValue());
                } else if (key.equals("Speed")) {
                    item.eq.setSpeed((short) stat.getValue().intValue());
                } else if (key.equals("Jump")) {
                    item.eq.setJump((short) stat.getValue().intValue());
                } else if (key.equals("MHP")) {
                    item.eq.setHp(GameConstants.getHpMp(itemId, stat.getValue().intValue()));
                } else if (key.equals("MMP")) {
                    item.eq.setMp(GameConstants.getHpMp(itemId, stat.getValue().intValue()));
                } else if (key.equals("tuc")) {
                    item.eq.setUpgradeSlots(stat.getValue().byteValue());
                } else if (key.equals("Craft")) {
                    item.eq.setHands(stat.getValue().shortValue());
                } else if (key.equals("durability")) {
                    item.eq.setDurability(stat.getValue().intValue());
                } else if (key.equals("charmEXP")) {
                    item.eq.setCharmEXP(stat.getValue().shortValue());
                } else if (key.equals("PVPDamage")) {
                    item.eq.setPVPDamage(stat.getValue().shortValue());
                } else if (key.equals("MHPr")) {
                    item.eq.setHpR((short) stat.getValue().intValue());
                } else if (key.equals("MMPr")) {
                    item.eq.setMpR((short) stat.getValue().intValue());
                }
            }

            if (item.equipStats.get("cash") != null && item.eq.getCharmEXP() <= 0) { //set the exp
                short exp = 0;
                int identifier = itemId / 10000;
                if (GameConstants.isWeapon(itemId) || identifier == 106) { //weapon overall
                    exp = 60;
                } else if (identifier == 100) { //hats
                    exp = 50;
                } else if (GameConstants.isAccessory(itemId) || identifier == 102 || identifier == 108 || identifier == 107) { //gloves shoes accessory
                    exp = 40;
                } else if (identifier == 104 || identifier == 105 || identifier == 110) { //top bottom cape
                    exp = 30;
                }
                item.eq.setCharmEXP(exp);
            }
        }
    }

    public void initItemInformation(ResultSet sqlItemData) throws SQLException {
        final ItemInformation ret = new ItemInformation();
        final int itemId = sqlItemData.getInt("itemid");
        ret.itemId = itemId;
        ret.slotMax = GameConstants.getSlotMax(itemId) > 0 ? GameConstants.getSlotMax(itemId) : sqlItemData.getShort("slotMax");
        ret.price = Double.parseDouble(sqlItemData.getString("price"));
        ret.wholePrice = sqlItemData.getInt("wholePrice");
        ret.option1 = sqlItemData.getInt("option1");
        ret.option2 = sqlItemData.getInt("option2");
        ret.option3 = sqlItemData.getInt("option3");
        ret.stateChange = sqlItemData.getInt("stateChange");
        ret.name = sqlItemData.getString("name");
        ret.desc = sqlItemData.getString("desc");
        ret.msg = sqlItemData.getString("msg");

        ret.flag = sqlItemData.getInt("flags");

        ret.karmaEnabled = sqlItemData.getByte("karma");
        ret.meso = sqlItemData.getInt("meso");
        ret.monsterBook = sqlItemData.getInt("monsterBook");
        ret.itemMakeLevel = sqlItemData.getShort("itemMakeLevel");
        ret.questId = sqlItemData.getInt("questId");
        ret.create = sqlItemData.getInt("create");
        ret.replaceItem = sqlItemData.getInt("replaceId");
        ret.replaceMsg = sqlItemData.getString("replaceMsg");
        ret.afterImage = sqlItemData.getString("afterImage");
        ret.cardSet = 0;

        if (ret.monsterBook > 0 && itemId / 10000 == 238) {
            mobIds.put(ret.monsterBook, itemId);
            for (Entry<Integer, Triple<Integer, List<Integer>, List<Integer>>> set : monsterBookSets.entrySet()) {
                if (set.getValue().mid.contains(itemId)) {
                    ret.cardSet = set.getKey();
                    break;
                }
            }
        }

        final String scrollRq = sqlItemData.getString("scrollReqs");
        if (scrollRq.length() > 0) {
            ret.scrollReqs = new ArrayList<Integer>();
            final String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.scrollReqs.add(Integer.parseInt(s));
                }
            }
        }
        final String consumeItem = sqlItemData.getString("consumeItem");
        if (consumeItem.length() > 0) {
            ret.questItems = new ArrayList<Integer>();
            final String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.questItems.add(Integer.parseInt(s));
                }
            }
        }

        ret.totalprob = sqlItemData.getInt("totalprob");
        final String incRq = sqlItemData.getString("incSkill");
        if (incRq.length() > 0) {
            ret.incSkill = new ArrayList<Integer>();
            final String[] scroll = incRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.incSkill.add(Integer.parseInt(s));
                }
            }
        }
        dataCache.put(itemId, ret);
    }

    public boolean isEquip(int itemId) {
        return itemId / 1000000 == 1;

    }

    public int getScriptedItemNpc(int itemId) {
        if (scriptedItemCache.containsKey(itemId)) {
            return scriptedItemCache.get(itemId);
        }
        int npcId = MapleDataTool.getInt("spec/npc", getItemData(itemId), 0);
        scriptedItemCache.put(itemId, npcId);
        return scriptedItemCache.get(itemId);
    }

    public String getScriptedItemScript(int itemId) {
        if (scriptedItemScriptCache.containsKey(itemId)) {
            return scriptedItemScriptCache.get(itemId);
        }
        String script = MapleDataTool.getString("spec/script", getItemData(itemId));
        scriptedItemScriptCache.put(itemId, script);
        return scriptedItemScriptCache.get(itemId);
    }

    public final List<Pair<Integer, String>> getScroll() {
        final List<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
        MapleData itemsData;
        itemsData = stringData.getData("Consume.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int v1 = (Integer.parseInt(itemFolder.getName()) / 10000);
            boolean v2 = true;
            if (v1 != 204) {
                continue;
            }
            itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
        }
        return itemPairs;
    }

    public List<Integer> getFindScroll(String percent) {
        List<Integer> scroll = new ArrayList<>();
        for (Pair<Integer, String> pairs : getScroll()) {
            int v1 = (pairs.left / 10000);
            if (v1 != 204) {
                continue;
            }
            if (!pairs.right.contains(percent)) {
                continue;
            }
            scroll.add(pairs.left);
        }
        return scroll;
    }

    public List<Integer> getFindHair(int color, int gender, String text, boolean search) {
        List<Integer> hairs = new ArrayList<>();
        String a;
        for (Pair<Integer, String> pairs : getEqItemss()) {
            if (pairs.left % 10 != color) {
                continue;
            }
            if (search && !pairs.right.contains(text)) {
                continue;
            }
            int checkCode = (pairs.left / 10000);
            a = ((pairs.left / 1000) + "").substring(1, 2);
            int b = Integer.parseInt(a);
            if (checkCode == 3 || checkCode == 4 || checkCode == 6) {
                if (pairs.right.contains("머리") || pairs.right.contains("헤어")) {
                    if (gender == 0) {
                        if (b != 0 && b != 3 && b != 6) {
                            continue;
                        }
                    } else {
                        if (b != 1 && b != 4 && b != 5 && b != 7 && b != 8) {
                            continue;
                        }
                    }
                    hairs.add(pairs.left);
                }
            }
        }
        return hairs;
    }

    public List<Integer> getFindFace(int color, String text, boolean search) {
        List<Integer> faces = new ArrayList<>();
        for (Pair<Integer, String> pairs : getEqItemss()) {
            if (pairs.left % 1000 - (pairs.left % 100) != color) {
                continue;
            }
            if (search && !pairs.right.contains(text)) {
                continue;
            }
            int checkCode = (pairs.left / 10000);
            if (checkCode == 2 || checkCode == 5) {
                if (pairs.right.contains("얼굴")) {
                    faces.add(pairs.left);
                }
            }
        }
        return faces;
    }

    public final List<Pair<Integer, String>> getEqItemss() {
        final List<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
        MapleData itemsData;

        itemsData = stringData.getData("Eqp.img").getChildByPath("Eqp");
        for (MapleData eqpType : itemsData.getChildren()) {
            for (MapleData itemFolder : eqpType.getChildren()) {
                itemPairs.add(new Pair<Integer, String>(Integer.parseInt(itemFolder.getName()), MapleDataTool.getString("name", itemFolder, "NO-NAME")));
            }
        }
        return itemPairs;
    }

    public void loadHairFace() {
        for (int i = 0; i < 8; i++) {
            this.hairCache.put(i, new ArrayList<>());
        }
        for (int i = 10; i < 18; i++) {
            this.faceCache.put(i, new ArrayList<>());
        }
        MapleData itemsData = stringData.getData("Eqp.img").getChildByPath("Eqp");
        for (MapleData eqpType : itemsData.getChildren()) {
            if (eqpType.getName().equals("Hair") || eqpType.getName().equals("Face")) {
                for (MapleData itemFolder : eqpType.getChildren()) {
                    int val = Integer.parseInt(itemFolder.getName());
                    if (eqpType.getName().equals("Hair")) {
                        if (val % 10 >= this.hairCache.size()) {
                            continue;
                        }
                        int idx = val % 10;
                        if (idx < 0) {
                            idx += 10;
                        }
                        hairCache.get(idx).add(Integer.parseInt(itemFolder.getName()));
                    } else {
                        if (val % 1000 >= this.faceCache.size() * 100) {
                            continue;
                        }
                        int idx = val % 1000 / 100;
                        if (idx < 10) {
                            idx += 10;
                        }
                        faceCache.get(idx).add(Integer.parseInt(itemFolder.getName()));
                    }
                }
            }
        }
    }

    public List<Integer> getlHair(int i) {
        List<Integer> lHair = new ArrayList<>(hairCache.get(i));
        Collections.sort(lHair);
        return lHair;
    }

    public List<Integer> getlFace(int i) {
        List<Integer> lFace = new ArrayList<>(faceCache.get(i));
        Collections.sort(lFace);
        return lFace;
    }

    public final String getBookEpisode(int monsterID) {
        MapleData itemsData = stringData.getData("MonsterBook.img").getChildByPath(monsterID + "");
        if (itemsData != null) {
            return MapleDataTool.getString("episode", itemsData, "NO-NAME");
        }
        return null;
    }

    public final String getBookMap(int monsterID) {
        List bookMap = new ArrayList<>();
        MapleData itemsData = stringData.getData("MonsterBook.img").getChildByPath(monsterID + "");
        if (itemsData != null) {
            for (MapleData mapList : itemsData.getChildByPath("map")) {
                bookMap.add((int) mapList.getData());
            }
        }
        String say = "#Cyellow#출몰 지역";
        for (int i = 0; i < bookMap.size(); i++) {
            String mapName = ("#m" + bookMap.get(i) + "#");
            say += "\r\n#Cgray#" + (i + 1) + ". " + mapName;
        }
        return say;
    }

    public final String getBookReward(int monsterID) {
        List bookReward = new ArrayList<>();
        MapleData itemsData = stringData.getData("MonsterBook.img").getChildByPath(monsterID + "");
        if (itemsData != null) {
            for (MapleData mapList : itemsData.getChildByPath("reward")) {
                bookReward.add((int) mapList.getData());
            }
        }
        String say = "#Cyellow#전리품";
        for (int i = 0; i < bookReward.size(); i++) {
            String rewardName = ("#z" + bookReward.get(i) + "#");
            say += "\r\n#Cgray#" + (i + 1) + ". " + rewardName;
        }
        return say;
    }
}
