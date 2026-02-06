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
            qm.sendNext("지금까지 크로스 헌터의 임무를 훌륭히 수행해 주셔서 감사합니다. 저를 포함한 다른 크로스 헌터 멤버들 역시, #h #님의 활약을 지켜 보았습니다. 당신이야 말로 #b<크로스 헌터 마스터>#k라 할 수 있겠군요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("#h #님에게 #b<크로스 헌터 마스터>#k의 칭호를 수여합니다. 그럼 앞으로의 활약도 기대하겠습니다.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1142354# #z1142354# 1개");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(1142354, 1);
            qm.forceCompleteQuest();
            break;
        }
    }
}