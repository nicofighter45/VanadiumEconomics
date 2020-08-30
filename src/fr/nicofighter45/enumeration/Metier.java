package fr.nicofighter45.enumeration;

public enum Metier {
	
	BUCHERON("§7Métier avec des réductions sur les blocks de constructions", 1),
	MINEUR("§7Métier avec des réductions sur les minéraux", 2),
	AGRICULTEUR("§7Métier avec des réductions sur les objets d'agriculture", 3),
	COMBATTANT("§7Métier avec des réductions sur les armes", 4);
	
	private String description;
	private int value;
	
	Metier(String desc, int v) {
		this.description = desc;
		this.value = v;
	}
	
	public String getDesc() {
		return description;
	}
	
	public int getValue() {
		return value;
	}
}