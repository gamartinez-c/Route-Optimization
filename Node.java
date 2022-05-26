import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.distribution.*;
import org.apache.poi.util.Beta;

public class Node {

    private static int idCounter = 1;
    private String desc;
    private int id;
    private String ordenID;
    private double output;
    private double taskDurationNoError;
    private double taskDuration;
    private double operationsDurations;
    private ArrayList<Crew> compatibleCrews;
    private boolean initialLoss;
    private Map<Node, Double> distancesToNodes;
    private boolean esFijo;
    private boolean esContinuidadDePrograma;

    private static double taskDurationMeanDist;
    private static double taskDurationVarianceDist;
    private static double coeficienteIndependienteDist;
    private static double coeficienteMultiplicadorDist;

    private static double taskDurationMeanNPT;
    private static double taskDurationVarianceNPT;
    private static double coeficienteIndependienteNPT;
    private static double coeficienteMultiplicadorNPT;

    //-----------------------CONSTRUCTOR-----------------------
    public Node(String desc, double output, double taskDuration,double operationsDurations,
                ArrayList<Crew> compatibleCrews, boolean initialLoss) {
        this.id = Node.idCounter;
        this.desc = desc;
        this.output = output;
        this.taskDuration = taskDuration;
        this.taskDurationNoError = taskDuration;
        this.operationsDurations = operationsDurations;
        this.compatibleCrews = compatibleCrews;
        this.initialLoss = initialLoss;
        this.distancesToNodes = new HashMap<Node, Double>();
        this.esFijo = false;
        this.esContinuidadDePrograma = false;
        Node.idCounter += 1;
    }
    //Con ordenID
    public Node(String desc, String ordenID, double output, double taskDuration, double operationsDurations,
                ArrayList<Crew> compatibleCrews, boolean initialLoss) {
        this.id = Node.idCounter;
        this.ordenID = ordenID;
        this.desc = desc;
        this.output = output;
        this.taskDuration = taskDuration;
        this.taskDurationNoError = taskDuration;
        this.operationsDurations = operationsDurations;
        this.compatibleCrews = compatibleCrews;
        this.initialLoss = initialLoss;
        this.distancesToNodes = new HashMap<Node, Double>();
        this.esFijo = false;
        this.esContinuidadDePrograma = false;
        Node.idCounter += 1;
    }


    //-----------------------GET-----------------------
    public int getId(){
        return this.id;
    }
    public String getOrdenID() {
        return ordenID;
    }
    public double getOutput(){
        return this.output;
    }
    public double getTaskDuration(){
        return this.taskDuration;
    }
    public double getOperationsDurations(){
        return this.operationsDurations;
    }
    public boolean getInitialLoss(){
        return this.initialLoss;
    }
    public ArrayList<Crew> getCompatibleCrews() { return this.compatibleCrews; }
    public double getOutputOverTaskDuration() {
        return this.output / this.taskDuration;
    }
    public String getDesc() {
        return desc;
    }
    public boolean getEsFijo() {
        return esFijo;
    }
    public boolean getEsContinuidadDePrograma() {
        return esContinuidadDePrograma;
    }
    public static double getTaskDurationMeanDist() {
        return taskDurationMeanDist;
    }
    public static double getTaskDurationVarianceDist() {
        return taskDurationVarianceDist;
    }
    public static double getCoeficienteIndependienteDist() {
        return coeficienteIndependienteDist;
    }
    public static double getCoeficienteMultiplicadorDist() {
        return coeficienteMultiplicadorDist;
    }
    public static double getTaskDurationMeanNPT() {
        return taskDurationMeanNPT;
    }
    public static double getTaskDurationVarianceNPT() {
        return taskDurationVarianceNPT;
    }
    public static double getCoeficienteIndependienteNPT(double numericCellValue) {
        return coeficienteIndependienteNPT;
    }
    public static double getCoeficienteMultiplicadorNPT() {
        return coeficienteMultiplicadorNPT;
    }
    public static int getIdCounter() {
        return idCounter;
    }

    //-----------------------SET-----------------------
    public void setId(int id){
        this.id = id;
    }
    public void setOrdenID(String ordenID) {
        this.ordenID = ordenID;
    }
    public void setOutput(double output){
        this.output = output;
    }
    public void setTaskDuration(double taskDuration){
        this.taskDuration = taskDuration;
    }
    public void setOperationsDurations(double operationDuration){
        this.operationsDurations = operationDuration;
    }
    public void setInitialLoss(boolean initialLoss){
        this.initialLoss = initialLoss;
    }
    public void setEsFijo(boolean esFijo) {
        this.esFijo = esFijo;
    }
    public void setEsContinuidadDePrograma(boolean esContinuidadDePrograma) {
        this.esContinuidadDePrograma = esContinuidadDePrograma;
    }
    public static void setTaskDurationMeanDist(double taskDurationMeanDist) {
        Node.taskDurationMeanDist = taskDurationMeanDist;
    }
    public static void setTaskDurationVarianceDist(double taskDurationVarianceDist) {
        Node.taskDurationVarianceDist = taskDurationVarianceDist;
    }
    public static void setCoeficienteIndependienteDist(double coeficienteIndependienteDist) {
        Node.coeficienteIndependienteDist = coeficienteIndependienteDist;
    }
    public static void setCoeficienteMultiplicadorDist(double coeficienteMultiplicadorDist) {
        Node.coeficienteMultiplicadorDist = coeficienteMultiplicadorDist;
    }
    public static void setTaskDurationMeanNPT(double taskDurationMeanNPT) {
        Node.taskDurationMeanNPT = taskDurationMeanNPT;
    }
    public static void setTaskDurationVarianceNPT(double taskDurationVarianceNPT) {
        Node.taskDurationVarianceNPT = taskDurationVarianceNPT;
    }
    public static void setCoeficienteIndependienteNPT(double coeficienteIndependienteNPT) {
        Node.coeficienteIndependienteNPT = coeficienteIndependienteNPT;
    }
    public static void setCoeficienteMultiplicadorNPT(double coeficienteMultiplicadorNPT) {
        Node.coeficienteMultiplicadorNPT = coeficienteMultiplicadorNPT;
    }
    public static void setIdCounter(int idCounter) {
        Node.idCounter = idCounter;
    }

