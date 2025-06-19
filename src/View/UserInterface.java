package View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterface {
    private JPanel Main;
    private JButton managePersonalButton;
    private JButton manageShipmentButton;
    private JButton scheduleDeliveryButton;
    private JButton TrackShipmentButton;
    private JButton assignDriverButton;
    private JButton generateMonthlyReportsButton;
    private JButton notifyCustomerButton;
    private JButton addJobButton;
    private JButton addPartButton;
    private JLabel deslbl;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("User Interface");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new UserInterface().Main);
            frame.pack();
            frame.setVisible(true);
        });
    }

    public UserInterface() {


        managePersonalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignDriverView();
            }
        });
        manageShipmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                managePersonalView();
            }
        });
        scheduleDeliveryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageShipment();
            }
        });
        TrackShipmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScheduleDelivery();
            }
        });
        assignDriverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackShipment();
            }
        });
        generateMonthlyReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //MonthlySalesReportForm();
            }
        });

    }

    private void assignDriverView() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            AssignDriver frame = new AssignDriver();
            frame.setTitle("Assign Drivers");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(500, 350);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private void managePersonalView() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ManagePersonal frame = new ManagePersonal();
            frame.setTitle("Management Personal");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private void ManageShipment() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ManageShipment frame = new ManageShipment();
            frame.setTitle("Shipment Management");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

    private void ScheduleDelivery() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ScheduleDelivery frame = new ScheduleDelivery();
            frame.setTitle("Schedule Delivery");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

    private void TrackShipment() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            TrackShipment frame = new TrackShipment();
            frame.setTitle("Track Shipment");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

//    private void MonthlySalesReportForm() {
//        new MonthlySalesReportForm();
//
//    }


}
