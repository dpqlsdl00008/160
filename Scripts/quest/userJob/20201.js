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
            if (qm.isQuestActive(20201) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendAcceptDecline("시험의 증표를 모두 가져왔군... 좋다. 그대에게는 정식 기사가 될 자격이 충분하다. 정식 기사의 길을 가겠는가?");
            }
            break;
        }
        case 1: {
            if (qm.isQuestActive(20201) == true) {
                qm.gainItem(4032096, -30);
                qm.changeJob(1110);
                qm.forceCompleteQuest();
            }
            qm.sendNext("이제 수련 기사로서의 그대는 없다. 그대는 시그너스 기사단의 정식 기사다.");
            break;
        }
        case 2: {
            qm.sendNextPrev("그대에게 #bSP#k를 주었다. 소울 마스터의 스킬 중, 정식 기사에게만 허락 된 스킬을 몇 가지 전수했으니 소울과 함께 더욱 강해지도록.");
            break;
        }
        case 3: {
            qm.sendNextPrev("시그너스 기사단의 정식 기사로서 부끄럽지 않은 몸가짐을 갖춰 여제의 명예를 높이도록.");
            break;
        }
        case 4: {
            qm.dispose();
            break;
        }
    }
}