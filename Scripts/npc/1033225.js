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
            cm.sendNext("기다리고 있었다... 메르세데스.");
            break;
        }
        case 1: {
            cm.sendNextPrevS("누구냐, 넌? 왜 이런 편지를 보냈지?!", 2);
            break;
        }
        case 2: {
            cm.sendNextPrev("그대가 엘프들 중에서도 특별한 자들만 사용 할 수 있다는 듀얼 보우건의 명수라는 말을 들었다. 엄청난 힘을 가졌다는 소문이 자자하던데... 과연 그 소문이 사실인지 직접 시험해 보고자 한다.");
            break;
        }
        case 3: {
            cm.sendNextPrevS("(블랙윙과 관련이 있는 자는 아닌 건가? 어쨌든 저런 말을 듣고도 승부를 피할 수는 없지... 엘프의 명예를 걸고 싸운다!)", 2);
            break;
        }
        case 4: {
            cm.sendNextPrev("진정한 강자라면 이 도전을 피하지 않겠지? 승부다!");
            break;
        }
        case 5: {
            cm.dispose();
            cm.resetMap(cm.getMapId());
            cm.spawnMonster(9300431, 599, 255);
            break;
        }
    }
}