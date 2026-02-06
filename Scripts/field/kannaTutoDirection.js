var status = -1;

function start() {
    cMap = cm.getMapId();
    switch (cMap) {
        case 507100100: {
            status = -1;
            break;
        }
        case 507100102: {
            status = 18;
            break;
        }
        case 507100103: {
            status = 33;
            break;
        }
        case 507100104: {
            status = 42;
            break;
        }
    }
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.lockInGameUI(true);
            cm.forcedInput(2);
            cm.sendDelay(2000);
            break;
        }
        case 1: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.sendNextS("I can already feel the tinging of the dark energy upon my skin.", 9, 9131003, 9131003);
            break;
        }
        case 3: {
            cm.sendNextPrevS("This Place gives me goosebumps.", 3);
            break;
        }
        case 4: {
            cm.sendNextPrevS("There's a lot of noise.", 3);
            break;
        }
        case 5: {
            cm.sendNextPrevS("We May not be the only intruders.", 9, 9131003, 9131003);
            break;
        }
        case 6: {
            cm.sendNextPrevS("Whatever's going on, I don't like it. Do you think someone else could have received the same orders?", 3);
            break;
        }
        case 7: {
            cm.sendNextPrevS("Leave the worrying to me Kanna. You hurry and stop the ritual.", 9, 9131006, 9131006);
            break;
        }
        case 8: {
            cm.sendNextPrevS("The others have gone to the Northem Wing or the southwesten area. Kenshin's right. You need to focus on stopping the ritual.", 9, 9131003, 9131003);
            break;
        }
        case 9: {
            cm.sendNextPrevS("We think the ritual is being performed in the Western Wing. If you can somehow disrupt it, the power flowing into the Tmple will be cut off. However, be aware that to fully stop the nitual, you'll have to destroy the altar in the basement.", 9, 9131003, 9131003);
            break;
        }
        case 10: {
            cm.sendNextPrevS("Got it.", 3);
            break;
        }
        case 11: {
            cm.sendNextPrevS("You must hurry, Kanna!", 9, 9131003, 9131003);
            break;
        }
        case 12: {
            cm.forcedInput(2);
            cm.sendDelay(1500);
            break;
        }
        case 13: {
            cm.forcedInput(7);
            cm.sendDelay(500);
            break;
        }
        case 14: {
            cm.forcedInput(2);
            cm.sendDelay(300);
            break;
        }
        case 15: {
            cm.forcedInput(7);
            cm.sendDelay(500);
            break;
        }
        case 16: {
            cm.dispose();
            cm.warp(507100101, 0);
            break;
        }
        case 17: {
            cm.dispose();
            break;
        }
        case 18: {
            cm.dispose();
            break;
        }
        case 19: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 20: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/1", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 21: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/2", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 22: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/3", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 23: {
            cm.forcedInput(2);
            cm.sendDelay(700);
            break;
        }
        case 24: {
            cm.forcedInput(8);
            cm.sendDelay(300);
            break;
        }
        case 25: {
            cm.forcedInput(2);
            cm.sendDelay(700);
            break;
        }
        case 26: {
            cm.forcedInput(8);
            cm.sendDelay(300);
            break;
        }
        case 27: {
            cm.forcedInput(2);
            cm.sendDelay(2950);
            break;
        }
        case 28: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 29: {
            cm.showFieldEffect(false, "JPKanna/magicCircle2");
            cm.sendDelay(7000);
            break;
        }
        case 30: {
            cm.sendNextS("It seems like the others have succeeded as well. The barrier is collapsing.", 3);
            break;
        }
        case 31: {
            cm.sendNextPrevS("I need to hurry to the basement and take care of the altar.", 3);
            break;
        }
        case 32: {
            cm.dispose();
            cm.warp(507100103, 0);
            break;
        }
        case 33: {
            cm.dispose();
            break;
        }
        case 34: {
            cm.forcedInput(2);
            cm.sendDelay(1500);
            break;
        }
        case 35: {
            cm.forcedInput(7);
            cm.sendDelay(300);
            break;
        }
        case 36: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 37: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/4", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 38: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/5", 0, 0, -110, 0, 0);
            cm.sendDelay(4000);
            break;
        }
        case 39: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/6", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 40: {
            cm.forcedInput(2);
            cm.sendDelay(650);
            break;
        }
        case 41: {
            cm.dispose();
            cm.warp(507100104, 0);
            break;
        }
        case 42: {
            cm.dispose();
            break;
        }
        case 43: {
            cm.forcedInput(0);
            cm.sendDelay(0);
            break;
        }
        case 44: {
            cm.showNpcSpecialActionByTemplateId(9131004, "back");
            cm.sendDelay(0);
            break;
        }
        case 45: {
            cm.forcedInput(2);
            cm.sendDelay(3000);
            break;
        }
        case 46: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 47: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/7", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 48: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/8", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 49: {
            cm.effectPlay("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/9", 2500, new java.awt.Point(0, -110), false, 0, true, 9131004, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 50: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kannaTuto/balloonMsg/10", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 51: {
            cm.dispose();
            cm.lockInGameUI(false);
            while (cm.getLevel() < 10) {
                cm.getPlayer().levelUp();
            }
            cm.getPlayer().changeJob(6200);
            cm.getPlayer().resetStats(4, 4, 4, 4);
            cm.warp(100000000, 0);
            break;
        }
    }
}