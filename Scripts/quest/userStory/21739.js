var status = -1;

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
            if (qm.isQuestActive(21739) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendYesNo("침입자는 막아내었나? 하지만 그런 것 치고는 표정이 좋지 않은데... 뭐? 봉인석을 빼앗겼다고?");
            }
            break;
        }
        case 1: {
            qm.sendNext("그런가. 봉인석을 빼앗겼는가... 그렇다면 어쩔 수 없지. 봉인석이 어떤 역활을 하는 것인지는 나 역시도 알지 못해. 사실 그것이 없어졌다고 해서 #b당장 오르비스에 직접적인 피해가 오지는 않을 거야.#k 그것만은 확실해.");
            break;
        }
        case 2: {
            qm.sendNextPrev("하지만 무언가 거대한 일의 전초가 될 것 같군. 점도 아니고 예언도 아닌, 그저 감일 뿐이지만... 행운을 빌지. 당신에게는 아주 많은 행운이 필요할 거야");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(오르비스 봉인석을 빼앗긴 채이다... 어떻게 하지? 트루와 상담해 보자.)#k", 2);
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}