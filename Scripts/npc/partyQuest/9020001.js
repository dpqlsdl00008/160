var status = -1;
value = 10200000;

function start() {
    switch (cm.getMapId()) {
        case 910340100: {
            status = -1;
            break;
        }
        case 910340200: {
            status = 2;
            break;
        }
        case 910340300: {
            status = 4;
            break;
        }
        case 910340400: {
            status = 6;
            break;
        }
        case 910340500: {
            status = 8;
            break;
        }
    }
    action(1, 0, 0);
}

function cEffect(cStage) {
    if (cm.isLeader() == false) {
        return;
    }
    var pMember = cm.getParty().getMembers();
    if (pMember != null) {
        var it = pMember.iterator();
        while (it.hasNext()) {
            var cUser = it.next();
            var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
            if (ccUser != null) {
                ccUser.updateOneInfoQuest(value, "pq_kerningcity_clear", cStage + "");
                ccUser.updateOneInfoQuest(value, "pq_kerningcity_answer", cStage + "");
            }
        }
    }
    switch (cStage) {
        case 5: {
            cm.sendNext("\r\n축하드립니다. 모든 스테이지를 클리어 하셨습니다. 이제 이 곳에서 더 이상 볼 일이 없으시면 저에게 말을 걸어주세요. 밖으로 보내드리겠습니다.");
            break;
        }
        default: {
            cm.sendNext("\r\n이번 스테이지를 클리어 한 것을 축하드립니다! 다음 스테이지로 통하는 포탈을 만들어 드리겠습니다. 제한 시간이 있으니 서둘러 주세요. 그럼 행운을 빕니다.");
            break;
        }
    }
    cm.showEffect(true, "quest/party/clear");
    cm.playSound(true, "Party1/Clear");
    cm.environmentChange(true, "gate");
}

