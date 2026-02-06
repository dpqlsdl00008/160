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
            qm.sendNext("아직 완전히 끝난 것이 아니에요. #b#h0##k님. 성 구석 구석에 아직 #b총리 대신#k의 전당이 남아 있어요.");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("잔당들이 숨어 있을 만한 장소는 #b보안실#k이라는 곳인데 #b마천루 3#k에서 제가 드린 #i4032405# #b밀실 열쇠#k를 사용해 들어가실 수 있어요. 그리고 남은 잔당들은 #b마천루#k 곳 곳에서 발견 하실 수 있을 테니 꼭 처리해주세요.");
            break;
        }
        case 2: {
            qm.sendNext("그럼 마지막으로 부탁 드릴께요.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.gainItem(4032405, 1);
            qm.forceStartQuest();
            break;
        }
    }
}