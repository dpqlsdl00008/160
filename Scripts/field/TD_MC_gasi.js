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
            cm.reservedEffect("Effect/Direction2.img/gasi/gasi1");
            cm.reservedEffect("Effect/Direction2.img/gasi/gasi2");
            cm.reservedEffect("Effect/Direction2.img/gasi/gasi22");
            cm.reservedEffect("Effect/Direction2.img/gasi/gasi3");
            cm.reservedEffect("Effect/Direction2.img/gasi/gasi4");
            cm.reservedEffect("Effect/Direction2.img/gasi/gasi5");
            cm.reservedEffect("Effect/Direction2.img/gasi/gasi6");
            cm.reservedEffect("Effect/Direction2.img/gasi/gasi7");
            cm.reservedEffect("Effect/Direction2.img/gasi/gasi8");
            cm.forceCustomDataQuest(2322, "1");
            break;
        }
    }
}