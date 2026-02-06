function enter(pi) {
    var v1 = pi.getMapId();
    var v2 = 0;
    switch (v1) {
        case 130030001: {
            v2 = 20010;
            break;
        }
        case 130030002: {
            v2 = 20011;
            break;
        }
        case 130030003: {
            v2 = 20012;
            break;
        }
        case 130030004: {
            v2 = 20013;
            break;
        }
    }
    var v3 = Packages.server.quest.MapleQuest.getInstance(v2).getName();
    var v4 = false;
    if (v2 != 20010) {
        if (pi.isQuestFinished(v2) == true) {
            v4 = true;
        }
    } else {
        if (pi.isQuestActive(v2) == true) {
            v4 = true;
        }
    }
    if (v4 == false) {
        pi.getPlayer().dropMessage(5, "<" + v3 + "> 퀘스트를 " + (v2 != 20010 ? "완료" : "진행") + " 후에 이동 할 수 있습니다.");
        return;
    }
    pi.warp(pi.getMapId() + 1, "sp");
}