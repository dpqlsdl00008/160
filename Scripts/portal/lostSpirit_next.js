function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() != 0) {
        pi.getPlayer().dropMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
        return;
    }
    if (pi.getMapId() == 940200240) {
        pi.warp(940200210, 0);
    } else {
        pi.warp(pi.getMapId() + 10, 0);
    }
}