function enter(pi) {
    if (pi.isQuestActive(31348) == true) {
        if (pi.getPlayerCount(924030000) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(924030000);
        pi.warp(924030000, 0);
        return;
    }
    pi.warp(240092400, "pt_west");
}