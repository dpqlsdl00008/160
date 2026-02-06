var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 4) {
            qm.sendNext("뭐, 마스터가 그렇다면 그런 거겠지만... 역시 신경 쓰여. 마스터도 다시 한 번 생각해봐.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("저기 말이야, 마스터. 좀 이상하지 않아? 방금 한 블랙윙의 임무말이야... 이상한 곳이 한 둘이 아닌 것 같아. 일단 마스터 소울 테니에게서 자유로운 영혼을 풀어주는 건 좋은 일인 줄 알았는데...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b그걸 일부러 주머니에 가둬 둘 필요는 없었던 것 같지? 게다가 하늘 발코니 앞에서만 풀어야 한다는 것도 이상하고. 자유롭게 해준다면 어디서 풀어줘도 상관 없을 텐데 말이야.#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("응응, 그거 풀어 놓으니 경비병들이 화내는 거 들었어? 경비 서는데 방해된다고 버럭 버럭 하고 있었잖아. 그렇지? 경비병들도 나쁜 사람이었을까?");
            break;
        }
        case 3: {
            qm.sendNextPrev("게다가 도어락이 사라지면서 한 말... 너무 신경 쓰여. 우리한테 도둑이라니... 나쁜 몬스터를 퇴치했는데도 기분이 좋아지지 않아.");
            break;
        }
        case 4: {
            qm.sendAcceptDecline("그 이베흐라는 인간은 신경쓰지 말라고 했지만 아무리 생각해도 신경 쓰여. 이번 일은 좋은 일 같지가 않아. 그렇지 않아, 마스터?\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 20,000 exp\r\n#fUI/UIWindow.img/QuestIcon/10/0# 1 sp");
            break;
        }
        case 5: {
            qm.dispose();
            qm.gainExp(20000);
            qm.getPlayer().gainSP(1, Packages.constants.GameConstants.getSkillBook(qm.getJob()));
            qm.sendOk("블랙윙이라는 단체... 워낙 비밀스러운 단체이니 우리가 모르는 뭐가 사정이 있는 거겠지? 의심하고 싶지는 않지만... 좀 의심스러워.");
            qm.forceCompleteQuest();
            break;
        }
    }
}