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
            qm.sendNext("마스터! 우리가 가입되어 있는 조직과 에델슈타인의 관계, 왠지 굉장히 신경 쓰이지 않아? 안 그래도 임무를 하면서 왠지 점점 블랙윙의 멤버로 굳어지는 것 같은데... 이게 좋은 일일까?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("블랙윙의 멤버가 되면 될수록 오히려 블랙윙이라는 조직에 대해서는 점점 모르겠다는 느낌이 들어. 에델슈타인 사람들은 블랙윙을 굉장히 싫어했잖아. 정말 블랙윙이 좋은 곳일까?", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("나도 잘 모르겠어. 어쨌든 지금 우리가 할 수 있는 일은 블랙윙의 멤버로, 블랙윙의 임무에 대해 알아보는 것 뿐인 것 같아. 그렇지?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.sendOk("아~ 점점 더 모르겠다. 블랙윙은 좋은 조직인거야, 아닌거야?");
            qm.forceCompleteQuest();
            break;
        }
    }
}