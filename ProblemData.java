import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ProblemData {

    private HashMap<String, Node> nodes;
    private HashMap<String, Node> totalNodes;
    private HashMap<String, Crew> compatibleCrews;
    private double mean;
    private double std;

    //-----------------------CONSTRUCTOR-----------------------
    public ProblemData(double mean, double std) {
        // ----------------------------- CARGO TODA LA INFO DEL EXCEL ----------------------------
        String currentDirectory = System.getProperty("user.dir");
        //System.out.println(currentDirectory + "\\Java\\datos\\RCL - 1008 1080.xlsx");
        nodes = new HashMap<>();
        compatibleCrews = new HashMap<>();
        totalNodes = new HashMap<>();
        this.mean = mean;
        this.std = std;
        try {
            File file = new File(currentDirectory + "\\Java\\datos\\OES - 1008 1080 - caso - replicado - de - Tool.xlsx");
            FileInputStream data = new FileInputStream(file);
            //creating Workbook instance that refers to .xlsx file
            XSSFWorkbook wb = new XSSFWorkbook(data);


            //Pestaña 1: NODES
            XSSFSheet sheet = wb.getSheetAt(1);     //creating a Sheet object to retrieve object
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            itr.next();
            while (itr.hasNext()) {
                //System.out.println("Entro al primer while");
                Row row = itr.next();
                String nodeDesc = row.getCell(0).getStringCellValue();
                double output = row.getCell(1).getNumericCellValue();
                double taskDuration = (int) row.getCell(2).getNumericCellValue() + (int) row.getCell(3).getNumericCellValue();
                double operationsDurations = (int) row.getCell(3).getNumericCellValue();
                // falta cargar las cuad posibles
                ArrayList<Crew> possibleCrews = new ArrayList<>();
                boolean perdidaInicial = row.getCell(5).getBooleanCellValue();
                Node n = new Node(nodeDesc, output, taskDuration, operationsDurations, possibleCrews, perdidaInicial);
                this.nodes.put(nodeDesc, n);
                this.totalNodes.put(nodeDesc, n);
            }

            //Cargamos desvios plan-real de nodos
            sheet = wb.getSheetAt(6);
            itr = sheet.iterator();    //iterating over excel file
            itr.next();
            while(itr.hasNext()) {
                Row row = itr.next();
                double constante = row.getCell(0).getNumericCellValue();
                Node.setCoeficienteIndependienteDist(constante);
                double multiplicador = row.getCell(1).getNumericCellValue();
                Node.setCoeficienteMultiplicadorDist(multiplicador);
                double media = row.getCell(2).getNumericCellValue();
                Node.setTaskDurationMeanDist(media);
                double varianza = row.getCell(3).getNumericCellValue();
                Node.setTaskDurationVarianceDist(varianza);
            }
            //Cargamos npt de nodos
            sheet = wb.getSheetAt(7);
            itr = sheet.iterator();
            itr.next();
            while(itr.hasNext()) {
                Row row = itr.next();
                double constante = row.getCell(0).getNumericCellValue();
                Node.setCoeficienteIndependienteNPT(constante);
                double multiplicador = row.getCell(1).getNumericCellValue();
                Node.setCoeficienteMultiplicadorNPT(multiplicador);
                double media = row.getCell(2).getNumericCellValue();
                Node.setTaskDurationMeanNPT(media);
                double varianza = row.getCell(3).getNumericCellValue();
                Node.setTaskDurationVarianceNPT(varianza);
            }

            for(Node n: nodes.values()){
                n.recalculateTimeForDistribution();
            }
            
            // Pestaña 0: CREWS
            sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            itr = sheet.iterator();
            itr.next();
            while (itr.hasNext()) {
                //System.out.println("entro al segundo while");
                Row row = itr.next();
                String id = row.getCell(0).getStringCellValue();
                String indexDesc = row.getCell(1).getStringCellValue();
                Node n = totalNodes.get(indexDesc);
                if (n == null) {
                    n = new Node(indexDesc, 0, 0, 0, new ArrayList<Crew>(),
                            false);
                    this.totalNodes.put(indexDesc, n);
                }
                int availabilityTime = (int) row.getCell(2).getNumericCellValue();
                Crew c = new Crew(id, availabilityTime, n, 30);
                //System.out.println("ID: " + row.getCell(0));
                //System.out.println(c);
                this.compatibleCrews.put(id, c);
            }

            // Pestaña 2: DISTANCE TO NODE
            sheet = wb.getSheetAt(2);     //creating a Sheet object to retrieve object
            itr = sheet.iterator();
            itr.next();
            int contador = 0;
            while (itr.hasNext()) {
                Row row = itr.next();
                String nodeKey1 = row.getCell(0).getStringCellValue();
                String nodeKey2 = row.getCell(1).getStringCellValue();
                double dist = row.getCell(2).getNumericCellValue();

                Node node1 = totalNodes.get(nodeKey1);
                Node node2 = totalNodes.get(nodeKey2);
                node1.addDistanceToOtherNode(node2, dist);
                if (node1.getId() != node2.getId()) {
                    node2.addDistanceToOtherNode(node1, dist);
                }
                contador++;
            }
            sheet = wb.getSheetAt(1);     //creating a Sheet object to retrieve object
            itr = sheet.iterator();
            itr.next();
            while (itr.hasNext()) {
                Row row = itr.next();
                String idNode = row.getCell(0).getStringCellValue();
                String compatibleCrews = row.getCell(4).getStringCellValue();
                String[] vec = compatibleCrews.split(",");
                for (String compatibleCrew : vec) {
                    Crew crew = this.compatibleCrews.get(compatibleCrew);
                    this.nodes.get(idNode).addCrewToCompatibleCrews(crew);
                }
            }

            //FIJAS
            sheet = wb.getSheetAt(3);
            itr = sheet.iterator();
            itr.next();
            HashMap<Crew, ArrayList<RouteStop>> auxMapFija = new HashMap<>();
            ArrayList<Node> auxArrayFijas = new ArrayList<>();
            while (itr.hasNext()) {
                Row row = itr.next();
                String idNode = row.getCell(0).getStringCellValue();
                String idCrew = String.valueOf((int) (row.getCell(1).getNumericCellValue()));
                Crew crew = compatibleCrews.get(idCrew);
                Node node = nodes.get(idNode);
                node.setEsFijo(true);
                double tiempoInicio = row.getCell(2).getNumericCellValue();
                RouteStop routeStop = new RouteStop(node, tiempoInicio, true);
                if (auxMapFija.containsKey(crew)){
                    auxMapFija.get(crew).add(routeStop);
                } else {
                    ArrayList<RouteStop> listaDeRouteStop = new ArrayList<>();
                    listaDeRouteStop.add(routeStop);
                    auxMapFija.put(crew, listaDeRouteStop);
                }
                auxArrayFijas.add(node);
            }
            Route.setMapaNodosFijos(auxMapFija);
            Route.setNodosFijos(auxArrayFijas);

            //Continuidad de Programa
            sheet = wb.getSheetAt(4);
            itr = sheet.iterator();
            itr.next();
            HashMap<Crew, Node> auxMapContinuidadDeProg = new HashMap<>();
            while (itr.hasNext()) {
                Row row = itr.next();
                String idNode = row.getCell(0).getStringCellValue();
                String idCrew = String.valueOf((int) (row.getCell(1).getNumericCellValue()));
                Crew crew = compatibleCrews.get(idCrew);
                Node node = nodes.get(idNode);
                node.setEsContinuidadDePrograma(true);
                auxMapContinuidadDeProg.put(crew, node);
            }
            Route.setMapaNodosContinuidadDePrograma(auxMapContinuidadDeProg);

        } catch (Exception e) {
            System.out.println("Catch");
            System.out.println(e.getLocalizedMessage());
        }
    }

    public ProblemData(int num, double mean, double std){
        this.mean = mean;
        this.std = std;
    }
    //-----------------------GET-----------------------
    public ArrayList<Node> getNodes(){
        Collection<Node> aux = this.nodes.values();
        return new ArrayList<>(aux);
    }
    public HashMap<String, Node> getHashNodes(){
        return this.nodes;
    }
    public HashMap<String, Crew> getHashCrews(){
        return this.compatibleCrews;
    }
    public ArrayList<Crew> getCompatibleCrews(){
        Collection<Crew> aux = this.compatibleCrews.values();
        return new ArrayList<>(aux);
    }
    public HashMap<String, Node> getTotalNodes() {
        return totalNodes;
    }

    //SETTER

    public void setNodes(HashMap<String, Node> nodes) {
        this.nodes = nodes;
    }
    public void setTotalNodes(HashMap<String, Node> totalNodes) {
        this.totalNodes = totalNodes;
    }
    public void setCompatibleCrews(HashMap<String, Crew> compatibleCrews) {
        this.compatibleCrews = compatibleCrews;
    }

    //OTHER
    public void refreshDistributions(boolean reset){
        for(Node n: nodes.values()) {
        	if(reset) {
        		n.resetTaskDurations();
        	}else {
        		n.recalculateTimeForDistribution();
        	}
            
        }
    }

    public void adjustFixedOTs(){
        for (Crew crew: Route.getMapaNodosFijos().keySet()){
            for(RouteStop routeStop: Route.getMapaNodosFijos().get(crew)){
                routeStop.setArrivingTime(Math.max((routeStop.getArrivingTime() -
                        RouteStop.getDesfasajeFijas()), crew.getAvailableTime()));
                routeStop.getNode().setTaskDuration(routeStop.getNode().getTaskDuration() +
                        RouteStop.getDesfasajeFijas());
            }
        }
    }

    public void cleanFixedOTsDuration(){
        for (Crew crew: Route.getMapaNodosFijos().keySet()){
            for(RouteStop routeStop: Route.getMapaNodosFijos().get(crew)){
                routeStop.setArrivingTime(routeStop.getArrivingTime() + RouteStop.getDesfasajeFijas());
                routeStop.getNode().setTaskDuration(routeStop.getNode().getTaskDuration() -
                        RouteStop.getDesfasajeFijas());
            }
        }
    }
}
