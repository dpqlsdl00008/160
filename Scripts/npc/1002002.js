var status = -1;

function start() {
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
        if (status == 6) {
            cm.sendNext("\r\n흠... 싫다면 할 수 없지. 다음에 시간 날 때 한 번 놀러 와!");
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            var say = "하얀 파도 선착장에서 약간 떨어진 곳에 #b해변가 원두막#k이라는 환상적인 해변이 있다는 말은 들어 본 적이 있는가? #b2,000메소#k를 내거나 #b자유 여행권#k이 있다면 언제든지 나를 통해 그 곳으로 갈 수 있다네.#b";
            say += "\r\n#L0#2,000메소를 내겠습니다.";
            say += "\r\n#L1#자유 여행권을 가지고 있습니다.";
            say += "\r\n#L2#자유 여행권이 뭔가요?";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            switch (selection) {
                case 2: {
                    status = 2;
                    cm.sendNext("\r\n자유 이용권은 왠만해선 얻을 수 없지...");
                    break;
                }
                default: {
                    status = 6;
                    var say = "그래, #b2,000#k 메소를 내고 가겠다고? 좋아!";
                    if (selection == 1) {
                        say = "자유 이용권을 이용해서 가겠다고?";
                    }
                    cm.sendYesNo(say);
                    break;
                }
            }
            break;
        }
        case 2: {
            status = 0;
            action(1, 0, 2);
            break;
        }
        case 3: {
            cm.sendNextPrev("\r\n너가 상당히 강하거나 혹은...");
            break;
        }
        case 4: {
            cm.sendNextPrev("\r\n엄청난 부자나 정상급 정부 관계자가 아닌이상 자유 이용권은 발급해 주지 않아!");
            break;
        }
        case 5: {
            cm.dispose();
            break;
        }
        case 6: {
            cm.dispose();
            break;
        }
        case 7: {
            cm.dispose();
            if (v1 == 0 && cm.getMeso() < 1500) {
                cm.sendNext("\r\n메소가 부족한거 같군. 현대인이 2,000메소도 없다니.. 너무 한거 아니야? 이봐 메소를 확인하고 다시 시도하라구!");
                return;
            }
            if (v1 == 1 && !cm.haveItem(4031134, 1)) {
                cm.sendNext("\r\n흠... 자유 이용권이 어딨다는거지? 장난 치지말구 똑바로 하라구 난 한가한 사람이 아니야!");
                return;
            }
            if (v1 == 0) {
                cm.gainMeso(-2000);
            }
            cm.warp(120030000, "west00");
            break;
        }
    }
}