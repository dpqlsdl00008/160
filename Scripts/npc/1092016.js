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
            if (cm.isQuestActive(2166) == true) {
                cm.forceCustomDataQuest(2166, "5");
                cm.sendNext("\r\n신비로운 힘이 온 몸에 전해져 오는 것 같다.");
            }
            break;
        }
    }
}