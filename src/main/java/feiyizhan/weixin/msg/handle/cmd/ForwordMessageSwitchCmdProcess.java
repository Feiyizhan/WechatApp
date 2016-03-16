package feiyizhan.weixin.msg.handle.cmd;

import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.CmdTextMessageHandle;

/**
 * 消息转发开关命令处理模块
 * @author Pluto Xu
 *
 */
public class ForwordMessageSwitchCmdProcess extends BaseCmdProcess {



	public ForwordMessageSwitchCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("消息转发到主控用户清单及工具群开关命令:\n");
		sb.append("--个人聊天模式\n");
		sb.append("【开启消息转发】:开启消息转发\n");
		sb.append("【关闭消息转发】:关闭消息转发\n");
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
			cmd = cmds[0];
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("开启消息转发".equals(cmd)){
			this.getHandle().getControl().forwordFlag=true;
			this.getHandle().getSession().webwxsendmsg("已开启消息转发，如果要关闭，请输入【关闭消息转发】",toUserID);
			return true;
		}else if("关闭消息转发".equals(cmd)){
			this.getHandle().getControl().forwordFlag=false;
			this.getHandle().getSession().webwxsendmsg("已关闭消息转发，如果要开启，请输入【开启消息转发】",toUserID);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("开启消息转发".equals(cmd)){
			return true;
		}else if("关闭消息转发".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}

}
