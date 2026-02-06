package handling.factory;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CPet;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;

/**
 * @author 몽키프
 */
public class PotentialStampHandler {

    /*  public static void EditionalScroll(final LittleEndianAccessor slea, MapleClient c) {
        try {
            slea.skip(4);
            short mode = slea.readShort();
            /* 에디셔널 인장 */
 /* Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(mode);
            if ((toUse.getItemId() >= 2048305 && toUse.getItemId() <= 2048315)) {
                short slot = slea.readShort();
                Item item;
                if (slot < 0) {
                    item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot); // 장비템
                } else {
                    item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot); // 장비템
                }
                Equip eq = (Equip) item; // eq.getPotential4()
                boolean succes = Randomizer.isSuccess(GameConstants.getSuccessRate(toUse.getItemId()));
                if (succes) {
                    int lines = 2; // default
                    if (eq.getPotential6() != 0) {
                        lines++;
                    }
                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
                    final int reqLevel = ii.getReqLevel(eq.getItemId()) / 10;
                    int new_state = Math.abs(eq.getBonusPotentialByLine(0));
                    if (eq.getBonusPotentialByLine(0) <= 0) {
                        if (new_state > 20 || new_state < 17) { // incase overflow
                            new_state = 17;
                        }
                    }
                    while (eq.getStateByPotential(eq.getBonusPotential()) != new_state) {
                        //31001 = haste, 31002 = door, 31003 = se, 31004 = hb, 41005 = combat orders, 41006 = advanced blessing, 41007 = speed infusion
                        for (int i = 0; i < lines; i++) { // minimum 2 lines, max 3
                            boolean rewarded = false;
                            while (!rewarded) {
                                StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                                if (pot != null && pot.reqLevel / 10 <= reqLevel && GameConstants.optionTypeFits(pot.optionType, eq.getItemId()) && GameConstants.potentialIDFits(pot.opID, new_state, i)) { //optionType
                                    if (isAllowedPotentialStat(eq, pot.opID)) {
                                        eq.setBonusPotentialByLine(i, pot.opID);
                                        rewarded = true;
                                    }
                                }
                            }
                        }
                    }
                    } else {
                        MapleInventoryManipulator.removeFromSlot(c, slot < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, item.getPosition(), (short) 1, false);
                    }
                    c.getSession().write(InventoryPacket.scrolledItem(toUse, item, !succes, true));
                    c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), succes, toUse.getItemId()));
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false);
                    c.getSession().write(CWvsContext.enableActions());*/
    // } else {
    //    c.getPlayer().dropMessage(1, "일반 잠재옵션을 먼저 열어야 합니다.");
    //    c.getSession().write(CWvsContext.enableActions());
    // }
    /*   }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/
    public static void EditionalScroll(final LittleEndianAccessor slea, MapleClient c) {
        try {
            slea.skip(4);
            short mode = slea.readShort();
            /* 에디셔널 인장 */
            Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(mode);
            if ((toUse.getItemId() >= 2048305 && toUse.getItemId() <= 2048311)) {
                short slot = slea.readShort();
                Item item;
                if (slot < 0) {
                    item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot); // 장비템
                } else {
                    item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot); // 장비템
                }
                if (item != null) {
                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    boolean succes = Randomizer.isSuccess(GameConstants.getSuccessRate(toUse.getItemId()));
                    if (succes) {
                        Equip eq = (Equip) item;

                        int rank = Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 4 ? -19 : -18) : -17;
                        if (Randomizer.nextInt(100) < 20) {
                            eq.setBonusPotentialByLine(0, rank);
                            eq.setBonusPotentialByLine(1, rank);
                            eq.setBonusPotentialByLine(2, rank);
                        } else {
                            eq.setBonusPotentialByLine(0, rank);
                            eq.setBonusPotentialByLine(1, rank);
                        }
                    } else {
                        MapleInventoryManipulator.removeFromSlot(c, slot < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, item.getPosition(), (short) 1, false);
                    }
                    c.getSession().write(InventoryPacket.scrolledItem(toUse, item, !succes, true));
                    c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), succes, toUse.getItemId()));
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false);
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isAllowedPotentialStat(Equip eqq, int opID) { //For now
        //if (GameConstants.isWeapon(eqq.getItemId())) {
        //    return !(opID > 60000) || (opID >= 1 && opID <= 4) || (opID >= 9 && opID <= 12) || (opID >= 10001 && opID <= 10006) || (opID >= 10011 && opID <= 10012) || (opID >= 10041 && opID <= 10046) || (opID >= 10051 && opID <= 10052) || (opID >= 10055 && opID <= 10081) || (opID >= 10201 && opID <= 10291) || (opID >= 210001 && opID <= 20006) || (opID >= 20011 && opID <= 20012) || (opID >= 20041 && opID <= 20046) || (opID >= 20051 && opID <= 20052) || (opID >= 20055 && opID <= 20081) || (opID >= 20201 && opID <= 20291) || (opID >= 30001 && opID <= 30006) || (opID >= 30011 && opID <= 30012) || (opID >= 30041 && opID <= 30046) || (opID >= 30051 && opID <= 30052) || (opID >= 30055 && opID <= 30081) || (opID >= 30201 && opID <= 30291) || (opID >= 40001 && opID <= 40006) || (opID >= 40011 && opID <= 40012) || (opID >= 40041 && opID <= 40046) || (opID >= 40051 && opID <= 40052) || (opID >= 40055 && opID <= 40081) || (opID >= 40201 && opID <= 40291);
        //}
        return opID < 60000;
    }

    public static final void handleUserCashPetPickUpOnOffRequest(final LittleEndianAccessor iPacket, final MapleClient c) {
        final MapleCharacter user = c.getPlayer();
        if (user == null) {
            return;
        }
        iPacket.skip(4);
        boolean on = iPacket.DecodeByte() != 0;
        for (MaplePet pet : user.getPets()) {
            if (pet == null) {
                return;
            }
            c.getSession().write(CWvsContext.InventoryPacket.getInventoryStatus());
        }
        c.sendPacket(CWvsContext.cashPetPickUpOnOffResult(on, true));
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void UsePotentialStamp(final LittleEndianAccessor slea, final MapleClient c) { // 인장
        slea.skip(4);//crc
        byte slot = (byte) slea.readShort();
        byte dst = (byte) slea.readShort();
        boolean success = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip toStamp;
        toStamp = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
        MapleInventory useInventory = c.getPlayer().getInventory(MapleInventoryType.USE);
        Item stamp = useInventory.getItem(slot);
        if (Randomizer.isSuccess(ii.getItemSuccessRate(toStamp.getItemId()))) {
            int level = toStamp.getState() - 16;
            int temp = level;
            while (temp > 1) {
                if (temp > 1) {
                    --temp;
                }
            }
            //    toStamp.setPotential3(level);
            toStamp.setPotentialByLine(2, level);
            success = true;
        }
        useInventory.removeItem(stamp.getPosition(), (short) 1, false);
        c.getSession().write(InventoryPacket.scrolledItem(stamp, toStamp, false, true));
        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), success, stamp.getItemId()));
        c.getPlayer().getClient().getSession().write(CWvsContext.enableActions());
    }
}
