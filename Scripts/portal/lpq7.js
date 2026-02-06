function enter(pi) {
    if (pi.getPlayer().getEventInstance().getProperty("stage4status") == null) {
	pi.playerMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
    } else {
	pi.warp(pi.getMapId() + 100, "st00");
    }
}