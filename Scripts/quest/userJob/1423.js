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
            qm.sendNext("시프(Thief)의 길을 택한 건가? 시프는 단검을 주로 사용하는 도적을 말하지. 근거리에서 재빠르게 적을 공격하고 빠지며 적이 가진 것을 빼앗는, 그런 직업이랄까...");
            break;
        }
        case 1: {
            qm.sendNextPrev("#b대거 마스터리#k와 #b대거 부스터#k도 단검의 숙련도와 속도를 올린 후, #b스틸#k로 다수의 적을 공격하며 아이템을 빼내거나, #b새비지 블로우#k로 적을 연속 공격하는 것이 특기지.");
            break;
        }
        case 2: {
            qm.sendNextPrev("#b헤이스트#k를 쓰면 파티원까지 모두 이동 속도와 점프력을 올릴 수 있어 편리하고, #b쉐도우 레지스턴스#k를 익히면 어둠과 친화력이 높아져 #b체력#k 및 기타 능력이 높아져.");
            break;
        }
        case 3: {
            qm.sendNextPrev("흠... 말이 너무 길어 지루했겠군. 그럼 시프의 길을 걷겠나. 그렇다면 시험을 하지. 뭐, 그렇다고 복잡한 시험은 아니야. 준비된 시험장에 들어가 몬스터를 물리치고 #r어둠의 힘이 담긴 구슬 30개#k를 구해 나오면 되는 간단한 내용이지. 다만... 일반적인 몬스터보다 좀 강한 게 문제랄까?");
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
            qm.sendYesNo("검은 구슬을 모두 가져왔군. 제법인걸? 너라면 시프가 되는 게 나쁘지 않을 것 같아. 그럼... 바로 시프로 만들어 주지. 준비는 되었나?");
            break;
        }
        case 1: {
            if (qm.isQuestActive(1423) == true) {
                qm.gainItem(4031013, -30);
                qm.changeJob(420);
                qm.forceCompleteQuest();
            }
            qm.sendNext("좋아. 자네는 이제부터 #b시프#k야. 시프는 재빠른 행동과 손재주로 적을 제압하는 자. 더욱 수련에 정진하길 바라겠어.");
            break;
        }
        case 2: {
            qm.sendNextPrev("시프는 강해져야 해. 하지만 그 강함을 약자에게 사용하는 것은 올바른 길이 아니야. 자신이 가지고 있는 힘을 옳은 일에 사용하는 것. 그것은 강해지는 것 이상으로 어려운 일이야.");
            break;
        }
        case 3: {
            qm.dispose();
            break;
        }
    }
}