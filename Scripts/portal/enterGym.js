function enter(pi) {
    if (pi.getQuestStatus(21701) == 1) {
	pi.playPortalSE();
	pi.warp(914010000, 1);
    } else if (pi.getQuestStatus(21702) == 1) {
	pi.playPortalSE();
	pi.warp(914010100, 1);
    } else if (pi.getQuestStatus(21703) == 1) {
	pi.playPortalSE();
	pi.warp(914010200, 1);
    } else {
	pi.playerMessage(5, "푸오에게 수련을 받을 때만 펭귄 수련장에 입장할 수 있다.");
    }
}