function enter(pi) {
    var qMap = [
    [24088, 931040000],
    [25416, 931040100],
    ];
    for (i = 0; i < qMap.length; i++) {
        if (pi.isQuestActive(qMap[i][0]) == true) {
            if (pi.getPlayerCount(qMap[i][1]) < 1) {
                pi.resetMap(qMap[i][1]);
                pi.warp(qMap[i][1], "out00");
            } else {
                var quest = Packages.server.quest.MapleQuest.getInstance(qMap[i][0]).getName();
                pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            }
            return;
        }
    }
    pi.warp(931020010, "out00");
}