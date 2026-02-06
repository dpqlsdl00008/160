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
            if (cm.getMap().getMonsterById(9100044) == null) {
                for (i = 0; i < 6; i++) {
                    cm.spawnMonster(9100044, 625, 60);
                }
            }
            break;
        }
    }
}