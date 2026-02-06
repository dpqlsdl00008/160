var status = -1;

function start() {
    switch (cm.getMapId()) {
        case 930000000:
        case 930000010:
        case 930000100: {
            status = 7;
            action (1, 0, 2);
            return;
        }
        case 930000200: {
            status = -1;
            break;
        }
        case 930000300: {
            status = 3;
            break;
        }
        case 930000400: {
            status = 6;
            break;
        }
        case 930000600: {
            status = 10;
            break;
        }
        case 930000700: {
            status = 13;
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
            cm.sendNext("\r\n이곳에는 가시 덤불이 자라서 길을 방해하고 있어. 가시 덤불부터 없애야 해! #r가시 덤불은 독#k으로만 없앨 수 있는데, #b스톤 버그들에게서 얻어지는 독#k은 너무 지독해서 잘못 뿌렸다간 독 때문에 지나 갈 수 없게 돼.");
            break;
        }
        case 1: {
            var say = "그러니 먼저,";
            say += "\r\n#d1. 스톤 버그들을 잡아 획득 한 독을 가지고 웅덩이를 클릭한다.";
            say += "\r\n#d2. 물에 희석 된 독을 가지고 가시 덤불로 간다.";
            say += "\r\n#d3. 희석된 독을 가진 파티원이 가시 덤불을 클릭하여 없앤다.";
            say += "\r\n#k자, 할 수 있겠지?#d";
            cm.sendSimple(say + "\r\n#L0#1. 이곳에서 나가고 싶어요.");
            break;
        }
        case 2: {
            status = 7;
            action (1, 0, 2);
            break;
        }
        case 3: {
            cm.dispose();
            break;
        }
        case 4: {
            cm.sendYesNo("휴우... 다행히 여기까지 왔구나. 안개 때문에 널 찾지 못해서 당황하고 있었어. 그럼 더 깊은 숲으로 갈래? 네가 가면 너의 파티원들도 마법으로 함께 보내줄게.");
            break;
        }
        case 5: {
            cm.dispose();
            if (cm.allMembersHere() == false) {
                return;
            }
            cm.warpParty(cm.getMapId() + 200);
            break;
        }
        case 6: {
            cm.dispose();
            break;
        }
        case 7: {
            if (cm.haveItem(4001169, 20) == true) {
                cm.sendNext("\r\n스프라이트들을 모두 정화의 구슬에 담아왔구나! 좋아, 그럼 정화 된 스프라이트를 한 명 깨워 볼게! 더 깊은 숲의 상황은 그 녀석이 더 잘 알테니, 녀석의 말을 따라 줘!");
                return;
            }
            var say = "지금은 중독되어 버렸지만, 원래 스프라이트는 숲의 주민이였어. 내가 만든 정화의 구슬이라면 녀석들을 정화시킬 수 있을 거야! 정화의 구슬에 스프라이트 20마리를 담아와 줘!#d";
            say += "\r\n#L0#1. 정화 된 구슬을 주세요.";
            say += "\r\n#L1#2. 몬스터는 어떻게 담나요?";
            say += "\r\n#L2#3. 이곳에서 나가고 싶어요.";
            cm.sendSimple(say);
            break;
        }
        case 8: {
            switch (selection) {
                case -1: {
                    cm.dispose();
                    cm.gainItem(4001169, -20);
                    cm.showFieldEffect(true, "quest/party/clear");
                    cm.playSound(true, "Party1/Clear");
                    if (cm.getMap().containsNPC(2133004) == false) {
                        cm.spawnNpc(2133004, -182, -242);
                    }
                    break;
                }
                case 0: {
                    cm.dispose();
                    cm.gainItem(2270004, 10);
                    cm.sendNext("\r\n자, 정화된 구슬 10개를 줄게. 모자라면 와서 더 받아가도록 해.");
                    break;
                }
                case 1: {
                    cm.dispose();
                    break;
                }
                case 2: {
                    cm.sendYesNo("정말로 숲에서 나가겠어?");
                    break;
                }
            }
            break;
        }
        case 9: {
            cm.dispose();
            cm.warp(300030100, 0);
            break;
        }
        case 10: {
            cm.dispose();
            break;
        }
        case 11: {
            var say = "여기가 바로 괴인이 있는 #r독의 숲#k이야! 그런대... 지금은 어딜 갔는지 괴인이 보이지 않네? 마침 잘 되었어. 스프라이트의 말대로, 마력석을 가지고 제단 근처로 가면 뭔가 반응하지 않을까? 어서 해 봐.#d";
            say += "\r\n#L0#1. 이곳에서 나가고 싶어요.";
            say += "\r\n#L1#2. 마력석을 안 가지고 왔어요.";
            cm.sendSimple(say);
            break;
        }
        case 12: {
            cm.dispose();
            switch (selection) {
                case 0: {
                    status = 7;
                    action (1, 0, 2);
                    break;
                }
                case 1: {
                    cm.gainItem(4001163, 1);
                    cm.sendNext("\r\n자, 아까 얻은 마력석이야. 어서 제단 근처로 가져가 봐!");
                    break;
                }
            }
            break;
        }
        case 13: {
            cm.dispose();
            break;
        }
        case 14: {
            cm.sendNext("\r\n도와줘서 고마워! 결국 괴인의 정체는 알아내지 못했지만... 연구를 방해했으니, 언젠가 녀석이 튀어 나올 거야. 수확이 전혀 없었던 건 아니니, 지금으로선 이걸로 만족하자구.");
            break;
        }
        case 15: {
            cm.dispose();
            //Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4001198, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310050, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            cm.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("독 안개의 숲", true));
            cm.gainExp(999999);
            cm.warp(300030100, 0);
            break;
        }
    }
}