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
            cm.resetMap(cm.getMapId());
            cm.lockInGameUI(true);
            cm.showInfoOnScreen("잡화 상점 뒷 마당");
            cm.showDirectionEffect("Effect/Direction7.img/effect/tuto/step0/4", 2000, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 1: {
            cm.sendNextS("어? 누군가 있네! 저 소녀는 누구지?", 3);
            break;
        }
        case 2: {
            cm.userEmotion(6, 10000, false);
            cm.forcedInput(2);
            cm.sendDelay(4500);
            break;
        }
        case 3: {
            cm.forcedInput(0);
            cm.sendNextS("너는 누구니? 길을 잃었니?", 3);
            break;
        }
        case 4: {
            cm.sendNextPrevS("당신을 아주 오래 찾아 헤맸어요. 겨우 찾았네요. 빛의 운명을 가진 사람.", 5, 1106001);
            break;
        }
        case 5: {
            cm.sendNextPrevS("무슨 소리니? 빛의 운명이라니?", 3);
            break;
        }
        case 6: {
            cm.sendNextPrevS("예의를 갖추게 소년! 이 분은 고귀한 분이시다!", 5, 1106003);
            break;
        }
        case 7: {
            cm.sendNextPrevS("앗! 당신은 얼마 전에 가게에 오셨던... 맞다! 기억났어요. 당신이 말했었죠? 크롬에 대해서 아냐고. 얼마 전에 가게 다락방에서 그가 보낸 편지를 봤어요. 그 사람이 맞는 지는 모르겠지만... 림버트씨라면 그 사람에 대해 아실지도 모르겠어요. 림버트씨가 돌아오시면...", 3);
            break;
        }
        case 8: {
            cm.sendNextPrevS("크롬... 그 분은 림버트씨가 아니라 당신과 관련이 있으신 분이에요. 바로 당신의 아버지니까요...", 5, 1106001);
            break;
        }
        case 9: {
            cm.sendNextPrevS("에? 뭐라고? 아니... 뭐라구요? 하지만 난 아버지에 대한 기억이 없어요. 그는 어린 날 이 곳에 버리고 떠났다구요...", 3);
            break;
        }
        case 10: {
            cm.sendNextPrevS("그는 당신을 버린 것이 아니에요. 당신의 아버지인 빛의 기사 크롬은 사랑하는 아내를 잃은 슬픔 때문에 어둠 속에 갇혀버렸죠. 빛과 어둠은 아주 작은 차이니까요. 그 어둠이 당신까지 파멸로 이끌기 전에 당신을 이 곳에 맡긴 것 뿐이에요. 결국 그는 그 어둠 속에서 빠져나오지 못한 채 생을 마감했지만요...", 5, 1106001);
            break;
        }
        case 11: {
            cm.sendNextPrevS("날 구했다고요? 틀렸어요. 내 삶도 어둠이었어요. 지금까지 난 이름도 없이 이 작은 가게에 갇혀서 돌아오지도 않는 아버지를 마음 속으로 기다리면서 살아가고 있었다고요.", 3);
            break;
        }
        case 12: {
            cm.sendNextPrevS("빛은 어둠 속에서 태어나는 법이죠. 양날의 검처럼 말이에요... 그 어둠 속에 있었기 때문에 당신은 이제 빛이 될 수 있을 거에요. 나와 함께 가요. 당신이 있어야 할 자리로요.", 5, 1106001);
            break;
        }
        case 13: {
            cm.sendNextPrevS("잠시만요, 여제님. 전 아직도 이 소년에 대한 확신이 없습니다. 그가 진정한 빛의 기사가 될 자격이 있는지 확인을 해봐야겠습니다.", 5, 1106003);
            break;
        }
        case 14: {
            cm.sendNextPrevS("나인하트는 아직도 의심을 하고 계시군요... 좋아요. 당신의 방식을 허용하겠어요. 하지만 그가 다쳐서는 안됩니다.", 5, 1106001);
            break;
        }
        case 15: {
            cm.sendNextPrevS("잠깐만요! 당신들 지금 무슨 짓을 하려는 거에요?", 3);
            break;
        }
        case 16: {
            cm.summonEffect(0, 240, 65);
            cm.sendDelay(3000);
            break;
        }
        case 17: {
            cm.dispose();
            cm.forceCompleteQuest(20034);
            cm.forceStartQuest(20035);
            for (var i = 0; i < 10; i++) {
                cm.spawnMonster(9001050, 240, 65);
            }
            cm.lockInGameUI(false);
            break;
        }
    }
}