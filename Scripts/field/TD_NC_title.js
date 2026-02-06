var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (cm.isQuestActive(3762) == true) {
                cm.lockInGameUI(true);
                cm.showFieldEffect(false, "temaD/enter/teraForest");
                cm.sendNextS("... 여긴 어디지?", 3);
                return;
            }
            if (cm.isQuestActive(3766) == true) {
                if (cm.isQuestFinished(3773) == false) {
                    if (cm.getQuestCustomData(3755) == 1) {
                        cm.forceStartQuest(3773);
                        cm.sendNextS("..... 그건 도저히 이길 수 있는 상대가 아니었어. 저런 몬스터가 있다는 말은 들어본 적이 없는데... 앤디가 알고 있는 몬스터일까?", 2);
                    }
                }
            }
            cm.dispose();
            var cMap = parseInt((cm.getMapId() / 100) % 10);
            switch (cMap) {
                case 0: {
                    cm.showFieldEffect(false, "temaD/enter/teraForest");
                    break;
                }
                default: {
                    cm.showFieldEffect(false, "temaD/enter/neoCity" + cMap);
                    break;
                }
            }
            break;
        }
        case 1: {
            cm.sendNextPrevS("지금 손에 든 건 내 시계... 그걸 작동시켜서 이 곳에 온건가?", 3);
            break;
        }
        case 2: {
            cm.sendNextPrevS("당신은 아까의 그 남자로군요. 이게 어떻게 된 일이죠?", 3);
            break;
        }
        case 3: {
            cm.sendNextPrevS("자네도 이미 개입되어버린 건가. 궁금하다면 내게 말을 걸게. 설명하자면 정말 긴 이야기가 될테지만. 최대한 간단히 설명 해줄테니.", 1, 0, 2082004);
            break;
        }
        case 4: {
            cm.dispose();
            cm.lockInGameUI(false);
            cm.forceCompleteQuest(3762);
            break;
        }
    }
}
