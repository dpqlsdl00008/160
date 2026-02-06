var status = -1;

function start() {
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
            if (cm.getMap().containsNPC(2159333) == false) {
                cm.spawnNpc(2159333, 580, -14);
            }
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 1: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.forcedInput(0);
            cm.sendDelay(500);
            break;
        }
        case 3: {
            cm.sendNextS("이제 물약을 마셔 볼께요.", 1, 0, 2159333);
            break;
        }
        case 4: {
            cm.effectPlay("Effect/Direction6.img/effect/story/balloonMsg0/0", 2000, new java.awt.Point(0, -110), false, 0, true, 2159333, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 5: {
            cm.sendNextS("성... 성공이다... 팔... 다리... 다 온전 한 것 맞죠?", 1, 0, 2159333);
            break;
        }
        case 6: {
            cm.sendNextPrevS("#b음... 근데... 마스테마... 제 모습으로 변한 것 같습니다만?#k", 3);
            break;
        }
        case 7: {
            cm.sendNextPrevS("에에엣??? 제가 #h #님의 모습으로 변했다고요?? 이게 무슨...", 1, 0, 2159333);
            break;
        }
        case 8: {
            cm.effectPlay("Effect/Direction6.img/effect/story/balloonMsg2/0", 2000, new java.awt.Point(0, -110), false, 0, true, 2159333, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 9: {
            cm.sendNextPrevS("#b다행히 효과가 오래가진 않은 모양입니다. 원하는 모습으로 변한다고 하는 소문은 잘못 된 것 이었군요.#k", 3);
            break;
        }
        case 10: {
            cm.sendNextPrevS("(알아 차리지 못하신 것을 다행이라고 해야 할 지...)\r\n네, 이제 돌아가죠. 어서요.", 1, 0, 2159333);
            break;
        }
        case 11: {
            cm.lockInGameUI(false);
            cm.forceCompleteQuest(23264);
            cm.warp(310010000, 0);
            break;
        }
    }
}