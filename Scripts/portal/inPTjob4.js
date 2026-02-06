function enter(pi) {
    if (pi.getQuestStatus(25121) > 0) {
        var v1 = true;
        if (pi.getPlayerCount(915010200) != 0) {
            v1 = false;
        }
        if (pi.getPlayerCount(915010201) != 0) {
            v1 = false;
        }
        if (!v1) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(915010200);
        pi.resetMap(915010201);
        pi.warp(915010200, "out00");
    }
}