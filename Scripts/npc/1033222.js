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
            cm.sendNext("무슨 일인가요, 메르세데스님?! 소란스러운 소리가 들리던데... 네? 습격이요? 다친 곳은 없으세요?!");
            break;
        }
        case 1: {
            cm.sendNextPrev("대체 누가 이 헤네시스에서 이런 짓을... 자세한 이야기는 안에 들어가서 하죠!");
            break;
        }
        case 2: {
            cm.dispose();
            cm.forceStartQuest(24095, "1");
            cm.warp(100000201, 0);
            break;
        }
    }
}