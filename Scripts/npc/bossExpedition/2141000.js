var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
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
            var say = "여신의 거울만 있으면... 다시 검은 마법사를 불러낼 수 있어! ...이, 이상해... 왜 검은 마법사를 불러내지 않는 거지? 이 기운은 뭐지? 검은 마법사와는 전혀 다른... 크아아악!";
            if (cm.isLeader() == false) {
                cm.dispose();
                cm.sendNext("\r\n" + say);
                return;
            } else {
                cm.sendAcceptDecline(say + "\r\n\r\n#b(키르스턴의 어깨에 손을 댄다.)");
            }
            break;
        }
        case 1: {
            cm.dispose();
            cm.showNpcSpecialActionByTemplateId(2141000, "magic");
            cm.handlePinkbeanSummon(2000);
            break;
        }
    }
}