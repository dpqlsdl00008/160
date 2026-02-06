var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.dispose();
            if (cm.isQuestActive(3900) == false) {
                return;
            }
            cm.getPlayer().dropMessage(5, "오아시스의 물을 마셨습니다.");
            cm.forceCustomDataQuest(3900, "5");
            break;
        }
    }
}