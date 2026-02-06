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
            qm.sendNext("드디어 수련 기사가 됐군요. 바로 임무를 드리고 싶습니다만... 아직 영 부족해 보이는걸요? 이대로 빅토리아 아일랜드에 나갈 수 있겠어요?");
            break;
        }
        case 1: {
             qm.sendAcceptDecline("나가는 건 당신의 자유지만 기사 단원이 비실 비실한 모습을 보이는 건 여제의 명예에 누가 됩니다. 책사로서 그런 꼴은 절대 못 보죠. 일단은 좀 더 수련을 하세요.");
             break;
        }
        case 2: {
            qm.sendNext("수련 교관 키쿠가 당신의 수련을 도와드릴 겁니다. 레벨이 13이 넘으면 임무를 드리도록 하죠. 그러니 그 때 까지는 꼼짝 말고 수련에 임해 주세요.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}