package fr.nicofighter45.enumeration;

public enum Category {
	
	CONSTRUCTION(0, "§9Construction"),
	FARM(1, "§aFarm"),
	MINERAUX(2, "§dMinéraux"),
	COMBAT(3, "§4Combat"),
	AUTRE(4, "§fAutre");
	
	private int value;
	private String name;
	
	Category(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
	
	public static Category getValuefromInt(int number) {
		if(number == 0) {
			return CONSTRUCTION;
		}else if(number == 1) {
			return FARM;
		}else if(number == 2) {
			return MINERAUX;
		}else if(number == 3) {
			return COMBAT;
		}else if(number == 4) {
			return AUTRE;
		}
		return null;
	}
	
}