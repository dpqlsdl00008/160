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
            cm.lockInGameUI(true);
            while (cm.getLevel() < 10) {
                cm.getPlayer().levelUp();
            }
            cm.forceStartQuest(24008, "1");
            cm.forceStartQuest(24009, "1");
            cm.teachSkill(20021166, 0, -1);
            cm.teachSkill(20021181, 0, -1);
            cm.getPlayer().changeJob(2300);
            cm.getPlayer().resetStats(4, 4, 4, 4);
            cm.reservedEffect("Effect/Direction5.img/mersedesTutorial/Scene1");
            break;
        }
    }
}