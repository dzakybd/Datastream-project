package split;

public class GridDensity {
	private int row;
	private int col;
	private double density;
	public GridDensity(int row, int col, double density){
		this.setRow(row);
		this.setCol(col);
		this.setDensity(density);
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public double getDensity() {
		return density;
	}
	public void setDensity(double density) {
		this.density = density;
	}
}
