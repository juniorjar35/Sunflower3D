package juniorjar35.sunflower3d;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.lwjgl.system.Platform;

public final class Application {
	
	private Application() { };
	
	private static List<CrashReportDetail> DETAILS = new ArrayList<CrashReportDetail>();
	
	private static String APPNAME = "Sunflower3D";
	
	public static String getLibraryInfo() {
		return "Sunflower3D 1.0";
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
		
		try {
			File crashes = new File(getSavesDirectory(),"crashes");
			if (!crashes.exists() || !crashes.isDirectory()) crashes.mkdir();
			File crashReport = new File(crashes, "CrashReport-[" + getTimeFileSafe() + "].log");
			BufferedWriter writer = new BufferedWriter(new FileWriter(crashReport));
			
			writer.write("Crash report. Date: " + getTime() + "\n");
			writer.write("Library: " + getLibraryInfo());
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
			try {
				for (CrashReportDetail crd : DETAILS) {
					for (String string : crd.call()) {
						writer.write(string);
						writer.newLine();
					}
					writer.write("------------------------------------\n");
					writer.flush();
				}
			} catch(Exception e) {};
			
			writer.flush();
			writer.close();
			forceExit(-1);
		} catch (IOException e) {
		}
		
	}
	
	public static String getTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
	}
	
	public static String getTimeFileSafe() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
	}
	
}
