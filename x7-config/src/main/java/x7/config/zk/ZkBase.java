package x7.config.zk;

import java.util.List;

import org.apache.zookeeper.CreateMode;

import x7.core.keeper.IKeeper;

/**
 * ZkBase singleton 为基础的整个服务器集群的一致性而设置<br>
 * 如果连接其他Zk集群推荐创建新的ZkClient实例<br>
 * 
 * @author Sim
 *
 */
public class ZkBase {

	private static ZkClient zkClient = null;
	
	private static ZkBase instance = null;
	
	public static ZkBase getInstance() {
		if (instance == null) {
			instance = new ZkBase();
		}
		return instance;
	}
	
	public static void init(String address){
		zkClient = new ZkClient(address);
	}

	public void add(IKeeper keeper) {
		this.zkClient.add(keeper);
	}
	
	public void create(String path, String value, CreateMode mode) {
		this.zkClient.create(path, value, mode);
	}
	
	public void refresh(String path, String value) {
		this.zkClient.refresh(path, value);
	}
	
	public void remove(String path) {
		this.zkClient.remove(path);
	}
	
	public String get(String path) {
		return this.zkClient.get(path);
	}
	
	public List<String> getChildren(String path) {
		return this.zkClient.getChildren(path);
	}
}
