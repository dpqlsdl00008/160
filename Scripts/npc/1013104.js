function action(mode, type, selection) {
    if (cm.isQuestActive(22007) == true && cm.haveItem(4032451, 1) == false){
        cm.sendNext("#b(달걀을 꺼냈다. 어서 유타에게 전해 주자.)#k");
        cm.gainItem(4032451, 1);
    } else {
        cm.sendNext("#b(지금은 달걀을 챙길 필요가 없는 것 같다.)#k");
    }
    cm.dispose();
}