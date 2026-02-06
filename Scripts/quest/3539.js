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
            qm.sendNext("무슨 일이에요, 아란? 뭔가 기억나는 거라도 있나요?");
            break;
        }
        case 1: {
            qm.sendNextPrev("추억? 아란과의 추억이라... 그거라면 많죠. 기억을 잃었지만 그래도 영웅으로서의 풍모를 잃지 않는 아란이 점점 원래의 능력을 찾아가는 모습을 보던 그 모든 과정이 추억인 걸요.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}