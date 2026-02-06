var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            qm.sendAcceptDecline("링크 스킬 #e#b데몬스 퓨리#k#n를 계정 내의 다른 캐릭터에게 전수 할 수 있습니다. 지금 전수 받을 캐릭터를 지정 하시겠습니까?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.teachSkill(30010112, 1, 1);
            qm.forceCompleteQuest();
            break;
        }
    }
}