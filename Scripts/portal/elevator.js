
function enter(pi) {
    pi.playPortalSE();
    if (pi.getMapId() == 222020100) {
        pi.warp(222020200, "sp");
    } else {
        pi.warp(222020100, "sp");
    }
}