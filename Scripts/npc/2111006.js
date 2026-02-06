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
            cm.askAcceptDecline("지금 당장 연금술사를 보러 가보겠나?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(926120200, 0);
            break;
        }
    }
}