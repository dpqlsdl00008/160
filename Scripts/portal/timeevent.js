importPackage(Packages.tools);
importPackage(Packages.handling.channel);

function enter(pi) {
if (SystemUtils.getByTime(20, 59, 0) < pi.getCurrentTime() && SystemUtils.getByTime(21, 1, 0) > pi.getCurrentTime()) {
    pi.playPortalSE();
    pi.warp(910000000, "sp");
} else if (ChannelServer.getInstance(1).eventMapwarp > 0) {
    pi.warp(ChannelServer.getInstance(1).eventMapwarp, "sp");
} else {
    pi.playPortalSE();
    pi.warp(910022000, "sp");
}
}