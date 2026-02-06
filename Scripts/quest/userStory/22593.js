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
            qm.sendAcceptDecline("음? 저한테 무슨 볼 일이라도 있으신가요? 그런 비장한 얼굴로... 네? 혹시 오르비스의 식물들이 빨리 자라는 일이 있었냐고요? 어머. 어떻게 아셨죠? 혹시 어디서 이 이야기에 관해 들으신 건가요?\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 23,000 exp");
            break;
        }
        case 1: {
            qm.sendNext("네. 있었죠. 있었고 말고요. #b갑자기 커다래진 네펜데스들 때문에 엄청 곤란했었답니다.#k 다행히 지나가던 모험가께서 시간에 대해 조사해 주시고 해결도 해주셔서 이제는 문제 없지만요.");
            break;
        }
        case 2: {
            qm.sendNextPrev("그런데 표정이 왜 그러세요? 이 일은 이미 해결 된 거니 걱정하실 필요 없는데...");
            break;
        }
        case 3: {
            qm.dispose();
            qm.gainExp(23000);
            qm.forceCompleteQuest();
            break;
        }
    }
}