var status = -1;

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
            cm.getPlayer().dropMessage(5, "아기 돼지를 구출했다.");
            cm.forceCompleteQuest(22015);
            cm.gainItem(4032449, 1);
            break;
        }
    }
}