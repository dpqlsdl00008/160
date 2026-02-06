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
            if (qm.isQuestActive(23050) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("블랙윙의 신무기를 없애는데 성공했구나! 으하하하하! 역시 넌 대단한 녀석이야! 내 눈은 절대 틀리지 않는다고! 궁수의 눈만큼 날카로운 건 없지! 너라면 해낼 거라고 믿었어! 네가 레지스탕스라는게 자랑스럽다!");
            }
            break;
        }
        case 1: {
            qm.sendYesNo("성격 같아서는 당장 겔리메르 녀석에게 자랑하고 싶지만, 그 녀석이 수하들을 잔뜩 끌고 나타나면 도망치기도 귀찮아질 테니 이쯤에서 얼른 지하본주로 돌아가자. 바로 지하본주 아지트 귀환 주문서를 사용할게! 자, 하나... 둘... 셋!");
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