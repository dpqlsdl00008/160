var status = -1;

function start() {
    switch (cm.getMapId()) {
        case 105000000: {
            status = -1;
            break;
        }
        case 105030000: {
            status = 1;
            break;
        }
        case 211000000: {
            status = 3;
            break;
        }
        case 220000000: {
            status = 5;
            break;
        }
        case 220050300: {
            status = 7;
            break;
        }
        case 300000100: {
            status = 9;
            break;
        }
        case 240000000: {
            status = 12;
            break;
        }
    }
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            var say = "\r\n안녕하십니까! 위험 지역이라면 어디든지 가는 위험 지역 총알 택시입니다! #m105000000#에서 #Cgreen##m105030000##k까지 운행하고 있습니다! 가격은 #Cgreen#15,000메소#k입니다.";
            cm.sendNext(say);
            break;
        }
        case 1: {
            cm.dispose();
            cm.warp(105030000, "west00");
            break;
        }
        case 2: {
            var say = "\r\n안녕하십니까! 위험 지역이라면 어디든지 가는 위험 지역 총알 택시입니다! #Cgreen#15,000메소#k를 지불하고 #Cgreen##m105000000##k까지 이동 하시겠습니까?";
            cm.sendNext(say);
            break;
        }
        case 3: {
            cm.dispose();
            cm.warp(105000000, "east00");
            break;
        }
        case 4: {
            var say = "#Cgreen#45,000메소#k를 지불하고 어느 지역으로 이동 하시겠습니까?#Cgreen#";
            say += "\r\n#L0#얼음 골짜기 2";
            say += "\r\n#L1#엘린 숲";
            say += "\r\n#L2#사자 왕의 성";
            say += "\r\n#L3#죽은 나무의 숲 4";
            cm.sendSimple(say);
            break;
        }
        case 5: {
            cm.dispose();
            switch (selection) {
                case 0: {
                    cm.warp(211040200, "under00");
                    break;
                }
                case 1: {
                    cm.warp(300000100, "in00");
                    break;
                }
                case 2: {
                    cm.warp(211060000, "out00");
                    break;
                }
                case 3: {
                    cm.warp(211041400, "west00");
                    break;
                }
            }
            break;
        }
        case 6: {
            var say = "#Cgreen#25,000메소#k를 지불하고 어느 지역으로 이동 하시겠습니까?#Cgreen#";
            say += "\r\n#L0#시간의 통로";
            say += "\r\n#L1#엘린 숲";
            cm.sendSimple(say);
            break;
        }
        case 7: {
            cm.dispose();
            switch (selection) {
                case 0: {
                    cm.warp(220050300, "in01");
                    break;
                }
                case 1: {
                    cm.warp(300000100, "in00");
                    break;
                }
            }
            break;
        }
        case 8: {
            var say = "\r\n안녕하십니까! 위험 지역 총알 택시입니다! 시간의 통로에서 #Cgreen#루디브리엄#k까지 운행하고 있습니다! 가격은 #Cgreen#35,000메소#k로 조금 비싸지만, 이용해서 절대 후회하지 않으실 거예요!";
            cm.sendNext(say);
            break;
        }
        case 9: {
            cm.dispose();
            cm.warp(220000000, "in02");
            break;
        }
        case 10: {
            var say = "\r\n안녕하십니까! 오시리아 대륙의 위험 지역이라면 어디든지 가는 위험 지역 총알 택시입니다! 현재 작은 숲에서 #Cgreen#루디브리엄, 엘나스#k까지만 운행하고 있습니다! 가격은 #Cgreen#55,000메소#k로 조금 비싸지만, 이용해서 절대 후회하지 않으실 거예요!";
            cm.sendNext(say);
            break;
        }
        case 11: {
            var say = "#Cgreen#55,000메소#k를 지불하고 어느 지역으로 이동 하시겠습니까?#Cgreen#";
            say += "\r\n#L0#루디브리엄";
            say += "\r\n#L1#엘나스";
            cm.sendSimple(say);
            break;
        }
        case 12: {
            cm.dispose();
            switch (selection) {
                case 0: {
                    cm.warp(220000000, "in02");
                    break;
                }
                case 1: {
                    cm.warp(211000000, "in01");
                    break;
                }
            }
            break;
        }
        case 13: {
            var say = "어느 지역으로 이동 하시겠습니까?#Cgreen#";
            say += "\r\n#L0#용의 숲 입구 (55,000메소)";
            say += "\r\n#L1#용의 둥지 입구 (20,000메소)";
            cm.sendSimple(say);
            break;
        }
        case 14: {
            cm.dispose();
            switch (selection) {
                case 0: {
                    cm.warp(240030000, "west00");
                    break;
                }
                case 1: {
                    cm.warp(240040500, "west00");
                    break;
                }
            }
            break;
        }
    }
}