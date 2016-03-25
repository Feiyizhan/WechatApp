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
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkMessageHandle.class);
	/**
	 * 链接消息类型
	 */
	private static final int MESSAGE_TYPE_LINK = 49;
	

	
	public LinkMessageHandle(UserSession session, AppControl control) {
		super(session, control);

	}

	@Override
	public int getMessageType() {
		// TODO Auto-generated method stub
		return MESSAGE_TYPE_LINK;
	}
	

}
