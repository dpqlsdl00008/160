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
            cm.sendNextS("거기 누구냐!", 1, 0, 2101004);
            break;
        }
        case 1: {
            cm.dispose();
            cm.playerMessage(5, "경비에게 들켜 궁전에서 쫓겨났습니다.");
            cm.warp(260000300, 0);
            break;
        }
    }
}