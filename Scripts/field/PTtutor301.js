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
            cm.sendNextS("엇차... 역시 내 실력이면 이 정도쯤이야 간단하지. 이제 슬슬 준비한대로 옷을 갈아 입어 볼까나?", 17);
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(915000301, 1);
            break;
        }
    }
}