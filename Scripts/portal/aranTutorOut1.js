function enter(pi) {
    if (pi.isQuestActive(21000) == true) {
        pi.teachSkill(20000017, 0, -1);
        pi.teachSkill(20000018, 0, -1);
        pi.teachSkill(20000017, 1, 0);
        pi.teachSkill(20000018, 1, 0);
        pi.warp(914000200, 1);
    }
}