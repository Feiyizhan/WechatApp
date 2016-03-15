package feiyizhan.weixin.msg.handle;

import java.util.ArrayList;
import java.util.List;

import blade.kit.StringKit;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.AppControl;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.cmd.AutoReceiveSwitchCmdProcess;
import feiyizhan.weixin.msg.handle.cmd.AutoReceiveUserCmdProcess;
import feiyizhan.weixin.msg.handle.cmd.BaseCmdProcess;
import feiyizhan.weixin.msg.handle.cmd.ForwordMessageSwitchCmdProcess;
import feiyizhan.weixin.msg.handle.cmd.HelpCmdProcess;
import feiyizhan.weixin.msg.handle.cmd.MangerCmdProcess;
import feiyizhan.weixin.msg.handle.cmd.RemindGroupCmdProcess;
import feiyizhan.weixin.msg.handle.cmd.RemindSwitchCmdProess;
import feiyizhan.weixin.msg.handle.cmd.RemindWordCmdProcess;
import feiyizhan.weixin.msg.handle.cmd.ShowDeletedListCmdProcess;
import feiyizhan.weixin.msg.handle.cmd.ShowStatusCmdProcess;
import feiyizhan.weixin.util.JSONUtil;
import feiyizhan.weixin.util.MessageUtil;
import feiyizhan.weixin.util.UserUtil;

public class CmdTextMessageHandle extends BaseMessageHandle {
	private static final Logger LOGGER = LoggerFactory.getLogger(CmdTextMessageHandle.class);
	/**
	 * 命令处理模块清单
	 */
	private List<BaseCmdProcess> processList ;
	
	public CmdTextMessageHandle(UserSession session,AppControl control){
		super(session, control);
		this.processList = new ArrayList<BaseCmdProcess>();
		this.processList.add(new MangerCmdProcess(this));  //注册主控命令处理模块
		this.processList.add(new RemindWordCmdProcess(this)); //注册提醒关键字清单管理命令模块
		this.processList.add(new RemindGroupCmdProcess(this)); //注册消息提醒群清单管理命令模块
		this.processList.add(new AutoReceiveSwitchCmdProcess(this)); //注册消息自动答复开关命令模块
		this.processList.add(new ForwordMessageSwitchCmdProcess(this)); //注册消息转发开关命令模块
		this.processList.add(new RemindSwitchCmdProess(this)); // 注册消息关键字消息提醒开关命令模块
		this.processList.add(new HelpCmdProcess(this)); // 注册帮助命令模块
		this.processList.add(new ShowStatusCmdProcess(this)); //注册显示当前状态命令处理模块
		this.processList.add(new ShowDeletedListCmdProcess(this)); //注册显示删除我的用户清单命令处理模块
		this.processList.add(new AutoReceiveUserCmdProcess(this)); //注册自动答复用户清单管理命令处理模块
		
	}
	
	/**
	 * 获取当前注册的命令处理模块清单
	 * @return
	 */
	public List<BaseCmdProcess> getProcessList(){
		return this.processList;
	}
	
	@Override
	public boolean handleMessage(JSONObject msg) {
		// TODO 自动生成的方法存根
		String fromUserID=MessageUtil.getFromUserID(msg);
		String toUserID=MessageUtil.getToUserID(msg);
		String content = MessageUtil.getContent(msg);
		String currUserID = UserUtil.getUserID(this.getSession().User);
		if(!StringKit.isNotBlank(content)){
			return false;
		}
		
		if(currUserID.equals(fromUserID)){//自己发出的消息
			if(currUserID.equals(toUserID)){//接收者是自己
				return this.handleMyself(fromUserID, toUserID, content, currUserID);
			}else{  //接收者是其他人
				if(toUserID.startsWith("@@")){ // 接收者是群
					return this.handleGroup(fromUserID, toUserID, content, currUserID);
				}else{
					return false;
				}
				
			}
		}else{  //其他人发送消息
			
			if(fromUserID.startsWith("@@")){//其他 人在群里@我的
				String[] peopleContent = content.split(":<br/>");
			}else{//其他人私信我的
				
			}
			
			
		}
		
		return false;
	}
	
	/**
	 * 群消息处理
	 * @param fromUserID
	 * @param toUserID
	 * @param content
	 * @param currUserID
	 * @return
	 */
	private boolean handleGroup(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		String[] cmds = content.split("[ ]");
		for(BaseCmdProcess process:this.processList){
			if(process.process(fromUserID, toUserID, content, currUserID)){
				return true;
			}
		}
		return false;

		
	}

	/**
	 * 处理自己发给自己的消息
	 * @param fromUserID
	 * @param toUserID
	 * @param content
	 * @param currUserID
	 * @return
	 */
	private boolean handleMyself(String fromUserID,String toUserID,String content,String currUserID){
		for(BaseCmdProcess process:this.processList){
			if(process.process(fromUserID, toUserID, content, currUserID)){
				return true;
			}
		}
		return false;
	}
	
	
	


	

	
	
}
