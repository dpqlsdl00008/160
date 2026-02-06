function init() {
    em.setProperty("map", "0");
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    var eim = em.newInstance("expedition_horntail" + leaderid);
    eim.setInstanceMap((parseInt(em.getProperty("map")))).resetFully();
    eim.setInstanceMap((parseInt(em.getProperty("map")) + 100)).resetFully();
    eim.setInstanceMap((parseInt(em.getProperty("map")) + 200)).resetFully();
    eim.startEventTimer(60000 * 60 * 1);
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(parseInt(em.getProperty("map")));
    player.changeMap(map, map.getPortal(0));
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
        case (parseInt(em.getProperty("map"))):
        case (parseInt(em.getProperty("map")) + 100):
        case (parseInt(em.getProperty("map")) + 200):
            return;
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
    eim.disposeIfPlayerBelow(100, 240050400);
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