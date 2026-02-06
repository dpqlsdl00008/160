function enter(pi) {
    if (pi.isQuestActive(21301) == true) {
        if (pi.getPlayerCount(914022000) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(914022000);
        pi.warp(914022000, "west00");
        return;
    }
    pi.warp(140020300, "west00");
}