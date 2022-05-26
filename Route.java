import org.apache.commons.math3.analysis.function.Min;

import java.util.ArrayList;
import java.util.HashMap;

public class Route {
    
    private Crew crew;
    private double startTime;
    private ArrayList<Node> nodes;
    private ArrayList<RouteStop> routeStopSequence;
    private Node primerNode;
    private static HashMap<Crew, ArrayList<RouteStop>> mapaNodosFijos = new HashMap<>();
    private static ArrayList<Node> nodosFijos = new ArrayList<>();
    private static HashMap<Crew, Node> mapaNodosContinuidadDePrograma = new HashMap<>();

    //-----------------------INICIALIZADORES-----------------------
    public Route(Crew crew,double start_time,ArrayList<Node> nodes) {
        this.crew = crew;
        this.startTime = start_time;
        this.nodes = nodes;

        /*Solo si instanciamos de 0 nos ocupamos de las fijas
         si no es de 0, se debe a una copia por lo que no nos ocupamos*/
        if (mapaNodosContinuidadDePrograma.containsKey(crew)){
            this.primerNode = mapaNodosContinuidadDePrograma.get(crew);
        } else if (crew != null) {
            this.primerNode = crew.getAvailableNode();
        } else {
            this.primerNode = null;
        }
        if (nodes.isEmpty()) {
            if (mapaNodosContinuidadDePrograma.containsKey(crew)) {
                nodes.add(mapaNodosContinuidadDePrograma.get(crew));
            }
            if (mapaNodosFijos.containsKey(crew)) {
                for (int i = 0; i < mapaNodosFijos.get(crew).size(); i++) {
                    RouteStop routeStopAAgregar = mapaNodosFijos.get(crew).get(i);
                    for (int j = i + 1; j < mapaNodosFijos.get(crew).size(); j++) {
                        if (routeStopAAgregar.getArrivingTime() > mapaNodosFijos.get(crew).get(j).getArrivingTime()) {
                            routeStopAAgregar = mapaNodosFijos.get(crew).get(j);
                        }
                    }
                    nodes.add(routeStopAAgregar.getNode());
                }
            }
        }
        this.refreshRouteStopSequence();
    }

    //-----------------------GET-----------------------
    public Crew getCrew(){
        return this.crew;
    }
    public static ArrayList<Node> getNodosFijos() {
        return nodosFijos;
    }
    public double getStartTime(){
        return this.startTime;
    }
    public ArrayList<Node> getNodes(){
        return this.nodes;
    }
    public ArrayList<RouteStop> getRouteStopSequence(){
        return this.routeStopSequence;
    }
    public Node getPrimerNode(){
        return this.primerNode;
    }
    public double getTimeTraveling(){
        double td = 0;
        double aux = 0;
        for(RouteStop r: routeStopSequence){
            td += r.getNode().getTaskDuration();
        }
        aux = routeStopSequence.get(routeStopSequence.size() - 1).getDepartingTime() - td;
        return aux;
    }
    public static HashMap<Crew, ArrayList<RouteStop>> getMapaNodosFijos() {
        return mapaNodosFijos;
    }
    public static HashMap<Crew, Node> getMapaNodosContinuidadDePrograma() {
        return mapaNodosContinuidadDePrograma;
    }

    //-----------------------SET-----------------------
    public void setCrew(Crew crew){
        this.crew = crew;
    }
    public void setStartTime(double startTime){
        this.startTime = startTime;
    }
    public static void setNodosFijos(ArrayList<Node> nodosFijos) {
        Route.nodosFijos = nodosFijos;
    }
    public void setNodes(ArrayList<Node> nodes){
        this.nodes = nodes;
    }
    public void setRouteStopSequence(ArrayList<RouteStop> routeStopSequence){
        this.routeStopSequence = routeStopSequence;
    }
    public void setPrimerNode(Node primernodo){
        this.primerNode = primernodo;
    }
    public static void setMapaNodosFijos(HashMap<Crew, ArrayList<RouteStop>> mapaNodosFijos) {
        Route.mapaNodosFijos = mapaNodosFijos;
    }
    public static void setMapaNodosContinuidadDePrograma(HashMap<Crew, Node> mapaNodosContinuidadDePrograma) {
        Route.mapaNodosContinuidadDePrograma = mapaNodosContinuidadDePrograma;
    }

