function init() {
    em.setProperty("map", "0");
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    var eim = em.newInstance("expedition_balrog" + leaderid);
    eim.setInstanceMap((parseInt(em.getProperty("map")))).resetFully();
    eim.setInstanceMap((parseInt(em.getProperty("map")) + 1)).resetFully();
    eim.startEventTimer(60000 * 20);
    var mMap = eim.getMapInstance(0);
    var mBody = em.getMonster(8830000);
    var mLight = em.getMonster(8830005);
    var mLeft = em.getMonster(8830006);
    mMap.spawnMonsterOnGroundBelow(mBody, new java.awt.Point(412, 258));
    mMap.spawnMonsterOnGroundBelow(mLight, new java.awt.Point(412, 258));
    mMap.spawnMonsterOnGroundBelow(mLeft, new java.awt.Point(412, 258));
    eim.floatNotice(105100300, "무영의 말 : 제가 봉인의 힘을 다시 일깨울 때까지 여러분이 발록을 저지해 주세요!", 5120025);
    eim.schedule("canAttack", 60000);
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(parseInt(em.getProperty("map")));
    player.changeMap(map, map.getPortal(0));
}

function canAttack(eim) {
    var mMap = eim.getMapInstance(0);
    var mLeft = mMap.getMonsterById(8830006);
    if (mLeft != null) {
        mLeft.damage(eim.getPlayers().get(0), mLeft.getMobMaxHp(), false);
    }
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
        case (parseInt(em.getProperty("map"))): {
            return;
        }
    }
    eim.unregisterPlayer(player);
    if (eim.disposeIfPlayerBelow(0, 0)) {
        em.setProperty("map", "0");
        em.setProperty("state", "0");
    }
}

function scheduledTimeout(eim) {
    end(eim);
}

function monsterValue(eim, mobId) {
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    if (eim.disposeIfPlayerBelow(0, 0)) {
        em.setProperty("map", "0");
        em.setProperty("state", "0");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 105100100);
    em.setProperty("map", "0");
    em.setProperty("state", "0");
}

function clearPQ(eim) {
    end(eim);
}

function playerRevive(eim, player) {
    return false;
}

function allMonstersDead(eim) {}
function playerDead(eim, player) {}
function playerDisconnected(eim, player) {}
function leftParty (eim, player) {}
function disbandParty (eim) {}
function cancelSchedule() {}