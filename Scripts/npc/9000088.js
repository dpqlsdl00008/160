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
            cm.sendYesNoS("장인의 마을 마이스터 빌로 이동 하시겠습니까? #Cyellow#<마이스터 빌>#k에서는 #Cyellow#약초 채집, 채광, 장비 제작, 장신구 제작, 연금술 등.#k 총 5개의 전문 기술을 배울 수 있습니다.", 4);
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(910001000, "st00");
            cm.saveReturnLocation("ARDENTMILL");
            break;
        }
    }
}