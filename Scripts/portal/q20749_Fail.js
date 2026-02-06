function enter(pi) {
    if (pi.isQuestActive(20749) == true) {
        if (pi.getPlayerCount(915010001) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(922000030);
        pi.warp(922000030, 0);
        pi.getPlayer().dropMessage(5, "문이 열리고, 다른 차원의 공간으로 들어 갑니다.");
    }
}