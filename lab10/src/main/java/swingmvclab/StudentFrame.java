package swingmvclab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.Component;
import java.awt.Color;

/*
 * A megjelen�tend� ablakunk oszt�lya.
 */
public class StudentFrame extends JFrame {

    /*
     * Ebben az objektumban vannak a hallgat�i adatok.
     * A program indul�s ut�n bet�lti az adatokat f�jlb�l, bez�r�skor pedig kimenti
     * oda.
     * 
     * NE M�DOS�TSD!
     */
    private StudentData data;

    private JTextField nameField;
    private JTextField neptunField;

    private class StudentTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            int modelRow = table.convertRowIndexToModel(row);
            Student student = data.students.get(modelRow);
            boolean isFailing = !student.hasSignature() || student.getGrade() < 2;
            if (isSelected) {
            } else if (isFailing) {
                cellComponent.setBackground(Color.RED);
                cellComponent.setForeground(Color.BLACK);
            } else {
                cellComponent.setBackground(Color.GREEN);
                cellComponent.setForeground(Color.BLACK);
            }
            return cellComponent;
        }
    }

    /*
     * Itt hozzuk l�tre �s adjuk hozz� az ablakunkhoz a k�l�nb�z� komponenseket
     * (t�bl�zat, beviteli mez�, gomb).
     */
    private void initComponents() {
        this.setLayout(new BorderLayout());
        /* 2-es feladat */
        JTable table = new JTable(this.data);
        table.setFillsViewportHeight(true);
        /* 6. feladat */
        TableRowSorter<StudentData> sorter = new TableRowSorter<>(this.data);
        table.setRowSorter(sorter);
        /* 7. feladat */
        table.setDefaultRenderer(String.class, new StudentTableCellRenderer());
        table.setDefaultRenderer(Integer.class, new StudentTableCellRenderer());
        table.setDefaultRenderer(Boolean.class, new StudentTableCellRenderer());
        /*  */
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);
        /* 5. feladat */
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Név:"));
        nameField = new JTextField(20);
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Neptun:"));
        neptunField = new JTextField(6);
        inputPanel.add(neptunField);
        JButton addButton = new JButton("Felvesz");
        inputPanel.add(addButton);
        this.add(inputPanel, BorderLayout.SOUTH);
        addButton.addActionListener(e -> addStudentButtonClicked());
    }

    private void addStudentButtonClicked() {
        String name = nameField.getText();
        String neptun = neptunField.getText();
        if (name.trim().isEmpty() && neptun.trim().isEmpty()) {
            return;
        }
        this.data.addStudent(name, neptun);
        nameField.setText("");
        neptunField.setText("");
    }

    /*
     * Az ablak konstruktora.
     * 
     * NE M�DOS�TSD!
     */
    @SuppressWarnings("unchecked")
    public StudentFrame() {
        super("Hallgatói nyilvántartás");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Indul�skor bet�ltj�k az adatokat
        try {
            data = new StudentData();
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("students.dat"));
            data.students = (List<Student>) ois.readObject();
            ois.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Bez�r�skor mentj�k az adatokat
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("students.dat"));
                    oos.writeObject(data.students);
                    oos.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Fel�p�tj�k az ablakot
        setMinimumSize(new Dimension(500, 200));
        initComponents();
    }

    /*
     * A program bel�p�si pontja.
     * 
     * NE M�DOS�TSD!
     */
    public static void main(String[] args) {
        // Megjelen�tj�k az ablakot
        StudentFrame sf = new StudentFrame();
        sf.setVisible(true);
    }
}
