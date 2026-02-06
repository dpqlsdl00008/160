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
            qm.sendNext("#Cgray#(찰칵)#k\r\n\r\n좋아! 이거면 되겠군...");
            break;
        }
        case 1: {
            qm.askAcceptDecline("하지만 저는 전문가가 아니라서 자세히 분석할 수는 없어요, 이 샘플을 가지고 #d젠싱 왕#k 선생님을 찾아가보시겠어요? 그는 예원 정원의 유일한 의사에요. 그에게 이것들에 대한 분석 보고서를 받아 와주세요.");
            break;
        }
        case 2: {
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
            qm.sendNext("#Cgray#(쿨럭, 쿨럭)#k\r\n\r\n에...? 자네... 어디 아픈가? 아파 보이진 않네만...");
            break;
        }
        case 1: {
            qm.sendNextPrev("음? 세포 샘플에 대해 분석을 해 달라는겐가? ... 해보겠네만 눈이 침침해서 원... 잠시만 기다려 주시게.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 852,991 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainItem(4034644, -1);
            qm.gainItem(4034645, -1);
            qm.gainItem(4034646, -1);
            qm.gainExp(852991);
            break;
        }
    }
}