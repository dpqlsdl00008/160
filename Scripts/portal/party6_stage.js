function enter(pi) {
    var v1 = true;
    var v2 = "";
    switch(pi.getMapId()) {
        case 930000000: {
            pi.warp(930000010, 0);
            return;
        }
        case 930000010: {
            pi.warp(930000100, 0);
            return;
        }
        case 930000100: {
            if (pi.getMap().getAllMonstersThreadsafe().size() != 0) {
                v2 = "몬스터가 " + pi.getMap().getAllMonstersThreadsafe().size() + "마리 남았습니다.";
                v1 = false;
            }
            break;
        }
        case 930000200: {
            if (pi.getPlayer().getIntNoRecord(7917) < 1) {
                v1 = false;
            }
            break;
        }
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
        if (pi.getMapId() == 930000300) {
            pi.warpParty(pi.getMapId() + 200);
        } else {
            pi.warpParty(pi.getMapId() + 100);
        }
    } else {
        pi.getPlayer().dropMessage(-1, v2);
        pi.getPlayer().dropMessage(5, v2);
    }
}