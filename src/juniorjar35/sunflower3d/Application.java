package juniorjar35.sunflower3d;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JOptionPane;

import org.lwjgl.system.Platform;

import juniorjar35.sunflower3d.Audio.SoundManager;

public final class Application {
	
	private Application() { };
	
	private static List<CrashReportDetail> DETAILS = new ArrayList<CrashReportDetail>();
	
	private static String APPNAME = "Sunflower3D";
	
	public static String getLibraryInfo() {
		return "Sunflower3D 1.3";
	}
	
	public static File getUserdataDirectory() {
		File directory = null;
		
		switch(Platform.get()) {
		case WINDOWS:
			directory = new File(System.getenv("APPDATA"));
			break;
		case MACOSX:
			directory = new File(System.getProperty("user.home") + "/Library/Application Support");
			break;
		case LINUX:
			directory = new File(System.getenv("user.home"));
			break;
		}
		
		return directory;
	}
	
	public static void setAppName(String name) {
		Objects.requireNonNull(name);
		if (name.length() <= 0) throw new IllegalArgumentException("Name length");
		APPNAME = name;
	}
	
	public static File getSavesDirectory() {
		File directory = new File(getUserdataDirectory(), APPNAME);
		if (!directory.exists() || !directory.isDirectory()) directory.mkdir();
		return directory;
	}
	
	public static void exit(int status) {
		System.exit(status);
	}
	
	public static void forceExit(int status) {
		Runtime.getRuntime().halt(status);
	}
	
	public static void addCrashReportDetail(CrashReportDetail crd) {
		DETAILS.add(Objects.requireNonNull(crd));
	}
	
	private static void causedBy(BufferedWriter writer, Throwable cause) throws IOException {
		if (cause.getCause() != null) {
			Throwable cb = cause.getCause();
			writer.write("Caused by:\n");
			for (StackTraceElement ste : cb.getStackTrace()) {
				writer.write("\tat " + ste.toString());
				writer.newLine();
			}
			writer.flush();
			causedBy(writer, cb);
		}
	}
	
	public static void stop(Throwable cause) {
		Objects.requireNonNull(cause);
		SoundManager.close();
		try {
			File crashes = new File(getSavesDirectory(),"crashes");
			if (!crashes.exists() || !crashes.isDirectory()) crashes.mkdir();
			File crashReport = new File(crashes, "CrashReport-[" + getTimeFileSafe() + "].log");
			BufferedWriter writer = new BufferedWriter(new FileWriter(crashReport));
			writer.write("Crash report. Date: " + getTime() + "\n");
			writer.write("Library: " + getLibraryInfo() + "\n");
			writer.write("Crash cause: " + cause.toString() + "\n");
			writer.flush();
			for (StackTraceElement ste : cause.getStackTrace()) {
				writer.write("\tat " + ste.toString());
				writer.newLine();
			}
			writer.flush();
			causedBy(writer, cause);
			writer.newLine();
			writer.write("------------------------------------\n");
			writer.flush();
			
				for (CrashReportDetail crd : DETAILS) {
					try {
						for (String string : crd.call()) {
							writer.write(string);
							writer.newLine();
						}
					} catch(Exception e) {
						writer.write("Failed to retrive details for this section!\n");
						writer.flush();
					};
					writer.write("------------------------------------\n");
					writer.flush();
				}
			
			
			writer.flush();
			writer.close();
			System.err.println("--------------GAME CRASH--------------");
			System.err.println("Crash report at: " + crashReport.getAbsolutePath());
			System.err.print("Crash cause: ");
			cause.printStackTrace(System.err);
			System.err.println("--------------GAME CRASH--------------");
			beep();
			if(JOptionPane.showConfirmDialog(null, "The game has crashed! Would you like to open the crash report", "Game crash", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION) {
				openFile(crashReport);
			}
			forceExit(-1);
		} catch (IOException e) {
		}
		
	}
	
	public static void openFile(File file) throws IOException {
		Desktop.getDesktop().open(file);
	}
	
	public static void openURL(URL url) throws IOException, URISyntaxException {
		Desktop.getDesktop().browse(url.toURI());
	}
	
	public static void beep() {
		Toolkit.getDefaultToolkit().beep();
	}
	
	public static String getTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
	}
	
	public static String getTimeFileSafe() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
	}
	
}
