package feiyizhan.weixin.msg.handle.text;

import java.util.ArrayList;
import java.util.List;

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
		if(!check(msg)){
			return false;
		}
		
		String fromUserID=MessageUtil.getFromUserID(msg);
		String toUserID=MessageUtil.getToUserID(msg);
		String content = MessageUtil.getContent(msg);
		String currUserID = UserUtil.getUserID(this.getSession().User);
		String fromUserName = getSession().getUserName(fromUserID, null);
		if(getControl().autoReceiveFlag){ //消息自动答复
		
			if(UserUtil.isFoundByID(getControl().AutoReceiveUserList, fromUserID)){  //在自动答复消息清单的，才自动答复
				List<String> contents = resolveContent(content,fromUserID);
				if(contents.size()==2){
					fromUserName = contents.get(0);
					content = contents.get(1);
				}else{
					content = contents.get(0);
				}
				
				String ans = TulingUtil.tuling(fromUserName, content);
				getSession().webwxsendmsg("【小白机器人】：\n"+ans, fromUserID);
				
				
			}
		}
		if(getControl().forwordFlag==true){   //消息转发处理
			for(int i=0;i<getControl().MangerUsrList.size();i++){
				JSONObject user =getControl().MangerUsrList.get(i).asObject();
				List<String> contents = resolveContent(content,fromUserID);
				if(contents.size()==2){
					fromUserName = contents.get(0);
					content = contents.get(1);
				}else{
					content = contents.get(0);
				}

				getSession().webwxsendmsg("【转发消息】：\n"+
						"From:"+fromUserName+"\n"+
						"Content:"+content
						,UserUtil.getUserID(user));
			}
		}
		
		if(getControl().keyWorkFlag){ //关键字消息提醒
			for(int i=0;i<getControl().MangerUsrList.size();i++){
				JSONObject user =getControl().MangerUsrList.get(i).asObject();
				List<String> contents = resolveContent(content,fromUserID);
				if(contents.size()==2){
					fromUserName = contents.get(0);
					content = contents.get(1);
				}else{
					content = contents.get(0);
				}
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

	/**
	 * 解析消息，如果是群消息，拆分为群两部分，一部分为群成员名称，一部分为内容。
	 * @param content
	 * @param fromUserID
	 * @return
	 */
	private List<String> resolveContent(String content,String fromUserID){
		List<String> contents = new ArrayList<String>();
		
		if(fromUserID.startsWith("@@")){  // 群消息
			contents = MessageUtil.resolveGroupContent(content);
			if(contents.size()==2){
				String val = contents.get(1).replace("<br/>", "\n").replaceAll("<[.[^<]]*>","");
				String id = contents.get(0);
				contents= new ArrayList<String>();
				JSONObject member = getSession().getUserByID(id, fromUserID);
				if(member!=null){
					contents.add(UserUtil.getUserName(member));
					contents.add(val);
				}else{
					contents.add(id);
					contents.add(val);
				}
			}else if(contents.size()==1){
				String val = contents.get(0).replace("<br/>", "\n").replaceAll("<[.[^<]]*>","");
				contents.remove(0);
				contents.add(val);
					
			}else{
				contents.add(content.replace("<br/>", "\n").replaceAll("<[.[^<]]*>",""));
			}
		}else{
			contents.add(content.replace("<br/>", "\n").replaceAll("<[.[^<]]*>",""));
		}
		
		return contents;
	}

}
