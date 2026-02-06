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
            qm.sendAcceptDecline("#b(나는 스스로를 거대한 폴암을 사용하던 영웅이라고 확신하고 있는가? 확신한다면 힘을 주어 거대한 폴암을 잡자. 분명 뭔가 반응이 올 것이다.)#k");
            break;
        }
        case 1: {
            qm.sendNextS("#b(뭔가 기억이 떠오르는 것 같다...)#k", 3);
            break;
        }
        case 2: {
            qm.dispose();
            qm.warp(914090100, 0);
            break;
        }
    }
}