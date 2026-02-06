function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() != 0) {
        pi.getPlayer().dropMessage(5, "몬스터를 모두 퇴치하셔야 다음 스테이지로 이동 할 수 있습니다.");
        return;
    }
    if (pi.isLeader() == false) {
        pi.getPlayer().dropMessage(5, "파티장만이 다음 스테이지로 이동 할 수 있습니다.");
        return;
    }
    if (pi.allMembersHere()) {
        pi.warpParty(pi.getMapId() + 100);
    } else {
        pi.getPlayer().tempban("", null, "mPark_nextStage", false);
        pi.getPlayer().dropMessage(1, "버그 악용으로 '이용 정지' 되었습니다.");
    }
}