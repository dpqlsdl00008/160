function enter(pi) {
    var cMap = pi.getPlayer().getMap();
    if (cMap.getReactorById(2408003).getState() < 1) {
        pi.mapMessage(5, "동굴 깊은 곳에서 무시 무시한 생명체가 나타납니다.");
        var rPos = cMap.getReactorById(2408003).getPosition();
        pi.spawnMonster(8810025, 1, rPos.x + 100, rPos.y);
        cMap.getReactorById(2408003).forceHitReactor(0);
        cMap.getReactorById(2408003).forceHitReactor(1);
    }
}