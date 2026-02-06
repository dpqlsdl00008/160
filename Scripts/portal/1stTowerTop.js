function enter(pi) {
    if (pi.isQuestActive(3164) == true && pi.haveItem(4032858, 1) == true) {
        if (pi.getPlayerCount(921140100) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.gainItem(4032858, -1);
        pi.resetMap(921140100);
        pi.warp(921140100, "down00");
        return;
    }
    if (pi.isQuestActive(3139) == true && pi.haveItem(4032832) == true) {
        if (pi.getPlayerCount(211060201) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(211060201);
        pi.warp(211060201, "down00");
        pi.getPlayer().dropMessage(5, "첫 번째 탑루의 레드 크로키를 물리치자.");
        return;
    }
}