    //-----------------------OTROS-----------------------
    //esta funcion es la que siempre se debe utilizar para agregar nodos
    public void append_node_to_route(Node newNode) {
        //pregunto si tiene fijas o no porque de no tenerlas solo debo agregar al final.
        //Los espacios muertos solo se me pueden generar al rededor de las fijas
        this.refreshRouteStopSequence();
        if (!newNode.getEsFijo() && !newNode.getEsContinuidadDePrograma()) {
            if (tieneNodosFijos()) {
                for (int i = 0; i < routeStopSequence.size(); i++) {
                    if (this.getRouteStopSequence().get(i).getEsfija()) {
                        double availableTime = calculateAvailableTimeInPosition(i);
                        double tiempoDeIntroducirNodo = totalTimeNeededForNodeInRouteByPosition(newNode, i);
                        //si estoy en el indice 0 debo comparar contra el nodo origen de la cuadrilla
                        if (availableTime >= tiempoDeIntroducirNodo) {
                            nodes.add(i, newNode);
                            break;
                        }
                    }
                    if (i == routeStopSequence.size() - 1) {
                        nodes.add(newNode);
                        break;
                    }
                }
            } else {
                this.nodes.add(newNode);
            }
            this.refreshRouteStopSequence();
        }
    }

    public double calculateAvailableTimeInPosition(int position){
        if (this.tieneNodosFijos()) {
            int lastFija = 0;
            int firstFija = Integer.MAX_VALUE;
            ArrayList<Integer> auxPosicionFijas = new ArrayList<>();
            for (int i = 0; i < this.getNodes().size(); i ++){
                if (this.getNodes().get(i).getEsFijo()) {
                    if (i > lastFija) {
                        lastFija = i;
                        auxPosicionFijas.add(i);
                    }
                    if (i < firstFija) {
                        firstFija = i;
                    }
                }
            }
            if (position > lastFija){
                return Double.POSITIVE_INFINITY;
            }else if (position == lastFija){
                if (position == 0) {
                    return routeStopSequence.get(0).getArrivingTime() - crew.getAvailableTime();
                } else {
                    return routeStopSequence.get(position).getArrivingTime() -
                            routeStopSequence.get(position - 1).getDepartingTime();
                }
            } else if (position <= firstFija){
                double tiempoYaUtilizado = 0;
                for (int i = 0; i < firstFija; i++){
                    if (position != i) {
                        if (i == 0) {
                            if (position != i + 1) {
                                tiempoYaUtilizado += crew.calculateTimeBetweenNodes(crew.getAvailableNode(), nodes.get(i)) +
                                        nodes.get(i).getTaskDuration() +
                                        crew.calculateTimeBetweenNodes(nodes.get(i), nodes.get(i + 1));
                            } else {
                                tiempoYaUtilizado += crew.calculateTimeBetweenNodes(crew.getAvailableNode(), nodes.get(i)) +
                                        nodes.get(i).getTaskDuration();
                            }
                        } else {
                            if (position != i + 1){
                                tiempoYaUtilizado += nodes.get(i).getTaskDuration() +
                                crew.calculateTimeBetweenNodes(nodes.get(i), nodes.get(i + 1));
                            } else {
                                tiempoYaUtilizado += nodes.get(i).getTaskDuration();
                            }
                        }
                    }
                }
                double tiempoTotal = routeStopSequence.get(firstFija).getArrivingTime() - crew.getAvailableTime();
                return tiempoTotal-tiempoYaUtilizado;
            //si o si se encuentra entre 2 fijas
            } else {
                int fijaInferior = 0;
                int fijaSuperior = Integer.MAX_VALUE;
                for (int h: auxPosicionFijas){
                    if(h <= position){
                        fijaInferior = h;
                    } else if(h < fijaSuperior){
                        fijaSuperior = h;
                    }
                }
                double tiempoYaUtilizado = 0;
                for (int j = fijaInferior + 1 ; j < fijaSuperior; j ++){
                    if (j != position){
                        if (j != position + 1){
                            if (j == fijaInferior + 1){
                                tiempoYaUtilizado += crew.calculateTimeBetweenNodes(nodes.get(fijaInferior),
                                        nodes.get(j)) + nodes.get(j).getTaskDuration() +
                                        crew.calculateTimeBetweenNodes(nodes.get(j), nodes.get(j + 1));
                            } else {
                                tiempoYaUtilizado += nodes.get(j).getTaskDuration() +
                                        crew.calculateTimeBetweenNodes(nodes.get(j), nodes.get(j +1));
                            }
                        } else {
                            if (j == fijaInferior + 1){
                                tiempoYaUtilizado += crew.calculateTimeBetweenNodes(nodes.get(fijaInferior),
                                        nodes.get(j)) + nodes.get(j).getTaskDuration();
                            } else {
                                tiempoYaUtilizado += nodes.get(j).getTaskDuration();
                            }
                        }
                    }
                }
                double tiempoTotal = routeStopSequence.get(fijaSuperior).getArrivingTime()  -
                        routeStopSequence.get(fijaInferior).getDepartingTime();
                return tiempoTotal - tiempoYaUtilizado;
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    public double totalTimeNeededForNodeInRouteByPosition(Node node, int position){
        refreshRouteStopSequence();
        if (position < nodes.size()) {
            if (position == 0) {
                return crew.calculateTimeBetweenNodes(primerNode, node) +
                        node.getTaskDuration() +
                        crew.calculateTimeBetweenNodes(node, nodes.get(position));
            } else {
                return crew.calculateTimeBetweenNodes(nodes.get(position - 1), node) +
                        node.getTaskDuration() +
                        crew.calculateTimeBetweenNodes(node, nodes.get(position));
            }
        } else {
            if (nodes.size() == 0) {
                return crew.calculateTimeBetweenNodes(primerNode, node) + node.getTaskDuration();
            } else {
                return crew.calculateTimeBetweenNodes(nodes.get(nodes.size() - 1), node) + node.getTaskDuration();
            }
        }
    }

    public boolean tieneNodosFijos(){
        return mapaNodosFijos.containsKey(this.crew);
    }

    public void insert_node_to_route(Node newNode,int index) {
        this.nodes.add(index, newNode);
        this.refreshRouteStopSequence();
    }

    public void delete_node_from_route(Node existingNode) {
        if (this.nodes.contains(existingNode)) {
            this.nodes.remove(existingNode);
        }
        this.refreshRouteStopSequence();
    }

    public void refreshRouteStopSequence() {
        double acum_time = this.startTime;
        this.routeStopSequence = new ArrayList<>();
        HashMap<Node, RouteStop> dicAuxiliarDeFijas = new HashMap<>();
        if(mapaNodosFijos.containsKey(crew)) {
            for (RouteStop routeStop : mapaNodosFijos.get(crew)) {
                dicAuxiliarDeFijas.put(routeStop.getNode(), routeStop);
            }
        }
        for (int i = 0; i < this.nodes.size(); i++) {
            Node node = this.nodes.get(i);
            RouteStop routeStopInfo;
            if (!dicAuxiliarDeFijas.containsKey(node)) {
                if (i == 0) {
                    acum_time += this.getCrew().calculateTimeBetweenNodes(node, this.primerNode);
                } else {
                    acum_time += this.getCrew().calculateTimeBetweenNodes(node, this.nodes.get(i - 1));
                }
                routeStopInfo = new RouteStop(node, acum_time);
            } else {
                routeStopInfo = dicAuxiliarDeFijas.get(node);
            }
            this.routeStopSequence.add(routeStopInfo);
            acum_time = routeStopInfo.getDepartingTime();
        }
    }

    public Route copy(){
        ArrayList<Node> nodeslist = new ArrayList<>();
        nodeslist.addAll(this.nodes);
        return new Route(this.crew, this.startTime, nodeslist);
    }

    //-----------------------TOSTRING-----------------------
    public String toString(){
        String aux = " Crew:" + this.crew.getId() + ", NI: " + this.primerNode.getId() + ": \n";
        for(RouteStop r: this.routeStopSequence){
            aux+= "   " + r.toString() + " \n";
        }
        return aux;
    }

    //-----------------------EQUALS-----------------------
    public boolean equals(Object obj){
        if (obj instanceof Route) {
            Route otherRoute = (Route) obj;
            if(this.getRouteStopSequence().size() == otherRoute.getRouteStopSequence().size()){
                for (int i = 0; i < this.getRouteStopSequence().size();i++){
                    if( !(this.getRouteStopSequence().get(i).equals(otherRoute.getRouteStopSequence().get(i))) ){
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}
