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
            qm.sendNext("믿을 수 없어. 스탄이, 그 쪼잔한 스탄이, 내가 몰래 사탕을 먹었다는 이유만으로 2년 동안 말도 하지 않은 스탄이, 3000메소만 빌려줘놓고 3년 동안 초 단위로 이자를 계산하던 스탄이, 그 스탄이!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(스탄 아저씨 그런 사람이었구나...)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("좋은 모험가를 소개시켜 줘서 내 수련장 발전에 기여를 하다니. 말도 안돼. 나에게 도움이 될만한 일을 하는 건 스탄이 아니야. 아무리 생각해도 확률이 너무 낮아. 한 번... 마지막으로 한 번만 더 시험을 해보도록 하지, 스탄의 앞잡이.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(그러니까 앞잡이가 아니라니까요.)#k", 2);
            break;
        }
        case 4: {
            qm.sendAcceptDecline("시험은 간단해! 내 수련장에 있는 특별한 스포아, #r수련생 스포아 100마리#k를 퇴치하면 돼! 주황버섯들과 섞여 있는 녀석들을 찾기는 그리 쉽지 않을 것이야. 후후... 수련장에 바로 들어가겠나?");
            break;
        }
        case 5: {
            qm.dispose();
            qm.warp(910060100, 1);
            qm.forceStartQuest();
            break;
        }
    }
}