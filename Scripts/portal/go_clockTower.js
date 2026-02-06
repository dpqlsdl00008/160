function enter(pi) {
    if (pi.getPlayer().getQuestStatus(34326) == 0) {
        pi.getPlayer().dropMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
        return;
    }
    pi.warp(450003500, "bottom00");
}