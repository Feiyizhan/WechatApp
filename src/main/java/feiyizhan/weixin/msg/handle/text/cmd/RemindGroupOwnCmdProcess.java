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
		sb.append("群主提醒开关命令:\n");
		sb.append("【开启群主提醒】:开启群主提醒\n");
		sb.append("【关闭群主提醒】:关闭群主提醒\n");
		sb.append("【允许提醒】:允许提醒 群主名称\n");
		sb.append("【不允许提醒】:不允许提醒 群主名称\n");
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
			this.getHandle().getSession().webwxsendmsg("已开启群主提醒，如果要关闭，请输入【关闭群主提醒】",fromUserID);
			return true;
		}else if("关闭群主提醒".equals(cmd)){
			if(!fromUserID.equals(toUserID)) return false;
			this.getHandle().getControl().remindGroupOwnSwitch =false;
			this.getHandle().getSession().webwxsendmsg("已关闭群主提醒，如果要关闭，请输入【开启群主提醒】",fromUserID);
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
					this.getHandle().getSession().webwxsendmsg("增加【"+groupName+"】群的群主提醒功能成功",toUserID);	
				}else{
					this.getHandle().getSession().webwxsendmsg("【"+val+"】不在群里",toUserID);	
				}

				
			}else{
				this.getHandle().getSession().webwxsendmsg("你没有加入【"+groupName+"】群",fromUserID);
			}
			return true;
		}else if("不允许提醒".equals(cmd)){
			if(!toUserID.startsWith("@@")) return false;
			if (null!=group){
				this.getHandle().getControl().remindGroupOwnList.remove(toUserID);
				this.getHandle().getSession().webwxsendmsg("删除【"+groupName+"】群的群主提醒功能成功",toUserID);	
			}else{
				this.getHandle().getSession().webwxsendmsg("你没有加入【"+groupName+"】群",fromUserID);
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
