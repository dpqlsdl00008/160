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
            qm.sendNext("그냥 돌아가게! 난 이 곳에서 한 발짝도 떼지 않을걸세!");
            break;
        }
        case 1: {
            qm.sendNextPrev("... 그래도 가끔은 젠 황이 보고 싶더군...");
            break;
        }
        case 2: {
            qm.askAcceptDecline("온 김에 내 부탁을 하나 들어주겠나? #d염소 우유#k를 좀 가져다 주게.");
            break;
        }
        case 3: {
            qm.sendNext("근처에 염소와 흑염소를 잡고 염소 우유를 가져와주게.");
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
            qm.sendNext("음 이거야 이거!");
            break;
        }
        case 1: {
            qm.sendNextPrev("크으~ 역시 이렇게 신선한 우유는 어디에도 없다니깐!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,364,787 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainItem(4009362, -1);
            qm.gainExp(1364787);
            break;
        }
    }
}