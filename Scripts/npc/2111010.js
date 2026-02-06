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
            cm.dispose();
            if (cm.isQuestActive(3309) == false) {
                return;
            }
            if (cm.haveItem(4031708, 1) == true) {
                return;
            }
            if (cm.getDistance() > 5000) {
                cm.getPlayer().dropMessage(5, "너무 거리가 멀어 조사 할 수 없다.");
                return;
            }
            cm.gainItem(4031708, 1);
            cm.sendNext("어둠 속에서 책장이 만져진다... 눈에 힘을 주고 잘 살펴보자, 이상한 서류 뭉치가 보인다... 이게 바로 베딘이 말한 그 서류인 것 같다. 서류를 챙겼으니 베딘에게 돌아가자.");
            break;
        }
    }
}