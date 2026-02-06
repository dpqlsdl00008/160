function enter(pi) {
    var quest = Packages.server.quest.MapleQuest.getInstance(24061).getName();
    if (pi.isQuestActive(24061) == true) {
        if (pi.haveItem(4032965, 1) == true) {
            if (pi.getPlayerCount(910080020) < 1) {
                pi.resetMap(910080020);
                pi.warp(910080020, "out00");
            } else {
                pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            }
        } else {
            pi.getPlayer().dropMessage(5, "버섯 페로몬 향수 병을 소지 중에 있지 않아 입장 할 수 없습니다.");
        }
    } else {
        pi.getPlayer().dropMessage(5, quest + " 퀘스트를 진행 중에만 입장 할 수 있습니다.");
    }
}