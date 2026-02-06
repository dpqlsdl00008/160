var status = -1;

function start() {
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
            cm.sendYesNo("석상에 손을 대자 어디론가 빨려드는 듯한 느낌이 듭니다. 이대로 #b슬리피우드#k로 돌아가시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(105040300, 0);
            break;
        }
    }
}