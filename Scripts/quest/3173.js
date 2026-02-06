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
            qm.sendNext("수 백 년 동안이나 이 성을 보았지만 아직도 믿어지지 않아요. 믿어지세요? 예전에는 정말 아름다운 성이었답니다. 창문 너머로 보이는 눈 쌓인 풍경마저 마음을 따스하게 만들던 그런 곳 이었죠.");
            break;
        }
        case 1: {
            qm.sendNextPrev("그 역시 마찬가지에요. 레온... 그는 검술 외에는 모르는 무뚝뚝 한 남자였지만 그래도 다정한 사람이었어요. 말은 없지만 그래도 상냥한 마음이 눈빛으로 드러나는... 그랬던 그가 이렇게까지 변하다니...");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("#r검은 마법사#k가 대체 그에게 무슨 짓을 한 걸까요. 왜 그는 예전과 전혀 다른 사람이 된 걸까요? 그는 저를... 완전히 잊은 걸까요? 모르겠어요. 당신이 저를 대신해 레온을 만나 주시지 않겠어요?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.warp(211070200, "out00");
            qm.forceStartQuest();
            break;
        }
    }
}