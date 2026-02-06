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
            cm.getPlayer().setSubcategory(1);
            cm.showFieldEffect(false, "demonSlayer/back");
            cm.showFieldEffect(false, "demonSlayer/text0");
            if (cm.getMap().containsNPC(2159307) == false) {
                cm.spawnNpc(2159307, 1247, 69);
            }
            cm.teachSkill(30011109, 1, 1);
            cm.teachSkill(30010110, 1, 1);
            cm.teachSkill(30010111, 1, 1);
            cm.teachSkill(30010185, 1, 1);
            cm.forcedInput(0);
            cm.sendDelay(500);
            break;
        }
        case 1: {
            cm.showFieldEffect(false, "demonSlayer/text1");
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.forcedInput(2);
            cm.sendDelay(4000);
            break;
        }
        case 3: {
            cm.showFieldEffect(false, "demonSlayer/text2");
            cm.forcedInput(0);
            cm.sendDelay(500);
            break;
        }
        case 4: {
            cm.showFieldEffect(false, "demonSlayer/text3");
            cm.forcedInput(2);
            cm.sendDelay(4000);
            break;
        }
        case 5: {
            cm.showFieldEffect(false, "demonSlayer/text4");
            cm.forcedInput(0);
            cm.sendDelay(500);
            break;
        }
        case 6: {
            cm.showFieldEffect(false, "demonSlayer/text5");
            cm.forcedInput(2);
            cm.sendDelay(4000);
            break;
        }
        case 7: {
            cm.showFieldEffect(false, "demonSlayer/text6");
            cm.forcedInput(0);
            cm.sendDelay(500);
            break;
        }
        case 8: {
            cm.showFieldEffect(false, "demonSlayer/text7");
            cm.forcedInput(2);
            cm.sendDelay(4000);
            break;
        }
        case 9: {
            cm.dispose();
            cm.lockInGameUI(false);
            break;
        }
    }
}