function enter(pi) {
    pi.playPortalSE();
    switch (pi.getMapId()) {
	case 130000000: pi.warp(130000100, 5); break;
	case 130000200: pi.warp(130000100, 4); break;
	case 140010100: pi.warp(140010110, 0); break;
	case 120000101: pi.warp(120000105, 0); break;
	case 103000003: pi.warp(103000008, 0); break;
	case 100000201: pi.warp(100000204, 0); break;
	default: pi.warp(pi.getMapId() + 1, 0); break;
    }
}