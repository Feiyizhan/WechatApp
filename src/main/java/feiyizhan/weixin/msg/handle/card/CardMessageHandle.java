package feiyizhan.weixin.msg.handle.card;

import blade.kit.json.JSONObject;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.BaseMessageHandle;

/**名片消息处理
 * @author Pluto Xu
 *
 */
public abstract class CardMessageHandle extends BaseMessageHandle {
	/**
	 * 名片消息类型
	 */
	private static final int MESSAGE_TYPE_CARD = 42;
	
	public CardMessageHandle(UserSession session, AppControl control) {
		super(session, control);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMessageType() {
		// TODO Auto-generated method stub
		return MESSAGE_TYPE_CARD;
	}


}
