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
            qm.sendNext("엇! #d천마 강시#k를 물리친 영웅 아니십니까? 정말 인상 깊었습니다!");
            break;
        }
        case 1: {
            qm.sendNextPrev("저는 옷을 디자인 하는 #d두상 왕#k 이라고합니다.  저는 패션에 죽고 패션에 살죠! 네? 저를 닮은 누군가를 본 적이 있으시다구요? 하하! 누군진 모르겠지만 정말 축복 받은 사람이네요! 이제 메이플 월드에 가장 잘생긴 사람이 두 명이라는 거겠죠?");
            break;
        }
        case 2: {
            qm.sendNextPrev("#i4009375# #i4009376#\r\n\r\n어쨋든 천마 강시를 퇴치하고 얻은 #d#z4009375##k과 #d#z4009376##k을 버리신 건 아니겠죠?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}