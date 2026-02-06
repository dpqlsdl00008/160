function enter(pi) {
    var cMap = pi.getPlayer().getMap();
    if (cMap.getAllMonster().isEmpty() && cMap.getReactorById(2408003).getState() == 1) {
        pi.warp(cMap.getId() + 100);
    } else {
        pi.playerMessage(5, "아직 이 포탈을 사용 할 수 없습니다.");
    }
}