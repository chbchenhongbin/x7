package x7.core.event;

import java.util.Objects;
import java.util.TreeMap;

import x7.core.mq.MessageEvent;

public class EventDispatcherX extends EventDispatcher{

//	@Override
	public static void dispatch(MessageEvent event)  {
		TreeMap<String, IEventListener> listenerMap = listenersMap
				.get(event.getType());
		if (listenerMap == null)
			return;
		
		System.out.println(event.getType() + ", listenerMap.size = " + listenerMap.size());
		
		for (IEventListener listener : listenerMap.values()) {
			if (listener != null) {
				try{
					if (event.getReTimes() == 0){
						listener.handle(event);
					}else if ( !Objects.isNull(event.getTag()) && event.getTag().equals(listener.getClass().getName())){
						listener.handle(event);
					}
					
				}catch (Exception e){
					
					MessageListenerException mle = new MessageListenerException("Exception, listener: " + listener  + ", event: " + event);
					mle.setTag(listener.getClass().getName());
					
					throw mle;
				}
			}
		}

	}
}
