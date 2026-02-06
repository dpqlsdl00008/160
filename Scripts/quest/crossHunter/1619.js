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
            qm.sendYesNo("당신의 임무는 메이플 월드 곳곳에 나타난 푸른 빛 미스틱 게이트의 정체를 밝히는 일이에요. 크로스 헌터로서의 임무를 본격적으로 시작해볼까요?");
            break;
        }
        case 1: {
            qm.sendNext("우선 루더스 호수 지역에 나타난 미스틱 게이트의 조사를 맡아주세요. 그 곳을 담당하는 #b양#k을 만나보세요. 그는 #r시간의 갈림길#k을 조사해 본다고 했는데 아마 그 곳에 있을 거에요.");
            break;
        }
        case 2: {
           qm.sendYesNo("지금 바로 출발해 주세요.");
           break;
        }
        case 3: {
           qm.sendNext("그럼, 건투를 빌어요.");
           break;
        }
        case 4: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}