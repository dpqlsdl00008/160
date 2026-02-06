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
            qm.sendAcceptDecline("혹시 도마뱀도 소처럼 #b건초 한 줌#k를 먹을 수도 있지 않을까? 주변에 #b건초더미#k들이 많이 있으니 궁금하다면 한번 먹여 보려무나. 안먹으면 그 때 다른 먹이를 구해 주면 되잖니.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}