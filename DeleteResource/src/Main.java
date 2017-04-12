import java.io.File;
import java.lang.Thread.State;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.condition.Os;

import cn.xm.yss.FileUtils;
import cn.xm.yss.JavaMacro;
import cn.xm.yss.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Main {
	//
	public static String curVersion;
	//
	private static ArrayList<String> commonDirs;
	private static ArrayList<String> commonFiles;
	//
	private static ArrayList<String> curDirs;
	private static ArrayList<String> curFiles;

	private static String Resources_Path = "";

	private static final String DIR_KEY = "dir";
	private static final String FILE_KEY = "file";

	private static ArrayList<Thread> threads;

	public static void main(String[] args) {
		commonDirs = new ArrayList<>();
		commonFiles = new ArrayList<>();
		curDirs = new ArrayList<>();
		curFiles = new ArrayList<>();
		threads = new ArrayList<>();

		String user_dir = System.getProperty("user.dir");
		// 
		String config_path = user_dir + File.separator + "delete_cfg" + File.separator + "config.json";
		String xml_path = user_dir + File.separator + "delete_cfg" + File.separator + "deleteRes.xml";
		System.out.println("-----config path:" + config_path);
		System.out.println("-----xml path:" + xml_path);

		String JsonContext = FileUtils.getInstance().ReadFile(config_path);
		JSONObject jsonObject = JSONObject.fromObject(JsonContext);

		curVersion = (String) jsonObject.get("curVer");
		JSONArray2JavaArray(jsonObject, "common", DIR_KEY, commonDirs, FILE_KEY, commonFiles);
		JSONArray2JavaArray(jsonObject, curVersion, DIR_KEY, curDirs, FILE_KEY, curFiles);

		if (isWin()) {
			Resources_Path = (String) jsonObject.get("Win_Resources_Path");
			Resources_Path = StringUtils.getInstance().replace(Resources_Path, JavaMacro.SLASH, JavaMacro.BACKSLASH);
			StringUtils.getInstance().replace(commonDirs, JavaMacro.SLASH, JavaMacro.BACKSLASH);
			StringUtils.getInstance().replace(curDirs, JavaMacro.SLASH, JavaMacro.BACKSLASH);
		} else if (isMac()) {
			Resources_Path = (String) jsonObject.get("Mac_Resources_Path");
		}

		init(xml_path);
		
		execute(commonDirs, true);
		execute(commonFiles, false);
		execute(curDirs, true);
		execute(curFiles, false);

		while (true) {
			int count = 0;
			for (Thread thread : threads) {
				if (thread.getState() == State.TERMINATED) {
					count++;
				}
			}
			if (count == threads.size()) {
				clean(xml_path);
				System.out.println("end -------------");

				break;
			}
		}
	}

	public static void init(String path) {
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
			helper.parse(project, new File(path));
			// 执行项目的某一个目标
			project.executeTarget("init");
		} catch (BuildException be) {
			project.fireBuildFinished(be);
		}
	}
	
	public static void clean(String path) {
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
			helper.parse(project, new File(path));
			// 执行项目的某一个目标
			project.setProperty("resources", Resources_Path);
			project.executeTarget("clean");
		} catch (BuildException be) {
			project.fireBuildFinished(be);
		}
	}

	public static void execute(ArrayList<String> list, boolean isCopyDir) {
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				CopyRunnable copyRunnable = new CopyRunnable();
				copyRunnable.Resources_Path = Resources_Path;
				copyRunnable.isCopyDir = isCopyDir;
				
				String tmp = list.get(i);
				File tmpFile = new File(Resources_Path + (isWin()?JavaMacro.BACKSLASH:JavaMacro.SLASH) + tmp);
				if (!tmpFile.exists()) {
					System.out.println(tmpFile.getAbsolutePath() + " not occur");
					continue;
				}
				if (isCopyDir) {
					copyRunnable.directory = list.get(i);
				} else {
					copyRunnable.file = list.get(i);
				}

				Thread thread = new Thread(copyRunnable);
				thread.start();

				threads.add(thread);
			}
		}
	}

	public static void check(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] list = directory.listFiles();
			for (File file : list) {
				if (file.isDirectory()) {
					// check or delete
					boolean isDelete = true;
					boolean isCheck = false;

					int len = commonDirs.size();
					for (int i = 0; i < len; i++) {
						String path = commonDirs.get(i);

						String tmp = Resources_Path + (isWin() ? JavaMacro.BACKSLASH : JavaMacro.SLASH) + path;
						if (file.getAbsolutePath().equals(tmp)) {
							commonDirs.remove(path);

							i--;
							len--;
							isDelete = false;
						} else if (tmp.contains(file.getAbsolutePath())) {
							isDelete = false;
							isCheck = true;
						}
					}

					len = curDirs.size();
					for (int i = 0; i < len; i++) {
						String path = curDirs.get(i);
						String tmp = Resources_Path + (isWin() ? JavaMacro.BACKSLASH : JavaMacro.SLASH) + path;
						if (file.getAbsolutePath().equals(tmp)) {
							curDirs.remove(path);

							i--;
							len--;
							isDelete = false;
						} else if (tmp.contains(file.getAbsolutePath())) {
							isDelete = false;
							isCheck = true;
						}
					}

					if (isDelete) {
						boolean isSuccess = deleteDir(file);
						if (isSuccess) {
							System.out.println("delete success");
						} else {
							System.out.println("delete failure");
						}
					}

					if (isCheck) {
						check(file);
					}
				} else {
					file.delete();
				}
			}
		}
	}

	/**
	 * ������ͨ����
	 * 
	 * @param object
	 * @param key1
	 *            第一层key值
	 * @param key2
	 *            第二层key值
	 * @return
	 */
	private static void JSONArray2JavaArray(JSONObject object, String key1, String key2, ArrayList<String> key2Array,
			String key3, ArrayList<String> key3Array) {
		if (key2Array == null) {
			key2Array = new ArrayList<String>();
		}

		if (key3Array == null) {
			key3Array = new ArrayList<String>();
		}

		JSONArray tmp = object.getJSONArray(key1);
		if (!tmp.isEmpty()) {
			String value = "";
			JSONObject obj;
			for (int i = 0; i < tmp.size(); i++) {
				obj = (JSONObject) tmp.get(i);
				value = (String) obj.get(key2);
				if (value != null) {
					key2Array.add(value);
					System.out.println(key2 + ":" + value);
					continue;
				}

				value = (String) obj.get(key3);
				if (value != null) {
					key3Array.add(value);
					System.out.println(key3 + ":" + value);
				}
			}
		}
	}

	/**
	 * �ݹ�ɾ��Ŀ¼�µ������ļ�����Ŀ¼�������ļ�
	 * 
	 * @param dir
	 *            ��Ҫɾ�����ļ�Ŀ¼
	 * @return boolean Returns "true" if all deletions were successful. If a
	 *         deletion fails, the method stops attempting to delete and returns
	 *         "false".
	 */
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// �ݹ�ɾ��Ŀ¼�е���Ŀ¼��
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// Ŀ¼��ʱΪ�գ�����ɾ��
		return dir.delete();
	}

	private static boolean isWin() {
		return Os.isFamily(Os.FAMILY_WINDOWS);
	}

	private static boolean isMac() {
		return Os.isFamily(Os.FAMILY_MAC);
	}

}
