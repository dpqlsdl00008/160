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
            qm.sendAcceptDecline("뭐... 영 믿음은 안 가지만 네가 기사 단원이라니 어쩔 수 없군. 조사할 사람이 또 있는 것도 아니고... 이번 일에 대해 설명해 주지.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}