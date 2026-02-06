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
            cm.sendYesNo("다시 오르비스로 돌아 가실 건가요? 그럼 저와 함께 가요.");
            break;
        }
        case 1: {
            cm.sendNext("\r\n지체하지 말고 바로 떠나 도록 해요.");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(200000000, 0);
            break;
        }
    }
}