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
            if (cm.getPlayer().getStat().getHp() > 25) {
                cm.getPlayer().addHP(-25);
            }
            cm.sendNextS("끼긱~~ 끼긱~~ ??", 32, 0, 1096003);
            break;
        }
    }
}