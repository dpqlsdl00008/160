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
            qm.sendNext("듀어러로 전직하기 위해서는 한 가지 증명이 필요해요. 당신이 그만한 능력을 갖췄는지, 시험에 통과해야만 하죠. 걱정말아요. 당신이라면 충분히 가능할 테니까요.");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("내가 만들어낸 공간에 가서 쉐도우 블레이드와 싸우고, 그들로부터 검은 구슬을 가져오세요. 이것이 장신의 혜안을 깨우기 위한 시럼. 수락하면 바로 보내주죠.");        
            break;
        }
        case 2: {
            qm.dispose();
            qm.warp(910350300, 0);
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
            qm.sendNext("듀어러는 당순히 적만을 바라보는 것이 아니라, 타인을 지킬 능력을 지닌 이도류의 계승자만이 배울 수 있는 경지.");
            break;
        }
        case 1: {
            qm.sendNextPrev("우리는 홀로 싸우는 자들이지만, 자신만을 위해서 싸워서는 안 돼요. 그것은 단순한 독선일 뿐... 혜안이 바라봐야 하는건 약한 자여야 하죠.");
            break;
        }
        case 2: {
            qm.sendNextPrev("약한 자들을 위해 싸울 수 있는가를 시험하는 게 이번 시험의 목적이었고, 당신은 그에 훌륭히 통과했어요.");
            break;
        }
        case 3: {
            qm.sendNextPrev("...왠지 예전 생각이 나네요. 아버지를 만나던 날... 몬스터에 둘러 싸여 꼼짝도 못하는 날 구해주셨지요. 그리고 딸로 삼아 주셨어요.");
            break;
        }
        case 4: {
            qm.sendNextPrev("그 분이 그랬던 것처럼 당신도 따뜻한 마음을 갖고 싸우길 바래요...");
            break;
        }
        case 5: {
            qm.dispose();
            qm.gainItem(4031013, -30);
            qm.changeJob(431);
            qm.teachSkill(4311003, 0, 20);
            qm.forceCompleteQuest();
            break;
        }
    }
}