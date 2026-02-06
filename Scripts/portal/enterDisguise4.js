function enter(pi) {
    if (pi.haveItem(4032179, 1) == false) {
        //pi.getPlayer().dropMessage(5, "에레브의 수색증을 가지고 있지 않으면 입장 하실 수 없습니다. 나인하트에게 수색증을 받으세요.");
        //return;
    }
    if (pi.isQuestActive(20301) == true || 
        pi.isQuestActive(20302) == true || 
        pi.isQuestActive(20303) == true || 
        pi.isQuestActive(20304) == true || 
        pi.isQuestActive(20305) == true) {
        if (pi.getPlayerCount(913002300) != 0) {
            var quest = Packages.server.quest.MapleQuest.getInstance(20301).getName();
            pi.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            return;
        }
        pi.resetMap(913002300);
        pi.getMap(913002300).spawnNpc(1104103, new java.awt.Point(-1766, 88));
        pi.warp(913002300, "out00");
        return;
    }
    pi.warp(130010120, "out00");
}