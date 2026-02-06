function enter(pi) {
    if (pi.isQuestFinished(34466) == false) {
        pi.getPlayer().dropMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
        return;
    }
    if (pi.getPlayer().getQuestStatus(34467) > 0) {
        pi.warp(940200216, "out00");
    } else {
        pi.warp(940200218, 0);
    }
}