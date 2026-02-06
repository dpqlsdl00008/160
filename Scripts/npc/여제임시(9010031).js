var empItems = [
    1302152, 1312065, 1322096, 1402095, 1412065, 1422066, 1432086, 1442116,
    1542015, 1372084, 1382104, 1552015, 1252014, 1452111, 1462099, 1522018,
    1332130, 1472122, 1342036, 1362019, 1492085, 1482084, 1532018, 1403015,
    1003172, 1052314, 1082295, 1072485, 1102275, 1152108, 1003173, 1052315,
    1082296, 1072486, 1102276, 1152110, 1003174, 1052316, 1082297, 1072487,
    1102277, 1152111, 1003175, 1052317, 1082298, 1072488, 1102278, 1152112,
    1003176, 1052318, 1082299, 1072489, 1102279, 1152113,
    
];

function start() {
    var questId = 99999;
    var now = Math.floor(new Date().getTime() / 1000); // 현재 시간(초)
    var saved = cm.getQuestCustomData(questId);
    var lastTime = 0;

    if (saved != null && !isNaN(parseInt(saved))) {
        lastTime = parseInt(saved);
    }

    var cooldown = 60; // 쿨타임: 3600초 = 1시간

    if (now - lastTime < cooldown) {
        var remain = cooldown - (now - lastTime);
        var min = Math.floor(remain / 60);
        var sec = remain % 60;
        cm.sendOk("이미 보상을 받으셨습니다. 재도전 후 다시 찾아오세요.");
        cm.dispose();
        return;
    }

    // 보상 지급
    var piece1 = 1 + Math.floor(Math.random() * 5);
    var piece2 = 1 + Math.floor(Math.random() * 5);
    cm.gainItem(2434588, piece1);
    cm.gainItem(2434589, piece2);

    var empGiven = false;
    var empItemText = "";
    if (Math.random() < 0.5) {
        var empItem = empItems[Math.floor(Math.random() * empItems.length)];
        cm.gainItem(empItem, 1);
        empGiven = true;
        empItemText = "\r\n#i" + empItem + "# #z" + empItem + "#";
    }

    var msg = "보상을 받았습니다!\r\n\r\n" +
          "#i2434588# #z2434588# " + piece1 + "개\r\n" +
          "#i2434589# #z2434589# " + piece2 + "개\r\n";

    if (empGiven) {
        msg += empItemText;
    }

    // 보상 지급 시간 기록 (현재 시간 초단위)
    cm.setQuestCustomData(questId, now.toString());

    cm.sendOk(msg);
    cm.dispose();
}