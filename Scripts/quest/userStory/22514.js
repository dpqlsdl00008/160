var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 4) {
            qm.sendNext("뻥이였던 거야? 하아... 마스터도 참. 애도 아니고.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("그럼 수련하자! 수련하면 되잖아! 파란버섯쯤은 거뜬히 물리칠 수 있을 때까지 수련한 다음, 아까 그 여자 인간 일을 도와주면 되잖아! 수련해, 수련! 수려어어어언!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(윽, 엄청나게 조르기 시작했다. 어떻게 하지? 그러고 보니 헤네시스 주변에 수련장이 있다는 소문을 들었는데 장로 스탄 아저씨가 알지 않을까?)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("수련하자아아아아아, 마스터어어어어어어~");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b아, 알았어! 알았다고! 스탄 아저씨한테 물어보면 되잖아!#k", 2);
            break;
        }
        case 4: {
            qm.sendAcceptDecline("정말? 정말 수련 하는 거야?");
            break;
        }
        case 5: {
            qm.sendNext("에헤헤, 그럼 수련 하는 거다?");
            break;
        }
        case 6: {
            qm.dispose();
            qm.forceStartQuest();
            qm.sendOkS("#b(일단 진정시킨 것 같다. 그럼 스탄 아저씨에게 가서 수련장에 대해 물어보자.)#k", 2);
            break;
        }
    }
}