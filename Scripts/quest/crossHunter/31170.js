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
            qm.sendNext("이 봐 여기야!");
            break;
        }
        case 1: {
            var say = "무슨 일이야? ...아니 이건 아란...?!";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "무슨 일이야? ...저건 아란...! 괜찮아?";
            }
            qm.sendNextPrevS(say, 2);
            break;
        }
        case 2: {
            var say = "주인을 알아? 그러고 보니 넌 왠지 이 세상 사람 같지 않아 보이는데... 아무래도 좋아. 네 눈은 선해 보이니까.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "메르세데스 무사했구나! 넌 검은 마법사의 저주에 당하지 않은거야?";
            }
            qm.sendNextPrev(say);
            break;
        }
        case 3: {
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == false) {
                qm.sendNextPrev("난 폴암의 정령 마하야. 내 옆에 있는 주인과 함께 검은 마법사를 물리치고 영원히 봉인 속에 가뒀지. 하지만 검은 마법사는 봉인의 순간 최후의 힘을 모아 그를 상대한 영웅들에게 강대한 저주를 내렸어. 그 때문에 주인이 지금 정신을 잃고 쓰러져 있는 거야.");
            } else {
                qm.sendNextPrevS("말하자면 길지만, 나도 검은 마법사의 저주에 걸렸었어. 길고 긴 시간 끝에 간신히 이 정도인 셈이지.", 2);
            }
            break;
        }
        case 4: {
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == false) {
                qm.sendNextPrev("이대로 가다간 다시는 일어나지 못할 지도 몰라. 어서 주인을 안전한 곳으로 데려가야 하는데, 나 역시 이 꼴이지. 그래서 말인데 네가 좀 도와줬으면 좋겠어.");
            } else {
                qm.sendNextPrev("그게 무슨 소리야? 넌 맨날 어렵게 이야기 한다니깐. 아무튼 건강한 모습이라 다행이야. 우리 주인이 이렇게 쓰러져 있지만 않았어도 더 좋았을텐데.");
            }
            break;
        }
        case 5: {
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == false) {
                qm.dispose();
                qm.forceCompleteQuest();
                return;
            }
            qm.sendNextPrevS("걱정 마. 아란은 반드시 일어 날 거야. 내 눈으로 똑똑히 봤으니까.", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("아까부터 자꾸 이상한 소리만 하는거야? 마치 미래에 갔다 온 것 처럼 말야. 그런 말을 할 시간에 나를 좀 도와 줘. 주인을 일단 안전한 곳으로 옮겨야겠어.");
            break;
        }
        case 7: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}