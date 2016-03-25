package feiyizhan.weixin.msg.handle.link;

import java.util.List;

import blade.kit.TimeKit;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.text.TextMesageHandle;
import feiyizhan.weixin.util.MessageUtil;
import feiyizhan.weixin.util.UserUtil;

/**
 * 普通链接消息处理
 * @author Pluto Xu
 *
 */
public class NormalLinkMessageHandle extends LinkMessageHandle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NormalLinkMessageHandle.class);

	public NormalLinkMessageHandle(UserSession session, AppControl control) {
		super(session, control);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean handleMessage(JSONObject msg) {
		// TODO Auto-generated method stub
		if(!check(msg)){
			return false;
		}
		LOGGER.info("[*] 普通链接消息");
		
		String fromUserID=MessageUtil.getFromUserID(msg);
		String toUserID=MessageUtil.getToUserID(msg);
		String content = MessageUtil.getContent(msg);
		String currUserID = UserUtil.getUserID(this.getSession().User);
		String fromUserName = getSession().getUserName(fromUserID, null);
		//获取消息发送者
		List<String> contents = MessageUtil.resolveGroupContent(content);
		if(contents.size()!=2){  //不是群里其他人发的消息，不处理
			return false;
		}
		
		
		//群广告提醒群主功能
		if(this.getControl().remindGroupOwnSwitch){
			if(fromUserID.startsWith("@@")){
				String groupOwnID = getControl().remindGroupOwnList.get(fromUserID);
				
				if(groupOwnID!=null){  //在提醒清单的群才提醒
					JSONObject group = getSession().getGroup(fromUserID);
					//获取群主
					JSONObject groupOwn = getSession().getGroupMemberUserByID(group, groupOwnID);
					//获取群主名称
					String groupOwnName = UserUtil.getUserRemarkName(groupOwn);
					//获取消息发送者的ID
					String sendMsgUserID = contents.get(0);
					
					//获取消息发送者对象
					JSONObject sendMsgUser = getSession().getGroupMemberUserByID(group, sendMsgUserID);
					
					//获取消息发送者的名称
					String sendMsgUserName =UserUtil.getUserRemarkName(sendMsgUser);
					
					
					String receiveMsgStr = "@"+groupOwnName+" "+
										"广告提醒，【"+sendMsgUserName+
										"】在"+TimeKit.getCurrentTimeInString()+
										"有发送广告嫌疑"+
										"\n";
					//给群主发送提醒消息
					getSession().webwxsendmsg(receiveMsgStr, fromUserID);
				

				}
			}


			
		}
		
		return true;
	}

	
}