function fEffect() {
    cm.showEffect(true, "quest/party/wrong_kor");
    cm.playSound(true, "Party1/Failed");
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
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_clear").equals("1")) {
                cm.sendNext("\r\n다음 스테이지로 통하는 포탈이 열렸습니다. 서둘러 주세요.");
                cm.dispose();
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").equals("0")) {
                cm.sendNext("\r\n안녕하세요. 첫 번째 스테이지에 오신 것을 환영합니다. 주변을 둘러 보면 리게이터가 돌아다니고 있는 것을 볼 수 있을 겁니다. 리게이터는 쓰러뜨리면 꼭 한 개의 #b쿠폰#k을 떨어 뜨립니다. 파티장을 제외 한 파티원 전원은 각 각 저에게 말을 걸어 문제를 받고 문제의 답에 해당하는 수 만큼 리게이터가 주는 #b쿠폰#k을 모아와야 합니다. 답 만큼 #b쿠폰#k을 모아오면 미션을 완료하게 됩니다. 파티장을 제외 한 모든 파티원이 #r개별 미션 클리어#k를 하면 스테이지를 클리어 하게 됩니다. 되도록 빨리 해결해야 더 많은 스테이지에 도전 할 수 있으므로 서둘러 주세요. 그럼 행운을 빕니다.");
                return;
            }
            if (cm.isLeader() == true) {
                cm.dispose();
                var pMember = cm.getParty().getMembers();
                if (pMember != null) {
                    var it = pMember.iterator();
                    while (it.hasNext()) {
                        var cUser = it.next();
                        var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                        if (ccUser != null) {
                            if (!ccUser.getOneInfoQuest(value, "pq_kerningcity_answer").equals("1")) {
                                cm.sendNext("\r\n죄송합니다만, 아직 미션을 완료하지 못한 파티원이 있습니다. 파티장을 제외 한 파티원 모두 개인 미션을 완료하셔야 이 스테이지를 클리어 하실 수 있습니다.");
                                return;
                            }
                        }
                    }
                    cEffect(1);
                    return;
                }
            }
            var v1 = parseInt(cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_question"));
            if (cm.haveItem(4001007, v1) == false) {
                cm.sendNext("\r\n정답이 아닙니다. 문제를 다시 알려드리죠.");
                return;
            }
            cm.dispose();
            cm.getPlayer().updateOneInfoQuest(value, "pq_kerningcity_answer", "1");
            cm.sendNext("\r\n정답입니다!");
            break;
        }
        case 1 : {
            cm.dispose();
            if (cm.isLeader() == true) {
                cm.getPlayer().updateOneInfoQuest(value, "pq_kerningcity_answer", "1");
                return;
            }
            var v1 = parseInt(cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_question"));
            cm.sendNext("\r\n미션입니다. #r쿠폰 " + v1 + "장#k을 모아오세요. 쿠폰은 이곳의 #r리게이터를 물리치면#k 얻을 수 있습니다.");
            break;
        }
        case 2: {
            cm.dispose();
            break;
        }
        case 3: {
            cm.dispose();
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_clear").equals("2")) {
                cm.sendNext("\r\n다음 스테이지로 통하는 포탈이 열렸습니다. 서둘러 주세요.");
                cm.dispose();
                return;
            }
            if (cm.isLeader() == false || cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").equals("1")) {
                if (cm.isLeader() == true) {
                    cm.getPlayer().updateOneInfoQuest(value, "pq_kerningcity_answer", Packages.server.Randomizer.shuffle(cm.getPlayer().isGM() == true ? "1000" : "1110"));
                }
                cm.sendNext("\r\n안녕하세요. 두 번째 스테이지에 오신 것을 환영합니다. 제 옆에 여러 개의 줄이 보일 것 입니다. 이 줄 중에서 #b3개가 다음 스테이지로 향하는 포탈#k과 통해 있습니다. 파티원 중에서 #b3명이 정답 줄을 찾아 매달리면#k 됩니다. 단, 줄을 너무 아래 쪽으로 잡고 매달리면 정답으로 인정되지 않으므로 줄을 잡고 충분히 위로 올라가 주시길 바랍니다. 그리고 반드시 3명만 줄을 잡고 있어야 합니다. 파티원이 줄에 매달리면 파티장은 #b저를 더블 클릭하여 정답인지 아닌지 확인#k하세요. 그럼 정답 줄을 찾아 주세요~!");
                return;
            }
            var v3 = cm.getPlayer().getMap().getNumPlayersInArea(0);
            var v4 = cm.getPlayer().getMap().getNumPlayersInArea(1);
            var v5 = cm.getPlayer().getMap().getNumPlayersInArea(2);
            var v6 = cm.getPlayer().getMap().getNumPlayersInArea(3);
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").equals((v3 + "") + (v4 + "") + (v5 + "") + (v6 + "") + "")) {
                cEffect(2);
                return;
            }
            fEffect();
            break;
        }
        case 4: {
            cm.dispose();
            break;
        }
        case 5: {
            cm.dispose();
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_clear").equals("3")) {
                cm.sendNext("\r\n다음 스테이지로 통하는 포탈이 열렸습니다. 서둘러 주세요.");
                cm.dispose();
                return;
            }
            if (cm.isLeader() == false || cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").equals("2")) {
                if (cm.isLeader() == true) {
                    cm.getPlayer().updateOneInfoQuest(value, "pq_kerningcity_answer", Packages.server.Randomizer.shuffle(cm.getPlayer().isGM() == true ? "10000" : "11100"));
                }
                cm.sendNext("\r\n안녕하세요. 세 번째 스테이지에 오신 것을 환영합니다. 옆에 고양이가 들어 있는 통이 놓인 발판들이 보일 것 입니다. 이 발판 중에서 #b3개가 다음 스테이지로 향하는 포탈#k과 통해 있습니다. 파티원 중에서 #b3명이 정답 발판을 찾아 위에 올라서면#k 됩니다. 단, 발판 끝에 아슬 아슬하게 걸쳐서 서지 말고 발판 중간에 서야 정답으로 인정되니 이 점 주의해 주시기 바랍니다. 그리고 반드시 3명만 발판 위에 올라가 있어야 합니다.");
                return;
            }
            var v7 = cm.getPlayer().getMap().getNumPlayersInArea(0);
            var v8 = cm.getPlayer().getMap().getNumPlayersInArea(1);
            var v9 = cm.getPlayer().getMap().getNumPlayersInArea(2);
            var v10 = cm.getPlayer().getMap().getNumPlayersInArea(3);
            var v11 = cm.getPlayer().getMap().getNumPlayersInArea(4);
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").equals((v7 + "") + (v8 + "") + (v9 + "") + (v10 + "") + (v11 + "") + "")) {
                cEffect(3);
                return;
            }
            var v12 = 0;
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").split("")[0].equals("1")) {
                v12 += v7;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").split("")[1].equals("1")) {
                v12 += v8;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").split("")[2].equals("1")) {
                v12 += v9;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").split("")[3].equals("1")) {
                v12 += v10;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").split("")[4].equals("1")) {
                v12 += v11;
            }
            fEffect();
            cm.getMap().broadcastMessage(Packages.tools.packet.CWvsContext.getTopMsg("현재 " + v12 + "개의 정답 발판을 선택 하셨습니다."));
            break;
        }
        case 6: {
            cm.dispose();
            break;
        }
        case 7: {
            cm.dispose();
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_clear").equals("4")) {
                cm.sendNext("\r\n다음 스테이지로 통하는 포탈이 열렸습니다. 서둘러 주세요.");
                cm.dispose();
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").equals("3")) {
                if (cm.getMonsterCount(cm.getMapId()) > 0) {
                    cm.sendNext("\r\n네 번째 스테이지에 대해 설명해 드리겠습니다. 이 곳에 있는 #b" + cm.getMonsterCount(cm.getMapId()) + "마리의 커즈아이#k를 모두 물리치면 됩니다. 그럼 힘내주세요.");
                    return;
                }
            }
            cEffect(4);
            break;
        }
        case 8: {
            cm.dispose();
            break;
        }
        case 9: {
            cm.dispose();
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").equals("5")) {
                Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310050, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
                cm.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("첫 번째 동행", true));
                cm.warp(910340700, "out00");
                return;
            }
            if (cm.getPlayer().getOneInfoQuest(value, "pq_kerningcity_answer").equals("4")) {
                if (cm.haveItem(4001008, 1) == false || cm.getMonsterCount(cm.getMapId()) > 0) {
                    cm.sendNext("\r\n안녕하세요. 마지막 스테이지에 오신 것을 환영합니다. 이번 스테이지는 이 곳의 최고 보스인 #r킹 슬라임#k을 물리치고 #b통행권 1장#k을 모아오시면 됩니다. 그럼 힘내주세요!");
                    return;
                }
            }
            cEffect(5);
            break;
        }
    }
}