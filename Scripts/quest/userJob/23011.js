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
            if (qm.isQuestActive(23011) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendYesNo("배틀 메이지가 되기로 결심한 건가? 아직 선택을 번복할 기회는 있어. 대화를 멈추고 퀘스트를 포기한 후 다른 녀석에게 말을 걸면 돼. 잘 생각해봐. 정말 배틀 메이지를 택하겠어? 이 직업이 네 레지스탕스의 길에 어울린다고 생각해?");
            }
            break;
        }
        case 1: {
            if (qm.isQuestFinished(23011) == false) {
                qm.changeJob(3200);
                qm.getPlayer().resetStats(4, 4, 4, 4);
                qm.gainItem(1382100, 1);
                qm.gainItem(1142242, 1);
		qm.gainItem(2431876, 1);
                qm.gainItem(2431877, 1);
                qm.forceCompleteQuest();
            }
            qm.sendNext("좋아! 정식 레지스탕스가 된 걸 환영하지. 지금부터 너는 배틀 메이지다. 싸우는 마법사로서 광기를 품고 누고보다 앞에 나가서 적과 맞서자고.");
            break;
        }
        case 2: {
            qm.sendNextPrev("대놓고 배틀 메이지라고 말하고 다니면 곤란하겠지? 블랙윙에게 걸리기라도 하면 시끄러워질 거야. 그러니 직므부터는 날 담임이라고 불러. 넌 특별수업 때문에 이 교실에 오는 거야. 후후... 내가 이 특별수업의 담임이 되어 널 잘 이끌어주겠어.");
            break;
        }
        case 3: {
            qm.dispose();
            break;
        }
    }
}