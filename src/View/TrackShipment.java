package View;

import Controller.DB;
import Controller.EmailUtil;
import com.toedter.calendar.JDateChooser;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrackShipment extends JFrame{
    private JPanel main;
    private JTextField txtSearch;
    private JButton searchButton;
    private JTextField txtshipmentID;
    private JTextField txtStatus;
    private JTextField txtStatusUpdate;
    private JButton updateButton;
    private JTextArea txtDelayInfo;
    private JTextField txtTrackingNum;
    private JDateChooser txtDeliveyDate;
    private JDateChooser txtEstimateDelivery;

    public TrackShipment() {
        setContentPane(main);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
    }

    private void search() {
        String query = """
        SELECT 
            s.shipment_id, 
            s.delivery_status, s.delivery_date,  
            s.estimated_delivery, s.trackingNum,
            s.current_location, t.delay_info
        FROM shipments s
        LEFT JOIN shipment_tracking t ON s.shipment_id = t.shipment_id
        WHERE s.shipment_id = ? OR t.shipment_id = ?
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            int shipmentId = Integer.parseInt(txtSearch.getText());
            pst.setInt(1, shipmentId);
            pst.setInt(2, shipmentId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {

                    txtshipmentID.setText(String.valueOf(rs.getInt("shipment_id")));
                    txtTrackingNum.setText(rs.getString("trackingNum"));
                    txtStatus.setText(rs.getString("delivery_status"));
                    txtDeliveyDate.setDate(rs.getDate("delivery_date"));
                    txtStatusUpdate.setText(rs.getString("current_location"));
                    txtEstimateDelivery.setDate(rs.getDate("estimated_delivery"));
                    txtDelayInfo.setText(rs.getString("delay_info"));

                } else {
                    JOptionPane.showMessageDialog(this, "No shipment found with ID: " + txtSearch.getText());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred during search.");
        }
    }


    public void update() {
        try (Connection con = DB.getConnection()) {

            // --- Update shipment_tracking table ---
            String trackingSQL = "UPDATE shipment_tracking SET shipment_id = ?, status_update = ?, delay_info = ? WHERE shipment_id = ?";
            PreparedStatement pst1 = con.prepareStatement(trackingSQL);
            pst1.setString(1, txtshipmentID.getText());
            pst1.setString(2, txtStatusUpdate.getText());
            pst1.setString(3, txtDelayInfo.getText());
            pst1.setInt(4, Integer.parseInt(txtshipmentID.getText()));
            pst1.executeUpdate();

            // --- Update shipments table ---
            String shipmentSQL = "UPDATE shipments SET current_location = ?, trackingNum = ? WHERE shipment_id = ?";
            PreparedStatement pst2 = con.prepareStatement(shipmentSQL);
            pst2.setString(1, txtStatusUpdate.getText());
            pst2.setString(2, txtTrackingNum.getText());
            pst2.setInt(3, Integer.parseInt(txtshipmentID.getText()));
            pst2.executeUpdate();

            String emailSQL = "SELECT senderEmail FROM shipments WHERE shipment_id = ?";
            PreparedStatement pst3 = con.prepareStatement(emailSQL);
            pst3.setInt(1, Integer.parseInt(txtshipmentID.getText()));
            ResultSet rs = pst3.executeQuery();

            if (rs.next()) {
                String senderEmail = rs.getString("senderEmail");


                String subject = "Shipment Update - ID: " + txtshipmentID.getText();
                String body = "Dear Sender,\n\n"
                        + "Your shipment (ID: " + txtshipmentID.getText() + ") has been updated.\n"
                        + "Current Location: " + txtStatusUpdate.getText() + "\n"
                        + "Tracking Number: " + txtTrackingNum.getText() + "\n"
                        + "Delay Info: " + txtDelayInfo.getText() + "\n\n"
                        + "Thank you.";


                EmailUtil.sendEmail(senderEmail, subject, body);
            }

            JOptionPane.showMessageDialog(this, "Shipment and Tracking Info Updated!");


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            TrackShipment frame = new TrackShipment();
            frame.setTitle("Track Shipment");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
