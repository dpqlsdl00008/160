function enter(pi) {
    var returnMap = pi.getSavedLocation("MULUNG_TC");
    if (returnMap < 0) {
        returnMap = 100000000;
    }
    pi.playPortalSE();
    pi.clearSavedLocation("MULUNG_TC");
    pi.warp(returnMap, "unityPortal2");
}