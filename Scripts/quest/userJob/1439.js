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
            if (qm.isQuestActive(1439) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("시험을 무사히 통과했군요. #b다른 차원의 헬레나#k님을 만나본 소감은 어떤가요. 놀랐나요? 이것이 바로 #b성스러운 돌#k이 가진 놀라움이지요. 다른 차원에 분신을 소환해 싸울 수 있게 하는 힘. 헬레나님이 당신을 위해 준비한 것이랍니다.");
            }
            break;
        }
        case 1: {
            qm.sendYesNo("진정한 궁수이신 헬레나님과의 전투는 당신을 진정한 궁수로의 길로 이끌죠... 당신의 성장이 느껴지시나요? 이제 남은 것은 전직 뿐이랍니다. 더 강력한 궁수, 레인저가 될 준비가 되었나요?");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(4031059, -1);
            qm.changeJob(311);
            qm.forceCompleteQuest();
            qm.sendNext("이제부터 당신은 #b레인저#k입니다. #b활#k의 마스터, 진정한 레인저로서 당신이 가진 힘을 마음껏 펼쳐보세요.");
            break;
        }
    }
}