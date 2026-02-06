function enter(pi) {
    pi.warp(-1, "np0" + pi.getPortal().getName().substring(2, 3));
    pi.getMap().changeEnvironment("an" + pi.getPortal().getName().substring(2, 4), 2);
    if (pi.getPortal().getName().equals("pt90")) {
        if (pi.getParty() != null) {
            var pMember = pi.getParty().getMembers();
            if (pMember != null) {
                var it = pMember.iterator();
                while (it.hasNext()) {
                    var cUser = it.next();
                    var ccUser = pi.getChannelServer().getMapFactory().getMap(pi.getMapId()).getCharacterByName(cUser.getName());
                    if (ccUser != null) {
                        ccUser.updateOneInfoQuest(10200000, "pq_ludibrium_clear", "3");
                    }
                }
            }
        }
        pi.showFieldEffect(true, "quest/party/clear");
        pi.playSound(true, "Party1/Clear");
    }
}