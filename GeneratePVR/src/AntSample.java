import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import net.sf.json.JSONObject;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntSample {
	private static String Resources_Path = "";
	private static String TexturePacker_Path = "";
	private static String semicolon = ";";
	private static String single_target = "single";
	private static String directory_target = "directory";
	// Ҫ���Ƴ� ����/�ų�Ŀ¼�Ͱ���/�ų��ļ�
	private static String[] ExculdedDirs;
	private static String[] ExculdedFiles;
	private static String[] InculdedDirs;
	private static String[] InculdedFiles;

	private static Project p;

	public static void main(String[] args) {
		String JsonContext = new Util().ReadFile("./src/config.json");
		JSONObject jsonObject = JSONObject.fromObject(JsonContext);
		Resources_Path = (String) jsonObject.get("Resources_Path");
		Resources_Path = Resources_Path.replace("/", "\\");
		TexturePacker_Path = (String) jsonObject.get("TexturePacker_Path");
		ExculdedDirs = ((String) jsonObject.get("ExculdedDir")).split(semicolon);
		ExculdedFiles = ((String) jsonObject.get("ExculdedFile")).split(semicolon);
		InculdedDirs = ((String) jsonObject.get("InculdedDir")).split(semicolon);
		InculdedFiles = ((String) jsonObject.get("InculdedFile")).split(semicolon);

		ArrayList<String> array = new ArrayList<String>();

		System.out.println("Resources Path    :" + Resources_Path);
		System.out.println("TexturePacker Path:" + TexturePacker_Path);
		System.out.println("Exculded Dir      :" + ExculdedDirs);
		System.out.println("Exculded File     :" + ExculdedFiles);
		System.out.println("Inculded Dir      :" + InculdedDirs);
		System.out.println("Inculded File	  :" + InculdedFiles);
		// }
		File file = new File(Resources_Path);
		File[] files = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				System.out.println(pathname.getPath());
				String tmp = pathname.getPath().replace("\\", "/");
				return tmp.equals(Resources_Path + "/button");
			}
		});
		System.out.println("----------" + files.length);
		//
		File buildFile = new File(".//src//generatePVR.xml");

		// ����һ��ANT��Ŀ
		p = new Project();

		// ����һ��Ĭ�ϵļ�����,������Ŀ���������е���־����
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);

		try {
			p.fireBuildStarted();
			// ��ʼ������Ŀ
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			// ������Ŀ�Ĺ����ļ�
			helper.parse(p, buildFile);
			// ִ����Ŀ��ĳһ��Ŀ��
			executeAnt(p);
			p.fireBuildFinished(null);
		} catch (BuildException be) {
			p.fireBuildFinished(be);
		}
	}

	private static void executeAnt(Project p) {
		System.out.println("Ĭ��Target��" + p.getDefaultTarget());

		p.setUserProperty("TexturePacker_Path", TexturePacker_Path);
		p.setUserProperty("folder_path", Resources_Path);

		File file = new File(Resources_Path);
		check(file);
	}

	private static void check(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				checkDirectory(file);
			} else {
				checkFile(file);
			}
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

		// ��Ҫ����Ŀ¼�Ƿ����ų�Ŀ¼��
		boolean isIn = false;
		for (int i = 0; i < ExculdedDirs.length; i++) {
			if (ExculdedDirs[i].equals(relativePath)) {
				isIn = true;
				break;
			}
		}

		if (isIn) {
			return;
		}

		// ��Ŀ¼���Ƿ�����ų��ļ������û�еĻ����ʹ��Ŀ¼��������ǵ��ļ����
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// System.out.println(pathname.getPath());
				String tmp = pathname.getName();
				for (int i = 0; i < ExculdedFiles.length; i++) {
					String tmp0 = ExculdedFiles[i];
					return !tmp.equals(tmp0);
				}
				return true;
			}
		});

		// ������顢���Ŀ¼��������ļ�
		boolean hasDirectory = false;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				hasDirectory = true;
				break;
			}
		}

		if (!hasDirectory) {// ��������Ŀ¼�����Ŀ¼
			if (files.length == dir.listFiles().length) {// ���Ŀ¼
				directory_target(p, dir.getAbsolutePath());
				return;
			}
		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {// �������Ŀ¼
				checkDirectory(file);
			} else {// ���Ŀ¼���ļ�
				if (!file.getName().endsWith(".png")) {
					continue;
				}
				single_target(p, file.getParentFile().getAbsolutePath(), file.getName().substring(0, file.getName().indexOf(".")));
			}
		}
	}

	private static void checkFile(File file) {
		if (!file.getName().endsWith(".png")) {
			return;
		}
		String relativePath = file.getPath().substring(file.getPath().indexOf(Resources_Path) + 1, file.getPath().length());
		for (int i = 0; i < ExculdedFiles.length; i++) {
			String file2 = ExculdedFiles[i];
			if (relativePath.equals(file2)) {
				break;
			}
		}

		// �ļ��Ƿ��ڰ����ļ���
		single_target(p, file.getParentFile().getAbsolutePath(), file.getName().substring(0, file.getName().indexOf(".")));
	}

	// �����ļ����
	private static void single_target(Project p, String folderPath, String foldername) {
		System.out.println("single:" + folderPath + "/" + foldername);

		p.setUserProperty("folder_path", folderPath);
		p.setUserProperty("foldername", foldername);
		p.executeTarget(single_target);
	}

	// Ŀ¼���
	private static void directory_target(Project p, String dirPath) {
		System.out.println("directory:" + dirPath);

		// Ŀ¼�Ƿ��� ����Ŀ¼�����ǵĻ����Ƿ��� �����ļ�
		p.setUserProperty("directory_path", dirPath);
		p.executeTarget(directory_target);
	}
}
