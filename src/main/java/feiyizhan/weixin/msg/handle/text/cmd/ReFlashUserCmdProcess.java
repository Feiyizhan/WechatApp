package feiyizhan.weixin.msg.handle.text.cmd;

import java.util.ArrayList;
import java.util.List;

import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;
import feiyizhan.weixin.util.UserUtil;

/**
 * 更新用户信息
 * @author Pluto Xu
 *
 */
public class ReFlashUserCmdProcess extends BaseCmdProcess {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReFlashUserCmdProcess.class);

	public ReFlashUserCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("更新用户信息命令:\n");
		sb.append("--群聊天模式\n");
		sb.append("【更新】:更新  -- 更新当前用户或者群\n");
		return sb.toString();
	}

	@Override
	public boolean process(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		String[] cmds = content.split("[ ]");
		String cmd=null;
		JSONObject user =this.getHandle().getSession().getUserByID(toUserID, null);
		if(cmds.length==2){
			return false;
		}else if(cmds.length==1){
			cmd = cmds[0];
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("更新".equals(cmd)){
			if(toUserID.startsWith("@@")){
				this.getHandle().getSession().reFlashGroup(user);
			}else{
				this.getHandle().getSession().reFlashContact(user);
			}
			
			return true;	
		}else{
			return false;
		}	
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("更新".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}
	

}
