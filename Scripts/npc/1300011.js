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
            cm.sendYesNo("#b덩굴 가시 제거제#k를 사용 하시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(106020502, 0);
            break;
        }
    }
}