public class main {

	public static void main(String[] args) {
		String sourceDir = "";
		String outputDir = "";
//		String gameResInfoFile = "";
		try {
			sourceDir = args[0];
			outputDir=args[1];
			System.out.println(sourceDir);
			System.out.println(outputDir);
//			mode = args[1];
//			gameResInfoFile = args[2];
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("��Ч�Ĳ���/��������");
			return;
		}

		GenerateCCS generateCCS=new GenerateCCS(sourceDir,outputDir);
//		RedundantResources redundantResources = new RedundantResources();
//		redundantResources.setLocalDir(localDir);
//		redundantResources.setMode(mode);
//		redundantResources.setGameResInfoFile(gameResInfoFile);
//		redundantResources.check();
	}

}