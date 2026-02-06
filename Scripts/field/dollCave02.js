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
            cm.dispose();
            if (cm.getMap().getMonsterById(9300346) == null) {
                cm.spawnMonster(9300346, 123, 259);
            }
            break;
        }
    }
}