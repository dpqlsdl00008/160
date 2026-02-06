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
            if (qm.isQuestActive(1663) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("비스트에게 어느 정도 말을 들었겠지만, 이번에 새로운 미스틱 게이트가 나타난 사태가 불어졌을 때 신속히 임무를 마친 #h #님에게 작은 선물을 드리고자 합니다.");
            break;
        }
        case 1: {
            qm.sendNextPrev("UI를 통해 이미 A급 헌터의 칭호를 받으셨겠죠? 안 반드셨다면 저와의 대화가 끝난 후 크로스 헌터 UI를 열어 보상을 받아주시면 됩니다. 또한 여기 소소하지만 임무에 대한 보상을 조금 더 얹어 보았습니다.");
            break;
        }
        case 2: {
            qm.sendNextPrev("다음 임무 때에도 기대하겠습니다. 그럼 잘 부탁드립니다.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.showFieldEffect(false, "crossHunter/chapter/end3");
            qm.forceCompleteQuest();
            break;
        }
    }
}