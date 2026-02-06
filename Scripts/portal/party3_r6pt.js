function enter(pi) {
    if (pi.getEventManager("PQ_Orbis") != null && pi.getEventManager("PQ_Orbis").getProperty("stage6_" + (pi.getPortal().getName().substring(2, 5)) + "").equals("1")) {
	if (pi.getPortal().getId() > 23 && pi.getPortal().getId() < 28) {
	    pi.warp(-1, 6);
	} else if (pi.getPortal().getId() > 27 && pi.getPortal().getId() < 32) {
	    pi.warp(-1, 7);
	} else if (pi.getPortal().getId() > 31 && pi.getPortal().getId() < 36) {
	    pi.warp(-1, 8);
	} else if (pi.getPortal().getId() > 35 && pi.getPortal().getId() < 40) {
	    pi.warp(-1, 9);
	} else if (pi.getPortal().getId() > 39 && pi.getPortal().getId() < 44) {
	    pi.warp(-1, 10);
	} else if (pi.getPortal().getId() > 43 && pi.getPortal().getId() < 48) {
	    pi.warp(-1, 11);
	} else if (pi.getPortal().getId() > 47 && pi.getPortal().getId() < 52) {
	    pi.warp(-1, 12);
	} else if (pi.getPortal().getId() > 51 && pi.getPortal().getId() < 56) {
	    pi.warp(-1, 13);
	}
	pi.getMap().changeEnvironment("an" + pi.getPortal().getName().substring(3, 5) + "", 2);
    } else {
	pi.warp(-1, "np16");
    }
}