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
            qm.askAcceptDecline("어쩐지 수상한 기욱을 풀풀 풍기는 곳이 있습니다. 조사해 보시겠습니까?");
            break;
        }
        case 1: {
            qm.sendNextS("#b(정체를 알 수 없는 연극 대본을 발견했다. 조금만 읽어볼까?)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrevS("[요정들의 대본]\r\n\r\n~제 3장~\r\n\r\n팬텀 : (관객들을 향해) 아아, 비통이 온 세상에 넘쳐 흐르는구나. 너, 간악한 검은 마법사의 군단장이여! 괴도의 이름으로 맹세컨대 너는 절대로 편안히 잠들지 못할 것이다. 이 괴도 팬텀이 훔칠 마지막 것은 보석도 아니고 미술품도 아닌 서 푼도 되지 않는 너의 값싼 목숨일 것이다.", 8, 1500022, 1500022);
            break;
        }
        case 3: {
            qm.sendNextPrevS("...? 다른 페이지를 읽어보자.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrevS("[요정들의 대본]\r\n\r\n아란 : (호기롭게) 빛의 수호자여, 빛보다도 빠르게 가거라! 이곳은 내가 막고 있을 테니 적들이 해일처럼 몰려와도 네 옷깃을 스치지 못하게 할 것이다.\r\n루미너스 : (탄식하며) 신의 이름에 걸고 말하건대 너 같은 전사가 어디에 또 있을까! 그 용맹이 하늘을 떠받들 듯 하구나. 적들의 잔해를 수천 수만 베어넘긴 너의 무구가 더 이상 닳기 전에 검은 마법사를 해치우고 돌아오겠다.\r\n\r\n조명이 어두워지며 무대 한 편에 프리드와 메르세데스 등장.\r\n\r\n프리드 : 오, 엘프의 여왕이여! 사리를 모르는 짐승이라도 발등에 입을 맞추고 싶어질 찬란한 아름다움이여! 그대와 같은 전장에 있다는 것은 더할 나위 없는 축복입니다.\r\n메르세데스 : 그대, 드래곤 마스터여! 함께 검은 마법사를 물리칩시다. 시위를 떠난 화살처럼 이미 우리의 전투는 시작되었습니다.", 8, 1500022, 1500022);
            break;
        }
        case 5: {
            qm.sendNextPrevS("아이들이 왜 이런 대본을... 일단은 가져가서 쿠디와 함께 이야기해보자.\r\n#b(마법사 쿠디와 대화하자.)#k", 2);
            break;
        }
        case 6: {
            qm.dispose();
            qm.gainItem(4033828, 1);
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
            qm.sendNext("대본을 발견했다고? 어디 한 번 읽어볼까?\r\n\r\n...이건 검은 마법사를 봉인한 메이플 월드의 영웅들에 대한 이야기잖아? 대사들이 어쩐지 조금씩 격양되어있긴 하지만 ...왜 이런 대본을 요정 아이들이 가지고 있었던 걸까?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainItem(4033828, -1);
            qm.forceCompleteQuest(32111);
            qm.forceCompleteQuest();
            break;
        }
    }
}