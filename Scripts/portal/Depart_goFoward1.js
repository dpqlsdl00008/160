function enter(pi) {
    if (pi.getMapId() == 103040450) {
        if (pi.isQuestActive(2288) == false) {
            return;
        }
        if (pi.getPlayerCount(103040460) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(103040460);
    }
    pi.warp(pi.getMapId() + 10, "right01");
}