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
            qm.sendNext("생각해 봤는데 주인을 리엔 섬에 데려가서 그 곳에 존재하는 순수한 냉기로 이루어진 얼음 속에 봉인 시킨다면 아란에게 걸린 저주를 막아 낼 수 있을 지도 몰라. 최소한 저주가 더 진행 되지는 않을 거야.");
            break;
        }
        case 1: {
            qm.sendNextPrev("문제는 내 힘이 약해진 상태라 주인을 데리고 그곳까지 이동 할 수가 없다는 거야. 주인이 저주를 당한 몸을 이끌고 이 곳에 남아 있던 몬스터들과 전투를 벌이지만 않았더라도... 그 때 주인을 지키기 위해 내 힘을 순간적으로 폭발 시킬 수 밖에 없었어.");
            break;
        }
        case 2: {
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == false) {
                qm.sendNextPrev("아마 내 힘이 담긴 폴암 조각들이 근처에 돌아다니는 몬스터들에게서 발견 될 거야. 몬스터들을 잡고 #b부러진 폴암 조각 50개#k만 모아줘. 그거면 주인을 데리고 리엔 섬으로 떠날 정도의 힘은 모일 것 같아.");
            } else {
                qm.sendNextPrevS("마하가 아란을 위하는 마음은 아마 아란을 제외하고 마하를 아는 모든 사람이 모두 다 잘 알 거야.", 2);
            }
            break;
        }
        case 3: {
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == false) {
                qm.dispose();
                qm.forceStartQuest();
                return;
            }
            qm.sendNextPrev("그런 얘긴 다른 데서 하지마! 아무튼 근처에 돌아다니는 몬스터들을 잡고 #b부러진 폴암 조각 50개#k만 모아 줘. 그거면 이 멍청한 주인을 데리고 리엔 섬으로 떠날 정도의 힘은 모일 것 같아. 진짜 주인이 메르세데스만 같았어도 내가 이렇게 고생은 안하는데 말야.");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
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
            qm.sendNext("부러진 폴암 조각은 다 모아 왔어?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#i4033080# 여기 있어. 이거면 충분해?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("응, 이제 이걸로 힘을 회복하면...");
            break;
        }
        case 3: {
            var say = "그래, 나 대신 안부 좀 전해줘.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say += "메르세데스가 내 대신 안부를 좀 전해줬으면 해.";
            }
            qm.sendNextPrev("휴, 전부는 아니지만 어느 정도 힘을 되 찾은 것 같아. 이제 주인을 데리고 리엔 섬으로 가야겠어. 여유가 있다면 아프리엔은 한 번 더 만나고 싶었는데... " + say);
            break;
        }
        case 4: {
            var say = "아프리엔은 누구야?";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "아프리엔은 어디에 있지? 프리드와 같이 있나?";
            }
            qm.sendNextPrevS(say, 2);
            break;
        }
        case 5: {
            var say = "그는 오닉스 드래곤의 왕이야. 오른쪽으로 보이는 포탈을 이용하면 그를 만날 수 있을 거야. 덩치에 너무 겁먹지 말라고. 알고 보면 엄청 자상한 용이니까. 그럼 난 이만. 네 덕분에 주인을 구할 수 있었어.";
            if (Packages.constants.GameConstants.isMercedes(qm.getJob()) == true) {
                say = "오른쪽의 포탈을 타고 가면 될 거야. 네 건강한 모습을 보면 매우 기뻐 할 게 분명하니까 얼른 가보라고. 나도 주인이 걱정이라 슬슬 떠나야겠어. 다음에 만나면 주인과 셋이서 밀린 이야기를 마저 하자고.";
            }
            qm.sendNextPrev(say);
            break;
        }
        case 6: {
            qm.sendNextPrev("아프리엔에게 안부 전해주는 것 잊지 마.");
            break;
        }
        case 7: {
            qm.dispose();
            qm.gainItem(4033080, -50);
            qm.forceCompleteQuest();
            break;
        }
    }
}