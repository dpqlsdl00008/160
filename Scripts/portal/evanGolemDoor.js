function enter(pi) {
    pi.playPortalSE();
    if (pi.isQuestActive(22557) == true) {
        if (pi.getPlayerCount(910600000) > 0) {
            var quest = Packages.server.quest.MapleQuest.getInstance(22557).getName();
            pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            return false;
        }
        pi.resetMap(910600000);
        pi.warp(910600000, 0);
        pi.spawnNpc(1013201, 334, 305);
    } else {
        pi.warp(100040000, 3);
    }
    return true;
}