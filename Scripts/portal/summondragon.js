function enter(pi) {
    if (pi.haveItem(4001094) == true) {
        if (pi.isAllReactorState(2406000, 1) == false) {
            pi.getMap().getReactorByName("dragonBaby").forceHitReactor(1);
            pi.playerMessage(5, "품안에 있던 나인스피릿의 알이 신비한 빛을 내며 둥지로 돌아갔다.");
            pi.gainItem(4001094, -1);
        }
    }
}
