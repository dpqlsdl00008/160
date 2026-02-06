function enter(pi) {
    if (pi.getPlayer().getIntNoRecord(7930) > 0) {
        return;
    }
    var pMember = pi.getParty().getMembers();
    if (pMember != null) {
        var it = pMember.iterator();
        while (it.hasNext()) {
            var cUser = it.next();
            var ccUser = pi.getChannelServer().getMapFactory().getMap(pi.getMapId()).getCharacterByName(cUser.getName());
            if (ccUser != null) {
                ccUser.updateQuest(7930, "1");
                ccUser.dropMessage(6, "정체를 알 수 없는 과학자가 몬스터를 불러내고 황급히 사라졌다.");
            }
        }
    }
    pi.spawnMobOnMap(9300142, 5, 200, 243, pi.getMapId());
    pi.spawnMobOnMap(9300143, 5, 200, 243, pi.getMapId());
    pi.spawnMobOnMap(9300144, 5, 200, 243, pi.getMapId());
    pi.spawnMobOnMap(9300145, 5, 200, 243, pi.getMapId());
    pi.spawnMobOnMap(9300146, 5, 200, 243, pi.getMapId());
}