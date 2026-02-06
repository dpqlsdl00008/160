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
            qm.sendAcceptDecline("큰일났어요! 도와주세요!");
            break;
        }
        case 1: {
            qm.dispose();
            if (qm.haveItem(2030028) == false) {
                qm.gainItem(2030028, 1);
            }
            qm.forceStartQuest();
            break;
        }
    }
}