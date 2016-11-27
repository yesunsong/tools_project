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

import cn.xm.yss.JavaMacro;

public class AntSample {
	private static final String RGBA8888 = "RGBA8888";
	private static final String RGBA4444 = "RGBA4444";
	private static final String Dither_FS_ALPHA = "fs-alpha";
	private static final String Dither_NONE = "none-nn";
	private static String single_target = "single";
	private static String directory_target = "directory";

	private static String Resources_Path = "";
	private static String TexturePacker_Path = "";

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
		String user_dir = System.getProperty("user.dir");
		// 配置文件的路径
		String config_path = user_dir + File.separator + "generatePVR_cfg" + File.separator + "config.json";
		String xml_path = user_dir + File.separator + "generatePVR_cfg" + File.separator + "generatePVR.xml";
		System.out.println("-----config path:" + config_path);
		System.out.println("-----xml path:" + xml_path);

		String JsonContext = FileUtils.getInstance().ReadFile(config_path);
		JSONObject jsonObject = JSONObject.fromObject(JsonContext);

		ExcludedDirs = JSONArray2JavaArray(jsonObject, "ExculdedDir", "dir");
		ExcludedFiles = JSONArray2JavaArray(jsonObject, "ExculdedFile", "file");
		IncludedDirs = JSONArray2JavaArray(jsonObject, "InculdedDir", "dir");
		IncludedFiles = JSONArray2JavaArray(jsonObject, "InculdedFile", "file");

