var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 2) {
            qm.sendNext("흠... 늑대 라이딩에 관심이 없는 거야? 늑대를 불편하게 만들 거라고 생각하는 거라면 오해인데, 늑대들도 예티처럼 등 위에 누군가를 얹고 다니는 걸 좋아한다고.");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("이야, 당신이 데리고 있는 그거 늑대 맞지? 늑대를 데리고 다니는 사람은 정말 오랜만에 보는군. 그런데... 늑대를 데리고 있으면서 #b늑대 라이딩#k은 하지 않는다니 혹시 늑대 라이딩에 대해 모르는 건가?");
            break;
        }
        case 1: {
            qm.sendNextPrev("늑대 라이딩이란 말이지, 늑대의 등을 타고 다니는 것을 말해. 더 빨리 이동 할 수있는 것은 물론 바람을 가르며 라이딩 하다 보면 늑대와 더 깊게 교감 할 수 있는 법이지. 나도 왕년에는 헥터며, 화이트팽이며, 멋지게 라이딩하고 다녔다고!");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("혹시 당신도 늑대 라이딩을 해보고 싶지 않아? 그렇다면 이 스카두르가 도와주도록 하지.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}