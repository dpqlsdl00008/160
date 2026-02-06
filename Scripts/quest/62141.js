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
            qm.sendNext("오오오오!!!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("...? 이 소리는 뭐지", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("저는 주 원장이 응천부를 도읍으로 삼아 명을 건국했을 때부터 살아온 도사입니다!!");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#Cgray#(응천부...? 명을 건국...? 무슨 말 이지?)#k", 2);
            break;
        }
        case 4: {
            qm.askAcceptDecline("아아~ 당신에게서 영웅의 상이 보이는군요!! 당신은 예원 정원의 영웅이 될 것임이 틀림없습니다!! 저와 함께 강시를 퇴치해 보겠습니까?");
            break;
        }
        case 5: {
            qm.sendNext("이렇게 결단력까지 좋을 수가!!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 170,598 exp");
            break;
        }
        case 6: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(170598);
            break;
        }
    }
}