function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() != 0) {
        pi.getPlayer().dropMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
        return;
    }
    pi.warp(940200214, 0);
}