function enter(pi) {
    if (pi.isQuestActive(25419) == true) {
        if (pi.getPlayerCount(910600100) > 0 || 
            pi.getPlayerCount(910600101) > 0 || 
            pi.getPlayerCount(910600102) > 0) {
            var quest = Packages.server.quest.MapleQuest.getInstance(25419).getName();
            pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            return false;
        }
        pi.warp(910600100, "out00");
        return true;
    }
    if (pi.isQuestActive(22556) == true) {
        pi.forceStartQuest(22598, "1");
        pi.playerMessage("수상쩍은 인형이 놓여 있는 건물이다. 안에서 잠겨 있는지 들어 갈 수 없다.");
        return false;
    }
    if (pi.isQuestActive(22559) == true) {
        if (pi.getPlayerCount(910600010) > 0) {
            var quest = Packages.server.quest.MapleQuest.getInstance(22559).getName();
            pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            return false;
        }
        pi.resetMap(910600010);
        pi.warp(910600010, 0);
        return true;
    }
    pi.playerMessage("수상쩍은 인형이 놓여 있는 건물이다. 안에서 잠겨 있는지 들어 갈 수 없다.");
}