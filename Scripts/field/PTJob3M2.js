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
            if (cm.getJob() != 2410) {
                cm.dispose();
                return;
            }
            cm.lockInGameUI(true);
            cm.sendNextS("\r\n옛날 생각 나는 군... 저 액자에 있는 그림, 어렸을 적에는 되게 싫어했는데... 사실 지금 봐도 좀 싫은 걸.", 17);
            break;
        }
        case 1:{
            cm.sendNextPrevS("\r\n아니, 이럴 때가 아니지. 스킬 북... 스킬 북을 어디에 뒀담. 아, 저 위의 상자인가?", 17);
            break;
        }
        case 2:{
            cm.sendNextPrevS("\r\n이 상자 안에 스킬 북이 있었던 것 같은데... 윽. 정리 좀 해 놓을 걸. 귀찮아서 대충 처박아 놨더니 영... 그 때는 스킬 연구가 지긋 지긋해서... 아, 찾았다! 전직이다!", 17);
            break;
        }
        case 3:{
            cm.dispose();
            cm.forceCompleteQuest(25111);
            cm.changeJob(2411);
            cm.lockInGameUI(false);
            break;
        }
    }
}