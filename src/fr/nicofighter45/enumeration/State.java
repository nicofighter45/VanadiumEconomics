package fr.nicofighter45.enumeration;

import org.bukkit.ChatColor;

public enum State {
	
	P("§7Pierre", 0, ChatColor.GRAY),	//age de pierre
	F("§fFer", 1, ChatColor.WHITE),	//age du fer
	O("§eOr", 2, ChatColor.YELLOW),	//age d'or
	N("§cNether", 3, ChatColor.RED),	//age du nether
	R("§9Riche", 4, ChatColor.BLUE),	//age du riche
	NN("§4New-N", 5, ChatColor.DARK_RED),	//age du nouveau nether
	E("§dEnd", 6, ChatColor.LIGHT_PURPLE),	//age de l'end
	S("§0Superior", 7, ChatColor.BLACK);	//age de la superiorité
	
	private String name;
	private int value;
	private ChatColor color;
	
	State(String name, int value, ChatColor color) {
		this.name = name;
		this.value = value;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public int getValue() {
		return value;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public static State getValuefromInt(int number) {
		if(number == 0) {
			return P;
		}else if(number == 1) {
			return F;
		}else if(number == 2) {
			return O;
		}else if(number == 3) {
			return N;
		}else if(number == 4) {
			return R;
		}else if(number == 5) {
			return NN;
		}else if(number == 6) {
			return E;
		}else if(number == 7) {
			return S;
		}
		return null;
	}
	
	public static State getValuefromString(String name) {
		if(name == "pierre") {
			return P;
		}else if(name == "fer") {
			return F;
		}else if(name == "or") {
			return O;
		}else if(name == "nether") {
			return N;
		}else if(name == "riche") {
			return R;
		}else if(name == "new-n") {
			return NN;
		}else if(name == "end") {
			return E;
		}else if(name == "super") {
			return S;
		}
		return null;
	}

}