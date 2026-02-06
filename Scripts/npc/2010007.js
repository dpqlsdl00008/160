// 자네가 속한 길드의 길드 마스터가 #r30일#k 이상 접속하지 않았군. 길드의 원활한 운영을 위해 길드 마스터를 강제로 변경하고 싶으면, 길드 마스터가 되려는 자가 나에게 말을 걸면 된다네.\r\n\r\n자네가 새로운 길드 마스터가 되려는 것 인가?

var status = -1;

function start() {
    if (cm.getGuild() == null) {
        cm.sendNext("\r\n당신... 혹시 길드에 관심이 있어서 나를 찾아 온 것 인가?");
        return;
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
        if ((status == 1 && v1 == 1) || (status == 2 && v2 == 1)) {
           cm.sendNext("\r\n그래, 잘 생각 했어. 힘들게 성장한 길드를 없애면 안되지...");
           cm.dispose();
           return;
        }
        if (status == 2 && v2 == 2) {
           cm.sendNext("\r\n수수료가 부담스러운 건가? 시간이 지나면 메소 정도는 금방 모을 수 있을 거니 너무 걱정 말게. 그럼, 나중에 또 찾아 오게나.");
           cm.dispose();
           return;
        }
        if (status == 2 && (v2 == 4 || v2 == 5)) {
           status = 0;
           action(1, 0, v1);
           return;
        }
        if (status == 3 && v3 == 5) {
           status = 1;
           action(1, 0, v2);
           return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            var say = "원하는 것이 무엇인가? 말해보게...#d";
            if (cm.getGuild() != null) {
                say = "자, 무엇을 도와줄까?#d";
            }
            if (cm.getPlayerStat("GRANK") != 1) {
                say += "\r\n#L4#길드가 무엇인지 알려 주세요.";
                say += "\r\n#L5#길드를 만들려면 어떻게 해야 돼요?";
            }
            if (cm.getGuild() == null) {
                say += "\r\n#L0#길드를 만들고 싶어요.";
            }
            if (cm.getPlayerStat("GRANK") == 1) {
                say += "\r\n#L2#길드 인원 수를 늘리고 싶어요.";
                say += "\r\n#L1#길드를 해체하고 싶어요.";
                say += "\r\n#L3#길드 마스터를 변경하고 싶어요.";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
           v1 = selection;
            switch (selection) {
                case 0: {
                    if (cm.getPlayerStat("GID") > 0) {
                        cm.dispose();
                        cm.sendNext("\r\n흐음... 이미 길드에 가입되어 있는 것 같은데?");
                        return;
                    }
                    cm.sendYesNo("오! 길드를 등록하러 왔군... 길드를 등록하려면 #b1,500,000 메소#k가 필요하다네. 준비는 되어 있을 것이라 믿겠네. 자~ 길드를 만들겠는가?");
                    break;
                }
                case 1: {
                    cm.sendYesNo("길드를 정말 해체하고 싶은가? 이런... 이런... 한 번 해체를 해버리면 자네의 길드는 영원히 삭제되어 버린다네. 여러가지 길드 특권도 물론 함께 사라지지. 그래도 하겠는가?");
                    break;
                }
                case 2: {
                    cm.sendNext("\r\n길드 인원을 늘리고 싶어서 왔는가? 음. 길드가 많이 성장했나 보군~ 잘 알겠지만 길드 인원을 늘리려면 우리 길드 본부에 다시 등록을 해야 돼지. 물론 수수료로 메소를 사용해야 하지만 말일세. 참고로 길드원은 최대 #b200명#k까지 늘릴 수 있다네.");
                    break;
                }
                case 3: {
                    cm.dispose();
                    cm.sendNext("\r\n길드 마스터의 자리가 부담스러운 건가? 길드원 리스트에서 위임 할 대상을 선택하고 마우스 우 클릭을 해보게. 길드 마스터 위임 버튼을 클릭하면 길드 마스터를 위임 할 수 있다네. 단, 상대방이 온라인인 경우에만 위임 할 수 있지.");
                    //cm.sendGetText("　\r\n길드 마스터의 자리가 부담스러운 건가? 그렇다면 위임을 진행 할 #b캐릭터의 이름#k을 입력하여 주게나.\r\n　\r\n");
                    break;
                }
                case 4: {
                    cm.sendNext("\r\n길드란... 우선 소모임 같은 것으로 이해하면 되네. 서로 마음이 맞는 사람들끼리 같은 목적을 가지고 모임을 만든 것이네. 하지만 길드는 그런 목적으로 만든 소모임을 길드 본부에 정식 등록을 해서 공식적으로 인정 된 모임이지.");
                    break;
                }
                case 5: {
                    cm.sendNext("\r\n길드를 만들려면 레벨이 적어도 50은 되어야 하지.");
                    break;
                }
            }
            break;
        }
        case 2: {
            v2 = v1;
            switch (v1) {
                case 0: {
                    cm.sendNext("\r\n그럼 원하는 길드 이름을 입력하게나.");
                    break;
                }
                case 1: {
                    cm.sendYesNo("다시 한 번 묻겠네. 모든 길드 특권을 포기하고, 정말 길드를 해체 하겠는가?");
                    break;
                }
                case 2: {
                    cm.sendYesNo("현재 길드 최대 인원 수는 #b" + cm.getGuild().getCapacity() + "명#k이고, #b5명#k 늘리는데 필요한 수수료는 #b500,000 메소#k일세. 어때, 길드 인원을 늘릴텐가?");
                    break;
                }
                case 3: {
                    var mOnline = false;
                    var gName = cm.getText();
                    var con = Packages.database.DatabaseConnection.getConnection();
                    var ps = con.prepareStatement("SELECT * FROM characters WHERE name = ?");
                    ps.setString(1, gName);
                    var rs = ps.executeQuery();
                    while (rs.next()) {
                        nLeaderID = rs.getInt("id");
                    }
                    rs.close();
                    ps.close();
                    con.close();
                    var gMember = cm.getGuild().getMembers();
                    if (gMember != null) {
                        var it = gMember.iterator();
                        while (it.hasNext()) {
                            var cPlayer = it.next();
                            if (cPlayer.getName().contains(gName)) {
                                mOnline = true;
                            }
                        }
                    }
                    if (!mOnline) {
                        cm.dispose();
                        cm.sendNext("\r\n음... 자네가 입력한 #b" + gName + "#k라는 자는 자네와 같은 길드가 아닌 것 같군. 다시 한 번 확인하여 주게나.");
                        return;
                    }
                    cm.sendYesNo("정말 #b" + gName + "#k에게 길드장 위임을 진행하겠나?");
                    nChangeID = nLeaderID;
                    break;
                }
                case 4: {
                    cm.sendNextPrev("\r\n길드 활동을 통해 여러 가지 혜택을 얻을 수도 있지. 예를 들어, 길드 스킬이나 길드 전용 아이템 같은 것을 얻을 수 있다네.");
                    break;
                }
                case 5: {
                    cm.sendNextPrev("\r\n그리고 #b1,500,000 메소#k가 필요해... 이건 길드를 등록하는 데 필요한 수수료라네.");
                    break;
                }
            }
            break;
        }
        case 3: {
            v3 = v2;
            switch (v2) {
                case 0: {
                    cm.dispose();
                    cm.genericGuildMessage(1);
                    break;
                }
                case 1: {
                    cm.dispose();
                    cm.disbandGuild();
                    break;
                }
                case 2: {
                    cm.dispose();
                    cm.increaseGuildCapacity(false);
                }
                case 3: {
                    cm.dispose();
                    cm.getGuild().changeGuildLeader(nChangeID);
                    break;
                }
                case 4: {
                    status = -1;
                    action(1, 0, 0);
                    break;
                }
                case 5: {
                    cm.sendNextPrev("\r\n자... 길드를 등록하고 싶다면 내게 오라고~ 아! 물론 이미 다른 길드에 가입되어 있으면 안 돼!!");
                    break;
                }
            }
            break;
        }
        case 4: {
            status = -1;
            action(1, 0, 0);
            break;
        }
    }
}