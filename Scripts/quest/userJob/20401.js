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
            qm.sendSimple("응? 듀나미스 말이에요? 아아, 알지요 그럼. 스카두르 아저씨하고 죽이 맞아서 사냥 다녀서 당연히 기억하고 있죠. 뭐, 무시 무시한 아저씨라 말 걸긴 어려웠지만 말이에요. 한동안 엘나스에서 뭔가 조사하는 것 같더니 얼마 전부터 안 보이기 시작했네요. 혹시 어디에 있는 건지 알아요?\r\n#L0##b나도 찾고 있는 중이라... 그가 여기에서 뭘 했어?#k");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("마을 안에 있을 때는 별로 없었고, 대체로 #r좀비들만 사냥#k하고 있었죠, 아마? 수련하는 건가 했는데 그건 아니고... 뭔가 찾는 것 같던데... 뭔지는 잘 모르지만... 아무튼 그 아저씨 엄청 세서 깜짝 놀랐어요. 그럼 질문은 이게 다?");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.sendNext("당신도 그하고 같은 곳 소속인 모양이죠? 응? 어떻게 알았냐고요? 글쎄요... 잘은 모르지만 어쩐지 분위기가 비슷하달까. 아무튼 잘해봐요. 듀나미스는 너무 진지해서 오히려 좀 불안한 느낌이었거든요. #r위험한 일#k에 말려들진 않았을지...");
            break;
        }
    }
}