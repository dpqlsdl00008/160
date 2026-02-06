var status = -1;

function start() {
    status = -1;
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
            if (cm.getJob() > 2410) {
                cm.dispose();
                return;
            }
            cm.lockInGameUI(true);
            cm.sendNextS("\r\n켁? 침입자? 오래 자리를 비웠더니 결국 누가 들어 온 건가? 자물쇠가 고장나지 않은 대신에 이번엔 딴 게 또 귀찮게 만드네...", 17);
            break;
        }
        case 1:{
            cm.sendNextPrevS("\r\n다 없애지 않으면 창고로 못 들어가겠지? 바로 없애 버려야지. 청소는 깨끗이!", 17);
            break;
        }
        case 2:{
            cm.dispose();
            cm.lockInGameUI(false);
            cm.forceStartQuest(25113, "1");
            break;
        }
    }
}