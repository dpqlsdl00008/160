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
            qm.sendNext("그것봐요. 나인하트씨. 이 분은 빛의 기사의 피를 이어받았고, 그만한 자격이 있음을 이렇게 훌륭하게 증명하셨잖아요.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("네. 항상 그렇듯이 여제님의 말씀이 맞는 것 같습니다. 아직 많이 부족하긴 하지만 이 소년에게는 빛의 기사의 피를 이어나갈 자격이 있는 것 같군요.", 4, 1106000);
            break;
        }
        case 2: {
            qm.sendNextPrevS("우리 아버지가 빛의 기사였다고요? 내가 빛의 기사가 될 거라는 건가요? 나는 그냥 평범한 소년일 뿐이에요. 그 흔한 이름조차 없는...", 2);
            break;
        }
        case 3: {
            qm.sendAcceptDecline("선택은 당신의 몫입니다. 하지만 빛을 가지고 태어난 자신의 운명을 거스르지 않으셨으면 좋겠어요. 당신을 위해서 그리고 이 메이플 월드를 위해서요.");
            break;
        }
        case 4: {
            qm.sendNextS("당신에게 이름이 필요할 것 같군요. '빛으로 태어난 사람'이라는 뜻의 '#e#b미하일#k#n'이 어떤가요? 당신에게 정말 잘 어울리는 것 같아요. 이제 저와 함께 에레브로 가요. 새로운 빛이 시작되기에 그 곳만큼 좋은 곳은 없으니까요.", 1);
            break;
        }
        case 5: {
            qm.dispose();
            qm.lockInGameUI(false);
            while (qm.getPlayer().getLevel() < 10) {
                qm.getPlayer().levelUp();
            }
            qm.gainItem(1302182, 1);
            qm.getPlayer().changeJob(5100);
            qm.getPlayer().resetStats(4, 4, 4, 4);
            qm.warp(913070071, 0);
            qm.gainItem(2431876, 1);
            qm.gainItem(2431877, 1);
            qm.forceCompleteQuest();
            break;
        }
    }
}