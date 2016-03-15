package feiyizhan.weixin;

import java.util.ArrayList;
import java.util.List;

import blade.kit.StringKit;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.api.tuling.TulingUtil;
import feiyizhan.weixin.msg.handle.CmdTextMessageHandle;
import feiyizhan.weixin.msg.handle.MessageHandleImpl;
import feiyizhan.weixin.msg.handle.TextMesageHandle;
import feiyizhan.weixin.util.JSONUtil;
import feiyizhan.weixin.util.UserUtil;

public class AppControl {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppControl.class);
	
	private UserSession userSession ;
	
	/**
	 * 工具群，用于转发系统消息
	 */
	public JSONObject ToolsGroup = null;
	
	/**
	 * 自动答复用户清单（包括群）
	 */
	public JSONArray AutoReceiveUserList = new JSONArray();
	/**
	 * 管理员帐号清单
	 */
	public JSONArray MangerUsrList = new JSONArray();
	
	/**
	 * 关键字消息提醒-指定的群列表（
	 */
	public JSONArray RemindGroupList = new JSONArray();
	
	/**
	 * 关键字消息列表
	 */
	public List<String> keyWordList = new ArrayList<String>();
	
	/**
	 * 自动消息回复标志
	 */
	public boolean autoReceiveFlag=false;
	
	/**
	 * 消息自动转发标志
	 */
	public boolean forwordFlag =false;
	
	/**
	 * 关键字内容提醒
	 */
	public boolean keyWorkFlag =false;
	
	/**
	 * 消息处理器列表
	 */
	public List<MessageHandleImpl> handleList;
	
	
	/**
	 * 批处理处理线程
	 */
	public boolean batchFlag = false;
	
	public boolean isBatchFlag() {
		return batchFlag;
	}

	public void setBatchFlag(boolean batchFlag) {
		this.batchFlag = batchFlag;
	}

	public AppControl(UserSession userSession) {
		// TODO 自动生成的构造函数存根
		this.userSession=userSession;
		this.init();
	}

	/**
	 * 控制器初始化
	 */
	private void init(){
		this.handleList = new ArrayList<MessageHandleImpl>();
		//增加文本消息处理器
		this.handleList.add(new TextMesageHandle(this.userSession,this));
	}




	/**
	 * 获取最新消息
	 */
	public void handleMsg(JSONObject data){
		if(null == data){
			return;
		}
		
		JSONArray AddMsgList = data.getJSONArray("AddMsgList");
		
		for(int i=0,len=AddMsgList.size(); i<len; i++){
			LOGGER.info("[*] 你有新的消息，请注意查收");
			JSONObject msg = AddMsgList.getJSONObject(i);
			int msgType = msg.getInt("MsgType", 0);
			
			String content = msg.getString("Content");
			
			LOGGER.info("[*] msg:"+msg);
			LOGGER.info("[*] 消息类型:"+msgType);
			LOGGER.info("[*] content:"+content);
			
			for(MessageHandleImpl mh:this.handleList){
				if(mh.handleMessage(msg)){
					break;
				}
			}
			
//			switch (msgType){
//			case 51:{  //初始化消息
//				
//				break ;
//				}
//			case 1:{  //文本消息
//				doHandleTextMsg(msg);  //文本消息处理
//				break ;
//				}
//			case 3:{  //图片消息
//				//webwxsendmsg("小白还不支持图片呢", msg.getString("FromUserName"));
//				break ;
//				}
//			case 34:{  //语音消息
//				//webwxsendmsg("小白还不支持语音呢", msg.getString("FromUserName"));
//				break ;
//				}
//			case 42:{  //名片消息
////				String name = getUserName(msg.getString("FromUserName"),null);
////				LOGGER.info(name + " 给你发送了一张名片:");
////				LOGGER.info("=========================");
//				break ;
//				}
//			default :{
//				
//				break ;
//				}
//			
//			}
			
		
		}
	}
	

	
	

