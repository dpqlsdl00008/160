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
            cm.lockInGameUI(true);
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesQuest/merBalloon/0", 2000, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 1: {
            cm.showDirectionEffect("Effect/Direction5.img/effect/mercedesQuest/merBalloon/1", 2000, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 2: {
            cm.sendNextS("잠깐... 뭔가 이상한데? 나 레벨이 몇인 거지?", 17);
            break;
        }
        case 3: {
            cm.sendNextPrevS("레벨... 10?!", 17);
            break;
        }
        case 4: {
            cm.dispose();
            cm.forceStartQuest(24093, "1");
            cm.lockInGameUI(false);
            break;
        }
    }
}