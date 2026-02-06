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
            qm.sendNext("어쌔신(Assassin)의 길을 택한 건가? 후... 쉬운 길이 아닐텐데? 어쌔신은 주로 표창을 사용하는 도적을 말하지. 원거리에서 적을 빠르게 공격하지. 이동 속도가 매우 빠르고 공격이 날카로워 1:1 대결의 최강자로 불리지.");
            break;
        }
        case 1: {
            qm.sendNextPrev("던지는 무기를 잘 사용하기 위해 #b자벨린 마스터리#k와 #b자벨린 부스터#k를 익히지. #b크리티컬 스로우#k를 통해 강력한 한 방을 노리는 것도 특징 중에 하나야.");
            break;
        }
        case 2: {
            qm.sendNextPrev("#b헤이스트#k를 쓰면 파티원까지 모두 이동속도와 점프력을 올릴 수 있어 편리하고, #b쉐도우 레지스턴스#k를 익히면 어둠과 친화력이 높아져 #b체력#k 및 기타 능력이 높아져.");
            break;
        }
        case 3: {
            qm.sendNextPrev("흠... 말이 너무 길어 지루했겠군. 그럼 어쌔신의 길을 걷겠나. 그렇다면 시험을 하지. 뭐, 그렇다고 복잡한 시험은 아니야. 준비된 시험장에 들어가 몬스터를 물리치고 #r어둠의 힘이 담긴 구슬 30개#k를 구해 나오면 되는 간단한 내용이지. 다만... 일반적인 몬스터보다 좀 강한 게 문제랄까?");
            break;
        }
        case 4: {
            qm.sendAcceptDecline("시험을 보다가 물약이 떨어지면 #b퀘스트를 포기하고 다시 시작#k해야 해. 그러니 알아서 잘 준비해서 들어가라고. 그럼 바로 시험 시작이다. 수락하면 자네를 시험의 늪으로 보내겠다.");
            break;
        }
        case 5: {
            qm.dispose();
            qm.warp(910370000, 0);
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
            qm.sendYesNo("검은 구슬을 모두 가져왔군. 제법인걸? 너라면 어쌔신이 되는 게 나쁘지 않을 것 같아. 그럼... 바로 어쌔신으로 만들어 주지. 준비는 되었나?");
            break;
        }
        case 1: {
            if (qm.isQuestActive(1422) == true) {
                qm.gainItem(4031013, -30);
                qm.changeJob(410);
                qm.forceCompleteQuest();
            }
            qm.sendNext("좋아. 자네는 이제부터 #b어쌔신#k이야. 어쌔신은 재빠른 행동과 손재주로 적을 제압하는 자. 더욱 수련에 정진하길 바라겠어.");
            break;
        }
        case 2: {
            qm.sendNextPrev("어쌔신은 강해져야 해. 하지만 그 강함을 약자에게 사용하는 것은 올바른 길이 아니야. 자신이 가지고 있는 힘을 옳은 일에 사용하는 것. 그것은 강해지는 것 이상으로 어려운 일이야.");
            break;
        }
        case 3: {
            qm.dispose();
            break;
        }
    }
}