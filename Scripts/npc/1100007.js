var status = -1;

function start() {
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 1) {
            if (status == 1) {
                cm.sendNext("\r\n그럼 엘리니아에서 즐거운 여행 되시길 바랍니다.");
            }
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendNext("\r\n엘리니아를 떠나 #b에레브#k로 이동하고 싶으신 건가요?");
            break;
        }
        case 1: {
            cm.sendYesNo("#b에레브#k로 가는데 #b1,000#k 메소가 필요 합니다. 정말로 엘리니아를 떠나 #b에레브#k로 가시겠습니까?");
            break;
        }
        case 2: {
            if (cm.getMeso() < 1000) {
                cm.dispose();
                cm.sendNext("\r\n메소가 부족하신 것 같은데요?");
                return;
            }
            cm.sendNext("\r\n즐거운 여행 되시길 바랍니다.");
            break;
        }
        case 3: {
            cm.dispose();
            cm.gainMeso(-1000);
            cm.warp(130000000, 0);
            break;
        }
    }
}