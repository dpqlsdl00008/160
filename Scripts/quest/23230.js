var status = -1;

function start(mode, type, selection) {
	qm.forceCompleteQuest();
	qm.gainItem(2431876, 1);
        qm.gainItem(2431877, 1);
	qm.dispose();
}
function end(mode, type, selection) {
	qm.dispose();
}
