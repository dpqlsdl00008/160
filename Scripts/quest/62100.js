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
            qm.sendNext("혹시 낮이 사라지고, 밤이 몇 일이고 지속되고 있는 상해에 대해서 들어본 적이 있나요? 리프레에서 하프링 한 명이 중국 상해로 연락을 가서 실종되는 사건이 벌어졌습니다. 그의 이름은 #b토모#k이죠. 리프레의 마을 사람들 모두가 그의 신변을 걱정하고 있지요...");
            break;
        }
        case 1: {
            qm.askAcceptDecline("혹시 시간이 괜찮으시다면 상해로 가서 실종 된 그를 찾아봐주실 수 있나요?\r\n\r\n#b(수락 시에 상해의 예원 정원으로 이동됩니다.)#k");
            break;
        }
        case 2: {
            qm.dispose();
            qm.warp(701100000, 0);
            qm.forceStartQuest();
            break;
        }
    }
}

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
            qm.sendNext("앗! 모두들 저를 걱정하고 있다고요!? 하지만... 지금은 돌아갈 수 없어요...\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,023,590 exp");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(1023590);
            break;
        }
    }
}