//	/**
//	 * 文本消息处理
//	 * @param msg
//	 */
//	private void doHandleTextMsg(JSONObject msg){
//		String fromUserID=msg.getString("FromUserName");
//		String toUserID=msg.getString("ToUserName");
//		String name = userSession.getUserName(fromUserID,null);
//		String content = msg.getString("Content");
//		String groupID ="";
//		String groupUserID = "" ;
//		
//		
//		LOGGER.info("[*] FromUserName:"+name);
//		LOGGER.info("[*] content:"+content);
//		if(userSession.SpecialUsers.contains(msg.getString("ToUserName"))){ //特殊用户的消息
//			return ;
//		} else if(userSession.User.getString("UserName").equals(fromUserID)){//自己的消息
//			//content = "@"+this.getUserRemarkName(this.User)+" " +content;  //作为命令执行
//			if(toUserID.startsWith("@@")){ //我在群内发消息
//				groupID=toUserID;
//				LOGGER.info("[*]【我在群里发了消息】|" + userSession.getUserName(toUserID,null) + "| " + content.replace("<br/>", "\n"));
//			}
//			
//			
//		} else if (fromUserID.startsWith("@@")) { //群内有人发消息
//			String[] peopleContent = content.split(":<br/>");
//			groupID=fromUserID;
//			groupUserID=peopleContent[0];
//			content=peopleContent[1];
//			LOGGER.info("[*]【群消息】|" + name + "| " + userSession.getUserName(groupUserID, groupID) + ":\n" + content.replace("<br/>", "\n"));
//		} else {
//			LOGGER.info(name + ": " + content);
//
//		}
//		
//		
//		if(null!=content){
//			if(userSession.User.getString("UserName").equals(fromUserID)){ //自己发送的消息(包括自己发自己的，自己在群里发的)
//				String cmdStr = content;
//				if(userSession.User.getString("UserName").equals(toUserID)){// 自己给自己发送的作为命令识别
//					if("##获取删除我的用户清单#".equals(cmdStr)){
//						List<String> list = this.getDeletedMeUserListWithCreatRoom();
//						userSession.webwxsendmsg("【系统消息】：\n 总共有【"+list.size()+"】个用户删除了我。清单如下：\n",userSession.User.getString("UserName"));
//						int count =0;
//						ArrayList<String> names = new ArrayList<String>();
//						for(String id:list){
//							count ++ ;
//							String userName = this.userSession.getUserName(id, null);
//							names.add(userName);
//							LOGGER.info("[*] "+id + "|"+userName);
//							if(count >=70){
//								count =0;
//								userSession.webwxsendmsg(names.toString()+"\n",userSession.User.getString("UserName"));
//								names = new ArrayList<String>();
//							}
//							
//						}
//						if(count >0){
//							userSession.webwxsendmsg(names.toString()+"\n",userSession.User.getString("UserName"));
//						}
//						userSession.webwxsendmsg("【系统消息】清单结束 \n",userSession.User.getString("UserName"));
//						
//					}else{
//						String receiveStr = doHandleCmdMsg(cmdStr,null);
//						userSession.webwxsendmsg("【系统消息】：\n"+receiveStr,userSession.User.getString("UserName"));
//						if(null!=this.ToolsGroup){
//							userSession.webwxsendmsg("【系统消息】：\n"+receiveStr,ToolsGroup.getString("UserName"));
//						}
//					}
//
//				}else{
//					if(cmdStr.startsWith("##")){  //群内指令，以##开始
//
//					}
//				}
//
//			}else{
//				if(content.startsWith("@"+UserUtil.getUserRemarkName(userSession.User)+" ")){ //@我的 暂时不处理
//
//		
//				}
//				if(this.autoReceiveFlag){ //消息自动答复
//					
//					if(JSONUtil.findIndexByKey(this.AutoReceiveUserList, "UserName", fromUserID)!=-1){  //在自动答复消息清单的，才自动答复
//						String ans = TulingUtil.tuling(name, content);
//						userSession.webwxsendmsg("【小白机器人】：\n"+ans, msg.getString("FromUserName"));
//						LOGGER.info("自动回复 " + ans);
//					}
//				}
//				if(this.forwordFlag==true){   //消息转发处理
//					for(int i=0;i<this.MangerUsrList.size();i++){
//						JSONObject usr =this.MangerUsrList.get(i).asObject();
//						userSession.webwxsendmsg("【转发消息】：\n"+
//								"From:"+name+"\n"+
//								"Content:"+content.replace("<br/>", "\n")
//								,usr.getString("UserName"));
//						
//						
//					}
//					LOGGER.info("消息转发: " + content);
//				}
//				if(this.keyWorkFlag){ //关键字消息提醒
//					for(int i=0;i<this.MangerUsrList.size();i++){
//						JSONObject usr =this.MangerUsrList.get(i).asObject();
//						for(String str:this.keyWordList){
//							if(content.indexOf(str)>=0){
//								userSession.webwxsendmsg(
//									"【重要消息提醒】：\n"+
//									"来自【"+name+"】的消息【"+content.replace("<br/>", "\n")+"】"
//									, usr.getString("UserName"));
//								break;
//							}
//						}
//						LOGGER.info("关键字消息提醒: " + content);
//					}
//				}
//			}
//			
//
//			
//
//		}else{
//			userSession.webwxsendmsg("【系统消息】：\n"+"亲，你多少得说点什么吧。", msg.getString("FromUserName"));
//			return ;
//		}
//		
//	}
	
