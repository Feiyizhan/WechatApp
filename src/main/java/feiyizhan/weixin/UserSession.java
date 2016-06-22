package feiyizhan.weixin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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
import feiyizhan.weixin.util.CookieUtil;
import feiyizhan.weixin.util.JSONUtil;
import feiyizhan.weixin.util.JSUtil;
import feiyizhan.weixin.util.Matchers;
import feiyizhan.weixin.util.UserUtil;

public class UserSession {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserSession.class);
	/**
	 * 联系人清单
	 */
	public String[] CharSet ;
	
	public String skey, synckey, wxsid, wxuin, pass_ticket, deviceId = "e" + DateKit.getCurrentUnixTime();
	
	public String cookie;
	
	public JSONObject SyncKey, User, BaseRequest;

	
	// 微信联系人列表，群列表,可聊天的联系人列表
	public JSONArray MemberList, GrouptList,ContactList,MaleContactList,FemaleContactList,UnisexContactLit,GongZongList,FuWuList,DingYueList,QiYeList;

	
	// 微信特殊账号
	public List<String> SpecialUsers = Arrays.asList("weixin","newsapp", "fmessage", "filehelper", "weibo", "qqmail", "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend", "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin", "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts", "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", "notification_messages");
	

	public String uuid;
	public int tip = 0;
	public String base_uri, redirect_uri, webpush_url = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
	
	public int media_count =0 ;  //多媒体发送次数
	

	/**
	 * 获取UUID
	 * @return
	 */
	public String getUUID() {
		String url = "https://login.weixin.qq.com/jslogin";
		HttpRequest request = HttpRequest.get(url, true, 
				"appid", "wx782c26e4c19acffb", 
				"fun", "new",
				"lang", "zh_CN",
				"_" , DateKit.getCurrentUnixTime());
		
		LOGGER.debug("[*] " + request);
		
		String res = request.body();
		request.disconnect();

		if(StringKit.isNotBlank(res)){
			String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
			if(null != code){
				if(code.equals("200")){
					this.uuid = Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
					LOGGER.info("[*] 获取到uuid为{}", this.uuid);
					return this.uuid;
				} else {
					LOGGER.info("[*] 错误的状态码:{}", code);
				}
			}
		}
		return null;
	}
	
	/**
	 * 等待登录
	 */
	public String waitForLogin(){
		this.tip = 1;
		String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
		HttpRequest request = HttpRequest.get(url, true, 
				"tip", this.tip, 
				"uuid", this.uuid,
				"_" , DateKit.getCurrentUnixTime());
		
		LOGGER.info("[*] " + request.toString());
		
		String res = request.body();
		request.disconnect();

		if(null == res){
			LOGGER.info("[*] 扫描二维码验证失败");
			return "";
		}
		
		String code = Matchers.match("window.code=(\\d+);", res);
		if(null == code){
			LOGGER.info("[*] 扫描二维码验证失败");
			return "";
		} else {
			if(code.equals("201")){
				LOGGER.info("[*] 成功扫描,请在手机上点击确认以登录");
				tip = 0;
			} else if(code.equals("200")){
				LOGGER.info("[*] 正在登录...");
				String pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res);

				String redirectHost = "wx.qq.com";
				try {
					URL pmURL = new URL(pm);
					redirectHost = pmURL.getHost();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				String pushServer = JSUtil.getPushServer(redirectHost);
				webpush_url = "https://" + pushServer + "/cgi-bin/mmwebwx-bin";

				this.redirect_uri = pm + "&fun=new";
				LOGGER.info("[*] redirect_uri={}", this.redirect_uri);
				this.base_uri = this.redirect_uri.substring(0, this.redirect_uri.lastIndexOf("/"));
				LOGGER.info("[*] base_uri={}", this.base_uri);
			} else if(code.equals("408")){
				LOGGER.info("[*] 登录超时");
			} else {
				LOGGER.info("[*] 扫描code={}", code);
			}
		}
		return code;
	}
	
	/**
	 * 登录
	 */
	public boolean login(){
		
		boolean isLogin = false;
		//等待扫描登录
		for(int i=0;i<5*60/2;i++){
			if(!waitForLogin().equals("200")){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
			}else{
				isLogin = true;
				break;
			}
		}
		if(!isLogin){
			return false;
		}
		
		//扫描登录成功之后
		
		HttpRequest request = HttpRequest.get(this.redirect_uri);
		
		LOGGER.debug("[*] " + request);
		
		String res = request.body();
		this.cookie = CookieUtil.getCookie(request);

		request.disconnect();
		
		if(StringKit.isBlank(res)){
			return false;
		}
		
		this.skey = Matchers.match("<skey>(\\S+)</skey>", res);
		this.wxsid = Matchers.match("<wxsid>(\\S+)</wxsid>", res);
		this.wxuin = Matchers.match("<wxuin>(\\S+)</wxuin>", res);
		this.pass_ticket = Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res);
		
		LOGGER.info("[*] skey{}", this.skey);
		LOGGER.info("[*] wxsid{}", this.wxsid);
		LOGGER.info("[*] wxuin{}", this.wxuin);
		LOGGER.info("[*] pass_ticket{}", this.pass_ticket);
		
		this.BaseRequest = new JSONObject();
		BaseRequest.put("Uin", this.wxuin);
		BaseRequest.put("Sid", this.wxsid);
		BaseRequest.put("Skey", this.skey);
		BaseRequest.put("DeviceID", this.deviceId);
		
		
		return true;
	}
	
	/**
	 * 微信初始化
	 */
	public boolean wxInit(){
		
		String url = this.base_uri + "/webwxinit?r=" + DateKit.getCurrentUnixTime() + "&pass_ticket=" + this.pass_ticket +
				"&skey=" + this.skey;
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", this.BaseRequest);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();
		
		if(StringKit.isBlank(res)){
			return false;
		}
		
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			if(null != jsonObject){
				JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
				if(null != BaseResponse){
					int ret = BaseResponse.getInt("Ret", -1);
					if(ret == 0){
						//记录下公众号和群号联系人
//						this.ContactList = jsonObject.getJSONArray("ContactList");
						
						
						this.CharSet = jsonObject.getString("ChatSet").split("[,]");
						LOGGER.info("[*] 公众号+群号联系人总数:" + CharSet.length);
						this.SyncKey = jsonObject.getJSONObject("SyncKey");
						this.User = jsonObject.getJSONObject("User");
						
						
						StringBuffer synckey = new StringBuffer();
						
						JSONArray list = SyncKey.getJSONArray("List");
						for(int i=0, len=list.size(); i<len; i++){
							JSONObject item = list.getJSONObject(i);
							synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
						}
						
						this.synckey = synckey.substring(1);
						
						this.sendTextMessage("登录成功，查询命令，请输入【帮助】。", UserUtil.getUserID(this.User));
						this.sendTextMessage("登录成功，查询命令，请输入【帮助】。", "filehelper");  //追加向文件传输助手发送消息，用于支持不能自己给自己发消息的微信号
						this.sendTextMessage("如果在网页版微信上无法自己给自己发送消息，则请使用【修改系统消息接收者为 文件传输助手】命令来修改默认的系统消息接收者。", "filehelper"); 
//						if(null != BaseResponse){
//							for(int i=0, len=MemberList.size(); i<len; i++){
//								JSONObject contact = this.MemberList.getJSONObject(i);
//								//公众号/服务号
//								if(contact.getInt("VerifyFlag", 0) == 8){
//									continue;
//								}
//								//特殊联系人
//								if(SpecialUsers.contains(contact.getString("UserName"))){
//									continue;
//								}
//								//群聊
//								if(contact.getString("UserName").indexOf("@@") != -1){
//									//continue;
//								}
//								//自己
//								if(contact.getString("UserName").equals(this.User.getString("UserName"))){
//									//continue;
//								}
//								ContactList.add(contact);
//							}
//						}
						
						
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 微信状态通知
	 */
	public boolean wxStatusNotify (){
		
		String url = this.base_uri + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + this.pass_ticket;
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("Code", 3);
		body.put("FromUserName", this.User.getString("UserName"));
		body.put("ToUserName", this.User.getString("UserName"));
		body.put("ClientMsgId", DateKit.getCurrentUnixTime());
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return false;
		}
		
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret", -1);
				return ret == 0;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 批处理获取联系人列表（主要是群联系人），返回联系人清单
	 * @return
	 */
	public JSONArray webWxBatchGetContact(JSONArray list){
		String url = this.base_uri + "/webwxbatchgetcontact?type=ex&pass_ticket=" + this.pass_ticket + "&r=" + DateKit.getCurrentUnixTime();
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("Count", list.size());
		body.put("List", list);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();
//		LOGGER.info("[*] 联系人的返回: " + res);  //显示消息返回
		if(StringKit.isBlank(res)){
			return null;
		}
		
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret", -1);
				if(ret == 0){
					JSONArray Contlist = jsonObject.getJSONArray("ContactList");
					return Contlist;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	
	/**
	 * 拉人进群
	 * @return
	 */
	public boolean webwxupdatechatroomAdd(String roomID,List<String> userID){
		String url = this.base_uri + "/webwxupdatechatroom?fun=addmember&pass_ticket=" + this.pass_ticket + "&r=" + DateKit.getCurrentUnixTime();
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("AddMemberList", userID);
		body.put("ChatRoomName", roomID);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();
		if(StringKit.isBlank(res)){
			return false;
		}
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret", -1);
				if(ret == 0){
					JSONArray MemberList = jsonObject.getJSONArray("MemberList");
					JSONObject member = MemberList.getJSONObject(0);
					if(null!=member){  //添加成功
						int memberStatus = member.getInt("MemberStatus", -1);
						
						return memberStatus!=-1?true:false;
						
					}
				}

			}
		} catch (Exception e) {
		}
		return false;
		
	}
	
	/**
	 * 从群里删人
	 * @param roomID
	 * @param userID
	 * @return
	 */
	public boolean webwxupdatechatroomDel(String roomID,List<String> userID){
		String url = this.base_uri + "/webwxupdatechatroom?fun=delmember&pass_ticket=" + this.pass_ticket + "&r=" + DateKit.getCurrentUnixTime();
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("DelMemberList", userID);
		body.put("ChatRoomName", roomID);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();
		if(StringKit.isBlank(res)){
			return false;
		}
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret", -1);
				if(ret == 0){
					int MemberCount = jsonObject.getInt("MemberCount", -1);
					if(MemberCount ==0){  //删除成功
						return true;
					}
				}

			}
		} catch (Exception e) {
		}
		return false;
		
	}
	
	/**
	 * 根据用户列表创建群,返回的Room对象。
	 * @param memberList 格式 {UserName: "@34d60af6f8f70c76a6f4af2cbbd25245c51e95d9be53b07904b51906965648b6"}
	 * @return
	 */
	public JSONObject webwxCreateChatRoom(JSONArray memberList){
		String url = this.base_uri + "/webwxcreatechatroom?pass_ticket=" + this.pass_ticket + "&r=" + DateKit.getCurrentUnixTime();
		JSONObject room = new JSONObject();
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("Topic","");
		body.put("MemberCount",memberList.size());
		body.put("MemberList", memberList);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();
		if(StringKit.isBlank(res)){
			return room;
		}
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret", -1);
				String errMsg = BaseResponse.getString("ErrMsg");
				if(ret == 0 && errMsg.equals("Everything is OK")){
					
					room.put("ChatRoomName", jsonObject.getString("ChatRoomName"));
					room.put("MemberList", jsonObject.getJSONArray("MemberList"));
				}else{
					room.put("ErrMsg", errMsg);
					LOGGER.debug("[*] " + res);
				}

			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return room;
	}
	
	/**
	 * 获取联系人
	 */
	public boolean getContact(){
		
		String url = this.base_uri + "/webwxgetcontact?pass_ticket=" + this.pass_ticket + "&skey=" + this.skey + "&r=" + DateKit.getCurrentUnixTime();
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();
//		LOGGER.info("[*] 联系人的返回: " + res);  //显示消息返回
		if(StringKit.isBlank(res)){
			return false;
		}
		
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret", -1);
				if(ret == 0){
					this.MemberList = jsonObject.getJSONArray("MemberList");
					
					if(null != MemberList){
						
						this.GrouptList=new JSONArray();
//						int size =0;
//						JSONArray list = new JSONArray();
//						for(String str:this.CharSet){
//							JSONObject user = new JSONObject();
//							if(size >=50){
//								UserUtil.combinUserList(this.GrouptList, this.webWxBatchGetContact(list));
//								list =  new JSONArray();
//								size =0;
//							}
//							if(str.startsWith("@@")){ //获取群明细
//								user.put("UserName", str);
//								user.put("EncryChatRoomId", "");
//								size ++;
//								list.add(user);
//							}else if(str.startsWith("@")){ //服务号
////								user.put("UserName", str);
////								user.put("ChatRoomId", "");
////								size +=1;
////								list.add(user);
//								LOGGER.info("[*] "+str);
//							}else{ //特殊帐号，不处理
//								LOGGER.info("[*] "+str);
//							}
//							
//						}
//						
//						if (size >0){
//							UserUtil.combinUserList(this.GrouptList, this.webWxBatchGetContact(list));
//						}
//						for(JSONValue val:this.GrouptList){
//							JSONObject obj = val.asObject();
//							reFlashGroupContactList(obj);
//						}
                          
						this.reFlashContactist();
						
						String report = this.getUserReport();
						LOGGER.info("[*]",report);
						
						
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 消息检查
	 */
	public int[] syncCheck(){
		
		int[] arr = new int[2];
		
		String url = this.webpush_url + "/synccheck";
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		
		HttpRequest request = HttpRequest.get(url, true,
				"r", DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5),
				"skey", this.skey,
				"uin", this.wxuin,
				"sid", this.wxsid,
				"deviceid", this.deviceId,
				"synckey", this.synckey,
				"_", System.currentTimeMillis())
				.header("Cookie", this.cookie);
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)){
			return arr;
		}
		
		String retcode = Matchers.match("retcode:\"(\\d+)\",", res);
		String selector = Matchers.match("selector:\"(\\d+)\"}", res);
		if(null != retcode && null != selector){
			arr[0] = Integer.parseInt(retcode);
			arr[1] = Integer.parseInt(selector);
			return arr;
		}
		return arr;
	}
	
	
	
	
	
	/**
	 * 发送文本消息
	 * @param content
	 * @param to
	 */
	public void sendTextMessage(String content, String to) {
		
		String url = this.base_uri + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + this.pass_ticket;
		
		JSONObject body = new JSONObject();
		
		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
		JSONObject Msg = new JSONObject();
		Msg.put("Type", 1);
		Msg.put("Content", content);
		Msg.put("FromUserName", UserUtil.getUserID(User));
		Msg.put("ToUserName", to);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);
		
		body.put("BaseRequest", this.BaseRequest);
		body.put("Msg", Msg);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		request.body();
		request.disconnect();
	}
	

	
	
	
	
	/**
	 * 上传微信多媒体数据
	 * @param fileName
	 * @return
	 */
	public JSONObject webwxuploadmedia(String fileName){
		String url = "https://file.wx.qq.com/cgi-bin/mmwebwx-bin/webwxuploadmedia?f=json";
		media_count +=1;
		File file = FileUtils.getFile(fileName);
		//文件名
		String file_name = FilenameUtils.getName(fileName);
		
		//MIME格式
		//mime_type = application/pdf, image/jpeg, image/png, etc.
		String mime_type = "image/png";	
		//微信识别的文档格式，微信服务器应该只支持两种类型的格式。pic和doc
	    //pic格式，直接显示。doc格式则显示为文件。
		String media_type ="pic";
		//上一次修改日期
		String lastModifieDate = "Thu Apr 06 2016 00:55:10 GMT+0800 (CST)";
		//文件大小
		long file_size =FileUtils.sizeOf(file);
		//PassTicket
		
		//clientMediaId
		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
		//webwx_data_ticket
		String webwx_data_ticket = "";
		
		JSONObject uploadmediarequest = new JSONObject();
		uploadmediarequest.put("BaseRequest", this.BaseRequest);
		uploadmediarequest.put("ClientMediaId",clientMsgId);
		uploadmediarequest.put("TotalLen", file_size);
		uploadmediarequest.put("StartPos", 0);
		uploadmediarequest.put("DataLen", file_size);
		uploadmediarequest.put("MediaType", 4);
		

//				
//				multipart_encoder = MultipartEncoder(
//						fields = {
//							'id': 'WU_FILE_' + str(self.media_count), 
//							'name': file_name,
//							'type': mime_type,
//							'lastModifieDate': lastModifieDate,
//							'size': str(file_size),
//							'mediatype': media_type,
//							'uploadmediarequest': uploadmediarequest,
//							'webwx_data_ticket': webwx_data_ticket,
//							'pass_ticket': this.pass_ticket,
//							'filename': (file_name, open(file_name, 'rb'), mime_type.split('/')[1])
//						},
//						boundary = '-----------------------------1575017231431605357584454111'
//					)
		String boundary="------WebKitFormBoundaryT2NGayLGOFMwSBwT";
		StringBuilder sb = new StringBuilder();
		sb.append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"id\"").append("\n").append("\n")
		  .append("WU_FILE_").append(media_count).append("\n")
		  
		  .append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"name\"").append("\n").append("\n")
		  .append(file_name).append("\n")
		  
		  .append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"type\"").append("\n").append("\n")
		  .append(mime_type).append("\n")
		  
		  .append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"lastModifiedDate\"").append("\n").append("\n")
		  .append(lastModifieDate).append("\n")
		  
		  
		  .append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"size\"").append("\n").append("\n")
		  .append(file_size).append("\n")
		  
		  .append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"mediatype\"").append("\n").append("\n")
		  .append(media_type).append("\n")
		  
		  .append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"uploadmediarequest\"").append("\n").append("\n")
		  .append(uploadmediarequest).append("\n")
		  
		  .append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"webwx_data_ticket\"").append("\n").append("\n")
		  .append(webwx_data_ticket).append("\n")
		  
		  
		  .append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"pass_ticket\"").append("\n").append("\n")
		  .append(pass_ticket).append("\n")
		  
		  .append(boundary).append("\n")
		  .append("Content-Disposition: form-data; name=\"filename\";").append("filename=\"").append(file_name).append("\"").append("\n")
		  .append("Content-Type: image/png").append("\n")
		  .append("\n")
		  .append("\n")
		  
		  .append(boundary).append("--\n");
		
		
		
