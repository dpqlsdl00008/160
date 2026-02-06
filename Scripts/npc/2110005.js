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
            goMap = 260020000;
            if (cm.getMapId() == 260020000) {
                goMap = 260020700;
            }
            cm.sendYesNo("#b5,000메소#k를 지불하고 #m" + goMap + "#(으)로 가시겠습니까?");
            break;
        }
        case 1: {
            cm.dispose();
            if (cm.getMeso() < 5000) {
                cm.sendNext("\r\n잠깐... 메소가 부족하신데요!");
                return;
            }
            cm.gainMeso(-5000);
            cm.warp(goMap, 0);
            break;
        }
    }
}