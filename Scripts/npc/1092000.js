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
            if (cm.isQuestActive(2180) == false) {
                cm.sendNext("\r\n나는 노틸러스호의 식량을 담당하는 탕윤이라고 하네.");
                cm.dispose();
                return;
            }
            if (cm.haveItem(4031850, 1) == true) {
                cm.sendNext("\r\n이미 신선한 우유를 구해왔구만?");
                cm.dispose();
                return;
            }
            cm.sendNext("\r\n자~ 그럼 나의 소중한 젖소들이 살고 있는 외양간으로 보내주지. 우유를 다 먹어 치워 버리는 아기 젖소들을 조심하게. 노력이 헛 수고가 되어 버릴 수 있으니.");
            break;
        }
        case 1: {
            cm.sendNext("\r\n아기 젖소와 어미 젖소는 한 눈에 구분 가지 않을거야. 태어난 지 얼마 안 된 아기들이지만 엄청난 먹성으로 벌써 어미 소만 하거든. 모습도 붕어 빵처럼 똑같이 생겼으니... 가끔 나도 헷갈린다네. 그럼 잘 해보게.");
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getPlayerCount(912000100) != 0) {
                cm.sendNext("\r\n이미 이 안에 다른 누군가가 들어가있는 것 같군. 나중에 다시 시도하게.");
                return;
            }
            cm.warp(912000100, "ntq2");
            break;
        }
    }
}