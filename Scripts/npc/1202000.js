var status = -1;

function start() {
    status = (cm.getMapId() != 140090000 ? -1 : cm.getInfoQuest(21019).equals("") ? 1 : 7);
    action(1, 0, 0);
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
            cm.sendSimple("뭔가 궁금하신 게 있나요? 있다면 다시 한 번 설명해 드릴게요.#b\r\n#L1#미니맵에 대해 알려줘!\r\n#L2#퀘스에 대해 알려줘!\r\n#L3#인벤토리에 대해 알려줘!\r\n#L4#공격에 대해 알려줘!\r\n#L5#아이템은 어떻게 먹지?\r\n#L6#장비 착용에 대해 알려줘!\r\n#L7#스킬창에 대해 알려줘!\r\n#L8#퀵슬롯 등록은 어떻게 하니?\r\n#L9#상자는 어떻게 부술 수 있어?\r\n#L10#의자에 앉고 싶어!\r\n#L11#좀 더 큰 세상을 보고 싶어!#k");
            break;
        }
        case 1: {
            cm.dispose();
            cm.summonMsg(selection);
            break;
        }
        case 2: {
            cm.sendNext("드디어 깨어나셨군요...!");
            break;
        }
        case 3: {
            cm.sendNextPrevS("...넌?", 2);
            break;
        }
        case 4: {
            cm.sendNextPrev("기다리고 있었어요. 당신이... 검은 마법사와 싸우던 영웅이 깨어나기를...!");
            break;
        }
        case 5: {
            cm.sendNextPrevS("...무슨 말을 하는 거지? 넌 누구지...?", 2);
            break;
        }
        case 6: {
            cm.sendNextPrevS("아니... 난 도대체 누구지...? 아무것도 기억나지 않아... 윽... 머리가 아파!", 2);
            break;
        }
        case 7: {
            cm.dispose();
            cm.userEmotion(1, 5000, false);
            cm.updateInfoQuest(21019, "helper=clear");
            break;
        }
        case 8: {
            cm.sendNext("괜찮으세요?");
            break;
        }
        case 9: {
            cm.sendNextPrevS("난... 아무것도 기억나지 않아... 여기는 어디지? 그리고 넌 누구지?", 2);
            break;
        }
        case 10: {
            cm.sendNextPrev("침착하세요. 검은 마법사의 저주가 당신의 기억을 지웠으니... 아무것도 기억나지 않는다고 걱정하실 필요 없어요. 지금부터 궁금하신 것에 관해 차근차근 설명해 드릴게요.");
            break;
        }
        case 11: {
            cm.sendNextPrev("당신은 영웅이에요. 수백 년 전 검은 마법사와 싸워 메이플월드를 구했죠. 하지만 마지막 순간 검은 마법사의 저주에 걸려 얼음 속에 긴 시간 동안 잠들고 말았어요. 그러면서 기억도 모두 잃고 말았고요.");
            break;
        }
        case 12: {
            cm.sendNextPrev("이곳은 리엔 섬이라고 해요. 검은 마법사가 당신을 가둔 곳이죠. 저주로 인해 기후에 맞지 않게 항상 얼음과 눈으로 뒤덮혀 있어요. 당신은 얼음 동굴 아주 깊숙한 곳에서 발견 되었어요.");
            break;
        }
        case 13: {
            cm.sendNextPrev("제 이름은 리린이에요. 리엔 일족의 한 명이죠. 리엔 일족은 오래 전부터 예언에 따라 영웅이 돌아오길 기다려 왔어요. 그리고... 드디어 당신을 찾아내었어요. 지금. 바로 여기에서...");
            break;
        }
        case 14: {
            cm.sendNextPrev("한 번에 너무 많은 설명을 드렸군요. 당장은 이해하지 못하셔도 상관 없어요. 점차 모든 것을 알게 되실 테니... #b그보다 어서 마을로 가요.#k 마을에 도착할 때까지 모르는 것이 있으면 제가 옆에서 알려드릴게요.");
            break;
        }
        case 15: {
            cm.dispose();
            cm.playerSummonHint(true);
            cm.warp(140090100, 1);
            break;
        }
    }
}