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
            qm.askAcceptDecline("어쩐지 수상한 기운을 풀풀 풍기는 곳이 있습니다. 조사해 보시겠습니까?");
            break;
        }
        case 1: {
            qm.sendNextS("#b(한 더미의 의상들을 찾았다. 일상복처럼 생기진 않은데... 한 번 펼쳐볼까?)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrevS("#i1052196##i1050168##i1052496#\r\n\r\n역시... 이 옷들은 연극을 위해 특별히 만들어진 옷처럼 보인다. 옷들을 가지고 쿠디에게 돌아가 이야기를 나눠보자.", 2);
            break;
        }
        case 3: {
            qm.dispose();
            qm.gainItem(4033829, 1);
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
            qm.sendNext("여자 아이들이 몰래 만들고 있던 게 바로 옷이었구나! 아이들은 검은 마법사를 봉인한 다섯 영웅들에 대한 연극을 준비하고 있었던 거야, 그것도 아주 은밀하게 몰래 말이야. 이것이 무엇을 뜻할까?");
            break;
        }
        case 1: {
            qm.sendNextPrev("우리가 알 수 있는 건 여기까지야. 1층으로 돌아가서 이야기를 나눠보자.\r\n\r\n#b(엘리넬 1층으로 가자.)#k");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(4033829, -1);
            qm.forceCompleteQuest(32114);
            qm.forceCompleteQuest();
            break;
        }
    }
}