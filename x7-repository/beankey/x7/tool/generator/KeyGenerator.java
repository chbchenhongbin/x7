package x7.tool.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import x7.tool.bean.BeanTemplate;

public class KeyGenerator {


	public static void generate(String projectPath, Set<BeanTemplate> beanTemplateList, Template template){
        try{
          
            
            
            File folder = new File(projectPath+"/beankey");
            if (!folder.exists()){
            	folder.mkdirs();
            }

            File file = new File(projectPath+"/beankey/Key.java");

            VelocityContext context = new VelocityContext();
    
            
            context.put("clzList", beanTemplateList);

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
