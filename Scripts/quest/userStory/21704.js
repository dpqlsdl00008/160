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
            qm.sendNext("수련은 잘 하고 돌아오셨나요? 펭귄 스승 푸오가 과장이 심한 성격인 데다가, 약간 치매가 오늘 게 아닐까 하고 있긴 하지만... 그가 영웅의 스킬에 대해 오랫동안 연구했던 것은 사실이에요. 분명 당신에게 도움이 되었을거에요.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(당신은 콤보 어빌리티를 기억해낸걸 리린에게 말합니다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("사실#p1202006# 수련방식이 훌륭했다기 보다는 역시 당신의 몸이 과거의 몸을 기억하고 있기 때문이겠지만요. 얼음 속에 잠들어 있는 동안 약해져 금방 깨어나지 않을 뿐. 계속해서 수련하다 보면 점점 더 많은 능력을 깨울 수 있을거예요!\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 500 exp");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(500);
            break;
        }
    }
}