function act() {
    rm.gainItem(4001528, -1);
    var rName = ["key1", "key2", "key3", "key4"];
    var cClear = true;
    for (i = 0; i < rName.length; i++) {
        if (rm.getMap().getReactorByName(rName[i]).getState() != 1) {
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
                        ccUser.updateQuest(7929, "3");
                    }
                }
            }
        }
        rm.getMap().setMobGen(9300452, false);
        rm.getMap().setMobGen(9300453, false);
        rm.getMap().resetFully();
        rm.showFieldEffect(true, "quest/party/clear");
        rm.playSound(true, "Party1/Clear");
    }
}