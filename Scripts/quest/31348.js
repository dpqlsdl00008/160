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
            if (qm.isQuestActive(31348) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.lockInGameUI(true);
            qm.sendNextS("여신의 반려 동물이 이토록이나 사악하고 포악한 몬스터로 탄생하다니...?", 17);
            break;
        }
        case 1: {
            qm.sendNextPrevS("후후후... 이것은 시작일 뿐, 앞으로의 일을 더욱 기대해도 좋을 것이다.", 5, 2210009, 2210009);
            break;
        }
        case 2: {
            qm.sendNextPrevS("#b(수상한 자들의 목소리가 바람을 타고 저 멀리로 사라졌다...)", 17);
            break;
        }
        case 3: {
            qm.dispose();
            qm.lockInGameUI(false);
            qm.warp(240092300, "pt_west");
            qm.forceCompleteQuest();
            break;
        }
    }
}