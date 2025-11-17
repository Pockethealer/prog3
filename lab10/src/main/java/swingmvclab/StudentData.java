package swingmvclab;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/*
 * A hallgat�k adatait t�rol� oszt�ly.
 */
public class StudentData extends AbstractTableModel {

    /*
     * Ez a tagv�ltoz� t�rolja a hallgat�i adatokat.
     * NE M�DOS�TSD!
     */
    List<Student> students = new ArrayList<Student>();

    private static final String[] COLUMN_NAMES = { "Név", "Neptun", "Aláírás", "Jegy" };

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0, 1:
                return String.class;

            case 2:
                return Boolean.class;

            case 3:
                return Integer.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student student = students.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return student.getName();
            case 1:
                return student.getNeptun();
            case 2:
                return student.hasSignature();
            default:
                return student.getGrade();
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2 || columnIndex == 3;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Student student = students.get(rowIndex);

        // Meghatározzuk, hogy melyik oszlopban történt a változás.
        switch (columnIndex) {
            case 2:
                student.setSignature((Boolean) aValue);
                break;
            case 3:
                if (aValue instanceof Integer) {
                    student.setGrade((Integer) aValue);
                }
                break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public void addStudent(String name, String neptun) {
        Student newStudent = new Student(name, neptun, false, 0);
        students.add(newStudent);
        int newRowIndex = students.size() - 1;
        fireTableRowsInserted(newRowIndex, newRowIndex);
    }

}
