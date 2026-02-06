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
            if (cm.getJob() != 2400) {
                cm.dispose();
                return;
            }
            cm.lockInGameUI(true);
            cm.sendNextS("\r\n어디 보자... 이 책은 아니고, 저 책도 아니고... 앗 여깄다! 이거로군! 오, 바로 전직인가? 좋은 걸?", 17);
            break;
        }
        case 1: {
            cm.sendNextPrevS("\r\n호오~ 저지먼트 스킬도 여기 적혀 있었군. 이건 초보자 스킬 창으로 들어가는 스킬이었던가?", 17);
            break;
        }
        case 2: {
            cm.dispose();
            cm.changeJob(2410);
            cm.lockInGameUI(false);
            break;
        }
    }
}