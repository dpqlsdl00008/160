//importPackage(java.lang);
//importPackage(java.util);

var status = -1;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		cm.dispose();
		return;
	}
	var qr = cm.getQuestRecord(2021061100);
	if (qr.getCustomData() == null || qr.getCustomData().equals("NaN")) {
		qr.setCustomData("0");
	}
	if (status == 0) {
		var ranking_chat = "";
		ranking_chat += "\r\n\r\n#b#h ##k님, 메이플 스토리에 오신 것을 환영합니다.";
		ranking_chat += "\r\n\r\n　#e#r* LEVEL#k#n\r\n#L0##r#e[월드 랭킹]#k#n #d을 확인하고 싶습니다.#k";
		ranking_chat += "\r\n\r\n#L1##Cgreen#[    모험가    ]#k #d랭킹을 확인하고 싶습니다.#k";
		ranking_chat += "\r\n#L2##Cgreen#[ 시 그 너 스 ]#k #d랭킹을 확인하고 싶습니다.#k";
		ranking_chat += "\r\n#L3##Cgreen#[     영 웅     ]#k #d랭킹을 확인하고 싶습니다.#k";
		ranking_chat += "\r\n#L4##Cgreen#[ 레지스탕스 ]#k #d랭킹을 확인하고 싶습니다.#k#l";
		ranking_chat += "\r\n#L5##Cgreen#[ 해 외 직 업 ]#k #d랭킹을 확인하고 싶습니다.#k#l\r\n";
		ranking_chat += "\r\n\r\n　#e#r* GUILD#k#n\r\n#L8##Cgreen#길드 랭킹#d를 확인하고 싶습니다.#k";
		cm.sendSimple(ranking_chat);
	} else if (status == 1) {
		if (selection == 0) {
			status = 9;
			cm.sendSimple(ranking(10) + "\r\n#L0##r11위 ~ 20위#b를 확인하고 싶습니다.#k");
		} else if (selection == 1) {
			status = 2;
			var adven_rank = "";
			adven_rank += "\r\n#L0##r#e[모험가]#k#n #d랭킹을 확인하고 싶습니다.";
			adven_rank += "\r\n\r\n#L1##Cgreen#[  전 사  ]#k #d랭킹을 확인하고 싶습니다.";
			adven_rank += "\r\n#L2##Cgreen#[ 마법사 ]#k #d랭킹을 확인하고 싶습니다.";
			adven_rank += "\r\n#L3##Cgreen#[  궁 수  ]#k #d랭킹을 확인하고 싶습니다.";
			adven_rank += "\r\n#L4##Cgreen#[  도 적  ]#k #d랭킹을 확인하고 싶습니다.";
			adven_rank += "\r\n#L5##Cgreen#[  해 적  ]#k #d랭킹을 확인하고 싶습니다.";
			cm.sendSimple(adven_rank);
		} else if (selection == 2) {
			status = 3;
			var knight_rank = "";
			knight_rank += "\r\n#L0##r#e[시그너스]#k#n #d랭킹을 확인하고 싶습니다.";
			knight_rank += "\r\n\r\n#L1##Cgreen#[   소울마스터   ]#k #d랭킹을 확인하고 싶습니다.";
			knight_rank += "\r\n#L2##Cgreen#[ 플레임 위자드 ]#k #d랭킹을 확인하고 싶습니다.";
			knight_rank += "\r\n#L3##Cgreen#[ 윈드 브레이커 ]#k #d랭킹을 확인하고 싶습니다.";
			knight_rank += "\r\n#L4##Cgreen#[   나이트워커   ]#k #d랭킹을 확인하고 싶습니다.";
			knight_rank += "\r\n#L5##Cgreen#[   스트라이커   ]#k #d랭킹을 확인하고 싶습니다.";
			knight_rank += "\r\n#L6##Cgreen#[      미하일      ]#k #d랭킹을 확인하고 싶습니다.";
			cm.sendSimple(knight_rank);
		} else if (selection == 3) {
			status = 4;
			var hero_rank = "";
			hero_rank += "\r\n#L0##r#e[영웅]#k#n #d랭킹을 확인하고 싶습니다.";
			hero_rank += "\r\n\r\n#L1##Cgreen#[     아 란     ] #d랭킹을 확인하고 싶습니다.";
			hero_rank += "\r\n#L2##Cgreen#[     에 반     ] #d랭킹을 확인하고 싶습니다.";
			hero_rank += "\r\n#L3##Cgreen#[ 메르세데스 ] #d랭킹을 확인하고 싶습니다.";
			hero_rank += "\r\n#L4##Cgreen#[     팬 텀     ] #d랭킹을 확인하고 싶습니다.";
			cm.sendSimple(hero_rank);
		} else if (selection == 4) {
			status = 5;
			var resi_rank = "";
			resi_rank += "\r\n#L0##r#e[레지스탕스]#k#n #d랭킹을 확인하고 싶습니다.";
			resi_rank += "\r\n\r\n#L1##Cgreen#[ 데몬 슬레이어 ] #d랭킹을 확인하고 싶습니다.";
			resi_rank += "\r\n#L2##Cgreen#[   배틀메이지   ] #d랭킹을 확인하고 싶습니다.";
			resi_rank += "\r\n#L3##Cgreen#[   와일드헌터   ] #d랭킹을 확인하고 싶습니다.";
			resi_rank += "\r\n#L5##Cgreen#[      메카닉      ] #d랭킹을 확인하고 싶습니다.";
			cm.sendSimple(resi_rank);
		} else if (selection == 5) {
			status = 6;
			var gms_rank = "";
			gms_rank += "\r\n#L0##r#e[해외 직업]#k#n #d랭킹을 확인하고 싶습니다.";
			gms_rank += "\r\n\r\n#L1##Cgreen#[ 하야토 ] #d랭킹을 확인하고 싶습니다.";
			gms_rank += "\r\n#L2##Cgreen#[  칸 나  ] #d랭킹을 확인하고 싶습니다.";
			gms_rank += "\r\n#L3##Cgreen#[    린    ] #d랭킹을 확인하고 싶습니다.";
			gms_rank += "\r\n#L5##Cgreen#[  묵 현  ] #d랭킹을 확인하고 싶습니다.";
			cm.sendSimple(gms_rank);
		} else if (selection == 6) {
			cm.sendOk(ranking_Type(4200));
			cm.dispose();
		} else if (selection == 7) {
			cm.sendOk(ranking_Type(-1));
			cm.dispose();
		} else if (selection == 8) {
			status = 17;
			var GUILD_rank = "";
			GUILD_rank += "\r\n#L0##r전체 랭킹#b을 확인하고 싶습니다.";
			cm.sendSimple(GUILD_rank);
		}
	} else if (status == 3) {
		cm.sendOk(ranking_Type(selection * 100));
		cm.dispose();
	} else if (status == 4) {
		if (selection == 6) {
			cm.dispose();
			cm.sendOk(ranking_Type(5100));
			return;
		}
		cm.sendOk(ranking_Type((selection * 100) + 1000));
		cm.dispose();
	} else if (status == 5) {
		cm.sendOk(ranking_Type((selection * 100) + 2000));
		cm.dispose();
	} else if (status == 6) {
		cm.sendOk(ranking_Type((selection * 100) + 3000));
		cm.dispose();
	} else if (status == 7) {
		if (selection == 3) {
			cm.dispose();
			cm.sendOk(ranking_Type(5200));
			return;
		}
		cm.sendOk(ranking_Type((selection * 100) + 6000));
		cm.dispose();
	} else if (status == 8) {
		if (selection == 0) {
			status = 17;
			var text_ = "";
			var con_ = Packages.database.DatabaseConnection.getConnection();
			var ps_ = con_.prepareStatement("SELECT * FROM guilds where level > 0 and guildid > 6 ORDER BY level desc, exp desc LIMIT 20");
			var rs_ = ps_.executeQuery();
			var check = false;
			var i = 0;
			while (rs_.next()) {
				i++;
				if (i < 21) {
					var member = Packages.handling.world.World.Guild.getGuild(rs_.getInt("guildid")).getMembers().size();
					var zero = (i < 10 ? "0" : "");
					if (i < 11) {
						continue;
					}
					text_ += "\r\n#L" + rs_.getInt("guildid") + "##r" + zero + i + "위#k : " + rs_.getString("name") + " #b(길드 정원 : " + member + " / " + rs_.getInt("capacity") + ")#k";
				}
				check = true;
			}
			if (!check) {
				text_ = "랭킹를 확인 할 수 없습니다.";
			}
			rs_.close();
			ps_.close();
			con_.close();
			cm.sendSimple(text_);
			return;
		}
		var con = Packages.database.DatabaseConnection.getConnection();
		var ps = con.prepareStatement("SELECT * FROM guilds where guildid = ?");
		ps.setString(1, selection);
		var rs = ps.executeQuery();
		while (rs.next()) {
			var ps2 = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
			leaderid = rs.getInt("leader");
			ps2.setInt(1, leaderid);
			var rs2 = ps2.executeQuery();
			while (rs2.next()) {
				leaderName = rs2.getString("name");
			}
			var text = "";
			text += "#e길드 : #r" + rs.getString("name") + "#k#n\r\n";
			text += "#e길드 마스터 : #r" + leaderName + "#k#n\r\n\r\n";
			text += "#d길드 레벨 : #bLv. " + cm.getPlayer().getNum(rs.getInt("level")) + "#k\r\n";
			text += "#d길드 포인트 : #b" + cm.getPlayer().getNum(rs.getInt("GP")) + " 포인트#k\r\n";
			text += "#d길드 경험치 : #b" + cm.getPlayer().getNum(rs.getLong("exp")) + " exp#k\r\n\r\n";
			text += "#d길드 대항전 전적 : #b승 : 0 / 패 : 0 #r[승률 : 0.0%]#k\r\n";
		}
		rs2.close();
		ps2.close();
		rs.close();
		ps.close();
		con.close();
		cm.sendSimple(text + "\r\n#L" + leaderid + "##r길드 가입 신청#b을 하고 싶습니다.#k");
	} else if (status == 9) {
		var ch = Packages.handling.world.World.Find.findChannel(selection);
		if (ch > 0) {
			var leader = Packages.handling.channel.ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(selection);
			if (leader != null) {
				if (canGwangyunTime(2021061100)) {
					setGwangyunTime(2021061100, 24);
					cm.playerMessage(1, "'" + leader.getName() + "'님께 성공적으로 길드 가입 신청을 하였습니다.");
					Packages.client.MapleCharacterUtil.sendNote(leader.getName(), cm.getPlayer().getName(), cm.getPlayer().getName() + "님의 길드 가입 신청입니다.", 0);
				} else {
					gwangyunTimer();
				}
			} else {
				cm.playerMessage(1, "알 수 없는 오류가 발생하였습니다.");
			}
		} else {
			cm.playerMessage(1, "알 수 없는 오류가 발생하였습니다.");
		}
		cm.dispose();
	} else if (status == 10) {
		cm.sendSimple(ranking(20) + "\r\n#L0##r21위 ~ 30위#b를 확인하고 싶습니다.#k");
	} else if (status == 11) {
		cm.sendSimple(ranking(30) + "\r\n#L0##r31위 ~ 40위#b를 확인하고 싶습니다.#k");
	} else if (status == 12) {
		cm.sendSimple(ranking(40) + "\r\n#L0##r41위 ~ 50위#b를 확인하고 싶습니다.#k");
	} else if (status == 13) {
		cm.sendSimple(ranking(50) + "\r\n#L0##r51위 ~ 60위#b를 확인하고 싶습니다.#k");
	} else if (status == 14) {
		cm.sendSimple(ranking(60) + "\r\n#L0##r61위 ~ 70위#b를 확인하고 싶습니다.#k");
	} else if (status == 15) {
		cm.sendSimple(ranking(70) + "\r\n#L0##r71위 ~ 80위#b를 확인하고 싶습니다.#k");
	} else if (status == 16) {
		cm.sendSimple(ranking(80) + "\r\n#L0##r81위 ~ 90위#b를 확인하고 싶습니다.#k");
	} else if (status == 17) {
		cm.sendSimple(ranking(90) + "\r\n#L0##r91위 ~ 99위#b를 확인하고 싶습니다.#k");
	} else if (status == 18) {
		cm.sendOk(ranking(100));
		cm.dispose();
	} else if (status == 19) {
		var text = String();
		var leaderid = -1;
		var con = null, ps = null, ps2 = null, rs = null, rs2 = null;
		try {
			con = Packages.database.DatabaseConnection.getConnection();
			ps = con.prepareStatement("SELECT * FROM guilds ORDER BY `GP` DESC LIMIT 10");
			rs = ps.executeQuery();
			var rank = 0;
			while (rs.next()) {
				rank++;
				ps2 = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
				leaderid = rs.getInt("leader");
				ps2.setInt(1, leaderid);
				rs2 = ps2.executeQuery();
				while (rs2.next()) {
					leaderName = rs2.getString("name");
				}
				text += "#e" + rank + "위. 길드 : #r" + rs.getString("name") + "#k#n\r\n";
				text += "#e - 길드 마스터 : #r" + leaderName + "#k#n\r\n";
				text += "#e - 길드 포인트 : #b" + rs.getInt("GP") + " 포인트#n#k\r\n\r\n";
			}
		} catch (e) {

		} finally {
			if (rs2 != null) {
				try { rs2.close(); } catch (e) { }
			}
			if (rs != null) {
				try { rs.close(); } catch (e) { }
			}
			if (ps2 != null) {
				try { ps2.close(); } catch (e) { }
			}
			if (ps != null) {
				try { ps.close(); } catch (e) { }
			}
			if (con != null) {
				try { con.close(); } catch (e) { }
			}
		}
		if (leaderid == -1) {
			var talk = "알 수 없는 오류가 발생하였습니다.";
			cm.sendOk(talk);
			cm.dispose();
			return;
		}
		cm.sendOk(text);// + "\r\n#L" + leaderid + "##r길드 가입 신청#b을 하고 싶습니다.#k");
		cm.dispose();
	} else if (status == 20) {
		var ch = Packages.handling.world.World.Find.findChannel(selection);
		if (ch > 0) {
			var leader = Packages.handling.channel.ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(selection);
			if (leader != null) {
				if (canGwangyunTime(2021061100)) {
					setGwangyunTime(2021061100, 24);
					cm.playerMessage(1, "'" + leader.getName() + "'님께 성공적으로 길드 가입 신청을 하였습니다.");
					Packages.client.MapleCharacterUtil.sendNote(leader.getName(), cm.getPlayer().getName(), cm.getPlayer().getName() + "님의 길드 가입 신청입니다.", 0);
				} else {
					gwangyunTimer();
				}
			} else {
				cm.playerMessage(1, "알 수 없는 오류가 발생하였습니다.");
			}
		} else {
			cm.playerMessage(1, "알 수 없는 오류가 발생하였습니다.");
		}
		cm.dispose();
	}
}

