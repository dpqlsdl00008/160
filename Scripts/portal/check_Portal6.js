function enter(pi) {
    if (pi.isQuestActive(31178) == false) {
        return;
    }
    if (pi.getPlayerCount(272010200) != 0) {
        pi.getPlayer().dropMessage(5, "잠시 후에 다시 시도하여 주시길 바랍니다.");
        return;
    }
    pi.resetMap(272010200);
    pi.warp(272010200, "west00");
}