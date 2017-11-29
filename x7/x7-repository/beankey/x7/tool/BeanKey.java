package x7.tool;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import x7.core.util.BeanUtil;
import x7.core.util.ClassFileReader;
import x7.tool.bean.BeanTemplate;


public class BeanKey {

	public static Set<BeanTemplate> createTemplateSet() {

		Set<Class<?>> set = ClassFileReader.getClasses(Config.PKG);

		Set<BeanTemplate> st = new HashSet<>();
		for (Class<?> clz : set) {
			BeanTemplate template = parse(clz);
			if (template == null)
				continue;
			if (template.getClzName() == null ||template.getClzName().equals("") || template.getClzName().equals("Key"))
				continue;
			st.add(template);
		}

		return st;
	}

	public static BeanTemplate parse(Class<?> clz) {

		BeanTemplate template = new BeanTemplate();
		
		String fullName = clz.getName();
		String simpleName = clz.getSimpleName();
		simpleName = BeanUtil.getByFirstLower(simpleName);
		String pkg = "key." + fullName.substring(0, fullName.lastIndexOf("."));
		template.setPackageName(pkg);
		template.setClzName(simpleName);

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
