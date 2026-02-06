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
            qm.sendNext("이제 엘프의 세 명의 장로가 모두 모였습니다... 기억하고 계십니까, 메르세데스, 우리의 왕인 분? 세 명의 장로가 모두 모이면 사용 할 수 있는 마법이 하나 있습니다. #b순백의 백화#k라고 불리는...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("순백의 정화라면 분명...", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("원래대로 라면 왕께서 위험한 상황에 빠져 힘을 쓸 수 없을 때 사용하는 마법이지요. 모든 엘프들의 정신 깊은 곳까지 내려가 가장 순수하고 고귀한 마음을 이끌어 낼 수 있습니다. 사용한다면 분명 저주에 빠진 다른 사람들도 깨어 날 겁니다.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("그대들도 저주의 여파 때문에 약해져 있을 텐데... 괜찮겠는가?", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("이 순백의 정화 마법은 엘프들의 정신을 공명으로 연결해 서로가 서로의 힘을 복돋게 만드는 것... 쉬운 마법은 아니지만 의외로 힘의 소모는 적답니다. 지금 당장이라도 사용 할 수 있습니다.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("그 마법에 성공하면... 모두가 깨어나는 건가? 그렇다면 망설일 필요 없겠지. 세 장로요, 저 차가운 얼음 속에서 고통 받는 백성들을 구해다오.", 2);
            break;
        }
        case 6: {
            qm.dispose();
            qm.showFieldEffect(false, "mercedes/elfElder");
            qm.showFieldEffect(false, "mercedes/frame");
            qm.forceCompleteQuest();
            break;
        }
    }
}