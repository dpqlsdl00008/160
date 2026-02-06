var status = -1;

function start() {
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
            cm.sendNext("나는 블랙윙의 인형사 프란시스. 몇 번이고 내가 심어놓은 인형을 골라내다니... 잘도 내 일을 방해해 줬더군. 정말 열받지만 이번만은 용서해 주겠다. 하지만 한 번만 더 방해했다간... 검은 마법사님의 이름을 걸고 가만두지 않겠어.");
            break;
        }
        case 1: {
            cm.sendNextPrevS("#b(...블랙윙? 방해? ...도대체 무슨 일이지? 몬스터에게서 인형을 찾은 것과 검은 마법사는 대체 무슨 관계가 있는 걸까? 트루에게 가서 상담해 보자.)#k", 2);
            break;
        }
        case 2: {
            cm.dispose();
            cm.warp(105070300, 0);
            break;
        }
    }
}