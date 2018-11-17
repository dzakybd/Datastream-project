package prefixspan;

public class Item{
	
	// group Id
	private int id = 0;
	
	// raw place Id
	private final int rawId;
	
	public Item(int rawId){
		this.rawId = rawId;
	}

	public int getId() {
		return id;
	}

	public int getRawId() {
		return rawId;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String toString(){
		return "" + getId();
	}
	
	public boolean equals(Object object){
		Item item = (Item) object;
		if((item.getId() == this.getId())){
			return true;
		}
		return false;
	}
	
	public int hashCode()
	{
		String string = ""+getId(); // This could be improved.
		return string.hashCode();
	}
	
}
