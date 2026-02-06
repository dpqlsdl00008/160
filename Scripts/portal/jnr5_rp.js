function enter(pi) {
    var cFloor = -1;
    var pName = pi.getPortal().getName();
    var qRecord = pi.getPlayer().getStringRecord(7930);
    for (i = 0; i < 5; i++) {
        if (pName.substring(2, 3).equals(i + "")) {
            if (pName.substring(3, 4) == qRecord.substring(i, (i + 1))) {
                cFloor = (i == 4 ? 9 : i);
            }
        }
    }
    if (cFloor == -1) {
        pi.warp(-1, "npFail");
        return;
    }
    if (cFloor == 9) {
        var pMember = pi.getParty().getMembers();
        if (pMember != null) {
            var it = pMember.iterator();
            while (it.hasNext()) {
                var cUser = it.next();
                var ccUser = pi.getChannelServer().getMapFactory().getMap(pi.getMapId()).getCharacterByName(cUser.getName());
                if (ccUser != null) {
                    pi.showFieldEffect(true, "quest/party/clear");
                    pi.playSound(true, "Party1/Clear");
                }
            }
        }
    }
    pi.warp(-1, "np0" + cFloor);
    pi.getMap().changeEnvironment("an" + pName.substring(2, 4), 2);
}