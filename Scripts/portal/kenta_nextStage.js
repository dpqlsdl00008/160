function enter(pi) {
    var v1 = true;
    var v2 = "";
    switch (pi.getMapId()) {
        case 923040100: {
            if (pi.getMap().getAllMonstersThreadsafe().size() != 0) {
                v2 = "몬스터가 " + pi.getMap().getAllMonstersThreadsafe().size() + "마리 남았습니다.";
                v1 = false;
            }
            break;
        }
        case 923040200: {
            if (pi.getPlayer().getIntNoRecord(7927) < 1) {
                if (pi.getPlayer().haveItem(2430364, 20) == true) {
                    var pMember = pi.getParty().getMembers();
                    if (pMember != null) {
                        var it = pMember.iterator();
                        while (it.hasNext()) {
                            var cUser = it.next();
                            var ccUser = pi.getChannelServer().getMapFactory().getMap(pi.getMapId()).getCharacterByName(cUser.getName());
                            if (ccUser != null) {
                                pi.getMap().setMobGen(9300446, false);
                                pi.getMap().setMobGen(9300447, false);
                                pi.getMap().resetFully();
                                pi.showFieldEffect(true, "quest/party/clear");
                                pi.playSound(true, "Party1/Clear");
                                ccUser.removeAll(2430364);
                                ccUser.updateQuest(7927, "1");
                                return;
                            }
                        }
                    }
                } else {
                    v1 = false;
                }
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
        pi.warpParty(pi.getMapId() + 100);
    } else {
        pi.getPlayer().dropMessage(-1, v2);
        pi.getPlayer().dropMessage(5, v2);
    }
}