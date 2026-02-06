function enter(pi) {
    if (pi.isQuestActive(2620) == true) {
        if (pi.getPlayerCount(910310300) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(910310300);
        pi.warp(910310300, "out00");
    }
}