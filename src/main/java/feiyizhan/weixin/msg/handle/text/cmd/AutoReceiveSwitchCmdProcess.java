package feiyizhan.weixin.msg.handle.text.cmd;

import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;

/**
 * 自动答复开关处理模块
 * @author Pluto Xu
 *
 */
public class AutoReceiveSwitchCmdProcess extends BaseCmdProcess {



	public AutoReceiveSwitchCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("============================\n");
		sb.append("自动答复开关命令:\n");
		sb.append("该命令仅在和自己聊天界面下有效。该命令是控制是否开启自动答复这个功能的。只有在该开启自动答复之后，允许自动答复命令才有效\n");
		sb.append("--在和自己的聊天界面下输入：\n");
		sb.append("【开启自动答复】:开启自动答复   -- 开启自动答复 \n");
		sb.append("【关闭自动答复】:关闭自动答复   -- 关闭自动答复\n");
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
			if(!fromUserID.equals(toUserID)) return false;  //该命令仅在个人聊天窗口有效
			cmd = cmds[0];
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("开启自动答复".equals(cmd)){
			this.getHandle().getControl().autoReceiveFlag=true;
			this.getHandle().getSession().webwxsendmsg("已开启自动答复，如果要关闭，请输入【关闭自动答复】",fromUserID);
			return true;

		}else if("关闭自动答复".equals(cmd)){
			this.getHandle().getControl().autoReceiveFlag=false;
			this.getHandle().getSession().webwxsendmsg("已关闭自动答复，如果要开启，请输入【开启自动答复】",fromUserID);
			return true;
			
		}else{
			return false;
		}
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("开启自动答复".equals(cmd)){
			return true;
		}else if("关闭自动答复".equals(cmd)){
			return true;
		}else{
			return false;
		}
		
	}

}
