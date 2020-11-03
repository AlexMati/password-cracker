package passwordcracker;

public class ProgressBar extends javax.swing.JProgressBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int current = 0;
	private int max = 0;
	
	public ProgressBar() {
		super();
	}
	
	public void setCurrent(int current) {
		this.current = current;
	}
	
	public void setMax(int max) {
		this.max = max;
	}
	
	public int getCurrent() {
		return this.current;
	}
	
	public int getMax() {
		return this.max;
	}
	
	public void incrementProgress() {
		int current = this.getCurrent() + 1;
		this.setCurrent(current);
		this.setValue((this.getCurrent() * 100) / this.getMax());
	}
}
