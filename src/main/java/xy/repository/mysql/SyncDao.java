package xy.repository.mysql;

import java.util.List;
import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.bean.CriteriaJoinable;
import x7.core.web.Pagination;



public class SyncDao implements ISyncDao{

	private static SyncDao instance;

	public static SyncDao getInstance() {
		if (instance == null) {
			instance = new SyncDao();
		}
		return instance;
	}

	private SyncDao() {

	}

	private ISyncDao dao;
	protected void setDao(ISyncDao dao){
		this.dao = dao;
	}

	@Override
	public long create(Object obj) {
		return this.dao.create(obj);
	}


	@Override
	public void remove(Object obj) {
		this.dao.remove(obj);
	}

	@Override
	public <T> List<T> list(Class<T> clz, long id) {
		return this.dao.list(clz, id);
	}

	@Override
	public <T> T get(Class<T> clz, long idOne, long idTwo) {
		return this.dao.get(clz, idOne, idTwo);
	}

	@Override
	public <T> List<T> list(Class<T> clz, String sql, List<Object> conditionList) {
		return this.dao.list(clz, sql, conditionList);
	}

	@Override
	public <T> List<T> list(Class<T> clz) {
		return this.dao.list(clz);
	}

	@Override
	public <T> long getMaxId(Class<T> clz, long key) {
		return this.dao.getMaxId(clz, key);
	}

	@Override
	public <T> long getMaxId(Class<T> clz) {
		return this.dao.getMaxId(clz);
	}

	@Override
	public <T> Pagination<T> list(Class<T> clz, long idOne, Pagination<T> pagination) {

		return this.dao.list(clz,idOne,pagination);
	}

	@Override
	public <T> List<T> list(Object conditionObj) {

		return this.dao.list(conditionObj);
	}

	@Override
	public <T> Pagination<T> list(Object conditionObj, Pagination<T> pagination) {

		return this.dao.list(conditionObj, pagination);
	}

	@Override
	public Pagination<Map<String,Object>> list(Criteria criteria, Pagination<Map<String,Object>> pagination) {

		return this.dao.list(criteria, pagination);
	}

	@Override
	public <T> long getCount(Class<T> clz, long idOne) {
		
		return this.dao.getCount(clz, idOne);
	}

	@Override
	public <T> T getOne(T obj, String orderBy, String sc) {
		
		return this.dao.getOne(obj, orderBy, sc);
	}

	@Override
	public long getMaxId(Object obj) {

		return this.dao.getMaxId(obj);
	}

	@Override
	public void refresh(Object obj) {
		
		this.dao.refresh(obj);
		
	}

	@Override
	public <T> T get(Class<T> clz, long idOne) {

		return this.dao.get(clz, idOne);
	}

	@Deprecated
	@Override
	public boolean execute(Object obj, String sql) {
		
		return this.dao.execute(obj, sql);
	}

	@Override
	public Object getSum(Object conditionObj, String sumProperty) {

		return this.dao.getSum(conditionObj, sumProperty);
	}
	
	@Override
	public long getCount(Object obj) {
		
		return this.dao.getCount(obj);
	}

	@Override
	public Object getSum(Object conditionObj, String sumProperty, Criteria criteria) {
		
		return this.dao.getSum(conditionObj, sumProperty, criteria);
	}

	@Override
	public boolean refresh(Object obj, Map<String, Object> conditionMap) {

		return this.dao.refresh(obj, conditionMap);
	}

	@Override
	public Object getCount(Object conditionObj, String countProperty, Criteria criteria) {
		return this.dao.getCount(conditionObj, countProperty, criteria);
	}

	@Override
	public <T> List<T> in(Class<T> clz, List<? extends Object> inList) {
		return this.dao.in(clz, inList);
	}

	@Override
	public <T> List<T> in(Class<T> clz, String inProperty, List<? extends Object> inList) {
		return this.dao.in(clz, inProperty, inList);
	}

	@Override
	public Pagination<Map<String, Object>> list(CriteriaJoinable criteriaJoinable,
			Pagination<Map<String, Object>> pagination) {
		return this.dao.list(criteriaJoinable, pagination);
	}
	
	
}
