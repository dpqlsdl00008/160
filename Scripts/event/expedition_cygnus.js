function init() {
    em.setProperty("map", "0");
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    var eim = em.newInstance("expedition_cygnus" + leaderid);
    eim.setInstanceMap((parseInt(em.getProperty("map")))).resetFully();
    eim.setInstanceMap((parseInt(em.getProperty("map")) + 100)).resetFully();
    eim.setInstanceMap((parseInt(em.getProperty("map")) + 110)).resetFully();
    eim.startEventTimer(60000 * 60 * 1);
    eim.schedule("spawnCygnus", 1000 * 5);
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
        case (parseInt(em.getProperty("map")) + 110): {
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

function spawnCygnus(eim) {
    var lucid = em.getMonster(8850011);
    eim.registerMonster(lucid);
    eim.getMapInstance(0).spawnMonsterOnGroundBelow(lucid, new java.awt.Point(-157, 115));
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
    eim.disposeIfPlayerBelow(100, 271040000);
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