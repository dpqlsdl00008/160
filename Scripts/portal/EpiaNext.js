function enter(pi) {
    if (pi.getPlayer().getMap().getAllMonstersThreadsafe().isEmpty() == false) {
        return;
    }
    pi.warp(921140001, "out00");
}