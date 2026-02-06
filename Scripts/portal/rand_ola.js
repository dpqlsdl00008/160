function enter(pi) {
    if (pi.getEvent("올라올라").isRunning() && pi.getEvent("올라올라").isCharCorrect(pi.getPortal().getName(), pi.getMapId())) {
	pi.warp(pi.getMapId() == 109030003 ? 109050000 : (pi.getMapId() + 1), 0);
    } else {
	pi.warpS(pi.getMapId(), 0);
    }
}