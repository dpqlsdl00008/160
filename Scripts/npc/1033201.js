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
        switch (status) {
            case 15: {
                cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.NPCPacket.getNPCTalk(1033200, 0, "#b(기절한 듯하다... 다행히 숨은 쉬고 있는 것 같다. 프리드는 마법사답게 체력이 약하니까... 아프리엔이 보호해주지 않았더라면 죽었을지도 모른다.)#k", "00 01", 32, 1033200));
                cm.dispose();
                return;
            }
            default: {
                status--;
            }
        }
    }
    switch (status) {
        case 0: {
            cm.sendNextS("아프리엔! 괜찮은거야? 프리드는!?... 휴, 정신만 잃은거구나...", 6);
            break;
        }
        case 1: {
            cm.sendNextPrevS("메르세데스... 살아 남았군.", 32);
            break;
        }
        case 2: {
            cm.sendNextPrevS("당연하지! 봉인까지 성공했는데 여기서 주저 앉을 수는 없잖아! 그런데... 넌 안 괜찮아 보이는데? 괜찮은거야? 다른 녀석들은? 다들 어디에 있는 거지?", 6);
            break;
        }
        case 3: {
            cm.sendNextPrevS("#b검은 마법사의 봉인에는 성공#k했지만 그가 마지막에 쓴 마법에서 일어난 폭발 때문에 모두 뿔뿔이 흩어져 버린 것 같군. 우리가 같은 곳으로 온 게 우연에 가까워.", 32);
            break;
        }
        case 4: {
            cm.sendNextPrevS("아, 그렇네. 정말 멀리 날아왔네. 하지만 무사해서 다행이야...", 6);
            break;
        }
        case 5: {
            cm.sendNextPrevS("긴장이 풀려서 그런가? 힘이 빠진다... 아니, 그냥 힘이 빠지는 게 아니라... 이상하게 추워.", 6);
            break;
        }
        case 6: {
            cm.sendNextPrevS("그러고보니... 여기 원래 눈이 오는 지역이었던가? 이렇게 불타고 있는데 눈이 내리다니... 이상한 일이네...", 6);
            break;
        }
        case 7: {
            cm.sendNextPrevS("...느껴지지 않는 건가, 메르세데스? 이 #r거대한 저주#k가... 너와 프리드. 그리고 다른 모두에게 내려진 검은 마법사의 저주가 말이다.", 32);
            break;
        }
        case 8: {
            cm.sendNextPrevS("저... 주?", 6);
            break;
        }
        case 9: {
            cm.sendNextPrevS("지독하게 차가운 기운이 너를 감싸고 있는 것이 보이는군. 체력이 강하던 때라면 모를까... 전투로 약해진 지금은 무척이나 위험해. ...검은 마법사는 우리를 쉽게 놓아주지 않을 모양이군...", 32);
            break;
        }
        case 10: {
            cm.sendNextPrevS("다른 녀석들은 괜찮을 거야. 다들 튼튼하니까! 하지만 프리드가 걱정인데... 이 녀석, 안 그래도 체력이 약하잖아.", 6);
            break;
        }
        case 11: {
            cm.sendNextPrevS("프리드는 내가 돌볼 수 있을 것이다. 걱정할 것 없다. ...그보다 메르세데스, 네가 더 걱정이다. 너는 #b엘프의 왕#k. 네게 내려진 저주는... #r엘프 전체에 내려진 것과 동일#k하지 않나?", 32);
            break;
        }
        case 12: {
            cm.sendNextPrevS("...!", 6);
            break;
        }
        case 13: {
            cm.sendNextPrevS("어서 #r에우렐#k로 가봐라. 만약 정말 #b검은 마법사의 저주가 엘프 전체에 영향을 미쳤다#k면... 왕인 네가 가봐야 할 것이다.", 32);
            break;
        }
        case 14: {
            cm.sendNextPrevS("알았어! 아프리엔. ...다시 만날 수 있겠지?", 6);
            break;
        }
        case 15: {
            cm.sendYesNoS("#b(동료들이 걱정되지 않는 건 아니지만... 지금은 그들을 믿을 수 밖에 없다. 귀환 스킬로 마을에 가자.)#k", 6);
            break;
        }
        case 16: {
            var say = "아직 할 말이 남은 건가. 메르세데스? 물어보고 싶은 게 있으면 어서 물어라.#b";
            say += "\r\n#L0#지금 어떤 상황인 거지?";
            say += "\r\n#L1#다른 동료들은?";
            say += "\r\n#L2#너는 괜찮아?";
            say += "\r\n#L3#왜 가야 한다는 거야?";
            say += "\r\n#L4#에우엘로 출발 하겠어!";
            cm.sendSimple(say);
            break;
        }
        case 17: {
            switch (selection) {
                case 0: {
                    cm.dispose();
                    cm.sendNextS("검은 마법사의 봉인에는 성공했다. 허나 마지막에 일어난 폭발 때문에 모두들 뿔뿔이 흩어져 버린 것 같군... 다행히 너와 프리드는 같은 방향으로 왔구나. 그보다는 검은 마법사가 마지막에 쓴 저주가 문제로구나.", 32);
                    break;
                }
                case 1: {
                    cm.dispose();
                    cm.sendNextS("모두들 뿔뿔이 흩어지고 말았다. 하지만 걱정할 것 없다. 다들 메이플 월드에서 손꼽히는 강자이고, 또 산전수전 겪은 자들이니 자기 몸 하나는 챙겼을 거다. ...그러기를 바래야지.", 32);
                    break;
                }
                case 2: {
                    cm.dispose();
                    cm.sendNextS("걱정할 것 없다. 이래 뵈도 오닉스 드래곤... 엘프에게 걱정을 받아야 할 정도로 약하지는 않다. 프리드가 걱정이지만... 알아서 해결하겠다.", 32);
                    break;
                }
                case 3: {
                    cm.dispose();
                    cm.sendNextS("검은 마법사가 남긴 저주 때문이다. 이 차갑고도 음산한 기운은 우리를 얼리고 말 것이다. 어쩌면, 영원히... 그러기 전에 넌 어서 엘프 마을, 에우엘로 가라. 엘프 왕인 너에게 내려진 저주는 곧 엘프 전체에 내려진 것이나 마찬가지.", 32);
                    break;
                }
                case 4: {
                    cm.sendNextS("어서 가라. 가서 네 종족을 구해라.", 32);
                    break;
                }
            }
            break;
        }
        case 18: {
            cm.sendNextPrevS("응. 꼭 다시 만나자, 아프리엔!", 6);
            break;
        }
        case 19: {
            cm.dispose();
            cm.warp(910150001, 0);
            break;
        }
    }
}