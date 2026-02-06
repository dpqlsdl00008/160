var status = -1;

function start() {
    status = -1;
    // 먼지 바람 언덕
    if (cm.getMapId() == 102020100) {
        status = -1;
    }
    // 와일드 보어의 땅
    if (cm.getMapId() == 102030000) {
        status = 4;
    }
    // 야생 돼지의 땅
    if (cm.getMapId() == 102030100) {
        status = 9;
    }
    // 철갑 돼지의 땅
    if (cm.getMapId() == 102030200) {
        status = 14;
    }
    // 타오르는 열기
    if (cm.getMapId() == 102030300) {
        status =19;
    }
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
        if (status == 13) {
            selection = 2;
        }
        status--;
    }
    if (cm.isQuestActive(22597) == false) {
       cm.forceStartQuest(22597, "0");
    }
    if (cm.getQuestRecord(22597).equals("")) {
       cm.forceStartQuest(22597, "0");
    }
    switch (status) {
        case 0: {
            var say = "#e먼지 바람 언덕 경고판#n\r\n";
            say += "\r\n출몰 몬스터 : 스텀프, 다크 스텀프.";
            say += "\r\n특이 사항 : 불타는 땅으로 이어지는 세 갈래 길.";
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) != -1) {
                cm.dispose();
                say += "\r\n확인 : O";
            } else {
                say += "\r\n확인 : ";
            }
            cm.sendNext(say);
            break;
        }
        case 1: {
            cm.sendYesNo("#b(경고판에 잘못 된 내용이 적혀있지는 않은 것 같다. 확인란에 O표를 하고 가자.)#k");
            break;
        }
        case 2: {
            var say = "#b(경고판의 확인란에 O를 표시했다.)#k\r\n\r\n#e먼지 바람 언덕 경고판#n\r\n";
            say += "\r\n출몰 몬스터 : 스텀프, 다크 스텀프.";
            say += "\r\n특이 사항 : 불타는 땅으로 이어지는 세 갈래 길.";
            say += "\r\n확인 : O";
            cm.sendNext(say);
            break;
        }
        case 3: {
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) == -1) {
                cm.forceStartQuest(22597, java.lang.Integer.parseInt(cm.getQuestRecord(22597).getCustomData()) + 1 + "");
                if (cm.getInfoQuest(22606).equals("")) {
                    cm.updateInfoQuest(22606, "" + cm.getMapId());
                } else {
                    cm.updateInfoQuest(22606, cm.getInfoQuest(22606) + ";" + cm.getMapId());
                }
            }
            cm.sendNextPrev("페리온 경고판을 " + cm.getQuestRecord(22597).getCustomData() + "개 확인했다. 다섯 개 모두 확인하면 마이크에게 보고하자.");
            break;
        }
        case 4: {
            cm.dispose();
            break;
        }
        case 5: {
            var say = "#e와일드 보어의 땅 경고판#n\r\n";
            say += "\r\n출몰 몬스터 : 와일드 보어, 겁 먹은 와일드 보어.";
            say += "\r\n특이 사항 : 없음.";
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) != -1) {
                cm.dispose();
                say += "\r\n확인 : O";
            } else {
                say += "\r\n확인 : ";
            }
            cm.sendNext(say);
            break;
        }
        case 6: {
            cm.sendYesNo("#b(경고판에 잘못 된 내용이 적혀있지는 않은 것 같다. 확인란에 O표를 하고 가자.)#k");
            break;
        }
        case 7: {
            var say = "#b(경고판의 확인란에 O를 표시했다.)#k\r\n\r\n#e와일드 보어의 땅 경고판#n\r\n";
            say += "\r\n출몰 몬스터 : 와일드 보어, 겁 먹은 와일드 보어.";
            say += "\r\n특이 사항 : 없음.";
            say += "\r\n확인 : O";
            cm.sendNext(say);
            break;
        }
        case 8: {
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) == -1) {
                cm.forceStartQuest(22597, java.lang.Integer.parseInt(cm.getQuestRecord(22597).getCustomData()) + 1 + "");
                if (cm.getInfoQuest(22606).equals("")) {
                    cm.updateInfoQuest(22606, "" + cm.getMapId());
                } else {
                    cm.updateInfoQuest(22606, cm.getInfoQuest(22606) + ";" + cm.getMapId());
                }
            }
            cm.sendNextPrev("페리온 경고판을 " + cm.getQuestRecord(22597).getCustomData() + "개 확인했다. 다섯 개 모두 확인하면 마이크에게 보고하자.");
            break;
        }
        case 9: {
            cm.dispose();
            break;
        }
        case 10: {
            var say = "#e야생 돼지의 땅 경고판#n\r\n";
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) != -1) {
                say += "\r\n출몰 몬스터 : 와일드 보어, 아이언 호그.";
            } else {
                say += "\r\n출몰 몬스터 : 와일드 보어, 아이언 호그, 고스텀프.";
            }
            say += "\r\n특이 사항 : 없음.";
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) != -1) {
                cm.dispose();
                say += "\r\n확인 : O";
            } else {
                say += "\r\n확인 : ";
            }
            cm.sendNext(say);
            break;
        }
        case 11: {
            var say = "경고판에 없는 몬스터가 하나 있는 것 같다. 잘못 된 몬스터의 이름은 지우도록 하자.#b";
            say += "\r\n#L0#와일드 보어";
            say += "\r\n#L1#아이언 호그";
            say += "\r\n#L2#고스텀프";
            cm.sendSimple(say);
            break;
        }
        case 12: {
            if (selection != 2) {
                cm.dispose();
            } else {
                var say = "#b(경고판에 잘못 적혀 있던 이름을 지우고 확인란에 O를 표시했다.)#k\r\n\r\n#e야생 돼지의 땅 경고판#n\r\n";
                say += "\r\n출몰 몬스터 : 와일드 보어, 아이언 호그.";
                say += "\r\n특이 사항 : 없음.";
                say += "\r\n확인 : O";
                cm.sendNext(say);
            }
            break;
        }
        case 13: {
            selection = 2;
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) == -1) {
                cm.forceStartQuest(22597, java.lang.Integer.parseInt(cm.getQuestRecord(22597).getCustomData()) + 1 + "");
                if (cm.getInfoQuest(22606).equals("")) {
                    cm.updateInfoQuest(22606, "" + cm.getMapId());
                } else {
                    cm.updateInfoQuest(22606, cm.getInfoQuest(22606) + ";" + cm.getMapId());
                }
            }
            cm.sendNextPrev("페리온 경고판을 " + cm.getQuestRecord(22597).getCustomData() + "개 확인했다. 다섯 개 모두 확인하면 마이크에게 보고하자.");
            break;
        }
        case 14: {
            cm.dispose();
            break;
        }
        case 15: {
            var say = "#e철갑 돼지의 땅 경고판#n\r\n";
            say += "\r\n출몰 몬스터 : 아이언 보어, 아이언 호그.";
            say += "\r\n특이 사항 : 없음.";
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) != -1) {
                cm.dispose();
                say += "\r\n확인 : O";
            } else {
                say += "\r\n확인 : ";
            }
            cm.sendNext(say);
            break;
        }
        case 16: {
            cm.sendYesNo("#b(경고판에 잘못 된 내용이 적혀있지는 않은 것 같다. 확인란에 O표를 하고 가자.)#k");
            break;
        }
        case 17: {
            var say = "#b(경고판의 확인란에 O를 표시했다.)#k\r\n\r\n#e철갑 돼지의 땅 경고판#n\r\n";
            say += "\r\n출몰 몬스터 : 아이언 보어, 아이언 호그.";
            say += "\r\n특이 사항 : 없음.";
            say += "\r\n확인 : O";
            cm.sendNext(say);
            break;
        }
        case 18: {
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) == -1) {
                cm.forceStartQuest(22597, java.lang.Integer.parseInt(cm.getQuestRecord(22597).getCustomData()) + 1 + "");
                if (cm.getInfoQuest(22606).equals("")) {
                    cm.updateInfoQuest(22606, "" + cm.getMapId());
                } else {
                    cm.updateInfoQuest(22606, cm.getInfoQuest(22606) + ";" + cm.getMapId());
                }
            }
            cm.sendNextPrev("페리온 경고판을 " + cm.getQuestRecord(22597).getCustomData() + "개 확인했다. 다섯 개 모두 확인하면 마이크에게 보고하자.");
            break;
        }
        case 19: {
            cm.dispose();
            break;
        }
        case 20: {
            var say = "#e타오르는 열기 경고판#n\r\n";
            say += "\r\n출몰 몬스터 : 아이언 보어, 파이어 호그.";
            say += "\r\n특이 사항 : 불타는 땅의 끝. 더 이상 길 없음.";
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) != -1) {
                cm.dispose();
                say += "\r\n확인 : O";
            } else {
                say += "\r\n확인 : ";
            }
            cm.sendNext(say);
            break;
        }
        case 21: {
            cm.sendYesNo("#b(경고판에 잘못 된 내용이 적혀있지는 않은 것 같다. 확인란에 O표를 하고 가자.)#k");
            break;
        }
        case 22: {
            var say = "#b(경고판의 확인란에 O를 표시했다.)#k\r\n\r\n#e타오르는 열기 경고판#n\r\n";
            say += "\r\n출몰 몬스터 : 아이언 보어, 파이어 호그.";
            say += "\r\n특이 사항 : 불타는 땅의 끝. 더 이상 길 없음.";
            say += "\r\n확인 : O";
            cm.sendNext(say);
            break;
        }
        case 23: {
            if (cm.getInfoQuest(22606).indexOf("" + cm.getMapId()) == -1) {
                cm.forceStartQuest(22597, java.lang.Integer.parseInt(cm.getQuestRecord(22597).getCustomData()) + 1 + "");
                if (cm.getInfoQuest(22606).equals("")) {
                    cm.updateInfoQuest(22606, "" + cm.getMapId());
                } else {
                    cm.updateInfoQuest(22606, cm.getInfoQuest(22606) + ";" + cm.getMapId());
                }
            }
            cm.sendNextPrev("페리온 경고판을 " + cm.getQuestRecord(22597).getCustomData() + "개 확인했다. 다섯 개 모두 확인하면 마이크에게 보고하자.");
            break;
        }
        case 24: {
            cm.dispose();
            break;
        }
    }
}