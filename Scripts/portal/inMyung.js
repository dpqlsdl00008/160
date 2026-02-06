function enter(pi) {
    if (pi.isQuestActive(2614) == true) {
        if (pi.getPlayerCount(910350200) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.warp(910350200, "out00");
        return;
    }
    pi.warp(103000002, "out00");
}