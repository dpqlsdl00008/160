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
            cm.dispose();
            cm.lockInGameUI(false);
            if (cm.getMap().containsNPC(2159309) == false) {
                cm.spawnNpc(2159309, 550, 50);
            }
            cm.teachSkill(30010166, 1, 1);
            cm.teachSkill(30011167, 1, 1);
            cm.teachSkill(30011168, 1, 1);
            cm.teachSkill(30011169, 1, 1);
            cm.teachSkill(30011170, 1, 1);
            break;
        }
    }
}