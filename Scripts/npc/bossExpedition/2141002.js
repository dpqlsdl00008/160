var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendYesNo("원정대를 그만두고 나가시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(270050000, "out00");
            break;
        }
    }
}