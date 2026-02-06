var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            status = 2;
            qm.sendNextS("마스테마. 안 그래도 마침 당신에게 말을 걸까 생각 중이었습니다.", 2);
            return;
        }
        if (status == 2) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendYesNo("#h #님. 수행은 잘 돼 가십니까?");
            break;
        }
        case 1: {
            qm.dispose();
            qm.sendNext("아닌 것 같은데요? 정말 잘 되가시는 건가요?");
            break;
        }
        case 2: {
            qm.sendNextS("마스테마. 안 그래도 마침 당신에게 말을 걸까 생각 중이었습니다.", 2);
            break;
        }
        case 3: {
            qm.sendNextPrev("(헉! #h #님이 나에게!?) 네? 무슨 일이신가요??)");
            break;
        }
        case 4: {
            qm.sendNextPrevS("당신과 했던 지난 번의 수련을 다시 했으면 합니다.", 2);
            break;
        }
        case 5: {
            qm.sendNextPrev("지난 번의 수련요? 흠... 보아 하니 또 벽에 부딪히셨군요. 하지만 지금의 #h #님의 수준으로는 그 수련은 도움이 안 될 것 같습니다...");
            break;
        }
        case 6: {
            qm.sendNextPrevS("할 수 없군요. 알겠습니다.", 2);
            break;
        }
        case 7: {
            qm.sendNextPrev("잠시만... 잠시만요. 그렇다고 바로 가시려고 하다니... 전에 생각 난 좋은 수련 방법을 말씀 드리겠습니다. 사실 #h #님의 수련에 가장 도움이 되는 것은 과거의 #h #님과 싸워 보는 것 입니다. 그래서 생각 한 방법으로, 과거로 돌아가서 과거의 #h #님과 겨뤄 보는 것이 어떨까 싶습니다.");
            break;
        }
        case 8: {
            qm.sendNextPrev("시간의 흐름을 되 돌리려면 시간의 신전에 가야 할 듯 하지만 지금의 #h #님이 가시기엔 위험합니다. 그래서 찾은 장소인데, 루디브리엄이란 곳은 시간의 흐름이 이상하다고 합니다.");
            break;
        }
        case 9: {
            qm.sendNextPrev("루디브리엄의 시계탑 최하층에 #b시간의 통로#k란 곳이 있는데, 그 곳에서 시간의 틈으로 들어 갈 수 있을 거에요. 그 곳으로 오시면 제가 시간의 틈을 열어 보도록 하겠습니다.");
            break;
        }
        case 10: {
            qm.sendNextPrev("제가 비록 이런 소환수의 모습이지만 이런 모습이기 떄문에 다른 차원을 통한 공간 이동을 할 수 있습니다. 저는 갈 수 있는 통로로 이동 할 터이니 그 곳에서 뵙겠습니다.");
            break;
        }
        case 11: {
            qm.sendNextPrev("시간의 틈으로 들어가는 것이기 때문에, 과거의 어떤 시점으로 가겠지만, 과거의 일을 바꾼다고 해서 현재나 미래에 영향을 주지는 않을 거에요. 안심하시고 과거의 자신과 싸우시길...");
            break;
        }
        case 12: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(220050300, "in00");
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
            qm.sendNext("#h #님 돌아오셨군요!! 상태는 어떠신가요?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("과거의 자신과 싸우는 일은 보통은 아니었습니다. 과거의 자신과 붙어 보니 잊었던 스킬들을 다시 기억해 낼 수 있을 것 같습니다.", 2);
            break;
        }
        case 2: {
            qm.sendYesNo("성공하셨군요. 강하시던 시절의 #h #님과 비슷한 기운이 느껴집니다. 그럼 잊지 않으시도록 각인 시켜 드릴까요?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.changeJob(3112);
            qm.sendNext("이제 과거의 힘을 거의 다 되 찾으신 것 같습니다. 이제부터는 열심히 #h #님 스스로 연마하시는 일만 남았군요. 저도 어서 과거의 모습으로 되 돌아가고 싶습니다.");
            break;
        }
    }
}