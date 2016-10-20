import java.io.File;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntSample {
	private static String target = "delete_png";
	private static Project p;

	public static void main(String[] args) {
		String user_dir = System.getProperty("user.dir");
		// 配置文件的路径
		String config_path = user_dir + File.separator + "removePNG_cfg" + File.separator + "config.json";
		String xml_path = user_dir + File.separator + "removePNG_cfg" + File.separator + "removePNG.xml";
		System.out.println("-----config path:" + config_path);
		System.out.println("-----xml path:" + xml_path);

		File buildFile = new File(xml_path);

		// 创建一个ANT项目
		p = new Project();
		// 创建一个默认的监听器,监听项目构建过程中的日志操作
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);

		try {
			p.fireBuildStarted();
			// 初始化该项目
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			// 解析项目的构建文件
			helper.parse(p, buildFile);

			System.out.println(p.getTargets());
			
			// 执行项目的某一个目标
			executeAnt(p);	
			
			p.fireBuildFinished(null);
		} catch (BuildException be) {
			p.fireBuildFinished(be);
		}
	}

	private static void executeAnt(Project p) {
		System.out.println("默认Target：" + p.getDefaultTarget());
		p.executeTarget(target);
	}
	
	/**
	 * 不具有通用性
	 * 
	 * @param object
	 * @param key1
	 * @param key2
	 * @return
	 */
	private static ArrayList<String> JSONArray2JavaArray(JSONObject object, String key1, String key2) {
		ArrayList<String> list = new ArrayList<String>();

		JSONArray tmp = object.getJSONArray(key1);
		if (!tmp.isEmpty()) {
			String value = "";
			JSONObject obj;
			for (int i = 0; i < tmp.size(); i++) {
				obj = (JSONObject) tmp.get(i);
				value = (String) obj.get(key2);
				list.add(value);
				System.out.println(value);
			}
		}
		return list;
	}
}