function enter(pi) {
    try {
        pi.warp(103000100, 2);
    } catch (e) {
        pi.playerMessage(5, "Error: " + e);
        e.printStackTrace();
    }
}