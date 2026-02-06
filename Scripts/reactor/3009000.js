function act() {
    rm.gainItem(4001162, -1);
    rm.topMessage("가시 덤불 정화 : " + rm.getReactor().getState() + " / 4 회");
    if (rm.getReactor().getState() == 4) {
        if (rm.getParty() != null) {
            var pMember = rm.getParty().getMembers();
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = rm.getChannelServer().getMapFactory().getMap(rm.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        ccUser.updateQuest(7917, "1");
                    }
                }
            }
        }
        rm.showFieldEffect(true, "quest/party/clear");
        rm.playSound(true, "Party1/Clear");
    }
}