package feiyizhan.weixin.msg.handle.image;

import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.BaseMessageHandle;

/**
 * 图片消息处理器基类
 * @author Pluto Xu
 *
 */
public abstract class ImageMessageHandle extends BaseMessageHandle {

	/**
	 * 图片消息类型
	 */
	private static final int MESSAGE_TYPE = 3;
	
	public ImageMessageHandle(UserSession session, AppControl control) {
		super(session, control);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMessageType() {
		// TODO Auto-generated method stub
		return MESSAGE_TYPE;
	}


}
