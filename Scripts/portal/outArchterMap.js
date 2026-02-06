function enter(pi) {
    if (pi.isQuestActive(24071) == true) {
        if (pi.getPlayerCount(910080010) < 1) {
            pi.resetMap(910080010);
            pi.warp(910080010, 0);
            pi.spawnNpc(1033223, 5675, 454);
            return;
        } else {
            var quest = Packages.server.quest.MapleQuest.getInstance(24071).getName();
            pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            return;
        }
    }
    pi.warp(100000000, "Achter00");
}