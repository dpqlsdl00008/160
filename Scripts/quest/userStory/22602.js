var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 13) {
            qm.sendNext("응? 왜? 혹시 나한테 이 비늘이 필요 할 거라고 생각하는 거야? 어차피 새로운 비늘이 싹 났으니 이 비늘은 필요 없어~ 마스터 좋을대로 사용해도 돼.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("마스터~ 이것 봐. 나 또 다시 성장했어.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b우와, 너 정말 엄청 커졌구나... 헉! 목소리도 바뀌었어.#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("흠흠~ 그래? 어때, 멋져?");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b응! 멋져! 전부터 느꼈지만 드래곤은 한 번에 정말 많이 성장하는 구나. 인간과 달리 탈피한다고 하더니. 그래서 그런가?#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("응. 새로운 비늘들이 자라면서 원래 입고 있던 비늘들을 전부 벗어 버리니까. 인간으로 치면... 몸이 커지면서 옷도 한 번에 갈아 입는 것 같은 느낌일까나?");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b새로 날 비늘이라 그런지 광택이 더 반짝 반짝 하는 걸.#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("에헷, 그렇지?");
            break;
        }
        case 7: {
            qm.sendNextPrevS("#b(덩치는 커졌지만 귀여운 말투는 여전하구나...)#k", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("그나저나 마스터, 이거 한 번 봐 봐. #i4032502# 탈피하면서 벗어버린 비늘 중에 하나인데 이 것만 반짝 반짝해. 다른 비늘들은 모두 힘을 잃고 부스러져 버렸는데 말이야. 이 비늘에는 아직도 내 힘이 그대로 담겨 있는 것 같아. 이거 어딘가 쓸 수 있지 않을까?");
            break;
        }
        case 9: {
            qm.sendNextPrevS("#b응? 어디에 말이야?#k", 2);
            break;
        }
        case 10: {
            qm.sendNextPrev("인간은 뿔도 없고 비늘도 없고 발톱도 없고 브레스도 쓸 수 없는 대신 쓸모 있는 물건들을 잘 만들잖아. 그런 물건을 만드는 재료로 쓰면 좋지 않을까? 내 비늘이니 당연히 엄청 튼튼한데다 힘도 담겨 있으니 아마 마스터의 힘을 강하게 해줄 거야.");
            break;
        }
        case 11: {
            qm.sendNextPrevS("#b우와, 그럴 듯 한데? 미르, 너 대단하다! 언제부터 그런 생각을 할 수 있게 된 거야!", 2);
            break;
        }
        case 12: {
            qm.sendNextPrev("엣헴. 마스터하고 지낸 게 하루 이틀이야? 이제 인간에 대해서 나도 잘 안다고. 이 정도야 간단하지.");
            break;
        }
        case 13: {
            qm.sendAcceptDecline("자, 마스터. 여기 비늘을 줄 테니 받아. 마스터의 힘으로 아마 이 비늘로 멋진 물건이 될 수도 있잖아? 잘 간직해줘!");
            break;
        }
        case 14: {
            qm.dispose();
            qm.gainItem(1142156, 1);
            qm.forceCompleteQuest();
            qm.sendOkS("#b미르에게 드래곤의 비늘을 받았다. 비늘이 손에 들어오자 신기하게도 #i1142156# #z1142156#로 변하였다.)#k", 2);
            break;
        }
    }
}