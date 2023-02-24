import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Solution {

    private ArrayList<Route> routes;
    private Map<Crew, Route> routesByCrew;
    private double loss;
    private String heuristic;
    private double runningTime;
    private Map<String, Integer> diccLogicaYProfundidad;
    private int id;
    private int idGrasp;

    //-----------------------CONSTRUCTOR-----------------------
    public Solution(ArrayList<Route> routes, Map<Crew, Route> routes_by_crew) {
        this.routes = routes;
        this.routesByCrew = routes_by_crew;
        this.diccLogicaYProfundidad = new HashMap<String, Integer>();
        calculateLoss();
    }
    public Solution(){
        this.routes = null;
        this.routesByCrew =null;
        this.diccLogicaYProfundidad = new HashMap<String, Integer>();
    }

    //-----------------------GET-----------------------
    public int getId(){
        return this.id;
    }

    public Map<Crew, Route> getRoutesByCrew(){
        return this.routesByCrew;
    }

    public ArrayList<Route> getRoutes() {
        return this.routes;
    }

    public double getLoss(){
        return this.loss;
    }

    public String getHeuristic(){ return this.heuristic; }

    public double getRunningTime(){ return this.runningTime; }

    public double getTimeTraveling(){
            double auxTimeTraveled = 0;
            for(Route route: routesByCrew.values()){
                double auxTaskDuration = 0;
                for(RouteStop routeStop: route.getRouteStopSequence()){
                    auxTaskDuration += routeStop.getNode().getTaskDuration();
                }

                auxTimeTraveled += route.getRouteStopSequence().get(route.getRouteStopSequence().size()-1).getDepartingTime();
                auxTimeTraveled -= auxTaskDuration;
            }
            return auxTimeTraveled;

    }

    public double getSolutionTotalDuration(){
        double auxLastDuration = 0;
        for(Route route: routesByCrew.values()){
            if(route.getRouteStopSequence().get(route.getRouteStopSequence().size()-1).getDepartingTime() > auxLastDuration){
                auxLastDuration = route.getRouteStopSequence().get(route.getRouteStopSequence().size()-1).getDepartingTime();
            }
        }
        return auxLastDuration;
    }

    public TuplaNodeCrew getNodeByLargestLoss(Route route){
        if(route != null){
            double auxLoss = 0;
            Node node = null;
            for(RouteStop routeStop: route.getRouteStopSequence()){
                if(routeStop.getLoss() > auxLoss){
                    node = routeStop.getNode();
                    auxLoss = routeStop.getLoss();
                }
            }
            return new TuplaNodeCrew(node, route.getCrew());
        } else {
            Node node = null;
            Route routeRet = null;
            double auxLoss = 0;
            for(Route routeAux: this.getRoutes()){
                for(RouteStop routeStop: routeAux.getRouteStopSequence()){
                    if(routeStop.getLoss() > auxLoss){
                        node = routeStop.getNode();
                        auxLoss = routeStop.getLoss();
                        routeRet = routeAux;
                    }
                }
            }
            return new TuplaNodeCrew(node, routeRet.getCrew());
        }
    }

    //FIX ME
    // esto metodo tiene getdistancetoothernode
    public TuplaNodeCrew getNodeByLargestTravelTime(Route route){
        if (route != null) {
            Node nodo = route.getRouteStopSequence().get(0).getNode();
            double maxTravelTime = route.getCrew().calculateTimeBetweenNodes(nodo, route.getCrew().getAvailableNode());
            for (int i = 0; i < route.getRouteStopSequence().size(); i++) {
                if (route.getRouteStopSequence().size() > i + 1) {
                    Node node1 = route.getRouteStopSequence().get(i).getNode();
                    Node node2 = route.getRouteStopSequence().get(i + 1).getNode();
                    if (route.getCrew().calculateTimeBetweenNodes(node1, node2) > maxTravelTime) {
                        maxTravelTime = route.getCrew().calculateTimeBetweenNodes(node1, node2);
                        nodo = node2;
                    }
                }
            }
            return new TuplaNodeCrew(nodo, route.getCrew());
        } else {
            Crew crew = null;
            Node nodo = null;
            double maxTravelTime = 0;
            for (Route route1: this.routes) {
                for (int i = 0; i < route1.getRouteStopSequence().size(); i++) {
                    if (route1.getRouteStopSequence().size() > i + 1) {
                        Node node1 = route1.getRouteStopSequence().get(i).getNode();
                        Node node2 = route1.getRouteStopSequence().get(i + 1).getNode();
                        if (route1.getCrew().calculateTimeBetweenNodes(node1, node2) > maxTravelTime) {
                            maxTravelTime = route1.getCrew().calculateTimeBetweenNodes(node1, node2);
                            nodo = node2;
                            crew = route1.getCrew();
                        }
                    }
                }
            }
            return new TuplaNodeCrew(nodo, crew);
        }
    }

    public Map<String, Integer> getDiccLogicaYProfundidad() {
        return diccLogicaYProfundidad;
    }

    public TuplaNodeCrew getNodeBySmallestOutputOverTaskDuration(Route route) {
        if (route != null) {
            Node auxNode = route.getRouteStopSequence().get(0).getNode();
            double auxSmallestOutputOverTaskDuration = auxNode.getOutput() / auxNode.getTaskDuration();
            for (Node node : route.getNodes()) {
                if (node.getOutputOverTaskDuration() < auxNode.getOutputOverTaskDuration()) {
                    auxNode = node;
                    auxSmallestOutputOverTaskDuration = node.getOutputOverTaskDuration();

                }
            }
            return new TuplaNodeCrew(auxNode, route.getCrew());
        } else {
            return new TuplaNodeCrew(route.getRouteStopSequence().get(route.getRouteStopSequence().size()-1).getNode(), route.getCrew());
        }
    }

    public TuplaNodeCrew getLastNodeInRoute(Route route){
        if(route != null){
            Node auxNode = route.getRouteStopSequence().get(route.getRouteStopSequence().size()-1).getNode();
            return new TuplaNodeCrew(auxNode, route.getCrew());
        } else {
            return null;
        }
    }

    public int getIdGrasp() {
        return idGrasp;
    }

    //-----------------------SET-----------------------
    public void setRoutesByCrew(Map<Crew, Route> routesByCrew){
        this.routesByCrew = routesByCrew;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public void setHeuristic(String heuristic){
        this.heuristic = heuristic;
    }

    public void setRunningTime(double runningTime){
        this.runningTime = runningTime;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setDiccLogicaYProfundidad(Map<String, Integer> diccLogicaYProfundidad) {
        this.diccLogicaYProfundidad = diccLogicaYProfundidad;
    }

    public void setIdGrasp(int idGrasp) {
        this.idGrasp = idGrasp;
    }

    //-----------------------CLONE-----------------------
    public Solution copy(){
        Solution newSolution = new Solution();

        newSolution.setId(this.getId());
        newSolution.setHeuristic(this.getHeuristic());
        newSolution.setRunningTime(this.getRunningTime());

        ArrayList<Route> newListOfRoutes = new ArrayList<>();
        for (Route route: this.getRoutes()){
            newListOfRoutes.add(route.copy());
        }
        newSolution.setRoutes(newListOfRoutes);

        Map<Crew, Route> newMapRoutesByCrew = new HashMap<>();
        for (Route route: newListOfRoutes){
            newMapRoutesByCrew.put(route.getCrew(), route);
        }
        newSolution.setRoutesByCrew(newMapRoutesByCrew);

        Map<String, Integer> newDicLogicaYProfundidad = new HashMap<>();
        for (Map.Entry<String, Integer> entry: this.getDiccLogicaYProfundidad().entrySet()){
            newDicLogicaYProfundidad.put(entry.getKey(), entry.getValue());
        }
        newSolution.setDiccLogicaYProfundidad(newDicLogicaYProfundidad);

        newSolution.calculateLoss();

        return newSolution;
    }

    //-----------------------OTHER-----------------------
    public void calculateLoss() {
        double auxLoss = 0;
        for (Route route : this.routes) {
            double previousTime = route.getCrew().getAvailableTime();
            for (int j = 0; j < route.getRouteStopSequence().size(); j++) {
                RouteStop routeStop = route.getRouteStopSequence().get(j);
                Node nodo = routeStop.getNode();
                if (nodo.getInitialLoss()) {
                    auxLoss += routeStop.getDepartingTime() * routeStop.getNode().getOutput();
                } else {
                    auxLoss += nodo.getTaskDuration() * routeStop.getNode().getOutput();
                }
                if (previousTime > routeStop.getArrivingTime()){
                    double numaux = (previousTime - routeStop.getArrivingTime()) *
                            routeStop.getNode().getOutput() * 10;
                    auxLoss += numaux;
                }
                previousTime = routeStop.getDepartingTime();
            }
        }
        this.loss = auxLoss;
    }

    public void calculateLossSevenDays(){
        double auxLoss = 0;
        for (Route route : this.routes) {
            for (int j = 0; j < route.getRouteStopSequence().size(); j++) {
                RouteStop routeStop = route.getRouteStopSequence().get(j);
                Node nodo = routeStop.getNode();
                if(routeStop.getArrivingTime() <= 168){
                    if(routeStop.getDepartingTime() >= 168){
                        if (nodo.getInitialLoss()) {
                            auxLoss += 168 * routeStop.getNode().getOutput();
                            //System.out.println("1:" + 168 * routeStop.getNode().getOutput());
                        } else {
                            auxLoss += (168 - route.getRouteStopSequence().get(j).getArrivingTime()) * routeStop.getNode().getOutput();
                            //System.out.println("2:" + (168 - route.getRouteStopSequence().get(j).getArrivingTime()) * routeStop.getNode().getOutput());
                        }
                    } else {
                        if (nodo.getInitialLoss()) {
                            auxLoss += routeStop.getDepartingTime() * routeStop.getNode().getOutput();
                            //System.out.println("3:" + routeStop.getDepartingTime() * routeStop.getNode().getOutput());
                        } else {
                            auxLoss += nodo.getTaskDuration() * routeStop.getNode().getOutput();
                            //System.out.println("4:" + nodo.getTaskDuration() * routeStop.getNode().getOutput());
                        }
                    }
                } else {
                    if(nodo.getInitialLoss()){
                        auxLoss += 168 * routeStop.getNode().getOutput();
                    }
                }
            }
        }
        this.loss = auxLoss;
    }

    public boolean entraEnOptimizacionDeTool(){
        for (Crew crew: Route.getMapaNodosFijos().keySet()){
            Route route = getRoutesByCrew().get(crew);
            if (route.getRouteStopSequence().size() > 1){
                for (int i = 0; i < route.getRouteStopSequence().size(); i ++){
                    if (route.getRouteStopSequence().size() > i + 1){
                        RouteStop routeStop1 = route.getRouteStopSequence().get(i);
                        RouteStop routeStop2 = route.getRouteStopSequence().get(i + 1);
                        if (Route.getMapaNodosFijos().get(crew).contains(routeStop2) &&
                            routeStop1.getDepartingTime() > routeStop2.getArrivingTime()){
                            return false;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return true;
    }

    public void visualisacionSimple(){
        for (Route route: this.routes){
            System.out.print(route.getCrew() + ": ");
            for (int i = 0; i < route.getNodes().size(); i++){
                String desc = String.valueOf(route.getNodes().get(i).getId());
                if (i!=route.getNodes().size()-1){
                    System.out.print(desc.split("-")[desc.split("-").length - 1] + "-->");
                } else{
                    System.out.print(desc.split("-")[desc.split("-").length - 1]);
                }
            }
            System.out.print("\n");
        }
        for (Route route: this.routes){
            System.out.print(route.getCrew() + ": ");
            for (int i = 0; i < route.getNodes().size(); i++){
                String desc = String.valueOf(route.getNodes().get(i).getDesc());
                if (i!=route.getNodes().size()-1){
                    System.out.print(desc.split("-")[desc.split("-").length - 1] + "-->");
                } else{
                    System.out.print(desc.split("-")[desc.split("-").length - 1]);
                }
            }
            System.out.print("\n");
        }
        for (Route route: this.routes){
            System.out.print(route.getCrew() + ": ");
            for (int i = 0; i < route.getNodes().size(); i++){
                String desc = String.valueOf(route.getNodes().get(i).getOrdenID());
                if (i!=route.getNodes().size()-1){
                    System.out.print(desc.split("-")[desc.split("-").length - 1] + "-->");
                } else{
                    System.out.print(desc.split("-")[desc.split("-").length - 1]);
                }
            }
            System.out.print("\n");
        }
    }

    public static void solutionafinity(Solution s1, Solution s2){
        int puntaje = 0;
        int maximoPuntaje = 0;
        int puntajeContains = 0;
        int maximoPuntajeContains = 0;
        for (Route route: s1.getRoutes()){
            ArrayList<Node> listNodesS2 = s2.getRoutesByCrew().get(route.getCrew()).getNodes();
            for (Node node: route.getNodes()){
                if (listNodesS2.contains(node)){
                    puntaje ++;
                    puntajeContains ++;
                }
                maximoPuntaje ++;
                maximoPuntajeContains ++;
                if (listNodesS2.indexOf(node) == route.getNodes().indexOf(node)){
                    puntaje ++;
                }
                maximoPuntaje ++;

            }
        }
        double coefContains = (double) puntajeContains/maximoPuntajeContains;
        double coefTotal = (double) puntaje/maximoPuntaje;
        System.out.println("PuntajeContains: " + coefContains);
        System.out.println("PuntajeTotal: " + coefTotal);
    }

    public void recalculateRouteForNewDuration(){
        for(Route route: routesByCrew.values()) {
            route.refreshRouteStopSequence();
        }
    }

    //-----------------------TOSTRING-----------------------
    public String toString(){
        return "Solution. Loss: " + this.loss;
    }

    //-----------------------EQUALS-----------------------
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Solution) {
            Solution otherSolution = (Solution) obj;
            for (Crew crew : this.routesByCrew.keySet()) {
                if (otherSolution.routesByCrew.containsKey(crew)) {
                    Route routeOfCrew1 = this.routesByCrew.get(crew);
                    Route routeOfCrew2 = otherSolution.routesByCrew.get(crew);
                    if (!routeOfCrew1.equals(routeOfCrew2)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    } //Cierre del método equals

    @Override
    public int hashCode() {
        return Objects.hash(loss);
    }
}
