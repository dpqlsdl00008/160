function enter(pi) {
    var banban = pi.getMap().getMonsterById(8910000);
    if (banban != null) {
        if (banban.isBuffed(Packages.client.status.MonsterTemporaryStat.ExchangeAttack)) {
            pi.warp(105200120, 0);
        }
    }
}