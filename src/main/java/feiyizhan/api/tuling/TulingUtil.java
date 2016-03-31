package feiyizhan.api.tuling;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import blade.kit.http.HttpRequest;
import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;

/**
 * 图灵机器人
 * @author Pluto Xu
 *
 */
public class TulingUtil {
	
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TulingUtil.class);
    /**
     * 图灵机器人 API URL
     */
    public static final String TULING_API = "http://www.tuling123.com/openapi/api?";
 
    /**
     *  图灵机器人API Key
     */
    private static String TULING_KEY = "";
    
    /**
     * 100000-Text OK.
     * 200000 - URL OK.
     * 302000 - news OK.
     * 308000 - 菜谱 OK.
     * ********** - 服务器正在升级
     * 40001 - 参数key长度错误（应该是32位）
     * 40002 - 请求内容info为空
     * 40003 - key错误或帐号未激活
     * 40004 - 当天请求次数已使用完
     * 40005 - 暂不支持所请求的功能
     * 40006 - 图灵机器人服务器正在升级
     * 40007 - 数据格式异常
     * 
     */
	
	/**
	 * Code :  200000 - URL OK.
	 */
	public static final int CODE_URL =200000;
	/**
	 * Code : 100000-Text OK.
	 */
	public static final int CODE_TEXT =100000;
	/**
	 * Code : 302000 - news OK.
	 */
	public static final int CODE_NEWS =302000;
	/**
	 * Code : 308000 - 菜谱 OK.
	 */
	public static final int CODE_MENU =308000;
	
	/**
	 * 格式化消息
	 * @param data
	 * @return
	 */
	static{
		Properties config = new Properties();
		try {
			config.load(new BufferedInputStream(TulingUtil.class.getResourceAsStream("TuLingConfig.ini")));
			TULING_KEY =config.getProperty("TULING_KEY");
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			LOGGER.error("图灵机器人API配置数据缺失。",e.getMessage());
		}
		
	}
	
	private static String formatMsg(JSONObject data){
		StringBuilder sb = new StringBuilder();

//        Gson gson = new Gson();

		if(null !=data){
			switch(data.getInt("code", -1)){
			case CODE_TEXT:{ // 文本
				sb.append(replaceTag(data.getString("text")));
				break;
			}
			case CODE_URL :{ //URL
				sb.append(replaceTag(data.getString("text")));
				sb.append("\n");
				sb.append(data.getString("url"));
				break ;
			}
			case CODE_NEWS : {// 新闻
				sb.append(replaceTag(data.getString("text")));
				sb.append("\n");
				JSONArray list = data.getJSONArray("list");
				for(JSONValue val:list){
					JSONObject obj = val.asObject();
//					if(sb.length() >=600){
//						break;
//					}
					sb.append(obj.getString("source"));
					sb.append(":");
					sb.append(obj.getString("article"));
					sb.append("\n");
					sb.append(obj.getString("detailurl"));
					sb.append("\n");
					sb.append("\n");
				}
				
				
				break ;
			}
			case CODE_MENU: {//菜谱
				sb.append(replaceTag(data.getString("text")));
				sb.append("\n");
				JSONArray list = data.getJSONArray("list");
				for(JSONValue val:list){
					JSONObject obj = val.asObject();
//					if(sb.length() >=600){
//						break;
//					}
					sb.append("菜谱：");
					sb.append(obj.getString("name"));
					sb.append("\n");
					sb.append(obj.getString("icon"));
					sb.append("\n");
					sb.append(obj.getString("info"));
					sb.append("\n");
					sb.append("详情：");
					sb.append(obj.getString("detailurl"));
					sb.append("\n");
					sb.append("\n");
				}
				break ;
			}
			default :
				sb.append("哈哈，机器人抽风了，要不你再试试？");
				break ;
			
			}


		}else{
			
			sb.append("糟糕！！！机器人没电了，谁去帮忙充下电呀！！！");
		}
		
		return sb.toString();
	}
	
	
	/**
	 * 调用图灵API，返回API的返回内容
	 * @param userid
	 * @param info
	 * @return
	 */
	public static String tuling(String userid,String info){
		String url="";
		String result="";
		LOGGER.debug("[*] 图灵请求的内容："+userid+"<==>"+info);
		try {
			url = TULING_API + "key=" + TULING_KEY + 
						"&userid="+userid+
						"&info="+ URLEncoder.encode(info, "utf-8");
			HttpRequest http=HttpRequest.get(url);
			String tulingReult = http.body();
			http.disconnect();
			LOGGER.debug("[*] 图灵返回："+tulingReult);
			JSONObject jsonObject = JSON.parse(tulingReult).asObject();
			
			result= formatMsg(jsonObject);

        } catch (UnsupportedEncodingException e) {
            result =e.getMessage();
        }catch (Exception e) {
			// TODO 自动生成的 catch 块
			result =e.getMessage();
		}  
		
		
		
		return result;
		
	}
	
	/**
	 * 替换标签
	 * @param str
	 * @return
	 */
	public static String replaceTag(String str){
		if(str!=null){
			return str.replaceAll("<br>", "\n")
					.replaceAll("<br/>", "\n");
		}else{
			return null ;
		}
	}
	
	
	public static void main(String[] args){
//		LOGGER.info(tuling("feiyizhan","红烧牛肉"));
		LOGGER.debug(TULING_KEY);


       
	}
	
	
}
