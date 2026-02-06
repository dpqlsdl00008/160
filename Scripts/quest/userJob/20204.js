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
            if (qm.isQuestActive(20204) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendAcceptDecline("시험의 증표를 모두 가져왔군. 생각보다... 아니, 겨우 이런 일로 칭찬해 줄 필요는 없겠지. 너에게 정식 기사가 될 자격이 있음을 확인했다. 이대로 정식 기사가 되겠나?");
            }
            break;
        }
        case 1: {
            if (qm.isQuestActive(20204) == true) {
                qm.gainItem(4032096, -30);
                qm.changeJob(1410);
                qm.forceCompleteQuest();
            }
            qm.sendNext("이제 넌 더 이상 수련 기사가 아니다. 시그너스 기사단의 정식 기사다.");
            break;
        }
        case 2: {
            qm.sendNextPrev("너에게 #bSP#k를 좀 줬어. 별로 많진 않지만 새로 전수한 스킬을 찍어 볼 수는 있겠지. 나이트 워커의 스킬 중 정식 기사에게 허락 된 스킬들이 어떤지 다크니스와 함께 시험해 보라고.");
            break;
        }
        case 3: {
            qm.sendNextPrev("그럼 시그너스 기사단의 정식 기사로서의 악에 물들지 않기를. 비록 어둠 속에 가려져 있다고 해도.");
            break;
        }
        case 4: {
            qm.dispose();
            break;
        }
    }
}