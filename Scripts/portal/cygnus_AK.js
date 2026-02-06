function enter(pi) {
    if (pi.isQuestActive(20752) == true) {
        pi.forceCompleteQuest(20752);
    }
    if (pi.isQuestActive(20755) == true ||
        pi.isQuestActive(20756) == true ||
        pi.isQuestActive(20757) == true ||
        pi.isQuestFinished(20757) == true) {
        pi.warp(927010000, "out00");
    }
}