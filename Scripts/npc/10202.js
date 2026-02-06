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
            cm.sendNext("전사는 엄청난 공격력과 강한 체력을 지닌 직업으로, 전장의 최전선에서 그 진가를 발휘하지. 기본 공격이 매우 강한 직업으로 고급 기술들을 배우면서 더욱 강한 힘을 발휘할 수 있지.");
            break;
        }
        case 1: {
            cm.sendYesNo("어때, 전사를 체험해 보겠나?");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(1020100, 0);
            break;
        }
    }
}