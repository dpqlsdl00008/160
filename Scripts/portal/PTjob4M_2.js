function enter(pi) {
    if (pi.isQuestActive(25122) == true ||
        pi.isQuestFinished(25121) == true) {
        pi.warp(915010201, "out00");
    }
}