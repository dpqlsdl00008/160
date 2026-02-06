function enter(pi) {
    if (pi.getQuestStatus(25100) > 0) {
        var v1 = true;
        if (pi.getPlayerCount(915010000) != 0) {
            v1 = false;
        }
        if (pi.getPlayerCount(915010001) != 0) {
            v1 = false;
        }
        if (!v1) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(915010000);
        pi.resetMap(915010001);
        pi.warp(915010000, "out00");
    }
}