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
            qm.sendNext("오, 영우님. 몰라 볼 정도로 레벨이 높아졌군요. 이번 일을 하는데 어려움이 없겠어. 무슨 일이냐고?");
            break;
        }
        case 1: {
            qm.sendNextPrev("영웅님이 수련에 힘 쓰는 동안 리린하고 난 영웅님의 과거와 봉인석에 대해 온갖 방면으로 알아봤지. 그런데 얼마 전에 아주 흥미로운 정보가 하나 들어왔어. 혹시 아이들을 위한 장난감의 마을, 루디브리엄을 알아?");
            break;
        }
        case 2: {
            qm.sendNextPrev("루디브리엄에는 시간을 관리하는 두 개의 시계탑이 있어. 각 탑에서 시간을 관리해서, 최종적으로 루디브리엄의 시간은 멈춰 있을 수 있도록 하고 있지. 아이들이 자라버리면 장난감은 필요 없어지니까 항상 시간을 멈춰 놓는다고 하더군.");
            break;
        }
        case 3: {
            qm.sendAcceptDecline("그런데 두 개의 시계탑 중 한 곳이 어떤 이유에서인지 망가진 모양이야. 덕분에 #b루디브리엄에서 관리하는 시간에 구멍이 뚫려서 과거로 갈 수 있게 되었다#k더군... 재미있는 건 여기부터야.");
            break;
        }
        case 4: {
            qm.sendNext("과거에 다녀온 사람의 정보를 종합해 본 결과, 리린은 그 시대가 #b영웅님이 살던 시대와 비슷#k하다고 결론 내렸어. 복식, 물건, 그리고 상황까지... 그렇다면 그곳에 가면 봉인석에 대한 정보를 얻을 수 있지 않겠어? 그렇지?");
            break;
        }
        case 5: {
            qm.sendNextPrev("사실 봉인석은 아무래도 좋아. 그보다 더 중요한 건, 그곳에 가면 어쩌면 영웅님을 아는 사람을 만날 수 있을지도 모른다는 거지.");
            break;
        }
        case 6: {
            qm.sendNextPrev("망가진 시계탑은 #b오른쪽에 있는 시계탑#k... 즉, 핼리오스탑이야. #b분홍색 토끼 머리 모양 건물#k 안에 시간 관리 장치가 있는데, 그리로 들어가려면 #b핼리오스탑 최상층에서 사다리를 타고 위쪽으로 올라가#k면 된다고 해. 거길 통과해서 과거로 갈 수 있을거야.");
            break;
        }
        case 7: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}