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
            qm.sendNext("#d#h ##k님 반가워요! 잘 지내셨나요? 저는 바쁘고 바쁘고 또 바빴답니다...");
            break;
        }
        case 1: {
            qm.sendNextPrev("수 없이 늘어나는 유령들과 강시들을 도저히 막아낼 수가 없네요...");
            break;
        }
        case 2: {
            qm.askAcceptDecline("지난 번에 #d#h ##k님께서 정말 잘 해내주셨는데, 혹시 저와 함께 일해보시지 않겠습니까?");
            break;
        }
        case 3: {
            qm.sendNextPrev("좋아요! 남경로를 넘어 가면 강시를 찾을 수 있습니다. 그 곳에서 #r창백한 강시 100마리#k, #r블러드 강시 100마리#k, 그리고 #r포이즌 강시 100마리#k를 퇴치하고 돌아와 주세요!");
            break;
        }
        case 4: {
            qm.dispose();
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
            qm.sendNext("와, 벌써 돌아오셨다니 대단한데요?");
            break;
        }
        case 1: {
            qm.sendNextPrev("다음번에도 저를 도와주실거죠?\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 2,388,377 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(2388377);
            break;
        }
    }
}