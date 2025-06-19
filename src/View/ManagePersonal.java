package View;

import Controller.DB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagePersonal extends JFrame {
    private JTable managePersonalTable;
    private JTextField txtName;
    private JTextField txtContactNo;
    private JTextField txtStatus;
    private JTextField txtRoute;
    private JTextField txtSchedule;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JTextField id;
    private JPanel main;
    private JTextField txtEmail;

    public ManagePersonal()  {
        setContentPane(main);
        pack();
        setLocationRelativeTo(null);

        String[] columnNames = {"Id", "Name", "Contact Number", "Email", "Availability Status", "Assign District", "Schedule"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        managePersonalTable.setModel(model);

        loadPersonalData();

        managePersonalTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = managePersonalTable.getSelectedRow();
                if (selectedRow >= 0) {

                    id.setText(managePersonalTable.getValueAt(selectedRow,0).toString());
                    txtName.setText(managePersonalTable.getValueAt(selectedRow, 1).toString());
                    txtContactNo.setText(managePersonalTable.getValueAt(selectedRow, 2).toString());
                    txtEmail.setText(managePersonalTable.getValueAt(selectedRow, 3).toString());
                    txtStatus.setText(managePersonalTable.getValueAt(selectedRow, 4).toString());
                    txtRoute.setText(managePersonalTable.getValueAt(selectedRow, 5).toString());
                    txtSchedule.setText(managePersonalTable.getValueAt(selectedRow,6).toString());
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPersonal() ;
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePersonal();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteShipment();
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
    }

    public void loadPersonalData() {
        DefaultTableModel model = (DefaultTableModel) managePersonalTable.getModel();
        model.setRowCount(0);

        String query = "SELECT * FROM delivery_personnel";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("personnel_id");
                String Name = rs.getString("name");
                String contactNumber = rs.getString("contact_number");
                String email = rs.getString("email");
                String availabilityStatus = rs.getString("availability_status");
                String assignedRoute = rs.getString("assigned_route");
                String schedule = rs.getString("schedule");

                model.addRow(new Object[]{id, Name, contactNumber, email, availabilityStatus, assignedRoute, schedule});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to Personal data");
        }
    }

    private void addPersonal() {
        try (Connection con = DB.getConnection()) {
            String sql = "INSERT INTO delivery_personnel (name, contact_number, email, availability_status, assigned_route, schedule) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtName.getText());
            pst.setString(2, txtContactNo.getText());
            pst.setString(3, txtEmail.getText());
            pst.setString(4, txtStatus.getText());
            pst.setString(5, txtRoute.getText());
            pst.setString(6,txtSchedule.getText());

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Shipment Added!");
            loadPersonalData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updatePersonal() {
        try (Connection con = DB.getConnection()) {

            String sql = "UPDATE delivery_personnel SET name=?, contact_number=?, email=?, availability_status=?, assigned_route=?, schedule=? WHERE personnel_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtName.getText());
            pst.setString(2, txtContactNo.getText());
            pst.setString(3, txtEmail.getText());
            pst.setString(4, txtStatus.getText());
            pst.setString(5, txtRoute.getText());
            pst.setString(6, txtSchedule.getText());
            pst.setString(7, id.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Shipment Updated!");
            loadPersonalData();
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteShipment() {
        try (Connection con = DB.getConnection()) {
            String sql = "DELETE FROM delivery_personnel WHERE personnel_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, id.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Personal Deleted!");
            loadPersonalData();
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtContactNo.setText("");
        txtStatus.setText("");
        txtRoute.setText("");
        txtSchedule.setText("");
        id.setText("");
        txtEmail.setText("");
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ManagePersonal frame = new ManagePersonal();
            frame.setTitle("Management Personal");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
