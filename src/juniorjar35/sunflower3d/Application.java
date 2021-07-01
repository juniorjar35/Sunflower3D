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
import juniorjar35.sunflower3d.Utils.OpenGLUtils;

public final class Application {
	
	private Application() { };
	
	private static List<CrashReportDetail> DETAILS = new ArrayList<CrashReportDetail>();
	private static boolean DEBUG = false;
	private static String APPNAME = "Sunflower3D";
	
	public static void setDebugMode(boolean debug) {
		DEBUG = debug;
	}
	
	public static boolean isDebugModeEnabled() {
		return DEBUG;
	}
	
	public static String getLibraryInfo() {
		return "Sunflower3D 1.4.2";
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
	
	public static boolean is64bit() {
		String arch = System.getProperty("os.arch");
		return arch.contains("64") || arch.startsWith("armv8");
	}
	
	private static void causedBy(BufferedWriter writer, Throwable cause) throws IOException {
		if (cause.getCause() != null) {
			Throwable cb = cause.getCause();
			writer.write("Caused by: " + cb + "\n");
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
			String[] GPU = null;
			if (OpenGLUtils.currentThreadHasContext()) {
				GPU = OpenGLUtils.getGPUDetails();
			}
			File crashes = new File(getSavesDirectory(),"crashes");
			if (!crashes.exists() || !crashes.isDirectory()) crashes.mkdir();
			File crashReport = new File(crashes, "CrashReport-[" + getTimeDateFileSafe() + "].log");
			BufferedWriter writer = new BufferedWriter(new FileWriter(crashReport));
			writer.write("Crash report. Date: " + getTime() + "\n");
			writer.write("Library: " + getLibraryInfo() + (DEBUG ? " (DEBUG ENABLED)" : "") + "\n");
			writer.write("OS: " + System.getProperty("os.name") + " (" + (is64bit() ? "64-bit" : "32-bit") + ")\n");
			writer.write("Java version: " + System.getProperty("java.version") + "\n");
			writer.write("Java vendor: " + System.getProperty("java.vendor") + "\n");
			writer.write("\n");
			writer.write("OpenGL Renderer: " + (GPU != null ? GPU[0] : "NULL") + "\n");
			writer.write("OpenGL Vendor: " + (GPU != null ? GPU[1] : "NULL") + "\n");
			writer.write("OpenGL Version: " + (GPU != null ? GPU[2] : "NULL") + "\n");
			writer.write("OpenGL Shading language version: " + (GPU != null ? GPU[3] : "NULL") + "\n");
			writer.write("\n");
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
			OpenGLUtils.deleteContext();
			System.err.println("--------------GAME CRASH--------------");
			System.err.println("Crash report at: " + crashReport.getAbsolutePath());
			System.err.print("Crash cause: ");
			cause.printStackTrace(System.err);
			System.err.println("--------------GAME CRASH--------------");
			beep();
			if(JOptionPane.showConfirmDialog(null, "The game has crashed! Would you like to open the crash report", "Game crash", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION) {
				openFile(crashReport);
			}
			
		} catch (IOException e) {
		} finally {
			forceExit(-1);
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
	
	public static String getTimeDate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
	}
	
	public static String getTimeDateFileSafe() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
	}
	
	public static String getTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
	
}
