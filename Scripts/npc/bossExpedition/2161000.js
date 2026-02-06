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
            var say = "나를 물리치러 온 용사들인가... 검은 마법사를 적대하는 자들인가... 어느 쪽이건 상관 없겠지. 서로의 목적이 명확하다면 더 이야기 할 필요는 없을 테니... 덤벼라. 어리석은 자들아...";
            if (cm.isLeader() == false) {
                cm.dispose();
                cm.sendNext("\r\n" + say);
                return;
            } else {
                cm.sendAcceptDecline(say);
            }
            break;
        }
        case 1: {
            cm.dispose();
            cm.removeNpc(2161000);
            cm.spawnMonster(8840000, -49, -181);
            break;
        }
    }
}