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
            cm.sendNext("찾았다! 네가 바로 메르세데스로구나!");
            break;
        }
        case 1: {
            cm.sendNextPrevS("...음? 넌 누구지? 왠 꼬마가...", 2);
            break;
        }
        case 2: {
            cm.sendNextPrev("꼬, 꼬마?! 닥쳐! 헬레나가 오기 전에 널 없애 버리겠다!");
            break;
        }
        case 3: {
            cm.dispose();
            cm.resetMap(cm.getMapId());
            cm.spawnMonster(9300285, 5479, 454);
            break;
        }
    }
}