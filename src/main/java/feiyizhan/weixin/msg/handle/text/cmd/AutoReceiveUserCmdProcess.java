package feiyizhan.weixin.msg.handle.text.cmd;

import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;
import feiyizhan.weixin.util.UserUtil;

/**
 * 自动答复用户清单管理命令模块
 * @author Pluto Xu
 *
 */
public class AutoReceiveUserCmdProcess extends BaseCmdProcess {

	public AutoReceiveUserCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("============================\n");
		sb.append("自动答复用户清单管理命令:\n");
		sb.append("该命令用于管理自动答复的用户清单，即增加或删除自动答复的用户。若要自动答复生效，必须先开启自动答复。该命令对自己无效，但可以在不是自己的聊天界面里，在需要自动答复的消息签名加上#号的前缀，用来告知助手该消息需要自动答复。\n");
		sb.append("--在和自己的聊天界面下输入：\n");
		sb.append("【增加自动答复】:增加自动答复 用户名 （支持特殊命令参数*ALL -- 通讯录的所有人和活跃的群)\n");
		sb.append("【删除自动答复】:删除自动答复 用户名 （支持特殊命令参数*ALL -- 通讯录的所有人和活跃的群)\n");
		sb.append("--其他人的聊天界面,作用于当前用户或者群。\n");
		sb.append("【允许自动答复】: 允许自动答复\n");
		sb.append("【不允许自动答复】: 不允许自动答复 \n");
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
				
				user = this.getHandle().getSession().getUserByName(cmds[1], null);
				name =cmds[1];
				all = false;
			}
				
		}else if(cmds.length==1){
			cmd =  cmds[0];
			user=this.getHandle().getSession().getUserByID(toUserID, null);
			name =UserUtil.getUserName(user);
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("增加自动答复".equals(cmd)){
			if(all){
				this.getHandle().getControl().AutoReceiveUserList = this.getHandle().getSession().ContactList;
				for(JSONValue v:this.getHandle().getSession().GrouptList){
					JSONObject obj = v.asObject();
					if(UserUtil.getUserID(obj).startsWith("@@")){
						this.getHandle().getControl().AutoReceiveUserList.add(obj);
					}
				}
				this.getHandle().getSession().webwxsendmsg("已设置自动答复所有人和活跃群的消息",toUserID);
				
			}else{
				if (null!=user){
					UserUtil.add(this.getHandle().getControl().AutoReceiveUserList, user);
					this.getHandle().getSession().webwxsendmsg("设置【"+name+"】的消息自动答复成功",toUserID);	
				}else{
					this.getHandle().getSession().webwxsendmsg("你还没有【"+name+"】这个好友/群",fromUserID);
				}
			}
			return true ;
		}else if("删除自动答复".equals(cmd)){
			if(all){
				this.getHandle().getControl().AutoReceiveUserList = new JSONArray();
				this.getHandle().getSession().webwxsendmsg("已取消自动答复所有人和活跃群的消息",toUserID);
			}else{
				if (null!=user){
					UserUtil.remove(this.getHandle().getControl().AutoReceiveUserList, user);
					this.getHandle().getSession().webwxsendmsg("取消【"+name+"】的消息自动答复成功",toUserID);
				}else{
					this.getHandle().getSession().webwxsendmsg("你还没有【"+name+"】这个好友/群",fromUserID);
				}
			}
			return true;
		}else if("允许自动答复".equals(cmd)){

			if (null!=user){
				UserUtil.add(this.getHandle().getControl().AutoReceiveUserList, user);
				this.getHandle().getSession().webwxsendmsg("设置【"+name+"】的消息自动答复成功",toUserID);
			}else{
				this.getHandle().getSession().webwxsendmsg("你还没有【"+name+"】这个好友/群",fromUserID);
			}
			
			return true;
		}else if("不允许自动答复".equals(cmd)){

			if (null!=user){
				UserUtil.remove(this.getHandle().getControl().AutoReceiveUserList, user);
				this.getHandle().getSession().webwxsendmsg("取消【"+name+"】的消息自动答复成功",toUserID);
			}else{
				this.getHandle().getSession().webwxsendmsg("你还没有【"+name+"】这个好友/群",fromUserID);
			}
			
			return true;
		}else{
			return false;
		}
		
		
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("增加自动答复".equals(cmd)){
			return true;
		}else if("删除自动答复".equals(cmd)){
			return true;
		}else if("允许自动答复".equals(cmd)){
			return true;
		}else if("不允许自动答复".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}

}
