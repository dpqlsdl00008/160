var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("끼이이이익! 끽! 끽!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("배는 찼는데 정신은 여전히 없네... 이 상황은 대체 뭐지? 눈을 뜨자마자 보이는 건 원숭이에, 여기는 어디인지도 모르겠고... 배는 어떻게 된 거지? 넌 어떻게 된 건지 알고 있니?", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("끼익, 끼익!\r\n\r\n#b(원숭이가 고개를 끄덕인다. 정말 이 녀석이 상황을 아는 건가? 원숭이에게 저세히 물어보자!)#k");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}