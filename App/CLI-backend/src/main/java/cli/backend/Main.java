package cli.backend;

import cli.backend.database.ExcelRead;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.*;

public class Main {
    public static void main(String[] args) {

        AppHandler app = AppHandler.getInstance();
        ExcelRead excelRead=new ExcelRead("App/CLI-backend/databases/Book1.xlsx");
        excelRead.readExcel();
        //Logger.init();
        //app.run();
    }
}
