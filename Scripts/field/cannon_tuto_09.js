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
            cm.lockInGameUI(true);
            cm.sendNextS("자, 이제 출발할게!", 32, 0, 1096005);
            break;
        }
        case 1: {
            cm.getClient().sendPacket(Packages.tools.packet.CWvsContext.sendString(1, "fire", "0"));
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/flying/0", 7000, new java.awt.Point(0, 0), false, 0, false, 0, 0, 0);
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/flying1/0", 7000, new java.awt.Point(0, 0), false, 0, false, 0, 0, 0);
            cm.sendDelay(800);
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(912060300, 0);
            break;
        }
    }
}