//					headers = {
//						'Host': 'file2.wx.qq.com',
//						'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:42.0) Gecko/20100101 Firefox/42.0',
//						'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
//						'Accept-Language': 'en-US,en;q=0.5',
//						'Accept-Encoding': 'gzip, deflate',
//						'Referer': 'https://wx2.qq.com/',
//						'Content-Type': multipart_encoder.content_type,
//						'Origin': 'https://wx2.qq.com',
//						'Connection': 'keep-alive',
//						'Pragma': 'no-cache',
//						'Cache-Control': 'no-cache'
//					}

				HttpRequest request = HttpRequest.post(url)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
						.header("Accept", "*/*")
						.header("Accept-Language", "zh-CN,zh;q=0.8")
						.header("Accept-Encoding", "gzip, deflate")
						.header("Content-Type", "multipart/form-data;boundary=----WebKitFormBoundaryT2NGayLGOFMwSBwT")  // 使用multipart/form-data发送POST请求
						.header("Host", "file.wx.qq.com")
						.header("Origin", "https://wx.qq.com")
						.header("Referer", "https://wx.qq.com/")
						.header("Connection", "keep-alive")
						.header("Pragma", "no-cache")
						.header("Cache-Control", "no-cache")
						.send(sb.toString());
						
					String res = request.body();
					request.disconnect();
					
					if(StringKit.isBlank(res)){
						return null;
					}
					
					JSONObject jsonObject = JSON.parse(res).asObject();
					JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
					if(null != BaseResponse){
						int ret = BaseResponse.getInt("Ret", -1);
						if(ret!=-1){
							return jsonObject;
						}else{
							return null;
						}
					}else{
						return null;
					}
		
		
	}
	
	
	
	/**
	 * 发送图片消息
	 * @param content
	 * @param to
	 */
	public void sendImageMessage(String content, String to) {
		
		String url = this.base_uri + "/webwxsendmsgimg?fun=async&f=json&pass_ticket=" + this.pass_ticket;
		
		JSONObject body = new JSONObject();
		
		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
		JSONObject Msg = new JSONObject();
		Msg.put("Type", 3);
		Msg.put("MediaId", content);
		Msg.put("FromUserName", UserUtil.getUserID(User));
		Msg.put("ToUserName", to);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);
		
		body.put("BaseRequest", this.BaseRequest);
		body.put("Msg", Msg);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		request.body();
		request.disconnect();
	}
	
	/**
	 * 发送表情消息
	 * @param conten
	 * @param to
	 */
	public void sendEmoMessage(String content,String to){
		String url = this.base_uri + "/webwxsendemoticon?fun=sys&pass_ticket=" + this.pass_ticket; 

		JSONObject body = new JSONObject();
		
		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
		JSONObject Msg = new JSONObject();
		Msg.put("Type",47);
		Msg.put("EmojiFlag", 2);
		Msg.put("EMoticonMd5", "8690f2ec5676b9d2d70f7cba012e772e"); 
		Msg.put("FromUserName", UserUtil.getUserID(User));
		Msg.put("ToUserName", to);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);
		
		body.put("BaseRequest", this.BaseRequest);
		body.put("Msg", Msg);
		
		
		HttpRequest request = HttpRequest.post(url)
				.header("Accept", "application/json, text/plain, */*")
