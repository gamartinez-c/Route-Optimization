import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RunConstructiveHeuristic {

    private HashMap<String, HashSet<Solution>> solutionsAsHashMapOfHashSet;
    private ArrayList<Solution> solutionsAsList;
    private HashSet<Solution> solutionsAsSet;
    private HashMap<String, ArrayList<Solution>> solutionsAsHashMapOfArrayList;
    private HashMap<String, ArrayList<Solution>> solutionsAVecindadesYaFiltradas;
    private ArrayList<Solution> solutionsAExport;
    private HashMap<String, Integer> hiperparameters;
    private ProblemData problemData;
    private double timeMaxConstructive;//tiempo en milisegundos
    private double timeMaxNeighbours;//tiempo en milisegundos
    private int cantidadDeCorridasPorHeuristica;
    private double porcentajeAFiltrar;
    double probConstructive;
    double probNeighbours;
    double nVecesMayor;
    int seed;
    static int corrida;
    int profundidadDeVecindades;
    int numeroDeBestSolutionVecindades;

    public RunConstructiveHeuristic(int cantidadDeCorridasPorHeuristica, double timeMaxConstructive,
                                    double timeMaxNeighbours, double porcentajeAFiltrar, double probConstructive,
                                    double probNeighbours, double nVecesMayor, int seed, ProblemData problemData,
                                    int profundidadDeVecindades, int numeroDeBestSolutionVecindades) {

        long initialTime = System.currentTimeMillis();
        long initialTimeDatos = initialTime;
        corrida = 0;

        this.timeMaxConstructive = timeMaxConstructive;
        this.timeMaxNeighbours = timeMaxNeighbours;
        // ************* HIPERPARAMETROS ***************
        // HashMap de heuristica y cantidad de corridas
        this.cantidadDeCorridasPorHeuristica = cantidadDeCorridasPorHeuristica;
        this.refreshHashMapOfHiperparameters();

        this.profundidadDeVecindades = profundidadDeVecindades;
        this.numeroDeBestSolutionVecindades = numeroDeBestSolutionVecindades;
        this.porcentajeAFiltrar = porcentajeAFiltrar;
        this.probConstructive = probConstructive;
        this.probNeighbours = probNeighbours;
        this.nVecesMayor = nVecesMayor;
        this.seed = seed;

        ConstructiveHeuristic.setProb(probConstructive);
        Neighbours.setnVecesMayor(nVecesMayor);
        Neighbours.setProb(probNeighbours);

        // Cargo toda la info del excel
        this.problemData = problemData;

        long endTimeDatos = System.currentTimeMillis();
        //System.out.println("Tiempo en carga de datos: " + (endTimeDatos - initialTimeDatos));

        //System.out.println("-----------------------------------------------------");

        long endTime = System.currentTimeMillis();
        //System.out.println("Finished");
        //System.out.println("Total Time(Millis): " + (endTime - initialTime));
        //System.out.println("-----------------------------------------------------");
    }

    //GET
    public HashMap<String, HashSet<Solution>> getSolutionsAsHashMapOfHashSet() {
        return solutionsAsHashMapOfHashSet;
    }
    public ArrayList<Solution> getSolutionsAsList() {
        return solutionsAsList;
    }
    public HashSet<Solution> getSolutionsAsSet() {
        return solutionsAsSet;
    }
    public HashMap<String, ArrayList<Solution>> getSolutionsAsHashMapOfArrayList() {
        return solutionsAsHashMapOfArrayList;
    }
    public HashMap<String, ArrayList<Solution>> getSolutionsAVecindadesYaFiltradas() {
        return solutionsAVecindadesYaFiltradas;
    }
    public ArrayList<Solution> getSolutionsAExport() {
        return solutionsAExport;
    }
    public HashMap<String, Integer> getHiperparameters() {
        return hiperparameters;
    }
    public ProblemData getProblemData() {
        return problemData;
    }
    public double getTimeMaxConstructive() {
        return timeMaxConstructive;
    }
    public int getCantidadDeCorridasPorHeuristica() {
        return cantidadDeCorridasPorHeuristica;
    }
    public static int getCorrida() {
        return corrida;
    }

    //SET
    public void setSolutionsAsHashMapOfHashSet(HashMap<String, HashSet<Solution>> solutionsAsHashMapOfHashSet) {
        this.solutionsAsHashMapOfHashSet = solutionsAsHashMapOfHashSet;
    }
    public void setSolutionsAsList(ArrayList<Solution> solutionsAsList) {
        this.solutionsAsList = solutionsAsList;
    }
    public void setSolutionsAsSet(HashSet<Solution> solutionsAsSet) {
        this.solutionsAsSet = solutionsAsSet;
    }
    public void setSolutionsAsHashMapOfArrayList(HashMap<String, ArrayList<Solution>> solutionsAsHashMapOfArrayList) {
        this.solutionsAsHashMapOfArrayList = solutionsAsHashMapOfArrayList;
    }
    public void setSolutionsAVecindadesYaFiltradas(HashMap<String, ArrayList<Solution>> solutionsAVecindadesYaFiltradas) {
        this.solutionsAVecindadesYaFiltradas = solutionsAVecindadesYaFiltradas;
    }
    public void setSolutionsAExport(ArrayList<Solution> solutionsAExport) {
        this.solutionsAExport = solutionsAExport;
    }
    public void setHiperparameters(HashMap<String, Integer> hiperparameters) {
        this.hiperparameters = hiperparameters;
        this.refreshHashMapOfHiperparameters();
    }
    public void setProblemData(ProblemData problemData) {
        this.problemData = problemData;
    }


    //OTHER
    public void refreshHashMapOfHiperparameters(){
        hiperparameters = new HashMap<>();

        hiperparameters.put("allRandom", cantidadDeCorridasPorHeuristica); //En realidad es "randomNodesAndRandomCrew
        hiperparameters.put("randomNodesAndNearestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("randomNodesAndFastestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("randomNodesAndLessTotalTravelCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("randomNodesAndCrewWithSmallerRoute", cantidadDeCorridasPorHeuristica);

        hiperparameters.put("orderNodosByOutputAndRandomCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosByOutputAndNearestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosByOutputAndFastestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosByOutputAndLessTotalTravelCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosByOutputAndCrewWithSmallerRoute", cantidadDeCorridasPorHeuristica);

        hiperparameters.put("orderNodosByInitialLossAndOutputAndRandomCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosByInitialLossAndOutputAndNearestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosByInitialLossAndOutputAndFastestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosByInitialLossAndOutputAndLessTotalTravelCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosByInitialLossAndOutputAndCrewWithSmallerRoute", cantidadDeCorridasPorHeuristica);

        hiperparameters.put("orderNodosBySizeOfCompatibleCrewsAndRandomCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosBySizeOfCompatibleCrewsAndNearestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosBySizeOfCompatibleCrewsAndFastestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosBySizeOfCompatibleCrewsAndLessTotalTravelCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodosBySizeOfCompatibleCrewsAndCrewWithSmallerRoute", cantidadDeCorridasPorHeuristica);

        hiperparameters.put("orderNodesByOutputAndTaskDurationAndRandomCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodesByOutputAndTaskDurationAndNearestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodesByOutputAndTaskDurationAndFastestCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodesByOutputAndTaskDurationAndLessTotalTravelCrew", cantidadDeCorridasPorHeuristica);
        hiperparameters.put("orderNodesByOutputAndTaskDurationAndCrewWithSmallerRoute", cantidadDeCorridasPorHeuristica);
    }
    public void rearangeDataStructures(){
        solutionsAsList = new ArrayList<>();
        solutionsAsSet = new HashSet<>();
        solutionsAsHashMapOfArrayList = new HashMap<>();
        for (Map.Entry<String, HashSet<Solution>> solSet : solutionsAsHashMapOfHashSet.entrySet()) {
            solutionsAsSet.addAll(solSet.getValue());
            solutionsAsHashMapOfArrayList.put(solSet.getKey(), new ArrayList<>(solSet.getValue()));
        }
        solutionsAsList.addAll(solutionsAsSet);

    }
    public void printUniqueAmountForEachSolution(){
        int max = 0;
        for (String key : solutionsAsHashMapOfArrayList.keySet()) {
            if (key.length() > max) {
                max = key.length();
            }
        }
        int total = 0;
        for (Map.Entry<String, ArrayList<Solution>> entry : solutionsAsHashMapOfArrayList.entrySet()) {
            System.out.print("    " + entry.getKey() + " cantidad unicas: ");
            for (int i = 0; i < (max - entry.getKey().length()); i++) {
                System.out.print(" ");
            }
            System.out.println(entry.getValue().size());
            total += entry.getValue().size();
        }
    }
    public void sortingSolutions(){
        //System.out.println("Sort-size: " + solutionsAsList.size());
        Main.sortSolutionList(solutionsAsList);
        for (ArrayList<Solution> solList : solutionsAsHashMapOfArrayList.values()) {
            Main.sortSolutionList(solList);
        }
    }
    public HashMap<String, ArrayList<Solution>> filterSolutions(double porcentajeAFiltrar){

        HashMap<String, ArrayList<Solution>> filterHashMap = new HashMap<>();
        for (String key: solutionsAsHashMapOfArrayList.keySet()){
            int cantidadAGuardar = (int) (solutionsAsHashMapOfArrayList.get(key).size()*porcentajeAFiltrar);
            filterHashMap.put(key,
                    new ArrayList<>(solutionsAsHashMapOfArrayList.get(key).subList(0, cantidadAGuardar)));
        }

        return filterHashMap;
    }
    public void exportSolutions(){

        System.out.println("-----------------------------------------------------");

        long initialTimeExport = System.currentTimeMillis();

        //System.out.println("Exportado a Excel");
        OutputExcel oe = new OutputExcel(solutionsAsList, solutionsAExport);

        long endTimeExport = System.currentTimeMillis();
        //System.out.println("Tiempo en export: " + (endTimeExport - initialTimeExport));
    }
    public void visualizationBestAndWorstNeighbours(){
        System.out.println("Resultados despues vecindades");
        Main.sortSolutionList(solutionsAExport);
        System.out.println(solutionsAExport.get(0));
        System.out.println(solutionsAExport.get(solutionsAExport.size() - 1));
    }
    public HashSet<Solution> getBestSolutions(int numberOfSolutions){
        Main.sortSolutionList(solutionsAExport);
        HashSet<Solution> bestSolutions = new HashSet<>();
        for(Node node: problemData.getNodes()){
            node.resetTaskDurations();
        }
        int contador = 0;
        while (bestSolutions.size() != numberOfSolutions && contador < solutionsAExport.size()) {
            Solution solAux = solutionsAExport.get(contador);
            solAux.recalculateRouteForNewDuration();
            if (solAux.entraEnOptimizacionDeTool()) {
                bestSolutions.add(solutionsAExport.get(contador));
            }
            contador ++;
        }
        return bestSolutions;
    }
    public void runGenerateSolutions(){

        //Genera Soluciones Iniciales
        long initialTimeConstructiveHeirostoc = System.currentTimeMillis();
        ConstructiveHeuristic constructive_heuristic = new ConstructiveHeuristic(seed, problemData);
        solutionsAsHashMapOfHashSet = constructive_heuristic.generatesSolutions(hiperparameters, timeMaxConstructive);


        //Genera nuevas estructuras para almacenar las soluciones
        this.rearangeDataStructures();
        long endTimeConstructiveHeuristic = System.currentTimeMillis();
        //System.out.println("Tiempo en contructive heuristic: " +
        //        (endTimeConstructiveHeuristic - initialTimeConstructiveHeirostoc));
        //System.out.println("-----------------------------------------------------");

        //Imprime la cantidad de soluciones unicas que se tiene de cada heuristica
        long initialTimeReorderingDataStructures = System.currentTimeMillis();
        //this.printUniqueAmountForEachSolution();
        long endTimeReorderingDataStructures = System.currentTimeMillis();
        //System.out.println("Tiempo en reorder data structures: " +
        //        (endTimeReorderingDataStructures - initialTimeReorderingDataStructures));

        //System.out.println("-----------------------------------------------------");

        //System.out.println("Mejor solucion inicial: " + solutionsAsList.get(0));

        //System.out.println("-----------------------------------------------------");

        //Ordeno las soluciones en los ArrayList
        long initialTimeSorting = System.currentTimeMillis();
        this.sortingSolutions();
        long endTimeSorting = System.currentTimeMillis();
        //System.out.println("Tiempo en sorting: " + (endTimeSorting - initialTimeSorting));

        //System.out.println("-----------------------------------------------------");

        //Define que soluciones van a pasar por las vecindades
        solutionsAVecindadesYaFiltradas = filterSolutions(porcentajeAFiltrar);


        long initialTimeVecindades = System.currentTimeMillis();
        //Inicio la Heuristica de Vecindades
        solutionsAExport = Neighbours.generatesNeighbours(seed, solutionsAVecindadesYaFiltradas,
                timeMaxNeighbours, profundidadDeVecindades, numeroDeBestSolutionVecindades);
        for (Solution sol: solutionsAExport){
            sol.setIdGrasp(corrida);
        }
        long endTimeVecindades = System.currentTimeMillis();
        //System.out.println("Tiempo en vecindades: " + (endTimeVecindades - initialTimeVecindades));

        //System.out.println("-----------------------------------------------------");
        corrida ++;
    }
}
