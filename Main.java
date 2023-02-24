import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.io.File;
import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) {

        int cantidadDeCorridas = 1000; //Cantidad de corridas por heuristica constructiva
        int timeMaxConstructive = 10000;  // Tiempo maximos en constructiva y vecindades (en milisegundos) (total)
        int timeMaxNeighbours = 10000;
        double porcentajeAFiltrar = 0.35;
        double probConstructive = 0.7;
        double probNeighbours = 0.25;
        double nVecesMayor = 1.05;
        int cantidadDeGraspsACorrer = 5;
        int numeroOfSolutionOfEachGrasp = 30; //Numero de mejores soluciones por grasp
        int seed = 8;//(int) (Math.random() * 100); //Semilla
        int numberOfSetToRobust = 300; //Cantidad sets de tiempos de intervencion para calcular medias para las mejores soluciones
        int profundidadDeVecindades = 8; //Este parametro modifica dentro de una vecindad, cuantos pasos puede dar antes de saltar a la proxima vecindad
        int numeroDeBestSolutionVecindades = 5; //Este parametro hace que en una vecindad mire 1 solo camino o n antes de avanzar recursivamente por esa mejora

        double mean = 28.4; //Media de el error del calculo de los TaskTime
        double std = 43.3; //Desviacion Standar de el error del calculo de los TaskTime

        double timeInitial = System.currentTimeMillis();

        RouteStop.setDesfasajeFijas(120);

        RunConstructiveHeuristic rnCE = new RunConstructiveHeuristic(cantidadDeCorridas, timeMaxConstructive,
                timeMaxNeighbours, porcentajeAFiltrar, probConstructive, probNeighbours, nVecesMayor, seed,
                new ProblemData(mean, std), profundidadDeVecindades, numeroDeBestSolutionVecindades);

        HashSet<Solution> setBestSol = new HashSet<>();

        for (int i = 0; i < cantidadDeGraspsACorrer; i++) {
            rnCE.getProblemData().adjustFixedOTs();
            //TRUE INDICA QUE NO SE TIENE EN CUENTA LA DISTRIBUCION
            rnCE.getProblemData().refreshDistributions(i == 0);
            rnCE.runGenerateSolutions();
            rnCE.getProblemData().cleanFixedOTsDuration();
            HashSet<Solution> solSet = rnCE.getBestSolutions(numeroOfSolutionOfEachGrasp);
            for (Solution sol: solSet){
                sol.visualisacionSimple();
            }
            Solution solAux = null;
            for (Solution sol: solSet){
                if (solAux == null){
                    solAux = sol;
                }
                Solution.solutionafinity(sol,solAux);
                solAux = sol;
            }
            setBestSol.addAll(solSet);
        }


        //System.out.println("Tool LEVANTADO EXCEL");
        //System.out.println(solutionFunctionExcel(rnCE.getProblemData()));
        
        ArrayList<Solution> solutionsToGetRobust = new ArrayList<>(setBestSol);
        //Calculo las medias para cada solucion de solutionsToGetRobust con distintas distribuciones de prob de tiempo.
        HashMap<String, Double> solutions2 = RecalculateMean.meanForSolution(rnCE, solutionsToGetRobust, numberOfSetToRobust);
        //System.out.println("------------------SOLUTIONS2---------------------");
        //System.out.println(solutions2);

        Solution bestSol = RecalculateMean.getBestSolution(solutions2, solutionsToGetRobust);
        //Faltaria recalcular la solucion para los tiempos sin distribucion...
        //System.out.println("------------------bestSol---------------------");
       //System.out.println(bestSol);
        
        //Recalculo media para esta nueva sol y para la sol de Tool.
        /*Solution solTool = solutionFunctionExcel(rnCE.getProblemData());
        solutionsToGetRobust.clear();
        solutionsToGetRobust.add(bestSol);
        solutionsToGetRobust.add(solTool);
        HashMap<String, Double> solutions3 = RecalculateMean.meanForSolution(rnCE, solutionsToGetRobust,
         numberOfSetToRobust);


        //System.out.println(solutions3);
        
        //System.out.println("RESULTADOS CON DURACIONES SIN DISTRIBUCION");
        
        rnCE.getProblemData().refreshDistributions(true);
        bestSol.recalculateRouteForNewDuration();
        solTool.recalculateRouteForNewDuration();
    	bestSol.calculateLoss();
    	solTool.calculateLoss();
        //System.out.println(bestSol);
        //System.out.println(solTool);

        //System.out.println("PERDIDA A 7 DIAS");
        bestSol.calculateLossSevenDays();
        solTool.calculateLossSevenDays();
        //System.out.println("SOLUCION Tool  " + solTool);
        solTool.visualisacionSimple();*/
        //System.out.println("BEST SOLUTION  " + bestSol);
        bestSol.visualisacionSimple();

        //System.out.println(bestSol.getRoutesByCrew());


        System.out.println("TIEMPO TOTAL");
        System.out.println(System.currentTimeMillis() - timeInitial);

    }

    public static Solution solutionFunctionExcel(ProblemData datos){
        ArrayList<Route> routesList = new ArrayList<Route>();
        Map<Crew, Route> routesByCrew = new HashMap<Crew, Route>();
        try{
            File file = new File(System.getProperty("user.dir") + "\\Java\\Solutions Tool\\Corrida - 1008 1080.xlsx");
            FileInputStream data = new FileInputStream(file);
            //creating Workbook instance that refers to .xlsx file
            XSSFWorkbook wb = new XSSFWorkbook(data);

            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            itr.next();

            while (itr.hasNext()){
                Row row = itr.next();
                Iterator<Cell> cellItr = row.iterator();
                String crewID = cellItr.next().getStringCellValue();
                Crew crew = datos.getHashCrews().get(crewID);
                ArrayList<Node> nodos = new ArrayList<>();
                String visitingNodes = cellItr.next().getStringCellValue();
                String[] vec = visitingNodes.split(",");
                for(String node: vec ){
                    nodos.add(datos.getHashNodes().get(node));
                }
                Route route = new Route(crew, crew.getAvailableTime(), nodos);

                routesList.add(route);
                routesByCrew.put(crew,route);
            }
        } catch (Exception e){
            System.out.println("EXCEPTION");
            System.out.println(e.getMessage());
        }
        Solution solution = new Solution(routesList, routesByCrew);

        return solution;
    }

    public static void sortSolutionList(ArrayList<Solution> solutions){
        // SORT DE LAS SOLUCIONES
        for (int i = 0; i < solutions.size(); i++) {
            //int min = array[i];
            double minSort = solutions.get(i).getLoss();
            int minId = i;
            for (int j = i+1; j < solutions.size(); j++) {
                if (solutions.get(j).getLoss() < minSort) {
                    minSort = solutions.get(j).getLoss();
                    minId = j;
                }
            }
            // swapping
            Collections.swap(solutions,i,minId);
        }
    }
}