		if (isWin()) {
			Resources_Path = (String) jsonObject.get("Win_Resources_Path");
			TexturePacker_Path = (String) jsonObject.get("Win_TexturePacker_Path");	

			Resources_Path = StringUtils.getInstance().replace(Resources_Path, JavaMacro.SLASH, JavaMacro.BACKSLASH);
			TexturePacker_Path = StringUtils.getInstance().replace(TexturePacker_Path, JavaMacro.SLASH, JavaMacro.BACKSLASH);
			StringUtils.getInstance().replace(ExcludedDirs, JavaMacro.SLASH, JavaMacro.BACKSLASH);
			StringUtils.getInstance().replace(ExcludedFiles, JavaMacro.SLASH, JavaMacro.BACKSLASH);
			StringUtils.getInstance().replace(IncludedDirs, JavaMacro.SLASH, JavaMacro.BACKSLASH);
			StringUtils.getInstance().replace(IncludedFiles, JavaMacro.SLASH, JavaMacro.BACKSLASH);
		} else if (isMac()) {
			Resources_Path = (String) jsonObject.get("Mac_Resources_Path");
			TexturePacker_Path = (String) jsonObject.get("Mac_TexturePacker_Path");
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

	private static boolean isWin() {
		return Os.isFamily(Os.FAMILY_WINDOWS);
	}

	private static boolean isMac() {
		return Os.isFamily(Os.FAMILY_MAC);
	}

	private static void executeAnt(Project p) {
		// System.out.println("默认Target：" + p.getDefaultTarget());
		p.setUserProperty("TexturePacker_Path", TexturePacker_Path);
		p.setUserProperty("folder_path", Resources_Path);
		//
		// 先过滤只生成pvr.ccz,不生成plist的文件
		for (int i = 0; i < ExcludedFiles.size(); i++) {
			String filepath = ExcludedFiles.get(i);
			File file = new File(Resources_Path + File.separator + filepath);
			String filename = file.getName().substring(0, file.getName().indexOf("."));
			// p.setUserProperty("PixelFormat", RGBA4444);
			// p.setUserProperty("DitherMode", Dither_FS_ALPHA);
			p.setUserProperty("folder_path", file.getParentFile().getPath());
			p.setUserProperty("filename", filename);
			p.setUserProperty("plist_name", filename + "_tmp");
			p.setUserProperty("pvr_name", filename);

			// 文件是否在包含文件里
			// String relativePath = (filepath + ".png");
			boolean isIn = ArrayUtils.getInstance().removeWhenOccur(IncludedFiles, filepath);
			if (isIn) {
				p.setUserProperty("PixelFormat", RGBA8888);
				p.setUserProperty("DitherMode", Dither_NONE);
				p.setUserProperty("Use_alpha", "--premultiply-alpha");
			} else {
				p.setUserProperty("PixelFormat", RGBA4444);
				p.setUserProperty("DitherMode", Dither_FS_ALPHA);
				p.setUserProperty("Use_alpha", "");
			}

			// 1.备份文件:xxx.plist ==> xxx_tmp
			p.executeTarget("rename_file");
			// 2.生成 xxx.pvr.ccz和xxx.plist
			p.executeTarget(single_target);
			// 3.删除xxx.png
			// String parent = file.getParent();
			p.setUserProperty("delete_filename", file.getParent() + File.separator + filename + ".png");
			p.executeTarget("delete_file");
		}
		// 不可放在同一逻辑执行的原因是 tp生成pvr.ccZ和plist有段时间
		for (int i = 0; i < ExcludedFiles.size(); i++) {
			String filepath = ExcludedFiles.get(i);
			File file0 = new File(Resources_Path + File.separator + filepath);
			String filename = file0.getName().substring(0, file0.getName().indexOf("."));
			// p.setUserProperty("PixelFormat", RGBA4444);
			// p.setUserProperty("DitherMode", Dither_FS_ALPHA);
			p.setUserProperty("folder_path", file0.getParentFile().getPath());
			p.setUserProperty("filename", filename);
			p.setUserProperty("plist_name", filename + "_tmp");
			p.setUserProperty("pvr_name", filename);

			// 4.删除 xxx_tmp.plist文件
			p.setUserProperty("delete_filename", file0.getParent() + File.separator + filename + "_tmp.plist");
			p.executeTarget("delete_file");
			// 5.还原备份文件:xxx_tmp => xxx.plist
			p.executeTarget("recovery_file");
			ExcludedFiles.remove(i);
			i--;
		}
		//
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
			}
		}
	}

	private static boolean checkFile(File file) {
		if (!file.getName().endsWith(".png")) {
			return false;
		}

		single_target(p, file.getParentFile().getAbsolutePath(), file.getName().substring(0, file.getName().indexOf(".")));
		return true;
	}

	// 单个文件打包
	private static void single_target(Project p, String folderPath, String foldername) {
		System.out.println("single:" + folderPath + "\\" + foldername);
		// 文件是否在包含文件里
		String relativePath = (folderPath.substring(folderPath.indexOf(Resources_Path) + Resources_Path.length() + 1, folderPath.length())
				+ File.separator + foldername + ".png");
		if (relativePath.equals("common\\animation\\hundred_cow\\hundred_cow.png")) {
			System.out.println("");
		}
		boolean isIn = ArrayUtils.getInstance().removeWhenOccur(IncludedFiles, relativePath);

		if (isIn) {
			p.setUserProperty("PixelFormat", RGBA8888);
			p.setUserProperty("DitherMode", Dither_NONE);
			p.setUserProperty("Use_alpha", "--premultiply-alpha");
		} else {
			p.setUserProperty("PixelFormat", RGBA4444);
			p.setUserProperty("DitherMode", Dither_FS_ALPHA);
			p.setUserProperty("Use_alpha", "");
		}

		p.setUserProperty("folder_path", folderPath);
		p.setUserProperty("filename", foldername);
		p.setUserProperty("plist_name", foldername);
		p.setUserProperty("pvr_name", foldername);
		p.executeTarget(single_target);
	}

	// 目录打包
	private static void directory_target(Project p, String dirPath) {
		System.out.println("directory:" + dirPath);
		String relativePath = dirPath.substring(dirPath.indexOf(Resources_Path) + Resources_Path.length() + 1, dirPath.length());
		// 目录是否在 包含目录里，不在的话，是否有 包含文件
		boolean isIn = ArrayUtils.getInstance().removeWhenOccur(IncludedDirs, relativePath);
		if (!isIn) {
			File dir = new File(dirPath);
			if (dir.isDirectory()) {
				// File[] files = dir.listFiles(new FileFilter() {
				//
				// @Override
				// public boolean accept(File pathname) {
				// String tmp = pathname.getName();
				// return
				// !ArrayUtils.getInstance().removeWhenOccur(IncludedFiles,
				// tmp);
				// }
				// });
				// isIn=files.length>0;
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					String path = files[i].getPath();
					if (path.endsWith(".png")) {
						String folderPath = path.substring(path.indexOf(Resources_Path) + Resources_Path.length() + 1,
								path.lastIndexOf(File.separator));
						String filenameWithSuffix = path.substring(path.lastIndexOf(File.separator)+1, path.length());
						String filenameWithoutSuffix = filenameWithSuffix.substring(0, filenameWithSuffix.indexOf("."));
						if (ArrayUtils.getInstance().removeWhenOccur(IncludedFiles, folderPath + File.separator + filenameWithSuffix)) {
							p.setUserProperty("PixelFormat", RGBA8888);
							p.setUserProperty("DitherMode", Dither_NONE);
							p.setUserProperty("Use_alpha", "--premultiply-alpha");
						} else {
							p.setUserProperty("PixelFormat", RGBA4444);
							p.setUserProperty("DitherMode", Dither_FS_ALPHA);
							p.setUserProperty("Use_alpha", "");
						}
						p.setUserProperty("folder_path",Resources_Path+File.separator+ folderPath);
						p.setUserProperty("filename", filenameWithoutSuffix);
						p.setUserProperty("plist_name", filenameWithoutSuffix);
						p.setUserProperty("pvr_name", filenameWithoutSuffix);
						p.executeTarget(single_target);
					}
				}
			}
		}

		// if (isIn) {
		// p.setUserProperty("PixelFormat", RGBA8888);
		// p.setUserProperty("DitherMode", Dither_NONE);
		// p.setUserProperty("Use_alpha", "--premultiply-alpha");
		// } else {
		// p.setUserProperty("PixelFormat", RGBA4444);
		// p.setUserProperty("DitherMode", Dither_FS_ALPHA);
		// p.setUserProperty("Use_alpha", "");
		// }
		//
		// p.setUserProperty("directory_path", dirPath);
		// p.executeTarget(directory_target);
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
