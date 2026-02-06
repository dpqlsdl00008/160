function enter(pi) {
    v1 = true;
    v2 = "";
    switch (pi.getMapId()) {
        case 923040300: {
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
                pi.warpParty(pi.getMapId() + 100);
            } else {
                pi.getPlayer().dropMessage(-1, v2);
                pi.getPlayer().dropMessage(5, v2);
            }
            return;
        }
        default: {
            pi.openNpc(9020004);
            break;
        }
    }
}