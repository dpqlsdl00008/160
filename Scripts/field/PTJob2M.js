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
            if (cm.getJob() > 2400) {
                cm.dispose();
                return;
            }
            cm.forceStartQuest(25102, "1");
            if (cm.getMap().getMonsterById(9001045) == null) {
                cm.spawnMonster(9001045, 171, 182);
            }
            break;
        }
    }
}