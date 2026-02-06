function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() < 1) {
	pi.warp(925100300, 0);
    } else {
	pi.playerMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
    }
}