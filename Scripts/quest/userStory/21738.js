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
            qm.sendNext("무슨 일인가? 초대하지 않은 손님은 반기지 않지만... 왠지 이상한 기운을 풍기는 사람이군... 자네의 말은 들어줘야만 할 것 같아.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(거대 네펜데스에 대해 이야기 했다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("거대 네펜데스? 확실히 큰 문제이지만... 아직까지 오르비스에 영향을 미칠 수준은 아닌 것 같군. 잠깐, 거대 네펜데스가 있는 곳이 어디라고 했지?");
            break;
        }
        case 3: {
            qm.sendNextPrevS("인적 드문 산책로였다.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("...인적 드문 산책로라고? 그곳에 거대 네펜데스라니, 그렇다면 누군가 봉인된 정원에 침입하려 한다는 건가? 도대체 왜? 누가?");
            break;
        }
        case 5: {
            qm.sendNextPrevS("봉인된 정원?", 2);
            break;
        }
        case 6: {
            qm.sendAcceptDecline("... 봉인된 정원에 대해서는 알려줄 수 없다. 만약 알고 싶다면 일단 자네가 믿을만한 사람인지 점을 쳐봐야 하지... 자네의 운명을 훔쳐봐도 좋은가?");
            break;
        }
        case 7: {
            qm.sendNext("그렇다면 점을 치도록 하지. 잠시만 기다리게...");
            break;
        }
        case 8: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}