package feiyizhan.weixin.msg.handle;

import java.util.ArrayList;
import java.util.List;

import blade.kit.json.JSONObject;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.util.MessageUtil;

/**
 * 文本消息处理
 * @author Pluto Xu
 *
 */
public class TextMesageHandle extends BaseMessageHandle{
	private static final Logger LOGGER = LoggerFactory.getLogger(TextMesageHandle.class);

	/**
	 * 消息处理器列表
	 */
	public List<MessageHandleImpl> handleList;
	
	public TextMesageHandle(UserSession session, AppControl control) {
		super(session, control);
		// TODO 自动生成的构造函数存根
		this.handleList = new ArrayList<MessageHandleImpl>();
		//增加命令文本消息处理器
		this.handleList.add(new CmdTextMessageHandle(this.getSession(), this.getControl()));
		//增加普通文本消息处理器
		this.handleList.add(new NormalTextMessageHandle(this.getSession(), this.getControl()));
	}


	

	@Override
	public boolean handleMessage(JSONObject msg) {
		// TODO 自动生成的方法存根
		int msgType = MessageUtil.getMsgType(msg);
		if(msgType==1){
			if(this.filterMessage(msg)){  //消息过滤
				return true;
			}
			for(MessageHandleImpl mh:this.handleList){ //消息处理
				if(mh.handleMessage(msg)){
					return true;
				}
			}
			return false;
		}else{
			return false;
		}
		
	}
	
	/**
	 * 过滤消息
	 * @param msg
	 * @return
	 */
	private boolean filterMessage(JSONObject msg){
		if(this.getSession().isSpaciaUser(MessageUtil.getFromUserID(msg))){ //特殊用户的消息
			LOGGER.info("[*]特殊用户消息:"+MessageUtil.getFromUserID(msg));
			return true; 
		} 
		return false;
	}

}
