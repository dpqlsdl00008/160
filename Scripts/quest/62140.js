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
            qm.sendNext("제가 당신이 잡아온 강시를 왕 선생님께 전달 해놓았습니다. 하지만 결과가 나오려면 조금 걸릴 것 같네요.");
            break;
        }
        case 1: {
            qm.sendNext("이건 제 수사의 시작에 불과해요! #d#h ##k님이 도와주신 정보를 좀더 연구해서 조만간 멋진 기사를 내도록 하겠습니다! 도와주셔서 정말 감사했습니다!!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 170,596 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(170596);
            break;
        }
    }
}