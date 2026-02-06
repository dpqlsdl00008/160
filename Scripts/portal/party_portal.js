function enter(pi) {
    value = 10200000;
    var cMap = ((pi.getMapId() - 910340000) / 100);
    var pMember = pi.getParty().getMembers();
    if (pMember != null) {
        var it = pMember.iterator();
        while (it.hasNext()) {
            var cUser = it.next();
            var ccUser = pi.getChannelServer().getMapFactory().getMap(pi.getMapId()).getCharacterByName(cUser.getName());
            if (ccUser != null) {
                if (ccUser != pi.getPlayer() && !ccUser.getOneInfoQuest(value, "pq_kerningcity_answer").equals(cMap + "")) {
                    pi.playerMessage(5, "현재 이 포탈을 사용 할 수 없습니다.");
                    return;
                }
            }
        }
        pi.warp(pi.getMapId() + 100, 1);
    }
}