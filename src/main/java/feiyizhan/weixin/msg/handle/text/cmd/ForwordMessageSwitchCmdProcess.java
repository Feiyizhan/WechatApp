package feiyizhan.weixin.msg.handle.text.cmd;

import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;

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
		sb.append("============================\n");
		sb.append("消息转发到主控用户清单及工具群开关命令:\n");
		sb.append("该命令将管理消息转发的功能的开启。当开启了消息转发功能之后，对于你接收到的所有的消息（包括群和个人消息）都将发送到你设置的工具群，主控用户清单的所有用户里。\n");
		sb.append("--在和自己的聊天界面下输入：\n");
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
			if(!fromUserID.equals(toUserID)) return false;
			cmd = cmds[0];
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("开启消息转发".equals(cmd)){
			this.getHandle().getControl().forwordFlag=true;
			this.getHandle().getSession().sendTextMessage("已开启消息转发，如果要关闭，请输入【关闭消息转发】",toUserID);
			return true;
		}else if("关闭消息转发".equals(cmd)){
			this.getHandle().getControl().forwordFlag=false;
			this.getHandle().getSession().sendTextMessage("已关闭消息转发，如果要开启，请输入【开启消息转发】",toUserID);
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
