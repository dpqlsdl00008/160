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
            cm.sendNextS("그... 정말 날개가 달려있군.", 5, 2159314);
            break;
        }
        case 1: {
            cm.sendNextPrevS("정체가 뭐지? 설마 블랙윙의 첩자? 상황을 보면 그건 아닌 거 같긴 한데...", 5, 2159312);
            break;
        }
        case 2: {
            cm.sendNextPrevS("당신은 누구시죠? 블랙윙과는 어떤 관계인가요?", 5, 2159315);
            break;
        }
        case 3: {
            cm.sendNextPrevS("블랙윙이라는 자들에 대해서는 잘 모릅니다. 솔직히 처음 들어 보는 이름이군요. 저에 대해 알고 싶은 게 뭡니까? 어디서부터 설명 해야 할 지... 감도 잡히지 않습니다만.", 17);
            break;
        }
        case 4: {
            cm.sendNextPrevS("우선 당신의 소속, 이력. ...그리고 실례가 안 된다면 그 날개의 정체도 말해주면 좋겠군요.", 5, 2159342);
            break;
        }
        case 5: {
            cm.sendNextPrevS("제 이름은 #h #. 현재 소속은 없다고 해야겠지요. 얼마 전까지 검은 마법사의 군단장이었지만... 검은 마법사에게 대항하려다가 패해, 정신을 잃었고 깨어나 보니 모자 쓴 저 남자가 본 그 상황이었습니다. 그리고 날개는 내 아버지가 마족이기 때문에 태어날 때부터 달려 있던 겁니다.", 17);
            break;
        }
        case 6: {
            cm.sendNextPrevS("검은 마법사? 군단장? 무슨 이야기를 하고 있는 건지 모르겠군요. 당신이 하는 이야기는 이 상황과 너무나 동 떨어져 있어요. 알고 있는 건가요? 검은 마법사는 이미 수백 년 전에 영웅들의 활약으로 봉인된 것으로 알려져 있어요!", 5, 2159315);
            break;
        }
        case 7: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/3", 0, 0, -110, 0, 0);
            cm.sendDelay(1000);
            break;
        }
        case 8: {
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 2000, new java.awt.Point(0, -110), false, 0, true, 2159312, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 2000, new java.awt.Point(0, -110), false, 0, true, 2159313, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 2000, new java.awt.Point(0, -110), false, 0, true, 2159314, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 2000, new java.awt.Point(0, -110), false, 0, true, 2159315, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 2000, new java.awt.Point(0, -110), false, 0, true, 2159316, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 2000, new java.awt.Point(0, -110), false, 0, true, 2159344, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg0/10", 2000, new java.awt.Point(0, -110), false, 0, true, 2159345, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 9: {
            cm.sendNextS("...음... 실험으로 머리가 이상해진 건 아닐까?", 5, 2159314);
            break;
        }
        case 10: {
            cm.sendNextPrevS("(수백 년 전...? 영웅들이 검은 마법사를 봉인...?! 대체 어떻게 된 거지? 이 자들이 거짓말을 하는 건가? 하지만 이 낯선 땅, 낯선 물건, 낯선 세력들... 그 싸움 이후... 대체 얼마나 시간이 지난 거지...)", 17);
            break;
        }
        case 11: {
            cm.sendNextPrevS("끄응... 모르겠다. 이봐, 블랙잭! 네가 보기엔 어때? 저 사람이 거짓말 하는 것 같아?", 5, 2159314);
            break;
        }
        case 12: {
            cm.sendNextPrevS("최소한 거짓말을 하는 것은 아니야. 제 정신이 아닐 가능성은 있지만. 분명한 건 악의는 없다는 거 정도일까?", 5, 2159345);
            break;
        }
        case 13: {
            cm.sendNextPrevS("블랙잭이 거짓말이 아니라고 하면 아닌 법이죠... 그럼 가능성은 두 가지인가요? 저 자가 제 정신이 아니라는 것과, 저 자의 말이 진실이라는 것.", 5, 2159316);
            break;
        }
        case 14: {
            cm.sendNextPrevS("...그저 개인적인 이유입니다. 그보다 대답을 했으니 저도 질문을 해도 괜찮겠습니까? 당신들은 누구입니까? 그리고 그 블랙윙이라는 자들은...?", 17);
            break;
        }
        case 15: {
            cm.sendNextPrevS("처음 만났을 때 말했다시피, 우리는 레지스탕스다. 블랙윙에 대항해 만들어진 그림자 조직으로 우리 마을 에델슈타인을 그들로부터 되찾기 위해 싸우고 있다.", 5, 2159342);
            break;
        }
        case 16: {
            cm.sendNextPrevS("당신의 에너지를 뽑아내던 녀석들이 바로 블랙윙이야. 평화롭던 우리 마을을 점령하고 마을의 에너지를 빼앗는 사악한 녀석들지... 대체 왜 그렇게 에너지를 모으는 지는 잘 몰라. 다만 그들이 검은 마법사를 신봉하고 있다는 사실만은 알고 있어.", 5, 2159342);
            break;
        }
        case 17: {
            cm.sendNextPrevS("검은 마법사를 신봉하는 자들...? 검은 마법사는 봉인되었다고 하지 않았습니까? 그런데 어떻게 그런 자들이 나타난 겁니까?", 17);
            break;
        }
        case 18: {
            cm.sendNextPrevS("그게 우리들 모두가 알고 싶어하는 수수께끼지. 그들은 검은 마법사가 다시 이 메이플 월드에 타나탈 거라고 믿고 있어. 실제로 그와 같은 징조들이 곳곳에 보이고 있고... 평화롭지만은 않은 상황이지.", 5, 2159312);
            break;
        }
        case 19: {
            cm.sendNextPrevS("검은 마법사가 다시금 메이플에...? 그건... 반가운 이야기로군요.", 17);
            break;
        }
        case 20: {
            cm.sendNextPrevS("...저에게 한 번 더 복수할 수 있는 기회가 남아 있다니...", 17);
            break;
        }
        case 21: {
            cm.sendNextPrevS("...당신이 제정신인지 아닌지는 모르겠지만, 검은 마법사를 증오하는 건 확실해 보이는군...", 5, 2159313);
            break;
        }
        case 22: {
            cm.sendNextPrevS("검은 마법사에게 복수를 원하는 분... 그렇다면 우리의 동료가 되는 것이 어떻습니까?", 5, 2159311);
            break;
        }
        case 23: {
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 2000, new java.awt.Point(0, -110), false, 0, true, 2159312, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 2000, new java.awt.Point(0, -110), false, 0, true, 2159313, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 2000, new java.awt.Point(0, -110), false, 0, true, 2159314, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 2000, new java.awt.Point(0, -110), false, 0, true, 2159315, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 2000, new java.awt.Point(0, -110), false, 0, true, 2159316, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 2000, new java.awt.Point(0, -110), false, 0, true, 2159344, 0, 0);
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/4", 2000, new java.awt.Point(0, -110), false, 0, true, 2159345, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 24: {
            cm.sendNextS("교장 선생님?! 갑자기 무슨 말씀을...", 5, 2159315);
            break;
        }
        case 25: {
            cm.sendNextPrevS("이 자의 말을 믿으시는 겁니까? 실험의 여파로 제정신이 아니게 된 자일지도 모릅니다. 설명 맞는 말이라고 하더라도 검은 마법사의 군단장이었던 자 아닙니까?", 5, 2159313);
            break;
        }
        case 26: {
            cm.sendNextPrevS("이렇게 모두 모여 있는 걸 보니 마음이 든든하군요. 하하하...", 5, 2159311);
            break;
        }
        case 27: {
            cm.sendNextPrevS("블랙잭이 말한대로 그가 거짓말을 하는 것이 아니라면, 적어도 그가 가진 검은 마법사에 대한 원한은 확실한 게 아닙니까? '#b전직#k'이 군단장이었을 뿐 더 이상 군단장이 아니기도 하고요.", 5, 2159311);
            break;
        }
        case 28: {
            cm.sendNextPrevS("그렇다면 그렇긴 하지만요. 저 자가 밖으로 나가봐야 블랙윙들에게 잡혀가기밖에 더 하겠나 싶기도 하고...", 5, 2159312);
            break;
        }
        case 29: {
            cm.sendNextPrevS("동료가 늘어서 나쁠 것은 없지요. 그와 우리의 목적이 일치하는 한, 함께 싸울 수 있지 않겠습니까?", 5, 2159311);
            break;
        }
        case 30: {
            cm.sendNextPrevS("자, 잠깐! 왜 이야기가 이렇게 갑자기 진행되는 겁니까? 저는 아직 이 상황에 적응도 못했고... 일단 생각 할 시간이 필요합니다!", 17);
            break;
        }
        case 31: {
            cm.sendNextPrevS("생각 할 필요까지 있나? 상황은 명확해. 검은 마법사와 싸우고 싶다며. 그럼 그 추종 세력인 블랙윙과의 싸움도 피할 수 없겠지. 적의 적은 친구? 그러므로 우리는 친구가 될 수 있는 거잖아? 무엇보다 어차피 당신에게 선택의 여지는 없어. 여기를 나가면 밖은 블랙윙 투성이라고. 그 상태로 이길 수 있겠어?", 5, 2159314);
            break;
        }
        case 32: {
            cm.sendNextPrevS("신중한 태도는 훌륭합니다. 어차피 당장 서로를 믿는 건 무리겠지요. 우리는 당신을 감시하고, 당신은 우리를 의심하면서... 그러면서 협력을 하면 되는 거 아니겠습니까? 신뢰는 천천히 쌓아나가도 되겠죠.", 5, 2159316);
            break;
        }
        case 33: {
            cm.sendNextPrevS("...틀린 말은 아니군요. 그렇다면... 일단 당신들의 말을 믿어 보겠습니다.", 17);
            break;
        }
        case 34: {
            cm.sendNextPrevS("그나저나... 늦었지만 저를 구해준 것에 대해 감사하다는 말을 하고 싶군요.", 17);
            break;
        }
        case 35: {
            cm.sendNextPrevS("그 말을 들으니 안심이 되는군... 감사 할 줄 아는 사람은 배신을 잘 안 하거든.", 5, 2159342);
            break;
        }
        case 36: {
            cm.sendNextPrevS("당신들이 나를 배신하기 전까지, 절대 배신하지 않을 겁니다.", 17);
            break;
        }
        case 37: {
            cm.sendNextPrevS("그럼 편하게 지내도록 해요. 마음이 바뀌면 언제든지 이야기 하고... 허허허.", 5, 2159316);
            break;
        }
        case 38: {
            cm.dispose();
            cm.teachSkill(30010166, -1, -1);
            cm.teachSkill(30011167, -1, -1);
            cm.teachSkill(30011168, -1, -1);
            cm.teachSkill(30011169, -1, -1);
            cm.teachSkill(30011170, -1, -1);
            cm.warp(310010000, 0);
            while (cm.getLevel() < 10) {
                cm.getPlayer().levelUp();
            }
            cm.forceStartQuest(23209, "1");
            cm.forceCompleteQuest(23279);
            cm.forceCompleteQuest(29958);
            cm.forceCompleteQuest(7621);
            cm.getPlayer().changeJob(3100);
            cm.getPlayer().resetStats(4, 4, 4, 4);
            cm.lockInGameUI(false);
            break;
        }
    }
}