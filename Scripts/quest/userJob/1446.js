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
            if (qm.isQuestActive(1446) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendYesNo("진정한 해적인 카이린님과의 전투는 자네를 진정한 해적으로 만들었다. 느껴지는가? 이제 남은 것은 전직 뿐. 더 강력한 해적, 발키리가 될 준비가 되었나?");
            }
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainItem(4031059, -1);
            qm.changeJob(521);
            qm.forceCompleteQuest();
            qm.sendNext("이제부터 자네는 #b발키리#k다. #b총#k의 마스터, 진정한 발키리로서 자네가 가진 힘을 마음껏 사용하게.");
            break;
        }
    }
}