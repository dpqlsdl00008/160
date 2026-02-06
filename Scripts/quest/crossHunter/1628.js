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
            if (qm.isQuestActive(1628) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNext("휴우, 큰일 날 뻔 했군. 그런 표정하지마. 바보 같아 보여.");
            break;
        }
        case 1: {
            var sense = qm.getPlayer().getTrait(Packages.client.MapleTrait.MapleTraitType.sense).getExp();
            var addString = "";
            if (sense > 0) {
                addString = " + " + sense + "(감성 추가 경험치)";
            }
            qm.sendNextPrev("의도하지 않게 생명의 은인이 되었군. 내.가!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1112677# #z1112677#\r\n#i2000025# #z2000025# 50개\r\n#i2000028# #z2000028# 50개\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 77790" + addString + " exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainExp(77790 + sense);
            qm.gainItem(1112677, 1);
            qm.gainItem(2000025, 50);
            qm.gainItem(2000028, 50);
            qm.showFieldEffect(false, "crossHunter/chapter/end1");
            qm.forceCompleteQuest();
            break;
        }
    }
}