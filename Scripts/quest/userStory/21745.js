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
            qm.sendSimple("무슨일이냐? 여긴 신성한 도장! 사담은 금지다!#b\r\n#L0#무공님을 만나고 싶다. 중요한 임무다.");
            break;
        }
        case 1: {
            qm.sendSimple("그렇다면 무릉도장에 도전해라! 무공님은 최상층에서 항상 도전자를 기다리고 계신다!#b\r\n#L0#... 뒷길로 슬그머니 무공님을 뵐 방법은 없을...");
            break;
        }
        case 2: {
            qm.sendSimple("기각! 무슨말을 하는 거냐! 신성한 도장에서 감히 그런 야비한 방법을 쓰려 하다니!#b\r\n#L0#... 몸에 좋은 도라지...");
            break;
        }
        case 3: {
            qm.sendSimple("...헉!...#b\r\n#L0#몸에 매우 좋은 도라지...");
            break;
        }
        case 4: {
            qm.askAcceptDecline("..아, 안 된다! 절대로 안 돼!#b\r\n\r\n(... 흠. 험. 허허험. 자, 잠시 이리로...)");
            break;
        }
        case 5: {
            qm.sendNext("(작은 목소리로) 정말 몸에 좋은 음식을 가져다 주는 게 맞겠지? 직접 가고 싶어도 도장일이 바빠서 갈 시간이 있어야 말이지... 스승님은 스승님대로 수련에만 신경쓰시고,");
            break;
        }
        case 6: {
            qm.sendNextPrev("(작은 목소리로) 아, 아무튼 정말 가져다 주는 거다? 개수는 정확히 100개! 그래, 도라지 100개 이 이하로는 안움직일거야!");
            break;
        }
        case 7: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}