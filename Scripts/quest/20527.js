var status = -1;

function start(mode, type, selection) {
    if (mode == 0 && status == 2) {
        qm.dispose();
        return;
    } else if (mode == 0) {
        status--;
    } else {
        status++;
    }

    if (status == 0) {
        qm.sendNext("이런이런... 레벨 100이 되었는데 아직도 라이딩이 일반 티티아나 입니까? 제가 다 얼굴이 화끈거리는군요. 이래서야 상급기사의 품격이 제대로 유지되고 있다고 말할 수 있겠습니까?");
    } else if (status == 1) {
        qm.sendAcceptDecline("누누이 말씀드리지만 당신의 행동이 여제의 명예에까지 영향을 줍니다. 제발 여제를 위해서라도 제대로 행동해 주세요. #b키리두#k에게 가면 #b더 강력한 라이딩#k에 대해 알려줄 겁니다.");
    } else if (status == 2) {
        qm.sendOk("키리두의 위치를 설마 잊지는 않으셨겠죠? ...혹시 잊으신 겁니까? 티티아나를 기르기 위해 #b키리두의 부화장#k에 있습니다.");
        qm.forceCompleteQuest();
        qm.dispose();
    }
}


function end(mode, type, selection) {
    if (mode == 0 && status == 2) {
        qm.dispose();
        return;
    } else if (mode == 0) {
        status--;
    } else {
        status++;
    }

    if (status == 0) {
        qm.sendNext("어떤가? 티티아나의 알은 잘 크고 있는가?");
    } else if (status == 1) {
        var qr = qm.getQuestRecord(20514);
        var info = qr.getCustomData();
        if (info == null) {
            qr.setCustomData("0");
        }
        if (java.lang.Integer.parseInt(qr.getCustomData()) >= 510000) {
            qm.sendNextPrev("오, 티티아나의 알을 깨운 건가? 대단한데? 어지간한 기사들도 이렇게 빠르긴 힘든데 말이야.");
            qm.forceCompleteQuest();
            qm.forceCompleteQuest(20514);
            qm.gainItem(4220137);
            qm.dispose();
        } else {
            qm.sendNextPrev("흠, 아직 티티아나의 알을 깨우지 못한거 같은데?");
            qm.forceCompleteQuest();
            qm.dispose();
        }
    }
}