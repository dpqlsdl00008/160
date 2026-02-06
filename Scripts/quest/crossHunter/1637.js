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
            qm.sendNext("미스틱 게이트는 검은 마법사와 관련이 있는 것이 분명해요. 미스틱 게이트를 통해 몬스터를 조종하고, 기운을 빨아들이고 있었어요! 근데 대체 왜 일까요? 메이플 월드를 혼란에 빠뜨리는 것이 목적일까요? 어쨌든 이 사실을 어서 게렉터님께 보고해야 돼요. 저는 몇 가지 더 조사를 한 뒤 갈테니 먼저 가서 보고 해주세요.");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("그럼 지금 당장 에델슈타인으로 보내드리겠습니다. 준비되셨나요?");
            break;
        }
        case 2: {
            qm.dispose();
            qm.warp(310000000, 0);
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
            qm.sendNext("리프레에서 뭔가 알아내셨습니까? 일단 조용한 곳으로 가서 이야기 합시다.");
            break;
        }
        case 1: {
            qm.dispose();
            qm.warp(931050500, 0);
            qm.forceCompleteQuest();
            break;
        }
    }
}