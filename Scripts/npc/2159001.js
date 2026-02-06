var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
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
            cm.sendNext("늦었잖아, #h0#~ 어서 이쪽으로 와!");
            break;
        }
        case 1: {
            cm.sendNextPrevS("왜 이렇게 늦은 거야? 예전부터 이쪽으로 놀러 오기로 했었잖아. 설마 겁 먹은 건 아니지?", 4, 2159002);
            break;
        }
        case 2: {
            cm.sendNextPrevS("겁 안먹었어.", 2);
            break;
        }
        case 3: {
            cm.sendNextPrevS("저, 정말? 나는 겁 나는데... 어른들이 벤 광산 쪽으로는 오지 말라고 했잖아. #r블랭윙 사람들#k도 지키고 있고...", 4, 2159000);
            break;
        }
        case 4: {
            cm.sendNextPrevS("그러니까 일부러 감시자가 없는 방향으로 돌아서 여기까지 온거잖아. 이럴 ? 아니면 언제 우리가 #b에델슈타인#k에서 나와 보겟냐고 어휴, 겁쟁이.", 4, 2159002);
            break;
        }
        case 5: {
            cm.sendNextPrevS("하지만.. 혼나면 어떻게 해?", 4, 2159000);
            break;
        }
        case 6: {
            cm.sendNextPrev("이미 여기까지 온 거 어쩌겟어. 혼날 ? 혼나더라도 놀고 가자. 종목은 숨바꼭질!");
            break;
        }
        case 7: {
            cm.sendNextPrevS("에엥? 숨바꼭질!", 2);
            break;
        }
        case 8: {
            cm.sendNextPrevS("유치하게..", 4, 2159002);
            break;
        }
        case 9: {
            cm.sendNextPrev("뭐가 유치해! 여기서 달리 할 놀이가 있어? 있으면 말해보시지! 그리고 술래는 너야, #h0#! 지각했으니까 말이지. 자, 그럼 우리는 숨는다? 10까지 센 후  찾아내!");
            break;
        }
        case 10: {
            cm.dispose();
            cm.warp(931000001, 1);
            break;
        }
    }
}