var status = -1;

function start() {
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
            if (cm.getMapId() == 927000100) {
                if (cm.getMap().getMonsterById(9001040) == null) {
                    cm.dispose();
                    cm.spawnMonster(9001040, 733, 69);
                    return;
                }
            }
            if (cm.getMap().containsNPC(2159335) == false) {
                cm.spawnNpc(2159335, 474, 69);
            }
            cm.lockInGameUI(true);
            cm.showNpcSpecialActionByTemplateId(2159335, "summon");
            cm.sendDelay(2000);
            break;
        }
        case 1: {
            cm.showDirectionEffect("Skill/3112.img/skill/31121005/effect", 0, 0, 0, 0, 0);
            cm.showDirectionEffect("Skill/3112.img/skill/31121005/effect0", 0, 0, 35, 0, 0);
            cm.sendDelay(500);
            break;
        }
        case 2: {
            cm.forcedAction(374, 0);
            cm.forcedInput(0);
            cm.sendDelay(1500);
            break;
        }
        case 3: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/job/balloonMsg2/0", 0, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 4: {
            cm.sendNextS("\r\n#b포스를 개방 한 느낌은 알겠는데... 오래 유지 할 수가 없군.#k", 3);
            break;
        }
        case 5: {
            cm.dispose();
            cm.resetMap(cm.getMapId());
            cm.spawnMonster(9001041, 474, 69);
            cm.lockInGameUI(false);
            break;
        }
    }
}