function enter(pi) {
    if (pi.isQuestFinished(34460) == false) {
        pi.getPlayer().dropMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
        return;
    }
    pi.warp(450005000, "west00");
}