package server.maps;

import client.MapleCharacter;
import client.MapleClient;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.World.Find;
import server.MaplePortal;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.PartyPacket;

public class MapleDoor extends MapleMapObject {

    private WeakReference<MapleCharacter> owner;
    private MapleMap town;
    private MaplePortal townPortal;
    private MapleMap mapField;
    private int skillId, ownerId;
    private Point mapFieldPosition;
    private boolean isTown;

    public MapleDoor(final MapleCharacter owner, final Point targetPosition, final int skillId) {
        super();
        this.owner = new WeakReference<MapleCharacter>(owner);
        this.ownerId = owner.getId();
        this.mapField = owner.getMap();
        this.mapFieldPosition = targetPosition;
        setPosition(this.mapFieldPosition);
        this.town = this.mapField.getReturnMap();
        this.townPortal = getFreePortal();
        this.skillId = skillId;
        isTown = false;
    }

    public MapleDoor(final MapleDoor origDoor) {
        super();
        this.owner = new WeakReference<MapleCharacter>(origDoor.owner.get());
        this.town = origDoor.town;
        this.townPortal = origDoor.townPortal;
        this.mapField = origDoor.mapField;
        this.mapFieldPosition = new Point(origDoor.mapFieldPosition);
        this.skillId = origDoor.skillId;
        this.ownerId = origDoor.ownerId;
        setPosition(townPortal.getPosition());
        isTown = true;
    }

    public final int getSkill() {
        return skillId;
    }

    public final int getOwnerId() {
        return ownerId;
    }

