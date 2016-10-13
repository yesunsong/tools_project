import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * ���Cocos2dx��Ϸ�е�������Դ
 * �÷���
 * @author yesunsong
 *
 */
public class RedundantResources {
	/** ��Դ�����·��-��Դ��С */
	private HashMap<String, String> resourcesInGame;
	/** ��Դ�����·��-��Դ��С */
	private HashMap<String, String> resourcesInLocal;
	/**������ԴĿ¼*/
	private String localDir= "F:\\mobileClient\\code\\game\\MixProject\\Resources";
	/**����ģʽ*/
	private String mode="Debug";
	/**��Ϸ��Դ��Ϣ�ļ����ڵ�Ŀ¼*/
	private String gameResInfoFile="texture_info.txt";
	
	public RedundantResources() {
		resourcesInGame = new LinkedHashMap<String, String>();
		resourcesInLocal = new LinkedHashMap<String, String>();
	}

	public void check() {
		parseResourcesInGame();
		parseResourcesInLocal();
		checkRedundantRes();
	}
	
	/**������Ϸ��ʹ�õ�����Դ*/
	private void parseResourcesInGame() {
		// ��������win
		String pattern = "proj.win32/"+mode+".win32";
		String pattern1 = "=>";
		// string
		String content = "";
		File file = new File(gameResInfoFile);
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
//					 System.out.println(line);
					String relativePath = "";
					String memorySize = "";
					int beginIndex = line.indexOf(pattern);
					int endIndex = line.lastIndexOf("\"");
					if (beginIndex != -1) {
						relativePath = line.substring(beginIndex + pattern.length() + 1, endIndex);
						memorySize = line.substring(line.indexOf(pattern1)+pattern1.length()+1,line.length());
						content += relativePath + "\n";
						relativePath=relativePath.replaceAll("/", "\\\\");
						if (!resourcesInGame.containsKey(relativePath)) {
//							System.out.println(relativePath + " >> " + memorySize);
							resourcesInGame.put(relativePath, memorySize);	
						}
					}
				}
				reader.close();
				// д���ļ�
//				save("test.txt",content);				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**�����ļ�*/
	private void save(String filename,String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**��������Ŀ¼����Դ*/
	private void parseResourcesInLocal() {
		readDirectory(localDir);
	}

	/**�������*/
	private void checkRedundantRes() {
		Iterator<Entry<String, String>> iterator=resourcesInLocal.entrySet().iterator();
		Entry<String, String> entry;
		String content="";
		while (iterator.hasNext()) {
			entry=iterator.next();
			if (!resourcesInGame.containsKey(entry.getKey())) {
				content+=entry.getKey()+"\n";
//				System.out.println("�ҵ�δʹ�õ���Դ��"+entry.getKey());
			}else{
//				System.out.println("�ҵ�ʹ�õ���Դ��"+entry.getKey());
			}
		}
		save("redundantRes.txt", content);
	}

	/** ȡָ��Ŀ¼�µ���Դ */
	private void readDirectory(String dir) {
		File file = new File(dir);
		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles();
			readFiles(files);
		}
	}

	/** �ݹ��ȡĿ¼ */
	private void readFiles(File[] files) {
		File tmpFile;
		for (int i = 0; i < files.length; i++) {
			tmpFile = files[i];
			if (tmpFile.isDirectory()) {
				readFiles(tmpFile.listFiles());
			} else {
				String pattern2 = "Resources";
				String line = tmpFile.getPath();
				String relativePath = "";
				int beginIndex = line.indexOf(pattern2);
				int endIndex = line.length();
				if (beginIndex != -1) {
					relativePath = line.substring(beginIndex + pattern2.length() + 1, endIndex);
//					System.out.println("local:" + relativePath);
					resourcesInLocal.put(relativePath, "");
				}
			}
		}
	}

	public void setLocalDir(String localDir) {
		this.localDir=localDir;
	}

	public void setMode(String mode) {
		this.mode=mode;
	}

	public void setGameResInfoFile(String gameResInfoFile) {
		this.gameResInfoFile=gameResInfoFile;		
	}
}
