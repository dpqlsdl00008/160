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
            if (cm.getMapId() == 104000000) {
                cm.movieClipInGameUI(false);
                cm.showFieldEffect(false, "maplemap/enter/104000000");
            }
            break;
        }
    }
}