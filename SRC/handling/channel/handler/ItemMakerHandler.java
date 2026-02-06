/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.channel.handler;

import client.MapleCharacter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import client.inventory.Item;
import client.inventory.Equip;
import client.SkillFactory;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleTrait.MapleTraitType;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory.CraftingEntry;
import client.inventory.ItemFlag;
import client.inventory.MapleImp;
import client.inventory.MapleImp.ImpFlag;
import client.inventory.MapleInventoryType;
import client.messages.MessageType;
import constants.GameConstants;
import handling.world.World;
import java.util.HashMap;
import java.util.Map.Entry;
import server.ItemMakerFactory;
import server.ItemMakerFactory.GemCreateEntry;
import server.ItemMakerFactory.ItemMakerCreateEntry;
import server.Randomizer;
import server.MapleItemInformationProvider;
import server.MapleInventoryManipulator;
import server.MapleStatEffect;
import server.maps.MapleExtractor;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.Pair;
import tools.packet.CField;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.EffectPacket;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;

public class ItemMakerHandler {

    private static final Map<String, Integer> craftingEffects = new HashMap<String, Integer>();

    static {
        craftingEffects.put("Effect/BasicEff.img/professions/herbalism", 92000000);
        craftingEffects.put("Effect/BasicEff.img/professions/mining", 92010000);
        craftingEffects.put("Effect/BasicEff.img/professions/herbalismExtract", 92000000);
        craftingEffects.put("Effect/BasicEff.img/professions/miningExtract", 92010000);
        craftingEffects.put("Effect/BasicEff.img/professions/equip_product", 92020000);
        craftingEffects.put("Effect/BasicEff.img/professions/acc_product", 92030000);
        craftingEffects.put("Effect/BasicEff.img/professions/alchemy", 92040000);
    }

    public static enum CraftRanking {

        SOSO(19, 30),
        GOOD(20, 40),
        COOL(21, 50);
        
        public int i, craft;

        private CraftRanking(int i, int craft) {
            this.i = i;
            this.craft = craft;
        }
    }

    public static final void ItemMaker(final LittleEndianAccessor slea, final MapleClient c) {
        //  System.out.println("왜안댐"); //change?
        final int makerType = slea.readInt();

        switch (makerType) {
            case 1: { // Gem
                final int toCreate = slea.readInt();

                if (GameConstants.isGem(toCreate)) {
                    final GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(toCreate);
                    if (gem == null) {
                        return;
                    }
                    if (!hasSkill(c, gem.getReqSkillLevel())) {
                        return; // H4x
                    }
                    if (c.getPlayer().getMeso() < gem.getCost()) {
                        return; // H4x
                    }
                    final int randGemGiven = getRandomGem(gem.getRandomReward());

                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(randGemGiven)).isFull()) {
                        return; // We'll do handling for this later
                    }
                    final int taken = checkRequiredNRemove(c, gem.getReqRecipes());
                    if (taken == 0) {
                        return; // We'll do handling for this later
                    }
                    c.getPlayer().gainMeso(-gem.getCost(), false);
                    MapleInventoryManipulator.addById(c, randGemGiven, (byte) (taken == randGemGiven ? 9 : 1), "Made by Gem " + toCreate + " on " + FileoutputUtil.CurrentReadable_Date()); // Gem is always 1

                    c.getSession().write(EffectPacket.ItemMaker_Success());
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                    c.sendPacket(CUserLocal.makerSkillResult(true, 1, toCreate, 1, gem.getReqRecipes(), 0, null, false, 0, gem.getCost()));
                } else if (GameConstants.isOtherGem(toCreate)) {
                    //non-gems that are gems
                    //stim and numEnchanter always 0
                    final GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(toCreate);
                    if (gem == null) {
                        return;
                    }
                    if (!hasSkill(c, gem.getReqSkillLevel())) {
                        return; // H4x
                    }
                    if (c.getPlayer().getMeso() < gem.getCost()) {
                        return; // H4x
                    }

                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
                        return; // We'll do handling for this later
                    }
                    if (checkRequiredNRemove(c, gem.getReqRecipes()) == 0) {
                        return; // We'll do handling for this later
                    }
                    c.getPlayer().gainMeso(-gem.getCost(), false);
                    if (GameConstants.getInventoryType(toCreate) == MapleInventoryType.EQUIP) {
                        MapleInventoryManipulator.addbyItem(c, MapleItemInformationProvider.getInstance().getEquipById(toCreate));
                    } else {
                        MapleInventoryManipulator.addById(c, toCreate, (byte) 1, "Made by Gem " + toCreate + " on " + FileoutputUtil.CurrentReadable_Date()); // Gem is always 1
                    }

                    c.getSession().write(EffectPacket.ItemMaker_Success());
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                    c.sendPacket(CUserLocal.makerSkillResult(true, 1, toCreate, 1, gem.getReqRecipes(), 0, null, false, 0, gem.getCost()));
                } else {
                    final boolean stimulator = slea.readByte() > 0;
                    final int numEnchanter = slea.readInt();

                    final ItemMakerCreateEntry create = ItemMakerFactory.getInstance().getCreateInfo(toCreate);
                    if (create == null) {
                        return;
                    }
                    if (numEnchanter > create.getTUC()) {
                        return; // h4x
                    }
                    if (!hasSkill(c, create.getReqSkillLevel())) {
                        return; // H4x
                    }
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
                        return; // We'll do handling for this later
                    }
                    if (checkRequiredNRemove(c, create.getReqItems()) == 0) {
                        return; // We'll do handling for this later
                    }
                    int totalCost = create.getCost();

                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    final Equip toGive = (Equip) ii.getEquipById(toCreate);
                    int baseCost = 0;
                    int reqLevel = ii.getReqLevel(toCreate);
                    baseCost = getEnchantAddCost(toCreate, reqLevel, baseCost);

