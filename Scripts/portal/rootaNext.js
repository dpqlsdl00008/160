function enter(pi) {
    if (pi.getParty() == null) {
        pi.getPlayer().dropMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
        return;
    }
    if (!pi.isLeader()) {
        pi.getPlayer().dropMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
        return;
    }
    if (!pi.allMembersHere()) {
        pi.getPlayer().dropMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
        return;
    }
    if (pi.getMap().getAllMonstersThreadsafe().size() != 0) {
        pi.getPlayer().dropMessage(5, "먼저 정원 내의 임프를 제거하세요.");
        return;
    }
    // 그럼 이동해 볼까?
    pi.warpParty(pi.getMapId() + 10);
}