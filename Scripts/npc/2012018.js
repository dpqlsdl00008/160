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
            cm.sendYesNo("출발 할 준비는 모두 된 거야? 먼 길이 될 테니 착실히 준비해 가는 게 좋을 거야. 지금 보내 줄게.");
            break;
        }
        case 1: {
            cm.sendNext("\r\n좋았어. 지금 바로 보내 줄게. 쉽지 않은 여정이 될 수 있으니 마음 단단히 먹으라고.");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(200100001, 0);
            cm.getPlayer().dropMessage(-1, "점프 키를 누르면 하늘을 날아 크리세에 도착 할 수 있습니다.");
            break;
        }
    }
}