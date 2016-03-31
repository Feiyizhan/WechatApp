package feiyizhan.weixin.msg.handle.text.cmd;

import blade.kit.json.JSONObject;
import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;
import feiyizhan.weixin.util.UserUtil;

/**
 * 主控命令处理模块
 * @author Pluto Xu
 *
 */
public class MangerCmdProcess extends BaseCmdProcess {



	public MangerCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}



	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("============================\n");
		sb.append("主控用户清单管理命令:\n");
		sb.append("该命令用于增加主控用户清单，目前主控用户清单的用户，在开启了消息转发的功能之后，当前登录帐号的所有的消息都会被转发给主控用户清单的用户。\n");
		sb.append("--在和自己的聊天界面下输入：\n");
		sb.append("【增加主控】: 增加主控 主控帐号名称\n");
		sb.append("【删除主控】: 删除主控 主控帐号名称\n");
		sb.append("--其他人的聊天界面,作用于当前用户或者群。\n");
		sb.append("【增加主控】: 增加主控 \n");
		sb.append("【删除主控】: 删除主控 \n");
		return sb.toString();
	}
	


	@Override
	public boolean process(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		String[] cmds = content.split("[ ]");
		String cmd=null;
		JSONObject manger =null;
		String name ="";
		if(cmds.length==2){
			if(!fromUserID.equals(toUserID)) return false;
			cmd =  cmds[0];
			manger = this.getHandle().getSession().getContactUserByName(cmds[1]);
			name = cmds[1];
		}else if(cmds.length==1){
			cmd =  cmds[0];
			manger=this.getHandle().getSession().getUserByID(toUserID, null);
			name =UserUtil.getUserName(manger);
		}else{
			return false;
		}

		if(!isProcess(cmd)){
			return false;
		}
		
		if("增加主控".equals(cmd)){
			if(null!=manger){
				UserUtil.add(this.getHandle().getControl().MangerUsrList, manger);
				this.getHandle().getSession().sendTextMessage("增加【"+name+"】主控成功",toUserID);
			}else{
				this.getHandle().getSession().sendTextMessage("【"+name+"】不在联系人清单",fromUserID);
			}
			return true;
			
		}else if("删除主控".equals(cmd)){
			if(null!=manger){
				UserUtil.remove(this.getHandle().getControl().MangerUsrList, manger);
				this.getHandle().getSession().sendTextMessage("删除【"+name+"】主控成功",toUserID);
			}else{
				this.getHandle().getSession().sendTextMessage("【"+name+"】不在联系人清单",fromUserID);
			}
			return true;
		}else{
			return false;
		}
	}



	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("增加主控".equals(cmd)){
			return true;
		}else if("删除主控".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}

}
