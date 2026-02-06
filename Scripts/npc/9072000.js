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
            cm.sendYesNo("아름다운 지상 낙원, #d골드 비치#k로 이동하겠나?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(120040300, 0);
            break;
        }
    }
}