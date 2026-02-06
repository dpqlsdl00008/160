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
            cm.sendNext("마법사는 화려한 효과의 속성 마법과 파티 사냥의 유용한 다양한 보조 마법을 가지고 있다네. 게다가 2차 전직 이후에 배우게 되는 속성 마법은 반대 속성의 적에게 치명적인 데미지를 준다네.");
            break;
        }
        case 1: {
            cm.sendYesNo("어때, 마법사를 체험해 보겠나?");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(1020200, 0);
            break;
        }
    }
}