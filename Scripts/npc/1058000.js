var status = -1;

function start() {
    status = -1;
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
            cm.dispose();
            var x = java.lang.Math.abs(cm.getMap().getNPCById(1058000).getPosition().x - cm.getPlayer().getPosition().x);
            var y = java.lang.Math.abs(cm.getMap().getNPCById(1058000).getPosition().y - cm.getPlayer().getPosition().y);
            if (x > 100 || y > 100) {
                cm.playerMessage("거리가 너무 멀어서 부엉이에게 말을 걸 수 없다.");
                break;
            }
            if (cm.haveItem(4033178, 1) == true) {
                cm.playerMessage("더 이상 부엉이에게 받을 것이 없다.");
                break;
            }
            cm.gainItem(4033178, 1);
            cm.sendNext("(부엉이는 심드렁한 얼굴로 증표 하나를 내주었다. 날개를 휘휘 흔들어 보인다. 가라는 의미인 모양이다...)");
            break;
        }
    }
}