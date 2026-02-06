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
            qm.askAcceptDecline("저는 상해에서 유명한 기자 입니다. 상해의 독자님들도 모두 제가 쓰는 글을 좋아하시죠. 하지만 저는 자료를 모으는 데에는 그다지 소질이 없습니다... 바쁘시지 않다면 예원 정원에서 일어나는 강시 사태의 진상 파악을 도와주시지 않겠습니까?");
            break;
        }
        case 1: {
            qm.sendNext("정말요? 우후!! 그럼 시작해 볼까요?");
            break;
        }
        case 2: {
            qm.sendNextPrev("먼저 상해 외곽을 조사해야 합니다. 그곳에서 부터 악몽이 시작되었죠...");
            break;
        }
        case 3: {
            qm.sendNextPrev("제가 듣기로는, 강시가 나타나기 전부터 상해 외곽의 가축들이 폭주하기 시작했기 때문에 #d닭#k, #d양#k 그리고 #d소#k의 모든 가축들의 #r세포 샘플#k을 분석하는 것에서 부터 시작하도록 하죠.");
            break;
        }
        case 4: {
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
            qm.sendNext("동물을의 세포 샘플을 구해 오셨군요!");
            break;
        }
        case 1: {
            qm.sendNextPrev("먼저 기사에 실릴 사진을 좀 찍겠습니다! 잠시만 기다려주세요!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,367,787 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(1364787);
            break;
        }
    }
}