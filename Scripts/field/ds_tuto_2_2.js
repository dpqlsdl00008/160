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
            if (cm.getMap().containsNPC(2159309) == false) {
                cm.spawnNpc(2159309, 550, 50);
            }
            cm.forcedInput(0);
            cm.sendDelay(30);
            break;
        }
        case 1: {
            cm.sendNextS("언제 봐도 훌륭한 실력이야. 사실 좋은 기회 아닌가? 군단장 사이에 누가 더 강한지 겨뤄보고 싶었는데 말이야. 자, 너의 포스와 나의 마법. 과연 어느 쪽이 더 강할까?", 5, 2159309);
            break;
        }
        case 2: {
            //cm.patternInputRequest("17#17#17#", 4, 2, 3000);
            //cm.fadeInOut(600, 1500, 600, 150);
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/9", 0, 0, -110, 0, 0);
            cm.forcedAction(376, 0);
            cm.showDirectionEffect("Skill/3112.img/skill/31121000/effect", 0, 0, 0, 0, 0);
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/guide1/0", 5010, 80, -300, 0, 0);
            cm.showNpcSpecialActionByTemplateId(2159309, "alert");
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/arkyrimAttack", 0, 0, -7, 2159309, 0);
            cm.forcedInput(0);
            cm.sendDelay(2000);
            break;
        }
        case 3: {
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 2000, new java.awt.Point(0, -170), false, 0, true, 2159309, 0, 0);
            cm.showNpcSpecialActionByTemplateId(2159309, "teleportation");
            cm.sendDelay(2000);
            break;
        }
        case 4: {
            cm.sendNextS("과연 제법이군... 재미있어. 하하하하!", 5, 2159309);
            break;
        }
        case 5: {
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/10", 2000, new java.awt.Point(0, -170), false, 0, true, 2159309, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 6: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/11", 0, 0, -110, 0, 0);
            cm.sendDelay(1500);
            break;
        }
        case 7: {
            cm.forcedAction(370, 0);
            cm.showDirectionEffect("Skill/3112.img/skill/31121005/effect", 0, 0, 0, 0, 0);
            cm.showDirectionEffect("Skill/3112.img/skill/31121005/effect0", 0, 0, 0, 0, 0);
            cm.sendDelay(1980);
            break;
        }
/*
        case 8: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/gateOpen/0", 2000, 918, -195, 1, 0);
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/gateLight/0", 2000, 926, -390, 1, 0);
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/gateStair/0", 2000, 879, 30, 1, 0);
            cm.sendDelay(1950);
            break;
        }
*/
        case 8: {
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg0/0", 2000, new java.awt.Point(0, -170), false, 0, true, 2159309, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 9: {
            cm.sendNextS("...오호. 검은 마법사님께서 친히 자네를 맞아주시는군. 아쉽지만 이쯤 해둘까? 그럼 나는 영웅이라 불리는 자들의 얼굴이나 봐둬야겠군.", 5, 2159309);
            break;
        }
        case 10: {
            cm.sendNextPrevS("다시는 못 보겠군. #h #. 영광인 줄 알게나. 그 분의 손에 소멸 될 수 있다는 걸 말이야! 하하하하!", 5, 2159309);
            break;
        }
        case 11: {
            cm.showNpcSpecialActionByTemplateId(2159309, "teleportation");
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg2/2", 0, 0, -110, 0, 0);
            cm.forcedInput(2);
            cm.sendDelay(2000);
            break;
        }
        case 12: {
            cm.dispose();
            cm.warp(931050300, 0);
            break;
        }
    }
}