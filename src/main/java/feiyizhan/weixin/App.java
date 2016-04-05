package feiyizhan.weixin;

import java.awt.EventQueue;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;

import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.api.tuling.TulingUtil;
import feiyizhan.rpc.RpcClient;
import feiyizhan.weixin.util.CookieUtil;
import feiyizhan.weixin.util.JSONUtil;
import feiyizhan.weixin.util.JSUtil;
import feiyizhan.weixin.util.Matchers;

/**
 * Hello world!
 *
 */
public class App {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	private UserSession userSession = new UserSession();
	private AppControl appControl = new AppControl(userSession);
	private String sessionID = "system";
	
	public QRCodeFrame qrCodeFrame;
	
	public App() {
		System.setProperty("jsse.enableSNIExtension", "false");
	}
	
	
	
	/**
	 * 显示二维码
	 * @return
	 */
	public void showQrCode() {
		
		String url = "https://login.weixin.qq.com/qrcode/" + userSession.uuid;
		
		final File output = new File("temp.jpg");
		
		HttpRequest.post(url, true, 
				"t", "webwx", 
				"_" , DateKit.getCurrentUnixTime())
				.receive(output);

		if(null != output && output.exists() && output.isFile()){
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
						qrCodeFrame = new QRCodeFrame(output.getPath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	
	
	private void closeQrWindow() {
		qrCodeFrame.dispose();
	}
	
	
	

	

	
	/**
	 * 监控消息
	 */
	public void listenMsgMode(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("[*] 进入消息监听模式 ...");
				int playWeChat = 0;
				boolean is_exit = false;
				while(!is_exit){
					
					int[] arr = userSession.syncCheck();
					
					LOGGER.info("[*] retcode={},selector={}", arr[0], arr[1]);
					
					if(arr[0] == 1100){
						LOGGER.info("[*] 你在手机上登出了微信，再见");
						appControl.setBatchFlag(false);
						RpcClient.RPC.setLoginedUser(sessionID, null);
						RpcClient.RPC.setUUID(sessionID, null);
						arr = userSession.syncCheck();
						is_exit=true;
					}
					
					if(arr[0] == 0){
						if(arr[1] == 2){
							JSONObject data = userSession.webwxsync();
							appControl.handleMsg(data);
						} else if(arr[1] == 6){
							JSONObject data = userSession.webwxsync();
							appControl.handleMsg(data);
						} else if(arr[1] == 7){
							playWeChat += 1;
							LOGGER.info("[*] 你在手机上玩微信被我发现了{}次", playWeChat);
							userSession.webwxsync();
						} else if(arr[1] == 3){
						} else if(arr[1] == 0){

						}
					} 
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, "listenMsgMode").start();
	}
	
	public static void main(String[] args){

		try {
			LOGGER.info("[*] App 启动");

			App app = new App();
			String uuid = app.userSession.getUUID();
			if(null == uuid){
				LOGGER.info("[*] uuid获取失败");
			} else { 
				if(args!=null && args.length==1 && StringKit.isNotBlank(args[0])){  //后台模式
					app.sessionID = args[0].trim().toUpperCase();

				}
				
				if(app.sessionID.equals("system")){ //前台模式
					app.showQrCode();
				}
				
				LOGGER.debug("[*] sessionID："+app.sessionID );
				
				if(!app.appControl.sendUUID(uuid,app.sessionID)){  //发送UUID
					LOGGER.info("RPC服务器通讯失败。");
					return ;
				}
				
				if(!app.userSession.login()){
					LOGGER.info("微信登录失败");
					if(app.sessionID.equals("system")){ //前台模式
						app.closeQrWindow();
					}
					return;
				}else{
					if(app.sessionID.equals("system")){ //前台模式
						app.closeQrWindow();
					}
				}
				


				
				LOGGER.info("[*] 微信登录成功");
				
				if(!app.userSession.wxInit()){
					LOGGER.info("[*] 微信初始化失败");
					return;
				}
				
				LOGGER.info("[*] 微信初始化成功");
				
				if(!app.appControl.saveLoginUser(app.sessionID)){
					LOGGER.info("[*] 保存登录信息失败。");
					return ;
				}
				
					
				if(!app.userSession.wxStatusNotify()){
					LOGGER.info("[*] 开启状态通知失败");
					return;
				}
				
				LOGGER.info("[*] 开启状态通知成功");
				
				if(!app.userSession.getContact()){
					LOGGER.info("[*] 获取联系人失败");
					return;
				}

				
				//发送帮助消息
//			app.userSession.webwxsendmsg(app.appControl.getHelpContent(), app.userSession.User.getString("UserName"));
				
				// 监听消息
				app.listenMsgMode();
				
				//mvn exec:java -Dexec.mainClass="me.biezhi.weixin.App"
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("[*]系统异常退出，原因："+e.getMessage());
			System.exit(0);
		}
	}
	
	
	
}