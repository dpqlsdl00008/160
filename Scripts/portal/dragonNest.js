function enter(pi) {
    if (pi.getPlayerCount(240040611) < 1) {
        pi.resetMap(240040611);
        pi.playPortalSE();
        pi.warp(240040611, "sp");
    } else {
        pi.playerMessage(1, "이미 다른 누군가가 입장하여 있습니다. 잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주시길 바랍니다.");
    }
}