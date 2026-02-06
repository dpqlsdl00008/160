function enter(pi) {
    if (pi.isQuestActive(21739)) {
        if (pi.getPlayerCount(920030000) == 0 && pi.getPlayerCount(920030001) == 0) {
            pi.playPortalSE();
	    pi.warp(920030000, 2);
        } else {
            pi.playerMessage(5, "이미 누군가 전투를 진행중입니다. 나중에 다시 시도 해주세요.");
        }
    } else {
        pi.playPortalSE();
	pi.warp(200060001, 2);
    }
}