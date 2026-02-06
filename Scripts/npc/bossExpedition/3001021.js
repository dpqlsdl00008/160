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
            cm.sendYesNoS("전투를 마치고 밖으로 나갈까?", 2);
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(401060000, "enter_magnusDoor");
            break;
        }
    }
}