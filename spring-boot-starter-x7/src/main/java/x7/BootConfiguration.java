package x7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import x7.config.ConfigProperties;
import x7.repository.RepositoryProperties_W;

@Configuration
@EnableConfigurationProperties({ConfigProperties.class,RepositoryProperties_W.class})
public class BootConfiguration {

	@Autowired
	private ConfigProperties configProperies;
	@Autowired
	private RepositoryProperties_W repositoryProperties;
	
	@Bean
    ConfigStarter x7ConfigStarter (){
		
        return  new ConfigStarter(configProperies.isCentralized(),configProperies.getSpace(), configProperies.getLocalAddress(), configProperies.getRemoteAddress());
    }


	@Bean
	RepositoryStarter x7RepsositoryStarter(){

		return new RepositoryStarter();
	}
	
}
