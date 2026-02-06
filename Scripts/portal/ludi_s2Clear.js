function enter(pi) {
    if (pi.getPlayer().getOneInfoQuest(10200000, "pq_ludibrium_clear").equals("2")) {
        pi.warp(pi.getMapId() + 200, "st00");
        return;
    }
    pi.getPlayer().dropMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
}