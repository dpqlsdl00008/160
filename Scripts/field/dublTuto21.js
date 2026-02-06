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
            cm.resetMap(cm.getMapId());
            cm.spawnMonster(9300522, -500, 152);
            cm.spawnMonster(9300522, -300, 152);
            cm.spawnMonster(9300521, -100, 152);
            cm.spawnMonster(9300522, 100, 152);
            cm.spawnMonster(9300522, 300, 152);
            break;
        }
    }
}