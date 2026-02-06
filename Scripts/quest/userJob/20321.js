var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendYesNo("시험 준비는 모두 끝나셨나요? 지금 바로 시험장으로 입장하셔도 괜찮겠습니까?");
            break;
        }
        case 1: {
            qm.dispose();
            if (qm.getPlayerCount(913070200) < 1) {
                qm.resetMap(913070200);
                qm.warp(913070200, 0);
            } else {
                var quest = Packages.server.quest.MapleQuest.getInstance(20321).getName();
                qm.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            }
            break;
        }
    }
}