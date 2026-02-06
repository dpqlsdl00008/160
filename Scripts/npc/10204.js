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
            cm.sendNext("해적은 뛰어난 민첩성이나 힘을 바탕으로 적을 향해 백날 백중의 총을 발사하거나 적을 한 순간에 제압하는 체술을 사용하지. 건슬링거는 속성별 총알을 이용해 효율적으로 공격하거나 배에 탑승해 더 강한 공격을 할 수 있으며, 인파이터는 변신을 통해 강한 체술을 발휘하지.");
            break;
        }
        case 1: {
            cm.sendYesNo("어때, 해적을 체험해 보겠나?");
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(1020500, 0);
            break;
        }
    }
}