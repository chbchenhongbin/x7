package x7;

import x7.core.config.Configs;
import x7.repository.RepositoryBooter;

public class RepositoryStarter {

	public RepositoryStarter() {

		System.out.println("_________Will start repository: " + Configs.isTrue("x7.repository.local"));
		if (Configs.isTrue("x7.repository.local")) {

			String dataSourceType = null;
			try{
				dataSourceType = Configs.getString("x7.repository.dataSourceType");
			}catch (Exception e){
				
			}
			RepositoryBooter.boot(dataSourceType);
		}

	}
}
