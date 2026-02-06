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
            qm.sendNext("안녕하세요. 루디브리엄의 경비병 마르쉘입니다. 뭔가 도와드릴 게 있나요? 네? 도어락이요?!");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("쉿! 도어락에 대해서는 어떻게 아시는 거죠? 비밀 창고를 지키는 비밀 문지기라 아는 사람이 거의 없는데... 흠흠. 어쨌든 도어락의 근황을 알려드릴게요.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 23,000 exp");
            break;
        }
        case 2: {
            qm.sendNext("얼마 전, 도어락이 누군가에게 습격을 당해 큰 부상을 당했어요. #r덕분에 비밀 창고를 지키는 사람이 아무도 없어, 그 틈을 타 도둑이 들고 말았지 뭐예요.#k 어떤 보물인지는 알려지지 않았지만... 아무튼 큰일이에요.");
            break;
        }
        case 3: {
            qm.sendNextPrev("...헉. 왜, 왜 그렇게 무서운 표정을 지으시는 거죠? 도둑에 화내주시는 건 감사하지만, 이 일은 비밀이니 주의해주세요!");
            break;
        }
        case 4: {
            qm.dispose();
            qm.gainExp(23000);
            qm.forceCompleteQuest();
            break;
        }
    }
}