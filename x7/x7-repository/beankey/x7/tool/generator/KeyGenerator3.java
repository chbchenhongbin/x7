package x7.tool.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import x7.tool.bean.BeanTemplate;

public class KeyGenerator3 {


	public static void generate(String projectPath, BeanTemplate beanTemplate, Template template){
        try{
          
            String beanPackage = beanTemplate.getPackageName();
            String className = beanTemplate.getClzName();
            beanPackage = beanPackage.replaceAll("\\.", "\\/");       
            
            File folder = new File(projectPath+beanPackage);
            if (!folder.exists()){
            	folder.mkdirs();
            }

            File file = new File(projectPath+beanPackage+"/"+ className+".java");

            VelocityContext context = new VelocityContext();
    
            
            context.put("package", beanTemplate.getPackageName());
            context.put("className", className);    
            context.put("propList", beanTemplate.getPropList()); 

            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file)));

            if ( template != null)
                template.merge(context, writer);

            writer.flush();
            writer.close();
            
        }catch( Exception e ){
            System.out.println(e);
        }
	}
}
