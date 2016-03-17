package feiyizhan.weixin.msg.handle.text;

import blade.kit.json.JSONObject;
import feiyizhan.api.tuling.TulingUtil;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.util.MessageUtil;
import feiyizhan.weixin.util.UserUtil;

public class NormalTextMessageHandle extends TextMesageHandle {

	public NormalTextMessageHandle(UserSession session, AppControl control) {
		super(session, control);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public boolean handleMessage(JSONObject msg) {
		// TODO 自动生成的方法存根
		if(!isMe(msg)){
			return false;
		}
		if(this.filterMessage(msg)){
			return false;
		}
		
		String fromUserID=MessageUtil.getFromUserID(msg);
		String toUserID=MessageUtil.getToUserID(msg);
		String content = MessageUtil.getContent(msg);
		String currUserID = UserUtil.getUserID(this.getSession().User);
		String fromUserName = getSession().getUserName(fromUserID, null);
		if(getControl().autoReceiveFlag){ //消息自动答复
		
			if(UserUtil.isFoundByID(getControl().AutoReceiveUserList, fromUserID)){  //在自动答复消息清单的，才自动答复
				
				String ans = TulingUtil.tuling(fromUserName, content);
				getSession().webwxsendmsg("【小白机器人】：\n"+ans, fromUserID);
			}
		}
		if(getControl().forwordFlag==true){   //消息转发处理
			for(int i=0;i<getControl().MangerUsrList.size();i++){
				JSONObject user =getControl().MangerUsrList.get(i).asObject();
				getSession().webwxsendmsg("【转发消息】：\n"+
						"From:"+fromUserName+"\n"+
						"Content:"+content.replace("<br/>", "\n")
						,UserUtil.getUserID(user));

			}
		}
		
		if(getControl().keyWorkFlag){ //关键字消息提醒
			for(int i=0;i<getControl().MangerUsrList.size();i++){
				JSONObject user =getControl().MangerUsrList.get(i).asObject();
				for(String str:getControl().keyWordList){
					if(content.indexOf(str)>=0){
						getSession().webwxsendmsg(
							"【重要消息提醒】：\n"+
							"来自【"+fromUserName+"】的消息【"+content.replace("<br/>", "\n")+"】"
							, UserUtil.getUserID(user));
						break;
					}
				}
			}
		}
		
	
		return true;
	}

}
