function enter(pi) {
/*	if (pi.isQuestActive(2324)) {
	    pi.forceCompleteQuest(2324);
	    pi.removeAll(2430015);
	    pi.playerMessage("Quest complete.");
	}*/

	if (pi.isQuestActive(2324)) {
		if (pi.isQuestFinished(2324)) {
			pi.warp(106020501,0);
		} else {
			pi.warp(106020500,0);
		}
	} else {
		pi.warp(106020500,0);
	}
	return true;
}