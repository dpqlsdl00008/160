function action(mode, type, selection) {
    cm.forceStartQuest(22011);
    cm.playerMessage("드래곤의 알을 획득했습니다.");
    cm.warp(900090103, 0);
    cm.dispose();
}