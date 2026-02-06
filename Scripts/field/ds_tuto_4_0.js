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
            cm.sendNextS("으음...", 17);
            break;
        }
        case 1: {
            cm.forcedInput(1);
            cm.sendDelay(600);
            break;
        }
        case 2: {
            cm.forcedInput(0);
            cm.sendNextS("(여긴 낯선 방이로군. 아까의 그 동굴은 아닌데... 으윽... 온 몸이 아프다.)", 17);
            break;
        }
        case 3: {
            cm.sendNextPrevS("(보아하니 낯설긴 하지만 치료실 같아 보이는데... 여긴 어디지? 나는 어떻게 된 거지?)", 17);
            break;
        }
        case 4: {
            cm.sendNextPrevS("(잠시 상황을 정리해 보자.)", 17);
            break;
        }
        case 5: {
            cm.sendNextPrevS("(검은 마법사는 나와의 약속을 어기고 어머니와 데미안이 살고 있는 오시리아 대륙 남부 지역을 파괴했다. 고향에 남은 것은 폐허 뿐... 대체 왜...)", 17);
            break;
        }
        case 6: {
            cm.sendNextPrevS("(펜던트! 펜던트는 어디로 갔지?)", 17);
            break;
        }
        case 7: {
            cm.sendNextPrevS("(싸우던 중에 사라져 버린 건가? 가족에 관한 물건조차 하나 남기지 못하다니... 크윽...)", 17);
            break;
        }
        case 8: {
            cm.forcedInput(2);
            cm.sendDelay(600);
            break;
        }
        case 9: {
            cm.forcedInput(0);
            cm.sendNextS("(검은 마법사에게 복수하기 위해 찾은 시간의 신전... 중간에 마스테마를 보냈지. 남아 있었다면 다른 군당장에게 목숨을 잃었을 테니까... 아카이럼의 방해가 있었지만 곧 물러섰고... 그러고보니 그 때 영웅들이 왔다고 했던가?)", 17);
            break;
        }
        case 10: {
            cm.sendNextPrevS("(역시나 검은 마법사는 강했다. 목숨을 걸면 조금이라도 타격을 입힐 수 있을 줄 알았는데... 겨우 보호 마법을 깨고 옷자락이나 건드린 정도... 처음부터 그를 죽일 수 있으리란 기대는 하지 않았지만...)", 17);
            break;
        }
        case 11: {
            cm.sendNextPrevS("(그런데 어떻게 내가 아직 살아있는 거지...? 검은 마법사가 반기를 든 부하를 살려뒀을 리가 없는데... 다른 이들이 그 사이에 개입한건가? 설마 영웅이라는 자들이...?)", 17);
            break;
        }
        case 12: {
            cm.sendNextPrevS("(후... 머리가 아프군. 추측만으로 확실해 지는 것은 없어... 일단 여기가 어디인지부터 알 수가 없으니. 모든 것이 파괴된 메이플 월드에 이런 곳이 남아 있다니 놀라운 걸. 게다가 저 물건들... 너무 낯설어.)", 17);
            break;
        }
        case 13: {
            cm.forcedInput(1);
            cm.sendDelay(600);
            break;
        }
        case 14: {
            cm.forcedInput(0);
            cm.sendNextPrevS("(일단 상태를 점검하자... 어떻게 된 건지는 모르지만 상황에 대처하려면 힘이 있어야 하니... 포스는 어느 정도 유지하고 있지?)", 17);
            break;
        }
        case 15: {
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg0/13", 0, 0, -110, 0, 0);
            cm.sendDelay(1500);
            break;
        }
        case 16: {
            cm.sendNextS("(...빌어먹을! 포스가 거의 없잖아...! 줄어든 포스 쉴드만 봐도 내 몸 상태가 어떤지 알겠어. 거의 모든 능력이 사라졌어. 하지만 아무리 상처를 입었다 해도 이렇게까지 약해지다니... 이게 말이 되는 일인가?)", 17);
            break;
        }
        case 17: {
            cm.sendNextPrevS("(이렇게 약해져서야 적이 나타나기라도 하면 버틸 수 있을까? 그 때 그 모자를 쓴 남자... 적은 아닌 것 같았지만 알 수 없어...)", 17);
            break;
        }
        case 18: {
            cm.sendNextPrevS("(휴... 어차피 힘이 회복되려면 멀었다. 이러고 있어봤자 달라질 것은 없지... 차라리 움직이자.)", 17);
            break;
        }
        case 19: {
            cm.forcedInput(1);
            cm.sendDelay(3000);
            break;
        }
        case 20: {
            cm.forcedInput(0);
            cm.showDirectionEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/3", 0, 0, -110, 0, 0);
            cm.sendDelay(1500);
            break;
        }
        case 21: {
            cm.sendNextS("(뭔가 소리가 들리는데...)", 17);
            break;
        }
        case 22: {
            cm.sendNextPrevS("발전소에 있던 에너지 전송 장치와 동일한 물건이 알에 연결되어 잇는 것을 확인하고 일단 돌아오려고 했지. 적어도 에델슈타인의 에너지를 끌어가지는 않는 것 같으니까. 그런데 그 때 저 자가 알을 깨고 나와서 블랙윙들을 전멸시켰어.", 5, 2159342);
            break;
        }
        case 23: {
            cm.sendNextPrevS("솔직히 J, 당신의 말이 아니라면 믿기 어려운 황단한 이야기군요. 블랙윙은 대체 무슨 짓을 하고 있는 거죠? 게다가 그 사람... 등에 날개까지 달려 있었어요. 적어도 평범한 인간은 아니예요.", 5, 2159315);
            break;
        }
        case 24: {
            cm.sendNextPrevS("(...내 이야기인가?)", 17);
            break;
        }
        case 25: {
            cm.sendNextPrevS("그 자가 쓰는 기술도 처음 보는 것이였어. 매우 강력하더군... 솔직히 힘이 빠져서 제압 가능한 상태가 아니라면 데려오지 않았을 거야. 위험할지도 모르니.", 5, 2159342);
            break;
        }
        case 26: {
            cm.sendNextPrevS("혹시 실험체인 건가?... 벨비티도 그렇고, 블랙윙이 광산 저 깊은 곳에서 무슨 실험을 하는지는 누구도 모르잖아. 미친 과학자 겔리메르가 만들어낸 실험체라면 그 역시 피해자일 뿐이야.", 5, 2159312);
            break;
        }
        case 27: {
            cm.sendNextPrevS("빌어먹을 겔리메르... 그 녀석만은 꼭 없애버리고 말겠어!", 5, 2159314);
            break;
        }
        case 28: {
            cm.sendNextPrevS("슬슬 깨어날 때가 된 것 같은데. 확인해봐야겠군.", 5, 2159342);
            break;
        }
        case 29: {
            cm.forcedInput(2);
            cm.sendDelay(2000);
            break;
        }
        case 30: {
            if (cm.getMap().containsNPC(2159342) == false) {
                cm.spawnNpc(2159342, -600, -20);
                cm.showNpcSpecialActionByTemplateId(2159342, "summon");
            }
            cm.forcedInput(1);
            cm.sendDelay(500);
            break;
        }
        case 31: {
            cm.effectPlay("Effect/Direction6.img/effect/tuto/balloonMsg1/3", 2000, new java.awt.Point(0, -170), false, 0, true, 2159342, 0, 0);
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 32: {
            cm.sendNextPrevS("일어났군. 몸은 괜찮은가? 아직 안색이 안 좋은데...", 5, 2159342);
            break;
        }
        case 33: {
            cm.sendNextPrevS("...당신이 날 구한 겁니까?", 17);
            break;
        }
        case 34: {
            cm.sendNextPrevS("쓰러진 사람을 블랙윙들 틈에 두고 올 수야 없지. 상황을 보면 우리는 서로 이해 관계가 일치할 것 같거든. 당신도 할 말이 많아 보이는데... 잠시 같이 가지.", 5, 2159342);
            break;
        }
        case 35: {
            cm.sendNextPrevS("(심문...? 회유...? 아직은 모르겠다. 최소한 깨어나자마자 본 블랙윙이라는 그 자들보다는 호의적인 것 같은데...) 좋습니다.", 17);
            break;
        }
        case 36: {
            cm.dispose();
            cm.warp(931050010, 0);
            break;
        }
    }
}