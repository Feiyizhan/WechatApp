package feiyizhan.weixin.msg.handle;

import blade.kit.json.JSONObject;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;

public abstract class BaseMessageHandle implements MessageHandleImpl {
	private UserSession session ;
	private AppControl control;
	
	public BaseMessageHandle( UserSession session,AppControl control){
		this.session = session;
		this.control=control;
	}
	
	
	
	public UserSession getSession() {
		return session;
	}



	public void setSession(UserSession session) {
		this.session = session;
	}



	public AppControl getControl() {
		return control;
	}



	public void setControl(AppControl control) {
		this.control = control;
	}



	@Override
	public abstract boolean handleMessage(JSONObject msg) ;
	

}
