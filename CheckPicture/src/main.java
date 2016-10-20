import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 检查png图的位深度（8，24,32）
 * @author yesunsong
 *
 */
public class main {

	private static ArrayList<String> breakFiles;
	private static ArrayList<String> breakDirs;
	private static StringBuffer buffer;

	public static void main(String[] args) {
//		String checkPATH = "F:\\HuangGuan\\mobileClient\\code\\game\\MixProject\\Resources\\";
		String checkPATH = "F:\\HuangGuan\\chengpinmeishuziyuan";
//		String resource = "F:\\HuangGuan\\mobileClient\\code\\game\\MixProject\\Resources\\";
		String resource = "F:\\HuangGuan\\chengpinmeishuziyuan";
		breakFiles = new ArrayList<String>();
		breakFiles.add(resource + "common\\head\\69.png");
		breakFiles.add(resource + "Games\\BJL\\game\\BJLStudio\\Layergame\\game_pokers.png");
		breakFiles.add(resource + "Games\\BJL\\game\\BJLStudio\\Layergame\\game_user_bg.png");
		breakFiles.add(resource + "Games\\goldenToad\\GameTableUi\\Res\\bg1.png");
		breakFiles.add(resource + "Games\\goldenToad\\notic\\control.png");
		breakFiles.add(resource + "Games\\goldenToad\\GameChatUi\\Res\\smiley_36.png");
		breakFiles.add(resource + "Games\\goldenToad\\GameChatUi\\Res\\smiley_71.png");
		breakFiles.add(resource + "Games\\goldenToad\\particle\\bigwin.png");
		breakFiles.add(resource + "Games\\goldenToad\\particle\\dazhongyu.png");
		breakFiles.add(resource + "Games\\goldenToad\\particle\\shark.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_01.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_02.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_03.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_04.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_05.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_06.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_07.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_08.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_09.png");
		breakFiles.add(resource + "Games\\goldenToad\\UserBoard\\Res\\bingo1_10.png");

		breakFiles.add(resource + "Games\\NN\\cards\\00.png");
		breakFiles.add(resource + "Games\\NN\\cards\\01.png");
		breakFiles.add(resource + "Games\\NN\\cards\\02.png");
		breakFiles.add(resource + "Games\\NN\\cards\\03.png");
		breakFiles.add(resource + "Games\\NN\\cards\\04.png");
		breakFiles.add(resource + "Games\\NN\\cards\\05.png");
		breakFiles.add(resource + "Games\\NN\\cards\\06.png");
		breakFiles.add(resource + "Games\\NN\\cards\\07.png");
		breakFiles.add(resource + "Games\\NN\\cards\\08.png");
		breakFiles.add(resource + "Games\\NN\\cards\\09.png");
		breakFiles.add(resource + "Games\\NN\\cards\\0a.png");
		breakFiles.add(resource + "Games\\NN\\cards\\11.png");
		breakFiles.add(resource + "Games\\NN\\cards\\12.png");
		breakFiles.add(resource + "Games\\NN\\cards\\13.png");
		breakFiles.add(resource + "Games\\NN\\cards\\14.png");
		breakFiles.add(resource + "Games\\NN\\cards\\15.png");
		breakFiles.add(resource + "Games\\NN\\cards\\16.png");
		breakFiles.add(resource + "Games\\NN\\cards\\17.png");
		breakFiles.add(resource + "Games\\NN\\cards\\18.png");
		breakFiles.add(resource + "Games\\NN\\cards\\19.png");
		breakFiles.add(resource + "Games\\NN\\cards\\1a.png");
		breakFiles.add(resource + "Games\\NN\\cards\\21.png");
		breakFiles.add(resource + "Games\\NN\\cards\\22.png");
		breakFiles.add(resource + "Games\\NN\\cards\\23.png");
		breakFiles.add(resource + "Games\\NN\\cards\\24.png");
		breakFiles.add(resource + "Games\\NN\\cards\\25.png");
		breakFiles.add(resource + "Games\\NN\\cards\\26.png");
		breakFiles.add(resource + "Games\\NN\\cards\\27.png");
		breakFiles.add(resource + "Games\\NN\\cards\\28.png");
		breakFiles.add(resource + "Games\\NN\\cards\\29.png");
		breakFiles.add(resource + "Games\\NN\\cards\\2a.png");
		breakFiles.add(resource + "Games\\NN\\cards\\31.png");
		breakFiles.add(resource + "Games\\NN\\cards\\32.png");
		breakFiles.add(resource + "Games\\NN\\cards\\33.png");
		breakFiles.add(resource + "Games\\NN\\cards\\34.png");
		breakFiles.add(resource + "Games\\NN\\cards\\35.png");
		breakFiles.add(resource + "Games\\NN\\cards\\36.png");
		breakFiles.add(resource + "Games\\NN\\cards\\37.png");
		breakFiles.add(resource + "Games\\NN\\cards\\38.png");
		breakFiles.add(resource + "Games\\NN\\cards\\39.png");
		breakFiles.add(resource + "Games\\NN\\cards\\3a.png");		

		breakDirs = new ArrayList<String>();
		breakDirs.add(resource + "Games\\goldenToad\\AdminUi\\Res");
		breakDirs.add(resource + "Games\\goldenToad\\fishAnimation");
		breakDirs.add(resource + "goldenToad\\GameChatUi\\Res");
		breakDirs.add(resource + "common\\animation");
		breakDirs.add(resource + "platform\\store");
		breakDirs.add(resource + "platform\\Sign");
		breakDirs.add(resource + "platform\\match");
		breakDirs.add(resource + "platform\\GiftShopUi");
		breakDirs.add(resource + "platform\\gameList\\gameItemRes");
		breakDirs.add(resource + "platform\\lobbyUi\\PlatformUi_BOTTOM");
		breakDirs.add(resource + "platform\\lobbyUi\\PlatformUi_TOP");
		breakDirs.add(resource + "platform\\ranking");
		breakDirs.add(resource + "platform\\RechargeRebate\\Res");
		breakDirs.add(resource + "platform\\room");
		breakDirs.add(resource + "platform\\onlineReward\\Res");
		breakDirs.add(resource + "platform\\popularise");
		breakDirs.add(resource + "platform\\serviceUi\\serviceUiRes");

		buffer = new StringBuffer();
		checkRecursive(checkPATH);
		save("png8.txt", buffer.toString());
	}

