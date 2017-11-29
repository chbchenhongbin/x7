package x7.core.web;

import java.util.Map;

public interface SignedRest {

	boolean isVerified();
	Map<String,Object> view(Object... obj);
	Map<String,Object> toast(String tips);
}
