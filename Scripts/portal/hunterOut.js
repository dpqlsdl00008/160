function enter(pi) {
    var v1 = pi.getMap().getForcedReturnMap();
    pi.getPlayer().dropMessage(5, "[" + v1.getMapName() + "] 맵으로 이동합니다.");
    pi.warp(v1.getId(), 0);
}