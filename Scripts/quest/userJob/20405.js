var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendAcceptDecline("#b(동굴 중앙에 구슬이 놓여 있다. 어두워서 잘 보이지 않는다. 가까이에서 보자.)");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.sendNext("#b(구슬을 건드리자 굵직한 목소리가 들려온다.)\r\n\r\n듀나미스다. 저주의 주체는 발견하지 못했지만 저주 도구는 발견. 회수에서 에레브로 이송한다. 조력으로 파견된 기사단원이 있을지 몰라 목소리를 남긴다. 에레브로 귀환해도 좋다.");
            break;
        }
    }
}