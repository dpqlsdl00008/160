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
            qm.sendNext("누군데 감히 에레브에 허락도 없이 발을 디디는 겁니까. 당신의 이름과 직업, 이 섬에 온 목적을 밝히십시오. 거짓을 말하거나 목적이 타당하지 않은 사람은 섬에 들어 올 수 없습니다.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b에엑? 여긴 출입 금지 지역인가요? 다른 사람들은 잘만 오가던데요?#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("여제 시그너스의 기사들만이 이 섬에 출입 할 수 있습니다. 모르셨던 모양이니 이번만은 용서해 드리겠습니다. 어서 섬 밖으로 나가주세요.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b자, 잠깐만요! 저기 한 가지만 알려주시면 안 되나요?#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("음? 방문 목적이 있으신 겁니까? 어줍잖은 모험가인 줄 알았는데 그건 아닌 모양이군요. 그럼 이름과, 직업, 방문 목적을 말씀해 주십시오.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b제 이름은 에반, 직업은 드래곤 마스터예요! 전 드래곤 라이딩을 위한 안장을 구하고 있어요. 여기 오면 멋진 안장을 구할 수 있다던데... 그 것만 구해 가지고 가면 안 될까요?#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("...드래곤 마스터? 바하뮤트를 부리는 마법사입니까?");
            break;
        }
        case 7: {
            qm.sendNextPrevS("#b아뇨. 그건 아닌데요...?#k", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("...흐음. 이상한 분이시군요. 드래곤 마스터라니... 모험가들 중에 그런 직업도 있었나... 뭐, 이건 제가 직접 알아보면 될 일이겠죠. 안장에 대해 말씀하셨죠?");
            break;
        }
        case 9: {
            qm.sendAcceptDecline("기사들의 안장은 이 곳에서 만들어지지 않습니다. 안장까지 모두 제작하기에 인력이 모자라서요. 외부 인력에게 안장 제작을 맡겼거든요. 그 쪽에라도 가보시겠습니까?");
            break;
        }
        case 10: {
            qm.sendNext("기사들의 안장은 모두 #b아쿠아리움 동물원#k의 #b켄타#k가 제작해 주고 있습니다. 말씀하신 것 처럼 훌륭한 안장들이지요. 단 #r눈이 튀어나올 정도로 비싼 값#k을 치러야 하니, 안장을 구하시는 거라면 각오를 단단히 하셔야 할 겁니다.");
            break;
        }
        case 11: {
            qm.sendNextPrev("자, 그럼 용건은 이걸로 끝이시겠죠? 그만 이 섬에서 나가 주세요. 딱히 나쁜 분은 아닌 것 같지만 규칙은 규칙이니까요. 외부인을 오랫동안 섬에 들여 놓을 수는 없습니다.");
            break;
        }
        case 12: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}