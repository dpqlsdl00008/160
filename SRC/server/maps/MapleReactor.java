package server.maps;

import client.MapleCharacter;
import java.awt.Rectangle;
import client.MapleClient;
import constants.GameConstants;
import handling.world.MaplePartyCharacter;
import java.awt.Point;
import scripting.ReactorScriptManager;
import server.MapleInventoryManipulator;
import server.Randomizer;
import server.Timer;
import server.Timer.MapTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import tools.packet.CField;
import tools.Pair;
import tools.packet.CWvsContext;

public class MapleReactor extends MapleMapObject {

    private int rid;
    private MapleReactorStats stats;
    private byte state = 0, facingDirection = 0;
    private int delay = -1;
    private MapleMap map;
    private String name = "";
    private boolean timerActive = false, alive = true, custom = false;
    private int rank;

    public MapleReactor(MapleReactorStats stats, int rid) {
        this.stats = stats;
        this.rid = rid;
    }

    public void setCustom(boolean c) {
        this.custom = c;
    }

    public boolean isCustom() {
        return custom;
    }

    public final void setFacingDirection(final byte facingDirection) {
        this.facingDirection = facingDirection;
    }

    public final byte getFacingDirection() {
        return facingDirection;
    }

    public void setTimerActive(boolean active) {
        this.timerActive = active;
    }

    public boolean isTimerActive() {
        return timerActive;
    }

    public int getReactorId() {
        return rid;
    }

