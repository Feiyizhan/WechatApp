package feiyizhan.rpc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.api.tuling.TulingUtil;
import hprose.client.HproseHttpClient;

/**
 * RPC客户端
 * @author Pluto Xu
 *
 */
public class RpcClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
 
	private static HproseHttpClient client = new HproseHttpClient(); 
	private static String RPC_SERVER =null;
	public static IServer RPC;
   
	static{
		Properties config = new Properties();
		try {
			config.load(new BufferedInputStream(RpcClient.class.getResourceAsStream("rpc.ini")));
			RPC_SERVER = config.getProperty("RPC_SERVER");
			client.useService(RPC_SERVER);
			RPC = (IServer) client.useService(IServer.class);  
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			LOGGER.error("RPC客户端初始化失败"+e.getMessage());
		}
		
	}
	
    

}
