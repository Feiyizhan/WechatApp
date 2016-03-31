package feiyizhan.weixin.msg.handle.image;

import blade.kit.json.JSONObject;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;

/**
 * 普通图片消息处理器
 * @author Pluto Xu
 *
 */
public class NormalImageMessageHandle extends ImageMessageHandle {

	public NormalImageMessageHandle(UserSession session, AppControl control) {
		super(session, control);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean handleMessage(JSONObject msg) {
		// TODO Auto-generated method stub
		return false;
	}

}
