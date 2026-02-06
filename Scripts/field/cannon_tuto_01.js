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
            if (cm.getMap().containsNPC(1096000) == false) {
                cm.spawnNpc(1096000, 2209, -107);
            }
            if (cm.getMap().containsNPC(1096001) == false) {
                cm.spawnNpc(1096001, 2046, -62);
            }
            cm.lockInGameUI(true);
            cm.sendDelay(1000);
            break;
        }
        case 1: {
            cm.forcedInput(2);
            cm.sendDelay(2000);
            break;
        }
        case 2: {
            cm.sendNextS("그래, 이 지역을 떠나 메이플 아일랜드로 가는 이유는 뭔가? 여기서 메이플 아일랜드로 가는 사람은 흔치 않은 편인데... 게다가 자네의 차림새를 보아하니 단순히 여행을 떠나는 걸로 보이진 않네만?", 1, 0, 1096000);
            break;
        }
        case 3: {
            cm.sendNextPrevS("메이플 아일랜드로 가서 수련을 하려고요. 거기서 수련을 한 다음 빅토리아 아일랜드로 가면 멋진 모험가가 될 수 있다고 들었어요.", 3);
            break;
        }
        case 4: {
            cm.sendNextPrevS("오, 잘 알고 있군. 모험가가 되기 위한 첫 걸음은 메이플 아일랜드에서 시작하는 게 가장 좋지. 다른 곳에서 오는 초보자들이 많으니 친구도 사귈 수 있고, 위험한 몬스터들도 없고 말이야. 하지만 모험의 시작은 그 이후부터야. 빅토리아 아일랜드나 오시리아 같은 큰 대륙에는 상상도 못할만큼 강력한 몬스터들이 득실거리니까. 그만큼 모험의 재미도 상당하겠지만 말이야.", 1, 0, 1096000);
            break;
        }
        case 5: {
            cm.sendNextPrevS("강력한 몬스터! 멋진 모험가가 되기 위해 필수적인 것들이죠? 착실히 수행만 하면 얼마든지 강해질 수 있다고 하니 열심히 수련해 보려고요. 출발 전에 공부도 많이 했어요. 전 준비 되어 있는 모험가니까요. 핫핫핫!", 3);
            break;
        }
        case 6: {
            cm.sendNextPrevS("오호, 자신만만하군. 그럼 마음가짐이 가장 중요하지. 하지만 언제 어떤 일이 벌어질지 모르는 법! 무슨 일이 생기더라도, #b발록의 동굴에 들어가도 정신만 바짝 차리면 살 수 있다#k는 속담을 항상 마음에 새겨두면 이겨낼 수 있을 걸세.", 1, 0, 1096000);
            break;
        }
        case 7: {
            cm.sendNextPrevS("그런데 잠깐...? 자네 무슨 소리 못 들었나? 심상치 않은 기운이 느껴지는데...? 이 곳은 몬스터가 잘 나오지 않는 평화로운 해역인데... 조심하게!", 1, 0, 1096000);
            break;
        }
        case 8: {
            cm.showInstruction("이렇게나 평화로운데? 무슨 일이 생긴다는거지?", 100, 2);
            cm.forcedInput(2);
            cm.sendDelay(2000);
            break;
        }
        case 9: {
            cm.summonEffect(15, 1982, -62);
            cm.forcedInput(0);
            cm.sendDelay(2000);
            break;
        }
        case 10: {
            if (cm.getMap().containsNPC(1096008) == false) {
                cm.spawnNpc(1096008, 1920, -25);
            }
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/balog/0", 2000, new java.awt.Point(0, -200), false, 0, true, 1096008, 0, 0);
            cm.sendDelay(500);
            break;
        }
        case 11: {
            cm.showNpcSpecialActionByTemplateId(1096008, "attack2");
            cm.sendDelay(1000);
            break;
        }
        case 12: {
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/npc/0", 2000, new java.awt.Point(0, -110), false, 0, true, 1096000, 0, 0);
            cm.sendDelay(1000);
            break;
        }
        case 13: {
            cm.showDirectionEffect("Effect/Direction4.img/effect/cannonshooter/User/0", 0, 0, -110, 0, 0);
            cm.sendDelay(1000);
            break;
        }
        case 14: {
            cm.forcedInput(1);
            cm.sendDelay(200);
            break;
        }
        case 15: {
            cm.userEmotion(5, 10000, false);
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 16: {
            cm.forcedInput(1);
            cm.sendDelay(500);
            break;
        }
        case 17: {
            cm.userEmotion(5, 10000, false);
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 18: {
            cm.showNpcSpecialActionByTemplateId(1096008, "attack2");
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/npc/1", 2000, new java.awt.Point(0, -110), false, 0, true, 1096000, 0, 0);
            cm.sendDelay(500);
            break;
        }
        case 19: {
            cm.showDirectionEffect("Effect/Direction4.img/effect/cannonshooter/User/1", 0, 0, -110, 0, 0);
            cm.userEmotion(18, 10000, false);
            cm.sendDelay(1000);
            break;
        }
        case 20: {
            cm.showNpcSpecialActionByTemplateId(1096008, "attack1");
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/balog/0", 2000, new java.awt.Point(0, -200), false, 0, true, 1096008, 0, 0);
            cm.effectPlay("Mob/8150000.img/attack2/info/effect", 0, new java.awt.Point(0, 0), false, 0, true, 1096008, 0, 0);
            cm.sendDelay(1000);
            break;
        }
        case 21: {
            cm.userEmotion(4, 10000, false);
            cm.showDirectionEffect("Effect/Direction4.img/effect/cannonshooter/User/2", 2000, 0, -110, 0, 0);
            cm.showDirectionEffect("Mob/8150000.img/attack2/info/hit", 0, 0, -110, 0, 0);
            cm.forcedInput(6);
            cm.sendDelay(500);
            break;
        }
        case 22: {
            cm.forcedInput(0);
            cm.sendDelay(500);
            break;
        }
        case 23: {
            cm.showNpcSpecialActionByTemplateId(1096008, "attack2");
            cm.effectPlay("Mob/8150000.img/attack1/info/effect", 0, new java.awt.Point(0, 0), false, 0, true, 1096008, 0, 0);
            cm.sendDelay(500);
            break;
        }
        case 24: {
            cm.userEmotion(4, 10000, false);
            //cm.showInstruction("갑자기 왜 이러는거야??!", 100, 2);
            cm.showDirectionEffect("Mob/8150000.img/attack1/info/hit", 0, 0, -110, 0, 0);
            cm.forcedInput(6);
            cm.sendDelay(500);
            break;
        }
        case 25: {
            cm.effectPlay("Mob/8150000.img/attack1/info/effect", 0, new java.awt.Point(0, 0), false, 0, true, 1096008, 0, 0);
            cm.forcedInput(2);
            cm.sendDelay(200);
            break;
        }
        case 26: {
            cm.forcedInput(1);
            cm.sendDelay(200);
            break;
        }
        case 27: {
            cm.forcedInput(2);
            cm.sendDelay(200);
            break;
        }
        case 28: {
            cm.forcedInput(1);
            cm.sendDelay(200);
            break;
        }
        case 29: {
            cm.forcedInput(2);
            cm.sendDelay(200);
            break;
        }
        case 30: {
            cm.forcedInput(0);
            cm.sendDelay(200);
            break;
        }
        case 31: {
            cm.showNpcSpecialActionByTemplateId(1096008, "attack");
            cm.effectPlay("Effect/Direction4.img/effect/cannonshooter/balog/1", 2000, new java.awt.Point(0, -200), false, 0, true, 1096008, 0, 0);
            cm.sendDelay(1000);
            break;
        }
        case 32: {
            cm.forcedInput(2);
            cm.showDirectionEffect("Effect/Direction4.img/effect/cannonshooter/User/3", 0, 0, -110, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 33: {
            cm.dispose();
            cm.warp(912060100, 0);
            break;
        }
    }
}