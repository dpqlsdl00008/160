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
            if (qm.isQuestActive(23263) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("역시 구해 주실 줄 알았어요!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b딱히 구할 생각은 없었는데, 어쩌다 얻게 되었을 뿐 입니다.#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("어쨌든 감사합니다. 이제 모습을 되 찾을 수 있을 거에요. 이제 이걸 땋기만 하면 되는데... ...아앗 이럴 수가!!!");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b!? 마스테마, 무슨 일 입니까?#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("지금 모습으로는, 고양이의 발로는 매듭을 만들 수 없다는 걸 깜빡했어요! 어, 어쩌죠? #h #님! 한 번만 더 절 도와 주시면 안 될까요?");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b... ... (마스테마가 애절하게 나를 바라본다. 하는 수 없이 내가 매듭을 땋아 줘야 할 것 같다.)#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("정말 감사합니다. #h #님!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 52,753 exp");
            break;
        }
        case 7: {
            qm.dispose();
            qm.gainExp(52753);
            qm.forceCompleteQuest();
            break;
        }
    }
}