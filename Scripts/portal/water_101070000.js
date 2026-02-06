function enter(pi) {
    if (pi.isQuestActive(32102) == true) {
        pi.forceStartQuest(32135, "1");
        pi.warp(101070001, 0);
        return;
    }
    pi.warp(101071000, 0);
}