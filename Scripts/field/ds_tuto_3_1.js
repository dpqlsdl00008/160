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
            if (cm.getMap().containsNPC(2159340) == false) {
                cm.spawnNpc(2159340, 175, 0);
                cm.showNpcSpecialActionByTemplateId(2159340, "panic");
            }
            if (cm.getMap().containsNPC(2159341) == false) {
                cm.spawnNpc(2159341, 300, 0);
                cm.showNpcSpecialActionByTemplateId(2159341, "panic");
            }
            if (cm.getMap().containsNPC(2159342) == false) {
                cm.spawnNpc(2159342, 600, 0);
                cm.showNpcSpecialActionByTemplateId(2159342, "summon");
            }
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg2/0", 0, 0, -110, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/3", 2000, new java.awt.Point(0, -170), false, 0, true, 2159340, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/3", 2000, new java.awt.Point(0, -170), false, 0, true, 2159341, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/3", 2000, new java.awt.Point(0, -170), false, 0, true, 2159342, 0, 0);
            cm.sendDelay(1500);
            break;
        }
        case 1: {
            cm.sendNextS("뭐... 뭐야? 뭐야 저건?!", 5, 2159340);
            break;
        }
        case 2: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg2/1", 0, 0, -110, 0, 0);
            cm.sendDelay(1000);
            break;
        }
        case 3: {
            cm.sendNextS("(...어찌된 일이지? 포스가 거의 사라졌다. 옆에 이 물건은... 설마 이 물건이 내 힘을 다 뽑아낸 것인가...?!)", 17);
            break;
        }
        case 4: {
            cm.sendNextPrevS("이... 이런 건 들은 적 없어!", 5, 2159340);
            break;
        }
        case 5: {
            cm.sendNextPrevS("무슨 짓을 한 거냐! 왜 이런 짓을 한 거지? 너희들에게서 느껴지는 그 힘은... 검은 마법사의 힘?!", 17);
            break;
        }
        case 6: {
            cm.sendNextPrevS("저 녀석을 붙잡아야 추궁을 면할 거야!!", 5, 2159340);
            break;
        }
        case 7: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/16", 0, 0, -110, 0, 0);
            cm.sendDelay(1500);
            break;
        }
        case 8: {
            //cm.fadeInOut(600, 1500, 600, 150);
            cm.forcedAction(372, 0);
            cm.showDirectionEffect("Skill/3112.img/skill/31121006/effect", 0, 0, 0, 0, 0);
            cm.reservedEffect("Effect/Direction6.img/DemonTutorial/Scene3");
            cm.sendDelay(1000);
            break;
        }
        case 9: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/17", 0, 0, -110, 0, 0);
            cm.sendDelay(1000);
            break;
        }
        case 10: {
            cm.showNpcSpecialActionByTemplateId(2159340, "die");
            cm.showNpcSpecialActionByTemplateId(2159341, "die");
            cm.sendDelay(1000);
            break;
        }
        case 11: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/13", 0, 0, -110, 0, 0);
            cm.sendDelay(1000);
            break;
        }
        case 12: {
            cm.sendNextS("(굉장한 기술... 저 자는 대체 뭐지...?)", 5, 2159342);
            break;
        }
        case 13: {
            cm.sendDelay(1500);
            break;
        }
        case 14: {
            cm.sendNextS("(으... 겨우 저런 녀석들을 처치한 걸로 힘이 너무 많이 빠졌어... 여기는 어디지? 최소한 나에게 호의가 있는 곳은 아니다. 어서 이 곳을 벗어나자.)", 17);
            break;
        }
        case 15: {
            cm.forcedInput(2);
            cm.sendDelay(1500);
            break;
        }
        case 16: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/12", 0, 0, -110, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 2000, new java.awt.Point(0, -170), false, 0, true, 2159342, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(1200);
            break;
        }
        case 17: {
            cm.moveNpcByTemplateId(2159342, -1, 150);
            cm.sendDelay(1000);
            break;
        }
        case 18: {
            cm.sendNextS("(큰일이다. 의식이 희려지고 있어... 지금 공격 당하면 위험하다!)", 17);
            break;
        }
        case 19: {
            cm.sendNextPrevS("잠깐. 진정해. 나는 당신과 싸울 생각이 없다. 당신은 누구지? 왜 이런 곳에...?", 5, 2159342);
            break;
        }
        case 20: {
            cm.sendNextPrevS("(저 자에게서는 검은 마법사의 기운이 느껴지지 않는다.)\r\n다가오지 마...!", 17);
            break;
        }
        case 21: {
            cm.sendNextPrevS("그렇게 비틀거리면서 무슨 말을 하는 거야? 블랙윙이 당신에게 무슨 짓을 했는지 알고 있기는 한 건가? 그 옆의 기계는 에너지 전송 장치... 블랙윙은 당신의 힘을 뽑아내고 있었다고.", 5, 2159342);
            break;
        }
        case 22: {
            cm.sendNextPrevS("(에너지 전송 장치...? 이 물건 말인가? 그런데 블랙윙은 뭐지? 뭐가 뭔지 모르겠군... 대체 어떻게 된 거지?)", 17);
            break;
        }
        case 23: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/13", 0, 0, -110, 0, 0);
            cm.sendDelay(1500);
            break;
        }
        case 24: {
            cm.sendNextS("넌 누구지? 어떻게... 쿨럭. 그런 사실에 대해 알고 있는 거지?", 17);
            break;
        }
        case 25: {
            cm.sendNextPrevS("난 레지스탕스 요원 J. 블랙윙과 적대하는 자다. 자세한 상황은 모르지만 다친 사람과 싸울 정도의 악한은 아니야. 그보다 상태가 좋아 보이지 않는데, 내가 도와주...", 5, 2159342);
            break;
        }
        case 26: {
            cm.sendNextPrevS("이런... 힘이... 이제 더 이상...", 17);
            break;
        }
        case 27: {
            cm.forcedAction(379, 0);
            var checkGender = (cm.getPlayer().getGender() == 0 ? "fallMale" : "fallFeMale");
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/" + checkGender, 0, 0, 0, 0, 0);
            cm.sendDelay(1500);
            break;
        }
        case 28: {
            cm.dispose();
            cm.warp(931050030, 0);
            break;
        }
    }
}