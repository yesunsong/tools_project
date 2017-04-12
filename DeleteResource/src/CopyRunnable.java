import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class CopyRunnable implements Runnable {

	public String directory = "";
	public String file = "";
	public boolean isCopyDir = true;

	public String Resources_Path = "";
	
	@Override
	public void run() {
		String user_dir = System.getProperty("user.dir");
		// �����ļ���·��
		// String config_path = user_dir + File.separator + "delete_cfg" +
		// File.separator + "config.json";
		String xml_path = user_dir + File.separator + "delete_cfg" + File.separator + "deleteRes.xml";
		// System.out.println("-----config path:" + config_path);
		System.out.println("-----xml path:" + xml_path);

		// 创建一个ANT项目
		Project project = new Project();

		// 创建一个默认的监听器,监听项目构建过程中的日志操作
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		project.addBuildListener(consoleLogger);

		try {
			project.fireBuildStarted();
			// 初始化该项目
			project.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			// 解析项目的构建文件
			helper.parse(project, new File(xml_path));
			// 执行项目的某一个目标
			project.setProperty("resources", Resources_Path);
			if (isCopyDir) {
				project.setProperty("directory", directory);
				project.executeTarget("copyDir");
			} else {
				project.setProperty("file", file);
				project.executeTarget("copyFile");
			}
		} catch (BuildException be) {
			project.fireBuildFinished(be);
		}
	}

}
