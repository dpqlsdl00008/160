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
            qm.sendNext("이야... 우리의 히어로, #h0# 아냐? 후후, 마을에서 보니 훨씬 더 반가운걸? 블랙윙에게 점령 당한 상태라 할지라도 역시 우리 마을 최고지. 안 그래?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("이제 몸은 괜찮은 건가요?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("응. 지그문트의 실력은 확실하니까. 멀쩡해졌어. 지금은 완벽하게 예전의 상태를 회복했지. 하지만 그래도 문제가 하나 있기는 해...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("또 무슨 문제 인가요? 혹시 블랙윙이 뭔가 음모를?", 2);
            break;
        }
        case 4: {
            qm.sendYesNo("아니, 이번에 문제는 다름아닌... 너야! 네가 너무 강해졌다고. 명색이 스승인 나조차 못한 일을 해내다니. 이래서야 내가 너무 부끄럽잖아? 그런 의미에서 너에게 더 어려운 과제를 부여하겠다!");
            break;
        }
        case 5: {
            if (qm.isQuestFinished(23052) == false) {
                qm.changeJob(3212);
                qm.forceCompleteQuest();
            }
            qm.sendNext("널 전직시켰어. 동시에 내가 알고 있떤, 하지만 완벽히 다루지는 못한 최고의 스킬들을 전수했지. 나는 완성하지 못한 것들이지만 너라면 해낼 수 있을 거라고 믿어. 못할게 뭐가 있겠어? 우리 레지스탕스 최고의 실력자인데 말이야!");
            break;
        }
        case 6: {
            qm.sendNextPrev("이걸로 내 수업도 마지막인가... 아니 그럴리가 없지. 이래봬도 나 꽤 유능한 레지스탕스인걸. 지금은 네가 나보다 강하지만, 그래도 나한테 배울 게 없지는 않을걸? 그러니... 다음 수업에 보자. 언제 할 지는 알 수 없는 수업이지만.");
            break;
        }
        case 7: {
            qm.sendNextPrev("그 때 까지 레지스탕스로서 멋지게 활약하길 기대하지.");
            break;
        }
        case 8: {
            qm.dispose();
            break;
        }
    }
}