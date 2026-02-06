function init() {
    em.setProperty("map", "0");
    em.setProperty("state", "0");
    em.setProperty("damage", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    var eim = em.newInstance("etc_traningroom" + leaderid);
    eim.setInstanceMap((parseInt(em.getProperty("map")))).resetFully();

    var monster = em.getMonster(9834028);
    eim.registerMonster(monster);
    eim.getMapInstance(0).spawnMonsterOnGroundBelow(monster, new java.awt.Point(168, 152));

    eim.startEventTimer(60000 * 1);
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(parseInt(em.getProperty("map")));
    player.changeMap(map, map.getPortal(0));
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
        em.setProperty("damage", "0");
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
        em.setProperty("damage", "0");
    }
}

function end(eim) {
    eim.stopEventTimer();
    em.openNpc(993198000, 2440007);
    em.setProperty("map", "0");
    em.setProperty("state", "0");
    em.setProperty("damage", "0");
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