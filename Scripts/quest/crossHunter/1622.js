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
            if (qm.isQuestActive(1622) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("오랜만이야. 애송이. 입단 테스트는 통과 한 모양이네?");
            break;
        }
        case 1: {
            qm.sendNextPrev("혼자서도 할 수 있는데 귀찮게 됬군. 난 누구랑 함께 일하는 스타일은 아니라서 말이야.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}