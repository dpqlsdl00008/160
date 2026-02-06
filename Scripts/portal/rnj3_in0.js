function enter(pi) {
    if (pi.getMap().getReactorByName("rnj3_out1").getState() == 1) {
        pi.warp(926100201, "out00");
    }
}