//				.header("Accept-Encoding", "gzip, deflate")
				.header("Cache-Control", "no-cache")
				.header("Connection", "Keep-Alive")
				.header("Content-Type", "application/json;charset=utf-8")
				
				.header("Cookie", this.cookie)
				.send(body.toString());
		LOGGER.debug("[*] " + cookie);
		LOGGER.debug("[*] " + body.toString());
		LOGGER.debug("[*] " + request);
		LOGGER.debug(request.body());
		request.disconnect();
		
		
		
		
	}
	
	/**
	 * 获取最新消息
	 */
	public JSONObject webwxsync(){
		
		String url = this.base_uri + "/webwxsync?lang=zh_CN&pass_ticket=" + this.pass_ticket
				 + "&skey=" + this.skey + "&sid=" + this.wxsid + "&r=" + DateKit.getCurrentUnixTime();
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("SyncKey", this.SyncKey);
		body.put("rr", DateKit.getCurrentUnixTime());
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();
		
		if(StringKit.isBlank(res)){
			return null;
		}
		
		JSONObject jsonObject = JSON.parse(res).asObject();
		JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
		if(null != BaseResponse){
			int ret = BaseResponse.getInt("Ret", -1);
			if(ret == 0){
				this.SyncKey = jsonObject.getJSONObject("SyncKey");
				
				StringBuffer synckey = new StringBuffer();
				JSONArray list = SyncKey.getJSONArray("List");
				for(int i=0, len=list.size(); i<len; i++){
					JSONObject item = list.getJSONObject(i);
					synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
				}
				this.synckey = synckey.substring(1);
			}
		}
		return jsonObject;
	}
	
	


	
	
	/**
	 * 根据用户ID获取用户名称，不区分是哪一类型用户。如果传人了第二个参数，则继续在第二个参数对应的群里找该用户
	 * @param id
	 * @param groupID
	 * @return
	 */
	public String getUserName(String id,String groupID){
		return UserUtil.getUserName(getUserByID(id,groupID));
	}
	
	/**
	 * 获取用户对象
	 * @param id
	 * @param groupID
	 * @return
	 */
	public JSONObject getUserByID(String id,String groupID){
		if(null==groupID){  //个人联系
			JSONObject member = this.getContactUser(id);  //先在个人联系人清单里查找
			if(null==member){
				return this.getGroup(id);  //
			}else{
				return member;
			}
		}else{  //群成员
			JSONObject group = this.getGroup(groupID);
			if(null==group){
				return null;
			}else{
				return this.getGroupMemberUserByID(group, id);
			}
		}
	}
	
	
	/**
	 * 获取用户对象
	 * @param id
	 * @param groupID
	 * @return
	 */
	public JSONObject getUserByName(String name,String groupName){
		if(null==groupName){  //个人联系
			JSONObject member = this.getContactUserByName(name);  //先在个人联系人清单里查找
			if(null==member){
				return this.getGroupByName(name);  //
			}else{
				return member;
			}
		}else{  //群成员
			JSONObject group = this.getGroupByName(groupName);
			if(null==group){
				return null;
			}else{
				return this.getGroupMemberUserByName(group, name);
			}
		}
	}
	/**
	 * 根据名称获取个人联系人,如未找到，返回null
	 * @param name
	 * @return
	 */
	public JSONObject getContactUserByName(String name){
		//String userID =this.User.getString("UserName");
		return UserUtil.findUserObjectByName(ContactList, name);
	}
	
	/**
	 * 根据名称，获取服务号或者群。如果未找到，返回null
	 * @param name
	 * @return
	 */
	public JSONObject getGroupByName(String name){
		return UserUtil.findUserObjectByName(GrouptList, name);
	}
	
	
	/**
	 *  根据ID获取个人联系人对象,未获取返回null
	 * @param id
	 * @return
	 */
	public JSONObject getContactUser(String id){
		return UserUtil.findUserObjectByID(this.ContactList,id);
	}
	
	
	/**
	 * 根据ID获取服务号或群备注名称，如果没有备注名称就获取微信名称
	 * @param id
	 * @return
	 */
	public String getGroupRemarkName(String id) {
		return UserUtil.getUserName(getGroup(id));
	}
	
	/**
	 *  根据ID获取服务号或群联系人对象,未获取返回null
	 * @param id
	 * @return
	 */
	public JSONObject getGroup(String id){
		return UserUtil.findUserObjectByID(GrouptList, id);
	}
	
	
	/**
	 * 根据ID和群联系人对象，获取该ID在群的联系对象，如果没有，返回null
	 * @param group
	 * @param id
	 * @return
	 */
	public JSONObject getGroupMemberUserByID(JSONObject group,String id){
		/*
		 * 联系人类型 ContactFlag：
		 *  2 -- 群
		 *  3 -- 公众号
		 */

			JSONArray memberList = UserUtil.getGroupMemberList(group);
			return UserUtil.findUserObjectByID(memberList,  id);

		
	}
	
	/**
	 * 根据名称和群联系人对象，获取该ID在群的联系对象，如果没有，返回null
	 * @param group
	 * @param name
	 * @return
	 */
	public JSONObject getGroupMemberUserByName(JSONObject group,String name){
		/*
		 * 联系人类型 ContactFlag：
		 *  2 -- 群
		 *  3 -- 公众号
		 */
		JSONArray memberList =  UserUtil.getGroupMemberList(group);
		return UserUtil.findUserObjectByName(memberList,  name);

		
	}
	
	/**
	 * 输出用户清单
	 */
	public void printUserList(){
		for(JSONValue val:this.ContactList){
			JSONObject obj = val.asObject();
			LOGGER.info("[*]"+obj.getString("UserName")+"|"+obj.getString("NickName")+"|"+obj.getInt("", -1)+"|");
		}
	}
	
	/**
	 * 获取当前用户报表
	 * @return
	 */
	public String getUserReport(){
		StringBuilder sb = new StringBuilder();
		sb.append(" 共有"+ MemberList.size()+"位联系人。\n");
		sb.append(" 其中有"+ MaleContactList.size()+"位男性。\n");
		sb.append(" 有"+ FemaleContactList.size()+"位女性。\n");
		sb.append(" 有"+ UnisexContactLit.size()+"位未设置性别。\n");
		sb.append(" 有"+ GongZongList.size()+"个公众号。\n");
		sb.append(" 有"+ FuWuList.size()+"个服务号。\n");
		sb.append(" 有"+ DingYueList.size()+"个订阅号。\n");
		sb.append(" 有"+ QiYeList.size()+"个企业号。\n");
		sb.append(" 另外还有"+ GrouptList.size()+"个活跃的群。\n");
		return sb.toString();
	}
	
	/**
	 * 判断指定的用户ID是否为Special用户
	 * @param id
	 * @return
	 */
	public boolean isSpaciaUser(String id){
		return this.SpecialUsers.contains(id);
	}
	
	/**
	 * 修改用户备注名
	 * @param user
	 * @param name
	 * @return
	 */
	public boolean changeUserRemarkName(JSONObject user,String name){
		String url = this.base_uri + "/webwxoplog?pass_ticket=" + this.pass_ticket;
				 
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", BaseRequest);
		body.put("CmdId", 2);
		body.put("RemarkName", name);
		body.put("UserName", UserUtil.getUserID(user));
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		
		LOGGER.debug("[*] " + request);
		String res = request.body();
		request.disconnect();
		
		if(StringKit.isBlank(res)){
			return false;
		}
		
		JSONObject jsonObject = JSON.parse(res).asObject();
		JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
		if(null != BaseResponse){
			int ret = BaseResponse.getInt("Ret", -1);
			if(ret == 0){
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * 按照当前的MemberList更新联系人列表
	 */
	public void reFlashContactist(){
		if(null != MemberList){
			this.ContactList = new JSONArray();
			this.GongZongList =new JSONArray();
			this.MaleContactList =new JSONArray();
			this.FemaleContactList = new JSONArray();
			this.FuWuList = new JSONArray();
			this.DingYueList = new JSONArray();
			this.QiYeList = new JSONArray();
			this.UnisexContactLit = new JSONArray();
			for(int i=0, len=MemberList.size(); i<len; i++){
				JSONObject contact = this.MemberList.getJSONObject(i);
				int sex = contact.getInt("Sex", -1);
				int verifyFlag = contact.getInt("VerifyFlag", -1);
				String userID =UserUtil.getUserID(contact);
				if(isSpaciaUser(userID)){  //特殊联系人不处理
					LOGGER.info("[*]特殊联系人"+contact);
					continue;
				}
				if(userID!=null && userID.equals(UserUtil.getUserID(this.User))){  //自己
					LOGGER.info("[*]自己"+contact);
					continue;
				}
				if(userID!=null && userID.startsWith("@@")){  //群号
					UserUtil.add(this.GrouptList, contact);
					LOGGER.info("[*]群号"+contact);
					continue;
				}
				switch (verifyFlag){
				case 0:{  //非公众号
					switch (sex){
					case 0:{ //不男不女
						this.ContactList.add(contact);
						this.UnisexContactLit.add(contact);
						break;
					}
					case 1:{ //男性
						this.ContactList.add(contact);
						this.MaleContactList.add(contact);
						break;
					}
					case 2:{ //女性
						this.ContactList.add(contact);
						this.FemaleContactList.add(contact);
						break;
					}
					default:{  //未识别的性别信息
						LOGGER.info("[*]未识别的性别信息"+contact);
					}
					}
					break;
				}
				case 24:{ // 服务号
					this.GongZongList.add(contact);
					this.FuWuList.add(contact);
					break;
				}
				
				case 8:{  //订阅号
					this.GongZongList.add(contact);
					this.DingYueList.add(contact);
					break;
				}
				case 29:{ //企业号
					this.GongZongList.add(contact);
					this.QiYeList.add(contact);
					break;
				}
				case 28:{ //优衣库微信号
					LOGGER.info("[*]特殊的类型|"+contact);
					break;
				}
				default:{ //其他未识别
					LOGGER.info("[*]未识别的类型|"+contact);
				}
				
				}
			}
			
		}
	}
	
	/**
	 * 刷新群成员清单为明细清单
	 * @param group
	 */
	public void reFlashGroupContactList(JSONObject group){
		String roomID = UserUtil.getUserID(group);
		int count =0;
		List<String> idList = new ArrayList<String>(50);
		JSONArray contactList = new JSONArray();
		JSONArray memberList = UserUtil.getGroupMemberList(group);
		for(JSONValue val:memberList){
			JSONObject member = val.asObject();
			String id = UserUtil.getUserID(member);
			if(count>=50){

				JSONArray list = UserUtil.transferToGetContactFromatArray(idList, roomID);
				UserUtil.combinUserList(contactList, this.webWxBatchGetContact(list));
				idList = new ArrayList<String>(50);
				count =0;
				
			}
			idList.add(id);
			count++;
			
		}
		
		if(count>=0){
			JSONArray list = UserUtil.transferToGetContactFromatArray(idList, roomID);
			UserUtil.combinUserList(contactList, this.webWxBatchGetContact(list));
		}
		
		//替换群成员对象为更明细的群成员对象
		UserUtil.replaceGroupMemberList(group,contactList);
		
	}
	
	/**
	 * 刷新群信息
	 * @param group
	 */
	public void reFlashGroup(JSONObject group){
		String id = UserUtil.getUserID(group);
		List<String> idList = new ArrayList<String>(1);
		idList.add(id);
		//先更新群本身的信息
		JSONArray list = UserUtil.transferToGetContactFromatArray(idList, "");
		UserUtil.combinUserList(this.GrouptList, this.webWxBatchGetContact(list));
		//再更新群成员的信息
		reFlashGroupContactList(this.getGroup(id));
	}
	
	/**
	 * 刷新用户信息
	 * @param id
	 */
	public void reFlashContact(JSONObject user){
		String id = UserUtil.getUserID(user);
		List<String> idList = new ArrayList<String>(1);
		idList.add(id);
		JSONArray list = UserUtil.transferToGetContactFromatArray(idList, "");
		UserUtil.combinUserList(this.MemberList, this.webWxBatchGetContact(list));
		
		
	}
	
	
	/**
	 * 更新指定ID的信息到当前联系人列表
	 * @param id
	 */
	public void updateMemberList(String id){
		if(UserUtil.getUserID(this.User).equals(id)){
			return;
		}
		if(this.isSpaciaUser(id)){
			return;
		}
		JSONArray list = new JSONArray();
		list.add(UserUtil.transferToGetContactFromat(id,""));
		if(id.startsWith("@@")){
			if(this.getGroup(id)==null){  //更新群联系人
				UserUtil.combinUserList(this.GrouptList, this.webWxBatchGetContact(list));
				JSONObject group = this.getGroup(id);
				if(group!=null){
					this.reFlashGroupContactList(group);
				}
			}
			

		}else{

			if(this.getUserByID(id,null)==null){  //更新个人联系人
				UserUtil.combinUserList(this.MemberList, this.webWxBatchGetContact(list));
				this.reFlashContactist();
				
			}
			
		}
	}

}
