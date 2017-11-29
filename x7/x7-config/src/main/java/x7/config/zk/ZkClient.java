package x7.config.zk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import x7.core.config.Configs;
import x7.core.keeper.IKeeper;
import x7.core.type.DataEventType;
import x7.core.util.JsonX;
import x7.core.util.KeyUtil;
import x7.core.util.StringUtil;

import org.apache.zookeeper.ZooKeeper;

public class ZkClient {

	private ZooKeeper zk = null;
	
	private Set<IKeeper> keeperSet = new HashSet<IKeeper>();
	
	public void add(IKeeper keeper) {
		this.keeperSet.add(keeper);
	}
	
	public ZkClient(String zkUrl) {
		try {// 192.168.1.68:2181
			System.out.println("zookeeper url: " + zkUrl);
			zk = new ZooKeeper(zkUrl, 500000, new Watcher() {
				// 监控所有被触发的事件
				public void process(WatchedEvent event) {

					EventType type = event.getType();

					if (type == null)
						return;
					
					DataEventType deType = KEY_MAP.get(type);
					if (deType == null)
						return;

					String path = event.getPath();

					if (StringUtil.isNullOrEmpty(path))
						return;

					List<String> keyList = KeyUtil.getKeyList(path);

					Object value = null;
					byte[] byteArr = null;
					try {
						if (type != EventType.NodeDeleted){
							byteArr = zk.getData(path, false, null);
						}
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String str = "";
					if (byteArr != null){
						str = new String(byteArr);
					}
					if ( str.startsWith("[")){
						value = JsonX.toList(str, String.class);
					}else if (str.contains("{") || str.contains("[")) {
						value = JsonX.toMap(str);
					} else {
						value = str;
					}

					System.out.println("------------ " + type);

					for (IKeeper keeper : keeperSet) {
						keeper.onChanged(deType, keyList, value);
					}

				}
			});
			zk.exists("/", true);// 观察这个节点发生的事件

		} catch (Exception e) {

			e.printStackTrace();
		}

		System.out.println("_________________________________");
		System.out.println("______ZK CLIENT IS WORKING O_____");
		System.out.println("_________________________________");

	}

	public ZkClient(IKeeper keeper) {
		
		keeperSet.add(keeper);
		
		try {// 192.168.1.68:2181, Configs.getString("ZK_URL")
			System.out.println(Configs.getString("ZK_URL"));
			zk = new ZooKeeper(Configs.getString("ZK_URL"), 500000, new Watcher() {
				// 监控所有被触发的事件
				public void process(WatchedEvent event) {

					EventType type = event.getType();

					if (type == null)
						return;
					
					DataEventType deType = KEY_MAP.get(type);
					if (deType == null)
						return;

					String path = event.getPath();

					if (StringUtil.isNullOrEmpty(path))
						return;

					List<String> keyList = KeyUtil.getKeyList(path);

					Object value = null;
					byte[] byteArr = null;
					try {
						if (type != EventType.NodeDeleted){
							byteArr = zk.getData(path, false, null);
						}
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String str = "";
					if (byteArr != null){
						str = new String(byteArr);
					}
					if ( str.startsWith("[")){
						value = JsonX.toList(str, String.class);
					}else if (str.contains("{") || str.contains("[")) {
						value = JsonX.toMap(str);
					} else {
						value = str;
					}

					System.out.println("------------ " + type);

					for (IKeeper keeper : keeperSet) {
						keeper.onChanged(deType, keyList, value);
					}

				}
			});
			zk.exists("/", true);// 观察这个节点发生的事件

		} catch (Exception e) {

			e.printStackTrace();
		}

		System.out.println("_________________________________");
		System.out.println("______ZK CLIENT IS WORKING O_____");
		System.out.println("_________________________________");

	}

	public static Map<EventType, DataEventType> KEY_MAP = new HashMap<EventType, DataEventType>() {
		{
			put(EventType.NodeCreated, DataEventType.CREATE);
			put(EventType.NodeDataChanged, DataEventType.REFRESH);
			put(EventType.NodeDeleted, DataEventType.REMOVE);
		}
	};

	/**
	 * 
	 * @param path
	 * @param value
	 * @param mode
	 *            CreateMode.PERSISTENT_SEQUENTIAL;
	 */
	public void create(String path, String value, CreateMode mode) {

		try {

			Stat s = zk.exists(path, true);
			if (s == null) {
				zk.create(path, value.getBytes(), Ids.OPEN_ACL_UNSAFE, mode);
//				s = zk.exists(path, true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refresh(String path, String value) {
		try {
			zk.setData(path, value.getBytes(), -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void remove(String path) {
		try {
//			zk.exists(path, true);
			List<String> list = zk.getChildren(path, true);

			if (list == null || list.isEmpty()) {
				zk.delete(path, -1);
				return;
			} else {
				for (String key : list) {
					System.out.println("remove ---- " + (path + "/" + key));
					remove(path + "/" + key);
				}
			}

			if (path.indexOf("/", 1) == -1){
//				zk.exists("/", true);
				System.out.println("remove ---- " + path);
				zk.delete(path, -1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String get(String path) {
		try {
			byte[] bytes = zk.getData(path, true, null);
			if (bytes == null)
				return null;
			return new String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> getChildren(String path) {
		try {
			return zk.getChildren(path, false);
		} catch (KeeperException | InterruptedException e) {
			
		}
		return null;
	}


}