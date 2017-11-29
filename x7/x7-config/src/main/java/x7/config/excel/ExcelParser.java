package x7.config.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import x7.config.PathUtil;
import x7.core.async.ScheduledTaskService;
import x7.core.bean.KV;
import x7.core.template.ITemplateable;
import x7.core.template.Templates;
import x7.core.util.TimeUtil;

public class ExcelParser {

	private Map<String, Map<String, Class<ITemplateable>>> fileSheetClassMap = new HashMap<String, Map<String, Class<ITemplateable>>>();
	private Map<String, List<String>> fileSheetNameClassMap = new HashMap<String, List<String>>();

	private Map<String, Long> lastModefyTimeMap = new HashMap<String, Long>();

	private static long configLastModifiedTime = 0;

	private List<String> fileNames = new ArrayList<String>();

	private static ExcelParser instance = null;

	public static ExcelParser getInstance() {
		if (instance == null) {
			instance = new ExcelParser();

		}
		return instance;
	}

	private void schedule() {

		ScheduledTaskService.getInstance().schedule(new Runnable() {

			@Override
			public void run() {

				try {
					init();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					parse();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, TimeUtil.now() + TimeUtil.ONE_MINUTE * 1, TimeUtil.ONE_MINUTE / 2);
	}

	public void load() {
		try {

			init();
			parse();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ExcelParser() {
		schedule();
	}

	/**
	 * 关联实体类的名称与excel表格
	 * 
	 * @throws Exception
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		// 获取excel.xml配置路径

		String root = PathUtil.getRoot();

		File configFile = new File(root + "config/excel.xml");
		if (configFile.lastModified() <= configLastModifiedTime)
			return;

		configLastModifiedTime = configFile.lastModified();

		InputStream in = new FileInputStream(configFile);
		// 或者这个xml文件对象
		// File file = new File(path);
		// 使用JDOM进行读取
		SAXBuilder builder = new SAXBuilder(false);
		// 创建dom对象
		Document doc = builder.build(in);
		Element configRoot = doc.getRootElement();
		// 获取dom内的XML元素
		List<?> configList = configRoot.getChildren("book");
		fileNames.clear();
		// 对元素进行遍历
		for (Object configObject : configList) {
			Element configItem = (Element) configObject;
			// 获取某个元素的name，这对应excel文件名
			String fileName = configItem.getAttributeValue("name");
			fileNames.add(fileName);
			// 创建按标签名进行查询键名的map表
			Map<String, Class<ITemplateable>> sheetClassMap = new HashMap<String, Class<ITemplateable>>();
			List<String> sheetNameList = new ArrayList<String>();
			// 将这个map表放入以文件名为查询键名的map表
			fileSheetClassMap.put(fileName, sheetClassMap);
			fileSheetNameClassMap.put(fileName, sheetNameList);
			// 获得XML元素中的sheet子元素
			List<Element> sheetElementList = configItem.getChildren("sheet");
			// 对子元素进行遍历
			for (Element sheetE : sheetElementList) {
				// 获取子元素中某个标签名name
				String sheetName = sheetE.getAttributeValue("name");
				// 获取该标签名对应的类名type
				try {
					// Class<ITemplateable> clz =
					// Beans.getClass(sheetE.getAttributeValue("type"));

					Class<ITemplateable> clz = (Class<ITemplateable>) Class.forName(sheetE.getAttributeValue("type"));
					// 将name-type对照关系存入map表
					sheetClassMap.put(sheetName, clz);
					sheetNameList.add(sheetName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void parse() throws Exception {

		// 获取当前运行路径
		String root = PathUtil.getRoot();

		// 获取当前路径对应的文件夹
		File folder = new File(root + "config/excel/");
		// 对该文件夹内的所有文件进行遍历
		if (folder.listFiles() == null) {
			return;
		}

		HashMap<String, File> files = new HashMap<String, File>();

		/*
		 * 最后更新时间
		 */

		for (File file : folder.listFiles()) {

			Long lastModifyTime = lastModefyTimeMap.get(file.getName());
			if (lastModifyTime == null || file.lastModified() > lastModifyTime) {
				lastModefyTimeMap.put(file.getName(), file.lastModified());
				// 获取文件夹内的文件名称和文件对象
				files.put(file.getName(), file);
			}
		}
		for (String fileName : fileNames) {
			// 获取文件夹内的文件
			File file = files.get(fileName);
			if (file == null) {
//				System.err.println("EXCEL文件 (" + fileName + ") 找不到");
				continue;
			}
			// 当文件扩展名为xls或者xlsx时
			if (fileName.contains(".xls")) {
				// 从某个配置表获取这个文件所对应的模板类名Map<标签页名, Class<模板类>>
				Map<String, Class<ITemplateable>> sheetClassMap = fileSheetClassMap.get(fileName);
				// 读取整个excel文件

				if (sheetClassMap == null) {
					try {
						System.err.println("配置文件 (config/excel.xml) 没找到 (" + fileName + ") 的配置信息");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				Workbook workbook = null;

				try {
					workbook = Workbook.getWorkbook(file);
				} catch (Exception e) {
					System.out.println("EXCEL文件 (" + fileName + ") 处理出错");
					e.printStackTrace();
					continue;
				}

				// 读取该文件内的所有标签页sheet
				Sheet[] sheets = workbook.getSheets();
				Map<String, Sheet> sheetMap = new HashMap<String, Sheet>();
				for (Sheet sheet : sheets) {
					sheetMap.put(sheet.getName(), sheet);
				}
				List<String> sheetNames = fileSheetNameClassMap.get(fileName);
				// 对所有标签进行遍历
				for (String sheetName : sheetNames) {
					// 获取标签名
					Sheet sheet = sheetMap.get(sheetName);
					// 获取这个标签对应的模板类(配置EXCEL.XML中已经事先指定)

					if (sheet == null) {
						continue;
					}

					Class<? extends ITemplateable> clz = sheetClassMap.get(sheetName);

					if (clz == null) {
						try {
							System.err.println(
									"配置文件 (config/excel.xml) 没找到 (" + fileName + ") 里工作表  (" + sheetName + ")的配置信息");
						} catch (Exception e) {
							e.printStackTrace();
						}
						continue;
					}

					// 该模板中按照每个id作为唯一的排序条件进行的数据配置集合
//					Map<Integer, ITemplateable> beans = Templates.createOrGet(clz);
					
					Map<Object, ITemplateable> beans = new HashMap<Object, ITemplateable>();
					// 存入配置表
					// 获得配置表的行数
					int rows = sheet.getRows();

					// 按行进行配置赋值
					int startLine = 2;
					Cell[] rowNames = sheet.getRow(1);

					Map<String, Integer> nameMap = new HashMap<String, Integer>();

					int i = 0;
					for (Cell cell : rowNames) {

						String name = cell.getContents().trim();
						if (name != null && !name.equals("")) {
							nameMap.put(name, i);
						}
						i++;
					}

					for (i = startLine; i < rows; i++) {
						Cell[] rowArr = sheet.getRow(i);
						// 每行都对应一个配置数据对象
						ITemplateable bean = clz.newInstance();
						// 各个配置类都实现了parse方法，自行按照各自逻辑读取行数据
						parse(bean, rowArr, nameMap);

						System.out.println(bean);
						// 将配置完成的配置数据对象放入配置表中
						beans.put(bean.getTemplateId(), bean);
					}
					System.out.println("Excel, clz = " + clz + ", beans = " + beans);
					Templates.put(clz, beans);
					
					/*
					 * schemaMap
					 * 第一行名
					 * 第二行屬性
					 * 
					 */
					Cell[] nameCells = sheet.getRow(0);
					Cell[] propertyCells = sheet.getRow(1);
					
					List<KV> schemaList = new ArrayList<>();
					int length = nameCells.length;
					for (int j=0; j<length; j++) {
						String k = propertyCells[j].getContents().trim();
						String v = nameCells[j].getContents().trim();
						KV kv = new KV(k, v);
						schemaList.add(kv);
					}
					Templates.put(clz, schemaList);
				}
			}
			
			System.out.println("-----------");
		}
	}



	private static void parse(ITemplateable bean, Cell[] rowArr, Map<String, Integer> propertyMap) {

		Field[] fieldArr = bean.getClass().getDeclaredFields();

		try {
			for (Field field : fieldArr) {

				Integer index = propertyMap.get(field.getName());
				if (index == null)
					continue;

				String str = rowArr[index].getContents();
				if (str == null || str.trim().equals(""))
					str = "";
				switch (field.getType().getName()) {
				case "int":
					bean.getClass().getDeclaredMethod(getSetter(field.getName()), int.class).invoke(bean,
							str.equals("") ? 0 : Integer.valueOf(str));
					break;
				case "long":
					bean.getClass().getDeclaredMethod(getSetter(field.getName()), long.class).invoke(bean,
							str.equals("") ? 0 : Long.valueOf(str));
					break;
				case "double":
					bean.getClass().getDeclaredMethod(getSetter(field.getName()), double.class).invoke(bean,
							str.equals("") ? 0 : Double.valueOf(str));
					break;
				case "String":
					bean.getClass().getDeclaredMethod(getSetter(field.getName()), String.class).invoke(bean, str);
					break;
				case "boolean":
					bean.getClass().getDeclaredMethod(getSetter(field.getName()), boolean.class).invoke(bean,
							str.equals("1") ? true : false);
					break;
				default:
					bean.getClass().getDeclaredMethod(getSetter(field.getName()), String.class).invoke(bean, str);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getSetter(String name) {
		if (name.startsWith("is")) {
			String rest = name.substring(2);
			return "set" + rest;
		}

		String a = name.substring(0, 1);
		String rest = name.substring(1);
		return "set" + a.toUpperCase() + rest;
	}
	
	
	public static class Export {
		
		public static Excel build(Class<? extends ITemplateable> clz, List<? extends ITemplateable> dataList){
			Excel excel = new Excel();
			excel.clz = clz;
			excel.dataList = dataList;
			
			
			return excel;
		}
		
		public static class Excel {
			private Class<? extends ITemplateable> clz;
			private List<? extends ITemplateable> dataList;	
			
			public void write(String filePath){
				System.out.println("clz = " + clz);
				System.out.println("dataList = " + clz);
				System.out.println("裝逼的EXCEL導出 => " + filePath);
				
				File file = new File(filePath);
				WritableWorkbook workbook = null;
				
				List<KV> tagList = Templates.getSchema(clz);
				
				try {
					workbook = Workbook.createWorkbook(file);
					WritableSheet sheet = workbook.createSheet(clz.getSimpleName(), 0);
					int r = 0, c = 0;
					for (KV kv : tagList){
						sheet.addCell(new Label(c++, r, kv.v.toString()));
					}
					
					r++;
					
					for (ITemplateable template : dataList) {
						c = 0;
						for (KV kv : tagList){
							Field f = clz.getDeclaredField(kv.k);
							Object v = f.get(template);
							sheet.addCell(new Label(c++, r, v.toString()));
						}
						r++;
					}
					
					workbook.write();
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {
						if (workbook != null)
							workbook.close();
					} catch (WriteException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			/**
			 * 文件路径， 可自定义简单标题
			 * @param filePath
			 * @param headline
			 */
			public void write(String filePath, Headline headline, Footer footer){
				System.out.println("clz = " + clz);
				System.out.println("dataList = " + clz);
				System.out.println("裝逼的EXCEL導出 => " + filePath);
				
				File file = new File(filePath);
				WritableWorkbook workbook = null;
				
				List<KV> tagList = Templates.getSchema(clz);
				
				try {
					workbook = Workbook.createWorkbook(file);
					WritableSheet sheet = workbook.createSheet(clz.getSimpleName(), 0);
					int r = 0, c = 0;
					
					if (headline != null){
						r += headline.addTo(sheet);
					}
					
					for (KV kv : tagList){
						sheet.addCell(new Label(c++, r, kv.v.toString()));
					}
					
					r++;
					
					for (ITemplateable template : dataList) {
						c = 0;
						for (KV kv : tagList){
							Field f = clz.getDeclaredField(kv.k);
							Object v = f.get(template);
							sheet.addCell(new Label(c++, r, v.toString()));
						}
						r++;
					}
					
					if (footer != null){
						footer.addTo(r, sheet);
					}
					
					workbook.write();
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {
						if (workbook != null)
							workbook.close();
					} catch (WriteException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			/**
			 * 简单标题
			 * @author Sim
			 *
			 */
			public static interface Headline{
				/**
				 * 
				 * @param sheet
				 * @return 当前最后一行
				 */
				int addTo(Sheet sheet);
			}
			
			/**
			 * 简单的表尾
			 * @author Sim
			 *
			 */
			public static interface Footer{
				
				int addTo(int lastRow, Sheet sheet);
			}
			
		}
		
	}
	

	public static class Import {
		
		public static ExcelBuilder build(){
			ExcelBuilder excel = new ExcelBuilder();
			return excel;
		}
		
		public static class ExcelBuilder {
			
			private Map<Class<? extends ITemplateable>, List<ITemplateable>> templateMap = new HashMap<>();
			
			public ExcelBuilder add(Class<? extends ITemplateable> clz){
				templateMap.put(clz, new ArrayList<>());
				return this;
			}
			
			
			public Map<Class<? extends ITemplateable>, List<ITemplateable>> read(String filePath){			
				
				File file = new File(filePath);
				
				Workbook workbook = null;

				try {
					workbook = Workbook.getWorkbook(file);
				} catch (Exception e) {
					System.out.println("EXCEL文件 (" + file + ") 处理出错");
					e.printStackTrace();
				}
				
				// 读取该文件内的所有标签页sheet
				Sheet[] sheets = workbook.getSheets();
				
				// 对所有标签进行遍历
				for (Sheet sheet : sheets) {
					// 获取标签名

					if (sheet == null) {
						continue;
					}

					Class<? extends ITemplateable> clz = null;
					for (Class clzz : templateMap.keySet()){
						if (clzz.getSimpleName().equals(sheet.getName())){
							clz = clzz;
							break;
						}
					}
					

					if (clz == null) {
						continue;
					}

					
					List<ITemplateable> list = templateMap.get(clz);
					// 存入配置表
					// 获得配置表的行数
					final int rows = sheet.getRows();

					// 按行进行配置赋值
					int startLine = 2;
					Cell[] propertyNameCellArr = sheet.getRow(1);

					Map<String, Integer> propertyMap = new HashMap<String, Integer>();

					int i = 0;
					for (Cell cell : propertyNameCellArr) {

						String name = cell.getContents().trim();
						if (name != null && !name.equals("")) {
							propertyMap.put(name, i);
						}
						i++;
					}

					for (i = startLine; i < rows; i++) {
						Cell[] rowArr = sheet.getRow(i);
						// 每行都对应一个配置数据对象
						ITemplateable bean = null;
						try {
							bean = clz.newInstance();
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
						// 各个配置类都实现了parse方法，自行按照各自逻辑读取行数据
						parse(bean, rowArr, propertyMap);

						if (bean.getTemplateId() == null || Integer.valueOf(bean.getTemplateId().toString())==0){
							bean.setTemplateId(i + 1);
						}
						System.out.println(bean);
						// 将配置完成的配置数据对象放入配置表中
						list.add(bean);
					}
					
					try {
						if (workbook != null)
							workbook.close();
					} catch (Exception e){
						e.printStackTrace();
					}
				} 
				
				return templateMap;
				
			}
		}
		
	}

}
 