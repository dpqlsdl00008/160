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
            qm.sendNext("자, 여기 택배 상자 안에 들어 있던 물건들이야... 당신 눈에는 어떤 게 고대 버섯의 포자처럼 보여? 내 눈에는 다 비슷 비슷해 보이는데...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("(이 인간이 말하는 고대 버섯이란 아마도 내가 살던 시대의 이끼 버섯이 틀림 없다. 이끼 버섯의 포자를 골라 주자.)", 2);
            break;
        }
        case 2: {
            var say = "어떤 게 고대 버섯의 포자처럼 보여?";
            say += "\r\n#L0##i4000011# #L1##i4000040# #L2##i4000437# #L3##i4000499# #L4##i4032389#";
            qm.sendSimple(say);
            break;
        }
        case 3: {
            if (selection != 2) {
                qm.dispose();
            } else {
                qm.sendNextPrev("이게 고대 버섯의 포자라고? 그러고 보니 그럴싸한데...? 흠, 좋아! 베티나 윈스턴 박사님이 신뢰하는 당신의 말을 믿고 이 포자로 연구를 해 보겠어!");
            }
            break;
        }
        case 4: {
            qm.sendNextPrevS("훗, 내 말은 틀림이 없다.", 2);
            break;
        }
        case 5: {
            qm.sendAcceptDecline("자신 만만 한 걸? 흠... 어디 보자, 이 포자는... 그러고 보니 이 포자와 비슷한 포자가 하나 있었던 것 같은데... 아, 저번 연구에 다 써버렸나... 저기 미안한데 부탁 하나만 더 해도 될까?");
            break;
        }
        case 6: {
            qm.sendNext("당신이 고대 버섯의 포자라고 말한 이 포자, 보아 하니 #b좀비 머쉬맘의 포자#k와 좀 닮은 것 같아. 좀비 머쉬맘의 포자를 구한다면 이 포자에 대해서도 좀 더 자세히 연구 할 수 있을 것 같은데... 당신이 좀비 머쉬맘의 포자를 구하는 걸 도와주지 않겠어?");
            break;
        }
        case 7: {
            qm.sendNextPrev("좀비 머쉬맘은 #b좀비 머쉬맘의 언덕#k에 가면 볼 수 있어. 내가 준 버섯 페로몬 향수 병을 가지고 가면 이 맘떄에 좀비 머쉬맘을 끌어 들일 수 있으니 기다리지 않고 쉽게 녀석을 만날 수 있을 거야.");
            break;
        }
        case 8: {
            qm.sendNextPrev("제법 강한 녀석이라 당신이 잡을 수 있을 지가 좀 걱정이긴 하지만... 뭐, 믿고 맡겨 볼게. 그럼 부탁해!");
            break;
        }
        case 9: {
            qm.dispose();
            qm.gainItem(4032965, 1);
            qm.forceStartQuest();
            break;
        }
    }
}