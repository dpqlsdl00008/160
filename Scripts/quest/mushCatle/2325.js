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
            if (qm.isQuestActive(2325) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("도... 도와주세요. 무서워요...");
            break;
        }
        case 1: {
            qm.sendNextPrev("네? 형이 보내서 오셨다구요? 아아... 이제 살았어요. 정말 고맙습니다.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}