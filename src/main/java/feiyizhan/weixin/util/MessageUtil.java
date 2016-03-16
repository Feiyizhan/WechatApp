package feiyizhan.weixin.util;

import blade.kit.json.JSONObject;

/**
 * 消息工具类
 * @author Pluto Xu
 *
 */
public class MessageUtil {
	/**
	 * 获取消息中的接收者用户ID
	 * @param msg
	 * @return
	 */
	public static String getToUserID(JSONObject msg){
		return msg.getString("ToUserName");
	}

	/**
	 * 获取消息中的发送者用户ID
	 * @param msg
	 * @return
	 */
	public static String getFromUserID(JSONObject msg){
		return msg.getString("FromUserName");
	}
	
	
	/**
	 * 获取消息内容
	 * @param msg
	 * @return
	 */
	public static String getContent(JSONObject msg){
		return msg.getString("Content");
	}
	
	/**
	 * 获取消息ID，如果没有获取到，返回-1
	 * @param msg
	 * @return
	 */
	public static int getMsgType(JSONObject msg){
		return msg.getInt("MsgType",-1);
	}
	

	/**
	 * 返回消息类型，如果没有获取到，返回-1
	 * @param msg
	 * @return
	 */
	public static int getMessageType(JSONObject msg){
		return  msg.getInt("MsgType", -1);
		
	}
}
