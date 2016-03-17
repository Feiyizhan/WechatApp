package feiyizhan.weixin.msg.handle.text.cmd;

import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;

/**
 * 关键字消息提醒开关命令处理模块
 * @author Pluto Xu
 *
 */
public class RemindSwitchCmdProess extends BaseCmdProcess {

	public RemindSwitchCmdProess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("关键字消息提醒开关命令:\n");
		sb.append("--个人聊天模式\n");
		sb.append("【开启关键字提醒】:开启关键字提醒\n");
		sb.append("【关闭关键字提醒】:关闭关键字提醒\n");
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
		
		if("开启关键字提醒".equals(cmd)){
			this.getHandle().getControl().keyWorkFlag=true;
			this.getHandle().getSession().webwxsendmsg("已开启关键字提醒，如果要关闭，请输入【关闭关键字提醒】",fromUserID);
			return true;
		}else if("关闭关键字提醒".equals(cmd)){
			this.getHandle().getControl().keyWorkFlag=false;
			this.getHandle().getSession().webwxsendmsg("已关闭关键字提醒，如果要开启，请输入【开启关键字提醒】",fromUserID);
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("开启关键字提醒".equals(cmd)){
			return true;
		}else if("关闭关键字提醒".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}

}
