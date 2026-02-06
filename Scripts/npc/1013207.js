var status = -1;

function start() {
    status = -1;
    if (cm.getMapId() == 200090080) {
        status = 1;
    }
    if (cm.getMapId() == 200090090) {
        status = 2;
    }
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
           cm.sendNext("흠. 아직 리스항구에 볼 일이 남은 모양이군.");
           cm.dispose();
           return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendYesNo("흠... 위험한 해역이긴 하지만 나름 스릴 넘치고 좋은걸? 가끔 혼자 와서 즐겨도 되겠어. 그럼 그만 리스항구로 돌아갈까?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(200090090, 0);
            break;
        }
        case 2: {
            cm.dispose();
            cm.sendNext("존의 지도에 있는 이 섬까지 가려면 한 15분 정도 걸릴 거야. 위험한 곳이니 너무 말을 걸지 말아줘. 잘못했다간 뱃길을 잃고 리스항구로 돌아갈 수도 있어.");
            break;
        }
        case 3: {
            cm.dispose();
            cm.sendNext("리스항구까지 가려면 15분 정도 걸릴 거야. 위험한 해역이니 너무 말을 걸지 말아줘. 잘못하다간 뱃길을 잃고 섬으로 돌아가 버릴 거야.");
            break;
        }
    }
}