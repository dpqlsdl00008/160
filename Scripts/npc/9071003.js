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
            if (cm.getMapId() != 951000000) {
                cm.sendYesNo("사랑합니다 고객님. 항상 새로운 즐거움이 가득한 슈피겔만의 몬스터 파크로 이동하시겠습니까?");
            } else {
                cm.sendYesNo("안녕하세요. 고객만족을 위해 항상 최선을 다하고 있는 몬스터파크 셔틀입니다. 자유 시장으로 돌아가시겠습니까?");
            }
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getMapId() != 951000000) {
                cm.warp(951000000, 0);
            } else {
                cm.warp(910000000, 0);
            }
            break;
        }
    }
}