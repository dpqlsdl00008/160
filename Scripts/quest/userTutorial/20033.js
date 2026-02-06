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
            if (qm.isQuestActive(20033) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendSimple("계란은 가져온거야? 깨지지는 않았겠지? 잠깐만 뭐야 이 꼴은 무슨 일이 있었어?#b\r\n#L0#저기...울프가 갑자기 공격을 해서...그리고 저...울프가 도망쳐 버렸어요.");
            }
            break;
        }
        case 1: {
            qm.sendNextS("뭐라고! 울프가 도망을 쳐? 이런 변변치 못한 녀석 같으니라구!! 오늘 밥은 없을 줄 알아!! 만약에 울프를 찾지 못하면 네 녀석도 나가버려!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i2000020# #d#z2000020# 30개#k\r\n#i2000021# #d#z2000021# 30개#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 40 exp", 1);
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(4033196, -10);
            qm.gainItem(2000020, 30);
            qm.gainItem(2000021, 30);
            qm.gainExp(40);
            qm.warp(913070004, 0);
            qm.forceCompleteQuest();
            break;
        }
     }
}