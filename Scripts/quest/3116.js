var status = -1;

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
            qm.sendNext("이봐 자네 #b#h ##k이지? 흐흐흐. 내가 너를 부른 이유가 궁금하겠지. 나는 #b샤모스#k라고 해. 지금 비록 이렇게 감금되어 불편한 몸이지만 너한테 아주 중요한 부탁이 있어서 불렀지. 지금 나에 대해서 궁금한 것이 많을 거야. 흐흐흐");
            break;
        }
        case 1: {
            qm.sendYesNo("나는 보다 시피 인간이 아닌 호브라는 종족이야. 그래 모두 나를 그렇게 부르고 있지. 하지만 나는 정말 누구지? 왜 나는 이곳에 갇혀 있지? 왜 나는 나의 어린 시절이 기억나지 않는 것 이지? 너무 답답해... 네가 도와 줘야만 해. 이 불쌍한 나를 위해 부탁을 좀 들어 주겠나?");
            break;
        }
        case 2: {
            qm.sendNext("지금 바로 나한테 와주게. 이미 알고 있을지 모르겠지만 나는 #b엘나스 장로의 관저 지하#k에 있어.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}