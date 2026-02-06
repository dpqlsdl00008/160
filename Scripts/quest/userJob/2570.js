var status = -1;

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
            if (qm.isQuestActive(2570) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("반갑군, #h #. 스토너를 도와 이 곳까지 왔다는 소리는 들었다. 꽤 다친 것 같았는데 벌써 다 나은 건가? 정말 강골이군... 스토너가 탐을 내는 것도 이해가 가. 내 이름은 카이린. 해적선, 노틸러스의 함장이며 해적들의 전직 교관이기도 하지.");
            }
            break;
        }
        case 1: {
            qm.sendNextPrev("스토너가 널 #b캐논 슈터#k로 만들고 싶어한다는 말은 들었겠지? 나 역시 너 같은 좋은 인재를 해적으로 받아들이는 것은 환영이야. 하지만 네가 과연 해적을 원할지 조금 걱정되는군. 그러니 해적에 관한 이야기를 먼저 해야겠어.");
            break;
        }
        case 2: {
            qm.sendNextPrev("나는 오래 전 메이플 월드를 위협하던 자, 검은 마법사를 조사하기 위해 해적단을 조직했다. 노틸러스의 해적들은 지금도 메이플 월드의 이곳 저곳에서 검은 마법사의 흔적을 조사 중이야.");
            break;
        }
        case 3: {
            qm.sendNextPrev("그러니 너도 해적이 된다면 검은 마법사를 조사하는 일을 좀 해줘야 할 거야. 물론 이건 의무는 아니다. 어디까지나 권유이지. 나는 해적들의 전직 교관이지만 해적들의 주인은 아냐. 명령이 아닌 권유일 뿐이다.");
            break;
        }
        case 4: {
            qm.sendNextPrev("그러나 너 역시 메이플 월드를 모험하는 자라면 메이플 월드를 위해 이 정도의 일은 해줄 수 있을 거라고 믿는다. 보상을 바래서가 아니라, 어디까지나 선의로... 후후, 서론이 너무 길었군.");
            break;
        }
        case 5: {
            qm.sendYesNo("너무 오래 떠들었군... 그럼 이제 결정해라. 해적이 될 것인가? 캐논 슈터가 되어준다면 기쁠 것 같군.");
            break;
        }
        case 6: {
            if (qm.isQuestActive(2570) == true) {
                qm.gainItem(1532045, 1);
		qm.gainItem(2431876, 1);
                qm.gainItem(2431877, 1);
                qm.changeJob(501);
                qm.forceCompleteQuest();
            }
            qm.sendNext("자, 이제 너도 우리 해적의 일원이야. 해적 스킬들이 많이 생겼으니 스킬창을 열어보게. SP도 약간 줬으니 스킬을 올릴 수 있을 거야. 더 레벨이 높아지면 더 많은 스킬을 쓸 수 있으니 수련에 힘쓰도록.");
            break;
        }
        case 7: {
            qm.sendNextPrev("스킬만으로는 완전하지 못하지. 스탯창을 열어 자네의 스탯을 해적에 어울리게 분배하도록. 캐논 슈터라면 묵직한 캐논을 들고 다녀야 하니 STR을 중심으로 올려야겠지? 모르겠다면 #b자동 배분#k을 사용하는 게 속 편하지.");
            break;
        }
        case 8: {
            qm.sendNextPrev("그리고 한 가지 선물. 네 장비, 기타 아이템 보관함의 갯수를 늘려주었네. 필요한 물건이 있으면 인벤토리에 넣어가지고 다니라고.");
            break;
        }
        case 9: {
	qm.sendNextPrev("아, 그리고 잊지 말아야 할 한 가지. 초보자에서 해적이 되었으니 전투시에 체력 관리에 힘을 쓸 것. 죽게 되면 그 동안 쌓았던 경험치가 깎일 수 있으니까. 애써 얻은 경험치가 깎이면 억울하잖아?");
            break;
        }
        case 10: {
	qm.sendNextPrev("자! 내가 너에게 가르쳐 줄 수 있는 건 이게 다다. 네 수준에 쓸만한 무기도 몇 개 줬으니 여행을 하면서 자신을 단련시켜봐. 혹시라도 검은 마법사의 부하를 마주치거든 꼭 없애버리고! 알았나?");
            break;
        }
        case 11: {
	 qm.dispose();
            break;
        }    
    }
}