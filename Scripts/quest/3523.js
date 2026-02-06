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
            qm.sendNext("...이 강력한 위압감을 보니 자네는 대단히 강력한 전사로군. 이름이... #h #? 하하! 그러고 보니 기억에 있는 이름이군. 아주 오래 전에 전사가 되겠다고 찾아왔던 미숙한 초보자들 중에, 그런 이름이 있었지.");
            break;
        }
        case 1: {
            qm.sendNextPrev("자네가 이렇게 강력한 전사가 될 줄이야! 이제는 이 주먹펴고일어서라고 해도 승부를 장담하기 힘들겠는걸? 정말 대단하네... 그래, 자네라면 훌륭한 전사로 성장할 줄 알았네.");
            break;
        }
        case 2: {
            qm.sendNextPrev("계속 더 정진하게. 자네를 전사로 만들어준 사람으로써 확신한다네. 더 강한 전사가 될 거란 걸...");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}