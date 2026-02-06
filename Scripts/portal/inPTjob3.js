function enter(pi) {
    if (pi.isQuestActive(25111) == true) {
        var v1 = true;
        if (pi.getPlayerCount(915010100) != 0) {
            v1 = false;
        }
        if (pi.getPlayerCount(915010100) != 0) {
            v1 = false;
        }
        if (!v1) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(915010100);
        pi.resetMap(915010101);
        pi.warp(915010100, "out00");
    }
}