var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
           cm.sendNext("\r\n영웅의 전당으로 이동 하길 원하실 때 다시 말씀해 주세요.");
           cm.dispose();
           return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendYesNo("안녕하세요. 길드 지원 업무를 맡은 레아입니다. 업무 편의를 위해, 영웅의 전당까지 이동을 돕고 있습니다. 길드 관련 업무 처리를 위해 영웅의 전당으로 이동 하시겠어요?");
            break;
        }
        case 1: {
            cm.sendNext("\r\n네, 그럼 바로 이동 시켜 드리겠습니다.");
            break;
        }
        case 2: {
            cm.dispose();
            cm.saveReturnLocation("MULUNG_TC");
            cm.warp(200000301, "out00");
            break;
        }
    }
}