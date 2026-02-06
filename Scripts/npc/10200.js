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
            cm.sendNext("궁수는 민첩성과 힘을 지니고 있는 직업으로 전장의 후열에서 원거리 공격을 담당하게 되며, 지형을 이용한 사냥에도 매우 강합니다.");
            break;
        }
        case 1: {
            cm.sendYesNo("궁수를 체험해 보시겠어요?");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(1020300, 0);
            break;
        }
    }
}