var status = -1;

function start() {
    action(1, 0, 0);
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
            cm.floatMessage("위험해! 어서 피해!!!", 5120054);
            cm.userEmotion(5, 3000, false);
            cm.showFieldEffect(false, "crossHunter/bomb");
            cm.forceStartQuest(1641, "bomb");
            break;
        }
    }
}