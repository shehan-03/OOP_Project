package Model;

public class Shipment {
    private int shipmentId;
    private String senderName;
    private String receiverName;
    private String packageDetails;
    private String deliveryStatus;

    public Shipment(int shipmentId, String senderName, String receiverName, String packageDetails, String deliveryStatus) {
        this.shipmentId = shipmentId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.packageDetails = packageDetails;
        this.deliveryStatus = deliveryStatus;
    }

    public int getShipmentId() {
        return shipmentId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getPackageDetails() {
        return packageDetails;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setPackageDetails(String packageDetails) {
        this.packageDetails = packageDetails;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
