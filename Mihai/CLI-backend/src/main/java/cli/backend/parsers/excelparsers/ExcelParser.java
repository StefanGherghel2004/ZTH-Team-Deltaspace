package cli.backend.parsers.excelparsers;

import org.dhatim.fastexcel.reader.Row;

public abstract class ExcelParser {

    public abstract Object parse(Row row);
}
