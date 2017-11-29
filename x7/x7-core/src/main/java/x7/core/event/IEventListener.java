package x7.core.event;

import java.util.EventListener;


/**
 * <li>无状态事件监听器</li><br>
 * 多线程事件框架,必须考虑逻辑上的先后顺序，多任务导致的可能的冲突<br>
 * 在处理事件上，框架保证事件的先进先出
 * @author Wangyan
 *
 */
public interface IEventListener extends EventListener{

	/**
	 * 监听器被设计为无状态的服务端模式，不根据实例数重复添加监听器<br>
	 * 因此，任何对象必须从event里获得，举例：<br>
	 * HeroEvent he = (HeroEvent) event;<br>
	 * Scene scene = he.getHero().getScene();<br>
	 * 特别提醒： <br>
	 * 内部类可以工作，但设计上导致不可以直接访问外部类的属性,语法上无法检测<br>
	 * 不建议使用匿名内部类
	 * @param event
	 */
	void handle(IEvent event);

}



