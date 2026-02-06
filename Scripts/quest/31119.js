function end(mode, type, selection) {
    if (qm.isQuestActive(31119) == false) {
	qm.forceStartQuest();
    } else {
	qm.forceCompleteQuest();
    }
    qm.dispose();
}