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
            qm.sendNext("#b#h0##k님 정말 감사드려요. 저희 왕국을 위기에서 구하신 영웅이세요. 머라고 말씀을 드려야 할지 모르겠네요. 그리고, 말씀 드리기 부끄럽지만 제가 이렇게 얼굴을 못 보여 드리는 점 용서하세요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("제 입으로 말씀드리긴 조금 그렇지만, 어릴 적 부터 저의 미모에 반한 남성들 때문에 여태껏 가족 외에 얼굴을 보여주지 않고 살다보니 그렇답니다. 심지어 여자라고 하더라도 얼굴을 보여 주기가 어색해 졌답니다. 그렇긴 해도 용사님에게 계속 뒷 모습만 보여드리는 것도 너무 실례인 것 같고.... 용기를 내어 정면에서 인사 드리도록 할께요.");
            break;
        }
        case 2: {
            qm.sendNextPrevS("아 네... #b(와우~ 도대체 얼마나 미인이길래?)#k", 2);
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(헉!........)#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrevS("#b(이... 이건... 설마 버섯들의 세계에서만 미인인 것인가!)#k", 2);
            break;
        }
        case 5: {
            qm.sendNextPrev("정말 부끄럽네요. 아무튼 정말 감사드립니다. #b#h ##k님.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 3,000 exp\r\n#fUI/UIWindow.img/QuestIcon/11/0# 감성 -5");
            break;
        }
        case 6: {
            qm.dispose();
            qm.showNpcSpecialActionByTemplateId(1300002, "face");
            qm.gainExp(1000);
            qm.getPlayer().getTrait(Packages.client.MapleTrait.MapleTraitType.sense).addExp(-5);
            qm.forceCompleteQuest();
            break;
        }
    }
}