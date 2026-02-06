function init() {
    em.setProperty("map", "0");
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    var eim = em.newInstance("expedition_lotus" + leaderid);
    eim.setInstanceMap((parseInt(em.getProperty("map")))).resetFully();
    eim.setInstanceMap((parseInt(em.getProperty("map")) + 100)).resetFully();
    eim.startEventTimer(60000 * 60 * 1);
    eim.schedule("spawnLotus", 5000);
    eim.schedule("spawnLighting", 1000 * 30);
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(parseInt(em.getProperty("map")));
    player.changeMap(map, map.getPortal(0));
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
        case (parseInt(em.getProperty("map"))):
        case (parseInt(em.getProperty("map")) + 100):{
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

function spawnLighting(eim) {
    var v1 = em.getMonster(8240186);
    eim.getMapInstance(0).spawnMonsterOnGroundBelow(v1, new java.awt.Point(-196, -16));
    var v2 = em.getMonster(8240186);
    eim.getMapInstance(1).spawnMonsterOnGroundBelow(v2, new java.awt.Point(-13, -16));
    eim.schedule("spawnLighting", 1000 * 50);
}

function spawnLotus(eim) {
    var v1 = em.getMonster(8240098);
    eim.registerMonster(v1);
    eim.getMapInstance(0).spawnMonsterOnGroundBelow(v1, new java.awt.Point(-196, -16));
    var v2 = em.getMonster(8240099);
    eim.registerMonster(v2);
    eim.getMapInstance(1).spawnMonsterOnGroundBelow(v2, new java.awt.Point(-13, -16));
    var v3 = em.getMonster(8240107);
    eim.registerMonster(v3);
    eim.getMapInstance(1).spawnMonsterOnGroundBelow(v3, new java.awt.Point(-13, -16));
    var v4 = em.getMonster(8240108);
    eim.registerMonster(v4);
    eim.getMapInstance(1).spawnMonsterOnGroundBelow(v4, new java.awt.Point(-13, -16));
    var v5 = em.getMonster(8240109);
    eim.registerMonster(v5);
    eim.getMapInstance(1).spawnMonsterOnGroundBelow(v5, new java.awt.Point(-13, -16));
}

function nextStage(eim) {
    em.warpAllPlayer(350060500, 350060600);
}

function monsterValue(eim, mobId) {
    if (mobId == 8240098) {
        eim.schedule("nextStage", 5000);
    }
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
    eim.disposeIfPlayerBelow(100, 350060300);
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