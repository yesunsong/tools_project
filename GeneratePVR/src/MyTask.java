import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class MyTask extends Task {
	private String mDir;
	private String mTarget;
	// 使用ant获取文件列表
	private Vector filesets = new Vector();

	@Override
	public void execute() throws BuildException {
		System.out.println("super.execute");
		System.out.println(mDir);
		System.out.println(mTarget);

		String[] includedFiles = null;
		for (Iterator iterator = filesets.iterator(); iterator.hasNext();) {
			FileSet fs = (FileSet) iterator.next();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			includedFiles = ds.getIncludedFiles();
			System.out.println("++++++++++++");
			// System.out.println(includedFiles.toString());

			for (int i = 0; i < includedFiles.length; i++) {
				String tmp = includedFiles[i];
				if (!tmp.endsWith(".png")) {
					continue;
				}
				String foldername=tmp.substring(0, tmp.indexOf("."));
				getProject().setUserProperty("folder_path", fs.getDir().getPath());
				getProject().setUserProperty("filename", foldername);
				getProject().setUserProperty("plist_name", foldername);
				getProject().setUserProperty("pvr_name", foldername);
				getProject().executeTarget("single");
			}
		}

	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	public void setTarget(String target) {
		mTarget = target;
	}

	// 绝对路径
	// 一定要是setXXX,XXX不区分大小
	public void setDir(String dir) {
		mDir = dir;
	}

}
