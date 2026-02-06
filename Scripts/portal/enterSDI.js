function enter(pi) {
    if (pi.isQuestActive(22588) == true) {
        if (pi.getPlayerCount(914100022) > 0) {
            var quest = Packages.server.quest.MapleQuest.getInstance(22588).getName();
            pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            return false;
        }
        pi.resetMap(914100022);
        pi.warp(914100022, "out00");
    }
    return true;
}