	/** 保存文件 */
	private static void save(String filename, String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
			// 指定编码格式，不指定中文会出现乱码
			String characterSet = "UTF-8";
			String str2 = new String(content.getBytes(characterSet), characterSet);
			//
			writer.write(str2);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void checkRecursive(String path) {
		File file = new File(path);
		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File temp = files[i];
				if (temp.exists()) {
					String tempStr = temp.getPath();

					if (temp.isFile()) {
						if (isBreakFile(tempStr)) {
							continue;
						}
						if (temp.getName().endsWith(".png")) {
							int bitDepth = checkPngBitDepth(temp.getPath());
							if (bitDepth == 8) {
								buffer.append(temp.getPath()).append("\n\n");
								System.out.println(temp.getPath());
							}
						}
					} else if (temp.isDirectory()) {
						if (isBreakDir(tempStr)) {
							continue;
						}
						checkRecursive(temp.getPath());
					}
				}
			}
		}
	}

	private static boolean isBreakFile(String targetFile) {
		for (String string : breakFiles) {
			if (string.equals(targetFile)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isBreakDir(String targetFile) {
		for (String string : breakDirs) {
			if (string.equals(targetFile)) {
				return true;
			}
		}
		return false;
	}

	private static String addSlash(String source) {
		return source.replace("\\", "\\\\");
	}

	/** 检查png的位深度 */
	private static int checkPngBitDepth(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = fis.read(buff)) != -1) {
				bos.write(buff, 0, len);
			}
			// 得到图片的字节数组
			byte[] result = bos.toByteArray();
			// 字节数组转成十六进制
			String str = byte2HexStr(result);
			// System.out.println("++++" + byte2HexStr(result));
			// System.out.println(str);
			// png位深度
			int bitsPerPixel = result[24] & 0xff;
			if ((result[25] & 0xff) == 2) {
				bitsPerPixel *= 3;
			} else if ((result[25] & 0xff) == 6) {
				bitsPerPixel *= 4;
			}
			// System.out.println(bitsPerPixel);
			return bitsPerPixel;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/** 实现字节数组向十六进制的转换方法 */
	public static String byte2HexStr(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < 9; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}
}