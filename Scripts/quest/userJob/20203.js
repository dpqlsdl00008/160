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
            if (qm.isQuestActive(20203) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendAcceptDecline("시험의 증표... 모두 확인했습니다. 당신에게 정식 기사가 될 자격이 있음을 확인했습니다. 정식 기사의 길을 가시겠습니까?");
            }
            break;
        }
        case 1: {
            if (qm.isQuestActive(20203) == true) {
                qm.gainItem(4032096, -30);
                qm.changeJob(1310);
                qm.forceCompleteQuest();
            }
            qm.sendNext("이제 당신은 더 이상 수련 기사가 아닙니다. 시그너스 기사단의 정식 기사입니다.");
            break;
        }
        case 2: {
            qm.sendNextPrev("당신께 #bSP#k를 드렸습니다. 윈드 브레이커의 스킬 중에 정식 기사에게만 허락 된 스킬들도 전수해 드렸으니, 스톰과 함께 연마하시길.");
            break;
        }
        case 3: {
            qm.sendNextPrev("그럼 시그너스 기사단의 정식 기사로서 항상 이성적인 사고를 잊지 마시길...");
            break;
        }
        case 4: {
            qm.dispose();
            break;
        }
    }
}