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
            qm.sendAcceptDecline("안녕~ 셰릴이 다른 임무로 좀 바빠서 내가 직접 너에게 부탁 할 것이 있어. 부탁이라기 보다는 네가 꼭 해야만 하는 일이지. 지금 바로 시작할거지?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.showFieldEffect(false, "crossHunter/chapter/start2");
            qm.forceStartQuest();
            break;
        }
    }
}