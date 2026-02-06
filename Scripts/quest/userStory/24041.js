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
            qm.sendNextS("하아... 하아... 침착해. 침착하자, 메르세데스. 머리를 쥐어 뜯어 봤자 소용 없어. 침착하게 현재 상황을 다시 한 번 정리해 보자고.", 17);
            break;
        }
        case 1: {
            qm.sendNextPrevS("1. 검은 마법사의 저주는 사라지지 않았다. 장로들이나 다른 백성들이 여전히 얼음 속에 갇혀 있는 것을 보면 이건 확실하다.", 17);
            break;
        }
        case 2: {
            qm.sendNextPrevS("2. 깨어난 사람은 나뿐인 것 같다. 어째서인지 정확히는 알 수 없지만... 아마도 깨어나기 직전에 느꼈던 그 기운을 생각해 보면, 어쩌면 검은 마법사와의 봉인이 약해졌기 때문일지도?", 17);
            break;
        }
        case 3: {
            qm.sendNextPrevS("3. 밖으로 나가 메이플 월드의 상황을 살피고 싶지만 그럴 수가 없다. 왜냐면... 내 레벨이 10에 불과하기 때문이다. 으아니, 이게 어찌 된 일이야! 내가 10레벨이라니!", 17);
            break;
        }
        case 4: {
            qm.sendNextPrevS("검은 마법사가 날린 마지막 저주... 그 정도의 저주라면 당연히 여파가 클 텐데 그걸 간과했어. 게다가 얼마 동안인지 정확히 모르겠지만 꽤나 오랜 시간 얼음 속에 갇혔으니 몸도 굳어졌을 테고.", 17);
            break;
        }
        case 5: {
            qm.sendNextPrevS("부상을 입은 채로, 저주를 받아, 얼음 속에 갇혀서 오랜 시간 있었으니 레벨이 낮아지는 것도 당연한 일이야. 당연한 일이지. 당연한 일... 일리가 없잖아! 내 레벨을 돌려줘! 나 메르세데스가 겨우 레벨 10이라니!", 17);
            break;
        }
        case 6: {
            qm.sendNextPrevS("...하아, 하아... 치, 침착을 되찾자. 절규 할 때가 아니야! 일단 몸 상태를 좀 살펴보자. 레벨만 10이 된 건가? 부상이 남아 있는 건 아니겠지? 팔이나 다리는 잘 움직여지고?", 17);
            break;
        }
        case 7: {
            qm.sendNextPrevS("왼 팔은 잘 휘둘러지고...", 17);
            break;
        }
        case 8: {
            qm.sendNextPrevS("오른 팔도 문제 없고...", 17);
            break;
        }
        case 9: {
            qm.sendNextPrevS("다리도 멀쩡해.", 17);
            break;
        }
        case 10: {
            qm.sendNextPrevS("그나마 부상은 다 나았군. 다만 레벨이... 으으으... 울고 싶다...", 17);
            break;
        }
        case 11: {
            qm.sendNextPrevS("울고 있을 때가 아니야. 몸이 멀쩡하다면 다시 한 번 경험을 쌓아 레벨을 올릴 수 있어. 무, 물론 사라진 190레벨이 억울하지 않은 건 아니지만!\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 500 exp", 17);
            break;
        }
        case 12: {
            qm.dispose();
            qm.gainExp(500);
            qm.forceCompleteQuest();
            break;
        }
    }
}