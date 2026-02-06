function enter(pi) {
    if (pi.isQuestActive(24077) == true) {
        if (pi.getPlayerCount(910150230) < 1) {
            pi.warp(910150230, 0);
        } else {
            var quest = Packages.server.quest.MapleQuest.getInstance(24077).getName();
            pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
        }
    } else {
        pi.warp(101050020, 0);
    }
}