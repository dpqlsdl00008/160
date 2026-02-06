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
            qm.sendSimple("...아니 당신은 아란님?! 살아 계셨던 겁니까?! 하긴, 그렇군요! 전사의 자랑, 폴암의 달인인 당신이 검은 마법사 따위에게 쉽게 당할 리가 없지요!\r\n\r\n#L0##b(... 당했는데.)#k");
            break;
        }
        case 1: {
            qm.sendSimple("그나저나 여기까진 웬 일 이십니까? 당신도 새로운 마을터를 찾으러 오신 겁니까? 그런 쉬운 일은 맡겨두고 당신은 당신에게 맞는 일을 하셔야 하지 않습니까?\r\n\r\n#L0##b보관고의 열쇠를 받으러 왔다.#k");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("아... 보관고의 열쇠 말입니까? 잠시만 기다려 보십시오. 어디에 뒀더라...");
            break;
        }
        case 3: {
            qm.sendNext("...죄송합니다, 아란님. 뭐라 드릴 말씀이 없습니다. 분명 어디 뒀는데... 다른 열쇠들과 한꺼번에 싹 잃어버린 것 같군요. 모두 제 잘못입니다. 당신께서 몇 번이나 물건을 소중히 여기라고 하셨는데, 마음이 해이해지고 말았습니다!");
            break;
        }
        case 4: {
            qm.sendNextPrev("하지만 걱정하지 마십시오! 열쇠들은 이 주변에 있는 #r도둑 원시멧돼지#k들이 가지고 있을 겁니다! 녀석들을 혼내주고 잃어버린 열쇠를 찾으면 됩니다! 잃어버린 #b나무 열쇠 10개#k 중, 어느 열쇠인지 확인해야 하니 10개 모두 찾아주십시오!");
            break;
        }
        case 5: {
            qm.sendNextPrev("네...? 직접 하면 되지 않냐고요? 전 이 곳에서 부상병들을 치료해야 하는 중요한 임무가 있습니다. 최강의 전사인 당신의 능력을 보여 주십시오!");
            break;
        }
        case 6: {
            qm.sendYesNo("대신 아란님을 놈들이 있는 곳으로 보내드릴테니, 지금 출발하시겠습니까?");
            break;
        }
        case 7: {
            qm.dispose();
            qm.warp(300010400, 0);
            qm.forceStartQuest();
            break;
        }
    }
}