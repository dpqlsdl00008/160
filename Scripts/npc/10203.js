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
            cm.sendNext("도적은 운과 어느 정도의 민첩성과 힘을 가진 직업으로 전장에서 상대를 기습하거나 몸을 숨기는 등 특수한 기술을 사용하게 되네. 높은 기동력과 회피율을 가진 도적은 다양한 기술로 컨트롤을 재미를 느끼게 된다네.");
            break;
        }
        case 1: {
            cm.sendYesNo("어때, 도적을 체험해 보겠나?");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(1020400, 0);
            break;
        }
    }
}