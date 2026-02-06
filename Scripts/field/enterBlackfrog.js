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
            if (cm.getMap().containsNPC(1013206) == false) {
                cm.spawnNpc(1013206, 183, 31);
            }
            break;
        }
    }
}