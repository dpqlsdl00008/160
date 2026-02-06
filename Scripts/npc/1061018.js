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
            if (cm.getMonsterCount(cm.getMapId()) == 0) {
                cm.dispose();
                cm.warp(cm.getMapId() + 1, 0);
                return;
            }
            cm.sendYesNo("도전을 포기하고 밖으로 나가겠어?");
            break;
        }
        case 1:{
            cm.dispose();
            cm.warp(105100100, 0);
            break;
        }
    }
}