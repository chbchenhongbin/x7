package x7.core.repository;

import java.nio.ByteBuffer;

public interface ISerialWR {

	<T> T read(ByteBuffer buffer) throws Exception;
	
	<T> ByteBuffer write(Object t) throws Exception;
}
