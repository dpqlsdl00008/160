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
            if (cm.isQuestActive(24058) == true && cm.haveItem(4032963, 1) == false) {
                cm.sendNext("(도서관에서 몬스터 도감을 꺼냈다. #b페리온#k의 #b윈스턴#k에게 가져다 주려면 #b신비한 포탈#k을 타는 게 편하겠지? 마을 오른편에 신비한 포탈이 있다.)");
            } else {
                cm.dispose();
            }
            break;
        }
        case 1: {
            cm.dispose();
            cm.gainItem(4032963, 1);
            break;
        }
    }
}