function ranking(max) {
	var text = "";
	var con = Packages.database.DatabaseConnection.getConnection();
	var ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 9 and gm = 0 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT " + max);
	var rs = ps.executeQuery();
	var check = false;
	var i = 0;
	while (rs.next()) {
		i++;
		if (i < (max == 100 ? max : (max + 1)) + 1) {
			var zero = (i < 10 ? "0" : "");
			if (max == 20 && i < 11) {
				continue;
			}
			if (max == 30 && i < 21) {
				continue;
			}
			if (max == 40 && i < 31) {
				continue;
			}
			if (max == 50 && i < 41) {
				continue;
			}
			if (max == 60 && i < 51) {
				continue;
			}
			if (max == 70 && i < 61) {
				continue;
			}
			if (max == 80 && i < 71) {
				continue;
			}
			if (max == 90 && i < 81) {
				continue;
			}
			if (max == 100 && i < 91) {
				continue;
			}
			text += "\r\n\r\n　 #r#e" + zero + i + "위#k#n : #d< " + rs.getString("name") + " > #Cgray#(Lv." + rs.getInt("level") + " / " + jobname(rs.getInt("job")) + ")#k";
			text += "\r\n　　　 　 #Cgreen#- 명예 레벨  " + rs.getInt("honourLevel") + "#k"
		}
		check = true;
	}
	if (!check) {
		text = "랭킹를 확인 할 수 없습니다.";
	}
	rs.close();
	ps.close();
	con.close();
	return text;
}

