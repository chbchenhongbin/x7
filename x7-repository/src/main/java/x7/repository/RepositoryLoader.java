package x7.repository;

import java.util.List;

import x7.core.repository.IRepositoryLoader;

public class RepositoryLoader implements IRepositoryLoader{

	@Override
	public <T> List<T> load(Class<T> clz) {
		return AsyncRepository.listSync(clz);
	}

}
