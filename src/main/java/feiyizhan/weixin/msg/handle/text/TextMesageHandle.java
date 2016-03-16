package feiyizhan.weixin.msg.handle.text;

import java.util.ArrayList;
import java.util.List;

import blade.kit.json.JSONObject;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.MessageHandleImpl;
import feiyizhan.weixin.util.MessageUtil;

/**
 * 文本消息处理
 * @author Pluto Xu
 *
 */
public abstract class TextMesageHandle extends BaseMessageHandle{
	private static final Logger LOGGER = LoggerFactory.getLogger(TextMesageHandle.class);
	/**
	 * 文本消息类型
	 */
	private static final int MESSAGE_TYPE_TEXT = 1;
	

	
	public TextMesageHandle(UserSession session, AppControl control) {
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
		// TODO 自动生成的方法存根
		return MESSAGE_TYPE_TEXT;
	}


}
