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
            qm.sendNext("...이제 당신의 능력도 어느 정도 모양새를 갖춰가는 것이 눈에 보이는군... 허허... 이 늙은이가 여기까지 해낼 수 있을 줄은 몰랐소. 정말이지 감동으로 눈물이... 아니 콧물이...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(...별로 오래 수련하지도 않았잖아...)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("자, 그럼 세 번째이자 마지막이 될 수련을 시작하겠소. 이번 수련의 대상은 바로... #r#o9300343##k! #o1210100#! 그들에 대해 알고 있으시오?!");
            break;
        }
        case 3: {
            qm.sendNextPrevS("약간은...", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("그들은 타고난 전사! 태어날 대부터 음식에 관한 무한한 분노를 가진 그들이 지나간 자리에 남는 음식은 전혀 없다고 하오! 실로 무시무시한 일 아니오?");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b(진담으로 하는 말일까?)#k", 2);
            break;
        }
        case 6: {
            qm.sendAcceptDecline("자, 어서 #b다시 한 번 수련장#k으로 들어가 타고난 전사인 수련용 돼지 30마리를 퇴치하여 당신의 능력을 한껏 펼쳐보시오! 당신이 사용할 수 있는 모든 힘을 쏟아 부어 보시오! 그리하여 이 스승을 뛰어넘으시오!");
            break;
        }
        case 7: {
            qm.sendNext("자, 가시오! 가서 #o9300343#를 마음껏 물리치시오!");
            break;
        }
        case 8: {
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
            qm.sendNext("#o9300343# 30마리를 모두 퇴치하고 돌아왔군... 역시 이 늙은이의 눈이 틀리지 않았어. 기억을 잃었어도! 능력을 잃었어도! 당신이야말로 진정한 영웅이요! 왜냐하면, 폴암을 들었으니까!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(재미 있으라고 하는 말이겠지?)#k'", 2);
            break;
        }
        case 2: {
            qm.sendYesNo("더 이상 당신에게 가르칠 것이 없소. 당신은 이미 이 늙은이를 뛰어넘었소. 하산해도 좋소... 크윽. 망설일 것 없소. 이 늙은이는 잠시나마 당신의 스승이었다는 사실만으로도 만족하오.");
            break;
        }
        case 3: {
            qm.sendNextS("(경험치가 차곡차곡 쌓이는 걸 보니 의외로 효과가 있는 모양이다. 치매가 걱정되는 노인에게 받은 수련이라 효과가 있을까 걱정했는데, 정말 효과가 있었다.)", 2);
            qm.delayEffect("Effect/BasicEff.img/AranGetSkill", 1);
            break;
        }
        case 4: {
            qm.sendNextPrev("자, 이제 그만 #p1201000#님에게로 돌아가시오. 그 분이 당신의 성과를 크게 기뻐하실 것이오!");
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.teachSkill(21000000, qm.getPlayer().getTotalSkillLevel(21000000), 10);
            qm.gainExp(2800);
            break;
        }
    }
}