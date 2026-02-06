function act() {
    rm.getPlayer().dropMessage(5, "해적선의 문을 닫았습니다.");
    var cReactor = [0, 1, 2, 3];
    var cClear = true;
    for (i = 0; i < cReactor.length; i++) {
        if (rm.getMap().getReactorById(2519000 + i).getState() != 1) {
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
                        ccUser.updateQuest(7046, "2");
                    }
                }
            }
        }
        rm.getMap().setMobGen(9300120, false);
        rm.getMap().setMobGen(9300121, false);
        rm.getMap().setMobGen(9300122, false);
        rm.getMap().setMobGen(9300126, false);
        rm.getMap().resetFully();
        rm.showFieldEffect(true, "quest/party/clear");
        rm.playSound(true, "Party1/Clear");
    }
}