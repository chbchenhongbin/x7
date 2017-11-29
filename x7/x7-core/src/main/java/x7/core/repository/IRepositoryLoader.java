package x7.core.repository;

import java.util.List;

public interface IRepositoryLoader {

	<T> List<T> load(Class<T> clz);
}
