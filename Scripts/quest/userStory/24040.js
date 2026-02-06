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
            if (qm.isQuestActive(24093) == true) {
                qm.sendNextS("...이게 어떻게 된 일이지? 내 레벨이... 터무니없이 낮잖아?! 겨우 10? 10이라고? 100도 아니고 10? 혹시 내가 아직도 꿈을 꾸고 있는 건가? 꼬집으면 깨어나는 건가? 에잇!", 16);
            } else {
                qm.dispose();
                qm.forceStartQuest();
            }
            break;
        }
        case 1: {
           qm.sendNextPrevS("아얏! 꿈이 아니잖아! 이것도 검은 마법사가 내린 저주의 여파?! ...으으으... 네 이놈, 검은 마법사! 원래도 호감이라고는 슬라임 머리 털 만큼도 없었지만 지금은 정말로 용서 할 수가 없다.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 500 exp", 16);
           break;
        }
        case 2: {
           qm.dispose();
           qm.gainExp(500);
           qm.reservedEffect("Effect/Direction5.img/mersedesQuest/Scene2");
           qm.delayEffect("Effect/OnUserEff.img/questEffect/mercedes/q24040", 0);
           qm.forceCompleteQuest();
           break;
        }
    }
}