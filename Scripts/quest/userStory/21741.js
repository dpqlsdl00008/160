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
            qm.sendNext("그 동안 레벨 업은 많이 했나, 영웅님? 오랜만에 블랙윙과 관련이 있는 듯한 흥미로운 정보가 발견되어서 말이야. 이번에는 좀 먼데... #b무릉#k이라는 마을 알아? 거기까지 가 줘야 할 것 같아.");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("무릉의 #b도의진#k이라는 자가 블랙윙과 접촉한 모양이야. 어쩌다 그렇게 된 건지는 모르겠지만 아무래도 확실한 정보인 것 같아. 왜 블랙윙이 도의진에게 접촉했는지, 어떤 거래가 있었는지 알아봐줘.");
            break;
        }
        case 2: {
            qm.sendNext("도의진은 말투가 좀 이상한 사람이니 참을성 있게 대화해야 할 거야. #b블랙윙의 무사를 만났다고 들었는데...#k라는 키워드로 말을 걸어봐.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}