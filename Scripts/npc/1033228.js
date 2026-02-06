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
            cm.sendNext("네가 메르세데스인가?");
            break;
        }
        case 1: {
            cm.sendNextPrevS("...? 넌 누구지? 소생의 불꽃은 어디에 있느냐!", 2);
            break;
        }
        case 2: {
            cm.sendNextPrev("소생의 불꽃? 아... 그걸로 속인 거로구나. 하하하... 잘 속는 녀석이네. 그런 건 없다!");
            break;
        }
        case 3: {
            cm.sendNextPrevS("...레드티라는 자의 말로는... 모두 거짓이었던 거냐!", 2);
            break;
        }
        case 4: {
            cm.sendNextPrev("생긴 거에 비해 단순하다더니, 정말 그렇구나. 하하... 더 떠들 필요는 없겠지?");
            break;
        }
        case 5: {
            cm.dispose();
            cm.resetMap(cm.getMapId());
            cm.spawnMonster(9300432, -534, 492);
            break;
        }
    }
}