function enter(pi) {
    if (pi.isQuestActive(21731) == true) {
        if (pi.getPlayerCount(910510000) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.warp(910510000, "out00");
        return;
    }
    pi.warp(910050300, "out00");
}