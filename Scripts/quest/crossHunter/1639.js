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
            if (qm.isQuestActive(1639) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("퍼즐 조각이 조금씩 맞춰져 가는 것 같습니다. 시간의 신전에 모습을 드러 낸 아카이럼, 그리고 시간의 균열... 비슷한 시기에 나타난 미스틱 게이트... 이 모든 것이 다 관련이 있는 것 같습니다. 아카이럼이 노리는 것이 대체 무엇일까요... 아무래도 시간의 신전으로 직접 가서 확인해 봐야 할 것 같습니다.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 164200 exp");
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainExp(164200);
            qm.forceCompleteQuest();
            break;
        }
    }
}