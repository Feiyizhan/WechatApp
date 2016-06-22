package feiyizhan.weixin.msg.handle.text.cmd;

import java.util.Map;
import java.util.TreeMap;

import blade.kit.json.JSONObject;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;
import feiyizhan.weixin.util.UserUtil;

/**
 * 提醒群主命令
 * @author Pluto Xu
 *
 */
public class RemindGroupOwnCmdProcess extends BaseCmdProcess {

	public RemindGroupOwnCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String help() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("============================\n");
		sb.append("群主提醒开关命令:\n");
		sb.append("该命令用于开启和设置群的广告提醒，在开启了群主提醒功能，并在指定的群设置了允许提醒的人员之后，当该群有人发送了名片消息或链接消息之后，将会自动@设置的提醒人员，提醒对方消息发送者有发送广告的嫌疑。\n");
		sb.append("--在和自己的聊天界面下输入：\n");
		sb.append("【开启群主提醒】:开启群主提醒\n");
		sb.append("【关闭群主提醒】:关闭群主提醒\n");
		sb.append("--在群聊天界面下输入：\n");
		sb.append("【允许提醒】:允许提醒 群主名称    -- 增加该群到群主广告提醒的群清单里，并设置群的广告消息提醒者为命令后面指定的人员名称。\n");
		sb.append("【不允许提醒】:不允许提醒 群主名称    -- 将该群从群主广告提醒的清单里移除。\n");
		return sb.toString();
	}

	@Override
	public boolean process(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO Auto-generated method stub
		String[] cmds = content.split("[ ]");
		String cmd=null;
		String val=null;
		JSONObject group =null;
		String groupName =null;
		
		if(cmds.length==2){
			cmd =cmds[0];
			val = cmds[1];
			group=this.getHandle().getSession().getUserByID(toUserID, null);
			groupName =UserUtil.getUserName(group);
		}else if(cmds.length==1){
			cmd =  cmds[0];
			group=this.getHandle().getSession().getUserByID(toUserID, null);
			groupName =UserUtil.getUserName(group);
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("开启群主提醒".equals(cmd)){
			if(!fromUserID.equals(toUserID)) return false;
			this.getHandle().getControl().remindGroupOwnSwitch =true;
			this.getHandle().sendSysTextMessage("已开启群主提醒，如果要关闭，请输入【关闭群主提醒】");
			return true;
		}else if("关闭群主提醒".equals(cmd)){
			if(!fromUserID.equals(toUserID)) return false;
			this.getHandle().getControl().remindGroupOwnSwitch =false;
			this.getHandle().sendSysTextMessage("已关闭群主提醒，如果要关闭，请输入【开启群主提醒】");
			return true;
		}else if("允许提醒".equals(cmd)){
			if(!toUserID.startsWith("@@")) return false;
			if (null!=group){
				JSONObject groupOwn = this.getHandle().getSession().getGroupMemberUserByName(group,val);
				
				if(groupOwn!=null){
					String groupOwnID = UserUtil.getUserID(groupOwn);
					if(this.getHandle().getControl().remindGroupOwnList.get(toUserID)!=null){
						this.getHandle().getControl().remindGroupOwnList.remove(toUserID);
						this.getHandle().getControl().remindGroupOwnList.put(toUserID, groupOwnID);
					}else{
						this.getHandle().getControl().remindGroupOwnList.put(toUserID, groupOwnID);
					}
					this.getHandle().sendSysTextMessage("增加【"+groupName+"】群的群主提醒功能成功");	
				}else{
					this.getHandle().sendSysTextMessage("【"+val+"】不在群里");	
				}

				
			}else{
				this.getHandle().sendSysTextMessage("你没有加入【"+groupName+"】群");
			}
			return true;
		}else if("不允许提醒".equals(cmd)){
			if(!toUserID.startsWith("@@")) return false;
			if (null!=group){
				this.getHandle().getControl().remindGroupOwnList.remove(toUserID);
				this.getHandle().sendSysTextMessage("删除【"+groupName+"】群的群主提醒功能成功");	
			}else{
				this.getHandle().sendSysTextMessage("你没有加入【"+groupName+"】群");
			}
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO Auto-generated method stub
		// TODO 自动生成的方法存根
		if("开启群主提醒".equals(cmd)){
			return true;
		}else if("关闭群主提醒".equals(cmd)){
			return true;
		}else if("允许提醒".equals(cmd)){
			return true;
		}else if("不允许提醒".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}

}
