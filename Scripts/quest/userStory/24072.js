var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("연합의 결성 이후 블랙윙이 저를 감시하는 기색은 간혹 느꼈어요. 하지만 이처럼 공격을 가한 적은 없어요... 어쩌면 제가 #b미스텔테인#k을 꺼내려 했다는 사실을 알아챈 것 같아요.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("미스텔테인...? 아직 갖고 있는 건가?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("물론이지요. 왕께서 제게 잠시 맡기신 거잖아요. 스스로가 가진 힘을 파악하지도 못한 채 위험한 곳을 따라다닌 저를 위해... 하지만 마을이 위험하니 그만 돌려 드리려고 했어요.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("그런데 왜 네가 아니라 나를 습격한 거지?", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("#r블랙윙에는 변신을 할 수 있는 능력을 가진 자가 있어요. 어쩌면 메르세데스님을 없앤 후, 메르세데스님의 모습으로 변신해서 미스텔테인을 가져가려고 했을 지도 모르겠어요.#k");
            break;
        }
        case 5: {
            qm.sendNextPrevS("별 능력이 다 있는 녀석들이군...", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("네. 무력보다는 이런 부분이 위험한 녀석들이죠... 그 녀석들... 메르세데스님과의 대화를 모두 엿들었겠죠? 그럼 에우렐에 대해서도 들었을 테고... 메르세데스님! 어서 마을로 가요!");
            break;
        }
        case 7: {
            qm.sendAcceptDecline("첫 번째 계책이 실패했으니 다음으로는 블랙윙이 #b에우렐을 공격 할 지#k도 몰라요! 인질을 잡아서 미스텔테인을 내놓으라고 하는 거죠! 억측 일 지도 모르지만... 귀환 마법을 쓸게요!");
            break;
        }
        case 8: {
            qm.dispose();
            if (qm.getPlayerCount(910150220) < 1) {
                qm.forceStartQuest();
                qm.resetMap(910150220);
                qm.warp(910150220, 0);
            } else {
               var quest = Packages.server.quest.MapleQuest.getInstance(24071).getName();
               qm.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            }
            break;
        }
    }
}