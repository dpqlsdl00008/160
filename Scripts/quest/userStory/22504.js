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
            qm.sendNext("히잉, 아무래도 안 되겠어. 다른 게 필요해. 풀도 고기도 아닌 다른거... 뭐 없어? 마스터는 나보다 나이가 많으니 잘 알 거 아니야?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b그, 그렇게 말해도 나도 잘 모르겠는데... 나이가 많다고 다 아는 건 아닌걸...#k", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("그래도 나이가 많으면 세상을 조금이라도 더 많이 경험해 봤으니 지식도 더 많은 게 당연하잖아. 아, 그렇지! 마스터보다 더 나이 많은 사람에게 물어보면 알지도 몰라!");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            qm.sendOkS("#b(아빠한테는 이미 한 번 물어봤는데... 그래도 혹시 모르니 다시 한번 아빠에게 물어보자.)#k", 2);
            break;
        }
    }
}