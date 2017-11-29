package x7.tool;

import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;

import x7.tool.bean.BeanTemplate;
import x7.tool.generator.KeyGenerator;

public class StartUp_Gen_BeanKey {

	
	public static void main(String[] args) {
		
		String projectPath = Config.BASE;
		String template_path = "template/beankey.vm";
		
		Template template = Velocity.getTemplate(template_path);
		
		Set<BeanTemplate> list = BeanKey.createTemplateSet();

		KeyGenerator.generate(projectPath, list, template);

	}
}
