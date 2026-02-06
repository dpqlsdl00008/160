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
            cm.forcedInput(2);
            cm.sendDelay(2000);
            break;
        }
        case 1: {
            cm.forcedInput(0);
            cm.sendNextS("#b어머니!! 어디 계신가요!!#k", 17);
            break;
        }
        case 2: {
            cm.forcedInput(2);
            cm.sendDelay(2000);
            break;
        }
        case 3: {
            cm.forcedInput(0);
            cm.sendNextS("#b데미안!! 어디 있는거냐... 살아 있다면 대답해줘!!#k", 17);
            break;        
        }
        case 4: {
            cm.forcedInput(1);
            cm.sendDelay(1000);
            break;
        }
        case 5: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 0, 0, -110, 1, 0);
            cm.forcedInput(0);
            cm.sendDelay(2000);
            break;
        }
        case 6: {
            cm.sendNextS("#b이것은...#k", 17);
            break;        
        }
        case 7: {
            cm.showFieldEffect(false, "demonSlayer/pendant");
            cm.sendDelay(4000);
            break;
        }
        case 8: {
            cm.sendNextS("#b어머니... 데미안...#k", 17);
            break;
        }
        case 9: {
            cm.sendNextPrevS("#b...#k", 17);
            break;
        }
        case 10: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/5", 0, 0, -110, 1, 0);
            cm.sendDelay(3000);
            break;
        }
        case 11: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/6", 0, 0, -170, 1, 0);
            cm.sendDelay(3000);
            break;
        }
        case 12: {
            cm.dispose();
            cm.warp(927000081, 0);
            break;
        }
    }
}