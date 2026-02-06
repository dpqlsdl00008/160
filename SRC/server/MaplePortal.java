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
package server;

import java.awt.Point;

import client.MapleClient;
import client.anticheat.CheatingOffense;
import handling.ChatType;
import handling.channel.ChannelServer;
import scripting.NPCScriptManager;
import scripting.PortalScriptManager;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import tools.packet.CUserLocal;
import tools.packet.CWvsContext;

public class MaplePortal {

    public static final int MAP_PORTAL = 2;
    public static final int DOOR_PORTAL = 6;

    private String name, target, scriptName;
    private Point position;
    private int targetmap, type, id;
    private boolean portalState = true;

    public MaplePortal(final int type) {
        this.type = type;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final Point getPosition() {
        return position;
    }

    public final String getTarget() {
        return target;
    }

    public final int getTargetMapId() {
        return targetmap;
    }

    public final int getType() {
        return type;
    }

    public final String getScriptName() {
        return scriptName;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final void setPosition(final Point position) {
        this.position = position;
    }

    public final void setTarget(final String target) {
        this.target = target;
    }

    public final void setTargetMapId(final int targetmapid) {
        this.targetmap = targetmapid;
    }

    public final void setScriptName(final String scriptName) {
        this.scriptName = scriptName;
    }

    public final void enterPortal(final MapleClient c) {
        if (getPosition().distanceSq(c.getPlayer().getPosition()) > 40000 && !c.getPlayer().isGM()) {
            c.getSession().write(CWvsContext.enableActions());
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
            return;
        }
        if (c.getPlayer().isGM() == true) {
            c.getPlayer().getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, "(enterPortal) Portal : " + this.getName() + "(" + this.getId() + ") | Script : " + this.getScriptName()));
        }
        final MapleMap currentmap = c.getPlayer().getMap();
        if (!c.getPlayer().hasBlockedInventory() && (portalState || c.getPlayer().isGM())) {
            if (getScriptName() != null) {
                c.getPlayer().checkFollow();
                try {
                    PortalScriptManager.getInstance().executePortalScript(this, c);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else if (getTargetMapId() != 999999999) {
                MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(getTargetMapId());
                if (to == null) {
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                if (!c.getPlayer().isGM()) {
                    if (to.getLevelLimit() > 0 && to.getLevelLimit() > c.getPlayer().getLevel()) {
                        c.getPlayer().dropMessage(-1, "You are too low of a level to enter this place.");
                        c.getSession().write(CWvsContext.enableActions());
                        return;
                    }
                }
                /* Hayato */
                int cMap = c.getPlayer().getMapId();
                boolean usePortal = true;
                int questID = 0;
                switch (c.getPlayer().getMapId()) {
                    case 507100000: {
                        to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(cMap + 1);
                        break;
                    }
                    case 701000100: {
                        if (this.getName().equals("y001")) {
                            to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(701100000);
                        }
                        break;
                    }
                    case 450001005: {
                        if (this.getId() == 1) {
                            to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(450001000);
                            target = "PV01";
                            break;
                        }
                        if (this.getId() == 2) {
                            usePortal = false;
                            c.removeClickedNPC();
                            NPCScriptManager.getInstance().start(c, 3003110);
                            break;
                        }
                    }
                    case 450001100: {
                        if (this.getId() == 1) {
                            to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(450001105);
                            target = "PS01";
                        }
                    }
                    case 450001210: {
                        if (this.getId() == 4) {
                            questID = 34116;
                            if (c.getPlayer().getQuestStatus(questID) != 2) {
                                usePortal = false;
                            }
                        }
                        break;
                    }
                    case 450001215: {
                        if (this.getId() == 4) {
                            questID = 34117;
                            if (c.getPlayer().getQuestStatus(questID) != 2) {
                                usePortal = false;
                            }
                        }
                        break;
                    }
                    case 450001218: {
                        if (this.getId() == 2) {
                            questID = 34118;
                            if (c.getPlayer().getQuestStatus(questID) != 2) {
                                usePortal = false;
                            } else {
                                if (c.getPlayer().getQuestStatus(34119) == 0) {
                                    to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(450001380);
                                }
                            }
                        }
                        break;
                    }
                    case 450001219: {
                        if (this.getId() == 2) {
                            if (c.getPlayer().getQuestStatus(34120) == 0) {
                                to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(450001350);
                            }
                        }
                        break;
                    }
                    case 450001240: {
                        if (this.getId() == 2) {
                            if (c.getPlayer().getQuestStatus(34120) == 0) {
                                to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(450001360);
                            }
                        }
                        break;
                    }
                    case 450005240: {
                        if (this.getId() == 4) {
                            switch (c.getPlayer().getQuestStatus(34471)) {
                                case 0: {
                                    usePortal = false;
                                    break;
                                }
                                case 1: {
                                    boolean canPortal = true;
                                    if (c.getChannelServer().getMapFactory().getMap(940200252).getCharactersSize() > 0) {
                                        canPortal = false;
                                    }
                                    if (c.getChannelServer().getMapFactory().getMap(940200253).getCharactersSize() > 0) {
                                        canPortal = false;
                                    }
                                    if (c.getChannelServer().getMapFactory().getMap(940200255).getCharactersSize() > 0) {
                                        canPortal = false;
                                    }
                                    if (!canPortal) {
                                        String aQuest = MapleQuest.getInstance(34464).getName();
                                        c.getPlayer().dropMessage(1, "이미 다른 유저가 '" + aQuest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
                                        c.sendPacket(CWvsContext.enableActions());
                                        return;
                                    }
                                    to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(940200252);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
                if (usePortal) {
                    c.getPlayer().changeMapPortal(to, to.getPortal(getTarget()) == null ? to.getPortal(0) : to.getPortal(getTarget()));
                } else {
                    String qMsg = "현재 이 포탈을 사용 할 수 없습니다.";
                    c.getPlayer().getClient().sendPacket(CUserLocal.chatMsg(ChatType.GameDesc, qMsg));
                }
            }
        }
        if (c.getPlayer().getMap() == currentmap) {
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public boolean getPortalState() {
        return portalState;
    }

    public void setPortalState(boolean ps) {
        this.portalState = ps;
    }
}