    //-----------------------OTHER-----------------------
    public void addDistanceToOtherNode(Node otherNode, double distanceValue){
        if(!this.distancesToNodes.containsKey(otherNode)){
            this.distancesToNodes.put(otherNode, distanceValue);
        } else {
            //System.out.println(this.getId() + " " + otherNode.getId() + " dist " + distanceValue);
            //System.out.println("value is being overwritten");
        }
    }

    public double getDistanceToOtherNode(Node otherNode) {
        if (this.distancesToNodes.containsKey(otherNode)) {
            return this.distancesToNodes.get(otherNode);
        } else {
            //System.out.println("not found");
            return 1;
        }
    }

    public void recalculateTimeForDistribution(){
    	int desvioLogica = 1;
    	//RINCON DE LOS SAUCES 1006 - 1007
        //EL PORTON 1013 - 1041
        //NEUQUEN OESTE (LLL) 1008 - 1080

        //Defino distribuciones para tanto el desvio plan-real y el npt
        NormalDistribution desvioNormal;
        BetaDistribution desvioBeta;
        GammaDistribution desvioGamma;
        ExponentialDistribution desvioExponencial;
        NormalDistribution nptNormal;
        BetaDistribution nptBeta;
        GammaDistribution nptGamma;
        ExponentialDistribution nptExponencial;

        double errorDesvio;
        double errorNPT;
        double taskDuration = taskDurationNoError;

        //Calculo la media y varianza de las distribuciones puras ej. Gamma, Beta, Normal, etc.
        double mediaDistPura = (-coeficienteIndependienteDist + taskDurationMeanDist)/coeficienteMultiplicadorDist;
        double varianzaDistPura = taskDurationVarianceDist / Math.pow(coeficienteMultiplicadorDist, 2);
        double mediaNptPura = (-coeficienteIndependienteNPT + taskDurationMeanNPT)/coeficienteMultiplicadorNPT;
        double varianzaNptPura = taskDurationVarianceNPT / Math.pow(coeficienteMultiplicadorNPT, 2);

        //Caluclo los parametros para las distribuciones Gamma, Beta, Exponencial
        double shape = Math.pow(mediaNptPura,2)/varianzaNptPura;
        double scale = varianzaNptPura/mediaNptPura;
        double alpha = ((Math.pow(mediaDistPura, 2)*(1-mediaDistPura))/varianzaDistPura) - mediaDistPura;
        double beta = (((mediaDistPura * (1-mediaDistPura))/varianzaDistPura) - 1) * (1-mediaDistPura);
        //double lambda = mediaNptPura;

        //Defino distribuciones puras y calculo la distribucion total para el desvio plan-real y el npt
        desvioBeta = new BetaDistribution(alpha, beta);
        nptGamma = new GammaDistribution(shape, scale);
        errorDesvio = Node.coeficienteIndependienteDist + Node.coeficienteMultiplicadorDist * desvioBeta.sample();
        errorNPT = Node.coeficienteIndependienteNPT + Node.coeficienteMultiplicadorNPT * nptGamma.sample();

        //Asigno la duracion de la intervencion con los errores calculados y le sumo la duracion de las operaciones
        //que no contienen el error de desvio plan-real y npt
        taskDuration = (taskDurationNoError - operationsDurations + desvioLogica * errorDesvio) *
                desvioLogica * errorNPT + operationsDurations;

        if(taskDuration < 0 ){
            taskDuration = this.taskDurationNoError;
        }
        this.taskDuration = taskDuration;
    }

    public void resetTaskDurations() {
    	this.taskDuration = taskDurationNoError;
    }

    public void addCrewToCompatibleCrews(Crew crew) {
        this.compatibleCrews.add(crew);
    }

    //-----------------------TO STRING-----------------------
    public String toString(){
            return "Node. Id: " + this.id + ", Output: " + this.output +", Task Duration: " +
                    this.taskDuration + ", Original TDNoError: " + this.taskDurationNoError +
                    ", Initial Loss: " + this.initialLoss + ", Es Fijo: " + esFijo;
    }



}