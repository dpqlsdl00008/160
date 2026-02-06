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
            cm.showInfoOnScreen("메이플력 XXXX년 X월 X일");
            cm.showInfoOnScreen("림버트씨의 잡화 상점");
            cm.sendDelay(1000);
            break;
        }
        case 1: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.forcedInput(1);
            cm.sendDelay(1000);
            break;
        }
        case 3: {
            cm.showDirectionEffect("Effect/Direction7.img/effect/tuto/step0/5", 2000, 0, -110, 0, 0);
            cm.sendDelay(1000);
            break;
        }
        case 4: {
            cm.sendDelay(1200);
            break;
        }
        case 5: {
            cm.forcedInput(2);
            cm.sendDelay(500);
            break;
        }
        case 6: {
            cm.showDirectionEffect("Effect/Direction7.img/effect/tuto/step0/4", 2000, 0, -110, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(3000);
            break;
        }
        case 7: {
            cm.showInfoOnScreen("뒷 마당에 누군가 있는 것 같다. 뒷 마당으로 나가보자.");
            cm.showDirectionEffect("Effect/Direction7.img/effect/tuto/step0/8", 2000, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 8: {
            cm.dispose();
            cm.lockInGameUI(false);
            break;
        }
    }
}