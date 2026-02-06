function enter(pi) {
    if (pi.getPlayer().getParty() != null && pi.isLeader() && pi.getEventManager("PQ_Orbis").getProperty("6stage").equals("0")) {
	pi.warpParty(920010400);
	pi.playPortalSE();
    } else {
	pi.playerMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
    }
}