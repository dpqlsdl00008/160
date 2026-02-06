var status = -1;

function start() {
    status = 14;
    if (cm.getInfoQuest(23007).indexOf("vel00=1") != -1 && cm.getInfoQuest(23007).indexOf("vel01=1") == -1) {
        status = -1;
    }
    if (cm.getInfoQuest(23007).indexOf("vel01=1") != -1) {
        status = 7;
    }
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
            cm.sendNext("난.. #r닥터 겔리메르#k의 실헴체야. 이름은 #b벨비티#k라고해.. 어쩌다 여기까지 왔는지 모르겠지만 어서 나가! 겔리메르에게 들키면 끝장이야!");
            break;
        }
        case 1: {
            cm.sendNextPrevS("실험체? 겔리메르? 도대체 무슨 말을 하는 거야? 대체 여긴.. 뭘하는 데지? 넌 왜 거기 들어가 이는거야?", 2);
            break;
        }
        case 2: {
            cm.sendNextPrev("겔리메르를 몰라? 닥터 겔리메르.. 블랙윙의 매드 사이언티스트잖아! 여긴 겔리메르의 연구실이고, 겔리메르는 여기서 인체 실험을 벌이고 있어..");
            break;
        }
        case 3: {
            cm.sendNextPrevS("인체.. 실험?", 2);
            break;
        }
        case 4: {
            cm.sendNextPrev("그래, 인체 실험. 너도 잡히면 심험 재료가 될지도 몰라! 어서 도망쳐!");
            break;
        }
        case 5: {
            cm.sendNextPrevS("뭐? 도, 도망..? 하지만, 넌..!", 2);
            break;
        }
        case 6: {
            cm.sendNextPrev("..쉿! 목소리를 낮춰! 닥터 겔리메르가 오고 있어!");
            break;
        }
        case 7: {
            cm.dispose();
            cm.updateInfoQuest(23007, "vel00=2");
            cm.warp(931000011, 0);
            break;
        }
        case 8: {
            cm.sendNext("휴우.. 겔리메르는 무슨 일이 있어서 간 것 같아... 자, 이 때야. 너도 어서 도망쳐.");
            break;
        }
        case 9: {
            cm.sendNextPrevS("나만 도망치라고? 그럼 넌...?", 2);
            break;
        }
        case 10: {
            cm.sendNextPrev("난 도망칠 수 없어. 닥터 겔리메르는 자신의 실험체를 모두 기억하고 있으니까 한 명이라도 사라지면 알아챌 거야.. 그리니 너라도 어서 도망쳐.");
            break;
        }
        case 11: {
            cm.sendNextPrev("그럴 순 없어! 너도 같이 도망쳐!");
            break;
        }
        case 12: {
            cm.sendNextPrev("불가능하다니까. 게다가 어차피 난.. 이 안에 갇혀 있으니까 도망치고 싶어도 그럴 수 없어... 신경 써줘서 정말 고마워. 이런 걱정 받아본거 정말 오랜만이야. 자. 어서 가!");
            break;
        }
        case 13: {
            cm.sendYesNoS("#b(밸비티는 모든 것을 포기한 것처럼 눈을 감았다. 어떻게 하지? 밸비티가 갇힌 실험관을 한 번 내리쳐 보자.)#k", 2);
            break;
        }
        case 14: {
            cm.dispose();
            cm.gainExp(60);
            cm.warp(931000013, 0);
            break;
        }
        case 15: {
            cm.dispose();
            break;
        }
    }
}