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
            qm.sendNext("내 동족들은 그럼 다 사라진 걸까, 마스터? 한 명도 안 남고? 나만 남기고? 수백 년 전의 사악한 자는 왜 동족들을 없애버린 걸까? 왜 나만 남겨둔 걸까 하나도 모르겠어...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b미르...#k", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("그렇지만 나, 포기는 안 할 거야. 나 씩씩한 용인걸. 내가 살아있는 것처럼, 어딘가에 아직 살아남은 동족이 있을지도 모르잖아. 그런 사람을 꼭 찾고 말겠어. 마스터가 찾아줄거지?\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 3,000 exp\r\n#fUI/UIWindow.img/QuestIcon/10/0# 2 sp");
            break;
        }
        case 3: {
            qm.dispose();
            qm.gainExp(3000);
            qm.getPlayer().gainSP(2, Packages.constants.GameConstants.getSkillBook(qm.getJob()));
            qm.sendOk("에헤헷, 좋아. 그럼 그 하인즈라는 인간이 뭔가 찾아내기를 기다리면서 우리는 우리대로 강해지자. 엄청 강해져서 사람들을 도와주고 영웅이 되자고.");
            qm.forceCompleteQuest();
            break;
        }
    }
}