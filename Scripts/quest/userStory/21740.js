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
            qm.sendNext("돌아왔군, 영웅님. 오르비스에서 있었던 일은 어땠어? 역시 블랙윙과 관련 있던 일이야? 왜이렇게 표정이 어두운 거야? 자세히 설명해 봐.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(오르비스의 봉인석과 관련된 이야기를 설명했다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("흠. 오르비스에도 봉인석이 있었다라... 그거 굉장한 정보로군. 빼앗긴 건 아쉽지만... 아, 영웅님을 탓하는 건 아냐. 블랙윙이 그만큼 철저히 준비했다는 거겠지.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("....", 2);
            break;
        }
        case 4: {
            qm.sendAcceptDecline("기운 내라고! 아, 그렇지. 리린이 이번에 #b새로운 스킬#k을 해독한 것 같아. 오르비스에 있었던 일도 알려줄 겸, #b리엔으로 가서 리린을 만나봐.#k");
            break;
        }
        case 5: {
            qm.sendOk("리린 역시 이 일의 관련자이고 또 영웅님이 살던 과거에 대해서 리린만큼 잘 아는 사람도 없으니 #b리린과는 항상 정보를 공유#k하고 상의하는 게 좋겠어.");
            break;
        }
        case 6: {
            qm.dispose();
            qm.forceStartQuest();
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
            qm.sendNext("아, 오랜만이에요. 아란. 그 동안 수련은 잘 하셨어요?마침 새로운 스킬을 발견해서 당신을 부를 생각이었는데... 잘 오셨어요!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(리린에게 오르비스 봉인석에 대해 설명했다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("오르비스 봉인석이라... 그렇군요. 이걸로 확실해 졌어요. 블랙윙이 노리는 것은 봉인석이고, 그것은 하나가 아니라는 사실이 말이에요. 이것만으로도 대단한 수확이에요.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("하지만 봉인석을 빼앗기고 말았는데...", 2);
            break;
        }
        case 4: {
            qm.sendYesNo("블랙윙은 훨씬 오래 전부터 이 일을 준비 했을거예요. 그걸 생각하면 빅토리아 아일랜드의 봉인석을 얻게 된 것도 충분히 대단한 일이에요. 그보다 이 스킬을 받아주세요.");
            break;
        }
        case 5: {
            qm.sendNext("당장은 당신이 강해지는 게 더 중요해요. 봉인석에 관한 건 저와 진실 아저씨가 주시하고 있을 테니, 아란은 방금 드린#b콤보 스매쉬#k 스킬을 익혀 주세요.");
            break;
        }
        case 6: {
            qm.dispose();
            qm.teachSkill(21100004, qm.getPlayer().getTotalSkillLevel(21100004), 20);
            qm.forceCompleteQuest();
            break;
        }
    }
}