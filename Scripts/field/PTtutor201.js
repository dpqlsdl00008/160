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
            cm.sendNextS("이 포탈을 타면 바로 에레브다... 기사들이 많을 테니 조심하자. 옛날 실력 한 번 발휘해보자고. 훗.", 17);
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(915000300, 0);
            break;
        }
    }
}