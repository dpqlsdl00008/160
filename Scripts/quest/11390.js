var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        qm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            qm.dispose();
            var say = "";
            say += "#Cgreen#[텔레그램]\r\n";
            say += "#Cgray#https://t.me/+cjEJzXAcNpE5ZjBl\r\n\r\n";
            say += "#Cgreen#[디스코드]\r\n";
            say += "#Cgray#https://t.me/+cjEJzXAcNpE5ZjBl\r\n\r\n";
            say += "#Cgreen#[후원신청]\r\n";
            say += "#Cgray#https://t.me/+cjEJzXAcNpE5ZjBl";
            qm.sendNext("\r\n" + say);
            break;
        }
    }
}