    public void setReactorId(int reactid) {
        rid = reactid;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public byte getState() {
        return state;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.REACTOR;
    }

    public int getReactorType() {
        return stats.getType(state);
    }

    public byte getTouch() {
        return stats.canTouch(state);
    }

    public void setMap(MapleMap map) {
        this.map = map;
    }

    public MapleMap getMap() {
        return map;
    }

    public Pair<Integer, Integer> getReactItem() {
        return stats.getReactItem(state);
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(CField.destroyReactor(this));
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.getSession().write(CField.spawnReactor(this));
    }

    public void forceStartReactor(MapleClient c) {
        ReactorScriptManager.getInstance().act(c, this);
    }

    public void forceHitReactor(final byte newState) {
        setState((byte) newState);
        setTimerActive(false);
        map.broadcastMessage(CField.triggerReactor(this, (short) 0));
        //map.broadcastMessage(CField.destroyReactor(this));
        //map.broadcastMessage(CField.resetReactor(this));
    }

    //hitReactor command for item-triggered reactors
    public void hitReactor(MapleClient c) {
        hitReactor(0, (short) 0, c);
    }

    public void resetReactor(MapleClient client) {
        client.sendPacket(CField.resetReactor(this));
    }

    public void forceTrigger() {
        map.broadcastMessage(CField.triggerReactor(this, (short) 0));
    }

    public void delayedDestroyReactor() {
        map.broadcastMessage(CField.destroyReactor(this, (short) 10));
    }

    public void delayedDestroyReactor(long delay) {
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                map.destroyReactor(getObjectId());
            }
        }, delay);
    }

    public void hitReactor(int charPos, short stance, MapleClient c) {
        if (c.getPlayer().isGM() == true) {
            c.getPlayer().dropMessage(5, "(hitReactor) Reactor : " + getName() + " | Id : " + getReactorId() + " | State : " + state);
        }

        if (rid > 1058000 && rid < 1058005) {
            if (state == 3) {
                MapleMonster realBody = MapleLifeFactory.getMonster(8830000);
                MapleMonster fakeBody = c.getPlayer().getMap().getMonsterById(8830003);
                if (fakeBody != null) {
                    this.forceHitReactor((byte) 0);
                    realBody.setHp((realBody.getMobMaxHp() / 100) * 70);
                    c.getPlayer().getMap().spawnMonsterOnGroudBelow(realBody, fakeBody.getPosition());
                    c.getPlayer().getMap().killMonster(fakeBody, (byte) 3);
                }
            }
        }

        if (rid == 1301000) {
            state = 9;
            c.sendPacket(CField.triggerReactor(this, stance));
            state = 10;
            c.sendPacket(CField.triggerReactor(this, stance));
            return;
        }
        if (rid > 9405999 && rid < 9406003) {
            state = 1;
            c.sendPacket(CField.triggerReactor(this, stance));
            return;
        }

        if (stats.getType(state) < 999 && stats.getType(state) != -1) {
            final byte oldState = state;
            if (!(stats.getType(state) == 2 && (charPos == 0 || charPos == 2)) || rid == 1301000) {
                state = stats.getNextState(state);
                if (stats.getNextState(state) == -1 || stats.getType(state) == 999) { //end of reactor
                    if ((stats.getType(state) < 100 || stats.getType(state) == 999) && delay > 0) {
                        map.destroyReactor(getObjectId());
                    } else {
                        map.broadcastMessage(CField.triggerReactor(this, stance));
                    }
                    ReactorScriptManager.getInstance().act(c, this);
                } else {
                    boolean done = false;
                    map.broadcastMessage(CField.triggerReactor(this, stance));
                    if (state == stats.getNextState(state) || rid == 2618000 || rid == 2309000 || rid == 3009000) {
                        if (rid > 200011) {
                            ReactorScriptManager.getInstance().act(c, this);
                        }
                        done = true;
                    }
                    if (stats.getTimeOut(state) > 0) {
                        if (!done && rid > 200011) {
                            ReactorScriptManager.getInstance().act(c, this);
                        }
                        scheduleSetState(state, oldState, stats.getTimeOut(state));
                    }
                }
            }
        }
        /* 여신의 흔적 */
        if (c.getPlayer().getMapId() == 920010700) {
            String sum = (c.getPlayer().getMap().getReactorByName("1").getState() + "") + (c.getPlayer().getMap().getReactorByName("2").getState() + "") + (c.getPlayer().getMap().getReactorByName("3").getState() + "");
            if (sum.equals(c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").getProperty("4stage") + "")) {
                c.getChannelServer().getEventSM().getEventManager("PQ_Orbis").setProperty("4stage", "1");
                c.getPlayer().getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                c.getPlayer().getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                c.getPlayer().getMap().floatNotice("레버를 바르게 조작하는데 성공하였습니다. 파티장이 시종 이크에게서 네 번째 조각을 받고 이동하여 주세요.", 5120019, false);
            }
        }
        if (c.getPlayer().getMapId() == 920010600) {
            if (c.getPlayer().getMap().getReactorByName("scar1").getState() == 1 && c.getPlayer().getMap().getReactorByName("scar2").getState() == 1 && c.getPlayer().getMap().getReactorByName("scar5").getState() == 1 && c.getPlayer().getMap().getReactorByName("scar6").getState() == 1) {
                c.getPlayer().getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                c.getPlayer().getMap().broadcastMessage(CField.playSound("Party1/Clear"));
            }
        }
        summonRootabyssMob();
    }

    public void summonRootabyssMob() {
        final MapleReactor reactor = this;
        final MapleMap reactorMap = this.getMap();
        switch (this.rid) {
            case 1058016: {
                Timer.MapTimer.getInstance().schedule(new Runnable() {
                    public void run() {
                        MapleMonster a2 = MapleLifeFactory.getMonster(8910000);
                        reactorMap.spawnMonsterOnGroundBelow(a2, reactor.getTruePosition());
                    }
                }, 1500);
                break;
            }
            case 1058018: {
                if (this.state == 5) {
                    int a1 = Randomizer.rand(8920000, 8920003);
                    MapleMonster a2 = MapleLifeFactory.getMonster(a1);
                    reactorMap.spawnMonsterOnGroundBelow(a2, this.getTruePosition());
                }
                break;
            }
            case 1058020: {
                Timer.MapTimer.getInstance().schedule(new Runnable() {
                    public void run() {
                        MapleMonster a2 = MapleLifeFactory.getMonster(8930000);
                        reactorMap.spawnMonsterOnGroundBelow(a2, new Point(-207, 443));
                        reactorMap.floatNotice("내 경고를 무시하고 다시 찾아 온 것은 네 놈이니 더 이상 자비를 베풀지는 않겠다.", 5120103, false);
                    }
                }, 3000);
                break;
            }
        }
    }

    public Rectangle getArea() {
        int height = stats.getBR().y - stats.getTL().y;
        int width = stats.getBR().x - stats.getTL().x;
        int origX = getTruePosition().x + stats.getTL().x;
        int origY = getTruePosition().y + stats.getTL().y;

        return new Rectangle(origX, origY, width, height);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRank(int r) {
        this.rank = r;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return "Reactor " + getObjectId() + " of id " + rid + " at position " + getPosition().toString() + " state" + state + " type " + stats.getType(state);
    }

    public void delayedHitReactor(final MapleClient c, long delay) {
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                hitReactor(c);
            }
        }, delay);
    }

    public void scheduleSetState(final byte oldState, final byte newState, long delay) {
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (MapleReactor.this.state == oldState) {
                    forceHitReactor(newState);
                }
            }
        }, delay);
    }
}
