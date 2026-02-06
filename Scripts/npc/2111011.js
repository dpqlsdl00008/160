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
            if (cm.isQuestActive(3311) == false) {
                cm.dispose();
                return;
            }
            if (cm.getDistance() > 5000) {
                cm.dispose();
                cm.sendNext("\r\n너무 거리가 멀어 조사 할 수 없다.");
                return;
            }
            cm.sendYesNo("거미줄 틈으로 보이는 벽에 뭔가 글자들이 보이는 것 같다... 벽을 살피시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.forceStartQuest(3311, "5");
            cm.sendNext("\r\n지저분한 낙서들 틈으로 유난히 선명하게 보이는 글자가 있다.  #b그것은 펜던트의 형태로 완성되었다...#k  무슨 말일까?")
            return;
        }
    }
}