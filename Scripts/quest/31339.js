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
            qm.askAcceptDecline("자, 이쯤 되었으면 연료는 충분해. 준비가 되었으면 출발할까?");
            break;
        }
        case 1: {
            qm.forceStartQuest();
            qm.warp(240090800, 0);
            break;
        }
    }
}