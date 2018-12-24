package Database;


public class Item {
	// group Id
	public int id = 0;

	// raw place Id
	private final int rawId;
	public Place place;

	public Item(int rawId) {
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
	
	public void view() {
		System.out.print(",Gid:"+id+",Pid:"+rawId+")-");
	}
	
	public int hashCode()
	{
		String string = ""+getId(); // This could be improved.
		return string.hashCode();
	}
	
	public boolean equals(Object object){
		Item item = (Item) object;
		if((item.getId() == this.getId())){
			return true;
		}
		return false;
	}
}
