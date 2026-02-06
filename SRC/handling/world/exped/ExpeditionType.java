package handling.world.exped;

public enum ExpeditionType {

    balog(2000, 105100100, 0),
    zakum1(2002, 211042300, 0), 
    horntail1(2003, 240050400, 0), 
    pinkbeen(2004, 270050000, 0), 
    zakum2(2005, 211042301, 0), 
    horntail2(2006, 240050400, 0), 
    vanleon(2007, 211070000, 0), 
    cygnus(2008, 271040000, 0),
    akayrum(2009, 272020110, 0),
    hilla(2010, 262000000, 0),
    magnus(2011, 401060000, 0),
    banban(2012, 105200000, 0),
    pieree(2013, 105200000, 0),
    blodyqueen(2014, 105200000, 0),
    bellum(2015, 105200000, 0),
    /*
    ranmaru(2016, 807300100, 0),
    nohime(2017, 811000999, 0),
    mitsuhide(2018, 874004000, 0),
    aufheben(2019, 876003620, 0),
    */
    swoo(2016, 350060300, 0),
    demian(2021, 105300303, 0),
    lucid(2023, 450004000, 0),
    will(2024, 450007240, 0),
    jinhilla(2025, 450011990, 0),
    dunkel(2026, 450012200, 0),
    blackmage(2027, 450012500, 0),
    seren(2028, 410000670, 0),
    kalos(2029, 410005005, 0),
    gollux(2030, 863010000, 0),
    karing(2031, 410007025, 0), 
    limbo(2032, 100000000, 0), 
    baldrix(2033, 100000000, 0), 
    ;
    
    public int type, map, quest;
    
    private ExpeditionType(int type, int map, int quest) {
	this.type = type;
        this.map = map;
        this.quest = quest;
    }
    
    public static ExpeditionType getByType(int id) {
	for (ExpeditionType pst : ExpeditionType.values()) {
	    if (pst.type == id) {
		return pst;
	    }
	}
	return null;
    }
    
    /*
    normal_balrog(15, 2000, 45, 255),
    normal_horntail(30, 2003, 80, 255),
    normal_zakum(30, 2002, 50, 255),
    normal_pinkbean(30, 2004, 140, 255),
    chaos_zakum(30, 2005, 100, 255),
    chaos_horntail(30, 2006, 110, 255),
    normal_vonleon(30, 2007, 120, 255),
    normal_cygnus(18, 2008, 170, 255),
    normal_akayrum(18, 2009, 120, 255),
    normal_hillah(30, 2010, 120, 255),
    */
    
    public int maxMembers, maxParty, exped, minLevel, maxLevel;
    
    private ExpeditionType(int maxMembers, int exped, int minLevel, int maxLevel) {
	this.maxMembers = maxMembers;
	this.exped = exped;
	this.maxParty = (maxMembers / 2) + (maxMembers % 2 > 0 ? 1 : 0);
	this.minLevel = minLevel;
	this.maxLevel = maxLevel;
    }

    public static ExpeditionType getById(int id) {
	for (ExpeditionType pst : ExpeditionType.values()) {
	    if (pst.exped == id) {
		return pst;
	    }
	}
	return null;
    }
}
