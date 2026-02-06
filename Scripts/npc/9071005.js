var status = -1;

function start() {
    action(1, 0, 0);
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
            cm.sendYesNo("왜? 벌써 나가려고? 아직 재미있는 일이 많이 남았는데?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(951000000, 0);
            break;
        }
    }
}