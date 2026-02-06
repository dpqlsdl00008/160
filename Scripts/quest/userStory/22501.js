var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 3) {
            qm.sendNext("우왁, 마스터가 밥 먹이는 걸 거절했어~ 말도 안 돼! 이건 드래곤 학대다!");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("이봐, 마스터. 자, 내 능력을 보여줬으니 마스터도 어서 나에게 능력을 보여줘. 음식을 구하는 능력을! 응? 내 힘을 쓸 수 있게 되었으니 그만큼 날 돌봐줘야지.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("으... 뭐가 뭔지 모르겠지만 일단 돌봐줘야 할 처지 같네. 맛있는 거라고? 뭐가 먹고 싶은데? 드래곤은 뭘 먹는지 감도 안 와.", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("태어난 지 몇 분 되지도 않은 내가 그런 걸 알 리가 없잔아. 내 머릿속에 있는 거라고는 내가 드래곤이라는 거, 네가 마스터라는 거, 그리고 마스터는 무조건 나한테 잘 해줘야 한다는 거밖에 없어!");
            break;
        }
        case 3: {
            qm.sendAcceptDecline("그 외의 것들은 마스터와 함께 지내며 하나씩 배워가야 하는 모양이야~ 하지만 일단 지식보다 배를 체울 음식이 더 급해... 마스터, 맛있는 걸 구해줘!");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceStartQuest();
            qm.sendOkS("#b(아기 드래곤 미르는 배가 무척 고픈 모양이다. 뭐라도 먹여야 할텐데... 그런데 드래곤은 뭘 먹을지 도무지 알 수 없다. 혹시 아빠는 알고 계시지 않을까? 한 번 아빠에게 물어보자.)", 2);
            break;
        }
    }
}