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
            cm.sendYesNo("아직 델리를 찾지 못한거야? 지금 그냥 나가고 싶어?");
            break;
        }
        default: {
            cm.dispose();
            cm.warp(120000104, "ao03");
            break;
        }
    }
}