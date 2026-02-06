function enter(pi) {
    if (pi.isQuestActive(23210) == true) {
        if (pi.getPlayerCount(931050100) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(931050100);
        pi.warp(931050100, 1);
        return;
    }
    if (pi.isQuestActive(23213) == true || pi.isQuestFinished(23214) == false) {
        if (pi.getPlayerCount(931050110) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.forceStartQuest(23206, "1");
        pi.resetMap(931050110);
        pi.warp(931050110, 1);
        return;
    }
}