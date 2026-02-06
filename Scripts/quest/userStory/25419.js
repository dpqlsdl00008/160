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
            qm.sendNext("빅토리아 아일랜드의 봉인석을 찾은 이후, 나름대로 다른 지역의 봉인석에 대한 정보를 모아 온 건 알고 있지? 마오에게도 몇 번 말해 줬으니까 말이야.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("안타깝게도 봉인석은 거의 빼앗겼다는 이야기를 들었지. 아란 녀석, 기억을 잃더니 기합이 빠졌나? 그래도 되게 성실한 녀석인데.", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("뭐... 아란님 잘못은 아니라고 봐. 솔직히 에레브 봉인석 외에 다른 봉인석들을 지키는 건 거의 무리였으니까. 빅토리아 아일랜드 봉인석이나마 남아서 다행인 거라고 생각해.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("정보 상인이 그렇게 결론은 내렸다면 그 말이 맞는 거겠지. 전에도 말했지만, 사실 검은 마법사만 깨어나지 않는다면 봉인석은 없어도 상관 없는 물건이야. 너무 집착 할 필요 없어.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("그다지 집착하는 건 아니고. 어쨌든 그렇게 됬으니 대신 빅토리아 아일랜드에서 만났던 #r블랙윙의 멤버#k를 계속 추척해 왔는데...");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#r인형사 프란시스#k 말인가?", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("응. 몇 번에 걸쳐 아지트를 옮겨서 종적을 찾기 힘들었는데... 얼마 전 스톤 골렘의 사원 쪽에서 인형사의 아지트로 보이는 곳이 발견되었어.");
            break;
        }
        case 7: {
            qm.sendNextPrevS("그거 좋은 정보인데...? 마침 필요하던 거야.", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("필요한 정보라니 다행이군. 사실 인형사는 이미 블랙윙에서는 다 쓰고 버리는 패라고 여겼거든. 주시 할 필요가 있을까 걱정했는데 도움이 되다니 기쁜 걸? 정보는 여기까지야. 혹시 나중에 또 흥미로운 정보가 생기면 연락할게.");
            break;
        }
        case 9: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}