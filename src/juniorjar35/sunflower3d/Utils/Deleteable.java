package juniorjar35.sunflower3d.Utils;

import java.io.Closeable;

public interface Deleteable extends AutoCloseable, Closeable {
	
	public void delete();
	
	@Override
	default void close() {
		delete();
	}
}
