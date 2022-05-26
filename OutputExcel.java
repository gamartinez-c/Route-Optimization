import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class OutputExcel {

    public OutputExcel(ArrayList<Solution> solutionsConstructive, ArrayList<Solution> solutionsVecindades) {


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Soluciones Inicial");
        int rowCount = 0;
        int colCount = 0;
        Row row = sheet.createRow(rowCount);
        // Agrego nombres columnas:
        Cell cell = row.createCell(colCount++);
        cell.setCellValue("ID");
        cell = row.createCell(colCount++);
        cell.setCellValue("Perdida Acumulada");
        cell = row.createCell(colCount++);
        cell.setCellValue("Heuristica utilizada");
        cell = row.createCell(colCount++);
        cell.setCellValue("Negocio");
        cell = row.createCell(colCount++);
        cell.setCellValue("KM Recorridos");
        cell = row.createCell(colCount++);
        cell.setCellValue("Tiempo finalizacion");
        cell = row.createCell(colCount++);
        cell.setCellValue("Tiempo Corrida (ms)");
        cell = row.createCell(colCount++);
        cell.setCellValue("Randomness");
        rowCount++;
        for(Solution s: solutionsConstructive){
            colCount = 0;
            row = sheet.createRow(rowCount);
            // ID
            cell = row.createCell(colCount++);
            cell.setCellValue(s.getId());
            // Perdida Acumulada
            cell = row.createCell(colCount++);
            cell.setCellValue(s.getLoss());
            // Heuristica utilizada
            cell = row.createCell(colCount++);
            cell.setCellValue(s.getHeuristic());
            // Negocio
            cell = row.createCell(colCount++);
            cell.setCellValue("CentroPrueba");
            // KM Recorridos
            cell = row.createCell(colCount++);
            cell.setCellValue(s.getTimeTraveling());
            // Tiempo finalziacion
            cell = row.createCell(colCount++);
            cell.setCellValue(s.getSolutionTotalDuration());
            // Tiempo Corrida
            cell = row.createCell(colCount++);
            cell.setCellValue(s.getRunningTime());
            // Probabilidad
            cell = row.createCell(colCount++);
            cell.setCellValue(ConstructiveHeuristic.getProb());
            rowCount++;
        }

        XSSFSheet sheet1 = workbook.createSheet("Vecinos");
        int rowCount1 = 0;
        int colCount1 = 0;
        Row row1 = sheet1.createRow(rowCount1);
        // Agrego nombres columnas:
        Cell cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("ID Padre");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Perdida Acumulada");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Negocio");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("KM Recorridos");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Tiempo finalizacion");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Tiempo Corrida (ms)");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Vecindad 1");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Profundiad Vec 1");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Vecindad 2");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Profundiad Vec 2");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Vecindad 3");
        cell1 = row1.createCell(colCount1++);
        cell1.setCellValue("Profundiad Vec 3");
        rowCount1++;
        for(Solution s: solutionsVecindades){
            colCount1 = 0;
            row1 = sheet1.createRow(rowCount1);
            // ID
            cell1 = row1.createCell(colCount1++);
            cell1.setCellValue(s.getId());
            // Perdida Acumulada
            cell1 = row1.createCell(colCount1++);
            cell1.setCellValue(s.getLoss());
            // Negocio
            cell1 = row1.createCell(colCount1++);
            cell1.setCellValue("CentroPrueba");
            // KM Recorridos
            cell1 = row1.createCell(colCount1++);
            cell1.setCellValue(s.getTimeTraveling());
            // Tiempo finalziacion
            cell1 = row1.createCell(colCount1++);
            cell1.setCellValue(s.getSolutionTotalDuration());
            // Tiempo Corrida
            cell1 = row1.createCell(colCount1++);
            cell1.setCellValue(s.getRunningTime());
            for(String key : s.getDiccLogicaYProfundidad().keySet()) {
                // Vecindad
                cell1 = row1.createCell(colCount1++);
                cell1.setCellValue(key);
                // Profundidad
                cell1 = row1.createCell(colCount1++);
                cell1.setCellValue(s.getDiccLogicaYProfundidad().get(key));
            }
            rowCount1++;
        }

        try (FileOutputStream outputStream = new FileOutputStream("Output.xlsx")) {
            workbook.write(outputStream);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
