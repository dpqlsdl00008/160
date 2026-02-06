function action(mode, type, selection) {
    cm.dispose();
    if (cm.getMapId() != 211060400) {
        cm.getPlayer().dropMessage(5, "너무 멀리 떨어져 있어 아이템을 사용 할 수 없습니다.");
        return;
    }
    cm.removeAll(2430159);
    cm.forceCustomDataQuest(3182, "211060400");
    cm.getPlayer().dropMessage(5, "알케스터의 크리스탈이 밝은 빛을 내며 사라지고, 동시에 머트는 신비로운 빛에 휩싸였습니다.");
}