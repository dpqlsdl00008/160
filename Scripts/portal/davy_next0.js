function enter(pi) {
    var v1 = true;
    var v2 = "";
    if (pi.getMap().getAllMonstersThreadsafe().size() != 0) {
        v2 = "몬스터가 " + pi.getMap().getAllMonstersThreadsafe().size() + "마리 남았습니다.";
        v1 = false;
    }
    if (pi.getPlayer().getParty() == null) {
        v1 = false;
    }
    if (pi.isLeader() == false) {
        v1 = false;
    }
    if (pi.allMembersHere() == false) {
        v1 = false;
    }
    if (v1) {
        pi.warpParty(925100100);
    } else {
        pi.getPlayer().dropMessage(-1, v2);
        pi.getPlayer().dropMessage(5, v2);
    }
}