var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendAcceptDecline("혹시 안에 들어 갈 작정이라면 생각을 바꾸는 것이 좋을 것 일세. 하지만 정 들어가고 싶다면... 안에서도 살아 남을 수 있을 정도로 강한 자들만이 내 허락 하에 들어 갈 수가 있다네. 더 이상의 피는 보고 싶지 않단 말일세. 어디 보자... 흠...! 자네는 꽤 강해 보이는 군. 어떤가, 저 안으로 들어가고 싶은가?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(211040300, "under00");
            break;
        }
    }
}