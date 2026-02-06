var status = -1;

var phantomMessage = [
"UI/tutorial/phantom/0/0", 
"UI/tutorial/phantom/1/0", 
"UI/tutorial/phantom/2/0", 
"UI/tutorial/phantom/3/0", 
];

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
            cm.dispose();
            cm.showFieldEffect(false, "phantom/mapname2");
            cm.setFuncKey(83, 20031211);
            cm.setFuncKey(79, 20031212);
            cm.sendTutorialUI(phantomMessage);
            break;
        }
    }
}