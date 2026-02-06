function enter(pi) {
    if (pi.getPlayerCount(300010420) != 0) {
        pi.getPlayer().dropMessage(5, "잠시 후에 다시 시도하여 주시길 바랍니다.");
        return;
    }
    pi.resetMap(300010420);
    pi.warp(300010420, "out00");
}