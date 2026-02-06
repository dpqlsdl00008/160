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
            if (cm.isQuestActive(1451) == false) {
                cm.dispose();
                return;
            }
            var say = "당신이 이 시험에 응한다면 당신을 마뇽과 그리프에게 보내드리겠어요. 물론 마뇽의 숲이나 그리프의 숲을 단신으로 돌파 할 역량이 있다면 스스로 찾아가셔도 돼요. 어떻게 하시겠어요?#d";
            say += "\r\n#L0#마뇽의 숲으로 보내주세요.";
            say += "\r\n#L1#그리프의 숲으로 보내주세요.";
            say += "\r\n#L2#아무 것도 아니에요. 제 스스로 찾아갈게요.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            s1 = selection;
            if (selection == 2) {
                cm.dispose();
                return;
            }
            cm.askAcceptDecline("#d" + (selection == 0 ? "마뇽" : "그리프") + "의 숲#k으로 가시겠어요? 보내드릴게요.");
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getPlayerCount(924000200 + s1) != 0) {
                cm.sendNext("\r\n현재 접속 한 채널에서는 다른 유저가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                return;
            }
            cm.resetMap(924000200 + s1);
            cm.warp(924000200 + s1, 0);
            break;
        }
    }
}