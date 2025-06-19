package View;

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
import Controller.*;
import com.toedter.calendar.JDateChooser;

public class ManageShipment extends JFrame {
    private JTable manageShipmentTable;
    private JTextField txtSenderName;
    private JTextField txtReceiverName;
    private JTextField txtDetails;
    private JTextField txtStatus;
    private JDateChooser txtDeliverDate;
    private JTextField txtLocation;
    private JDateChooser txtEstimateDelivery;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel main;
    private JTextField shipment_id;
    private JTextField txtTrackingNum;
    private JButton searchButton;
    private JTextField txtAddress;
    private JTextField txtEmail;

    public ManageShipment() {
        setContentPane(main);
        pack();
        setLocationRelativeTo(null);

        String[] columnNames = {"shipment_id", "Sender", "Receiver", "Address", "Package Details", "Status", "Shipment Date", "current_location", "estimated_delivery"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        manageShipmentTable.setModel(model);

        loadShipmentData();

        manageShipmentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = manageShipmentTable.getSelectedRow();
                if (selectedRow >= 0) {

                    shipment_id.setText(manageShipmentTable.getValueAt(selectedRow,0).toString());
                    txtSenderName.setText(manageShipmentTable.getValueAt(selectedRow, 1).toString());
                    txtReceiverName.setText(manageShipmentTable.getValueAt(selectedRow, 2).toString());
                    txtDetails.setText(manageShipmentTable.getValueAt(selectedRow, 3).toString());
                    txtStatus.setText(manageShipmentTable.getValueAt(selectedRow, 4).toString());
                    java.util.Date shipmentDate = (java.util.Date) manageShipmentTable.getValueAt(selectedRow, 5);
                    java.util.Date estimatedDate = (java.util.Date) manageShipmentTable.getValueAt(selectedRow, 7);
                    txtDeliverDate.setDate(shipmentDate);
                    txtEstimateDelivery.setDate(estimatedDate);

                    txtLocation.setText(manageShipmentTable.getValueAt(selectedRow, 6).toString());
                }
            }
        });




        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addShipment();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateShipment();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // deleteShipment(int shipmentId);
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
    }

    public void loadShipmentData() {
        DefaultTableModel model = (DefaultTableModel) manageShipmentTable.getModel();
        model.setRowCount(0);

        String query = "SELECT * FROM shipments";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("shipment_id");
                String senderName = rs.getString("sender_name");
                String receiverName = rs.getString("receiver_name");
                String address = rs.getString("destinationAddress");
                String packageDetails = rs.getString("package_details");
                String status = rs.getString("delivery_status");
                java.sql.Date shipmentDate = rs.getDate("delivery_date");
                String location = rs.getString("current_location");
                java.sql.Date estimated_delivery = rs.getDate("estimated_delivery");

                model.addRow(new Object[]{id, senderName, receiverName,address, packageDetails, status, shipmentDate, location, estimated_delivery});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load shipment data");
        }
    }

    private void addShipment() {
        try (Connection con = DB.getConnection()) {
            String sql = "INSERT INTO shipments (sender_name, senderEmail, receiver_name, destinationAddress, package_details, delivery_status, delivery_date, current_location,estimated_delivery, trackingNum) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtSenderName.getText());
            pst.setString(2, txtEmail.getText());
            pst.setString(3, txtReceiverName.getText());
            pst.setString(4, txtAddress.getText());
            pst.setString(5, txtDetails.getText());
            pst.setString(6, txtStatus.getText());
            java.util.Date selectedDate = txtDeliverDate.getDate();
            java.sql.Date sqlDate = selectedDate != null ? new java.sql.Date(selectedDate.getTime()) : null;
            pst.setDate(7, sqlDate);

            pst.setString(8, txtLocation.getText());

            java.util.Date estDate = txtEstimateDelivery.getDate();
            java.sql.Date sqlEstDate = estDate != null ? new java.sql.Date(estDate.getTime()) : null;
            pst.setDate(9, sqlEstDate);

            pst.setString(10, txtTrackingNum.getText());

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Shipment Added!");
            loadShipmentData();
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateShipment() {
        try (Connection con = DB.getConnection()) {

            String sql = "UPDATE shipments SET sender_name=?, senderEmail=?, receiver_name=?, destinationAddress=?, package_details=?,  delivery_status=?, trackingNum=? WHERE shipment_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtSenderName.getText());
            pst.setString(2, txtEmail.getText());
            pst.setString(3, txtReceiverName.getText());
            pst.setString(4, txtAddress.getText());
            pst.setString(5, txtDetails.getText());
            pst.setString(6, txtStatus.getText());
            pst.setString(7, txtTrackingNum.getText());
            pst.setString(8, shipment_id.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Shipment Updated!");
            loadShipmentData();
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteShipment(int shipmentId) {
        try (Connection con = DB.getConnection()) {
            String sql = "DELETE FROM shipments WHERE shipment_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, shipmentId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Shipment Deleted!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void search() {
        String query = "SELECT * FROM shipments WHERE shipment_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, Integer.parseInt(shipment_id.getText()));

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    shipment_id.setText(String.valueOf(rs.getInt("shipment_id")));
                    txtSenderName.setText(rs.getString("sender_name"));
                    txtEmail.setText(rs.getString("senderEmail"));
                    txtReceiverName.setText(rs.getString("receiver_name"));
                    txtAddress.setText(rs.getString("destinationAddress"));
                    txtDetails.setText(rs.getString("package_details"));
                    txtStatus.setText(rs.getString("delivery_status"));
                    txtDeliverDate.setDate(rs.getDate("delivery_date"));
                    txtLocation.setText(rs.getString("current_location"));
                    txtEstimateDelivery.setDate(rs.getDate("estimated_delivery"));
                    txtTrackingNum.setText(rs.getString("trackingNum"));
                } else {
                    JOptionPane.showMessageDialog(this, "No shipment found with ID: " + shipment_id.getText());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load shipment data");
        }
    }

    private void clearFields() {
        txtSenderName.setText("");
        txtReceiverName.setText("");
        txtDetails.setText("");
        txtStatus.setText("");
        txtDeliverDate.setDate(null);
        txtLocation.setText("");
        txtEstimateDelivery.setDate(null);
        shipment_id.setText("");
        txtEmail.setText("");
        txtTrackingNum.setText("");
        txtAddress.setText("");
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ManageShipment frame = new ManageShipment();
            frame.setTitle("Shipment Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


}
