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
            qm.sendNextS("여기서 뭘 하고 계셨던거죠?", 2);
            break;
        }
        case 1: {
            qm.sendNextPrev("전.. 그저 강시들의 사진을 찍고 싶었을 뿐이었어요.. 하지만 강시들에게 들켜버려 갇히고 말았어요...");
            break;
        }
        case 2: {
            qm.sendNextPrev("당신은 제 생명의 은인이에요... 정말 감사해요!");
            break;
        }
        case 3: {
            qm.sendNextPrev("제 이름은 장 웨이 입니다. 전 상해 일보의 기자이고 이곳에 일어나는 수상한 일들을 낱낱이 파헤칠 거예요!");
            break;
        }
        case 4: {
            qm.askAcceptDecline("#i2030056# #d#z2030056##k\r\n\r\n혹시 예원 정원으로 가는 귀환 주문서를 가지고 있으신가요? 전 잃어 버리고 말았답니다... 정말 여기서 벗어나고 싶어요...");
            break;
        }
        case 5: {
            qm.sendNextS("#Cgray#(그에게 예원 정원 마을 귀환서를 한 장 구해다 주자.)#k", 2);
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
            qm.sendNext("제가 정말 가져도 되는거죠? 드디어... 이곳에서 벗어나다니. 흑흑...\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,705,983 exp");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainItem(2030056, 1);
            qm.gainExp(1705983);
            qm.warp(701100000, 0);
            break;
        }
    }
}