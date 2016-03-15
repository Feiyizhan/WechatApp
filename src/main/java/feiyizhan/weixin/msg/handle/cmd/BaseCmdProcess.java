package feiyizhan.weixin.msg.handle.cmd;

import feiyizhan.weixin.msg.handle.CmdTextMessageHandle;

/**
 * 命令处理基础类
 * @author Pluto Xu
 *
 */
public abstract class BaseCmdProcess {

	/**
	 * 消息处理对象
	 */
	private CmdTextMessageHandle handle;
	
	public CmdTextMessageHandle getHandle() {
		return handle;
	}

	public void setHandle(CmdTextMessageHandle handle) {
		this.handle = handle;
	}

	public BaseCmdProcess(CmdTextMessageHandle handle) {
		// TODO 自动生成的构造函数存根
		this.handle =handle;
	}

	/**
	 * 获取帮助内容
	 * @return
	 */
	public abstract String help();
	
	/**
	 * 命令处理方法
	 * @param fromUserID
	 * @param toUserID
	 * @param content
	 * @param currUserID
	 * @return
	 */
	public abstract boolean process(String fromUserID,String toUserID,String content,String currUserID);
	
	/* （非 Javadoc）
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return help();
	}
	
	/**
	 * 返回是否是本命令处理
	 * @param cmd
	 * @return
	 */
	public abstract boolean isProcess(String cmd);
	
	
}