                    int[] enchanters = new int[numEnchanter];
                    if (stimulator || numEnchanter > 0) {
                        for (int i = 0; i < numEnchanter; i++) {
                            enchanters[i] = slea.readInt();
                        }
                        for (int enchant : enchanters) {
                            int gemQuality = enchant % 10;
                            int enchantCost = 0;
                            if (gemQuality == 0)// 하급
                            {
                                enchantCost = baseCost * 1;
                            } else if (gemQuality == 1)// 중급
                            {
                                enchantCost = baseCost * 2;
                            } else if (gemQuality == 2)// 상급
                            {
                                enchantCost = baseCost * 3 + 1000;
                            }
                            totalCost += enchantCost;
                        }
                    }

                    if (c.getPlayer().getMeso() >= totalCost) {
                        c.getPlayer().gainMeso(-totalCost, false);
                    } else {
                        c.getPlayer().dropMessage(1, "메소가 " + (totalCost - c.getPlayer().getMeso()) + "메소 부족합니다.");
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }
                    if (stimulator || numEnchanter > 0) {
                        for (int enchant : enchanters) {
                            if (c.getPlayer().haveItem(enchant, 1, false, true)) {
                                final Map<String, Integer> stats = ii.getEquipStats(enchant);
                                if (stats != null) {
                                    addEnchantStats(stats, toGive);
                                    MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, enchant, 1, false, false);
                                }
                            }
                        }
                        if (c.getPlayer().haveItem(create.getStimulator(), 1, false, true)) {
                            ii.randomizeStats_Above(toGive);
                            MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, create.getStimulator(), 1, false, false);
                        }
                    }
                    if (!stimulator || Randomizer.nextInt(10) != 0) {
                        MapleInventoryManipulator.addbyItem(c, toGive);
                        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                    } else {
                        c.getPlayer().dropMessage(5, "The item was overwhelmed by the stimulator.");
                    }
                    c.getSession().write(EffectPacket.ItemMaker_Success());
                    c.sendPacket(CUserLocal.makerSkillResult(true, 1, toGive.getItemId(), 1, create.getReqItems(), numEnchanter, enchanters, stimulator, create.getStimulator(), totalCost));
                }
                break;
            }
            case 3: { // Making Crystals
                final int etc = slea.readInt();
                if (c.getPlayer().haveItem(etc, 100, false, true)) {

                    MapleInventoryManipulator.addById(c, createCrystal(etc), (short) 1, "Made by Maker " + etc + " on " + FileoutputUtil.CurrentReadable_Date());
                    if (Randomizer.nextInt(10) < 4) {
                        MapleInventoryManipulator.addById(c, 4310000, (short) 1, "Made by Maker " + etc + " on " + FileoutputUtil.CurrentReadable_Date());
                        c.getPlayer().dropMessage(4310000, " 절대 음감 합성에 성공하셨습니다!");
                    } else {
                        c.getPlayer().dropMessage(4310000, " 절대 음감 합성에 실패하셨습니다.");
                    }
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, etc, 100, false, false);

                    c.getSession().write(EffectPacket.ItemMaker_Success());
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                    c.sendPacket(CUserLocal.makerSkillResult(true, 3, etc, 0, null, 0, null, false, 0, 0));
                }
                break;
            }
            case 4: { // Disassembling EQ.
                final int itemId = slea.readInt();
                c.getPlayer().updateTick(slea.readInt());
                final byte slot = (byte) slea.readInt();

                final Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot);
                if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
                    return;
                }
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

                if (!ii.isDropRestricted(itemId) && !ii.isAccountShared(itemId)) {
                    final int[] toGive = getCrystal(itemId, ii.getReqLevel(itemId));
                    MapleInventoryManipulator.addById(c, toGive[0], (byte) toGive[1], "Made by disassemble " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, slot, (byte) 1, false);
                }
                c.getSession().write(EffectPacket.ItemMaker_Success());
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), EffectPacket.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                break;
            }
        }
    }

    public static final int createCrystal(final int etc) {
        int itemid;
        final short level = MapleItemInformationProvider.getInstance().getItemMakeLevel(etc);

        if (level >= 1 && level <= 50) { // 확장
            itemid = 4260000;
        } else if (level >= 51 && level <= 60) {
            itemid = 4260001;
        } else if (level >= 61 && level <= 70) {
            itemid = 4260002;
        } else if (level >= 71 && level <= 80) {
            itemid = 4260003;
        } else if (level >= 81 && level <= 90) {
            itemid = 4260004;
        } else if (level >= 91 && level <= 100) {
            itemid = 4260005;
        } else if (level >= 101 && level <= 110) {
            itemid = 4260006;
        } else if (level >= 111 && level <= 120) {
            itemid = 4260007;
        } else if (level >= 121) {
            itemid = 4260008;
        } else {
            throw new RuntimeException("Invalid Item Maker id");
        }
        return itemid;
    }

    private static final int[] getCrystal(final int itemid, final int level) {
        int[] all = new int[2];
        all[0] = -1;
        if (level >= 1 && level <= 50) { // 확장
            all[0] = 4260000;
        } else if (level >= 51 && level <= 60) {
            all[0] = 4260001;
        } else if (level >= 61 && level <= 70) {
            all[0] = 4260002;
        } else if (level >= 71 && level <= 80) {
            all[0] = 4260003;
        } else if (level >= 81 && level <= 90) {
            all[0] = 4260004;
        } else if (level >= 91 && level <= 100) {
            all[0] = 4260005;
        } else if (level >= 101 && level <= 110) {
            all[0] = 4260006;
        } else if (level >= 111 && level <= 120) {
            all[0] = 4260007;
        } else if (level >= 121 && level <= 200) {
            all[0] = 4260008;
        } else {
            throw new RuntimeException("Invalid Item Maker type" + level);
        }
        if (GameConstants.isWeapon(itemid) || GameConstants.isOverall(itemid)) {
            all[1] = Randomizer.rand(5, 11);
        } else {
            all[1] = Randomizer.rand(3, 7);
        }
        return all;
    }

    private static final void addEnchantStats(final Map<String, Integer> stats, final Equip item) {
        Integer s = stats.get("PAD");
        if (s != null && s != 0) {
            item.setWatk((short) (item.getWatk() + s));
        }
        s = stats.get("MAD");
        if (s != null && s != 0) {
            item.setMatk((short) (item.getMatk() + s));
        }
        s = stats.get("ACC");
        if (s != null && s != 0) {
            item.setAcc((short) (item.getAcc() + s));
        }
        s = stats.get("EVA");
        if (s != null && s != 0) {
            item.setAvoid((short) (item.getAvoid() + s));
        }
        s = stats.get("Speed");
        if (s != null && s != 0) {
            item.setSpeed((short) (item.getSpeed() + s));
        }
        s = stats.get("Jump");
        if (s != null && s != 0) {
            item.setJump((short) (item.getJump() + s));
        }
        s = stats.get("MaxHP");
        if (s != null && s != 0) {
            item.setHp((short) (item.getHp() + s));
        }
        s = stats.get("MaxMP");
        if (s != null && s != 0) {
            item.setMp((short) (item.getMp() + s));
        }
        s = stats.get("STR");
        if (s != null && s != 0) {
            item.setStr((short) (item.getStr() + s));
        }
        s = stats.get("DEX");
        if (s != null && s != 0) {
            item.setDex((short) (item.getDex() + s));
        }
        s = stats.get("INT");
        if (s != null && s != 0) {
            item.setInt((short) (item.getInt() + s));
        }
        s = stats.get("LUK");
        if (s != null && s != 0) {
            item.setLuk((short) (item.getLuk() + s));
        }
        s = stats.get("randOption");
        if (s != null && s != 0) {
            final int ma = item.getMatk(), wa = item.getWatk();
            if (wa > 0) {
                item.setWatk((short) (Randomizer.nextBoolean() ? (wa + s) : (wa - s)));
            }
            if (ma > 0) {
                item.setMatk((short) (Randomizer.nextBoolean() ? (ma + s) : (ma - s)));
            }
        }
        s = stats.get("randStat");
        if (s != null && s != 0) {
            final int str = item.getStr(), dex = item.getDex(), luk = item.getLuk(), int_ = item.getInt();
            if (str > 0) {
                item.setStr((short) (Randomizer.nextBoolean() ? (str + s) : (str - s)));
            }
            if (dex > 0) {
                item.setDex((short) (Randomizer.nextBoolean() ? (dex + s) : (dex - s)));
            }
            if (int_ > 0) {
                item.setInt((short) (Randomizer.nextBoolean() ? (int_ + s) : (int_ - s)));
            }
            if (luk > 0) {
                item.setLuk((short) (Randomizer.nextBoolean() ? (luk + s) : (luk - s)));
            }
        }
    }

    private static final int getRandomGem(final List<Pair<Integer, Integer>> rewards) {
        int itemid;
        final List<Integer> items = new ArrayList<Integer>();

        for (final Pair p : rewards) {
            itemid = (Integer) p.getLeft();
            for (int i = 0; i < (Integer) p.getRight(); i++) {
                items.add(itemid);
            }
        }
        return items.get(Randomizer.nextInt(items.size()));
    }

    private static final int checkRequiredNRemove(final MapleClient c, final List<Pair<Integer, Integer>> recipe) {
        int itemid = 0;
        for (final Pair<Integer, Integer> p : recipe) {
            if (!c.getPlayer().haveItem(p.getLeft(), p.getRight(), false, true)) {
                return 0;
            }
        }
        for (final Pair<Integer, Integer> p : recipe) {
            itemid = p.getLeft();
            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemid), itemid, p.getRight(), false, false);
        }
        return itemid;
    }

    private static final boolean hasSkill(final MapleClient c, final int reqlvl) {
        return c.getPlayer().getSkillLevel(SkillFactory.getSkill(c.getPlayer().getStat().getSkillByJob(1007, c.getPlayer().getJob()))) >= reqlvl;
    }

    public static final void UseRecipe(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 251) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
    }

    public static final void MakeExtractor(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final int itemId = slea.readInt();
        final int fee = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        if (toUse == null || toUse.getQuantity() < 1 || itemId / 10000 != 304 || fee <= 0 || chr.getExtractor() != null || !chr.getMap().isTown()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        chr.setExtractor(new MapleExtractor(chr, itemId, fee, chr.getFH()));
        chr.getMap().spawnExtractor(chr.getExtractor());
    }

    public static final void LeaveExtractor(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        chr.removeExtractor();
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void UseBag(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.ETC).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 433) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        boolean firstTime = !chr.getExtendedSlots().contains(itemId);
        if (firstTime) {
            chr.getExtendedSlots().add(itemId);
            chr.changedExtended();
            short flag = toUse.getFlag();
            flag |= ItemFlag.PreventColdness.getValue();
            toUse.setFlag(flag);
            c.getSession().write(InventoryPacket.updateSpecialItemUse(toUse, (byte) 4, toUse.getPosition(), true, chr));
        }
        c.sendPacket(CUserLocal.useBagResult(chr.getExtendedSlots().indexOf(itemId), itemId, firstTime, false));
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void StartHarvest(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        //its ok if a hacker bypasses this as we do everything in the reactor anyway
        final MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(slea.readInt());
        if (reactor == null || !reactor.isAlive() || reactor.getReactorId() > 200011 || chr.getStat().harvestingTool <= 0 || reactor.getTruePosition().distanceSq(chr.getTruePosition()) > 10000 || c.getPlayer().getFatigue() >= 200 || !c.getPlayer().isAlive()) {
            return;
        }
        Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) c.getPlayer().getStat().harvestingTool);
        if (item == null || ((Equip) item).getDurability() == 0) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            return;
        }
        c.sendPacket(CUserLocal.gatherRequestResult(reactor.getObjectId(), 11));
        c.getPlayer().getMap().broadcastMessage(chr, CField.showHarvesting(c, item.getItemId()), false);
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void StopHarvest(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        //its ok if a hacker bypasses this as we do everything in the reactor anyway
        /*final MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(slea.readInt());
        if (reactor == null || !reactor.isAlive() || reactor.getReactorId() > 200011 || chr.getStat().harvestingTool <= 0 || reactor.getTruePosition().distanceSq(chr.getTruePosition()) > 40000.0 || reactor.getState() < 3 || c.getPlayer().getFatigue() >= 100) { //bug in global, so we use this to bug fix
        return;
        }
        Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) c.getPlayer().getStat().harvestingTool);
        if (item == null || ((Equip) item).getDurability() == 0) {
        c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
        return;
        }
        c.getPlayer().getMap().destroyReactor(reactor.getObjectId());
        ReactorScriptManager.getInstance().act(c, reactor);*/
    }

    public static void handleUserRequestInstanceTable(final LittleEndianAccessor iPacket, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        String requestStr = iPacket.readMapleAsciiString();
        int type = iPacket.DecodeInt();
        int subType = iPacket.DecodeInt();
        int skillID = Integer.parseInt(requestStr);
        int perc = 100;
        switch (skillID) {
            case 92000000:
            case 92010000: {
                perc = 100;
                break;
            }
            case 92020000: 
            case 92030000: 
            case 92040000: {
                int recommandedSkillLevel = (type + 1);
                int skillLevel = user.getProfessionLevel(skillID);
                perc = Math.max(0, 100 - (recommandedSkillLevel - skillLevel) * 20);
                if (perc > 99) {
                    perc = 100;
                }
                break;
            }
        }
        c.sendPacket(CWvsContext.professionInfo(requestStr, type, subType, true, perc));
    }

    public static final void CraftEffect(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr.getMapId() != 910001000 && chr.getMap().getExtractorSize() <= 0) {
            return;
        }
        final String effect = slea.readMapleAsciiString();
        final Integer profession = craftingEffects.get(effect);
        if (profession != null && (c.getPlayer().getProfessionLevel(profession.intValue()) > 0 || (profession == 92040000 && chr.getMap().getExtractorSize() > 0))) {
            int a1 = slea.readInt();
            if (a1 > 6000 || a1 < 3000) {
                a1 = 4000;
            }
            int a2 = slea.readInt();
            c.getSession().write(EffectPacket.showOwnCraftingEffect(effect, a1, a2));
            chr.getMap().broadcastMessage(chr, EffectPacket.showCraftingEffect(chr.getId(), effect, a1, a2), false);
        }
    }

    public static final void CraftMake(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr.getMapId() != 910001000 && chr.getMap().getExtractorSize() < 1) {
            return;
        }
        int a1 = slea.readInt();
        int a2 = slea.readInt();
        if (a2 > 6000 || a2 < 3000) {
            a2 = 4000;
        }
        chr.getMap().broadcastMessage(CField.craftMake(chr.getId(), a1, a2));
    }

    public static final void CraftComplete(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int craftID = slea.readInt();
        final CraftingEntry ce = SkillFactory.getCraft(craftID);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((chr.getMapId() != 910001000 && (craftID != 92049000 || chr.getMap().getExtractorSize() <= 0)) || ce == null || chr.getFatigue() >= 200) {
            return;
        }
        final int theLevl = c.getPlayer().getProfessionLevel((craftID / 10000) * 10000);
        if (theLevl <= 0 && craftID != 92049000) {
            return;
        }
        int toGet = 0, expGain = 0, fatigue = 0;
        short quantity = 1;
        CraftRanking cr = CraftRanking.GOOD;
        if (craftID == 92049000) {
            final int extractorId = slea.readInt();
            final int itemId = slea.readInt();
            final long invId = slea.readLong();
            final int reqLevel = ii.getReqLevel(itemId);
            final Item item = chr.getInventory(MapleInventoryType.EQUIP).findByInventoryId(invId, itemId);
            if (item == null || chr.getInventory(MapleInventoryType.ETC).isFull()) {
                return;
            }
            if (extractorId <= 0 && (theLevl == 0 || theLevl < (reqLevel > 130 ? 6 : ((reqLevel - 30) / 20)))) {
                return;
            } else if (extractorId > 0) {
                final MapleCharacter extract = chr.getMap().getCharacterById(extractorId);
                if (extract == null || extract.getExtractor() == null) {
                    return;
                }
                final MapleExtractor extractor = extract.getExtractor();
                if (extractor.owner != chr.getId()) { //fee
                    if (chr.getMeso() < extractor.fee) {
                        return;
                    }
                    final MapleStatEffect eff = ii.getItemEffect(extractor.itemId);
                    if (eff != null && eff.getUseLevel() < reqLevel) {
                        return;
                    }
                    chr.gainMeso(-extractor.fee, true);
                    final MapleCharacter owner = chr.getMap().getCharacterById(extractor.owner);
                    if (owner != null && owner.getMeso() < (Integer.MAX_VALUE - extractor.fee)) {
                        owner.gainMeso(extractor.fee, false);
                    }
                }
            }
            toGet = 4021016;
            quantity = (short) Randomizer.rand(3, GameConstants.isWeapon(itemId) || GameConstants.isOverall(itemId) ? 11 : 7);
            if (reqLevel <= 60) {
                toGet = 4021013;
            } else if (reqLevel <= 90) {
                toGet = 4021014;
            } else if (reqLevel <= 120) {
                toGet = 4021015;
            }
            if (quantity <= 5) {
                cr = CraftRanking.SOSO;
            }
            if (Randomizer.nextInt(5) == 0 && toGet != 4021016) {
                toGet++;
                quantity = 1;
                cr = CraftRanking.COOL;
            }
            fatigue = 3;
            MapleInventoryManipulator.addById(c, toGet, quantity, "Made by disassemble " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item.getPosition(), (byte) 1, false);
        } else if (craftID == 92049001) {
            final int itemId = slea.readInt();
            final long invId1 = slea.readLong();
            final long invId2 = slea.readLong();
            final int reqLevel = ii.getReqLevel(itemId);
            Equip item1 = (Equip) chr.getInventory(MapleInventoryType.EQUIP).findByInventoryIdOnly(invId1, itemId);
            Equip item2 = (Equip) chr.getInventory(MapleInventoryType.EQUIP).findByInventoryIdOnly(invId2, itemId);
            for (short i = 0; i < chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit(); i++) {
                Item item = chr.getInventory(MapleInventoryType.EQUIP).getItem(i);
                if (item != null && item.getItemId() == itemId && item != item1 && item != item2) {
                    if (item1 == null) {
                        item1 = (Equip) item;
                    } else if (item2 == null) {
                        item2 = (Equip) item;
                        break;
                    }
                }
            }
            if (item1 == null || item2 == null) {
                c.getPlayer().dropMessage(1, "개 수작 no");
                return;
            }
            if (theLevl < (reqLevel > 130 ? 6 : ((reqLevel - 30) / 20))) {
                return;
            }
            int potentialState = 17, potentialChance = (theLevl * 2);
            if (item1.getState() > 0 && item2.getState() > 0) {
                potentialChance = 100;
            } else if (item1.getState() > 0 || item2.getState() > 0) {
                potentialChance *= 2;
            }
            if (item1.getState() == item2.getState() && item1.getState() > 17) {
                potentialState = item1.getState();
            }
            //use average stats if scrolled.
            Equip newEquip = ii.fuse(item1.getLevel() > 0 ? (Equip) ii.getEquipById(itemId) : item1, item2.getLevel() > 0 ? (Equip) ii.getEquipById(itemId) : item2);
            final int newStat = ii.getTotalStat(newEquip);
            if (newStat > ii.getTotalStat(item1) || newStat > ii.getTotalStat(item2)) {
                cr = CraftRanking.COOL;
            } else if (newStat < ii.getTotalStat(item1) || newStat < ii.getTotalStat(item2)) {
                cr = CraftRanking.SOSO;
            }
            if (Randomizer.nextInt(100) < (newEquip.getUpgradeSlots() > 0 || potentialChance >= 100 ? potentialChance : (potentialChance / 2))) {
                newEquip.resetPotential_Fuse(theLevl > 5);
            }
            newEquip.setFlag((short) ItemFlag.Crafted.getValue());
            newEquip.setOwner(chr.getName());
            toGet = newEquip.getItemId();
            expGain = (60 - ((theLevl - 1) * 2));
            fatigue = 3;
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item1.getPosition(), (byte) 1, false);
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item2.getPosition(), (byte) 1, false);
            MapleInventoryManipulator.addbyItem(c, newEquip);
        } else {
            if (ce.needOpenItem && chr.getSkillLevel(craftID) <= 0) {
                return;
            }
            for (Entry<Integer, Integer> e : ce.reqItems.entrySet()) {
                if (!chr.haveItem(e.getKey(), e.getValue())) {
                    return;
                }
            }
            for (Triple<Integer, Integer, Integer> i : ce.targetItems) {
                if (!MapleInventoryManipulator.checkSpace(c, i.left, i.mid, "")) {
                    return;
                }
            }
            for (Entry<Integer, Integer> e : ce.reqItems.entrySet()) {
                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(e.getKey()), e.getKey(), e.getValue(), false, false);
            }
            if (Randomizer.nextInt(100) < (100 - (ce.reqSkillLevel - theLevl) * 20) || (craftID / 10000 <= 9201)) {
                final Map<Skill, SkillEntry> sa = new HashMap<>();
                while (true) {
                    boolean passed = false;
                    for (Triple<Integer, Integer, Integer> i : ce.targetItems) {
                        if (Randomizer.nextInt(100) < i.right) {
                            toGet = i.left;
                            quantity = i.mid.shortValue();
                            Item receive = null;
                            if (GameConstants.getInventoryType(toGet) == MapleInventoryType.EQUIP) {
                                Equip first = (Equip) ii.getEquipById(toGet);
                                if (Randomizer.nextInt(100) < (theLevl * 2)) {
                                    first = (Equip) ii.randomizeStats(first);
                                    cr = CraftRanking.COOL;
                                }
                                if (Randomizer.nextInt(100) < (theLevl * (first.getUpgradeSlots() > 0 ? 2 : 1))) {
                                    first.resetPotential();
                                    cr = CraftRanking.COOL;
                                }
                                receive = first;
                                receive.setFlag((short) ItemFlag.Crafted.getValue());
                            } else {
                                receive = new Item(toGet, (short) 0, quantity, (short) (ItemFlag.Crafted.getValue()));
                            }
                            if (ce.period > 0) {
                                receive.setExpiration(System.currentTimeMillis() + (ce.period * 60000)); //period is in minutes
                            }
                            receive.setOwner(chr.getName());
                            //receive.setGMLog("Crafted from " + craftID + " on " + FileoutputUtil.CurrentReadable_Date());
                            MapleInventoryManipulator.addFromDrop(c, receive, true, false);
                            if (ce.needOpenItem) {
                                byte mLevel = chr.getMasterLevel(craftID);
                                if (mLevel == 1) {
                                    sa.put(ce, new SkillEntry(0, (byte) 0, SkillFactory.getDefaultSExpiry(ce)));
                                } else if (mLevel > 1) {
                                    sa.put(ce, new SkillEntry(Integer.MAX_VALUE, (byte) (chr.getMasterLevel(craftID) - 1), SkillFactory.getDefaultSExpiry(ce)));
                                }
                            }
                            fatigue = ce.incFatigability;
                            expGain = ce.incSkillProficiency == 0 ? (((fatigue * 20) - (ce.reqSkillLevel - theLevl) * 2)) : ce.incSkillProficiency;
                            chr.getTrait(MapleTraitType.craft).addExp(cr.craft, chr);
                            passed = true;
                            break;
                        }
                    }
                    if (passed) {
                        break;
                    }
                }
                chr.changeSkillsLevel(sa);
            } else {
                quantity = 0;
                cr = CraftRanking.SOSO;
            }
        }
        if (expGain > 0 && theLevl < 10) {
            expGain *= chr.getClient().getChannelServer().getTraitRate();
            if (Randomizer.nextInt(100) < chr.getTrait(MapleTraitType.craft).getLevel() / 5) {
                expGain *= 2;
            }
            String s = "Alchemy";
            switch (craftID / 10000) {
                case 9200:
                    s = "Herbalism";
                    break;
                case 9201:
                    s = "Mining";
                    break;
                case 9202:
                    s = "Smithing";
                    break;
                case 9203:
                    s = "Accessory Crafting";
                    break;
            }
            chr.dropMessage(-5, s + "'s mastery increased. (+" + expGain + ")");
            if (chr.addProfessionExp((craftID / 10000) * 10000, expGain)) {
                chr.dropMessage(-5, s + " has gained a level.");
            }
        } else {
            expGain = 0;
        }
        MapleQuest.getInstance(2550).forceStart(c.getPlayer(), 9031000, "1");
        chr.setFatigue((byte) (chr.getFatigue() + fatigue));
        chr.getMap().broadcastMessage(CField.craftFinished(chr.getId(), craftID, cr.i, toGet, quantity, expGain));
    }

    public static final void handleUseItemPot(final LittleEndianAccessor iPacket, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        final int itemID = iPacket.readInt();
        final Item pSlot = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(iPacket.readShort());
        if (pSlot == null) {
            return;
        }
        if (pSlot.getQuantity() < 1) {
            return;
        }
        if (itemID != pSlot.getItemId()) {
            return;
        }
        for (int i = 0; i < user.getImps().length; i++) {
            if (user.getImps()[i] == null) {
                if (i == 3 && user.getProfessions().isEmpty()) {
                    return;
                }
                user.getImps()[i] = new MapleImp(itemID);
                c.sendPacket(CWvsContext.itemPotChanged(user.getImps()[i], ImpFlag.SUMMONED.getValue(), i, false));
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, pSlot.getPosition(), (short) 1, false, false);
                return;
            }
        }
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void handleClearItemPot(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        final int indexByPot = (slea.readInt() - 1);
        if (indexByPot < 0) {
            return;
        }
        if (indexByPot > (user.getImps().length - 1)) {
            return;
        }
        if (user.getImps()[indexByPot] == null) {
            return;
        }
        c.sendPacket(CWvsContext.itemPotChanged(user.getImps()[indexByPot], ImpFlag.REMOVED.getValue(), indexByPot, false));
        user.getImps()[indexByPot] = null;
    }

    public static final void handleFeedItemPot(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int itemID = slea.readInt();
        final MapleInventoryType mType = GameConstants.getInventoryType(itemID);
        final Item pSlot = user.getInventory(mType).getItem((short) slea.readInt());
        if (pSlot == null) {
            return;
        }
        if (pSlot.getQuantity() < 1) {
            return;
        }
        if (itemID != pSlot.getItemId()) {
            return;
        }
        final int indexByPot = (slea.readInt() - 1);
        if (indexByPot < 0) {
            return;
        }
        if (indexByPot > (user.getImps().length - 1)) {
            return;
        }
        final MapleImp iPot = user.getImps()[indexByPot];
        if (iPot == null) {
            return;
        }
        if (iPot.getLevel() > (ii.getPot(iPot.getItemId()).right - 2)) {
            return;
        }
        if (iPot.getState() != 1) {
            return;
        }
        int mask = ImpFlag.FULLNESS.getValue();
        mask |= ImpFlag.FULLNESS_2.getValue();
        mask |= ImpFlag.UPDATE_TIME.getValue();
        mask |= ImpFlag.AWAKE_TIME.getValue();
        iPot.setFullness(iPot.getFullness() + (100 * (mType == MapleInventoryType.EQUIP ? 2 : 1)));
        if (Randomizer.nextBoolean()) {
            mask |= ImpFlag.CLOSENESS.getValue();
            iPot.setCloseness(iPot.getCloseness() + 1 + (Randomizer.nextInt(5 * (mType == MapleInventoryType.EQUIP ? 2 : 1))));
        } else if (Randomizer.nextInt(10) == 0) {
            iPot.setState(4);
            mask |= ImpFlag.STATE.getValue();
        }
        if (iPot.getFullness() > 999) {
            iPot.setState(1);
            iPot.setFullness(0);
            iPot.setLevel(iPot.getLevel() + 1);
            mask |= ImpFlag.SUMMONED.getValue();
            if (iPot.getLevel() > (ii.getPot(iPot.getItemId()).right - 2)) {
                iPot.setState(5);
            }
        }
        MapleInventoryManipulator.removeFromSlot(c, mType, pSlot.getPosition(), (short) 1, false, false);
        c.sendPacket(CWvsContext.itemPotChanged(iPot, mask, indexByPot, false));
    }

    public static final void handleCureItemPot(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        final int itemID = slea.readInt();
        final Item pSlot = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((short) slea.readInt());
        if (pSlot == null) {
            return;
        }
        if (pSlot.getQuantity() < 1) {
            return;
        }
        if (itemID != pSlot.getItemId()) {
            return;
        }
        final int indexByPot = (slea.readInt() - 1);
        if (indexByPot < 0) {
            return;
        }
        if (indexByPot > (user.getImps().length - 1)) {
            return;
        }
        if (user.getImps()[indexByPot] == null) {
            return;
        }
        if (user.getImps()[indexByPot].getState() != 4) {
            return;
        }
        user.getImps()[indexByPot].setState(1);
        c.sendPacket(CWvsContext.itemPotChanged(c.getPlayer().getImps()[indexByPot], ImpFlag.STATE.getValue(), indexByPot, false));
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, pSlot.getPosition(), (short) 1, false, false);
    }

    public static final void handleRewardItemPot(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user != null) {
            user.dropMessage(1, "'아이템 팟'은 현재 점검 중에 있습니다.");
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int indexByPot = (slea.readInt() - 1);
        if (indexByPot < 0) {
            return;
        }
        if (indexByPot > (user.getImps().length - 1)) {
            return;
        }
        if (user.getImps()[indexByPot] == null) {
            return;
        }
        if (user.getImps()[indexByPot].getLevel() < (ii.getPot(user.getImps()[indexByPot].getItemId()).right - 1)) {
            return;
        }
        final int itemID = GameConstants.getRewardPot(user.getImps()[indexByPot].getItemId(), user.getImps()[indexByPot].getCloseness());
        MapleInventoryManipulator.addById(c, itemID, (short) 1, "");
        c.sendPacket(CWvsContext.itemPotChanged(c.getPlayer().getImps()[indexByPot], ImpFlag.REMOVED.getValue(), indexByPot, false));
        user.getImps()[indexByPot] = null;
    }

    private static int getEnchantAddCost(final int toCreate, int reqLevel, int baseCost) {
        switch (toCreate / 10000) {

            case 100: {
                if (reqLevel < 60) {
                    baseCost = 6000;
                } else if (reqLevel < 70) {
                    baseCost = 8000;
                } else if (reqLevel < 80) {
                    baseCost = 13000;
                } else if (reqLevel < 90) {
                    baseCost = 21000;
                } else if (reqLevel < 100) {
                    baseCost = 24000;
                } else if (reqLevel < 110) {
                    baseCost = 27000;
                } else if (reqLevel < 120) {
                    baseCost = 45000;
                } else {
                    baseCost = 51000;
                }
                break;
            }

            case 105: {
                if (reqLevel < 60) {
                    baseCost = 14000;
                } else if (reqLevel < 70) {
                    baseCost = 15000;
                } else if (reqLevel < 80) {
                    baseCost = 17000;
                } else if (reqLevel < 90) {
                    baseCost = 38000;
                } else if (reqLevel < 100) {
                    baseCost = 42000;
                } else if (reqLevel < 110) {
                    baseCost = 51000;
                } else if (reqLevel < 120) {
                    baseCost = 90000;
                } else {
                    baseCost = 108000;
                }
                break;
            }

            case 107: {
                if (reqLevel < 60) {
                    baseCost = 8000;
                } else if (reqLevel < 70) {
                    baseCost = 10000;
                } else if (reqLevel < 80) {
                    baseCost = 14000;
                } else if (reqLevel < 90) {
                    baseCost = 23000;
                } else if (reqLevel < 100) {
                    baseCost = 30000;
                } else if (reqLevel < 110) {
                    baseCost = 36000;
                } else if (reqLevel < 120) {
                    baseCost = 54000;
                } else {
                    baseCost = 60000;
                }
                break;
            }

            case 108: {
                if (reqLevel < 60) {
                    baseCost = 13000;
                } else if (reqLevel < 70) {
                    baseCost = 17000;
                } else if (reqLevel < 80) {
                    baseCost = 23000;
                } else if (reqLevel < 90) {
                    baseCost = 42000;
                } else if (reqLevel < 100) {
                    baseCost = 48000;
                } else if (reqLevel < 110) {
                    baseCost = 55000;
                } else if (reqLevel < 120) {
                    baseCost = 90000;
                } else if (reqLevel < 130) {
                    baseCost = 101000;
                }
                break;
            }

            case 109: {
                if (reqLevel < 130) {
                    baseCost = 105000;
                } else {
                    baseCost = 20000;
                }
                break;
            }
            default: {
                if (GameConstants.isWeapon(toCreate)) {
                    if (reqLevel < 60) {
                        baseCost = 20000;
                    } else if (reqLevel < 70) {
                        baseCost = 36000;
                    } else if (reqLevel < 80) {
                        baseCost = 45000;
                    } else if (reqLevel < 90) {
                        baseCost = 74000;
                    } else if (reqLevel < 100) {
                        baseCost = 82000;
                    } else if (reqLevel < 110) {
                        baseCost = 93000;
                    } else if (reqLevel < 120) {
                        baseCost = 153000;
                    } else {
                        baseCost = 168000;
                    }
                }
                break;
            }
        }
        return baseCost;
    }
}
