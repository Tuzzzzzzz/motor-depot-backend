package by.evgen.motor_depot.util;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtil {
    public static CellStyle createDateStyle(Workbook workbook){
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(
                workbook.getCreationHelper()
                        .createDataFormat()
                        .getFormat("dd.MM.yyyy HH:mm")
        );
        return dateStyle;
    }
}
