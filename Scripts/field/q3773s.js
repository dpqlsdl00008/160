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
            cm.forceStartQuest(3755, "1");
            cm.getPlayer().updateInfoQuest(3755, "ent=1");
            cm.spawnMonster(7120100, 141, 168);
            break;
        }
    }
}