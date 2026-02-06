var status = -1;

function start() {
    action(1, 0, 0);
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
            cm.sendNext("\r\n훌쩍... 지, 집에 가고 싶어.");
            if (cm.getMonsterCount(cm.getMapId()) != 0) {
                cm.dispose();
            }
            if (cm.haveItem(4032831, 1) == true) {
                cm.dispose();
            }
            break;
        }
        case 1: {
            cm.sendNextPrev("\r\n엣? 누, 누구세요? 혹시 절 도와주러 오신 분인가요? 제발 여기서 나가게 해주세요! 너무 너무 무서워요!");
            break;
        }
        case 2: {
            cm.dispose();
            cm.gainItem(4032831, 1);
            break;
        }
    }
}