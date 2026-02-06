var status = -1;

function start() {
    cMap = cm.getMapId();
    switch (cMap) {
        case 507100000: {
            status = -1;
            break;
        }
        case 507100001: {
            status = 12;
            break;
        }
        case 507100003: {
            status = 19;
            break;
        }
        case 507100004: {
            status = 45;
            break;
        }
    }
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.lockInGameUI(true);
            cm.forcedInput(1);
            cm.sendDelay(4000);
            break;
        }
        case 1: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 2: {
            cm.sendNextS("겨우 때가 왔구나. 제육 천마 왕의 분신 오다 노부가나의 신어도 오늘로 끝내는 것인가...", 9, 9131007, 9131007);
            break;
        }
        case 3: {
            cm.sendNextPrevS("주요 가문을 지키지 못하고, 저의 가문을 지키지 못하고, 공주님도 지키지 못한 굴욕... 오늘이야 말로 해결 할 수 있다고 생각하니, 몸이 계속 떨리는 군요.", 3);
            break;
        }
        case 4: {
            cm.sendNextPrevS("오명을 되돌려 주는 것도 좋지만, 복수만을 위해서 움직이는 것은 좋지 않아. 그대의 솜씨는 인정하지만, 흥분하다가는 판단력을 상실하게 될 테니 조심하게.", 9, 9131007, 9131007);
            break;
        }
        case 5: {
            cm.sendNextPrevS("네, 가슴에 새기겠습니다. 그런데, 소인의 검에 말려들어 간 피까지 거느릴 수 있을 지 모르겠군요.", 3);
            break;
        }
        case 6: {
            cm.sendNextPrevS("그렇게 까지 말 할 여유가 있다면 문제는 없을 걸세. 그렇다면 #b#h ##k, 그대에게 혼노지의 동문을 여는 것을 맡겨도 되겠나?", 9, 9131007, 9131007);
            break;
        }
        case 7: {
            cm.sendNextPrevS("혼노지의 동문이라면...", 3);
            break;
        }
        case 8: {
            cm.sendNextPrevS("맞네, 혼노지의 외벽을 넘어 동문을 열어 준다면 나의 기마대가 돌입하여 제육 천마 왕의 수하들을 산산 조각으로 만들어 줄 걸세.", 9, 9131007, 9131007);
            break;
        }
        case 9: {
            cm.sendNextPrevS("소인의 검은 단지 적의 피를 바랄 뿐... 뒤를 돌아보는 일 따윈... 맡겨만 주십시오.", 3);
            break;
        }
        case 10: {
            cm.sendNextPrevS("대단한 기개로구만. 가능하다면 내 수하로 거두고 싶을 정도로 말이지. 그럼 무운을 빌겠네. 나의 기마대에게도 곧 바로 자네를 뒤 따라 움직이게 조치하겠네.", 9, 9131007, 9131007);
            break;
        }
        case 11: {
            cm.dispose();
            cm.warp(507100001, 0);
            break;
        }
        case 12: {
            cm.dispose();
            break;
        }
        case 13: {
            cm.forcedInput(2);
            cm.sendDelay(500);
            break;
        }
        case 14: {
            cm.forcedInput(0);
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/0", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 15: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/1", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 16: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/2", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 17: {
            cm.sendNextS("모든 적을 섬멸하고 혼노지의 동문을 개방하자.", 3);
            break;
        }
        case 18: {
            cm.dispose();
            cm.lockInGameUI(false);
            break;
        }
        case 19: {
            cm.dispose();
            break;
        }
        case 20: {
            cm.forcedInput(1);
            cm.sendDelay(2000);
            break;
        }
        case 21: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 22: {
            cm.sendNextS("호랑이를 잡으러 가는 길에 사슴을 만나게 되었군요. 후후. 그 모습은 노부나가님을 따르는 자가 아닌 것 같습니다만... 이 혼노지에는 무슨 볼 일 이라도?", 9, 9131000, 9131000);
            break;
        }
        case 23: {
            cm.sendNextPrevS("#b(부드러운 외모와는 달리 무서운 살기가 느껴진다... 노부나가 휘하의 무장인가...)#k", 3);
            break;
        }
        case 24: {
            cm.sendNextPrevS("저는 #b#h ##k, 마츠야마 가문의 가신 아네가사키 도모노부의 장남입니다. 저는 그저 제 가족의 복수와 함께 공주님을 구하러 왔을 뿐 입니다.", 3);
            break;
        }
        case 25: {
            cm.sendNextPrevS("마츠야마... 마츠야마... 기억에 없는 가문이군요. 이미 존재하지 않는 가문이라면 기억에 남지도 않아서...", 9, 9131000, 9131000);
            break;
        }
        case 26: {
            cm.sendNextPrevS("그 오만하고 방약무인 한 태도를 보면 굳이 이름을 듣지 않아도 제육 천마 왕의 수하가 틀림이 없는 것 같군요. 그럼 저의 검도 망설 일 이유 따윈 없습니다.", 3);
            break;
        }
        case 27: {
            cm.sendNextPrevS("더 할 말이 있다면 말을 하는 것이 좋을 것 입니다. 그 말이 그대가 이승에서의 최후의 말이 될 테니...", 3);
            break;
        }
        case 28: {
            cm.sendNextPrevS("#h #! 이 녀석을 상대하느라 시간을 허비하지 말게.", 9, 9131007, 9131007);
            break;
        }
        case 29: {
            cm.sendNextPrevS("...!?", 3);
            break;
        }
        case 30: {
            cm.sendNextPrevS("그대를 보니, 지금의 이 혼전이 어떻게 시작 되었는 지 알고 있을 것 같네만... 그렇지 않은가? 오다 사천 왕, 아케치 미츠히데!", 9, 9131007, 9131007);
            break;
        }
        case 31: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/0", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 32: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/3", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 33: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/4", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 34: {
            cm.sendNextPrevS("이런 이런... 후후... 과연 다케다 신겐공... 저의 정체를 한 번에 꿰 뚫어 보고, 모반을 일으킨 것 까지 알아 챌 줄 이야...", 9, 9131000, 9131000);
            break;
        }
        case 35: {
            cm.sendNextPrevS("귀공을 직접 만나는 것은 오늘이 처음이네만, 지금까지의 풍문으로부터 귀공이 타인을 오래 섬기는 인물이 아니라는 것 정도는 알고 있네. 언젠가는 원래 섬기는 자에게 등을 돌릴 것이라면, 여기서는 잠깐 손을 잡는 게 좋을 것 같네만...", 9, 9131007, 9131007);
            break;
        }
        case 36: {
            cm.sendNextPrevS("다게다 신겐님, 저 자는 마츠야마가를 멸망시킨 자입니다! 불구대천의 원수와 손을 잡는다는 것은 말이 안됩니다.", 3);
            break;
        }
        case 37: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/5", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 38: {
            cm.sendNextPrevS("그만...!", 9, 9131007, 9131007);
            break;
        }
        case 39: {
            cm.sendNextPrevS("그대의 임무는 공주님을 구출하는 것이 아니였나!?", 9, 9131007, 9131007);
            break;
        }
        case 40: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/8", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 41: {
            cm.sendNextPrevS("이곳은 나에게 맡기고 서두르게나.", 9, 9131007, 9131007);
            break;
        }
        case 42: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/10", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 43: {
            cm.forcedInput(1);
            cm.sendDelay(1150);
            break;
        }
        case 44: {
            cm.dispose();
            cm.warp(507100004, 0);
            break;
        }
        case 45: {
            cm.dispose();
            break;
        }
        case 46: {
            cm.forcedInput(1);
            cm.sendDelay(3500);
            break;
        }
        case 47: {
            cm.forcedInput(0);
            cm.sendDelay(1000);
            break;
        }
        case 48: {
            cm.sendNextS("여자에게는 칼을 보이고 싶지 않군요. 물러나 주시길...", 3);
            break;
        }
        case 49: {
            cm.sendNextPrevS("흥, 무례한 것 같으니... 내가 누구인지 모르는 것이더냐!? 사이토 도산의 딸이자, 제육 천마 왕 오다 노부나가의 정실, 노히메니라!", 9, 9131005, 9131005);
            break;
        }
        case 50: {
            cm.sendNextPrevS("긴 말 하지 않겠습니다. 여자에게는 칼을 보이고 싶지 않습니다. 물러나 주시지요.", 3);
            break;
        }
        case 51: {
            cm.sendNextPrevS("건방지긴. 네 놈의 검 솜씨도 그 태도 만큼인 지, 한 번 볼까?", 9, 9131005, 9131005);
            break;
        }
        case 52: {
            cm.showNpcSpecialActionByTemplateId(9131005, "step");
            cm.sendDelay(1000);
            break;
        }
        case 53: {
            cm.effectPlay("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/14", 2500, new java.awt.Point(0, -110), false, 0, true, 9131005, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 54: {
            cm.effectPlay("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/15", 2500, new java.awt.Point(0, -110), false, 0, true, 9131005, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 55: {
            cm.effectPlay("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/16", 2500, new java.awt.Point(0, -110), false, 0, true, 9131005, 0, 0);
            cm.sendDelay(500);
            break;
        }
        case 56: {
            cm.showDirectionEffect("Effect/DirectionJP3.img/effect/kenjiTuto/balloonMsg/17", 0, 0, -110, 0, 0);
            cm.sendDelay(2500);
            break;
        }
        case 57: {
            cm.forcedInput(1);
            cm.sendDelay(1950);
            break;
        }
        case 58: {
            cm.forcedInput(7);
            cm.sendDelay(750);
            break;
        }
        case 59: {
            cm.dispose();
            cm.lockInGameUI(false);
            while (cm.getLevel() < 10) {
                cm.getPlayer().levelUp();
            }
            cm.getPlayer().changeJob(6100);
            cm.getPlayer().resetStats(4, 4, 4, 4);
            cm.warp(910000000, 0);
            break;
        }
    }
}