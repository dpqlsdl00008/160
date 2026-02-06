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
            qm.sendYesNo("마지막으로, 이제 #h #님이 구해다 주신 #b투구 페페의 투구#k를 사용해 탈출할 때가 왔어요. 제가 먼저 탈출 할테니 무사히 나갈 수 있도록 지켜봐 주세요. 정말 감사합니다. 저희 형한테도 용사님 이야기를 꼭 해놓을 게요.");
            break;
        }
        case 1: {
            qm.sendNext("정말 감사해요. 그럼... 이제 변장하고 출발하도록 할게요.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.showNpcSpecialActionByTemplateId(1300008, "hat");
            qm.forceCompleteQuest();
            break;
        }
    }
}