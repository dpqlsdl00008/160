function enter(pi) {
    var v1 = true;
    var v2 = "";
    if (pi.getPlayer().getIntNoRecord(7930) != 2) {
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
        var pMember = pi.getParty().getMembers();
        if (pMember != null) {
            var it = pMember.iterator();
            while (it.hasNext()) {
                var cUser = it.next();
                var ccUser = pi.getChannelServer().getMapFactory().getMap(pi.getMapId()).getCharacterByName(cUser.getName());
                if (ccUser != null) {
                    ccUser.updateQuest(7930, "01230");
                }
            }
        }
        pi.warpParty(pi.getMapId() + 97);
    } else {
        pi.getPlayer().dropMessage(-1, v2);
        pi.getPlayer().dropMessage(5, v2);
    }
}