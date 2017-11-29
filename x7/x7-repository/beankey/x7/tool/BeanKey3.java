package x7.tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import x7.core.util.ClassFileReader;
import x7.tool.bean.BeanTemplate;




public class BeanKey3 {

	public static List<BeanTemplate> createTemplateList() {

		Set<Class<?>> set = ClassFileReader.getClasses(Config.PKG);

		List<BeanTemplate> list = new ArrayList<>();
		for (Class<?> clz : set) {
			BeanTemplate template = parse(clz);
			list.add(template);
		}

		return list;
	}

	public static BeanTemplate parse(Class<?> clz) {

		BeanTemplate template = new BeanTemplate();
		
		String fullName = clz.getName();
		String simpleName = clz.getSimpleName();
		String pkg = "key." + fullName.substring(0, fullName.lastIndexOf("."));
		template.setPackageName(pkg);
		template.setClzName(simpleName+"Key");

		Field[] fields = clz.getDeclaredFields();

		for (Field field : fields) {
			
			if (field.getModifiers() > 2) 
				continue;

			template.getPropList().add(field.getName());
		}
		
		System.out.println(template);

		return template;
	}

}
