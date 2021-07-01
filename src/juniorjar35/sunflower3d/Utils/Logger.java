package juniorjar35.sunflower3d.Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;

import juniorjar35.sunflower3d.Application;

public class Logger {
	private static BufferedWriter logWriter = null;
	private static final PrintStream STDOUT = new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out),128),true);
	private static final PrintStream STDERR = new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.err),128),true);
	
	public static DebugPrintStream DEBUG = null;
	public static DebugPrintStream WARN = null;
	
	public static void setFile(File file) throws IOException {
		Objects.requireNonNull(file);
		if (logWriter != null) throw new IllegalStateException("Log file already set!");
		logWriter = new BufferedWriter(new FileWriter(file));
	}
	
	public static void override() {
		System.setOut(new DebugPrintStream(new BufferedOutputStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {}
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				log(new String(b, off,len));
			}
			
		},128),true));
		
		System.setErr(new DebugPrintStream(new BufferedOutputStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {}
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				error(new String(b,off,len));
			}
			
		},128),true));
		
		
		DEBUG = (new DebugPrintStream(new BufferedOutputStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {}
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				debug(new String(b,off,len));
			}
			
		},128),true));
		
		WARN = (new DebugPrintStream(new BufferedOutputStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {}
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				warn(new String(b,off,len));
			}
			
		},128),true));
		
	}
	
	private static void writeFile(String f) {
		if (logWriter != null) {
			try {
				
				logWriter.write(f);
				logWriter.newLine();
				logWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void log(String msg) {
		if (msg == null) return;
		if (msg.length() == 0) return;
		String f = "[" + Application.getTime() + "] [" + Thread.currentThread().getName() + "] [INFO]: " + msg;
		STDOUT.println(f);
		writeFile(f);
	}
	
	public static void warn(String msg) {
		if (msg == null) return;
		if (msg.length() == 0) return;
		String f = "[" + Application.getTime() + "] [" + Thread.currentThread().getName() + "] [WARN]: " + msg;
		STDOUT.println(f);
		writeFile(f);
	};
	
	public static void debug(String msg) {
		if (!Application.isDebugModeEnabled()) return;
		if (msg == null) return;
		if (msg.length() == 0) return;
		String f = "[" + Application.getTime() + "] [" + Thread.currentThread().getName() + "] [DEBUG]: " + msg;
		STDOUT.println(f);
		writeFile(f);
	}
	
	
	public static void error(String msg) {
		if (msg == null) return;
		if (msg.length() == 0) return;
		String f = "[" + Application.getTime() + "] [" + Thread.currentThread().getName() + "] [ERROR]: " + msg;
		STDERR.println(f);
		writeFile(f);
	}
	
	private Logger() { }
}
