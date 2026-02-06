var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 9) {
            qm.sendNext("흠... 마스터는 겸손하구나.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("마스터, 마스터~ 이번 일도 잘 끝냈네? 이걸로 메이플 월드의 사람들을 조금이라도 도울 수 있었을까?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b좀비들을 물리쳤으니 분명 엘나스 지방에 도움이 되었을 거야.#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("헤에, 그렇구나~ 하긴. 몬스터는 많이 퇴치 할 수록 좋은 거니까. 그런데 마지막에 그 검은 열쇠는 뭐였을까?");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b글쎄, 잘은 모르지만, 좋은 단체이니 분명 어딘가 좋은 일에 쓰겠지.#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("그렇겠지? 그런데 이 단체는 왜 이렇게 비밀스럽게 움직이는 거지? 이래서야 좋은 일을 하는 걸 아무도 모르잖아.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b왼 손이 하는 일을 오른 손이 모르게 하라는 말도 있잖아.#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("왼 손, 오른 손? 그게 무슨 말이야. 오른 손은 왕따 시키라는 거야?");
            break;
        }
        case 7: {
            qm.sendNextPrevS("#b...아니 그게 아니라... 자랑하고 싶어서 좋은 일을 하는 건 옳지 않으니까 몰래 하라는 말일 걸?#k", 2);
            break;
        }
        case 8: {
            qm.sendAcceptDecline("헤헤? 난 자랑하고 싶은데, 아무튼 이상해. 너무 비밀스럽고 조심스럽고. 그게 재밌긴 한데 잘 이해를 못하겠어. 마스터는 안 그래?\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 10,000 exp\r\n#fUI/UIWindow.img/QuestIcon/10/0# 2 sp");
            break;
        }
        case 9: {
            qm.dispose();
            qm.gainExp(10000);
            qm.getPlayer().gainSP(2, Packages.constants.GameConstants.getSkillBook(qm.getJob()));
            qm.sendOkS("#b그런 생각이 안 드는 건 아닌데... 분명 뭔가 의미가 있겠지. 어쨌든 좋은 일이니까 말이야. 정 궁금하면 다음에 임무를 받을 때 물어보지 뭐. 어떤 단체냐고 말이야.#k", 2);
            qm.forceCompleteQuest();
            break;
        }
    }
}