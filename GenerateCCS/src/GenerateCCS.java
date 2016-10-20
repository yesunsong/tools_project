import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GenerateCCS {
	private String cocosstudioDir = "";

	public GenerateCCS(String sourceDir, String outputDir) {
		// 把F:\Myself\mobileClient\code\game\MixProject\Resources下的文件拷贝到
		// F:\Myself\mobileClient\code\game\MixProject\cocosstudio目录下

		cocosstudioDir = sourceDir;

		StringBuffer buffer = new StringBuffer();
		buffer.append("<Solution>").append("\n");
		buffer.append(
				"<PropertyGroup Name=\"HuangGuangPro\" Version=\"3.10.0.0\" Type=\"CocosStudio\" />")
				.append("\n");
		buffer.append("<SolutionFolder>").append("\n");
		buffer.append("<Group ctype=\"ResourceGroup\">").append("\n");
		buffer.append("<RootFolder Name=\".\">").append("\n");

		File resource = new File(cocosstudioDir);
		if (resource.isDirectory()) {
			for (int i = 0; i < resource.listFiles().length; i++) {
				File tmp = resource.listFiles()[i];
				buffer.append(getBaseFolder(tmp));
			}
		}

		buffer.append("</RootFolder>").append("\n");
		buffer.append("</Group>").append("\n");
		buffer.append("</SolutionFolder>").append("\n");
		buffer.append("</Solution>").append("\n");

//		System.out.println(buffer.toString());

		File file = new File(outputDir + File.separator + "HuangGuangPro.ccs");
		if (file.exists()) {
			file.delete();
		}
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
			writer.write(buffer.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getBaseFolder(File file) {
		StringBuffer buffer = new StringBuffer();

		boolean isDir = file.isDirectory();
		if (isDir) {
			buffer.append(getStartFolder(file.getName()));
		}

		if (isDir) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File tmp = files[i];
				buffer.append(getBaseFolder(tmp));
			}
		}

		if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")
				|| file.getName().endsWith(".PNG")
				|| file.getName().endsWith(".JPG")) {
			buffer.append(getPNG(file.getName()));
		}

		if (file.getName().endsWith(".csd")) {
			buffer.append(getCSD(file));
		}

		if (file.getName().endsWith(".fnt")) {
			buffer.append(getFNT(file.getName()));
		}
		
		if(file.getName().endsWith(".ExportJson")){
			buffer.append(getExportJson(file.getName()));
		}	
//		
		if(file.getName().endsWith(".ttf")){
			buffer.append(getTTF(file.getName()));
		}	
		
		if (isDir) {
			buffer.append(getEndFolder(file.getName()));
		}

		return buffer.toString();
	}

	private String getTTF(String name) {
		return "<TTF Name=\""+name+"\"/>";
	}

	private String getExportJson(String name) {
		return "<ExportJsonFile Name=\""+name+"\"/>";
	}

	private String getCSD(File file) {
		String type = "";
		try {
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String text = "";
			while (!(text = reader2.readLine()).equals("")) {
				if (text.contains("Type=\"")) {
					int index = text.indexOf("Type=\"");
					String tmpString = text.substring(
							index + "Type=\"".length(), text.length() - 1);
					int secIndex = tmpString.indexOf("\"");
					if (secIndex != -1) {
						type = tmpString.substring(0, secIndex);
						break;
					}
				}
			}
			reader2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "<Project Name=\"" + file.getName() + "\"" + " Type=\"" + type
				+ "\" />\n";
	}

	private Object getFNT(String name) {
		return "<Fnt Name=\""+name+"\" />\n";
	}

	private String getPNG(String name) {
		
		return "<Image Name=\"" + name + "\" />\n";
	}

	private String getEndFolder(String name) {
		return "</Folder>\n";
	}

	private String getStartFolder(String name) {
		return "<Folder Name=\"" + name + "\">\n";
	}

}

// HuangGuangPro.ccs文件结构如下
// <Solution>
// <PropertyGroup Name="HuangGuangPro" Version="3.10.0.0" Type="CocosStudio" />
// <SolutionFolder>
// <Group ctype="ResourceGroup">
// <RootFolder Name=".">
// <Folder Name="button" />
// <Folder Name="common">
// <Folder Name="fnt_number">
// <Image Name="blue0.png" />
// <Image Name="blue1.png" />
// <Image Name="red0.png" />
// <Image Name="yellow0.png" />
// <Image Name="yellow1.png" />
// <Image Name="yellow2.png" />
// <Image Name="yellow3.png" />
// </Folder>
// </Folder>
// <Folder Name="csb">
// <Folder Name="BRNN">
// <Project Name="cow_settlement.csd" Type="Node" />
// <Project Name="test.csd" Type="Node" />
// </Folder>
// </Folder>
// <Folder Name="Games">
// <Folder Name="BJL">
// <Folder Name="game">
// <Folder Name="settlement">
// <Folder Name="gametable">
// <Image Name="game_result.png" />
// <Fnt Name="number.fnt" />
// </Folder>
// </Folder>
// </Folder>
// </Folder>
// </Folder>
// </RootFolder>
// </Group>
// </SolutionFolder>
// </Solution>