    private final MaplePortal getFreePortal() {
        final List<MaplePortal> freePortals = new ArrayList<MaplePortal>();
        for (final MaplePortal port : town.getPortals()) {
            if (port.getType() == 6) {
                freePortals.add(port);
            }
        }
        Collections.sort(freePortals, new Comparator<MaplePortal>() {
            @Override
            public final int compare(final MaplePortal o1, final MaplePortal o2) {
                if (o1.getId() < o2.getId()) {
                    return -1;
                } else if (o1.getId() == o2.getId()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        return freePortals.iterator().next();
    }

    public MaplePortal updateTownDoorPosition(MapleParty party) {
        int i = 0;
        if (party != null) {
            for (MaplePartyCharacter pchr : party.getMembers()) {
                if (pchr.getId() == getOwnerId()) {
                    break;
                }
                ++i;
            }
        }
        final List<MaplePortal> freePortals = new ArrayList<MaplePortal>();
        for (final MaplePortal port : town.getPortals()) {
            if (port.getType() == 6) {
                freePortals.add(port);
            }
        }
        Collections.sort(freePortals, new Comparator<MaplePortal>() {
            @Override
            public final int compare(final MaplePortal o1, final MaplePortal o2) {
                if (o1.getId() < o2.getId()) {
                    return -1;
                } else if (o1.getId() == o2.getId()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        MaplePortal ret = freePortals.get(i);
        townPortal = ret;
        return ret;
    }

    public void leavePartyElseDoorOwner(MapleClient client) {
        if (getOwner() == null || mapField == null) {
            return;
        }
        MapleCharacter pchrCharacter = client.getPlayer();
        if (pchrCharacter != null) {
            if (pchrCharacter.getMapId() == (isTown ? town.getId() : mapField.getId())) {
                sendDestroyData(pchrCharacter.getClient());
            }
        }
    }

    public void leavePartyDoorOwner(MapleParty party) {
        if (getOwner() == null || mapField == null) {
            return;
        }
        for (MaplePartyCharacter pchr : party.getMembers()) {
            if (pchr.getId() == getOwner().getId()) {
                continue;
            }
            int ch = Find.findChannel(pchr.getName());
            if (ch > 0 && town.getChannel() == ch) {
                MapleCharacter pchrCharacter = World.getStorage(ch).getCharacterByName(pchr.getName());
                if (pchrCharacter != null) {
                    if (pchrCharacter.getMapId() == (isTown ? town.getId() : mapField.getId())) {
                        sendDestroyData(pchrCharacter.getClient());
                    }
                }
            }
        }
    }

    public void joinPartyElseDoorOwner(MapleClient client) {
        if (getOwner() == null || mapField == null) {
            return;
        }
        MapleCharacter pchrCharacter = client.getPlayer();
        if (pchrCharacter != null) {
            if (pchrCharacter.getMapId() == (isTown ? town.getId() : mapField.getId())) {
                pchrCharacter.getClient().sendPacket(PartyPacket.partyPortal(town.getId(), mapField.getId(), skillId, mapField.getId() == client.getPlayer().getMapId() ? mapFieldPosition : townPortal.getPosition(), true));
            }
        }
    }

    public void joinPartyDoorOwner(MapleParty party) {
        if (getOwner() == null || mapField == null) {
            return;
        }
        for (MaplePartyCharacter pchr : party.getMembers()) {
            int ch = Find.findChannel(pchr.getName());
            if (ch > 0 && town.getChannel() == ch) {
                MapleCharacter pchrCharacter = World.getStorage(ch).getCharacterByName(pchr.getName());
                if (pchrCharacter != null) {
                    if (pchrCharacter.getMapId() == (isTown ? town.getId() : mapField.getId())) {
                        pchrCharacter.getClient().sendPacket(PartyPacket.partyPortal(town.getId(), mapField.getId(), skillId, mapField.getId() == pchrCharacter.getMapId() ? mapFieldPosition : townPortal.getPosition(), true));
                    }
                }
            }
        }
    }

    public void sendSinglePortal() {
        if (getOwner() == null || mapField == null) {
            return;
        }
        getOwner().getClient().sendPacket(CWvsContext.spawnPortal(town.getId(), mapField.getId(), skillId, mapField.getId() == getOwner().getMapId() ? mapFieldPosition : townPortal.getPosition()));
    }

    public final void sendSpawnData(MapleClient client, boolean animated) {
        if (getOwner() == null || mapField == null || client.getPlayer() == null) {
            return;
        }
        if (mapField.getId() == client.getPlayer().getMapId() || getOwnerId() == client.getPlayer().getId() || (getOwner() != null && getOwner().getParty() != null && client.getPlayer().getParty() != null && getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
            if (!isTown) {
                client.getSession().write(CField.spawnDoor(getOwnerId(), mapField.getId() == client.getPlayer().getMapId() ? mapFieldPosition : townPortal.getPosition(), animated)); //spawnDoor always has same position.
            }
            if (getOwner() != null && getOwner().getParty() != null && client.getPlayer().getParty() != null && (getOwnerId() == client.getPlayer().getId() || getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
                client.getSession().write(PartyPacket.partyPortal(town.getId(), mapField.getId(), skillId, mapField.getId() == client.getPlayer().getMapId() ? mapFieldPosition : townPortal.getPosition(), animated));
            } else {
                client.getSession().write(CWvsContext.spawnPortal(town.getId(), mapField.getId(), skillId, mapField.getId() == client.getPlayer().getMapId() ? mapFieldPosition : townPortal.getPosition()));
            }
        }
    }

    @Override
    public final void sendSpawnData(final MapleClient client) {
        sendSpawnData(client, false);
    }

    @Override
    public final void sendDestroyData(final MapleClient client) {
        if (client.getPlayer() == null || getOwner() == null || mapField == null) {
            return;
        }
        if (mapField.getId() == client.getPlayer().getMapId() || getOwnerId() == client.getPlayer().getId() || (getOwner() != null && getOwner().getParty() != null && client.getPlayer().getParty() != null && getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
            client.getSession().write(CField.removeDoor(getOwnerId(), false));
            if (getOwner() != null && getOwner().getParty() != null && client.getPlayer().getParty() != null && (getOwnerId() == client.getPlayer().getId() || getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
                client.getSession().write(PartyPacket.partyPortal(999999999, 999999999, 0, new Point(-1, -1), false));
            } else {
                client.getSession().write(CWvsContext.spawnPortal(999999999, 999999999, 0, null));
            }
        }
    }

    public final void warp(final MapleCharacter chr, final boolean toTown) {
        if (chr.getId() == getOwnerId() || (getOwner() != null && getOwner().getParty() != null && chr.getParty() != null && getOwner().getParty().getId() == chr.getParty().getId())) {
            if (!toTown) {
                chr.changeMap(mapField, mapField.findClosestPortal(mapFieldPosition));
            } else {
                chr.changeMap(town, townPortal);
            }
        } else {
            chr.getClient().getSession().write(CWvsContext.enableActions());
        }
    }

    public final MapleCharacter getOwner() {
        return owner.get();
    }

    public final MapleMap getTown() {
        return town;
    }

    public final MaplePortal getTownPortal() {
        return townPortal;
    }

    public final MapleMap getTarget() {
        return mapField;
    }

    public final Point getTargetPosition() {
        return mapFieldPosition;
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.DOOR;
    }
}