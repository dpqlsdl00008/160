var status = -1;

function start() {
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
            // 아, 오셨습니까. 마스터. 이 지역을 떠나 다른 지역으로 가고 싶으신 거라면 원하는 지역을 말씀해 주십시오. 바로 날아가도록 하겠습니다.
            // 빅토리아 아일랜드 승강장, 에레브 하늘 나루, 오르비스 정거장, 루디브리엄 정거장, 아리안트 정거장, 리프레 정거장, 에델슈타인 승강장
            cm.sendYesNo("#Cgreen#에델슈타인의 승강장#k으로 바로 이동하시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(310000010, "out00");
            break;
        }
    }
}