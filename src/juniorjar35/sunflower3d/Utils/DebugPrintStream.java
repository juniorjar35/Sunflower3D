package juniorjar35.sunflower3d.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class DebugPrintStream extends PrintStream {
	
	public DebugPrintStream(OutputStream out) {
		super(out);
	}

	public DebugPrintStream(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	public DebugPrintStream(File file) throws FileNotFoundException {
		super(file);
	}

	public DebugPrintStream(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public DebugPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	public DebugPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
	}

	public DebugPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
	}
	
	@Override
	public void println(boolean x) {
		super.print(x);
	}
	
	@Override
	public void println(char x) {
		super.print(x);
	}
	
	@Override
	public void println(char[] x) {
		super.print(x);
	}
	
	@Override
	public void println(double x) {
		super.print(x);
	}
	
	@Override
	public void println(float x) {
		super.print(x);
	}
	
	@Override
	public void println(int x) {
		super.print(x);
	}
	
	@Override
	public void println(long x) {
		super.print(x);
	}
	
	@Override
	public void println(Object x) {
		super.print(x);
	}
	
	@Override
	public void println(String x) {
		super.print(x);
	}

}
