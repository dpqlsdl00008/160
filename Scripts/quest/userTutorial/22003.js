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
            qm.sendAcceptDecline("농장에 일하러 가신 #b아빠#k가 그만 도시락을 깜빡 잊고 가셨지 뭐니. 네가 #b농장 중심지#k에 계신 아빠에게 #b도시락을 배달#k해주렴. 착하지?");
            break;
        }
        case 1: {
            qm.sendNext("후훗, 역시 에반은 착한 아이라니까~ 그럼 바로 #b집 밖으로 나가 왼쪽으로 쭈욱#k 가렴. 아마 무척 배가 고프실테니 서둘러주면 좋겠구나.");
            break;
        }
        case 2: {
            qm.sendNextPrev("혹시 도시락을 잃어버리면 집으로 곧장 돌아오렴. 다시 싸 줄 테니까.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.gainItem(4032448, 1);
            qm.forceStartQuest();
            break;
        }
    }
}