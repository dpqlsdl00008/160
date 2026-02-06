package tools.packet;

import client.MapleCharacter;
import client.MapleStat;
import client.inventory.Item;
import client.inventory.MaplePet;
import handling.SendPacketOpcode;

import java.util.List;

import server.movement.LifeMovementFragment;
import tools.data.OutPacket;

public class CPet {

    public static final byte[] updatePet(MaplePet pet, Item item, boolean active) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnInventoryOperation.getValue());
        mplew.write(0);
        mplew.write(2);
        mplew.write(0);
        mplew.write(3);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(0);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(3);
        mplew.writeInt(pet.getPetItemId());
        mplew.write(1);
        mplew.writeLong(pet.getUniqueId());
        PacketHelper.addPetItemInfo(mplew, item, pet, active);
        return mplew.getPacket();
    }

    public static final byte[] showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SPAWN_PET.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getPetIndex(pet));
        // 킵
        pet.setPos(chr.getTruePosition());
        pet.setStance(0);
        if (remove) {
            mplew.writeShort(hunger ? 0x100 : 0);
        } else {
            mplew.write(1);
            mplew.write(1); // 클로이 귀찮아
            mplew.writeInt(pet.getPetItemId());
            mplew.writeMapleAsciiString(pet.getName());
            mplew.writeLong(pet.getUniqueId());
            mplew.writeShort(pet.getPos().x);
            mplew.writeShort(pet.getPos().y - 20);
            mplew.write(pet.getStance());
            mplew.writeShort(pet.getFh());
            //mplew.writeInt(-1); // 펫색깔 160에선안쓰는패킷
            //mplew.writeInt(100); 
        }
        return mplew.getPacket();
    }

    public static final byte[] removePet(int cid, int index) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.SPAWN_PET.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(index);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static final byte[] movePet(int cid, int pid, byte slot, List<LifeMovementFragment> moves) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PET_MOVE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(slot);
        mplew.writeLong(pid);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static final byte[] petChat(int cid, int un, String text, byte slot) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PET_ACTION.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(slot);
        mplew.writeShort(un);
        mplew.writeMapleAsciiString(text);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static final byte[] commandResponse(int cid, byte command, byte slot, boolean success, boolean food) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PET_ACTION_COMMAND.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(slot);
        mplew.write(command == 1 ? 1 : 0);
        mplew.write(command);
        if (command != 1) {
            mplew.write(success ? 1 : 0);
        }
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static final byte[] showPetLevelUp(MapleCharacter chr, byte index) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.USER_EFFECT.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(6);
        mplew.write(0);
        mplew.writeInt(index);

        return mplew.getPacket();
    }

    public static final byte[] showPetUpdate(MapleCharacter chr, int uniqueId, byte index) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.PET_LOAD_EXCEPTION_LIST.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(index);
        mplew.writeLong(uniqueId);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static final byte[] petStatUpdate(MapleCharacter chr) {
        OutPacket mplew = new OutPacket();

        mplew.writeShort(SendPacketOpcode.OnStatChanged.getValue());
        mplew.write(0);
        mplew.writeInt((int) MapleStat.Pet.getValue());

        byte count = 0;
        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                mplew.writeLong(pet.getUniqueId());
                count = (byte) (count + 1);
            }
        }
        while (count < 3) {
            mplew.writeZeroBytes(8);
            count = (byte) (count + 1);
        }
        mplew.write(0);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static final byte[] loadPetPickupExceptionList(MapleCharacter chr, MaplePet pet) {
        final OutPacket mplew = new OutPacket();
        mplew.writeShort(SendPacketOpcode.PET_LOAD_EXCEPTION_LIST.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getPetIndex(pet));
        mplew.writeLong(pet.getUniqueId());
        mplew.write(pet.getPickupExceptionList().size());
        for (int i : pet.getPickupExceptionList()) {
            mplew.writeInt(i);
        }
        return mplew.getPacket();
    }
}