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
            cm.dispose();
            cm.showInfoOnScreen("메이플력 XXXX년 X월 X일");
            cm.showInfoOnScreen("림버트씨의 잡화 상점");
            break;
        }
    }
}