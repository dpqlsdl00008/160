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
            qm.sendNext("이야... 우리의 히어로, #h0# 오셨군! 하하하, 마을에서 보니 훨씬 멋져 보이는걸? 블랙윙에 점령 당했느니 어쩌니 해도 역시 우리 마을이 최고지. 안그러냐?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("이제 몸은 괜찮은 건가요?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("아직 좀 쑤시긴 하지만 괜찮아. 지그문트의 실력은 최고니까 말이야. 거의 완벽하게 예전의 상태를 회복했지. 다만 문제가 하나 있다면...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("또 무슨 문제 인가요? 혹시 블랙윙이 뭔가 음모를?", 2);
            break;
        }
        case 4: {
            qm.sendYesNo("아니, 이번에 문제는 다름아닌... 너다! 네가 너무 강해지는 바람에 내가 나설 자리가 없어졌다고! 이래봬도 레지스탕스 최고의 와일드 헌터였는데, 이젠 널 가르칠 수조차 없잖아! 이건 말도 안돼! 그런 의미에서 너에게 더 어려운 과제를 내겠다!");
            break;
        }
        case 5: {
            if (qm.isQuestFinished(23053) == false) {
                qm.changeJob(3312);
                qm.forceCompleteQuest();
            }
            qm.sendNext("널 전직시켰어. 동시에 내가 알고 있던, 하지만 나조차 자유 자재로 다루지 못한 최고의 스킬들을 전수했지. 나는 해내지 못한 거지만 아무래도 너라면 할 수 있을 것 같거든. 뭐, 당연하잖아? 네가 우리 레지스탕스 최고의 실력자인데 말이야!");
            break;
        }
        case 6: {
            qm.sendNextPrev("이걸로 내 수업도 마지막...인건 아냐. 이래봬도 아직 나 꽤 유능하다고? 네가 나보다 강해지긴 했지만, 그래도 나에게 배울 게 없지는 않을걸? 그러니.. 다음 수업에 보자. 언제 할 지는 알 수 없는 수업이지만.");
            break;
        }
        case 7: {
            qm.sendNextPrev("그 때 까지 레지스탕스로서 멋지게 활약하길 기대하지");
            break;
        }
        case 8: {
            qm.dispose();
            break;
        }
    }
}