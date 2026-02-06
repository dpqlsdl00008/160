var pets = [
    { id: 5002561, name: "마주르카" },
    { id: 5002562, name: "칸타빌레" },
    { id: 5002563, name: "쁘띠 타임" },
    { id: 5000084, name: "유니콘 에셀" },
    // 필요한 만큼 계속 추가 가능
];

var status = -1;
var input = "";
var found = null;
var PET_PRICE = 1000000000; // 10억 메소

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }

    status++;

    if (status == 0) {
        cm.sendGetText("\r\n안녕하세요? 모든 펫을 관리하고 있는 #Cgreen#펫 마스터#k입니다.\r\n분양 가격은 마리당 #Cyellow#10억 메소#k입니다.\r\n\r\n분양받고 싶은 #b펫 이름#k을 입력해보세요.");
    } else if (status == 1) {
    input = cm.getText();
    if (input == null || input.trim() == "") {
        cm.sendOk("펫 이름을 입력하지 않았습니다. 다시 시도해주세요.");
        cm.dispose();
        return;
    }

    input = input.trim();

    found = null;
    for (var i = 0; i < pets.length; i++) {
        if (pets[i].name == input) {
            found = pets[i];
            break;
        }
    }

        if (found == null) {
            cm.sendOk("입력한 펫 이름 [" + input + "] 과 일치하는 펫을 찾을 수 없습니다.");
            cm.dispose();
            return;
        }

        // 확인 메시지 표시
        cm.sendYesNo("입력한 펫 이름은 #b#i" + found.id + "# [#z" + found.id + "#]#k 입니다.\r\n\r\n"
    + "이 펫을 #b10억 메소#k에 분양받으시겠습니까?");
    } else if (status == 2) {
        if (cm.getMeso() < PET_PRICE) {
            cm.sendOk("메소가 부족합니다. 펫을 분양받기 위해서는 #r10억 메소#k가 필요합니다.");
            cm.dispose();
            return;
        }

        // 메소 차감
        cm.gainMeso(-PET_PRICE);

        var petId = found.id;
        var name = cm.getItemName(petId);
        var level = 1;
        var closeness = 0;
        var fullness = 100;
        var period = 0; // 무제한
        var flags = 32255;

        cm.gainPet(petId, name, level, closeness, fullness, period, flags);
        cm.sendOk("축하합니다! #b" + name + "#k 펫을 분양받았습니다.");
        cm.dispose();
    }
}