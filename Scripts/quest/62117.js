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
            qm.sendNext("으으! 이를 어쩌면 좋지..?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("저기 경관님 무슨 문제라도 있으신가요?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("예원 정원에 문제가 생겨 사람들에게 빨리 가보아야 해요, 하지만 제 자전거 바퀴가 펑크가 나고 말았습니다... 걸어서 가기엔 너무 먼곳이라... 이곳에서 옴짝달싹 못하고 있답니다. 정말 큰일이에요...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#Cgray#(어디보자, 경관님께 남경로의 자전거 유령을 퇴치하고 자전거의 타이어를 구해다 드리면 수리할 수 있지 않을까?)#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrevS("제가 도와드릴께요 경관님! 제가 타이어를 구해다 드리겠습니다!\r\n\r\n#Cgray#(타이어 50개면 수리하기에 충분하겠지?)#k", 2);
            break;
        }
        case 5: {
            qm.sendNextPrev("정말 감사합니다 모험가님! 저는 여기서 기다리고 있겠습니다.");
            break;
        }
        case 6: {
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
            qm.sendNext("타이어를 모두 구해 오셨나요?");
            break;
        }
        case 1: {
            qm.sendNextPrev("정말 감사합니다 #d#h ##k님! 저는 예원 정원의 최고 책임자 입니다. 이 곳의 중요한 사건들은 모두 저를 통해 전달 된답니다. 당신을 만난건 정말 행운이에요. 정말 감사했습니다.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,125,949 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainItem(4009368, -50);
            qm.gainExp(1125949);
            break;
        }
    }
}