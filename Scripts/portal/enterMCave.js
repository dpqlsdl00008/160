function enter(pi) {
    if (pi.isQuestActive(21201) == true) {
        var v1 = true;
        if (pi.getPlayerCount(914021000) != 0) {
            v1 = false;
        }
        if (pi.getPlayerCount(914021010) != 0) {
            v1 = false;
        }
        if (!v1) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(914021000);
        pi.resetMap(914021010);
        pi.warp(914021000, "out00");
        return;
    }
    if (pi.isQuestActive(21302) == true) {
        var v1 = true;
        if (pi.getPlayerCount(914022100) != 0) {
            v1 = false;
        }
        if (pi.getPlayerCount(914022200) != 0) {
            v1 = false;
        }
        if (!v1) {
            pi.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
            return;
        }
        pi.resetMap(914022100);
        pi.resetMap(914022200);
        pi.warp(914022100, "out00");
    }
}