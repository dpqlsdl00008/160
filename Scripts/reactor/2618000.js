function act() {
    rm.gainItem(4001132, -1);
    var rName = ["beaker1", "beaker2", "beaker3"];
    var cClear = true;
    for (i = 0; i < rName.length; i++) {
        if (rm.getMap().getReactorByName(rName[i]).getState() != 7) {
            cClear = false;
        }
    }
    if (cClear) {
        if (rm.getParty() != null) {
            var pMember = rm.getParty().getMembers();
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = rm.getChannelServer().getMapFactory().getMap(rm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        ccUser.updateQuest(7923, "2");
                    }
                }
            }
        }
        rm.showFieldEffect(true, "quest/party/clear");
        rm.playSound(true, "Party1/Clear");
        rm.getMap().getReactorByName(rm.getMapId() == 926100100 ? "rnj2_door" : "jnr2_door").forceHitReactor(3);
    }
}