function ranking_Type(type) {
	var text = "";
	var con = Packages.database.DatabaseConnection.getConnection();
	var ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 9 and gm = 0 ORDER BY level desc LIMIT 10");
	if (type == -1) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 0 and gm = 0 and (job / 10) = 0 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 0) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 0 and job < 1000 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 100) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 99 and job < 136 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 200) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 142 and job < 236 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 300) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 242 and job < 326 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 400) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 322 and job < 436 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 500) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 434 and job < 526 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 1000) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 1000 and job < 2000 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 1100) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 1099 and job < 1116 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 1200) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 1112 and job < 1216 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 1300) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 1212 and job < 1316 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 1400) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 1312 and job < 1416 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 1500) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 1412 and job < 1516 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 2000) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 2000 and job < 3000 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 2100) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 2099 and job < 2116 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 2200) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 2112 and job < 2219 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 2300) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 2218 and job < 2316 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 2400) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 2312 and job < 2416 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 3000) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 3000 and job < 4000 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 3100) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 3099 and job < 3213 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 3200) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 3112 and job < 3313 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 3300) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 3212 and job < 3513 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 3500) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 3412 and job < 3613 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 5000) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 5000 and job < 6000 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 5100) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 5099 and job < 5116 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 5200) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 5112 and job < 5216 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 6000) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 6000 and job < 7000 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 6100) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 6099 and job < 6116 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 6200) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 6112 and job < 6216 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	} else if (type == 6500) {
		ps = con.prepareStatement("SELECT name, level, honourLevel, job FROM characters where level > 29 and gm = 0 and job > 6212 and job < 6516 ORDER BY honourLevel DESC, CASE WHEN honourLevel = 1 THEN level ELSE NULL END DESC LIMIT 10");
	}
	var rs = ps.executeQuery();
	var check = false;
	var i = 0;
	while (rs.next()) {
		i++;
		if (i < 11) {
			var zero = (i < 10 ? "0" : "");
			text += "\r\n\r\n　 #r#e" + zero + i + "위#k#n : #d< " + rs.getString("name") + " > #Cgray#(Lv." + rs.getInt("level") + " / " + jobname(rs.getInt("job")) + ")#k";
			text += "\r\n　　　 　 #Cgreen#- Hyper LV." + rs.getInt("honourLevel") + "#k"
		}
		check = true;
	}
	if (!check) {
		text = "랭킹를 확인 할 수 없습니다.";
	}
	rs.close();
	ps.close();
	con.close();
	return text;
}

