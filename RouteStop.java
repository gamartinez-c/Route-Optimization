import org.apache.commons.math3.util.Precision;

public class RouteStop {

    private Node node;
    private double arrivingTime;
    private double departingTime;
    private boolean esfija;
    private static double desfasajeFijas;

    public RouteStop(Node node, double arriving_time) {
        this.node = node;
        this.arrivingTime = arriving_time;
        this.departingTime = this.arrivingTime + node.getTaskDuration();
        this.esfija = false;
    }

    public RouteStop(Node node, double arriving_time, boolean esfija) {
        this.node = node;
        this.arrivingTime = arriving_time;
        this.departingTime = this.arrivingTime + node.getTaskDuration();
        this.esfija = esfija;
    }

    //GET
    public Node getNode(){
        return this.node;
    }

    public double getArrivingTime(){
        return this.arrivingTime;
    }

    public double getDepartingTime(){
        return this.departingTime;
    }

    public double getLoss(){
        double auxLoss = 0;
        if(this.node.getInitialLoss()){
            auxLoss = this.node.getOutput() * this.departingTime;
        } else {
            auxLoss = this.node.getOutput() * this.node.getTaskDuration();
        }
        return auxLoss;
    }

    public boolean getEsfija() {
        return esfija;
    }

    public static double getDesfasajeFijas() {
        return desfasajeFijas;
    }

    //SET
    public  void setNode(Node nodo){
        this.node = nodo;
    }

    public void setArrivingTime(double arrivingTime){
        this.arrivingTime = arrivingTime;
    }

    public void setDepartingTime(double departingTime){
        this.departingTime = departingTime;
    }

    public void setEsfija(boolean esfija) {
        this.esfija = esfija;
    }

    public static void setDesfasajeFijas(double desfasajeFijas) {
        RouteStop.desfasajeFijas = desfasajeFijas;
    }

    //TOSTRING
    public String toString() {
        return this.node +", AT:" + Precision.round(this.arrivingTime,2) +
                ", DT:" + Precision.round(this.departingTime,2) +
                ", Fija: " + esfija;
    }

    public boolean equals(Object obj){
        if (obj instanceof RouteStop) {
            RouteStop otherRouteStop = (RouteStop) obj;
            boolean booleanID = this.getNode().getId() == otherRouteStop.getNode().getId();
            boolean booleanAT = this.getArrivingTime() == otherRouteStop.getArrivingTime();
            boolean booleanDT = this.getDepartingTime() == otherRouteStop.getDepartingTime();
            if( !(booleanID & booleanAT & booleanDT) ){
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}
