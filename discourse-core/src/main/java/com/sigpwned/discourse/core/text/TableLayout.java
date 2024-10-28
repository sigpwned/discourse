package com.sigpwned.discourse.core.text;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import com.sigpwned.discourse.core.util.MoreInts;
import com.sigpwned.discourse.core.util.Text;

public class TableLayout {
  public static final class Row {
    private final List<String> columns;

    public Row(List<String> columns) {
      this.columns = unmodifiableList(columns);
    }

    public List<String> getColumns() {
      return columns;
    }

    public int getNumColumns() {
      return columns.size();
    }

    public String getColumn(int index) {
      return columns.get(index);
    }

    @Override
    public int hashCode() {
      return Objects.hash(columns);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Row other = (Row) obj;
      return Objects.equals(columns, other.columns);
    }
  }

  private final int numColumns;
  private final List<Row> rows;

  public TableLayout(int numColumns) {
    if (numColumns < 1)
      throw new IllegalArgumentException("numColumns must be at least 1");
    this.numColumns = numColumns;
    this.rows = new ArrayList<>();
  }

  public void addRow(Row row) {
    if (row == null)
      throw new NullPointerException();
    if (row.getNumColumns() != getNumColumns())
      throw new IllegalArgumentException("Row has different number of columns");
    rows.add(row);
  }

  public int getNumRows() {
    return rows.size();
  }

  public int getNumColumns() {
    return numColumns;
  }

  /**
   * Indicates the minimum width of a fixed-width column.
   */
  public static final int MIN_COLUMN_WIDTH = 2;

  /**
   * Indicates that the column should be as wide as the longest line in the column.
   */
  public static final int COLUMN_WIDTH_TIGHT = -1;

  /**
   * Indicates that the column should expand to share the remaining space with other columns evenly.
   */
  public static final int COLUMN_WIDTH_FLEX = -2;

  public String toString(int totalWidth, int[] columnWidths, int rowSpacing, int columnSpacing) {
    if (rows.isEmpty())
      return "";
    if (columnWidths == null) {
      columnWidths = new int[getNumColumns()];
      for (int i = 0; i < columnWidths.length; i++) {
        columnWidths[i] = COLUMN_WIDTH_FLEX;
      }
    }

    final int numColumns = getNumColumns();

    final int availableWidth = totalWidth - (numColumns - 1) * columnSpacing;

    if (numColumns * MIN_COLUMN_WIDTH > availableWidth)
      throw new IllegalArgumentException("totalWidth and columnSpacing is too small");

    // rows x columns x softwrapped lines
    List<List<List<String>>> data = new ArrayList<>();
    for (int ri = 0; ri < getNumRows(); ri++) {
      Row row = rows.get(ri);

      List<List<String>> r = new ArrayList<>();
      for (int ci = 0; ci < numColumns; ci++) {
        String cell = row.getColumn(ci);

        List<String> lines;
        if (cell.equals("")) {
          lines = List.of();
        } else {
          lines = Stream.of(cell.split("\r?\n")).map(String::strip).toList();
        }

        r.add(lines);
      }

      data.add(r);
    }

    int[] columnMaxLineWidths = new int[numColumns];
    for (int i = 0; i < columnMaxLineWidths.length; i++) {
      // We need at least two columns so we can hyphenate long text
      columnMaxLineWidths[i] = 2;
    }
    for (int ri = 0; ri < getNumRows(); ri++) {
      List<List<String>> r = data.get(ri);
      for (int ci = 0; ci < numColumns; ci++) {
        List<String> c = r.get(ci);
        columnMaxLineWidths[ci] = Math.max(columnMaxLineWidths[ci],
            c.isEmpty() ? 0 : c.stream().mapToInt(String::length).max().getAsInt());
      }
    }

    boolean[] flexColumns = new boolean[columnWidths.length];
    for (int i = 0; i < columnWidths.length; i++) {
      flexColumns[i] = (columnWidths[i] == COLUMN_WIDTH_FLEX);
    }

    int numFlexColumns = 0;
    for (int i = 0; i < flexColumns.length; i++) {
      if (flexColumns[i])
        numFlexColumns++;
    }

    // Calculate the width of the fixed columns
    int allocatedColumnWidth = 0;
    for (int i = 0; i < columnWidths.length; i++) {
      if (columnWidths[i] == COLUMN_WIDTH_TIGHT) {
        columnWidths[i] = columnMaxLineWidths[i];
        allocatedColumnWidth += columnWidths[i];
      } else if (columnWidths[i] == COLUMN_WIDTH_FLEX) {
        // We defer calculating this for now
      } else if (columnWidths[i] > 0) {
        if (columnWidths[i] < MIN_COLUMN_WIDTH)
          throw new IllegalArgumentException("Column width is too small");
        allocatedColumnWidth += columnWidths[i];
      } else {
        throw new IllegalArgumentException("Invalid column width");
      }
    }

    // Calculate the width of the flex columns
    if (numFlexColumns > 0) {
      int unallocatedColumnWidth = availableWidth - allocatedColumnWidth;

      for (int i = 0; i < columnWidths.length; i++) {
        if (flexColumns[i]) {
          columnWidths[i] = Math.max(MIN_COLUMN_WIDTH, unallocatedColumnWidth / numFlexColumns);
        }
      }

      while (MoreInts.sum(columnWidths) < availableWidth) {
        for (int i = 0; i < columnWidths.length; i++) {
          if (flexColumns[i]) {
            if (MoreInts.sum(columnWidths) < availableWidth)
              columnWidths[i] = columnWidths[i] + 1;
          }
        }
      }
    }

    // Render the table
    StringBuilder result = new StringBuilder();
    for (int ri = 0; ri < getNumRows(); ri++) {
      boolean lastRow = ri == getNumRows() - 1;

      List<List<String>> r = data.get(ri);

      List<List<String>> lines = new ArrayList<>();
      for (int ci = 0; ci < numColumns; ci++) {
        boolean lastColumn = ci == numColumns - 1;

        List<String> c = r.get(ci);

        List<String> wrappedAndPaddedAndSpacedLines = new ArrayList<>();
        for (int li = 0; li < c.size(); li++) {
          String line = c.get(li);

          List<String> wrappedLine = Text.wrap(line, columnWidths[ci]);

          List<String> wrappedAndPaddedLine = new ArrayList<>();
          for (int wli = 0; wli < wrappedLine.size(); wli++)
            wrappedAndPaddedLine.add(Text.rpad(wrappedLine.get(wli), columnWidths[ci]));

          List<String> wrappedAndPaddedAndSpacedLine = new ArrayList<>();
          for (int wpli = 0; wpli < wrappedAndPaddedLine.size(); wpli++) {
            String s = wrappedAndPaddedLine.get(wpli);
            if (!lastColumn)
              s += Text.times(" ", columnSpacing);
            wrappedAndPaddedAndSpacedLine.add(s);
          }

          wrappedAndPaddedAndSpacedLines.addAll(wrappedAndPaddedAndSpacedLine);
        }

        lines.add(wrappedAndPaddedAndSpacedLines);
      }

      int maxLines = lines.stream().mapToInt(List::size).max().getAsInt();
      for (int li = 0; li < maxLines; li++) {
        for (int ci = 0; ci < numColumns; ci++) {
          List<String> c = lines.get(ci);
          String text = li < c.size() ? c.get(li) : "";
          String line = Text.rpad(text, columnWidths[ci] + columnSpacing);
          result.append(line);
        }
        result.append("\n");
      }

      if (!lastRow)
        result.append(Text.times("\n", rowSpacing));
    }

    return result.toString();
  }
}
