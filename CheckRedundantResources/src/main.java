public class main {

	public static void main(String[] args) {
		String localDir = "";
		String mode = "";
		String gameResInfoFile = "";
		try {
			localDir = args[0];
			mode = args[1];
			gameResInfoFile = args[2];
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("无效的参数/参数不足");
			return;
		}

		RedundantResources redundantResources = new RedundantResources();
		redundantResources.setLocalDir(localDir);
		redundantResources.setMode(mode);
		redundantResources.setGameResInfoFile(gameResInfoFile);
		redundantResources.check();
	}

}