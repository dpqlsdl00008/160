function enter(pi) {
    if (pi.getMap().getReactorByName("rnj3_out2").getState() == 1) {
        pi.warp(926100202, "out00");
    }
}