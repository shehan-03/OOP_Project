package View;

import Controller.DB;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ScheduleDelivery extends JFrame{
    private JPanel main;
    private JTable scheduleTable;
    private JTextField txtSearch;
    private JButton searchButton;
    private JTextField txtshipmentID;
    private JTextField txtcustomer_Name;
    private JDateChooser txtbookingTime;
    private JTextField txtSlot;
    private JButton doneButton;
    private JButton updateButton;

    public ScheduleDelivery() {
        setContentPane(main);

        String[] columnNames = {"Schedule_ID", "Shipment_ID", "Customer Name", "Booking Time", "Preferred Slot"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        scheduleTable.setModel(model);

        loadShipmentData();

        scheduleTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = scheduleTable.getSelectedRow();
                if (selectedRow >= 0) {

                    txtshipmentID.setText(scheduleTable.getValueAt(selectedRow,1).toString());
                    txtcustomer_Name.setText(scheduleTable.getValueAt(selectedRow, 2).toString());
                    java.util.Date bookingDate = (java.util.Date) scheduleTable.getValueAt(selectedRow, 3);
                    txtbookingTime.setDate(bookingDate);
                    txtSlot.setText(scheduleTable.getValueAt(selectedRow, 4).toString());

                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                book();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
    }

    public void loadShipmentData() {
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        model.setRowCount(0);

        String query = "SELECT * FROM delivery_schedule";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("schedule_id");
                int shipmentId = rs.getInt("shipment_id");
                String customer_name = rs.getString("customer_name");
                java.sql.Date bookingTime = rs.getDate("booking_time");
                String preferredSlot = rs.getString("preferred_slot");

                // Add row to table model
                model.addRow(new Object[]{id, shipmentId, customer_name, bookingTime, preferredSlot});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load shipment data");
        }
    }

    public void search() {
        String query = "SELECT sender_name FROM shipments WHERE shipment_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, txtSearch.getText());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    txtshipmentID.setText(txtSearch.getText());
                    txtcustomer_Name.setText(rs.getString("sender_name"));
                } else {
                    JOptionPane.showMessageDialog(this, "No shipment found with ID: " + txtSearch.getText());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load shipment data");
        }
    }

    public void book() {
        String query = "INSERT INTO delivery_schedule (shipment_id, customer_name, booking_time, preferred_slot) VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, txtSearch.getText());
            pst.setString(2, txtcustomer_Name.getText());
            java.util.Date selectedDate = txtbookingTime.getDate();
            java.sql.Date sqlDate = selectedDate != null ? new java.sql.Date(selectedDate.getTime()) : null;
            pst.setDate(3, sqlDate);
            pst.setString(4, txtSlot.getText());

            int rowsInserted = pst.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Booking successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Booking failed!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to book delivery schedule");
        }
    }

    public void update(){
        try (Connection con = DB.getConnection()) {

            String sql = "UPDATE delivery_schedule SET customer_name=?, booking_time=?, preferred_slot=? WHERE shipment_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtcustomer_Name.getText());
            java.util.Date selectedDate = txtbookingTime.getDate();
            java.sql.Date sqlDate = selectedDate != null ? new java.sql.Date(selectedDate.getTime()) : null;
            pst.setDate(2, sqlDate);
            pst.setString(3, txtSlot.getText());
            pst.setString(4, txtshipmentID.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Booking Updated!");
            loadShipmentData();
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public void clearFields(){
        txtshipmentID.setText("");
        txtcustomer_Name.setText("");
        txtSearch.setText("");
        txtSlot.setText("");
        txtbookingTime.setDate(null);
    }



    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ScheduleDelivery frame = new ScheduleDelivery();
            frame.setTitle("Schedule Delivery");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
