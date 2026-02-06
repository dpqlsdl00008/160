var status = -1;

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
            if (qm.isQuestActive(23049) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("블랙윙의 신무기를 없애는데 성공했구나! 하하... 믿어지지가 않아! 나조차도 하지 못한 일을 해내다니! 넌 정말 대단한 녀석이야! 네가 레지스탕스라는 게 정말 자랑스럽다!");
            }
            break;
        }
        case 1: {
            qm.sendYesNo("아, 아니 이럴 때가 아니지... 신무기가 파괴된 걸 알아챈 겔리메르가 금방 수하들을 이끌고 여기로 내려올 거야. 그 전에어서 도망치자고! 바로 지하본부 아지트 귀환 주문서를 사용할게! 자, 하나... 둘... 셋!");
            break;
        }
        case 2: {
            qm.dispose();
            qm.warp(310010000, 0);
            qm.forceCompleteQuest();
            break;
        }
    }
}