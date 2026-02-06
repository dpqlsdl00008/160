function enter(pi) {
    var v1 = true;
    var v2 = "";
    switch (pi.getMapId()) {
        case 921160100: {
            if (pi.getPlayer().getIntNoRecord(7929) < 1) {
                var pMember = pi.getParty().getMembers();
                if (pMember != null) {
                    var it = pMember.iterator();
                    while (it.hasNext()) {
                        var cUser = it.next();
                        var ccUser = pi.getChannelServer().getMapFactory().getMap(pi.getMapId()).getCharacterByName(cUser.getName());
                        if (ccUser != null) {
                            pi.showFieldEffect(true, "quest/party/clear");
                            pi.playSound(true, "Party1/Clear");
                            ccUser.updateQuest(7929, "1");
                            return;
                        }
                    }
                }
            }
            break;
        }
        case 921160200: 
        case 921160400: {
            if (pi.getMap().getAllMonstersThreadsafe().size() != 0) {
                v2 = "몬스터가 " + pi.getMap().getAllMonstersThreadsafe().size() + "마리 남았습니다.";
                v1 = false;
            }
            break;
        }
        case 921160500: {
            if (pi.getPlayer().getIntNoRecord(7929) < 2) {
                var pMember = pi.getParty().getMembers();
                if (pMember != null) {
                    var it = pMember.iterator();
                    while (it.hasNext()) {
                        var cUser = it.next();
                        var ccUser = pi.getChannelServer().getMapFactory().getMap(pi.getMapId()).getCharacterByName(cUser.getName());
                        if (ccUser != null) {
                            pi.showFieldEffect(true, "quest/party/clear");
                            pi.playSound(true, "Party1/Clear");
                            ccUser.updateQuest(7929, "2");
                            return;
                        }
                    }
                }
            }
            break;
        }
        case 921160600: {
            if (pi.getPlayer().getIntNoRecord(7929) < 3) {
                v1 = false;
            }
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
        if (pi.getMapId() == 921160350) {
            pi.warpParty(pi.getMapId() + 50);
            return;
        }
        pi.warpParty(pi.getMapId() + 100);
    } else {
        pi.getPlayer().dropMessage(-1, v2);
        pi.getPlayer().dropMessage(5, v2);
    }
}