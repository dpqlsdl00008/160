var status = -1;

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
            if (qm.isQuestActive(21757) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendYesNo("무슨 일이십니까? 기사가 될 사람이 아니면 그리 달갑지 않은데요. 흠... 그건 뭡니까? 여제께 그걸 드리려는 겁니까? 위험 물질일지도 모르는데 그럴 수는 없죠. 자, 이리 주십시오. 제가 먼저 보죠.");
            }
            break;
        }
        case 1: {
            qm.sendNext("...흠...꽤 흥미로운 이야기가 적혀 있군요. 신수의 눈물에 대해 알고 있다니... 아무것도 아닙니다. 신중하게 검토해 보도록 하지요.");
            break;
        }
        case 2: {
            qm.sendNextPrevS("블랙윙이 이곳을 노릴지도 모른다.", 2);
            break;
        }
        case 3: {
            qm.sendNextPrev("그렇다 하더라도 그건 에레브의 일입니다. 외부인인 당신이 상관 할 바가 아니죠. 애초에 당신이 블랙윙이 아니라는 보장은 어디 있습니까? ...정보는 감사합니다만 이만 안녕히 가세요.");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}