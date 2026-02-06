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
            qm.sendNext("이야... 우리들의 히어로, #h0#님 아니십니까. 후후, 마을에서 뵙게 되니 유난히 반갑군요. 비록 자유와 평화는 빼앗기고 말았지만 그래도 우리 마을이 가장 좋은 곳 이에요.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("이제 몸은 괜찮은 건가요?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("네. 지그문트의 약이 잘 들은 덕분에 이제는 편안해졌답니다. 거의 완벽하게 예전의 실력을 되찾았어요. 다만 큰 문제가 하나 남았습니다...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("또 무슨 문제 인가요? 혹시 블랙윙이 뭔가 음모를?", 2);
            break;
        }
        case 4: {
            qm.sendYesNo("아니, 이번에 발생한 문제는... 당신입니다! 당신께서 너무 강해지는 바람에 제 자리가 약해졌단 말입니다. 이래봬도 레지스탕스의 메카닉을 가르치는 선생이었는데. 제자보다 약하다니 원... 그런 의미에서 당신께 더 어려운 과제를 부여하겠습니다.");
            break;
        }
        case 5: {
            if (qm.isQuestFinished(23054) == false) {
                qm.changeJob(3512);
                qm.forceCompleteQuest();
            }
            qm.sendNext("당신을 전직시켰습니다. 동시에 제가 만들 수 있는 최고의 로봇들을 드렸습니다. 저로서도 다 통제할 수 없어 제대로 다루지 못하고 있었지만... 아무래도 당신이라면 어쩐지 해낼 것 만 같군요. 레지스탕스 최고의 메카닉으로서 말입니다.");
            break;
        }
        case 6: {
            qm.sendNextPrev("이걸로 제 수업도 끝...에 가까워졌군요. 하지만 아직 끝은 아닙니다. 당신이 저보다 강해진 건 사실이지만, 그래도 아직 제게 배울 게 없지는 않을 테니까요. 그러니 다음 수업에 뵙시다. 언제 시작 될 지 알 수 없는 수업이지만...");
            break;
        }
        case 7: {
            qm.sendNextPrev("그 때 까지 레지스탕스로서 멋지게 활약하길 기대하겠습니다.");
            break;
        }
        case 8: {
            qm.dispose();
            break;
        }
    }
}