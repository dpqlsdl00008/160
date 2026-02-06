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
            qm.sendNext("붐 샤카 라카 움파 룸파!!! 아브라 카다브라~");
            break;
        }
        case 1: {
            qm.askAcceptDecline("#i2270043# #d#z2270043##k\r\n\r\n좋습니다! 이제 다시 강시에게 돌아가 제가 만든 이 부적을 시험해 봐 주실 수 있습니까?");
            break;
        }
        case 2: {
            qm.sendNext("이번에야 말로 강시들이 비명을 지르며 달아날 것 입니다! 하하하!");
            break;
        }
        case 3: {
            qm.dispose();
            if (qm.getInventory(2).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceStartQuest();
            qm.gainItem(2270043, 10);
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
            qm.sendNextS("...", 2);
            break;
        }
        case 1: {
            qm.sendNextPrevS(".........", 2);
            break;
        }
        case 2: {
            qm.sendNextPrevS("#Cgray#(3시간이 흘렀다.)#k", 2);
            break;
        }
        case 3: {
            qm.sendNextPrevS("저기...", 2);
            break;
        }
        case 4: {
            qm.sendNextPrevS("음?", 4, 9310537);
            break;
        }
        case 5: {
            qm.sendNextPrevS("혹시 여기 있던 도사님 어디 가셨는지 본 적 있으신가요?", 2);
            break;
        }
        case 6: {
            qm.sendNextPrevS("여기서 항상 서 있던 노숙자 말인가?", 4, 9310537);
            break;
        }
        case 7: {
            qm.sendNextPrevS("헉...\r\n\r\n#Cgray#(아무래도 사기를 당한 것 같다.)#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,364,787 exp", 2);
            break;
        }
        case 8: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.removeAll(2270043);
            qm.removeAll(4034272);
            qm.gainExp(1364787);
            break;
        }
    }
}