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
            cm.dispose();
            switch (cm.getMapId()) {
                case 922010100: {
                    cm.floatMessage("차원의 라츠와 차원의 블랙 라츠를 모두 해치우고 차원의 통행증 20장을 모아라!", 5120018);
                    break;
                }
                case 922010400: {
                    var mCount = 0;
                    for (i = 0; i < 5; i++) {
                        mCount += cm.getChannelServer().getMapFactory().getMap(922010401 + i).getNumMonsters();
                    }
                    if (mCount > 0) {
                        cm.floatMessage("어두운 공간에 숨어있는 다크 아이와 쉐도우 아이를 모두 찾아서 퇴치하자!", 5120018);
                        cm.topMessage("다크 아이와 쉐도우 아이가 총 " + mCount + "마리 남았다. 모두 찾아서 퇴치하자!");
                        return;
                    }
                    cm.environmentChange(false, "gate");
                    cm.floatMessage("다크 아이와 쉐도우 아이를 모두 퇴치하였습니다. 옐로 그린 벌룬에게 말을 걸어 다음 스테이지로 이동해 주세요!", 5120018);
                    break;
                }
                case 922010401:
                case 922010402:
                case 922010403:
                case 922010404:
                case 922010405: {
                    if (cm.getMonsterCount(cm.getMapId()) > 0) {
                        cm.floatMessage("몬스터의 기운이 느껴집니다. 몬스터를 찾아 퇴치하여 주세요.", 5120018);
                        return;
                    }
                    cm.floatMessage("몬스터의 기운이 느껴지지 않습니다. 다른 방으로 이동하여 주세요.", 5120018);
                    break;
                }
                case 922010600: {
                    cm.floatMessage("숨겨진 상자의 암호를 풀고 꼭대기로 올라가라!", 5120018);
                    break;
                }
                case 922010700: {
                    cm.floatMessage("이 곳에 있는 롬바드를 모두 물리치자!", 5120018);
                    break;
                }
                case 922010800: {
                    cm.floatMessage("문제를 듣고 정답에 맞는 상자 위로 올라가라!", 5120018);
                    break;
                }
                case 922010900: {
                    cm.floatMessage("몬스터를 퇴치하고 알리샤르를 소환하여 퇴치하라!", 5120018);
                    break;
                }
            }
        }
    }
}