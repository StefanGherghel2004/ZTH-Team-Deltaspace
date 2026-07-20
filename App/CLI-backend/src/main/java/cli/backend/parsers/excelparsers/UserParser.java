package cli.backend.parsers.excelparsers;

import cli.backend.User;
import org.dhatim.fastexcel.reader.Row;

public class UserParser extends ExcelParser {

    @Override
    public Object parse(Row row) {

        if (row == null || row.getCellCount() < 5)
            throw new IllegalArgumentException("The row you are trying to parse is empty.");

        String username = String.valueOf(row.getCell(1));
        String email = String.valueOf(row.getCell(2));
        String password = String.valueOf(row.getCell(3));
        String dateOfBirth = String.valueOf(row.getCell(4));

        return new User(username,email,password,dateOfBirth);
    }
}
