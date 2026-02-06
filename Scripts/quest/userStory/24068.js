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
            if (qm.isQuestActive(24068) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("궁수가 되기 위해 찾아오신 분인가요? 아니, 그렇지는 않은 것 같은데... ...익숙한 얼굴... 당신은... 당신은...!");
            break;
        }
        case 1: {
            qm.sendNextPrev("메르세데스님!");
            break;
        }
        case 2: {
            qm.sendTutorialUI(["UI/tutorial/mersedes/0/0"]);
            break;
        }
        case 3: {
            qm.sendNext("살아 계셨군요! 그럴 거라고 생각했어요! 그럴 거라고 믿었어요! 절대... 절대 왕인 당신이 검은 마법사에게 당해 사라지지 않았을 거라고... 그렇게 믿었어요!\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 2,500 exp");
            break;
        }
        case 4: {
            qm.dispose();
            qm.gainExp(2500);
            qm.forceCompleteQuest();
            break;
        }
    }
}