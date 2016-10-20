import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {

	public String ReadFile(String Path) {
		BufferedReader reader = null;
		String laststr = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(Path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return laststr;
	}

	/**
	 * 
	 * @param instance
	 * @param path 相对路径
	 * @return
	 */
	public String readFileInJar(Class instance,String path) {
		StringBuffer title = new StringBuffer();

		// 获取jar中资源的输入流
		InputStream is = this.getClass().getResourceAsStream(path);

		BufferedReader buff = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = buff.readLine()) != null) {
				title.append(line);
			}
			buff.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return title.toString();
	}

}