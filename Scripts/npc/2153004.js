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
            cm.sendYesNo("블랙윙의 비밀 포탈을 작동합니다. 블랙 토큰 1개를 소비하여 빅토리아 아일랜드로 이동하실 수 있습니다. 빅토리아 아일랜드로 이동하시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.gainItem(4032766, -1);
            cm.warp(100000000, 0);
            break;
        }
    }
}