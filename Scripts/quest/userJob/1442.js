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
            if (qm.isQuestActive(1442) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendYesNo("진정한 도적인 다크로드와의 전투는 자네를 진정한 도적으로 만들었다... 본인은 잘 못 느끼겠지만 말이야. 이제 남은 것은 전직 뿐. 더 강력한 도적, 허밋이 될 준비가 되었나?");
            }
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainItem(4031059, -1);
            qm.changeJob(411);
            qm.forceCompleteQuest();
            qm.sendNext("이제부터 자네는 #b허밋#k이다. #b아대와 표창#k의 마스터, 진정한 허밋으로서 자네가 가진 힘을 마음껏 사용하게.");
            break;
        }
    }
}