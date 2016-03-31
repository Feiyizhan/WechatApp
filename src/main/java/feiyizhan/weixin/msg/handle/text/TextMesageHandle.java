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
	private static final int MESSAGE_TYPE = 1;
	
	
	public TextMesageHandle(UserSession session, AppControl control) {
		super(session, control);

	}
	
	@Override
	public int getMessageType() {
		// TODO Auto-generated method stub
		return MESSAGE_TYPE;
	}
	

}
