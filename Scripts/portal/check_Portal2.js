function enter(pi) {
    if (pi.isQuestFinished(31171) == true && pi.isQuestActive(31172) == false) {
        pi.warp(272000310, "west00");
    } else {
        pi.warp(272000400, "west00");
    }
}