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
            cm.sendYesNoS("다른 캐릭터들과 아이템을 거래 할 수 있는 #Cyellow#<자유 시장>#k으로 이동 하시겠습니까?", 4);
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(910000000, "out00");
            cm.saveReturnLocation("FREE_MARKET");
            break;
        }
    }
}