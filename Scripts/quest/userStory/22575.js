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
            qm.sendNext("안녕하십니까, 에반씨. 이, 갑자기 말을 걸어서 놀라신 모양이군요. 걱정하지 마십시오. 전 수상한 사람이 아닙니다. 당신이 가입한 그 단체의 사람일 뿐입니다.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b당신이 남겨진 인형의 주인인가요?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("아, 프란시스를 말씀하시는 모양이군요. 그렇지는 않습니다. 하지만 제가 그보다 상급자인 것은 확실합니다. 전에 오르비스에서 당신께 임무를 드린 게 바로 저이죠.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b그 은밀한 벽돌에 들어 있던 일 말인가요?#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("네. 은밀한 벽돌 안에 임무가 쓰인 종이를 넣어둔 게 바로 저입니다. 당신께서 만들어 주신 성장 촉진제는 유용하게 잘 썼습니다. 정말 감사했습니다. 도움이 많이 되었습니다.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b아하하하. 사람들을 위해서라면 당연히 해야 할 일이죠.#k", 2);
            break;
        }
        case 6: {
            qm.sendYesNo("무슨 말씀을요. 아직 정식 멤버도 아니시면서 너무 잘해주셔서 단체 내에서 당신의 위상이 꽤 높아졌습니다. 당신이라면 믿고 이번 일도 맡길 수 있을 것 같습니다. 괜찮으시겠습니까?");
            break;
        }
        case 7: {
            qm.sendNext("이번 일은 전보다 훨씬 난이도가 있습니다. 죽은 자들의 숲에서 #r이빨 빠진 쿨리 좀비#k들을 물리친 후, #b이빨 빠진 좀비의 어금늬#k를 구해주셔야 합니다. 죽은 자들의 숲은 #r장로의 관저 제일 아래 층의 맨 오른쪽 열려있는 문#k을 통해서 들어 갈 수 있습니다.");
            break;
        }
        case 8: {
            qm.sendNextPrev("그렇게 #b이빨 빠진 좀비의 어금늬를 150개#k 구해 엘나스 장로의 관저 지하에 있는 #b샤모스#k라는 자에게 전해 주시면 됩니다. 그럼 샤모스가 당신께 약속한 물건을 드릴 겁니다. 당신이 물건을 받으면 다시 연락 드리겠습니다.");
            break;
        }
        case 9: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}