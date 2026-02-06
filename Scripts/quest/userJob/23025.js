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
            if (qm.isQuestActive(23025) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("이게 바로 그 블랙윙의 보고서로군요. 과연... 이 문서만 있다면 앞으로 레지스탕스의 방향성이 달라질 수도 있을 것 같습니다. 잘 하셨습니다.");
            }
            break;
        }
        case 1: {
            qm.sendNextPrev("이 임무는 원래 당신의 것이 아니였습니다만, 일부러 당신계 맡겼습니다. 그 블랙윙... 아마도 당신 손으로 꼭 물리치고 싶지 않을까 싶어서요. 복수를 위해서라도 더 훌륭한 임무를 완수하리라 기대했습니다.");
            break;
        }
        case 2: {
            qm.sendYesNo("좀 더 나중의 일이 될 거라고 생각했지만... 당신의 성장이 이 정도라면 고민할 필요 없겠군요. 지금 당신을 다음 단계로 성장시키겠습니다. 이전까지와는 다른 더 많은 기계를 자유자제로 다룰 수 있는 강력한 메카닉으로 말입니다.");
            break;
        }
        case 3: {
            if (qm.isQuestActive(23025) == true) {
                qm.gainItem(4032739, -1);
                qm.changeJob(3510);
                qm.forceCompleteQuest();
            }
            qm.sendNext("당신을 전직시켰습니다. 동시에 제가 알고 있는 더 많은 지식과 스킬을 전수했습니다. 이제 당신은 예전의 당신이 아닙니다. 더 진화한 형태의 메카닉, 그것이 당신입니다. 당신에게 주어진 새로운 힘을 마음껏 즐기십시오.");
            break;
        }
        case 4: {
            qm.dispose();
            qm.sendNextPrev("그럼 다음 수업게 뵙겠습니다. 그때까지 레지스탕스로서 멋지게 활약하길 기대하겠습니다.");
            break;
        }
    }
}