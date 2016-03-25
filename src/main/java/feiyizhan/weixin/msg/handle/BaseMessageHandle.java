package feiyizhan.weixin.msg.handle;

import blade.kit.json.JSONObject;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.util.MessageUtil;

public abstract class BaseMessageHandle implements MessageHandleImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageHandle.class);
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

	
	/**
	 * 过滤消息
	 * @param msg
	 * @return
	 */
	public boolean filterMessage(JSONObject msg){
		if(this.getSession().isSpaciaUser(MessageUtil.getFromUserID(msg))){ //特殊用户的消息
			LOGGER.info("[*]特殊用户消息:"+MessageUtil.getFromUserID(msg));
			return true; 
		} 
		return false;
	}
	
	
	/**
	 * 检测消息,检测通过返回true
	 * @param msg
	 * @return
	 */
	public boolean check(JSONObject msg){
		if(!isMe(msg)){
			return false;
		}
		if(this.filterMessage(msg)){
			return false;
		}
		return true;
	}
	
	/**
	 *  判断是否是本消息处理器的处理
	 * @return
	 */
	public boolean isMe(JSONObject msg) {
		// TODO 自动生成的方法存根
		return this.getMessageType()==MessageUtil.getMessageType(msg);
	}
}
