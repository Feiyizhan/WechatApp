package feiyizhan.weixin.msg.handle.text.cmd;

import java.util.ArrayList;

import blade.kit.StringKit;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;
import feiyizhan.weixin.util.UserUtil;

/**
 * 显示当前状态命令处理模块
 * @author Pluto Xu
 *
 */
public class ShowStatusCmdProcess extends BaseCmdProcess {

	public ShowStatusCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("============================\n");
		sb.append("显示当前登录用户的一些简单的信息，显示助手当前的状态。\n");
		sb.append("显示当前状态命令:\n");
		sb.append("--在和自己的聊天界面下输入：\n");
		sb.append("【显示状态】:显示状态  -- 显示当前助手的状态\n");
		return sb.toString();
	}

	@Override
	public boolean process(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		String[] cmds = content.split("[ ]");
		String cmd=null;
		if(cmds.length==2){
			return false;
		}else if(cmds.length==1){
			if(!fromUserID.equals(toUserID)) return false;
			cmd = cmds[0];
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("显示状态".equals(cmd)){
			this.getHandle().getSession().webwxsendmsg(this.getHandle().getControl().getStatus(),fromUserID);
			return true;	
		}else{
			return false;
		}	
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("显示状态".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}
	
	

}
