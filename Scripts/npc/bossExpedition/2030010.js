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
            cm.sendYesNo("이 곳에서 나가겠는가? 다음 번에 들어올 때는 처음부터 다시 도전해야 한다네.");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(211042300, "west00");
            break;
        }
    }
}