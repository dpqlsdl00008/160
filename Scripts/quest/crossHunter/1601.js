var status = -1;

function start(mode, type, selection) {
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
            qm.sendNext("곧 그들이 공격해올거예요! 저 뿐만 아니라 당신도 위험해질거예요. 그들을 물리쳐주세요!!");
            break;
        }
        case 1: {
            qm.dispose();
            qm.spawnMonster(9300470, 7, -156, 64);
            qm.forceStartQuest();
            break;
        }
    }
}