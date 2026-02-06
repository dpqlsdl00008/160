var status = -1;
var beatrice = [
["전사", "주먹펴고 일어서", 102000003, 1401], 
["마법사", "하인즈", 101000003, 1402], 
["도적", "다크로드", 103000003, 1404], 
["해적", "카이린", 120000101, 1405]
];

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 4) {
            qm.dispose();
            return;
        }
        if (status == 2) {
            status = 4;
        } else {
            status--;
        }
    }
    switch (status) {
        case 0: {
            qm.sendNext("안녕하세요, #h #... 마이에게 당신의 이름은 많이 들어 보았답니다. 궁수에 관심이 있으시다고 들었어요. 궁수 전직교관 헬레나라고 합니다. 만나서 반가워요...");
            break;
        }
        case 1: {
            qm.sendNextPrev("궁수에 대해서는 얼마나 알고 계신가요? 궁수는 활이나 석궁을 이용해 원거리에서 적을 공격하는 직업이지요... 이동속도는 비교적 느린 편이지만 날카로운 화살이 적을 빗나가는 법은 없지요. 한 발 한 발이 위협적이랍니다.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("진정 당신이 궁수가 되길 원하신다면 전직관의 특권을 이용해 당신을 #b헤네시스의 궁수 교육원#k으로 초대하겠어요. #r그러나 다른 직업이 필요하시다면 거절하셔도 된답니다. 다른 길을 갈 수 있도록 도와 드릴게요...#k 궁수가 되시겠어요?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(100000201, 0);
            break;
        }
        case 4: {
            var say = "다른 길을 택하셨군요... 아쉽지 않은 것은 아니나 이 또한 당신의 선택... 그럼 궁수가 아닌, 어떤 길을 원하시나요?#b";
            for (i = 0; i < beatrice.length; i++) {
                say += "\r\n#L" + i + "#" + beatrice[i][0];
            }
            qm.sendSimple(say);
            break;
        }
        case 5: {
            jgys = selection;
            qm.sendAcceptDecline(beatrice[selection][0] + (selection < 2 ? "를" : "을") + "" + beatrice[selection][1] + "");
            break;
        }
        case 6: {
            qm.dispose();
            qm.forceStartQuest(beatrice[jgys][3]);
            qm.warp(beatrice[jgys][2], 0);
            break;
        }
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.sendNextS("아직 준비할 게 있나요? 그렇다면 서두르지 마세요.", 2);
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendAcceptDecline("궁수 교육원에 잘 오셨어요. 이렇게 직접 얼굴을 맞대고 말할 수 있게 되어 기쁘군요... 그럼 당신을 궁수로 만들어 드리지요.");
            break;
        }
        case 1: {
            if (qm.isQuestFinished(1403) == false) {
                qm.changeJob(300);
                qm.getPlayer().resetStats(4, 4, 4, 4);
                qm.gainItem(1452051, 1);
                qm.gainItem(2060000, 2000);
		qm.gainItem(2431876, 1);
                qm.gainItem(2431877, 1);
                qm.gainItem(1142107, 1);
                qm.forceCompleteQuest();
            }
            qm.sendNext("이제 당신은 궁수입니다. 궁수로서 새로운 스킬을 익히고 싶다면 스킬창을 열어보세요. 당신에게 약간의 SP를 드렸으니 스킬을 올릴 수 있을 꺼에요.");
            break;
        }
        case 2: {
            qm.sendNextPrev("스킬만으로는 부족하죠? 스탯 또한 궁수에 어울리게 변경하시는게 좋겠어요. 궁수는 DEX를 중심 스탯으로 STR을 보조 스탯으로 여긴답니다. 스탯을 어느정도 올려야 할지 모르겠다면 #b자동 배분#k을 사용해 보세요.");
            break;
        }
        case 3: {
            qm.sendNextPrev("아... 그리고... 당신께 작은 선물을 드렸어요. 당신의 장비, 소비, 아이템 보관함 갯수를 늘려 드렸답니다. 좋은 아이템으로 인벤토리를 채워보세요.");
            break;
        }
        case 4: {
            qm.sendNextPrev("그리고 한 가지, 주의해야 할 점이 있어요. 궁수가 된 당신은 전투에 있어 좀 더 주의해야 한답니다. 죽게 되면 그동안 쌓았던 경험치가 깎일 수 있으니까요. 초보자와 다른 점이죠... 잊지 마세요.");
            break;
        }
        case 5: {
            qm.sendNextPrev("제가 가르쳐 드릴 수 있는건 여기까지 입니다. 당신에게 궁수의 무기를 드렸으니 이곳 저곳 여행을 하면서 자기 자신을 단련시키도록 하세요.");
            break;
        }
        case 6: {
            qm.dispose();
            break;
        }
    }
}