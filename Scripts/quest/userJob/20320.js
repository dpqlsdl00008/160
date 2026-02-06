var status = -1;

function end(mode, type, selection) {
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
            if (qm.isQuestActive(20320) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendAcceptDecline("승급 시험을 통과하신 것을 축하드립니다. 이제부터 정식 기사입니다. 지금 바로 전직하시겠습니까?");
	    }
            break;
        }
        case 1: {
            qm.dispose();
            qm.changeJob(5111);
            qm.forceCompleteQuest();
            break;
        }
    }
}