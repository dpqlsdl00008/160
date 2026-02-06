function enter(pi) {
    if (pi.haveItem(4032860) == false) {
	pi.playerMessage(5, "공중 감옥을 탈출 하기 위해서는 열쇠가 필요합니다.");
	return;
    }
    pi.warp(211070100, 0);
}