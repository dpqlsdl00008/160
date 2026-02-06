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
            qm.sendAcceptDecline("기사 단장이 된 모습을 바로 보시겠습니까? 보시고 나면 원하는 직업을 정하실 수 있을 겁니다. 직업을 정하면 바로 왼쪽에 있는 기사 단장들에게 말을 걸어 기사가 되십시오. 무엇을 선택하시건 그것이 당신의 길입니다.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.warp(913040100, 0);
            qm.forceCompleteQuest();
            break;
        }
    }
}