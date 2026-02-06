function start() {
    var itemId = 4033179;
    
    if (cm.haveItem(itemId)) {
        cm.sendOk("이미 해당 퀘스트 아이템을 가지고 있습니다.");
        cm.dispose();
        return;
    }

    if (!cm.canHold(itemId)) {
        cm.sendOk("인벤토리에 공간이 부족합니다.");
        cm.dispose();
        return;
    }

    cm.gainItem(itemId, 1);
    cm.dispose(); // 메시지 없이 조용히 지급 후 종료
}