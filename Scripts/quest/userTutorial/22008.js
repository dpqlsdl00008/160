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
            qm.sendAcceptDecline("좀 이상하지 않아? 요새 닭들이 예전 같지 않잖아. 예전에는 달걀을 이거보다 훨씬 많이 낳았는데 점점 줄어들고 있어. 혹시 여우가 많아진 게 원인일까? 그렇다면 얼른 손을 써야 할 텐데. 안 그래?");
            break;
        }
        case 1: {
            qm.sendNext("그렇지? 그럼 우리가 여우를 퇴치하자. 네가 먼저 #b뒷 마당#k으로 가서 #r음흉한 여우 10마리#k만 사냥해 놔. 그럼 내가 뒤를 치도록 할게. 자, 그럼 얼른 뒷 마당으로 출발~");
            break;
        }
        case 2: {
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
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("음흉한 여우는 퇴치하고 온 거야?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b여우의 뒤를 치겠다더니 어떻게 된 거야?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("아, 그거? 뒤를 쫓아 가긴 했는데 잘못 나섰다가 네가 음흉한 여우에게 인질로 잡힐까봐 그냥 뒀지 뭐.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b여우가 무서워서 숨어있던 건 아니고?#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("무슨 말이야? 내가 왜 여우를 무서워하는데? 난 여우 따위는 하나도 안 무서워!");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b...앗, 저기 음흉한 여우가!#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("으악! 숨어!");
            break;
        }
        case 7: {
            qm.sendNextPrevS("#b......#k", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("......");
            break;
        }
        case 9: {
            qm.sendNextPrev("...너 이 녀석, 형을 놀리지 마! 이 형은 심장이 약해서 놀래면 안 된다고!");
            break;
        }
        case 10: {
            qm.sendNextPrevS("#b(이래서 형이라고 부르기 싫다니까.)#k", 2);
            break;
        }
        case 11: {
            qm.sendNextPrev("흠흠. 아무튼. 음흉한 여우를 잘 퇴치해서 다행이야. 수고했으니 대신 보답으로 전에 지나가던 모험가가 준 물건을 줄게. 자, 받아.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1372043# #z1372043# 1개\r\n#i2022621# #z2022621# 25개\r\n#i2022622# #z2022622# 25개\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 910 exp");
            break;
        }
        case 12: {
            qm.sendNextPrev("#b마법사의 공격 무기인 완드#k야. 뭐, 딱히 너한테 필요한 물건을 아닐 것 같지만 들고 다니면 멋지긴 할 거야. 아하하.");
            break;
        }
        case 13: {
            qm.sendNextPrev("그나저나 여우들이 늘어난 건 맞지? 이상해. 왜 이렇게 여우가 늘은 거지? 뭔가 조사해 볼 필요가 있는 것 같아.");
            break;
        }
        case 14: {
            qm.dispose();
            qm.gainItem(1372043, 1);
            qm.gainItem(2022621, 25);
            qm.gainItem(2022622, 25);
            qm.gainExp(910);
            qm.forceCompleteQuest();
            break;
        }
    }
}