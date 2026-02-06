function enter(pi) {
    if (pi.haveItem(4032766, 1) == true) {
        pi.openNpc(2153004);
    } else {
        pi.playerMessage("현재 이 포탈을 작동 할 수 없습니다.");
    }
}