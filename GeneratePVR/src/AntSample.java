import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.condition.Os;

import cn.xm.yss.ArrayUtils;
import cn.xm.yss.FileUtils;
import cn.xm.yss.StringUtils;

public class AntSample {
	private static final String RGBA8888 = "RGBA8888";
	private static final String RGBA4444 = "RGBA4444";

	private static String Resources_Path = "";
	private static String TexturePacker_Path = "";
	private static String single_target = "single";
	private static String directory_target = "directory";
	// 要逐渐移除 包含/排除目录和包含/排除文件
	private static ArrayList<String> ExcludedDirs;
	private static ArrayList<String> ExcludedFiles;
	private static ArrayList<String> IncludedDirs;
	private static ArrayList<String> IncludedFiles;

	private static Project p;

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

	public static void main(String[] args) {
		String filePath = AntSample.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		// System.out.println("jar+path:" + filePath);

		String user_dir = System.getProperty("user.dir");
		// 配置文件的路径
		String config_path = user_dir + File.separator + "generatePVR_cfg" + File.separator + "config.json";

		System.out.println("-----config path:" + config_path);
		// System.out.println(System.getProperty("user.home"));
		// System.out.println("-----main_class_path:" +
		// getClassFilePath(AntSample.class));
		// System.out.println("-----" + getClassPath(AntSample.class));

		// String xml_path =
		// AntSample.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String xml_path = user_dir + File.separator + "generatePVR_cfg" + File.separator + "generatePVR.xml";
		System.out.println("-----xml path:" + xml_path);

		String JsonContext = new Util().ReadFile(config_path);

		JSONObject jsonObject = JSONObject.fromObject(JsonContext);
		Resources_Path = (String) jsonObject.get("Resources_Path");
		if (Os.isFamily(Os.FAMILY_WINDOWS)) {
			Resources_Path = Resources_Path.replace("/", "\\");	
		}		
		TexturePacker_Path = (String) jsonObject.get("TexturePacker_Path");

		ExcludedDirs = JSONArray2JavaArray(jsonObject, "ExculdedDir", "dir");
		ExcludedFiles = JSONArray2JavaArray(jsonObject, "ExculdedFile", "file");
		IncludedDirs = JSONArray2JavaArray(jsonObject, "InculdedDir", "dir");
		IncludedFiles = JSONArray2JavaArray(jsonObject, "InculdedFile", "file");

		if (Os.isFamily(Os.FAMILY_WINDOWS)) {
			StringUtils.getInstance().replace(ExcludedDirs, '/', '\\');
			StringUtils.getInstance().replace(ExcludedFiles, '/', '\\');
			StringUtils.getInstance().replace(IncludedDirs, '/', '\\');
			StringUtils.getInstance().replace(IncludedFiles, '/', '\\');
		}

		System.out.println("Resources Path    :" + Resources_Path);
		System.out.println("TexturePacker Path:" + TexturePacker_Path);
		System.out.println("Exculded Dir      :" + ExcludedDirs.toString());
		System.out.println("Exculded File     :" + ExcludedFiles.toString());
		System.out.println("Inculded Dir      :" + IncludedDirs.toString());
		System.out.println("Inculded File	  :" + IncludedFiles.toString());

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
			// 执行项目的某一个目标
			executeAnt(p);
			p.fireBuildFinished(null);
		} catch (BuildException be) {
			p.fireBuildFinished(be);
		}
	}

	private static void executeAnt(Project p) {
		System.out.println("默认Target：" + p.getDefaultTarget());

		p.setUserProperty("TexturePacker_Path", TexturePacker_Path);
		p.setUserProperty("folder_path", Resources_Path);

		File file = new File(Resources_Path);
		check(file);
	}

	private static void check(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				checkDirectory(file);
			}
			// else {
			// checkFile(file);
			// }
		}
	}

	private static void checkDirectory(File dir) {
		int index = dir.getPath().indexOf(Resources_Path);
		String relativePath;
		if (dir.getPath().equals(Resources_Path)) {
			relativePath = "";
		} else {
			relativePath = dir.getPath().substring(index + Resources_Path.length() + 1, dir.getPath().length());
		}

		// 需要检查该目录是否在排除目录里
		boolean isIn = ArrayUtils.getInstance().removeWhenOccur(ExcludedDirs, relativePath);
		// for (String string : ExcludedDirs) {
		// if (string.equals(relativePath)) {
		// isIn = true;
		// ExcludedDirs.remove(string);
		// break;
		// }
		// }

		if (isIn) {
			return;
		}

		// 该目录里是否包含排除文件，如果没有的话，就打包目录，否则就是单文件打包
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// System.out.println(pathname.getPath());
				String tmp = pathname.getName();

				return !ArrayUtils.getInstance().removeWhenOccur(ExcludedFiles, tmp);
				// for (String string : ExcludedFiles) {
				// boolean isEqual = tmp.equals(string);
				// if (isEqual) {
				// ExcludedFiles.remove(string);
				// }
				// return !isEqual;
				// }
				// return true;
			}
		});

		// 继续检查、打包目录、打包单文件
		boolean hasDirectory = FileUtils.getInstance().hasDirectory(dir);

		if (!hasDirectory) {// 不存在子目录，打包目录
			if (files.length == dir.listFiles().length) {// 打包目录
				directory_target(p, dir.getAbsolutePath());
				return;
			}
		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {// 继续检查目录
				checkDirectory(file);
			} else {// 打包目录或文件
				if (!checkFile(file)) {
					continue;
				}
				// if (!file.getName().endsWith(".png")) {
				// continue;
				// }
				// single_target(p, file.getParentFile().getAbsolutePath(),
				// file.getName().substring(0, file.getName().indexOf(".")));
			}
		}
	}

	private static boolean checkFile(File file) {
		if (!file.getName().endsWith(".png")) {
			return false;
		}
		// String relativePath =
		// file.getPath().substring(file.getPath().indexOf(Resources_Path) + 1,
		// file.getPath().length());

		// // TODO 未处理
		// for (String string : ExcludedFiles) {
		// if (relativePath.equals(string)) {
		// // break;
		// return;
		// }
		// }

		// 文件是否在包含文件里
		single_target(p, file.getParentFile().getAbsolutePath(), file.getName().substring(0, file.getName().indexOf(".")));
		return true;
	}

	// 单个文件打包
	private static void single_target(Project p, String folderPath, String foldername) {
		System.out.println("single:" + folderPath + "\\" + foldername);

		String relativePath = (foldername + ".png");
		boolean isIn = ArrayUtils.getInstance().removeWhenOccur(IncludedFiles, relativePath);

		// for (String string : IncludedFiles) {
		// if (relativePath.equals(string)) {
		// isIn = true;
		// IncludedFiles.remove(string);
		// p.setUserProperty("PixelFormat", RGBA8888);
		// break;
		// }
		// }
		if (isIn) {
			p.setUserProperty("PixelFormat", RGBA8888);
		} else {
			p.setUserProperty("PixelFormat", RGBA4444);
		}

		// if (!isIn) {
		// p.setUserProperty("PixelFormat", RGBA4444);
		// }

		p.setUserProperty("folder_path", folderPath);
		p.setUserProperty("filename", foldername);
		p.executeTarget(single_target);
	}

	// 目录打包
	private static void directory_target(Project p, String dirPath) {
		System.out.println("directory:" + dirPath);
		String relativePath = dirPath.substring(dirPath.indexOf(Resources_Path) + Resources_Path.length() + 1, dirPath.length());

		boolean isIn = ArrayUtils.getInstance().removeWhenOccur(IncludedDirs, relativePath);
		// for (String string : IncludedDirs) {
		// if (relativePath.equals(string)) {
		// isIn = true;
		// IncludedDirs.remove(string);
		// p.setUserProperty("PixelFormat", RGBA8888);
		// break;
		// }
		// }

		if (!isIn) {
			p.setUserProperty("PixelFormat", RGBA4444);
		} else {
			p.setUserProperty("PixelFormat", RGBA8888);
		}

		// 目录是否是 包含目录，不是的话，是否有 包含文件
		p.setUserProperty("directory_path", dirPath);
		p.executeTarget(directory_target);
	}

	/**
	 *
	 * Description:同getClassFile 解决中文编码问题
	 *
	 * @param clazz
	 * @return
	 * @mail sunyujia@yahoo.cn
	 * @since：Sep 21, 2008 1:10:12 PM
	 */
	public static String getClassFilePath(Class clazz) {
		try {
			return java.net.URLDecoder.decode(getClassFile(clazz).getAbsolutePath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 *
	 * Description:取得当前类所在的文件
	 *
	 * @param clazz
	 * @return
	 * @mail sunyujia@yahoo.cn
	 * @since：Sep 21, 2008 12:32:10 PM
	 */
	public static File getClassFile(Class clazz) {
		URL path = clazz.getResource(clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1) + ".class");
		if (path == null) {
			String name = clazz.getName().replaceAll("[.]", "/");
			path = clazz.getResource("/" + name + ".class");
		}
		return new File(path.getFile());
	}

	/**
	 *
	 * Description: 同getClassPathFile 解决中文编码问题
	 *
	 * @param clazz
	 * @return
	 * @mail sunyujia@yahoo.cn
	 * @since：Sep 21, 2008 1:10:37 PM
	 */
	public static String getClassPath(Class clazz) {
		try {
			return java.net.URLDecoder.decode(getClassPathFile(clazz).getAbsolutePath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 *
	 * Description:取得当前类所在的ClassPath目录
	 *
	 * @param clazz
	 * @return
	 * @mail sunyujia@yahoo.cn
	 * @since：Sep 21, 2008 12:32:27 PM
	 */
	public static File getClassPathFile(Class clazz) {
		File file = getClassFile(clazz);
		for (int i = 0, count = clazz.getName().split("[.]").length; i < count; i++)
			file = file.getParentFile();
		if (file.getName().toUpperCase().endsWith(".JAR!")) {
			file = file.getParentFile();
		}
		return file;
	}
}
