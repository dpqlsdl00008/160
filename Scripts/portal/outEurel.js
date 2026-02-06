function enter(pi) {
    if (pi.isQuestFinished(24040) == true) {
        pi.warp(101050100,2);
    } else {
        pi.openNpc(9000019, "merStoryQuest_0");
    }
}