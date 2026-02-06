var status = -1;
var mBelt = [[1132112, 25], [1132113, 50], [1132114, 100], [1132115, 125]];

function start() {
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
            var say = "안녕하세요~ 무엇을 도와드릴까요?\r\n#b";
            //say += "\r\n#L0#무릉 도장을 이용하고 싶어.";
            say += "\r\n#L1#데미지를 측정해보고 싶어.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (selection == 1) {
                status = 4;
                var jump = "안개 숲 수련장에서 1분 동안 공격하여\r\n누적 된 데미지를 확인해볼 수 있습니다.\r\n";
                //jump += "\r\n#Cgray#　- 몬스터의 방어율 일정 무시 : #Cyellow#+0%";
               // jump += "\r\n#Cgray#　- 보스 공격 시 데미지 : #Cyellow#+0%\r\n";
               // jump += "\r\n#Cgray#　- 보스 몬스터 판정 : #Cyellow#활성화";
                //jump += "\r\n#Cgray#　- 도트 데미지 적용 : #Cyellow#비활성화";
                jump += "\r\n#L0##b안개 숲 수련장에 입장하고 싶어.#k";
                jump += "\r\n#L1##r안개 숲 수련장 순위를 확인하고 싶어.#k";
                cm.sendSimple(jump);
                return;
           }
            var say = "우리 사부님은 무릉에서 최고로 강한 분이지. 그런 분에게 네가 도전하겠다고? 나중에 후회하지마.#b";
            say += "\r\n#L0#무릉 도장에 도전해 볼게.";
            say += "\r\n#L1#무공의 허리띠를 받고 싶어.";
            say += "\r\n#L2#무릉 도장에서 받을 수 있는 보상을 확인하고 싶어.";
            //say += "\r\n#L3#하드 모드에서 내 점수랑 등급을 알고 싶어.";
            //say += "\r\n#L5#오늘 남은 도전 횟수를 확인하고 싶어.";
            cm.sendSimple(say);
            break;
        }
        case 2: {
            s1 = selection;
            switch (selection) {
                case 0: {
                    cm.dispose();
                    if (cm.getParty() != null) {
                        cm.sendNext("\r\n파티로는 입장 할 수 없어! 혼자서 도전하라구! 겁쟁이냐?");
                        return;
                    }
                    var mEnter = true;
                    for (i = 100; i < 4300; i+=100) {
                        if (cm.getPlayerCount(925060000 + i) > 0) {
                            mEnter = false;
                        } else {
                            cm.getMap(925060000 + i).resetFully();
                        }
                    }
                    if (!mEnter) {
                        cm.sendNext("\r\n현재 접속한 채널에서는 다른 유저가 입장을 하고 있습니다. 다른 채널에서 진행하여 주시길 바랍니다.");
                        return;
                    }
                    cm.getPlayer().setMulungTime(java.lang.System.currentTimeMillis());
                    cm.warp(925060100, 0);
                    break;
                }
                case 1: {
                    if (cm.itemQuantity(4001620) < 25) {
                        cm.dispose();
                        var say = "네가 무릉 도장에서 #z4001620#를 모아오면 #b무공의 허리띠#k를 얻을 수 있어. 무릉 도장에서 가끔씩 떨어지는 #b허리띠 전용 주문서#k로 허리띠를 업그레이드 할 수 있으니까, 잘 모아두라구.";
                        say += "\r\n#e#Cgray#[무공의 증표 보상 : 유효 기간 15일]#k#n";
                        for (i = 0; i < mBelt.length; i++) {
                            say += "\r\n#i" + mBelt[i][0] + "# #b#z" + mBelt[i][0] + "# #r(무공의 증표 " + mBelt[i][1] + "개 필요)#k";
                        }
                        cm.sendNext(say);
                        return;
                    }
                    cm.sendYesNo("#i4001620# #b#z4001620##k가 있으면, #b허리띠#k를 줄께. 단, 이 허리띠는 #e#r15일#k#n 뒤에 사라지니까, 또 받고 싶다면 무공의 증표를 다시 모아오라구.");
                    break;
                }
                case 2: {
                    cm.dispose();
                    var say = "네가 하드 모드를 어떻게 도전했는 지에 따라 점수를 얻을 수 있어. 이 점수를 일주일 동안 누적하면, 누적 된 점수에 따라 보상을 받을 수 있어. 네가 열심히 한 만큼 보상을 받을 수 있으니까 열심히 해보라구.";
                    say += "\r\n#e#Cgray#[하드 모드 보상]#k#n";
                    say += "\r\n#e#bS 등급 : #i1022135# #z1022135# #r(유효 기간 : 7일)#k#n";
                    say += "\r\n#e#bA 등급 : #i1022136# #z1022136# #r(유효 기간 : 7일)#k#n";
                    say += "\r\n#e#bB 등급 : #i2022957# #z2022957# 3개#r(유효 기간 : 7일)#k#n";
                    cm.sendNext(say);
                    break;
                }
                case 3: {
                    cm.dispose();
                    break;
                }
                case 4: {
                    cm.dispose();
                    cm.sendNext("\r\n우리 사부님은 무릉에서 가장 강한 분이야. 그런 사부님께서 만드신 곳이 바로 이 무릉 도장이라는 것이지. 무릉 도장은 40층이나 되는 높은 건물이야. 하나 하나 올라가면서 자신을 수련할 수 있어. 물론 너의 실력으로는 끝까지 가기 힘들겠지만.");
                    break;
                }
                case 5: {
                    cm.dispose();
                    cm.sendNext("\r\n오늘 무릉 도장에는 0번 참여 할 수 있어. 그런 건 알아서 세어 보라고.");
                    break;
                }
            }
            break;
        }
        case 3: {
            cm.dispose();
            switch (s1) {
                case 1: {
                    var say = "어떤 허리띠를 받길 원해?\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
                    for (i = 0; i < mBelt.length; i++) {
                        say += "\r\n#L" + i + "##i" + mBelt[i][0] + "# #d#z" + mBelt[i][0] + "##k #r(#z4001620# " + mBelt[i][1] + "개 필요)#k";
                    }
                    cm.sendSimple(say);
                    break;
                }
            }
            break;
        }
        case 4: {
            cm.dispose();
            break;
        }
        case 5: {
            cm.dispose();
            if (selection == 0) {
                if (cm.getPlayerCount(993198000) > 0) {
                    cm.sendNext("");
                    return;
                }
                var eScript = cm.getEventManager("etc_traningroom");
                if (eScript != null) {
                    eScript.setProperty("map", "993198000");
                    eScript.startInstance(cm.getPlayer());
                }
            } else {
                cm.sendNext(cm.displayTraningRanks(cm.getPlayer()));
            }
            break;
        }
    }
}