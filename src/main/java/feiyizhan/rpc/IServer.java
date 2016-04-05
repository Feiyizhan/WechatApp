package feiyizhan.rpc;

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
}
