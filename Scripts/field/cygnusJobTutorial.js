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
            cm.movieClipInGameUI(true);
            cm.reservedEffect("Effect/Direction.img/cygnusJobTutorial/Scene" + (cm.getPlayer().getMapId() - 913040100));
            break;
        }
    }
}