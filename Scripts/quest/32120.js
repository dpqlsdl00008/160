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
            qm.sendNext("저는 숲의 생물들을 연구하죠. 숲 속에서 정확히 원하는 생명체의 소리를 잡아내기 위해 이도구를 사용하기도 해요. 일단 소리를 잡아내기만 하면 대략적인 방향과 거리도 알 수 있죠.\r\n\r\n#i4033830# #b#z4033830##k");
            break;
        }
        case 1: {
            qm.askAcceptDecline("도움이 될진 모르겠지만, 적어도 없는 것보단 나을 거에요. 가서 그 불쌍한 요정 아이들을 구해주도록 해요.\r\n\r\n#b(수락 시 요정 학원 엘리넬로 이동합니다.)#k");
            break;
        }
        case 2: {
            qm.gainItem(4033830, 1);
            qm.warp(101072000, 0);
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
            qm.sendNextS("왔군요. 소득은 있었나요?", 8, 1500001, 1500001);
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(요정들에게 베티 박사의 물건을 보여주고 기능들을 설명했다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrevS("...그러니까 지금, 우리더러 이 불순한 인간의 문물을 사용하란 말인가? 안 돼! 절대 못 해! 적어도 안 돼!", 8, 1500002, 1500002);
            break;
        }
        case 3: {
            qm.sendNextPrevS("하지만 지금 이것밖엔 방법이 없어요, 교감 선생님.", 8, 1500009, 1500009);
            break;
        }
        case 4: {
            qm.sendNextPrevS("로웬의 말이 맞아요. 아이들을 찾는 것이 최우선 아닌가요?", 8, 1500008, 1500008);
            break;
        }
        case 5: {
            qm.sendNextPrevS("저 역시 탐탁치는 않지만, 지금으로서는 방법이 이것 밖에는 없답니다.", 8, 1500001, 1500001);
            break;
        }
        case 6: {
            qm.sendNextPrevS("...끄응... 그렇다면 할 수 없나, 이번 한 번만이라면... 아니, 아무리 그래도...", 8, 1500002, 1500002);
            break;
        }
        case 7: {
            qm.sendNextPrevS("제가 한 번 작동시켜 볼게요. 모두들 잠시만 조용히 해주세요.", 8, 1500000, 1500000);
            break;
        }
        case 8: {
            qm.sendNextPrevS("......", 8, 1500000, 1500000);
            break;
        }
        case 9: {
            qm.sendNextPrevS("숲의 여러가지 소리가 잡히는 것 같아요...", 8, 1500000, 1500000);
            break;
        }
        case 10: {
            qm.sendNextPrevS("???", 8, 1500000, 1500000);
            break;
        }
        case 11: {
            qm.sendNextPrevS("뭐야, 잡음밖에 안 들리잖아.", 8, 1500002, 1500002);
            break;
        }
        case 12: {
            qm.sendNextPrevS("쉿...조용히.", 8, 1500009, 1500009);
            break;
        }
        case 13: {
            qm.sendNextPrevS("!! 이 목소리는!", 8, 1500001, 1500001);
            break;
        }
        case 14: {
            qm.sendNextPrevS("방향은 뒷뜰이예요.", 8, 1500000, 1500000);
            break;
        }
        case 15: {
            qm.sendNextPrevS("기다려라, 얘들아! 이 교감 선생님이 너릐들을 구하러 간다!", 8, 1500002, 1500002);
            break;
        }
        case 16: {
            qm.sendNextPrevS("아르웬, 우리도 함께 아이들을 찾아보자!", 8, 1500009, 1500009);
            break;
        }
        case 17: {
            qm.sendNextPrevS("모두들, 잠깐만...!", 8, 1500001, 1500001);
            break;
        }
        case 18: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}