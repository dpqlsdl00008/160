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
            qm.sendSimple("내 가방은 찾았어?\r\n#L0##b응, 찾았어.");
            break;
        }
        case 1: {
            qm.sendSimple("아아아! 고마워! 넌 천사야!\r\n#L0##b이 근처에 수상한 잠수함이 있다던데, 니가 밤에 잠수함을 봤던 곳이 여기였어?");
            break;
        }
        case 2: {
            qm.sendNext("\r\n아니, 난 해변가에서 봤어. 그런데 지금은 이동했나봐. 저쪽 수상한 바다에 있어. 여기에서 오른쪽으로 가면 보이는 포탈을 통해 갈 수 있어. 이번엔 낮이라서 확실하게 똑똑히 봤어. 그 잠수함에서 검은색 수상한 액체들이 나오고 있었어.");
            break;
        }
        case 3: {
            qm.sendNextPrev("아까 #p1082000#씨가 먼저 #b#m120040000##k으로 돌아간다고, 너도 그쪽으로 오라는데? 방금 전에 준 #b골드비치 직원용 출입 카드#k를 사용하면 바로 이동할 수 있을거야.");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}