function jobname(job) {
	if (job == 0)
		return "초보자";
	if (job == 100)
		return "검사";
	if (job == 110)
		return "파이터";
	if (job == 120)
		return "페이지";
	if (job == 130)
		return "스피어맨";
	if (job == 111)
		return "크루세이더";
	if (job == 121)
		return "나이트";
	if (job == 131)
		return "용기사";
	if (job == 112)
		return "히어로";
	if (job == 122)
		return "팔라딘";
	if (job == 132)
		return "다크나이트";
	if (job == 200)
		return "마법사";
	if (job == 210)
		return "위자드(불,독)";
	if (job == 220)
		return "위자드(썬,콜)";
	if (job == 230)
		return "클레릭";
	if (job == 211)
		return "메이지(불,독)";
	if (job == 221)
		return "메이지(썬,콜)";
	if (job == 231)
		return "프리스트";
	if (job == 212)
		return "아크메이지(불,독)";
	if (job == 222)
		return "아크메이지(썬,콜)";
	if (job == 232)
		return "비숍";
	if (job == 300)
		return "궁수";
	if (job == 310)
		return "헌터";
	if (job == 320)
		return "사수";
	if (job == 311)
		return "레인저";
	if (job == 321)
		return "저격수";
	if (job == 312)
		return "보우마스터";
	if (job == 322)
		return "신궁";
	if (job == 400)
		return "도적";
	if (job == 410)
		return "어쌔신";
	if (job == 420)
		return "시프";
	if (job == 411)
		return "허밋";
	if (job == 420)
		return "시프마스터";
	if (job == 412)
		return "나이트로드";
	if (job == 422)
		return "섀도어";
	if (job >= 430 && job <= 434)
		return "듀얼블레이드";
	if (job == 500)
		return "해적";
	if (job == 510)
		return "인파이터";
	if (job == 520)
		return "건슬링거";
	if (job == 511)
		return "버커니어";
	if (job == 521)
		return "발키리";
	if (job == 512)
		return "바이퍼";
	if (job == 522)
		return "캡틴";
	if (job == 900)
		return "운영자";
	if (job == 3000)
		return "시티즌";
	if (job == 1000)
		return "노블레스";
	if (job >= 1100 && job <= 1112)
		return "소울마스터";
	if (job >= 1200 && job <= 1212)
		return "플레임위자드";
	if (job >= 1300 && job <= 1312)
		return "윈드브레이커";
	if (job >= 1400 && job <= 1412)
		return "나이트워커";
	if (job >= 1500 && job <= 1512)
		return "스트라이커";
	if (job >= 2000 && job <= 2010)
		return "영웅";
	if (job >= 2100 && job <= 2112)
		return "아란";
	if (job >= 2200 && job <= 2218)
		return "에반";
	if (job >= 501 && job <= 532)
		return "캐논 슈터";
	if (job >= 2300 && job <= 2312)
		return "메르세데스";
	if (job >= 3100 && job <= 3112)
		return "데몬 슬레이어";
	if (job >= 2400 && job <= 2412)
		return "팬텀";
	if (job >= 3200 && job <= 3212)
		return "배틀메이지";
	if (job >= 3300 && job <= 3312)
		return "와일드헌터";
	if (job >= 3500 && job <= 3512)
		return "메카닉";
	if (job >= 5100 && job <= 5112)
		return "미하일";
	if (job >= 5200 && job <= 5212)
		return "린";
	if (job >= 6100 && job <= 6112)
		return "하야토";
	if (job >= 6200 && job <= 6212)
		return "칸나";
	if (job >= 6500 && job <= 6512)
		return "묵현";
}