//	
//	
//	/**
//	 * 命令消息处理
//	 * @param cmdConten
//	 * @return
//	 */
//	private String doHandleCmdMsg(String cmdStr,String groupID){
//		String[] cmds = cmdStr.split("[ ]");
//		if(cmds.length==2){
//			String cmd = cmds[0];
//			String val = cmds[1];
//			if("增加主控".equals(cmd)){
//				JSONObject manger =userSession.getMemberUserByName(val);
//				if(null!=manger){
//					if(JSONUtil.findIndexByKey(MangerUsrList, "UserName", manger.getString("UserName"))!=-1){
//						this.MangerUsrList.add(manger);
//					}
//					return "增加【"+val+"】主控成功";
//				}else{
//					return "【"+val+"】不在联系人清单";
//				}
//			}else if("删除主控".equals(cmd)){
//				JSONObject manger =userSession.getMemberUserByName(val);
//				if(null!=manger){
//					int index = JSONUtil.findIndexByKey(MangerUsrList, "UserName", manger.getString("UserName"));
//					if(index!=-1){
//						this.MangerUsrList.remove(index);
//					}
//					return "删除【"+val+"】主控成功";
//				}else{
//					return "【"+val+"】不在联系人清单";
//				}
//			}else if("增加提醒关键字".equals(cmd)){
//				if(!this.keyWordList.contains(val)){
//					this.keyWordList.add(val);
//				}
//				return "增加提醒关键字成功。当前关键字清单：\n"+this.keyWordList.toString();
//			}else if("删除提醒关键字".equals(cmd)){
//				this.keyWordList.remove(val);
//				return "删除提醒关键字成功。当前关键字清单：\n"+this.keyWordList.toString();
//			}else if("设置工具群".equals(cmd)){
//				JSONObject manger =userSession.getGroupByName(val);
//				if(null!=manger){
//					this.ToolsGroup=manger;
//					if(JSONUtil.findIndexByKey(MangerUsrList, "UserName", manger.getString("UserName"))!=-1){
//						this.MangerUsrList.add(manger);
//					}
//					return "设置【"+val+"】工具群成功。";
//				}else{
//					return "你没有加入【"+val+"】群";
//				}
//
//			}else if("增加监控群".equals(cmd)){
//				if("*ALL".equalsIgnoreCase(val)){
//					this.RemindGroupList = new JSONArray();
//					for(JSONValue v:userSession.GrouptList){
//						JSONObject obj = v.asObject();
//						if(obj.getString("UserName").startsWith("@@")){
//
//							this.RemindGroupList.add(obj);
//						}
//					}
//					return "已设置监控所有群消息";
//				}else{
//					JSONObject group =userSession.getGroupByName(val);
//					if (null!=group){
//						if(JSONUtil.findIndexByKey(RemindGroupList, "UserName", group.getString("UserName"))!=-1){
//							this.RemindGroupList.add(group);
//						}
//						
//						return "增加【"+val+"】群消息监控成功";
//					}else{
//						return "你没有加入【"+val+"】群";
//					}
//				}
//
//			}else if("删除监控群".equals(cmd)){
//				if("*ALL".equalsIgnoreCase(val)){
//					this.RemindGroupList = new JSONArray();
//					return "已取消监控所有群消息";
//				}else{
//					JSONObject group =userSession.getGroupByName(val);
//					if (null!=group){
//						int index =JSONUtil.findIndexByKey(RemindGroupList, "UserName", group.getString("UserName"));
//						if(index!=-1){
//							this.RemindGroupList.remove(index);
//						}
//						
//						return "删除【"+val+"】群消息监控成功";
//					}else{
//						return "你没有加入【"+val+"】群";
//					}
//				}
//	
//			}else{
//				return "不支持的命令";
//			}
//		}else if(cmds.length==1){
//			String cmd = cmds[0];
//			if("#开启自动答复#".equals(cmd)){
//				if(null!=groupID){
//					JSONObject group =JSONUtil.findObjectByKey(userSession.GrouptList, "UserName", groupID);
//					if (null!=group){
//						if(JSONUtil.findIndexByKey(AutoReceiveUserList, "UserName", groupID)!=-1){
//							this.RemindGroupList.add(group);
//						}
//						
//						return "已开启该群的自动答复";
//					}else{
//						return "你没有加入该群";
//					}
//				}else{
//					this.autoReceiveFlag=true;
//					return "开启了自动答复，如果要关闭，请输入【#关闭自动答复#】";
//				}
//
//			}else if("#关闭自动答复#".equals(cmd)){
//				if(null!=groupID){
//					JSONObject group =JSONUtil.findObjectByKey(userSession.GrouptList, "UserName", groupID);
//					if (null!=group){
//						int index = JSONUtil.findIndexByKey(AutoReceiveUserList, "UserName", groupID);
//						if(index!=-1){
//							this.RemindGroupList.remove(index);
//						}
//						
//						return "已关闭该群的自动答复";
//					}else{
//						return "你没有加入该群";
//					}
//				}else{
//					this.autoReceiveFlag=false;
//					return "关闭了自动答复，如果要开启，请输入【#开启自动答复#】";
//				}
//			}else if("#开启消息转发#".equals(cmd)){
//				this.forwordFlag=true;
//				return "开启消息转发，如果要关闭，请输入【#关闭消息转发#】";
//			}else if("#关闭消息转发#".equals(cmd)){
//				this.forwordFlag=false;
//				return "关闭消息转发，如果要开启，请输入【#开启消息转发#】";
//			}else if("#开启关键字提醒#".equals(cmd)){
//				this.keyWorkFlag=true;
//				return "开启关键字提醒，如果要关闭，请输入【#关闭关键字提醒#】";
//			}else if("#关闭关键字提醒#".equals(cmd)){
//				this.keyWorkFlag=false;
//				return "关闭关键字提醒，如果要开启，请输入【#开启关键字提醒#】";
//			}else if("#帮助#".equals(cmd)){
//				return this.getHelpContent();
//			}else if("#显示状态#".equals(cmd)){
//				return this.getStatus();	
//			}else{
//				return "命令不正确，请重新设置。";
//			}	
//			
//		}else{
//			return "命令不正确，请重新设置。";
//		}
//
//		
//		
//	}
	
	/**
	 * 返回状态
	 * @return
	 */
	public String getStatus(){
		StringBuilder sb = new StringBuilder();
		sb.append("当前登录用户【 "+UserUtil.getUserRemarkName(userSession.User)+"】\n");
		sb.append("当前用户有【"+userSession.MemberList.size()+"】个联系人\n");
		int groupCount =0;
		int otherCount =0;
		for(JSONValue val:userSession.GrouptList){
			JSONObject obj = val.asObject();
			if(obj.getString("UserName").startsWith("@@")){ //群
				groupCount++;
			}else{
				otherCount++;
			}
		}
		sb.append("当前用户有【"+groupCount+"】个群和【"+otherCount+"】个其他联系人\n");
		sb.append("当前提醒关键字清单【"+this.keyWordList+"】\n");
		
		String mangerString =JSONUtil.getStringValues(this.MangerUsrList, UserUtil.NAMES_KEYS);
		sb.append("当前总控用户清单【"+mangerString+"】\n");
		
		String groupString =JSONUtil.getStringValues(this.RemindGroupList, UserUtil.NAMES_KEYS);
		sb.append("当前监控消息的群清单【"+groupString+"】\n");
		
		String autoUserString =JSONUtil.getStringValues(this.AutoReceiveUserList, UserUtil.NAMES_KEYS);
		sb.append("当前自动答复消息的用户清单【"+autoUserString+"】\n");
		
		sb.append(null==this.ToolsGroup?"还未设置工具群\n":"当前工具群【"+UserUtil.getUserRemarkName(this.ToolsGroup)+"】\n");
		sb.append(this.autoReceiveFlag?"开启了自动答复\n":"没有开启自动答复\n");
		sb.append(this.forwordFlag?"开启了消息转发\n":"没有开启消息转发"+"\n");
		sb.append(this.keyWorkFlag?"开启了关键字提醒\n":"没有开启关键字提醒"+"\n");
		
		
		return sb.toString();
	}
	
	

	
}
