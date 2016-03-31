package feiyizhan.weixin.msg.handle.voice;

import blade.kit.json.JSONObject;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.text.TextMesageHandle;

/**
 * @author Pluto Xu
 *
 */
public abstract class VoiceMessageHandle extends BaseMessageHandle {
	private static final Logger LOGGER = LoggerFactory.getLogger(VoiceMessageHandle.class);
	/**
	 * 音频消息类型
	 */
	private static final int MESSAGE_TYPE= 34;
	
	
	public VoiceMessageHandle(UserSession session, AppControl control) {
		super(session, control);
		// TODO Auto-generated constructor stub
	}
	


	@Override
	public int getMessageType() {
		// TODO Auto-generated method stub
		return MESSAGE_TYPE;
	}

}
