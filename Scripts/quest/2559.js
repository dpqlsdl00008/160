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
            qm.sendNext("안녕하세요. 모험가님, 오늘은 알려 드릴 게 있어서 이렇게 연락을 드렸어요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("오늘은 유독 하늘이 푸르지 않나요? 저 하늘을 날아 보고 싶어 질 만큼. 저 하늘을 날 수 있다면 얼마나 기분이 좋을까요?");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("뜬금 없이 무슨 얘기냐구요? 후훗, 저 하늘을 날 수 있는 방법이 있다면 흥미가 좀 가시나요?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
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
            qm.sendNext("음, 메이플 운영자의 소개로 왔다고? 그래, 자네도 비행기에 관심이 있나?");
            break;
        }
        case 1: {
            qm.sendNextPrev("비행기가 뭐냐고? 비행기는 메이플 월드의 새로운 이동 수단일세. 다른 라이딩처럼 타고 여기 저기 돌아 다니는 것도 가능하지만, 다른 대륙으로 날아 다닐 수 있는 기능이 있다네.");
            break;
        }
        case 2: {
            qm.sendNextPrev("물론, 모든 대륙을 날아 갈 수 있는 건 아니야. #b오르비스#k에서 #b빅토리아 아일랜드#k, #b에레브#k, #b에델슈타인#k, #b루디브리엄#k, #b아리안트#k, #b무릉#k, #b리프레#k로 날아 갈 수 있지. 물론, 반대로도 갈 수 있다네. 그리고 #b빅토리아 아일랜드#k와 #b에델슈타인#k 사이도 비행기로 날아 갈 수 있지. 다른 곳은 비행기로 이동하기엔 너무 위험해서 날 수 없으니 기억해 두게.");
            break;
        }
        case 3: {
            qm.sendNextPrev("비행기로 다른 대륙으로 가고 싶다면 정거장에 있는 #b정거장 안내원#k에게 말을 걸면 되네.");
            break;
        }
        case 4: {
            qm.sendNextPrev("여기까지, 질문 있나?");
            break;
        }
        case 5: {
            qm.sendNextPrev("좋아, 이것으로 비행기에 대한 설명을 끝내지. 자, 이것은 내가 주는 선물일세.");
            break;
        }
        case 6: {
            qm.dispose();
            qm.teachSkill(80001027, 1, 1);
            qm.forceCompleteQuest();
            break;
        }
    }
}