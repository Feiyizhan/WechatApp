package feiyizhan.rpc;

import blade.kit.json.JSONObject;

/**
 * HproseRPC 远程接口类
 * @author Pluto Xu
 *
 */
public interface  IServer {

	/**
	 * 设置Session对应的UUID
	 * @param session
	 * @param UUID
	 * @return
	 */
	public boolean setUUID(String session,String UUID);
	
	/**
	 * 返回Session对应的UUID
	 * @param session
	 * @return
	 */
	public String getUUID(String session);
	
	
	/**
	 * 设置当前登录用户
	 * @param user
	 * @return
	 */
	public boolean setLoginedUser(String session,String user);
	
	/**
	 * 获取当前登录用户
	 * @param session
	 * @return
	 */
	public String getLoginedUser(String session);
}
