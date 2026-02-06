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
            var pSpawn = true;
            if (cm.getMap().getMonsterById(8145100) != null) {
                pSpawn = false;
            }
            if (cm.getMap().getMonsterById(8145200) != null) {
                pSpawn = false;
            }
            if (pSpawn == true) {
                cm.spawnMonster(8145100, 294, 4);
                cm.spawnMonster(8145200, -294, 4);
            }
            break;
        }
    }
}