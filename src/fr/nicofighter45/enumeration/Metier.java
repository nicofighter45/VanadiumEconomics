package fr.nicofighter45.enumeration;

public enum Metier {
	
	BUCHERON("�7M�tier avec des r�ductions sur les blocks de constructions", 1),
	MINEUR("�7M�tier avec des r�ductions sur les min�raux", 2),
	AGRICULTEUR("�7M�tier avec des r�ductions sur les objets d'agriculture", 3),
	COMBATTANT("�7M�tier avec des r�ductions sur les armes", 4);
	
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