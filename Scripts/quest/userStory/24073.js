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
            qm.sendNext("다행히 이번에는 마을을 지켜 낼 수 있었지만... 다음에 또 공격을 받으면 위험할지도 몰라요. 아직 장로님들도 메르세데스님도 예전 힘을 되찾지 못하셨으니까요.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("...일단, #b미스털테인은 네가 계속 갖고 있는 게 좋겠다.#k 헬레나. 마을에 두고 있다가 혹시라도 공격 받아 빼앗기기라도 하면 큰 손실. 너라면 잘 지켜 낼 수 있겠지...", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("하지만 그럼 마을의 보호는...");
            break;
        }
        case 3: {
            qm.sendNextPrevS("다른 방법을 찾아봐야지. 내가 계속 지켜도 괜찮고.", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("메르세데스님은 수련을 계속 하셔야죠. ...그럼 이 방법은 어떨까요? 블랙윙과 가장 최전선에서 싸우는 #b시그너스 기사단에게 이 사실을 이야기하고 협력을 요청#k하는 거예요.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("시그너스 기사단? 지금의 황제는 기사를 갖고 있다고 했지?", 2);
            break;
        }
        case 6: {
            qm.sendAcceptDecline("네. 예전과 달리 대대적으로 기사를 모집했답니다. 그들은 우리와 같은 적을 둔 동지. 우리의 상황을 말해 주면 분명 협력해 줄 거예요.");
            break;
        }
        case 7: {
            qm.sendNext("그럼 제가 헤네시스로 가서 시그너스 기사단이 있는 에레브에 편지를 쓸게요. 메르세데스님은 일단 마을에서 그들의 협력을 기다리시겠어요?");
            break;
        }
        case 8: {
            qm.sendNextPrevS("아니. 내가 에레브로 가서 현 황제를 만나 보겠다. 그가 신뢰 할 만한 자인지 아닌지 내 눈으로 확인해야지.", 2);
            break;
        }
        case 9: {
            qm.sendNextPrev("역시 메르세데스님은 변하지 않으셨군요... 왕이시면서 누군가에게 시키기는 커녕 항상 먼저 나서서 일을 처리하곤 하셨죠... 그럼 저는 그들이 미리 메르세데스님을 맞을 준비를 하도록 편지를 보낼게요.");
            break;
        }
        case 10: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}