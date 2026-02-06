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
            if (qm.isQuestActive(25416) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNextS("... 역시 함점이었나. 안전 제일 같은 쉬운 몬스터를 퇴치하고 능력이 증명되었다니 처음부터 이상했지. 이렇게 승진이 쉬울 정도로 블랙윙이 허술 한 곳도 아니고 말이야.", 16);
            }
            break;
        }
        case 1: {
            qm.sendNextPrevS("어디서 의심을 받게 된 걸까나? 르티에하고는 거의 대화를 안 했는데... 다른 감시자들에게 의심을 산 걸까? 솔직히 수상하게 보이긴 했을 테니 할 말은 없군.", 16);
            break;
        }
        case 2: {
            qm.sendNextPrevS("뭐, 이 정도 함정으로 괴도 팬텀을 가둘 수는 없지. 크리스탈 가든 같은 최고급 비행선을 괜히 산 게 아니라고. 바로 탈출해 볼까?\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 2,300 exp", 16);
            break;
        }
        case 3: {
            qm.dispose();
            qm.gainExp(2300);
            qm.warp(150000000, "out00");
            qm.forceCompleteQuest();
            break;
        }
    }
}