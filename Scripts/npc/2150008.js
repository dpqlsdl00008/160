function start() {
    if (cm.isQuestActive(23121) == true) {
        cm.dispose();
        cm.forceCompleteQuest(23121);
        return;
    }
    var say = "에델슈타인을 떠나 다른 대륙으로 가 볼 생각이야? 빅토리아 아일랜드와 오시리아의 오르비스 지역까지 운항하고 있어. 요금은 800메소지. 어디로 갈래?#b";
    say += "\r\n#L0#빅토리아 아일랜드";
    say += "\r\n#L1#오르비스";
    cm.sendSimple(say);
}

function action(mode,type,selection) {
    cm.dispose();
    if (mode == 1) {
        if (cm.getMeso() < 800) {
            cm.sendNext("\r\n메소가 충분하지 않은 걸?");
            return;
        }
        cm.gainMeso(-800);
        switch(selection) {
            case 0: {
                cm.warp(104020130, 0);
                break;
            }
            case 1: {
                cm.warp(200000170, 0);
                break;
            }
        }
    }
}