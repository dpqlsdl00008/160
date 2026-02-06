var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == -1) {
            qm.sendNext("앗, 안 할 거야? 안 할 거야? 아... 아쉽다.");
            qm.dispose();
        }
        if (status == 0) {
            qm.sendOk("#b60레벨#k 달성을 축하드립니다! 여기까지 정말 숨차게 달려오셨죠? 더~ 힘내시라고, #b12월20일부터 12월31일#k까지 #e50레벨, 55레벨, 60레벨, 65레벨, 70레벨, 75레벨, 80레벨, 85레벨, 90레벨, 95레벨, 100레벨#n을 달성하시는 분들께 특별선물을 드리고 있습니다. 지금 받으시겠어요?\r\n#i2450038# 레전드, 힘내세요! 3개\r\n\r\n\r\n아자아자! 더 힘내서 한별스토리와 함께해주세요~!"); 
	    qm.gainItem(2450038, 3);
	    qm.forceCompleteQuest();
	    qm.dispose();
        } else {
            qm.dispose();
        }
    }
}