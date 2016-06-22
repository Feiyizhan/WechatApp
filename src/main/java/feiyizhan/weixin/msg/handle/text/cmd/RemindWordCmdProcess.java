package feiyizhan.weixin.msg.handle.text.cmd;

import feiyizhan.weixin.msg.handle.BaseMessageHandle;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;

/**
 * 提醒关键字清单管理命令处理模块
 * @author Pluto Xu
 *
 */
public class RemindWordCmdProcess extends BaseCmdProcess {



	public RemindWordCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("============================\n");
		sb.append("提醒关键字清单管理命令:\n");
		sb.append("该命令用于管理关键字提醒的清单。\n");
		sb.append("--在和自己的聊天界面下输入：\n");
		sb.append("【增加提醒关键字】:增加提醒关键字 关键字\n");
		sb.append("【删除提醒关键字】:删除提醒关键字 关键字\n");
		return sb.toString();
	}

	@Override
	public boolean process(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		String[] cmds = content.split("[ ]");
		String cmd=null;
		String val =null;
		if(cmds.length==2){
			if(!fromUserID.equals(toUserID)) return false;
			cmd =  cmds[0];
			val = cmds[1];
		}else if(cmds.length==1){
			return false;
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("增加提醒关键字".equals(cmd)){
			if(!this.getHandle().getControl().keyWordList.contains(val)){
				this.getHandle().getControl().keyWordList.add(val);
			}
			this.getHandle().sendSysTextMessage("增加提醒关键字成功。当前关键字清单：\n"+this.getHandle().getControl().keyWordList.toString());
			return true;
		}else if("删除提醒关键字".equals(cmd)){
			this.getHandle().getControl().keyWordList.remove(val);
			this.getHandle().sendSysTextMessage("删除提醒关键字成功。当前关键字清单：\n"+this.getHandle().getControl().keyWordList.toString());
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("增加提醒关键字".equals(cmd)){
			return true;
		}else if("删除提醒关键字".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}

}
