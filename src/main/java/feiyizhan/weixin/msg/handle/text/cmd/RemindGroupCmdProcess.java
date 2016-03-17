package feiyizhan.weixin.msg.handle.text.cmd;

import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;
import feiyizhan.weixin.util.UserUtil;

/**
 * 消息提醒群清单管理命令处理模块
 * @author Pluto Xu
 *
 */
public class RemindGroupCmdProcess extends BaseCmdProcess {



	public RemindGroupCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("消息提醒群清单管理命令:\n");
		sb.append("--个人聊天模式\n");
		sb.append("【增加监控群】:增加监控群 群名称(支持特殊命令参数*ALL -- 所有群)\n");
		sb.append("【删除监控群】:删除监控群 群名称(支持特殊命令参数*ALL -- 所有群)\n");
		sb.append("群聊天模式,作用于当前群。\n");
		sb.append("【增加监控群】: 增加监控群\n");
		sb.append("【删除监控群】: 删除监控群 \n");
		return sb.toString();
	}

	@Override
	public boolean process(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		String[] cmds = content.split("[ ]");
		String cmd=null;
		JSONObject user =null;
		boolean all = false;
		String name ="";
		if(cmds.length==2){
			if(!fromUserID.equals(toUserID)) return false;
			cmd =  cmds[0];
			if(cmds[1].equalsIgnoreCase("*ALL")){
				all =true;
			}else{
				user = this.getHandle().getSession().getGroupByName(cmds[1]);
				name =cmds[1];
				all = false;
			}
				
		}else if(cmds.length==1){
			cmd =  cmds[0];
			user=this.getHandle().getSession().getUserByID(toUserID, null);
			name =UserUtil.getUserRemarkName(user);
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("增加监控群".equals(cmd)){
			if(all){
				this.getHandle().getControl().RemindGroupList = new JSONArray();
				for(JSONValue v:this.getHandle().getSession().GrouptList){
					JSONObject obj = v.asObject();
					if(UserUtil.getUserID(obj).startsWith("@@")){
						this.getHandle().getControl().RemindGroupList.add(obj);
					}
				}
				this.getHandle().getSession().webwxsendmsg("已设置监控所有群消息",toUserID);
				
			}else{
				if (null!=user){
					UserUtil.add(this.getHandle().getControl().RemindGroupList, user);
					this.getHandle().getSession().webwxsendmsg("增加【"+name+"】群消息监控成功",toUserID);	
				}else{
					this.getHandle().getSession().webwxsendmsg("你没有加入【"+name+"】群",fromUserID);
				}
			}
			return true ;
		}else if("删除监控群".equals(cmd)){
			if(all){
				this.getHandle().getControl().RemindGroupList = new JSONArray();
				this.getHandle().getSession().webwxsendmsg("已取消监控所有群消息",toUserID);
			}else{
				if (null!=user){
					UserUtil.remove(this.getHandle().getControl().RemindGroupList, user);
					this.getHandle().getSession().webwxsendmsg("删除【"+name+"】群消息监控成功",toUserID);
				}else{
					this.getHandle().getSession().webwxsendmsg("你没有加入【"+name+"】群",fromUserID);
				}
			}
			return true;

		}else{
			return false;
		}
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("增加监控群".equals(cmd)){
			return true;
		}else if("删除监控群".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}

}
