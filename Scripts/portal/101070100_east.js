function enter(pi) {
    if (pi.isQuestActive(32103) == true) {
        pi.warp(101070001, "pt_west");
        return;
    }
    pi.warp(101070000, "pt_west");
}