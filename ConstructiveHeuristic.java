import java.util.*;

public class ConstructiveHeuristic {

    private int random_seed;
    private ProblemData problem_data;
    private static double prob; //agrega aleatoreidad a las soluciones (1 nada aleatoreidad, valor deseado 0.9)
    private Random random;

    //-----------------------CONSTRUCTOR-----------------------
    public ConstructiveHeuristic(int random_seed, ProblemData problem_data){
        this.random_seed = random_seed;
        this.problem_data = problem_data;
        this.random = new Random(random_seed);

    }

    //GETTER
    public static double getProb() {
        return prob;
    }

    //SETTER
    public static void setProb(double prob) {
        ConstructiveHeuristic.prob = prob;
    }

    //-----------------------ARMO SOLUCIONES GENERAL-----------------------
    public HashMap<String, HashSet<Solution>> generatesSolutions(HashMap<String, Integer> hiperparams, double maxTime){
        HashMap<String, HashSet<Solution>> s = new HashMap<>();
        double initialTime = System.currentTimeMillis();
        int total = 0;
        int contadorId = 0;

        for(int value: hiperparams.values()){
            total += value;
        }
        for(String key: hiperparams.keySet()) {
            double timePerRun = hiperparams.get(key) * maxTime / total;
            int runNumber = 1;
            double currentTime = System.currentTimeMillis();
            contadorId++;
            while((currentTime - initialTime) < timePerRun && runNumber <= hiperparams.get(key)){
                Solution sol = generateSolution(key);
                sol.setId(contadorId);
                if (!s.containsKey(sol.getHeuristic())) {
                    s.put(sol.getHeuristic(), new HashSet<>());
                }
                s.get(sol.getHeuristic()).add(sol);
                contadorId++;
                runNumber++;
                currentTime = System.currentTimeMillis();
            }
            initialTime = System.currentTimeMillis();
        }

        return s;

    }

    public Solution generateSolution(String heuristica) {
        double initTime = System.currentTimeMillis();
        ArrayList<Node> nodes = this.problem_data.getNodes(); // nodos totales
        nodeHeuristic(heuristica, nodes);
        ArrayList<Route> routes = new ArrayList<Route>();
        HashMap<Crew, Route> routes_by_crew = new HashMap<Crew, Route>();

        //me ocupo de generar todas las rutas con nodos fijos
        for (Crew crew: Route.getMapaNodosFijos().keySet()) {
            ArrayList<Node> route_nodes = new ArrayList<>();
            //Internamente al hacer un new Route va a asignar las fijas, ya que esta como atributo static
            Route route = new Route(crew, crew.getAvailableTime(), route_nodes);
            routes.add(route);
            routes_by_crew.put(crew, route);
        }

        //cargo todos los nodos
        for (Node node : nodes) {
            //filtro los fijos que ya fueron cargados antes
            if (!node.getEsFijo() && !node.getEsContinuidadDePrograma()){
                // empiezo a armar mis soluciones aleatorias
                Crew crew = crewHeuristic(node, node.getCompatibleCrews(), routes_by_crew, heuristica);
                if (!routes_by_crew.containsKey(crew)) {
                    ArrayList<Node> route_nodes = new ArrayList<>();
                    //route_nodes.add(crew.getAvailableNode());
                    Route route = new Route(crew, crew.getAvailableTime(), route_nodes);
                    route.append_node_to_route(node);
                    routes.add(route);
                    routes_by_crew.put(crew, route);
                } else {
                    Route route = routes_by_crew.get(crew);
                    route.append_node_to_route(node);
                }
            }
        }

        //Prueba de testeo
        int suma = 0;
        int sumid = 0;
        for (Route route: routes){
            suma += route.getRouteStopSequence().size();
            for (Node node: route.getNodes()){
                sumid += node.getId();
            }
        }

        Solution sol = new Solution(routes, routes_by_crew);
        sol.setRunningTime(System.currentTimeMillis() - initTime);
        sol.setHeuristic(heuristica);
        return sol;
    }

    //-----------------------FUNCIONES PARA SELECCIONAR NODO O CREW-----------------------
    public void nodeHeuristic(String heuristica, ArrayList<Node> nodes){
        if (heuristica.contains("allRandom") || heuristica.contains("randomNodes")) {
            shuffleNodes(nodes);
        } else if (heuristica.contains("orderNodosByOutput")) {
            orderNodesByOutput(nodes);
        } else if (heuristica.contains("orderNodosByInitialLossAndOutput")) {
            orderNodesByInitialLossAndOutput(nodes);
        } else if (heuristica.contains("orderNodosBySizeOfCompatibleCrews")) {
            orderNodesBySizeOfCompatibleCrews(nodes);
        } else if (heuristica.contains("orderNodesByOutputAndTaskDuration")){
            orderNodesByOutputAndTaskDuration(nodes);
        }else {
            System.out.println("No se ingreso correctamente el orden a poner en los nodos");
        }
    }
    public Crew crewHeuristic(Node node,ArrayList<Crew> crews,HashMap<Crew, Route> routes_by_crew, String heuristica){
        if (heuristica.contains("allRandom") || heuristica.contains("RandomCrew")) {
            return getRandomCrews(node.getCompatibleCrews());
        } else if (heuristica.contains("NearestCrew")) {
            return getNearestCrewToNode(node, node.getCompatibleCrews(), routes_by_crew);
        } else if (heuristica.contains("FastestCrew")) {
            return getFirstArrivalCrew(node, node.getCompatibleCrews(), routes_by_crew);
        } else if (heuristica.contains("LessTotalTravelCrew")) {
            return getLessTotalTravelCrew(node, node.getCompatibleCrews(), routes_by_crew);
        } else if (heuristica.contains("CrewWithSmallerRoute")) {
            return  getCrewWithSmallerRoute(node, node.getCompatibleCrews(), routes_by_crew);
        } else {
            //FIX
            // si se iintroduce mal estaria tomando random eso hay que definirlo bien
            System.out.println("La seleccion de Crew no es correcta, se tomo random de Default. CORREGIR.");
            return getRandomCrews(node.getCompatibleCrews());
        }
    }

