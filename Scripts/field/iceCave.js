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
            cm.teachSkill(20000014, -1, -1);
            cm.teachSkill(20000015, -1, -1);
            cm.teachSkill(20000016, -1, -1);
            cm.teachSkill(20000017, -1, -1);
            cm.teachSkill(20000018, -1, -1);
            break;
        }
    }
}