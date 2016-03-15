package feiyizhan.weixin.msg.handle;

import blade.kit.json.JSONObject;

/**
 * 消息处理接口
 * @author Pluto Xu
 *
 */
public interface MessageHandleImpl {
	
	/**
	 *  消息处理方法
	 * @return
	 */
	public boolean handleMessage(JSONObject msg);

	
		
		
	
}
