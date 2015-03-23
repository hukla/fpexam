package model;

public class FPColumnCounts {
		private char column;
		private int support = 0;
		
		public FPColumnCounts(char column) {
			this.column = column;
		}
		
		public FPColumnCounts(char column, int sup) {
			this.column = column;
			this.support = sup;
		}
		
		public int getSupport() {
			return support;
		}
		public void setSupport(int support) {
			this.support = support;
		}
		
		
}
