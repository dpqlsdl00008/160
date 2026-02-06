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
            cm.sendDelay(1000);
            break;
        }
        case 1: {
            cm.showFieldEffect(false, "demonSlayer/text12");
            cm.sendDelay(3000);
            break;
        }
        case 2: {
            cm.forcedInput(1);
            cm.sendDelay(10);
            break;
        }
        case 3: {
            cm.forcedInput(0);
            cm.sendNextS("......", 17);
            break;
        }
        case 4: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/14", 0, 100, 120, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 5: {
            cm.sendNextS("(...뭔가... 무슨 소리가 들리는데...)", 17);
            break;
        }
        case 6: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/15", 0, 100, 120, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 7: {
            cm.sendNextS("(여긴 어디지... 난 살아 있는 건가?)", 17);
            break;
        }
        case 8: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/16", 0, 100, 120, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 9: {
            cm.sendNextS("(으... 힘이 빠져나가고 있어... 뭔가 내 힘을 빨아들이는 것 같다...)", 17);
            break;
        }
        case 10: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/17", 0, 100, 120, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 11: {
            cm.sendNextS("(저 녀석들이 내 힘을...? 여기서 나가자!)", 17);
            break;
        }
        case 12: {
            cm.playSound(false, "demonSlayer/punch");
            cm.playSound(false, "demonSlayer/crackEgg");
            cm.playSound(false, "demonSlayer/punch");
            cm.playSound(false, "demonSlayer/crackEgg");
            cm.showInfoOnScreen("Ctrl 키를 연타하여 벽을 깨고 나오십시오.");
            cm.effectPlay("Effect/Direction6.img/effect/tuto/breakEgg/0", 5000, new java.awt.Point(0, 0), true, 7, true, 0, 1, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/guide1/0", 5000, new java.awt.Point(0, -130), true, 7, true, 0, 1, 0);
            cm.patternInputRequest("17#17#17#", 2, 2, 4000);
            break;
        }
        case 13: {
            cm.playSound(false, "demonSlayer/punch");
            cm.playSound(false, "demonSlayer/crackEgg");
            cm.showInfoOnScreen("Ctrl 키를 연타하여 벽을 깨고 나오십시오.");
            cm.effectPlay("Effect/Direction6.img/effect/tuto/breakEgg/1", 5000, new java.awt.Point(0, 0), true, 7, true, 0, 1, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/guide1/0", 5000, new java.awt.Point(0, -130), true, 7, true, 0, 1, 0);
            cm.patternInputRequest("17#17#17#", 2, 2, 4000);
            break;
        }
        case 14: {
            cm.playSound(false, "demonSlayer/punch");
            cm.playSound(false, "demonSlayer/crackEgg");
            cm.showInfoOnScreen("Ctrl 키를 연타하여 벽을 깨고 나오십시오.");
            cm.effectPlay("Effect/Direction6.img/effect/tuto/breakEgg/2", 5000, new java.awt.Point(0, 0), true, 7, true, 0, 1, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/guide1/0", 5000, new java.awt.Point(0, -130), true, 7, true, 0, 1, 0);
            cm.patternInputRequest("17#17#17#", 2, 2, 4000);
            break;
        }
        case 15: {
            cm.showFieldEffect(false, "demonSlayer/whiteOut");
            cm.sendDelay(3000);
            break;
        }
        case 16: {
            cm.dispose();
            cm.warp(931050020, 0);
            break;
        }
    }
}