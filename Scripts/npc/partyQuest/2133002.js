var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.sendYesNo("독 안개의 숲에서 나가시겠어요? -헬레나-");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(300030100, 0);
            break;
        }
    }
}