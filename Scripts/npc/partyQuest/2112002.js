var status = -1;

function start() {
    switch (cm.getMapId()) {
        case 926100600:
        case 926110600: {
            status = 4;
            break;
        }
        default: {
            status = -1;
            break;
        }
    }
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (cm.isLeader() == false) {
                cm.dispose();
                cm.sendNext("\r\n이럴수가... 내가 그동안 쌓아 온 모든 것을 잃고 말았어... 자네들의 대표와 이야기하고 싶네...");
                return;
            }
            cm.mapMessage(6, "안경을 벗은 유레테는 어느 정도 정신을 차린 모습이다.");
            cm.sendSimple("이럴 수가... 내가 그 동안 쌓아온 모든 것을 잃고 말았어...#d\r\n#L0#1. 잃은 건 아무 것도 없어요. 당신이 살아 있으니 무엇이든 다시 시작 할 수 있습니다.");
            break;
        }
        case 1: {
            cm.mapMessage(6, cm.getPlayer().getName() + "님의 설득에 유레테는 마음이 흔들리는 모습이다.");
            cm.sendNextPrev("\r\n그런가... 정말 지금부터 다시 시작 할 수 있단 말인가?");
            break;
        }
        case 2: {
            cm.mapMessage(6, "유레테는 마음을 고쳐먹고, 마가티아에 협력하겠다고 말한다.");
            cm.sendNextPrev("\r\n그럼 자네들을 다음 장소로 보내주겠네.");
            break;
        }
        case 3: {
            cm.dispose();
            cm.warpParty(cm.getMapId() + 100);
            break;
        }
        case 4: {
            cm.dispose();
            break;
        }
        case 5: {
            cm.sendSimple("제뉴미스트의 구슬과 알카드노의 구슬을 각각 5개씩 가져오시면 호루스의 눈을 만들어 주겠네.\r\n#d#L0#1. 호루스의 눈을 만들어 주세요.");
            break;
        }
        case 6: {
            if (cm.haveItem(4001159, 5) == false || cm.haveItem(4001160, 5) == false) {
                cm.dispose();
                return;
            }
            cm.askAcceptDecline("자네는 #b제뉴미스트 구슬 " + cm.itemQuantity(4001159) + "개#k와 #b알카드노 구슬 " + cm.itemQuantity(4001160) + "개#k를 가지고 있군. 정말 #r#z1122010##k을 만들겠는가?");
            break;
        }
        case 7: {
            cm.sendNext("\r\n자, 제뉴미스트와 알카드노... 두 힘의 정수가 담긴 호루스의 눈이라네. 잘 사용해 주리라 믿네. 자네에게 도움이 되었으면 좋겠군. 분열된 마가티아의 내전도 이 목걸이처럼 다시 화합되길.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#d#i1122010# #z1122010# 1개#k");
            break;
        }
        case 8: {
            cm.sendNextPrev("\r\n다시 한 번 나의 도움이 필요하다면 언제든 찾아와 주게. 자내라면 언제라도 환영하겠네.");
            break;
        }
        case 9: {
            cm.dispose();
            cm.gainItem(4001159, -5);
            cm.gainItem(4001160, -5);
            cm.gainItem(1122010, 1);
            break;
        }
    }
}