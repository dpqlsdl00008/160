var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendNext("흠. 아직 리스 항구에 볼 일이 남은 모양이군.");
            cm.dispose();
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.sendYesNo("존의 지도에 적힌 섬으로 가겠나? 굉장히 멀어서 가려면 한참 걸릴 텐데... 한 15분 정도? 특별한 일이 없으면 가기 힘든 곳이지. 바로 출발하겠나?");
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(200090080, 0);
            break;
        }
    }
}