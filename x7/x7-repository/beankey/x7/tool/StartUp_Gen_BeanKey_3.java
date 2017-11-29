package x7.tool;

import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;

import x7.tool.bean.BeanTemplate;
import x7.tool.generator.KeyGenerator3;

public class StartUp_Gen_BeanKey_3 {

	
	public static void main(String[] args) {
		
		String projectPath = Config.BASE;
		String template_path = "template/beankey3.vm";
		
		Template template = Velocity.getTemplate(template_path);
		
		List<BeanTemplate> list = BeanKey3.createTemplateList();
		
		for (BeanTemplate beanTemplate : list) {
			KeyGenerator3.generate(projectPath, beanTemplate, template);
		}
	}
}
