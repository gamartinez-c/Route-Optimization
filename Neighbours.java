import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Neighbours {

    private ArrayList<Route> routes;
    private Map<Crew, Route> routesByCrew;
    private static double prob;
    private static double nVecesMayor;

    //GETTER
    public static double getProb() {
        return prob;
    }
    public static double getnVecesMayor() {
        return nVecesMayor;
    }

    //SETTER
    public static void setProb(double prob) {
        Neighbours.prob = prob;
    }
    public static void setnVecesMayor(double nVecesMayor) {
        Neighbours.nVecesMayor = nVecesMayor;
    }

    public static ArrayList<Solution> generatesNeighbours(int rand, HashMap<String,
            ArrayList<Solution>> initialSolutionsFiltered, double timeMax,
                                                          int maxiterations, int numbreOfBestSolutions){
        Random r = new Random(rand);
        ArrayList<String> auxLogics = new ArrayList<>();
        ArrayList<Solution> solutionsAExport = new ArrayList<>();
        Solution bestSolution = null;
        double timePerNeighbour = timeMax;
        for(ArrayList<Solution> aLSolution: initialSolutionsFiltered.values()){
            for(Solution solution: aLSolution){
                auxLogics.add("longestTravel");
                auxLogics.add("largestLost");
                auxLogics.add("nodeSwapInRoute");
                auxLogics.add("lastNode");
                timePerNeighbour = (timeMax*1000000)/(auxLogics.size()*aLSolution.size());

                while(auxLogics.size() > 0){
                    int posRandom = (int) (r.nextDouble() * auxLogics.size());
                    String logicSel = auxLogics.get(posRandom);
                    Solution solVec = null;
                    switch (logicSel) {
                        case "longestTravel":
                            solVec = Neighbours.tryReorderNodeByCriteria(numbreOfBestSolutions, maxiterations,
                                    maxiterations, timePerNeighbour, solution, "longestTravel");
                            break;
                        case "largestLost":
                            solVec = Neighbours.tryReorderNodeByCriteria(numbreOfBestSolutions, maxiterations,
                                    maxiterations, timePerNeighbour, solution,"largestLost");
                            break;
                        case "nodeSwapInRoute":
                            solVec = Neighbours.nodeSwapInRouteIterations(numbreOfBestSolutions, maxiterations,
                                    maxiterations, timePerNeighbour, solution);
                            break;
                        case "lastNode":
                            solVec = Neighbours.tryReorderNodeByCriteria(numbreOfBestSolutions, maxiterations,
                                    maxiterations, timePerNeighbour, solution,"lastNode");
                            break;
                    }
                    if(solVec.getLoss() < solution.getLoss()){
                        solution = solVec;
                    } else if (solVec.getLoss() < nVecesMayor*solution.getLoss() && (r.nextDouble() <= prob)){
                        solution = solVec;
                    }
                    auxLogics.remove(posRandom);
                }
                solutionsAExport.add(solution);
                if(bestSolution == null || solution.getLoss() < bestSolution.getLoss()){
                    bestSolution = solution;
                }
            }
        }
        return solutionsAExport;
    }

    /***** LOGICAS VECINDADES *****/
    /* toma dos el nodo del medio y su continuo y swapea*/
    public static Solution nodeSwapInRouteIterations(int n, int iterations, int maxIterations,
                                                     double timeOut, Solution solution){

        if (iterations != 0) {
            long timeSpent;
            long startTime = System.nanoTime();
            ArrayList<Solution> posibleSolutions = new ArrayList<>();
            Solution solAux = solution.copy();// reescribir metodo clone
            int contador = 0;
            for (Route route : solAux.getRoutesByCrew().values()) {
                if (route.getRouteStopSequence().size() > 1) {
                    int auxSize = route.getRouteStopSequence().size();
                    int auxSizeMayor = Math.round(auxSize / 2);
                    int auxSizeMenor = Math.round((auxSize / 2) - 1);
                    //intentar para la derecha y para la izquierda (aca solo hacemos ala izquierda=
                    moveNodeOfCrew(solAux, route.getCrew(), route.getCrew(),
                            auxSizeMenor, auxSizeMayor, true);
                    solAux.calculateLoss();
                    if (solAux.getLoss() < solution.getLoss()) {
                        posibleSolutions.add(solAux);
                        solAux = solution.copy();
                        if (posibleSolutions.size() == n) {
                            Main.sortSolutionList(posibleSolutions);
                            timeSpent = System.nanoTime() - startTime;
                            return nodeSwapInRouteIterations(n, iterations - 1, maxIterations,
                                    timeOut - timeSpent, posibleSolutions.get(0));
                        }
                    } else {
                        moveNodeOfCrew(solAux, route.getCrew(), route.getCrew(),
                                auxSizeMenor, auxSizeMayor, true);
                        if (contador == (solAux.getRoutesByCrew().size() - 1) && posibleSolutions.size() != 0){
                            Main.sortSolutionList(posibleSolutions);
                            timeSpent = System.nanoTime() - startTime;
                            return nodeSwapInRouteIterations(n, iterations - 1, maxIterations,
                                    timeOut - timeSpent, posibleSolutions.get(0));
                        }
                    }
                }
                contador ++;
            }
            int numIterations = maxIterations - iterations;
            //System.out.println("OPTIMO LOCAL + iteration " + numIterations);
        }
        solution.getDiccLogicaYProfundidad().put("nodeSwapInRoute", maxIterations-iterations);
        return solution;
    }
    /*recorre todas las rutas,
     agarra el nodo con mayor perdida de cada ruta y
     intenta posicionarlo en cualquier punto de todas las rutas*/
    public static Solution tryReorderNodeByCriteria(int n, int iterations, int maxIterations,
                                                    double timeOut, Solution solution, String criteria){
        if (iterations != 0 && timeOut > 0) {
            long timeSpent;
            long startTime = System.nanoTime();
            ArrayList<Solution> posibleSolutions = new ArrayList<>();
            Solution auxSol = solution.copy();
            int contador = 0;
            // Me traigo el nodo de mayor perdida de cada ruta y lo muevo a todos las posiciones posibles.
            for (Route route : solution.getRoutesByCrew().values()) {
                TuplaNodeCrew tupla;
                switch (criteria) {
                    case "largestLost":
                        tupla = solution.getNodeByLargestLoss(route);
                        break;
                    case "longestTravel":
                        tupla = solution.getNodeByLargestTravelTime(route);
                        break;
                    case "lastNode":
                        tupla = solution.getLastNodeInRoute(route);
                        break;
                    default:
                        System.out.println("El criterio ingresado en tryReorderNodeByCriteria es incorrecto.");
                        tupla = solution.getNodeByLargestTravelTime(route);
                }
                for (Route route1 : solution.getRoutesByCrew().values()) {
                    auxSol = moveNodeInRoute(solution, route.getCrew(), route1.getCrew(), tupla.getNode());
                    if (auxSol.getLoss() < solution.getLoss()) {
                        //auxBestSol = auxSol.copy();
                        posibleSolutions.add(auxSol.copy());
                        if (posibleSolutions.size() == n) {
                            Main.sortSolutionList(posibleSolutions);
                            timeSpent = System.nanoTime() - startTime;
                            return tryReorderNodeByCriteria(n, iterations - 1, maxIterations,
                                    timeOut - timeSpent, posibleSolutions.get(0), criteria);
                        }
                    } else {
                        if (contador == (solution.getRoutesByCrew().size() - 1) && posibleSolutions.size() != 0){
                            Main.sortSolutionList(posibleSolutions);
                            timeSpent = System.nanoTime() - startTime;
                            return tryReorderNodeByCriteria(n, iterations - 1, maxIterations,
                                    timeOut - timeSpent, posibleSolutions.get(0), criteria);
                        }
                    }
                }
                contador ++;
            }
            int numIterations = maxIterations - iterations;
            //System.out.println("OPTIMO LOCAL + iteration " + numIterations);
        }

        solution.getDiccLogicaYProfundidad().put(criteria, maxIterations-iterations);
        return solution;
        //return auxBestSol.copy();
    }

    /********** FUNCIONES AUXILIARES *************/
    public static void crewSwap(Solution solution, Crew crew1, Crew crew2){
        for(Node nodo : solution.getRoutesByCrew().get(crew1).getNodes()) {
            if (!nodo.getCompatibleCrews().contains(crew2)) {
                System.out.println("La permutacion no es valida, el nodo no tiene disponible la cuadrilla");
                return;
            }
        }
        for(Node nodo : solution.getRoutesByCrew().get(crew2).getNodes()){
            if(!nodo.getCompatibleCrews().contains(crew1)){
                System.out.println("La permutacion no es valida, el nodo no tiene disponible la cuadrilla");
                return;
            }
        }

        //Swapeo las rutas de las cuadrillas
        Route auxRuta = solution.getRoutesByCrew().get(crew1);
        solution.getRoutesByCrew().put(crew1, solution.getRoutesByCrew().get(crew2));
        solution.getRoutesByCrew().put(crew2, auxRuta);

        //ruta 1 tiene datos de la crew2 y route2 tiene los datos de crew1
        Route route1 = solution.getRoutesByCrew().get(crew1);
        Route route2 = solution.getRoutesByCrew().get(crew2);

        Node initialNode1 = crew1.getAvailableNode();
        Node initialNode2 = crew2.getAvailableNode();

        //Seteo crew1
        route1.setCrew(crew1);
        route1.refreshRouteStopSequence();

        //Seteo crew2
        route2.setCrew(crew2);
        route2.refreshRouteStopSequence();

    }
    public static void  moveNodeOfCrew(Solution solution, Crew crewOrigin, Crew crewDestiny,
                                       int posIni, int posFin, boolean isbilateral) {
        // Mando el pozo posIni de crewOrigin a posFin en creDestiny
        Route routeOrigin = solution.getRoutesByCrew().get(crewOrigin);
        Route routeDestiny = solution.getRoutesByCrew().get(crewDestiny);

        //Testeos
        if (!(routeOrigin.getNodes().size() > posIni)) {
            return;
        }
        if (!(routeDestiny.getNodes().size() + 1 > posFin)) {
            return;
        }
        if (!routeOrigin.getNodes().get(posIni).getCompatibleCrews().contains(crewDestiny)) {
            return;
        }
        //descarto el movimiento de ser fijas
        if (routeOrigin.getNodes().get(posIni).getEsFijo() ||
                (isbilateral && routeDestiny.getNodes().get(posFin).getEsFijo())) {
            return;
        }
        //descarto el movimiento de implicar a una continuidad de programa, ya que tiene que ir primeras siempre.
        if (routeOrigin.getNodes().get(posIni).getEsContinuidadDePrograma() ||
                routeDestiny.getNodes().get(posFin).getEsContinuidadDePrograma()){
            return;
        }

        Node nodoPosIni = routeOrigin.getNodes().get(posIni);
        Node nodoPosFin = routeDestiny.getNodes().get(posFin);

        double availableTime1 = routeDestiny.calculateAvailableTimeInPosition(posFin);
        double tiempoTotal1 = routeDestiny.totalTimeNeededForNodeInRouteByPosition(nodoPosIni, posFin);

        double availableTime2 = routeOrigin.calculateAvailableTimeInPosition(posIni);
        double tiempoTotal2 = routeOrigin.totalTimeNeededForNodeInRouteByPosition(nodoPosFin, posIni);

        ArrayList<Integer> listaPosFijas1 = new ArrayList<>();
        if (routeDestiny.tieneNodosFijos()) {
            for (int i = 0; i < routeDestiny.getNodes().size(); i++) {
                Node node = routeDestiny.getNodes().get(i);
                if (node.getEsFijo()) {
                    listaPosFijas1.add(i);
                }
            }
        }
        ArrayList<Integer> listaPosFijas2 = new ArrayList<>();
        if (isbilateral) {
            if (routeOrigin.tieneNodosFijos()) {
                for (int i = 0; i < routeOrigin.getNodes().size(); i++) {
                    Node node = routeOrigin.getNodes().get(i);
                    if (node.getEsFijo()) {
                        listaPosFijas2.add(i);
                    }
                }
            }
        }

        //si no tengo timpo suficiente y encima mi movimiento esta pegado a un nodo fijo, descarto
        //el movimiento.
        if (routeDestiny.tieneNodosFijos() && availableTime1 < tiempoTotal1) {
            if (isbilateral && listaPosFijas1.contains(posFin + 1)) {
                return;
            } else if (!isbilateral && listaPosFijas1.contains(posFin)){
                return;
            }
        }
        if (isbilateral && routeOrigin.tieneNodosFijos() &&
                availableTime2 < tiempoTotal2 && listaPosFijas2.contains(posIni + 1)) {
                return;
        }

        //Antes de ralizar el primer cambio me debo preguntar si es posible esto.
        //3 Casos
        //Caso1: la posFinal esta a la derecha de las fijas, no hay inconvenientes en su movimiento
        //Caso2: la posFinal se encuentra justo al la izquierda de una fija y encima no tengo timpo
        //Caso3: Sen encuentra a la derecha y con tareas no fijas entre si misma y la proxima fija
        if (routeDestiny.tieneNodosFijos() && availableTime1 < tiempoTotal1) {
            routeDestiny.refreshRouteStopSequence();
            routeOrigin.refreshRouteStopSequence();
            //saco todos los nodos NO fijo para despues agregarlos con add_node_to_rote,
            // que considera a las fijas
            ArrayList<Node> auxListNodes1 = new ArrayList<>();
            routeOrigin.getNodes().remove(nodoPosIni);
            //hay que tener en cuenta que posIni== posFin - 1 no es posible
            // porque ya hubiese escapado en un return
            int inicioLoop1 = 0;
            if ((crewDestiny == crewOrigin) && (posIni < posFin - 1)) {
                inicioLoop1 = posFin - 1;
            } else {
                inicioLoop1 = posFin;
            }
            for (int h = inicioLoop1; h < routeDestiny.getNodes().size(); h++) {
                if (!routeDestiny.getNodes().get(h).getEsFijo()) {
                    auxListNodes1.add(routeDestiny.getNodes().get(h));
                    routeDestiny.getNodes().remove(h);
                    h--;
                }
            }
            routeDestiny.append_node_to_route(nodoPosIni);
            for (Node node : auxListNodes1) {
                routeDestiny.append_node_to_route(node);
            }
        } else {
            routeOrigin.getNodes().remove(nodoPosIni);
            routeDestiny.getNodes().add(posFin, nodoPosIni);
        }
        if (isbilateral) {
            //esAplicableCambiosPorBilateralidad descarta los movimiento que con un solo movimiento se logro
            //la bilateralidad, como puede ser el caso de operaciones simultaneas
            boolean esAplicableCambiosPorBilateralidad = ((crewDestiny == crewOrigin) && (posIni < posFin - 1)) ||
                    (posIni > posFin + 1) || (crewOrigin != crewDestiny);
            if (routeOrigin.tieneNodosFijos() && availableTime2 < tiempoTotal2) {
                ArrayList<Node> auxListNodes2 = new ArrayList<>();
                if (esAplicableCambiosPorBilateralidad) {
                    routeDestiny.getNodes().remove(nodoPosFin);
                    for (int h = posIni; h < routeOrigin.getNodes().size(); h++) {
                        if (!routeOrigin.getNodes().get(h).getEsFijo()) {
                            auxListNodes2.add(routeOrigin.getNodes().get(h));
                            routeOrigin.getNodes().remove(h);
                            h--;
                        }
                    }
                    routeOrigin.append_node_to_route(nodoPosFin);
                    for (Node node : auxListNodes2) {
                        routeOrigin.append_node_to_route(node);
                    }
                }
            } else {
                //si no tiene fijas se setea como siempre
            /*Hay 3 casos posibles:
            - situacion de 2 nodos Simultaneos o que sean el mismo.(no pasa nada)
            - situacion en la misma cuadrilla pero el nodo destino es mayor al nodo origen.
            - situacion donde son distintas cuadrillas o siendo las mismas cuadrillas se
             encuentra el nodo destino anterior al nodo destino.
            (estos casos son a considerar a la hora de modificar esta funcion)*/
                if (esAplicableCambiosPorBilateralidad) {
                    routeDestiny.getNodes().remove(nodoPosFin);
                    routeOrigin.getNodes().add(posIni, nodoPosFin);
                }
            }
        }
        routeOrigin.refreshRouteStopSequence();
        routeDestiny.refreshRouteStopSequence();
        solution.calculateLoss();
    }

    public static Solution moveNodeInRoute(Solution solution, Crew crewOrigin, Crew crewDestiny, Node node){
        Solution solutionCopy = solution.copy();
        Solution auxBestSolution = solutionCopy.copy();
        int indexOrigin = solutionCopy.getRoutesByCrew().get(crewOrigin).getNodes().indexOf(node);
        ArrayList<Node> auxNodesDestiny = solutionCopy.getRoutesByCrew().get(crewDestiny).getNodes();


        for(int i = 0; i < auxNodesDestiny.size(); i++){
            moveNodeOfCrew(solutionCopy, crewOrigin, crewDestiny, indexOrigin, i, true);

            if(solutionCopy.getLoss() < auxBestSolution.getLoss()) {
                auxBestSolution = solutionCopy.copy();
            }

            moveNodeOfCrew(solutionCopy, crewOrigin, crewDestiny, indexOrigin, i, true);
        }
        return auxBestSolution;
    }
}

