function enter(pi) {
    if (pi.getMonsterCount(pi.getMapId()) != 0) {
        return;
    }
    if (pi.getPlayerCount(915010001) != 0) {
        pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
        return;
    }
    pi.resetMap(915010001);
    pi.warp(915010001, "out00");
}