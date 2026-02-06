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
            cm.reservedEffect("Effect/Direction2.img/open/back0");
            cm.reservedEffect("Effect/Direction2.img/open/back1");
            cm.reservedEffect("Effect/Direction2.img/open/light");
            cm.reservedEffect("Effect/Direction2.img/open/pepeKing");
            cm.reservedEffect("Effect/Direction2.img/open/line");
            cm.reservedEffect("Effect/Direction2.img/open/violeta0");
            cm.reservedEffect("Effect/Direction2.img/open/violeta1");
            cm.reservedEffect("Effect/Direction2.img/open/frame");
            cm.reservedEffect("Effect/Direction2.img/open/chat");
            cm.reservedEffect("Effect/Direction2.img/open/out");
            break;
        }
    }
}