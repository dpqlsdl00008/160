function enter(pi) {
    if (pi.isQuestActive(24079) == true) {
        if (pi.getPlayerCount(910510400) < 1) {
            pi.resetMap(910510400);
            pi.warp(910510400, "out00");
            pi.spawnNpc(1033225, 599, 255);
        } else {
            var quest = Packages.server.quest.MapleQuest.getInstance(24079).getName();
            pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
        }
    } else {
        pi.warp(105000000, 9);
    }
}