function enter(pi) {
    var v0 = true;
    if (pi.isQuestActive(24090) == true) {
        if (pi.getPlayerCount(931040010) != 0) {
            v0 = false;
        }
        if (pi.getPlayerCount(931040011) != 0) {
            v0 = false;
        }
        if (!v0) {
            var quest = Packages.server.quest.MapleQuest.getInstance(24090).getName();
            pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            return;
        }
        pi.resetMap(931040010);
        pi.resetMap(931040011);
        pi.warp(931040010, "out00");
        return;
    }
    var v1 = false;
    if (pi.isQuestActive(23033) == true) {
        v1 = true;
    }
    if (pi.isQuestActive(23034) == true) {
        v1 = true;
    }
    if (pi.isQuestActive(23035) == true) {
        v1 = true;
    }
    if (!v1) {
        return;
    }
    if (pi.getPlayerCount(931000200) != 0) {
        var quest = Packages.server.quest.MapleQuest.getInstance(24090).getName();
        pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
        return;
    }
    pi.resetMap(931000200);
    pi.warp(931000200, 0);
}