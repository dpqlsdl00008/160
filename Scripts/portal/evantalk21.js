function enter(pi) {
    if (pi.getInfoQuest(22013).equals("dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o;mo11=o;mo20=o;mo21=o") == true) {
        return false;
    }
    pi.updateInfoQuest(22013, "dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o;mo11=o;mo20=o;mo21=o");
    pi.delayEffect("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon21", 0);
    return true;
}