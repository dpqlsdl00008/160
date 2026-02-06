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
            qm.sendNext("과거 이 성에서 일어났던 일을 자세히 알고 계신 분은 만났나요? 네에? 루덴님이요? 그 분은 왕의 근위 기사 단장이셨어요. 그 분이 남아 계셨군요... 그래요, 루덴님께서는 뭐라고 하시던가요? 왜 레온이 저렇게 변해버렸는지는 들으셨나요?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(루덴에게 들은 이야기를 설명했다.)#k", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("맞아... 그래요, 그랬어요. 검은 구름이 하늘을 뒤덮은 그 날, 평범하게만 보이던 병사들이 돌변해 우리를 공격했어요. 성벽이 무너지고 지붕이 불타고... 전, 전 탑 안에서 연기에... 그런 일이 있었어요.");
            break;
        }
        case 3: {
            qm.sendNextPrevS("#b(이피아의 반응을 보니 루덴의 말은 사실인 모양이다.)#k", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("레온이 변해버린 건 그래서였군요. 저는 그저 레온을 원망만 하고 있었는데, 그러는 동안 그는 깊은 슬픔과 분노 속에서 사람에 대한 증오만을 키우고 있었어요. ...이제 더 이상 그렇게 두지는 않겠어요.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("#b뭔가 방법이 있으신가요?#k", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("성에는 루덴님도 모르는, 왕과 왕비만이 사용하는 길이 있어요. 원래는 다른 분께 알려드리면 안 되지만... 이번만은 예외로 할게요. 저와 함께 알현실로 가서 그를 만나요.");
            break;
        }
        case 7: {
            qm.sendAcceptDecline("제가 직접 그를 찾아가겠어요. 검은 기운을 가진 몬스터들 때문에 지금까지는 알현실로 직접 갈 수 없었지만... 당신께서 도와주신다면 가능할 거예요. 저와 함께 알현실로 가주세요! 부탁드려요!");
            break;
        }
        case 8: {
            qm.dispose();
            if (qm.getPlayerCount(921140000) != 0) {
                qm.getPlayer().dropMessage(5, "다른 캐릭터가 퀘스트를 진행 중이여서 들어 갈 수 없습니다.");
                return;
            }
            qm.resetMap(921140000);
            qm.warp(921140000, "out00");
            qm.getPlayer().dropMessage(5, "몬스터로부터 이피아를 보호하며 알현실까지 가자.");
            qm.forceStartQuest();
            break;
        }
    }
}