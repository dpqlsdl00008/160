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
            qm.sendNext("하아...\r\n\r\n#Cgray#(어쩐지 기운이 없어 보인다.)#k");
            break;
        }
        case 1: {
            qm.sendNextPrevS("셰프님 무슨 문제라도 있으신가요?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("아니.. 그게 아니라해.. 그냥 마음이 너무 아프다해..");
            break;
        }
        case 3: {
            qm.sendNextPrevS("저에게 무슨 일인지 이야기 해 주실 수 있나요? 도움을 드리고 싶어요.", 2);
            break;
        }
        case 4: {
            qm.askAcceptDecline("사실 상해 호텔 근무 당시 최고의 농장을 가꾸었던 농장 주인 #d도민 첸#k이라는 친구가 있었다해. 최상의 고기와 신선한 우유... 모든 식재료는 그의 손을 거치면 훌륭하게 탄생되었네. 농장에 정말 헌신적인 친구였지만 예원 정원에 먹구름이 덮히고 난 후부터는 그가 살아 있는지 죽었는지 조차 모를 정도로 깜깜 무 소식이라네. 자네가 한 번 그를 찾아봐 줄 수 있겠나?");
            break;
        }
        case 5: {
            qm.sendNext("정말 천사같은 사람이다해! 만약 그를 찾게 된다면 그를 안전한 이곳으로 피신 할 수 있도록 도와주게! 나는 그가 이 곳에서 무엇이든 기적과 같은 일을 해낼 수 있다고 믿는다해!");
            break;
        }
        case 6: {
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
            qm.sendNext("음? 거기 누구요! 누가 내 농장에 발을 들인거지?");
            break;
        }
        case 1: {
            qm.sendNextPrev("어디보자 젠 황이 보낸 사람인가?\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 511,795 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(511795);
            break;
        }
    }
}