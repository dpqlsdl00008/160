function enter(pi) {
    if (pi.getMap().getReactorByName("jnr3_out1").getState() == 1) {
        pi.warp(926110201, "out00");
    }
}