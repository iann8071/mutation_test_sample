import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class MutationTestExecuter {

	public static String pathToJsFile = "/var/www/...";
	public static String pathToMutantsDir = "/path/to/mutant_dir";
	public static String testClassName = "SampleTest";

	public void start() throws IOException, ClassNotFoundException {

		PrintWriter pw;
		File file = new File("log.txt");
		if (file.exists()) {
			file.delete();
		}

		File jsFile = new File(pathToJsFile);
		File jsFileBackup = new File(pathToJsFile + ".backup");
		if (!jsFileBackup.exists()) {
			copyFile(jsFile, jsFileBackup);
		} else {
			copyFile(jsFileBackup, jsFile);
		}

		Result result = JUnitCore.runClasses(Class.forName(testClassName));
		pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
				new File("log.txt"), true)));
		if (!result.wasSuccessful()) {
			pw.println("test to original js file has failed. finish mutation testing.");
			pw.close();
			return;
		}

		File mutantsDir = new File(pathToMutantsDir);

		for (File mutant : mutantsDir.listFiles()) {
			if(mutant.getName().contains("~") || mutant.getName().startsWith(".")) {
				continue;
			}
			
			copyFile(mutant, jsFile);
			try {
				result = JUnitCore.runClasses(Class.forName(testClassName));
				if (result.wasSuccessful()) {
					pw.println(mutant.getName() + ": unkilled");
				} else {
					pw.println(mutant.getName() + ": killed");
					for (Failure f : result.getFailures()) {
						pw.println("test method:"
								+ f.getDescription().getMethodName());
					}
				}
				pw.println();
				pw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		pw.close();
		
		copyFile(jsFileBackup, jsFile);
	}

	public void copyFile(File srcPath, File destPath) throws IOException {

		FileChannel srcChannel = new FileInputStream(srcPath).getChannel();
		FileChannel destChannel = new FileOutputStream(destPath).getChannel();
		try {
			srcChannel.transferTo(0, srcChannel.size(), destChannel);
		} finally {
			srcChannel.close();
			destChannel.close();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new MutationTestExecuter().start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
