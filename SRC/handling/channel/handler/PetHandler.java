package handling.channel.handler;

import java.util.List;

import client.*;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import client.inventory.PetCommand;
import server.Randomizer;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.movement.LifeMovementFragment;
import server.maps.FieldLimitType;
import tools.packet.CPet;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext;

public class PetHandler {

    public static final void SpawnPet(final LittleEndianAccessor slea, final MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        chr.spawnPet(slea.readByte(), slea.readByte() > 0);
    }

    public static void Pet_AutoBuff(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        int petid = slea.readInt();
        MaplePet pet = chr.getPet(petid);
        if ((chr == null) || (chr.getMap() == null) || (pet == null)) {
            return;
        }
        int skillId = slea.readInt();

        Skill buffId = SkillFactory.getSkill(skillId);
        if ((chr.getSkillLevel(buffId) > 0) || (skillId == 0)) {
            pet.setBuffSkill(skillId);
            c.getSession().write(CPet.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), true));
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void Pet_AutoPotion(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(1);
        chr.updateTick(slea.readInt());
        final short slot = slea.readShort();
        //chr.dropMessage(5, "Pet_AutoPotion : " + slea.readInt());
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MonsterSkill.StopPotion)) {
            return;
        }
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != slea.readInt()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                if (chr.getMap().getConsumeItemCoolTime() > 0) {
                    chr.setNextConsume(time + (chr.getMap().getConsumeItemCoolTime() * 500));
                }
            }
        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
    }

    public static final void PetChat(final int petid, final short command, final String text, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.getPet(petid) == null) {
            return;
        }
        chr.getMap().broadcastMessage(chr, CPet.petChat(chr.getId(), command, text, (byte) petid), true);
    }

    /*  public static final void PetCommand(final LittleEndianAccessor slea, final MapleClient c) {
        int petindex = slea.readInt();
        MaplePet pet = c.getPlayer().getPet(petindex);
	byte d = slea.readByte();
        if (pet == null) {
            return;
        }
        byte petIndex = (byte) c.getPlayer().getPetIndex(pet);        
        PetCommand petCommand = PetDataFactory.getPetCommand(pet.getPetItemId(), d);        
        if (petCommand == null) {
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte) d, (byte) petIndex, false, false), true);                 
            return;
        }
        boolean success = false;
        if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + (petCommand.getIncrease() * c.getChannelServer().getPetClosenessRate());
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.getSession().write(EffectPacket.showOwnPetLevelUp(petIndex));
                    c.getPlayer().getMap().broadcastMessage(PetPacket.showPetLevelUp(c.getPlayer(), petIndex));
                }
                c.getSession().write(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            }
        }
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte) petCommand.getSkillId(), petIndex, success, false), true);
    }*/
    public static final void PetCommand(final MaplePet pet, final PetCommand petCommand, final MapleClient c, final MapleCharacter chr) {
        if (petCommand == null) {
            return;
        }
        byte petIndex = (byte) chr.getPetIndex(pet);
        boolean success = false;
        if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness()
                        + (petCommand.getIncrease() * c.getChannelServer().getTraitRate());
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.getSession().write(EffectPacket.showOwnPetLevelUp(petIndex));
                    chr.getMap().broadcastMessage(CPet.showPetLevelUp(chr, petIndex));
                }
                c.getSession().write(CPet.updatePet(pet,
                        chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            }
        }
        chr.getMap().broadcastMessage(CPet.commandResponse(chr.getId(), (byte) petCommand.getSkillId(), petIndex, success, false));
    }

    public static void PetFood(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        int previousFullness = 100; // 팻 먹이 임시처리
        MaplePet pet = null;
        if (chr == null) {
            return;
        }
        for (final MaplePet pets : chr.getPets()) {
            if (pets.getSummoned()) {
                if (pets.getFullness() < previousFullness) {
                    previousFullness = pets.getFullness();
                    pet = pets;
                }
            }
        }
        if (pet == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }

        c.getPlayer().updateTick(slea.readInt());
        short slot = slea.readShort();
        final int itemId = slea.readInt();
        Item petFood = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (petFood == null || petFood.getItemId() != itemId || petFood.getQuantity() <= 0 || itemId / 10000 != 212) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        boolean gainCloseness = false;

        if (Randomizer.nextInt(99) <= 50) {
            gainCloseness = true;
        }
        if (pet.getFullness() < 100) {
            int newFullness = pet.getFullness() + 30;
            if (newFullness > 100) {
                newFullness = 100;
            }
            pet.setFullness(newFullness);
            final byte index = chr.getPetIndex(pet);

            if (gainCloseness && pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + 1;
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.getSession().write(EffectPacket.showOwnPetLevelUp(index));
                    chr.getMap().broadcastMessage(CPet.showPetLevelUp(chr, index));
                }
            }
            c.getSession().write(CPet.updatePet(pet,
                    chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            chr.getMap().broadcastMessage(c.getPlayer(),
                    CPet.commandResponse(chr.getId(), (byte) 1, index, true, true), true);
        } else {
            if (gainCloseness) {
                int newCloseness = pet.getCloseness() - 1;
                if (newCloseness < 0) {
                    newCloseness = 0;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness < GameConstants.getClosenessNeededForLevel(pet.getLevel())) {
                    pet.setLevel(pet.getLevel() - 1);
                }
            }
            c.getSession().write(CPet.updatePet(pet,
                    chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            chr.getMap().broadcastMessage(chr,
                    CPet.commandResponse(chr.getId(), (byte) 1, chr.getPetIndex(pet), false, true), true);
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, true, false);
        c.getSession().write(CWvsContext.enableActions());
    }

    public static final void MovePet(final LittleEndianAccessor slea, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final int petId = slea.readInt();
        slea.skip(9); // byte(index?), int(pos), int
        final List<LifeMovementFragment> res = MovementParse.parseMovement(slea, 3);
        if (res != null && chr != null && res.size() != 0 && chr.getMap() != null) { // map crash hack
            final MaplePet pet = chr.getPet(petId);
            if (pet == null) {
                return;
            }
            pet.updatePosition(res);
            chr.getMap().broadcastMessage(chr, CPet.movePet(chr.getId(), pet.getUniqueId(), (byte) petId, res), false);
            if (chr.hasBlockedInventory() || chr.getStat().pickupRange <= 0.0 || chr.inPVP()) {
                return;
            }
            chr.setScrolledPosition((short) 0);
        }
    }

    public static final void PetExceptionPickup(final LittleEndianAccessor slea, final MapleCharacter chr) {
        int petid = slea.readInt();
        MaplePet pet = chr.getPet(petid);
        if (pet == null) {
            return;
        }
        short size = slea.readByte();
        pet.getPickupExceptionList().clear();
        for (int i = 0; i < size; ++i) {
            pet.getPickupExceptionList().add(slea.readInt());
        }
        pet.changeException();
        chr.getClient().getSession().write(CPet.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), true));
        chr.getClient().getSession().write(CWvsContext.enableActions());
    }
}
