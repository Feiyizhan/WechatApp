package feiyizhan.weixin.msg.handle.link;

import blade.kit.json.JSONObject;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.text.TextMesageHandle;
import feiyizhan.weixin.util.MessageUtil;

/**
 * 链接消息处理
 * @author Pluto Xu
 *
 */
public abstract class LinkMessageHandle extends BaseMessageHandle {
	private static final Logger LOGGER = LoggerFactory.getLogger(TextMesageHandle.class);
	/**
	 * 链接消息类型
	 */
	private static final int MESSAGE_TYPE_LINK = 49;
	

	
	public LinkMessageHandle(UserSession session, AppControl control) {
		super(session, control);

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
	

	@Override
	public int getMessageType() {
		// TODO Auto-generated method stub
		return MESSAGE_TYPE_LINK;
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

}
