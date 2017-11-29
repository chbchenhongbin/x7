package x7.config;

import java.util.ArrayList;
import java.util.List;

import x7.config.excel.ExcelParser;
import x7.config.excel.ExcelParser.Export.Excel;

public class ExcelTester {

	public static void main(String[] args) {
		
		List<DogTemplate> dogList = new ArrayList<DogTemplate>();
		List<CatTemplate> catList = new ArrayList<CatTemplate>();
		
		Excel excel = ExcelParser.Export.build(DogTemplate.class, dogList);
		
		ExcelParser.Export.build(CatTemplate.class, catList).write("cat.xls");
		
		excel.write("dog.xls");
	}
	
}
