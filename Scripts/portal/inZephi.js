function enter(pi) {
    if (pi.isQuestActive(2631) == true) {
        if (pi.getPlayerCount(910100200) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(910100200);
        pi.warp(910100200, "out00");
    }
}