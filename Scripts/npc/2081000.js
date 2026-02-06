var status = -1;

var value = 10200000;
var cEtc = [
[4000226, 2],
[4000229, 4],
[4000236, 3],
[4000237, 6],
[4000260, 3],
[4000261, 6],
[4000231, 7],
[4000238, 9],
[4000239, 12],
[4000241, 15],
[4000242, 20],
[4000234, 20],
[4000232, 20],
[4000233, 20],
[4000235, 100],
[4000243, 100],
];

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status > 9) {
            status--;
        } else {
            if (status == 3) {
                cm.sendNext("\r\n알았네. 구매 할 마음이 생기면 다시 찾아오게나.");
            }
            if (status == 7) {
                cm.sendNext("\r\n흐음. 생각 해 보고 결정해 주게나.");
            }
            cm.dispose();
            return;
        }
    }
    switch (status) {
        case 0: {
            var say = "내가 도울 일이라도 있는가?#b";
            say += "\r\n#L0#마법의 씨앗을 구매합니다.";
            say += "\r\n#L1#리프레를 위해서 무언가 하고 싶습니다.";
            if (cm.haveItem(4032531) == false) {
                say += "\r\n\r\n#L2##r하늘을 날기 위한 비약 재료 얻기";
            }
            cm.sendSimple(say);
            break;
        }
        case 1: {
            cSay = "자네는 아직 우리 마을에 온 지 얼마 되지 않았나 보군. 무엇을 도와 주면 좋겠는가?";
            cPrice = 30000;
            switch (selection) {
                case 0: {
                    var cGift = parseInt(cm.getPlayer().getOneInfoQuest(value, "tatamo_feellike"));
                    if (cGift > 23999) {
                        cPrice = 27000;
                        cSay = "우리 만난 적 있는가...? 낯이 많이 익군. 허허.. 무엇을 도와 주면 좋겠는가?";
                    }
                    if (cGift > 49999) {
                        cPrice = 24000;
                        cSay = "날씨가 참 좋구먼~ 이렇게 좋은 날씨에는 가족과 소풍을 다녀 오는건 어떻겠는가? 흠. 내가 자네를 처음 만났을 때가 생각나는군. 그땐 몰랐지만, 이렇게 우리 마을을 위해 힘 써주고... 고마운 젊은이가 될 줄 누가 알았겠는가. 허허... 무엇을 도와 주면 좋겠는가?";
                    }
                    if (cGift > 199999) {
                        cPrice = 18000;
                        cSay = "무엇을 도와주면 좋겠는가?";
                    }
                    if (cGift > 799999) {
                        cPrice = 12000;
                        cSay = "무엇을 도와주면 좋겠는가?";
                    }
                    cm.sendSimple(cSay + "#b\r\n#L0#마법의 씨앗을 구매하고 싶습니다.");
                    break;
                }
                case 1: {
                    status = 5;
                    var say = "마을에 기부해 줄 물건이라도 있는가?\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0#";
                    for (i = 0; i < cEtc.length; i++) {
                        say += "\r\n#L" + i + "##i" + cEtc[i] + "# #b#z" + cEtc[i] + "# #r(" + cm.itemQuantity(cEtc[i][0]) + ")#k";
                    }
                    cm.sendSimple(say);
                    break;
                }
                case 2: {
                    status = 9;
                    cm.sendNext("용을 타고다니는 괴물에 대한 조사는 어느정도 진척이 있나?");
                    break;
                }
            }
            break;
        }
        case 2: {
            cm.sendGetNumber("\r\n흐음. 마법의 씨앗을 구매하고 싶단 건가? 귀한 물건이기 때문에 많이 줄 수 없다네. 가격은 개당 #b" + cm.getPlayer().getNum(cPrice) + "메소#k일세. 몇 개 구매하고 싶은가?\r\n　\r\n", 1, 1, parseInt(cm.getMeso() / cPrice));
            break;
        }
        case 3: {
            v1 = selection;
            if (selection < 1) {
                cm.dispose();
                return;
            }
            cm.sendYesNo("흐음. 그렇다면 #b#z4031346#을 " + v1 + "개 구매#k하고 싶은 것인가? 총 가격은 #r" + cm.getPlayer().getNum(cPrice * selection) + "메소#k라네. 정말 구매하겠는가?");
            break;
        }
        case 4: {
            cm.dispose();
            if (cm.getMeso() < (cPrice * v1) || cm.canHold(4031346, v1) == false) {
                cm.sendNext("\r\n메소는 충분히 갖고 있는 지, 또는 인벤토리 공간이 부족한 건 아닌 지 다시 한 번 확인을 해보게. 총 가격은 #b" + cm.getPlayer().getNum(cPrice * v1) + "메소#k라네.");
                return;
            }
            cm.gainMeso(-(cPrice * v1));
            cm.gainItem(4031346, v1);
            cm.sendNext("\r\n좋은 곳에 사용하기를 바라네.")
            break;
        }
        case 5: {
            cm.dispose();
            break;
        }
        case 6: {
            if (cm.itemQuantity(cEtc[selection][0]) < 1) {
                cm.sendNext("\r\n자네... 기부해 줄 물건은 제대로 갖고 있는 건가?");
                cm.dispose();
                return;
            }
            v2 = selection;
            cm.sendGetNumber("\r\n#b#z" + cEtc[selection][0] + "##k(을)를 마을에 기부하고 싶다 이건가...? 몇 개나 기부하고 싶은가?\r\n　\r\n", 1, 1, cm.itemQuantity(cEtc[selection][0]));
            break;
        }
        case 7: {
            if (selection < 1) {
                cm.sendNext("\r\n자네... 기부해 줄 물건은 제대로 갖고 있는 건가?");
                cm.dispose();
                return;
            }
            v3 = v2;
            v4 = selection;
            cm.sendYesNo("오오. 그렇다면 #b#z" + cEtc[v2][0] + "##k(을)를 #r" + selection + "개#k 기부하고 싶다 이건가?");
            break;
        }
        case 8: {
            cm.dispose();
            var cGift = parseInt(cm.getPlayer().getOneInfoQuest(value, "tatamo_feellike"));
            var uGift = (cGift + (cEtc[v3][1] * v4));
            if (uGift > 799999) {
                uGift = 800000;
            }
            cm.gainItem(cEtc[v3][0], -(cEtc[v3][1] * v4));
            cm.getPlayer().updateOneInfoQuest(value, "tatamo_feellike", uGift + "");
            cm.sendNext("\r\n마을을 위해 물건을 기부해 주어서 정말 고맙네!");
            cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CUserLocal.chatMsg(Packages.handling.ChatType.GameDesc, "촌장 타타모에게 " + Packages.server.MapleItemInformationProvider.getInstance().getName(cEtc[v3][0]) + " " + (cEtc[v3][1] * v4) + "개를 기부하고 리프레 마을 기여도를 " + (cEtc[v3][1] * v4) + "만큼 획득하였습니다. 현재 리프레 마을 기여도는 " + uGift + "입니다."));
            break;
        }
        case 9: {
            status = 0;
            action(1, 0, 2);
            break;
        }
        case 10: {
            cm.sendNextPrev("\r\n뭐라고? #b마타타#k형님을 만났다고? 그래 평생을 세계 각지를 돌며 모험을 즐기더니 결국 마을로 돌아왔군. 흥. 마을을 위해 해준 것도 없는 형님이지만 이번 만큼은 조금 도움이 되는가 보군.");
            break;
        }
        case 11: {
            cm.sendNextPrevS("\r\n드래곤 라이더를 쫓으려면 하늘을 날아야 하고, 그러기 위해서 #r용족의 이끼 추출액#k이 필요 합니다.", 2);
            break;
        }
        case 12: {
            cm.sendNextPrev("\r\n용족의 이끼 추출액? 그것은 우리 하프링거족의 특요약 제조에 사용되는 것인데 필요하다면 주는 것은 어렵지 않지만 그걸로 하늘을 날 수 있다고?");
            break;
        }
        case 13: {
            cm.sendNextPrevS("\r\n네. 마타타 아저씨가 하늘을 나는 비약을 제조하려면 그것이 필요하다고 했어요.", 2);
            break;
        }
        case 14: {
            cm.sendNextPrev("\r\n흠 그렇군. 좋아 주도록 하지. 부디 드래곤 라이더 녀석을 혼내주게.");
            break;
        }
        case 15: {
            cm.sendNextPrev("\r\n그럼 계속 조사를 해주게나. 부탁하네.");
            break;
        }
        case 16: {
            cm.dispose();
            cm.gainItem(4032531, 1);
            break;
        }
    }
}