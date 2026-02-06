function enter(pi) {
    if (pi.getQuestStatus(2605) > 0 && pi.getQuestStatus(2609) < 2) {
	pi.warp(103050500, 0);
    } else {
	pi.getPlayer().dropMessage(5, "문이 잠겨있어 접근할 수 없습니다.");
    }
}
