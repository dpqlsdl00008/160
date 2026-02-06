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
            qm.sendNext("훌륭하게 정제된 마력이군. 대마법사의 반열에 오른 자들만이 보일 수 있는... 그러고 보니, 아주 오래 전에 대마법사의 재능이 있는 초보자를 본 적이 있었는데... 그 이름이 #h #였지.");
            break;
        }
        case 1: {
            qm.sendNextPrev("아직 매직 클로도 쓸 줄 모르던 미숙한 초보자였던 자네가 이리 성장한 모습을 보게 되다니... 정말 기쁘구만. 자네라면 이렇게 될 줄 알았지.");
            break;
        }
        case 2: {
            qm.sendNextPrev("계속 더 정진하게... 자네를 마법사로 만든 사람으로써, 확신하고 있다네. 자네가 더 강한 마법사가 되리라고...");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}