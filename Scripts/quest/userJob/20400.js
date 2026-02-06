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
            qm.sendNext("오랜만입니다. 그 동안 정말 강해졌군요. 이제 시그너스 기사단에 당신만큼 강한 기사는 거의 없을 겁니다. 기사 단장들도 당신을 당해내긴 힘들 것 같으니 말입니다. ...빈말은 여기까지 하고, 본론을 이야기 할까요?");
            break;
        }
        case 1: {
            qm.sendNextPrev("새로운 임무입니다. 얼마 전에 들어온 정보에 의하면 #r블랙윙#k의 멤버 중 누군가가 여제를 노리고 있다고 합니다. 그것을 저지하기 위해 기사단의 상급 기사 #b듀나미스#k가 움직이고 있습니다만, 그녀 혼자로는 어려울 것 같군요.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("빅토리아 아일랜드 지역이면 모를까, 오시리아는 기사단의 정보원들도 다 파악하지 못한 곳이라 지원이 필요할 것 같습니다. 당신께서 듀나미스를 서포트 해주십시오. 그녀가 마지막으로 연락한 곳은 #b엘나스#k이니 그곳에서 듀나미스를 찾으면 될 겁니다.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}