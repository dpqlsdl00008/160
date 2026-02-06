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
            qm.sendAcceptDecline("장미 정원에서 레온과 저의 추억이 담긴 플라워 북을 찾아 내었답니다. 둘 다 손 재주가 없어서 예쁘게 만들 지는 못했지만 이 걸 만들 때는 참 즐거웠어요... 자, 이걸 어서 레온에게 전해 주세요.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i4032837# #b#z4032837# 1개#k");
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainItem(4032837, 1);
            qm.forceStartQuest();
            break;
        }
    }
}