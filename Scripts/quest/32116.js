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
            qm.sendNextS("어서 와, #b#h ##k. 이제 막 이야기를 하고 있던 참이야.", 8, 1500000, 1500000);
            break;
        }
        case 1: {
            qm.sendNextPrevS("이야기는 전해 들었습니다. 아이들이 연극 준비를 하고 있었다구요?", 8, 1500001, 1500001);
            break;
        }
        case 2: {
            qm.sendNextPrevS("우리가 알아낸 것은 그것뿐이에요. 혹시 단서가 될 수 있을까요?", 8, 1500000, 1500000);
            break;
        }
        case 3: {
            qm.sendNextPrevS("... 모든 것이 제 불찰입니다, 교장 선생님.", 8, 1500002, 1500002);
            break;
        }
        case 4: {
            qm.sendNextPrevS("...", 8, 1500002, 1500002);
            break;
        }
        case 5: {
            qm.sendNextPrevS("얼마 전의 일이었습니다. 아이들이 인간 세계의 영웅들을 좋아하고 또 영웅들을 흉내내는 연극을 하려 하더군요. 그래서 크게 혼을 내고 벌을 준 적이 있었죠.", 8, 1500002, 1500002);
            break;
        }
        case 6: {
            qm.sendNextPrevS("어째서 혼을 내신 거죠? 아이들이라면 누구나 영웅을 좋아하는게 당연하잖아요.", 8, 1500000, 1500000);
            break;
        }
        case 7: {
            qm.sendNextPrevS("우리 엘리넬은 기본적으로 인간들을 신뢰하지 않아. 그렇기에 인간 세계의 영웅을 추앙하는 것은 아이들의 교육상 좋지 않다고 생각했지.\r\n\r\n하지만 그 후로도 아이들이 계속해서 연극 준비를 하고 있었을 줄은 몰랐어... 분명 나에게 들키지 않기 위해 어디선가 몰래 연습을 했을 거야.", 8, 1500002, 1500002);
            break;
        }
        case 8: {
            qm.sendNextPrevS("아이들은 선생님들의 눈이 닿지 않는 다른 장소에서 연습을 하다가 실종되었던 거군요. 예를 들면 숲 속 깊숙한 곳이라거나...", 8, 1500000, 1500000);
            break;
        }
        case 9: {
            qm.sendNextPrevS("이럴 수가, 모든 것이 나의 잘못이야. 혹시라도 아이들이 잘못된다면... 나는, 나는...", 8, 1500002, 1500002);
            break;
        }
        case 10: {
            qm.sendNextPrevS("진정하세요, 교감 선생님. 이럴 때일수록 침착함을 되찾아야 합니다.", 8, 1500001, 1500001);
            break;
        }
        case 11: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
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
            qm.askAcceptDecline("일단 사과부터 해야겠군요. 그런 일이 있었을 줄은... 모든 것은 저희의 오해였어요. 아이들을 찾을 수 있도록 계속해서 도와주겠어요?");
            break;
        }
        case 1: {
            qm.sendNext("아이들을 찾으려면 여떻게 해야 할까요? 생각을 정리할테니 다시 한 번 말을 걸어 주세요.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}