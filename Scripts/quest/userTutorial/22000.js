var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 7) {
            qm.sendNext("응? 유타에게는 말해주기 싫은 거니? 얘도 참, 형제끼리는 사이 좋게 지내야지.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("잘 잤니, 에반?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b네... 엄마도 잘 주무셨어요?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("그래... 그런데 넌 어째 잠을 제대로 자지 못한 얼굴이구나. 어젯밤에 천둥하고 번개가 엄청나게 쳤지. 그래서 그런가?");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b아뇨. 그게 아니라 간밤에 이상한 꿈을 꿔서요.#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("이상한 꿈? 무슨 꿈인데 그러니?");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b그러니까...#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrevS("#b(안개 속에서 드래곤을 만나는 꿈을 꿨다고 설명했다.)#k", 2);
            break;
        }
        case 7: {
            qm.sendAcceptDecline("호호호호, 드래곤이라고? 그거 굉장한데? 잡아 먹히지 않아서 다행이구나. 재미있는 꿈이니 유타에게도 말해주렴. 분명 좋아할 거야.");
            break;
        }
        case 8: {
            qm.dispose();
            qm.forceStartQuest();
            qm.sendNext("#b유타#k는 불독에게 아침 밥을 주러 #b앞 마당#k에 나갔단다. 집 밖으로 나가면 바로 볼 수 있을 거야.");
            break;
        }
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("어, 일어났냐. 에반? 아침부터 눈 밑이 왜 그렇게 퀭해? 밤에 못잤어? 뭐? 이상한 꿈을 꿨다고? 무슨 꿈인데? 에엥? 드래곤이 나오는 꿈을 꿨단 말이야?");
            break;
        }
        case 1: {
            qm.sendNextPrev("푸하하하하~ 드래곤이라고? 그거 굉장한데? 용꿈이잖아! 근데 혹시 그 꿈에 개는 한 마리 안나왔나? 하하하하~\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 20 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainExp(20);
            qm.forceCompleteQuest();
            break;
        }
    }
}