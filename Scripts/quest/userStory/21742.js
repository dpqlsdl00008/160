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
            qm.sendNext("뭐 별로 바쁜 건 아니지만 그렇다고 해도 약을 만들 기분도 아니니 나중에 다시 찾아왔으면 좋겠달까나 뭐랄까나. 아무튼 볼 일 없으면 비켜달랑께?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("블랙윙의 무사를 만났다고 들었는데...", 2);
            break;
        }
        case 2: {
            qm.sendNext("아. 시커무리죽죽한 데다가 미간에는 주름 팍 잡은 그 남자 말이여? 만났지. 만났고 말고. 심지어 그 사람 물건도 맡아줬는걸? 왠 물건을 떠억 넘기면서 무공 할배한테 전하라카더라고.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("물건?", 2);
            break;
        }
        case 4: {
            qm.sendNext("응. 큼지막한 #b족자#k를 무턱대고 주면서 꼭 전하라던데. 살벌한 면상이 안 전하면 찾아오기라도 할 기세더랬지. 어이쿠, 무서워라.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("그래서 족자는 전했나?", 2);
            break;
        }
        case 6: {
            qm.sendAcceptDecline("아니. 그게... 실은 문제가 좀 생겼드랬지. 들어 볼텐가?");
            break;
        }
        case 7: {
            qm.sendNext("그게 말이야, 내가 새로운 약제를 만든다고 단지에 물과 약초를 잔뜩 넣고 끓이는데 그만 족자가 거기 퐁당 빠져버렸지 뭔감. 잽싸게 꺼내긴 했는데 족자가 물에 푹 젖어가지고 글씨는 다 사라졌더래지.");
            break;
        }
        case 8: {
            qm.sendNextPrev("이래서야 무공 할배에게 전하나마나 아니겠는감? 일단 족자를 복원한 후에 전해야쓰겠네. 그래서 말인데 네가 해줘야 할 일이 하나 있구만. 저기에서 족자에 글씨 쓰는 녀석이 무릉 최고의 화사 #b진진#k인데, 녀석이라면 족자를 복원할 수 있을 거구먼");
            break;
        }
        case 9: {
            qm.dispose();
            qm.forceStartQuest(21763, "0.0");
            if (qm.haveItem(4220151, 1) == false) {
                qm.gainItem(4220151, 1);
            }
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
            qm.sendYesNo("워떤감? 족자 복원은 잘 되어가는감? 어디, 그럼 뭐라고 쓰여 있는지 한 번 볼꺼나?");
            break;
        }
        case 1: {
            qm.sendNext("히에에엑! 이게 뭐시여!");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(4220151, -1);
            qm.forceCompleteQuest();
            break;
        }
    }
}