function setGwangyunTime(qid, hour) {
	cm.getQuestRecord(qid).setCustomData((cm.getCurrentTime() + hour * 3600000) + "");
}

function canGwangyunTime(qid) {
	if (cm.getQuestRecord(qid).getCustomData() == null) {
		return true;
	}
	return Long.parseLong(cm.getQuestRecord(qid).getCustomData()) < cm.getCurrentTime();
}

function gwangyunTimer() {
	var text = "";
	var start = cm.getCurrentTime();
	var end = Long.parseLong(cm.getQuestRecord(2021061100).getCustomData());
	var elapsedSeconds = (end - start) / 1000;
	var elapsedSecs = parseInt(elapsedSeconds) % 60;
	var elapsedMinutes = parseInt((elapsedSeconds / 60.0));
	var elapsedMins = parseInt(elapsedMinutes % 60);
	var elapsedHrs = parseInt(elapsedMinutes / 60);
	var elapsedHours = parseInt(elapsedHrs % 24);
	if (elapsedHours > 0) {
		var mins = elapsedMins > 0;
		text += elapsedHours + "";
		text += "시간 ";
		if (mins) {
			var secs = elapsedSecs > 0;
			text += elapsedMins + "";
			text += "분 ";
			if (secs) {
				text += elapsedSecs + "";
				text += "초";
			}
		}
	} else if (elapsedMinutes > 0) {
		var secs = elapsedSecs > 0;
		text += elapsedMinutes + "";
		text += "분 ";
		if (secs) {
			text += elapsedSecs + "";
			text += "초";
		}
	} else if (elapsedSeconds > 0) {
		text += elapsedSeconds + "";
		text += "초";
	} else {
		text += "";
	}
	cm.playerMessage(1, "길드 가입 신청은 24시간에 한 번만 요청 할 수 있습니다.\r\n\r\n대기 시간 : " + text);
}