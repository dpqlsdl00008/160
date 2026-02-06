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
                cm.sendNext("\r\n그럼 에레브에서 즐거운 여행 되시길 바랍니다.");
            }
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendNext("\r\n에, 또... 안녕하세요. 에레브를 떠나 다른 지역으로 가고 싶으신가요? 그렇다면 잘 오셨습니다. 에레브에서 #b빅토리아 아일랜드#k의 #b엘레니아#k까지 배를 운행하고 있습니다.");
            break;
        }
        case 1: {
            cm.sendYesNo("빅토리아 아일랜드의 #b하늘나루<에레브행>#k까지 가는데 걸리는 시간은 약 #b2분#k, 요금은 100메소입니다. 100메소를 내고 배를 타시겠습니까?");
            break;
        }
        case 2: {
            if (cm.getMeso() < 100) {
                cm.dispose();
                cm.sendNext("\r\n메소가 부족하신 것 같은데요?");
                return;
            }
            cm.sendNext("\r\n즐거운 여행 되시길 바랍니다.");
            break;
        }
        case 3: {
            cm.dispose();
            cm.gainMeso(-100);
            cm.warp(101000400, 0);
            break;
        }
    }
}