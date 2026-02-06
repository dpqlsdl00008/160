function enter(pi) {
    if (pi.isQuestActive(3140) == true && pi.haveItem(4032833) == true) {
        if (pi.getPlayerCount(211060401) != 0) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(211060401);
        pi.warp(211060401, "down00");
        pi.getPlayer().dropMessage(5, "두 번째 탑루의 교도관 보어를 물리치자.");
        return;
    }
}