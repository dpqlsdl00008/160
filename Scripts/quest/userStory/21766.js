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
            qm.sendNext("잠깐! 부탁 한 가지만 들어주겠어? 요새 #p20000#이 좀 수상해서...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b존이 요즘 어떻길래...?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("얼마 전까지만 해도 관절염이 심해졌다면서 울상이었는데 갑자기 웃기 시작했어!!");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b그게 왜?? 관절염이 좀 나아졌나보지#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("그게 수상하다는 거야. 그렇게 쉽게 낫다니... 게다가 요즘 항상 갖고 다니는 나무상자를 보며 히죽히죽 웃기도 한다고!");
            break;
        }
        case 5: {
            qm.sendNextPrev("그 나무상자에 무언가 비밀이 감춰져 있는 것 같은데... #p20000# 옆에 있는 나무상자를 몰래 살펴봐 주겠어?");
            break;
        }
        case 6: {
            qm.sendNextPrevS("#b알았어.나무상자에 무엇이 들어있는지 알아볼게.#k", 2);
            break;
        }
        case 7: {
            qm.sendNextPrev("그래. 드디어 존이 왜 이상한 행동을 하는지 알 수 있겠군.#p20000# 옆에 있는 나무상자를 잘 조사해서 알려줘.");
            break;
        }
        case 8: {
            qm.sendNextPrev("#p20000#이 있는 곳은 알고 있지? 여기서 오른쪽에 있다구. 상자는 존의 근처에 있을테니 잘 찾아봐.");
            break;
        }
        case 9: {
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
            qm.sendAcceptDecline("나무상자에 뭐가 들어있는지 알아냈어? 어서 알려줘...!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b그 나무상자에는 별 것이 없었어. 달팽이 껍질 같은 관절염에 쓸 재료들 뿐이었어..#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("그래? 존이 이상해진 이유는 대체 무엇일까..? 궁금하네..어쨌든 고마워.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(테오에게 약간 미안하긴 하지만, 모르는 게 약이야..)#k", 2);
            break;
        }
        case 4: {
            qm.dispose();
            qm.gainExp(200);
            qm.forceCompleteQuest();
            break;
        }
    }
}