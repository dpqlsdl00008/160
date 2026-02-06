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
            if (qm.isQuestActive(21720) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                 qm.sendNext("무슨 일이신가요? 분명 빅토리아 아일랜드에서 수련에 열중하고 있다고, 일에도 매우 도움이 된다고 진실 아저씨가 전갈을 보내셨는데... 네? 블랙윙?");
            }
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(인형사와 블랙윙, 블랙윙의 목적에 대해 말해주었다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("그렇군요... 블랙윙이라. 그런 자들이 있을 줄이야... 위험할 줄 알면서도 메이플 월드에 검은 마법사를 부활시키려 하다니, 어리석기 짝이 없네요.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("그, 그렇지...\r\n#b(말투가 너무 단호해서 좀 무섭다.)#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("예언에는 고작해야 영웅이 일어나 검은 마법사와 싸울 것이다, 정도밖에 없어요. 겨우 그뿐이어서 반신반의했지만 이걸로 확실해졌네요. 검은 마법사는 확실히 존재한다는 게.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("무섭지 않나?", 2);
            break;
        }
        case 6: {
            qm.sendYesNo("무섭냐고요? 전혀요. 검은 마법사건 뭐건 나오면 당신이 물리쳐줄 텐데 뭐가 걱정이겠어요. 오히려 투지가 생기는 걸요? 당신도 그렇죠, 아란?");
            break;
        }
        case 7: {
            qm.sendNext("당신은 차근차근히 강해져 가고 있어요. 나는 당신이 강해질 수 있도록 온 힘을 다해 도울 거예요. 그런데 뭐가 무섭겠어요? 절대 지지 말아요. 검은 마법사 따위에게 지기 위해 수백 년을 잠든 게 아니잖아요? 이번에야말로 물리쳐 버려요!");
            break;
        }
        case 8: {
            qm.sendNextPrev("자, 그러기 위해선 계속해서 수련! 수련이겠죠? 빅토리아 아일랜드로 가서 계속해서 수련해 주세요. 검은 마법사 따위는 물리쳐 버릴 수 있을 정도로 강해지자고요!");
            break;
        }
        case 9: {
            qm.dispose();
            qm.gainExp(3900);
            qm.teachSkill(21001003, qm.getPlayer().getTotalSkillLevel(21001003), 20);
            qm.forceCompleteQuest();
            break;
        }
    }
}