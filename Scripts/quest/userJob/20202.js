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
            if (qm.isQuestActive(20202) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendAcceptDecline("시험의 증표를 모두 가져오셨군요! 정말 대단해요! 당신께는 정식 기사가 될 자격이 충분해요! 정식 기사의 길을 가시겠어요?");
            }
            break;
        }
        case 1: {
            if (qm.isQuestActive(20202) == true) {
                qm.gainItem(4032096, -30);
                qm.changeJob(1210);
                qm.forceCompleteQuest();
            }
            qm.sendNext("이제 당신은 더 이상 수련 기사가 아니예요. 시그너스 기사단의 정식 기사예요!");
            break;
        }
        case 2: {
            qm.sendNextPrev("당신께 #bSP#k를 드렸어요. 플레임 위자드의 스킬 중에 정식 기사에게만 허락 된 스킬들도 전수해 드렸으니, 플레임과 함께 더 강해지세요!");
            break;
        }
        case 3: {
            qm.sendNextPrev("자, 그럼 시그너스 기사단의 정식기사로서 더욱 열정을 갖고 노력해 주세요!");
            break;
        }
        case 4: {
            qm.dispose();
            break;
        }
    }
}