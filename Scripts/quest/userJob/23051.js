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
            if (qm.isQuestActive(23051) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("블랙윙의 신무기를 파괴하는... 성공하신 겁니까? 제눈으로 보고도 믿어지지 않는군요. 당신이 미래를 바꾸었습니다! 정말, 정말 대단합니다! 당신이 레지스탕스에 있어줘서 다행입니다! 정말! 정말... 정말입니다!");
            }
            break;
        }
        case 1: {
            qm.sendYesNo("아... 너무 감격해서 해야 할 일 조차 잊을 것 같군요. 신무기가 파괴된 걸 알면 겔리메르가 수하들을 이끌고 내려올 게 분명합니다. 그러기 전에 먼저 도망치죠. 바로 지하본부 아지트 귀환 주문서를 사용하겠습니다. 자, 하나... 둘... 셋!");
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