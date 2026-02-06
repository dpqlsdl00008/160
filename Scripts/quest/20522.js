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
        qm.sendNext("기사들의 라이딩은 일반적인 라이딩과는 조금 다르다네. 섬에서 자라는 티티의 일종인 티티아나를 통해 이루어지지. 몬스터가 아니라 티티아나를 타고 다니는 게야. 여기서 간과해서는 안 될 게 하나 있네.");
    } else if (status == 1) {
        qm.sendNextPrev("라이딩은 단순한 탈 것, 이동수단으로 생각해서는 안 된다네. 라이딩은 친구, 동료, 전우... 그 모든 것이 될 수 있지. 때로는 목숨마저 맡겨야 하는 상대가 라이딩이 아니겠는가? 그래서 에레브의 기사들은 자신의 라이등을 직접 기르게 하고 있다네.");
    } else if (status == 2) {
        qm.sendAcceptDecline("자, 자네에게 티티아나의 알을 하나 주겠네. 티티아나를 길러 평생을 함께 할 자네의 라이딩으로 할 준비가 되었는가?");
    } else if (status == 3) {
        qm.forceStartQuest();
        qm.forceStartQuest(20514, "0");
        qm.gainItem(4220137, 1);
        qm.sendOk("티티아나의 알은 #b자네와 경험을 공유#k함으로써 기를 수 있다네. 티티아나가 완전히 성장하거든 다시 데려와 주게.");
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