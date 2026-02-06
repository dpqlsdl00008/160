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
            qm.sendNext("기다리고 있었습니다. 그런데, 어쩌죠? 지금 자이로드롭은 운행중입니다. 고장이 나서 수리 중 이죠.");
            break;
        }
        case 1: {
            qm.sendNextPrev("네? 오픈 이벤트 때문에 Big3 이용 확인 도장이 필요하시다구요? 음, 그럼 제 부탁 하나를 들어주시겠어요? 그럼 확인 도장을 찍어드리겠습니다.\r\n#b#L0#네, 제가 무엇을 하면 되죠?\r\n#L1#지금 고장 중이라 이용 못하는 건데, 그냥 찍어주시면 안돼요?");
            break;
        }
        case 2: {
            qm.sendNextPrev("일단 수리를 위해 고장 수리 요원을 불렀습니니다만...고장의 원인이 무엇인지 빨리 알고 싶군요. 자이로드롭 꼭대기로 올라가 좀 살펴봐주세요. 이상한 점이 없는지 확인 해보시면 됩니다.");
            break;
        }
        case 3: {
            qm.sendNextPrev("자이로드롭을 타지도 못하셨는데, 제가 확인 도장을 그냥 찍어드릴 수는 없겠죠? 이 세상에 공짜란 없는 법이지요.");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}