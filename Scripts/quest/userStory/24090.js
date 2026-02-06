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
            if (qm.isQuestActive(24090) == true) {
                qm.sendYesNo("#b(정신을 잃고 쓰러져 있는 아이의 몸을 흔들었다. 어떻게 된 일인지 검은 마법사의 저주에서 풀려나 있다. 다행히도 몸은 따뜻하다... 숨을 쉬고 있는 것 같다. 정신을 잃은 것 뿐인가? 이러고 있지 말고 바로 마을로 돌아가자.)#k");
            } else {
                qm.dispose();
                qm.forceStartQuest();
            }
            break;
        }
        case 1: {
            qm.dispose();
            qm.warp(101050000, 0);
            qm.forceCompleteQuest();
            break;
        }
    }
}