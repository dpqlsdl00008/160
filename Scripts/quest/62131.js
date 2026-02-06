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
            qm.sendNext("...도와주세요!!!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("...? 무슨 소리가 들렸는데?...", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("위... 위를 봐요!! 여기에요!!");
            break;
        }
        case 3: {
            qm.sendNextPrev("보시다시피 전 여기에 갇혔어요... 강시가 사방에 널려있다구요!!");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}