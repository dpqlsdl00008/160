function enter(pi) {
    for (var i = 913070020; i < 913070040; i++) {
        if (pi.getPlayerCount(i) < 1) {
            pi.warp(i, 0);
            return true;
        } else {
            pi.playerMessage(1, "잠시 후에 다시 시도하여 주세요.");
            return false;
        }
    }
}