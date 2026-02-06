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
            qm.sendNextPrev("사실은 요새 농장의 돼지들이 좀 이상하단다. 괜히 화를 내거나 짜증을 부리는 일이 많아. 그게 걱정돼서 오늘도 일찍 나와봤는데, 아니나 다를까 돼지들 중 몇마리가 밤새 울타리를 뚫고 밖으로 뛰쳐나간 모양이구나.");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("돼지들을 찾아오기 전에 일단 망가진 울타리부터 고쳐놔야 하지 않겠니? 다행이 그렇게 많이 망가진 건 아니라 나무토막만 몇 개 있으면 고칠 수 있을 것 같구나. 에반이 #b나무토막#k을 #b3개#k만 구해다주면 편할 텐데...");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceStartQuest();
            qm.sendNext("오, 정말 고맙구나. #b나무 토막#k은 주변에 있는 #r스텀프#k들에게 구할 수 있단다. 별로 강한 녀석들은 아니지만 혹시라도 방심하다가 위험한 순간이 올 수도 있으니 스킬과 아이템을 잘 사용하도록 하렴.");
            break;
        }
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("오, 나무토막은 다 구해온 거니? 장하구나. 그럼 상으로 뭘 주면 될까... 옳지, 그게 있었지.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i3010097# 튼튼한 나무 의자 1개\r\n#i2022621# 맛있는 우유 15개\r\n#i2022622# 맛있는 주스 15개\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 210 exp");
            break;
        }
        case 1: {
            qm.sendNextPrev("자, 울타리를 만들고 남은 판자로 만든 새 의자란다. 모양은 그래도 튼튼해서 쓸만할 거야. 잘 쓰렴.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainExp(210);
            qm.gainItem(4032498, -3);
            qm.gainItem(3010097, 1);
            qm.gainItem(2022621, 15);
            qm.gainItem(2022622, 15);
            qm.forceCompleteQuest();
            break;
       }
    }
}