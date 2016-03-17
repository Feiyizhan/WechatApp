package feiyizhan.weixin.msg.handle.text.cmd;

import blade.kit.StringKit;
import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;

/**
 * 帮助命令处理模块
 * @author Pluto Xu
 *
 */
public class HelpCmdProcess extends BaseCmdProcess {



	public HelpCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("帮助命令:\n");
		sb.append("--个人聊天模式\n");
		sb.append("【帮助】:帮助  -- 显示当前所有支持的命令的帮助信息\n");
		sb.append("【帮助】:帮助 命令名称 -- 显示命令名称的帮助信息  \n");
		return sb.toString();
	}

	@Override
	public boolean process(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		if(!fromUserID.equals(toUserID)) return false;
		
		String[] cmds = content.split("[ ]");
		String cmd=null;
		String val ="" ;
		if(cmds.length==2){
			cmd = cmds[0];
			val = cmds[1];
		}else if(cmds.length==1){
			cmd = cmds[0];
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("帮助".equals(cmd)){
			 if(!StringKit.isNotBlank(val)){ //不带参数的，显示所有命令的帮助
				StringBuilder sb = new StringBuilder();
				for(BaseCmdProcess bp:this.getHandle().getProcessList()){
					sb.append(bp.help());
				}
				this.getHandle().getSession().webwxsendmsg(sb.toString(),toUserID);
			 }else{ //显示指定命令的参数
				StringBuilder sb = new StringBuilder();
				for(BaseCmdProcess bp:this.getHandle().getProcessList()){
					if(bp.isProcess(val)){
						sb.append(bp.help());
					}
				}
				this.getHandle().getSession().webwxsendmsg(sb.toString(),toUserID);
			 }
				
			return true;
		}else{
				return false;
		}
		
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("帮助".equals(cmd)){
			return true;
		}else{
			return false;
		}

	}

}
