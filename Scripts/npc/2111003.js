var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.sendNext("\r\n준비가 되시면 저를 다시 찾아와 주세요.");
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            if (cm.isQuestActive(3335) == false) {
                cm.dispose();
                cm.sendNext("\r\n인간이 되고 싶습니다. 따뜻한 심장을 가진 인간이... 인간이 된다면 그녀의 손을 잡아줄 수도 있겠지요. 하지만 지금은 그럴 수 없지요...");
                return;
            }
            cm.sendYesNo("와주셨군요... 설원 장미를 피울 준비는 되셨나요? 5월의 이슬이 있어야만 장미를 피울 수 있다는 건 알고 계시죠?");
            break;
        }
        case 1: {
            cm.sendNext("\r\n그럼 설원 장미를 피울 부화기가 마련된 곳으로 당신을 안내하겠습니다...");
            break;
        }
        case 2: {
            cm.dispose();
            if (cm.getPlayerCount(926120300) != 0) {
                cm.sendNext("\r\n이미 다른 누군가가 이 안에서 설원 장미를 피우고 있는 것 같습니다. 다음에 다시 찾아와 주세요.");
                return;
            }
            cm.warp(926120300, 0);
            break;
        }
    }
}