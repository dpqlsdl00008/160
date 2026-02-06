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
            qm.sendNext("혹시 최근에 나타난 #d천마 강시#k라는 녀석에 대한 이야기를 들어보셨습니까?");
            break;
        }
        case 1: {
            qm.sendNextPrev("천마 강시는 일반 강시보다 수 배는 거대한 강시로 인간의 언어까지 흉내 낼 수 있으며, 도깨비 불을 소환하거나 회오리 바람을 일으키는 무술에도 능해 옛날 무술가의 유해에서 만들어진 강시로 추측됩니다...");
            break;
        }
        case 2: {
            qm.sendNextPrev("이렇게 강력한 천마 강시가 구곡교를 넘어 예원을 습격한다면 저희의 힘으로는 사실 막을 수 있을지 불투명 한 상황입니다...");
            break;
        }
        case 3: {
            qm.askAcceptDecline("혹시... 모험가님께서 도와 주시겠습니까?");
            break;
        }
        case 4: {
            qm.sendNext("도와주실 줄 알았습니다! #r천마 강시#k라는 녀석은 #r천마 강시 소굴#k이라는 곳에서 나타납니다. #r5마리#k 정도를 퇴치해 주신다면 마을 주민들이 안심 할 것 같습니다!");
            break;
        }
        case 5: {
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
            qm.sendNext("이럴 수가! 정말 천마 강시를 다섯 마리나 무찌르신겁니까?");
            break;
        }
        case 1: {
            qm.sendNextPrev("이건 예원 정원의 모든 사람에게 알려야 해요!! 정말이지 모험가님 덕분에 주민들은 안심하고 지낼 수 있을 겁니다!");
            break;
        }
        case 2: {
            qm.sendNextPrev("도와주셔서 정말 감사합니다... 당신은 정말 이 마을의 영웅이에요! 이 은혜는 꼭 잊지 않겠습니다!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 2,729,574 exp");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(2729574);
            break;
        }
    }
}