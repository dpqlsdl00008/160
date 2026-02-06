var status = -1;
var beatrice = [
["전사", "주먹펴고 일어서", 102000003, 1401], 
["마법사", "하인즈", 101000003, 1402], 
["궁수", "헬레나", 100000201, 1403], 
["도적", "다크로드", 103000003, 1404]
];

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0 || status == 5) {
            qm.dispose();
            return;
        }
        if (status == 3) {
            status = 5;
        } else {
            status--;
        }
    }
    switch (status) {
        case 0: {
            qm.sendNext("#h #...? 흠. 과연 마이의 말대로 제법 자질이 보이는군. 반갑다. 내 이름은 카이린. 해적선, 노틸러스의 함장이며 해적들의 전직 교관이기도 하지. 해적에 관심이 있다고 들었는데, 맞나?");
            break;
        }
        case 1: {
            qm.sendNextPrev("그렇다면 먼저 개인적인 이야기를 좀 해야겠군. 나는 오래 전 메이플 월드를 위협하던 자, 검은 마법사를 조사하기 위해 해적단을 조직했다. 노틸러스의 해적들은 지금도 메이플 월드의 이곳 저곳에서 검은 마법사의 흔적을 조사 중이야.");
            break;
        }
        case 2: {
            qm.sendNextPrev("그러나 너 역시 메이플 월드를 모험하는 자라면 메이플 월드를 위해 이 정도의 일은 해줄 수 있을 거라고 믿는다. 보상을 바래서가 아니라, 어디까지나 선의로... 후후. 서론이 너무 길었군. 이건 그냥 알아두라고 하는 소리고 진짜 중요한 건 이 다음이지.");
            break;
        }
        case 3: {
            qm.sendAcceptDecline("너무 오래 떠들었군... 그럼 이제 결정해라. 해적이 될 것인가. 다른 직업을 택할 것인가? 해적이 되겠다고 말한다면 전직관의 특권으로 자네를 당장 노틸러스에 초대하지. #r거절한다면? 다른 직업을 권유해줄테니 걱정 마.#k");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceStartQuest();
            qm.warp(120000101, 0);
            break;
        }
        case 5: {
            var say = "#b";
            for (i = 0; i < beatrice.length; i++) {
                say += "\r\n#L" + i + "#" + beatrice[i][0];
            }
            qm.sendSimple(say);
            break;
        }
        case 6: {
            jgys = selection;
            qm.sendAcceptDecline(beatrice[selection][0] + (selection < 3 ? "를" : "을") + "" + beatrice[selection][1] + "");
            break;
        }
        case 7: {
            qm.dispose();
            qm.forceStartQuest(beatrice[jgys][3]);
            qm.warp(beatrice[jgys][2], 0);
            break;
        }
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.sendNextS("", 2);
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendAcceptDecline("이리 얼굴을 보니 반갑군... 왜 놀란 표정이지? 내가 너무 젊어 보여서? 이래봬도 보기보다 훨씬 나이가 많으니 만만하게 보지 말라고. 자, 그럼 바로 해적으로 전직 시켜주지.");
            break;
        }
        case 1: {
            if (qm.isQuestFinished(1405) == false) {
                qm.changeJob(500);
                qm.getPlayer().resetStats(4, 4, 4, 4);
                qm.gainItem(1142107, 1);
		qm.gainItem(2431876, 1);
                qm.gainItem(2431877, 1);
                qm.forceCompleteQuest();
            }
            qm.sendNext("자, 이제 너도 우리 해적의 일원이야. 해적 스킬들이 많이 생겼으니 스킬창을 열어보게. SP도 약간 줬으니 스킬을 올릴 수 있을 거야. 더 레벨이 높아지면 더 많은 스킬을 쓸 수 있으니 수련에 힘쓰도록.");
            break;
        }
        case 2: {
            qm.sendNextPrev("스킬만으로는 완전하지 못하지. 스탯창을 열어 자네의 스탯을 해적에 어울리게 분배하도록. 인파이터를 꿈꾼다면 STR을 중심으로, 건슬링거를 꿈군다면 DEX를 중심으로 올리면 돼. 모르겠다면 #b자동 배분#k을 사용하는 게 속 편하지.");
            break;
        }
        case 3: {
            qm.sendNextPrev("그리고 한 가지 선물. 네 장비, 소비, 설치, 기타 아이템 보관함의 개수를 눌려주었네. 필요한 물건이 있으면 인벤토리에 넣어가지고 다니라고.");
            break;
        }
        case 4: {
            qm.sendNextPrev("아, 그리고 잊지 말아야 할 한 가지. 초보자에서 해적이 되었으니 전투 시에 체력 관리에 힘을 쓸 것. 죽게 되면 그 동안 쌓았던 경험치가 깎일 수 있으니까. 애써 얻은 경험치가 깎이면 억울하잖아?");
            break;
        }
        case 5: {
            qm.sendNextPrev("자! 내가 너에게 가르쳐 줄 수 있는 건 이게 다다. 네 수준에 쓸만한 무기도 몇 개 줬으니 여행을 하면서 자신을 단련시켜 봐. 혹시라도 검은 마법사의 부하를 마주치거든 꼭 없애버리고! 알았나?");
            break;
        }
        case 6: {
            qm.dispose();
            break;
        }
    }
}