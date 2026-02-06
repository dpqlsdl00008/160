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
            qm.sendNext("마스터... 으윽...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b무슨 일이야? 어디 아파?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("그, 그게... 으으으... 수, 숨이 막혀...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b숨이 막힌다고? 왜지? 성장이 뭔가 이상하게 됐나?#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("그, 그게 아니라 으으윽...");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b그게 아니면 뭔데? 어서 말해 봐!#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("안장이 너무 꽉 껴!");
            break;
        }
        case 7: {
            qm.sendNextPrevS("#b...엥?#k", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("으으, 날개 움직이기도 힘들어! 이 안장 너무 작아! 내 몸에 맞지 않는다고! 이대로라면 마스터를 태울 수도 없을 것 같아!");
            break;
        }
        case 9: {
            qm.sendNextPrevS("#b그, 그래? 그럼 어떻게 하지?#k", 2);
            break;
        }
        case 10: {
            qm.sendAcceptDecline("새로운 안장이 필요해~ 마스터, 전에 안장을 만들었던 켄타라는 사람에게 가서 새로운 안장을 부탁해줘.");
            break;
        }
        case 11: {
            qm.sendNextS("#b(미르의 몸에 비해 안장이 너무 작아 불편해 보인다. 아쿠아리움의 켄타에게 가서 새로운 안장을 만들자. 안장이 완성 될 때 까지 라이딩은 자제하는 게 좋겠다.)#k", 2);
            break;
        }
        case 12: {
            qm.sendNextPrev("그나저나 마스터... 새 안장 만들려면 또 엄청나게 메소가 드는 거 아냐?");
            break;
        }
        case 13: {
            qm.sendNextPrevS("#b...에휴우...#k", 2);
            break;
        }
        case 14: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}