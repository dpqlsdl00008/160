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
        status--;
    }
    switch (status) {
        case 0: {
            cm.lockInGameUI(true);
            cm.sendNextS("이거 #h # 아닌가? 외유는 즐거웠나? 명령도 무시하고 간 것이니 즐거웠겠지? 가족분들은 잘 있던가? 안부나 전해 주게! 하하하하!", 5, 2159309);
            break;
        }
        case 1: {
            cm.sendNextPrevS("...너와 말다툼 할 시간은 없다. 비켜라, #r아카이럼#k!", 17);
            break;
        }
        case 2: {
            cm.forcedAction(371, 0);
            cm.showDirectionEffect("Skill/3112.img/skill/31121003/effect", 0, 0, 0, 0, 0);
            cm.showNpcSpecialActionByTemplateId(2159309, "teleportation");
            cm.sendDelay(2000);
            break;
        }
        case 3: {
            cm.sendNextS("임무지 무단 이탈에, 명령 불복종... 거기에 그 살기어린 눈빛이라. 그래놓고 검은 마법사님을 뵙겠다고? 그럴수야 없지.", 5, 2159309);
            break;
        }
        case 4: {
            cm.forcedAction(371, 0);
            cm.showDirectionEffect("Skill/3112.img/skill/31121001/effect", 0, 0, 0, 0, 0);
            cm.sendDelay(2000);
            break;
        }
        case 5: {
            cm.sendNextS("오호라~ 이건 확실한 배신이라고 봐도 되겠군. 아직도 인간의 마음을 버리지 못한 건가? 그깟 사소한 인연에 이렇게 망가지다니. 한심하기 짝이 없도다!", 5, 2159309);
            break;
        }
        case 6: {
            cm.sendNextPrevS("위대한 그 분의 진정한 목적도 모르고 있는 우매한 네 녀석에게는 실망을 금치 않을 수가 없군. 경비병은 나와서 저 배신자를 처치해라!", 5, 2159309);
            break;
        }
        case 7: {
            cm.dispose();
            cm.lockInGameUI(false);
            cm.spawnMonster(9300455, 3);
            cm.forceStartQuest(23205);
            cm.reservedEffect("Effect/Direction6.img/DemonTutorial/Scene4");
            break;
        }
    }
}