    private void shuffleNodes(ArrayList<Node> nodes){
        ArrayList<Node> auxNodesArray = new ArrayList<Node>();
        while(!nodes.isEmpty()){
            Node nodo = nodes.get((int) (this.random.nextDouble() * nodes.size()));
            auxNodesArray.add(nodo);
            nodes.remove(nodo);
        }
        nodes.addAll(auxNodesArray);
    }
    private void orderNodesByOutput(ArrayList<Node> nodes){
        ArrayList<Node> auxNodesOutput = new ArrayList<Node>();
        while(!nodes.isEmpty()){
            int random = (int) (this.random.nextDouble() * nodes.size());
            double max = nodes.get(random).getOutput();
            Node auxNodo = nodes.get(random);
            for (Node nodo : nodes){
                if(nodo.getOutput() > max && (this.random.nextDouble() <= prob)){
                    max = nodo.getOutput();
                    auxNodo = nodo;
                }
            }

            auxNodesOutput.add(auxNodo);
            nodes.remove(auxNodo);
        }
        nodes.addAll(auxNodesOutput);
    }
    private void orderNodesByInitialLossAndOutput(ArrayList<Node> nodes){
        ArrayList<Node> auxWithInitialLoss = new ArrayList<Node>();
        ArrayList<Node> auxWithoutInitialLoss = new ArrayList<Node>();
        for (Node node: nodes){
            if (node.getInitialLoss() && (this.random.nextDouble() <= prob)) {
                auxWithInitialLoss.add(node);
            } else {
                auxWithoutInitialLoss.add(node);
            }
        }

        orderNodesByOutput(auxWithInitialLoss);
        orderNodesByOutput(auxWithoutInitialLoss);

        nodes.clear();
        nodes.addAll(auxWithInitialLoss);
        nodes.addAll(auxWithoutInitialLoss);
    }
    private void orderNodesByOutputAndTaskDuration(ArrayList<Node> nodes){
        ArrayList<Node> auxWithInitialLoss = new ArrayList<Node>();
        ArrayList<Node> auxWithoutInitialLoss = new ArrayList<Node>();
        for (Node node: nodes){
            if (node.getInitialLoss() && (this.random.nextDouble() <= prob)) {
                auxWithInitialLoss.add(node);
            } else {
                auxWithoutInitialLoss.add(node);
            }
        }

        orderNodesByOutputOverTaskDuration(auxWithInitialLoss);
        orderNodesByOutputOverTaskDuration(auxWithoutInitialLoss);

        nodes.clear();
        nodes.addAll(auxWithInitialLoss);
        nodes.addAll(auxWithoutInitialLoss);
    }
    private void orderNodesBySizeOfCompatibleCrews(ArrayList<Node> nodes){
        ArrayList<Node> auxList = new ArrayList<Node>();
        while (!nodes.isEmpty()){
            int random = (int) (this.random.nextDouble() * nodes.size());
            Node node = nodes.get(random);
            int min = node.getCompatibleCrews().size();
            for (Node nodeIt: nodes){
                if (nodeIt.getCompatibleCrews().size() < min && (this.random.nextDouble() <= prob)) {
                    node = nodeIt;
                    min = node.getCompatibleCrews().size();
                }
            }
            auxList.add(node);
            nodes.remove(node);
        }
        nodes.addAll(auxList);
    }
    private Crew getRandomCrews(ArrayList<Crew> crews){
        return crews.get((int) (this.random.nextDouble() * crews.size()));
    }
    private Crew getNearestCrewToNode(Node node, ArrayList<Crew> listOfCrews, HashMap<Crew,Route> routes_by_crew){

        double distToNode = Double.POSITIVE_INFINITY;
        int index = (int)(this.random.nextDouble()*listOfCrews.size());

        for(Crew c: listOfCrews){
            if(!routes_by_crew.containsKey(c)){
                if( c.getAvailableNode().getDistanceToOtherNode(node) < distToNode && (this.random.nextDouble() <= prob)){
                    distToNode = c.getAvailableNode().getDistanceToOtherNode(node); //5
                    index = listOfCrews.indexOf(c); //2
                }
            }else{
                Route route = routes_by_crew.get(c);
                int sizeAux = route.getRouteStopSequence().size();
                Node nodeOrigin = route.getRouteStopSequence().get(sizeAux - 1).getNode();
                if( nodeOrigin.getDistanceToOtherNode(node) < distToNode && (this.random.nextDouble() <= prob)){
                    distToNode = nodeOrigin.getDistanceToOtherNode(node);
                    index = listOfCrews.indexOf(c);
                }
            }

        }
        return listOfCrews.get(index);
    }
    private Crew getFirstArrivalCrew(Node node, ArrayList<Crew> listOfCrews, HashMap<Crew,Route> routes_by_crew){
        double arrivalTime = (int) Double.POSITIVE_INFINITY;
        int index = (int)(this.random.nextDouble()*listOfCrews.size());
        for(Crew c: listOfCrews){
            if(!routes_by_crew.containsKey(c)){
                if( c.calculateTimeBetweenNodes(c.getAvailableNode(), node) + c.getAvailableTime() < arrivalTime && (this.random.nextDouble() <= prob) ){
                    arrivalTime = c.getAvailableTime() + c.calculateTimeBetweenNodes(c.getAvailableNode(), node);
                    index = listOfCrews.indexOf(c);
                }
            }else{
                Route route = routes_by_crew.get(c);
                int sizeAux = route.getRouteStopSequence().size();
                Node nodeOrigin = route.getRouteStopSequence().get(sizeAux - 1).getNode();
                double timeToNextNode = c.calculateTimeBetweenNodes(nodeOrigin, node);
                double departureTimeToNextNode = route.getRouteStopSequence().get(sizeAux - 1).getDepartingTime();
                if( timeToNextNode + departureTimeToNextNode < arrivalTime  && (this.random.nextDouble() <= prob)){
                    arrivalTime = timeToNextNode + departureTimeToNextNode;
                    index = listOfCrews.indexOf(c);
                }
            }

        }
        return listOfCrews.get(index);
    }
    private Crew getLessTotalTravelCrew(Node node, ArrayList<Crew> listOfCrews, HashMap<Crew,Route> routes_by_crew){
        double travelTime = (int) Double.POSITIVE_INFINITY;
        int index = (int)(this.random.nextDouble()*listOfCrews.size());
        for(Crew c: listOfCrews){
            if(!routes_by_crew.containsKey(c)){
                if(c.calculateTimeBetweenNodes(c.getAvailableNode(), node) + c.getAvailableTime() < travelTime && (this.random.nextDouble() <= prob)){
                    travelTime = c.calculateTimeBetweenNodes(c.getAvailableNode(), node) + c.getAvailableTime();
                    index = listOfCrews.indexOf(c);
                }
            }else{
                Route route = routes_by_crew.get(c);
                int sizeAux = route.getRouteStopSequence().size();
                Node nodeOrigin = route.getRouteStopSequence().get(sizeAux - 1).getNode();
                double totalTimeTraveling = route.getTimeTraveling() + c.calculateTimeBetweenNodes(nodeOrigin,node);
                if(totalTimeTraveling < travelTime && (this.random.nextDouble() <= prob)){
                    travelTime = totalTimeTraveling;
                    index = listOfCrews.indexOf(c);
                }
            }
        }
        return listOfCrews.get(index);
    }
    private Crew getCrewWithSmallerRoute(Node node, ArrayList<Crew> listOfCrews, HashMap<Crew,Route> routes_by_crew){
        Crew crewReturn = listOfCrews.get(0);
        for(Crew c: listOfCrews){
            if(!routes_by_crew.containsKey(c)) {
                crewReturn = c;
                break;
            } else {
                int auxSize = routes_by_crew.get(crewReturn).getNodes().size();
                if (routes_by_crew.get(c).getNodes().size() < auxSize) {
                    auxSize = routes_by_crew.get(c).getNodes().size();
                    crewReturn = c;
                }
            }
        }
        return crewReturn;
    }

    // Funcion auxiliar que se corred dentro de orderNodesByInitialLossAndOutput
    private void orderNodesByOutputOverTaskDuration(ArrayList<Node> nodes){
        ArrayList<Node> auxNodesOutput = new ArrayList<Node>();
        while(!nodes.isEmpty()){
            int random = (int) (this.random.nextDouble() * nodes.size());
            double max = nodes.get(random).getOutput() / nodes.get(random).getTaskDuration();
            Node auxNodo = nodes.get(random);
            for (Node nodo : nodes){
                if(nodo.getOutput() / nodo.getTaskDuration() > max && (this.random.nextDouble() <= prob)){
                    max = nodo.getOutput() / nodo.getTaskDuration();
                    auxNodo = nodo;
                } else if (nodo.getOutput() / nodo.getTaskDuration() == max){
                    if(nodo.getOutput() > auxNodo.getOutput()){
                        max = nodo.getOutput() / nodo.getTaskDuration();
                        auxNodo = nodo;
                    }
                }
            }

            auxNodesOutput.add(auxNodo);
            nodes.remove(auxNodo);
        }
        nodes.addAll(auxNodesOutput);
    }



}
