var status = -1;

function start() {
    status = -1;
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
            cm.lockInGameUI(true);
            cm.showInfoOnScreen("메이플력 XXXX년 X월 X일");
            cm.showInfoOnScreen("림버트씨의 잡화 상점");
            cm.sendDelay(1000);
            break;
        }
        case 1: {
            cm.userEmotion(6, 10000, false);
            cm.showDirectionEffect("Effect/Direction7.img/effect/tuto/step0/0", 2000, 30, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 2: {
            cm.showDirectionEffect("Effect/Direction7.img/effect/tuto/step0/1", 2000, 30, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 3: {
            cm.userEmotion(4, 8000, false);
            cm.showDirectionEffect("Effect/Direction7.img/effect/tuto/step0/2", 2000, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 4: {
            cm.forcedInput(1);
            cm.sendDelay(1000);
            break;
        }
        case 5: {
            cm.forcedInput(0);
            cm.showDirectionEffect("Effect/Direction7.img/effect/tuto/step0/3", 2000, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 6: {
            cm.forcedInput(1);
            cm.sendDelay(1000);
            break;
        }
        case 7: {
            cm.forcedInput(0);
            cm.sendNextS("저...저에게 할 말이 있으신가요?", 3);
            break;
        }
        case 8: {
            cm.sendNextPrevS("이름이 뭐지요?", 1, 0, 1106000);
            break;
        }
        case 9: {
            cm.sendNextPrevS("전...이름이 없는데요. 그냥 '꼬마'라고 부르세요. 림버트 아저씨도 그렇게 부르니까요. 어떤 물건을 찾으시는데요?", 3);
            break;
        }
        case 10: {
            cm.sendNextPrevS("가족은... 가족은 없나요?", 1, 0, 1106000);
            break;
        }
        case 11: {
            cm.sendNextPrevS("저한테는 가족이 없는데요...\r\n#b(이 사람 누군데 나한테 이런걸 묻는거지? )#k\r\n필요하신 물건이 없다면...전 이만...", 3);
            break;
        }
        case 12: {
            cm.sendNextPrevS("빛의 기사 크롬을 알고 있나요?", 1, 0, 1106000);
            break;
        }
        case 13: {
            cm.sendNextPrevS("크롬이요? 글쎄요...전 잘...\r\n#b(크롬은 누구지? 이 사람은 왜 나한테 이런 걸 묻는거야?)#k", 3);
            break;
        }
        case 14: {
            cm.sendNextPrevS("#e꼬마녀석!\r\n상자를 치우라니까 누구랑 노닥거리고 있는거야!!#n", 5, 1106002);
            break;
        }
        case 15: {
            cm.sendNextPrevS("네...네!! 림버트 아저씨! 지금 치우려고 했어요!\r\n저기 그럼...전...이만...", 3);
            break;
        }
        case 16: {
            cm.userEmotion(6, 10000, false);
            cm.showDirectionEffect("Effect/Direction7.img/effect/tuto/step0/4", 2000, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 17: {
            cm.sendNextPrevS("어... 어디 간거지? 그 사람?\r\n아참! 람버트 아저씨한테 또 혼나기 전에 상자를 치워야 해!", 3);
            break;
        }
        case 18: {
            cm.forcedInput(2);
            cm.sendDelay(1000);
            break;
        }
        case 19: {
            cm.dispose();
            cm.gainExp(18);
            cm.forceCompleteQuest(20030);
            cm.lockInGameUI(false);
            cm.warp(913070001, 0);
            break;
        }
    }
}