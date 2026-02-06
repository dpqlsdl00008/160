function enter(pi) {
    var v1 = true;
    var v2 = "";
    if (pi.getPlayer().getIntNoRecord(7923) < 4) {
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
        pi.warpParty(pi.getMapId() + 3);
    } else {
        pi.getPlayer().dropMessage(-1, v2);
        pi.getPlayer().dropMessage(5, v2);
    }
}