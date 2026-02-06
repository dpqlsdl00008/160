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
            qm.sendNext("오랜만입니다. 에반씨. 그간 잘 지내셨습니까? 저희 쪽은 당신께서 일을 잘 해결해주신 덕분에 수월히 계획을 진행 할 수 있었습니다. 그럼 다음 임무를...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b임무를 받기 전에 하나만 물어봐도 되나요?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("네, 뭐든 물어보십시오. 궁금한 게 있으시다면 당연히 알려 드려야지요.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b전 이 단체의 임시 멤버인데도 아는 게 하나도 없어서요. 단체에 대해 좀 더 자세히 알고 싶어요.#k", 2);
            break;
        }
        case 4: {
            qm.sendAcceptDecline("...아. 그렇군요. 당신도 궁금하실 때가 되었군요... 흠. 좋습니다. 그럼 세 번째 임무를 드리기 전에 직접 만나 우리 단체에 대해 자세히 설명 드리도록 하겠습니다. 그럼 됩니까?");
            break;
        }
        case 5: {
            qm.dispose();
            qm.sendOk("그럼... #b루디브리엄 마을#k로 오십시오. 그 곳에 저희 단체가 사용하는 아지트 중 하나가 있으니, 그 곳에서 뵙기로 하죠. #b개구리 임의 집#k으로 오시면 됩니다.");
            qm.forceStartQuest();
            break;
        }
    }
}