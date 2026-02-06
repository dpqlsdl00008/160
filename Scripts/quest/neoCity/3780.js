var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 4) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("한 가지 고민이 있어. 아무리 생각해 봐도 이번 사고가 왜 일어났는지 알 수가 없다는 거지. 이런 말을 하긴 조금 쑥쓰럽지만 나는 오랫 동안 시간 여행을 하면서 한 번도 이런 일을 겪어본 일이 없어.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("어쨋든 부품도 모두 회수했고 할 수 있는 일은 모두 다 했잖아요?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("사고의 원인을 파악하지 못한다면 다음에도 이런 일이 일어나지 않으리란 보장을 못해. 이런 위험성을 안은 채 시간 여행을 계속할 수는 없다고. 다행이 자네가 타임 다이버의 부품을 모두 찾아줬으니 이걸 이용하면 될거야.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("타임 다이버가 그런 것도 알려주나요?", 2);
            break;
        }
        case 4: {
            qm.sendYesNo("아니. 하지만 시간의 힘에 밀접한 연관이 있는 사람들을 찾을 수는 있지. 이 시간대에도 시간의 힘에 대해 알고 있는 사람들이 있을거야. 그들에게 뭔가를 알아낼 수 있을 지도 몰라. 어때 한번 조사 해보겠어?");
            break;
        }
        case 5: {
            qm.sendNext("타임 다이버의 힘에 몸을 맡겨. 가장 가까운 곳에 있는 목표를 스스로 찾아 갈거야. 그럼 좋은 소식 기대하지.");
            break;
        }
        case 6: {
            qm.dispose();
            qm.warp(240000000, 0);
            qm.forceStartQuest();
            break;
        }
    }
}