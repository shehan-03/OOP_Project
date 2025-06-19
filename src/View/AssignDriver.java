package View;

import Controller.DB;
import Controller.EmailUtil;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssignDriver extends JFrame{
    private JPanel main;
    private JTextField txtSearchID;
    private JButton searchButton;
    private JButton searchDriverButton;
    private JTextField txtShipmentID;
    private JTextField txtPackageDetails;
    private JTextField txtReceiverName;
    private JTextField txtAddress;
    private JComboBox comboBoxDrivers;
    private JTextField txtNote;
    private JButton assignDriverButton;
    private JTextField txtTrackingNum;
    private JDateChooser txtEstimatedDelivery;
    private JTextField txtDistrict;

    public AssignDriver() {
        setContentPane(main);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchShipment();
            }
        });
        searchDriverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchDriver();
            }
        });
        assignDriverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignDriver();
            }
        });
    }

    public void searchShipment(){
        String query = "SELECT * FROM shipments WHERE shipment_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, Integer.parseInt(txtSearchID.getText()));

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    txtShipmentID.setText(String.valueOf(rs.getInt("shipment_id")));
                    txtReceiverName.setText(rs.getString("receiver_name"));
                    txtAddress.setText(rs.getString("destinationAddress"));
                    txtPackageDetails.setText(rs.getString("package_details"));
                    txtEstimatedDelivery.setDate(rs.getDate("estimated_delivery"));
                    txtTrackingNum.setText(rs.getString("trackingNum"));
                    txtDistrict.setText(rs.getString("District"));
                } else {
                    JOptionPane.showMessageDialog(this, "No shipment found with ID: " + txtShipmentID.getText());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load shipment data");
        }
    }

    public void searchDriver() {
        String district = txtDistrict.getText().trim();
        comboBoxDrivers.removeAllItems();

        String query = "SELECT name FROM delivery_personnel WHERE availability_status = 'Available' AND assigned_route = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, district);

            try (ResultSet rs = pst.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    comboBoxDrivers.addItem(rs.getString("name"));
                    found = true;
                }

                if (!found) {
                    JOptionPane.showMessageDialog(this, "No available drivers in " + district);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching available drivers");
        }
    }

    public void assignDriver() {
        String driverName = (String) comboBoxDrivers.getSelectedItem();
        String shipmentID = txtShipmentID.getText().trim();

        if (driverName == null || shipmentID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a driver and enter a shipment ID.");
            return;
        }

        String getIdQuery = "SELECT personnel_id, email FROM delivery_personnel WHERE name = ?";
        String insertQuery = "INSERT INTO driver_assignments (shipment_id, personnel_id, assigned_date) VALUES (?, ?, NOW())";
        String updateAvailabilityQuery = "UPDATE delivery_personnel SET availability_status = 'Unavailable' WHERE personnel_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement getIdStmt = conn.prepareStatement(getIdQuery)) {

            getIdStmt.setString(1, driverName);
            ResultSet rs = getIdStmt.executeQuery();

            if (rs.next()) {
                int personnelId = rs.getInt("personnel_id");
                String email = rs.getString("email");
                System.out.println(email);

                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, shipmentID);
                    insertStmt.setInt(2, personnelId);

                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        // --- Update availability ---
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateAvailabilityQuery)) {
                            updateStmt.setInt(1, personnelId);
                            updateStmt.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(this, "Driver assigned successfully!");

                        // Send email
                        String subject = "New Delivery Assignment";
                        String message = "Dear " + driverName + ",\n\n" +
                                "You have been assigned to shipment ID: " + shipmentID + ".\n\n" +
                                "Package Details: " + txtPackageDetails + "\n" +
                                "Receiver Name: " + txtReceiverName + "\n" +
                                "Tracking Number: " + txtTrackingNum + "\n" +
                                "Estimated Delivery: " + txtEstimatedDelivery + "\n" +
                                "Destination Address: " + txtAddress + "\n" +
                                "District: " + txtDistrict + "\n" +
                                "Note: " + txtNote + "\n\n" +
                                "Please check your dashboard for more details.\n\n" +
                                "Thank you.";

                        EmailUtil.sendEmail(email, subject, message);
                    } else {
                        JOptionPane.showMessageDialog(this, "Assignment failed.");
                    }
                }

            } else {
                JOptionPane.showMessageDialog(this, "Driver not found in the database.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error assigning driver.");
        }
    }





    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            AssignDriver frame = new AssignDriver();
            frame.setTitle("Assign Drivers");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 350);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
