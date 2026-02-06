var status = -1;

function start() {
    action(1, 0, 0);
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
            var v1 = true;
            if (cm.isQuestActive(3178) == false) {
                v1 = false;
            }
            if (cm.haveItem(4032839, 1) == true) {
                v1 = false;
            }
            if (!v1) {
                cm.dispose();
                cm.sendNext("\r\n그만 돌아가라. 지금은 싸울 기분이 아니다...");
                return;
            }
            cm.sendNextS("\r\n넌... 어떻게 이 길을 알고 있는 거지? 이 길은 왕족, 나와 이피아 외에는 모르는 길인데... 또 다시 이피아를 만났다는 거짓말을 할 생각인가?", 8);
            break;
        }
        case 1: {
            cm.sendNextPrevS("\r\n정말로 이피아를 만났어요. 이번에는 이피아와 함께 오기까지 했다고요. 당신이 직접 이피아와 말해보세요.", 2);
            break;
        }
        case 2: {
            cm.sendNextPrevS("\r\n레온, 내가 보이지 않나요? 내 목소리가 들리지 않는 건가요?", 4, 2161009);
            break;
        }
        case 3: {
            cm.sendNextPrevS("\r\n무슨 말을 하는 거냐... 이피아가 어디 있다는 거지? 날 놀리려는 의도인가?", 8);
            break;
        }
        case 4: {
            cm.sendNextPrevS("\r\n이피아의 목소리가 들리지 않는 건가요? 어째서...? 왜 당신에게는 그녀의 목소리가 닿지 않는 거죠?", 2);
            break;
        }
        case 5: {
            cm.sendNextPrevS("\r\n마치 진실을 말하는 것 같은 표정이군. 아니... 네 말이 진실 일 지도 모르지. 이피아가 여기에 있고, 또 내게 말을 걸고 있을 지도 몰라. 하지만 그게 무슨 소용이지? 내 손은 이미 더러워져 있는데...", 8);
            break;
        }
        case 6: {
            cm.sendNextPrevS("\r\n... 왜 그런 슬픈 말을...", 4, 2161009);
            break;
        }
        case 7: {
            cm.sendNextPrevS("\r\n아아... 그래서 일지도 모르겠군. 내가 검은 마법사에게 영혼을 팔았기 때문에... 너무 많은 사람을 죽였기 때문에 그녀의 목소리를 들을 수 없는 것 일 지도 몰라. 이것이 내 죄의 대가인가...", 8);
            break;
        }
        case 8: {
            cm.sendAcceptDeclineS("\r\n이피아를 알고 있는 자여. 이것을 받아다오.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i4032839# #b#z4032839# 1개#k", 8);
            break;
        }
        case 9: {
            cm.sendNextS("\r\n오래 전, 궁정 화가가 그려준 이피아의 그림이 담긴 펜던트이지... 이걸 바라보며 그녀를 추억해 왔지만, 이제 이런 물건은 내게 어울리지 않아.", 8);
            break;
        }
        case 10: {
            cm.sendNextPrevS("\r\n영혼을 바쳐 복수를 마쳤다. ... 남은 것 따위는 없군. 이런 내게 그녀를 추억 할 자격 조차 없어...", 8);
            break;
        }
        case 11: {
            cm.sendNextPrevS("\r\n다시 한 번 그 때로 돌아갈 수 있다면 이런 선택을 하지 않았을까? 수만 번 생각해 봤지만 알 수 없어. 분노와 허무... 무엇을 택해도 결국 돌아오는 것은 아무 것도 없으니까.", 8);
            break;
        }
        case 12: {
            cm.sendNextPrevS("\r\n그만 돌아가라. 지금은 싸울 기분이 아니다...", 8);
            break;
        }
        case 13: {
            cm.dispose();
            cm.gainItem(4032839, 1);
            break;
        }
    }
}