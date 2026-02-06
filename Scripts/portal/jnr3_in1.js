function enter(pi) {
    if (pi.getMap().getReactorByName("jnr3_out2").getState() == 1) {
        pi.warp(926110202, "out00");
    }
}