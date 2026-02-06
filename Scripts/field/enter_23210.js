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
            if (cm.getMap().getMonsterById(9001036) == null) {
                cm.spawnMonster(9001036, 582, -14);
                cm.spawnNpc(2153006, 582, -14);
            }
            break;
        }
    }
}