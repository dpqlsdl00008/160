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
            if (cm.getMapId() != 931000020) {
                cm.dispose();
            } else {
                cm.sendNext("흥, 이 쥐새끼 같은 녀석들.. 어딜 감히 도망치려고?");
            }
            break;
        }
        case 1: {
            cm.sendNextPrevS("앗 들켰나!", 2);
            break;
        }
        case 2: {
            cm.sendNextPrev("뛰어봤자 벼룩이지. 귀찮게 반항하지 말고 어서 잡혀라. 실험체가 가야 어딜 가겠다고... 어라? 뒤의 녀석은 그렇치고.. 넌 실험체가 아니잖아? 뭐지? 마을 사람인가?");
            break;
        }
        case 3: {
            cm.sendNextPrevS("그렇다! 난 에델슈타인의 주민이야!", 2);
            break;
        }
        case 4: {
            cm.sendNextPrev("쳇... 꼬맹이들이 광산에 접근하지 못하도록 하라고 몇 번을 말했건만 들어먹지를 않는군. 멍청한 주민들... 어쩔 수 없지 마을에 가서 실험실에 관한 걸 떠들게 할 수는 없으니 너도 잡혀줘야겠다.");
            break;
        }
        case 5: {
            cm.sendNextPrevS("뭐라고? 누가 순순히 잡혀주겠대?", 2);
            break;
        }
        case 6: {
            cm.sendNextPrev("흥. 건방지긴.. 언제까지 건방지게 굴 수 있는지 볼까?");
            break;
        }
        case 7: {
            cm.addHP(-45);
            cm.sendNextPrevS("#b(쉴러에게 공격을 받아 체력이 절반으로 줄었다! 어떻게 하지? 싸워서 이길 수 있는 상대가 아닌 것 같다!)#k", 2);
            break;
        }
        case 8: {
            cm.sendNextPrev("이래도 감히 함부로 입을 놀리지는 않겠지? 너에게는 좀더 강력한 실험을 하라고 겔리메르님에게 건의해 보겟다. 후후후.. 그럼 순순히 잡혀라.");
            break;
        }
        case 9: {
            cm.sendNextPrevS("멈춰! ", 4, 2159010);
            break;
        }
        case 10: {
            cm.dispose();
            cm.warp(931000021, 1);
            break;
        }
    }
}