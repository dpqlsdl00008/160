var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
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
            cm.sendNext("드디어 도착했나, 계약자의 조건을 갖춘 자여...");
            break;
        }
        case 1: {
            cm.sendNextPrev("계약의 조건을 이행하라...");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(900090101, 0);
            break;
        }
    }
}