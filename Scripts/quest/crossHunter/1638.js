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
            if (qm.isQuestActive(1638) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("아카이럼이 돌아온 것 같아! 시간의 신전에서 그를 목격했데. 지금은 어디로 갔는지 알 수 없지만...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("아카이럼? 그가 대체 누구죠?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrevS("아카이럼... 그는 검은 마법사의 군단장 중 한 명으로 검은 마법사가 봉인 된 후 모습을 감췄다고 전해지고 있습니다.", 4, 9073011);
            break;
        }
        case 3: {
            qm.sendNextPrevS("군단장이요? 그럼 그가 나타났다는 것은 검은 마법사가 다시 나타났다는 건가요?", 2);
            break;
        }
        case 4: {
            qm.sendNextPrevS("그 것은 아직 알 수 없군요. 어쨌든 심상치 않은 일이 벌어지고 있는 것은 분명하군요. 다시 나타난 아카이럼과 미스틱 게이트... 뭔가 관련이 있는 것이 분명합니다. 검은 마법사의 책사 역할을 했었던 아카이럼이라면, 미스틱 게이트의 목적이 단순히 메이플 월드를 혼란에 빠뜨리는 것 만은 아닐 것 같군요.", 4, 9073011);
            break;
        }
        case 5: {
            qm.dispose();
            qm.showFieldEffect(false, "crossHunter/chapter/end2");
            qm.forceCompleteQuest();
            break;
        }
    }
}