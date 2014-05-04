package Util;

public class TableCellIndex {
  public int rowIndex;
  public int columnIndex;
  public TableCellIndex (int aRowIndex, int aColumnIndex) {
    rowIndex = aRowIndex;
    columnIndex = aColumnIndex;
  }
  public boolean equals (Object o) {
    return (o instanceof TableCellIndex)? ((TableCellIndex) o).rowIndex == rowIndex && ((TableCellIndex) o).columnIndex == columnIndex : false;
  }
  public int hashCode () {
    return rowIndex + columnIndex;
  }
}
			