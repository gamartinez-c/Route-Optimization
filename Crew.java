public class Crew {

    private static int idCounter = 1;

    private String id;
    private double availableTime;
    private Node availableNode;
    private double speed;

    public Crew(String id, int availabilityTime, Node availabilityNode, double speed) {
        this.id = id;
        this.availableTime = availabilityTime;
        this.availableNode = availabilityNode;
        this.speed = speed;

        Crew.idCounter += 1;
    }

    //-----------------------GET-----------------------
    public String getId(){
        return this.id;
    }

    public double getAvailableTime(){
        return this.availableTime;
    }

    public Node getAvailableNode(){
        return this.availableNode;
    }

    public double getSpeed(){ return this.speed; }

    //-----------------------SET-----------------------
    public void setId(String id){
        this.id = id;
    }

    public void setAvailableTime(double availableTime){
        this.availableTime = availableTime;
    }

    public void setAvailableNode(Node nodo){
        this.availableNode = nodo;
    }

    //-----------------------OTHER-----------------------
    public double calculateTimeBetweenNodes(Node node1, Node node2){
        return node1.getDistanceToOtherNode(node2)/this.getSpeed();
    }

    //-----------------------TOSTRING-----------------------
    public String toString() {
        return "Crew: " + String.valueOf(this.id) +
                ", NI: " +  String.valueOf(this.availableNode.getId() +
                " ");
    }
}
