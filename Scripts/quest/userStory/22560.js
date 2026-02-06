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
            qm.sendNext("오랜만입니다, 은인님! 주인님께서 상처 치료도 하시고 새 아지트도 구하시느라 바쁘셔서 한 동안 연락을 안 주셨는데, 얼마 전에 드디어 연락이 닿았네요!");
            break;
        }
        case 1: {
            qm.sendNextPrev("주인님께 당신에 대해 말씀드리자 괜찮은 사람이라면 얼마든지 단체에 가입 할 수 있다고 하셨어요! 단, 조건이 있다고 하시네요. 단체에 가입하기 위한 최저한의 시험인 모양이에요.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("은인님처럼 강하고 멋진 분이라면 그런 시험 정도는 간단히 통과 할 수 있을 겁니다. 그럼 시험에 대해 설명드려도 될까요?");
            break;
        }
        case 3: {
            if (qm.isQuestActive(22560) == false) {
                qm.forceStartQuest();
            }
            qm.sendNext("시험은 간단해요! #b북쪽 숲 지역#k으로 가서 #r커즈아이 150마리#k를 퇴치하면 됩니다. 주인님께서 거기 아지트를 만드시려는 커즈아이가 위험해서 골치가 아프신 모양이더라고요.");
            break;
        }
        case 4: {
            qm.sendNextPrev("거기 말고 다른 데 아지트를 만들면 좋을 텐데... 근데 얼마 전에는 #b어떤 사원인가 하는 곳에 아지트를 만들려다가, 주변에 있는 몬스터가 너무 강해 폭주하는 사건#k이 있었다니 이번에는 신중하게 결정하시는 모양이에요.");
            break;
        }
        case 5: {
            qm.dispose();
            break;
        }
    }
}