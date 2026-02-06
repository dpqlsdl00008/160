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
            if (qm.isQuestActive(20205) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendAcceptDecline("시험의 증표를 모두 가져왔네? 아하하! 너라면 잘 할 줄 알았어. 너에게 정식 기사가 될 자격이 있음을 확인하였노라! 하하. 이대로 정식 기사가 되겠어?");
            }
            break;
        }
        case 1: {
            if (qm.isQuestActive(20205) == true) {
                qm.gainItem(4032096, -30);
                qm.changeJob(1510);
                qm.forceCompleteQuest();
            }
            qm.sendNext("이제 넌 더 이상 수련 기사가 아니야. 시그너스 기사단의 정식 기사지.");
            break;
        }
        case 2: {
            qm.sendNextPrev("너한테 #bSP#k를 줬어. 스트라이커의 정식 기사에게만 허락 된 스킬들을 찍어볼 수 있을 거야. 라이트닝과 결합 된 더 강한 스킬들, 잘 올려봐.");
            break;
        }
        case 3: {
            qm.sendNextPrev("뭐, 시그너스 기사단의 정식 기사라고 너무 심각하게 생각하지는 말았으면 좋겠어. 아무리 어려운 일이라도 즐거움을 찾자고.");
            break;
        }
        case 4: {
            qm.dispose();
            break;
        }
    }
}