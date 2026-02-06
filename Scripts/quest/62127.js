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
            qm.sendNext("...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("...?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#Cgray#(거지인가...)#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("저기 말이야, 왜 자꾸 날 쳐다보는 거지?");
            break;
        }
        case 5: {
            qm.sendNextPrevS("실례 했습니다. 이만 가볼게요.", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("잠깐 잠깐!! 저기 너가 입고 있는 옷 말이야... 어디서 산 거야? 나에게 팔지 않을래? 가격을 제시 해봐! 내가 후하게 쳐줄테니.");
            break;
        }
        case 7: {
            qm.sendNextPrevS("정말 인가요?\r\n\r\n#Cgray#(믿어도 되는걸까? 음... 하지만 입고 있는 옷을 팔 수는 없는데...)#k", 2);
            break;
        }
        case 8: {
            qm.askAcceptDecline("그럼 혹시 밖에 나갔다가 진귀한 물건을 발견하게 된다면 나에게로 가져오는게 어때? 내가 평가해 주겠어! 이래 보여도 난 골동품 상인이라구! 다른 사람들보다 가장 공정하게 가격을 책정해주지!");
            break;
        }
        case 9: {
            qm.sendNextS("우선 알겠습니다.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 170,598 exp", 2);
            break;
        }
        case 10: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(170598);
            break;
        }
    }
}