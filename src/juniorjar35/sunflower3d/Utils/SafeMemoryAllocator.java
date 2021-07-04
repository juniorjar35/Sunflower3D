package juniorjar35.sunflower3d.Utils;

import org.lwjgl.system.jemalloc.JEmallocAllocator;

public class SafeMemoryAllocator extends JEmallocAllocator {
	
	@Override
	public long malloc(long size) {
		long addr = super.malloc(size);
		if (addr == 0) throw new RuntimeException("Bad alloc: failed to allocate " + size + " bytes!");
		return addr;
	}
	
	@Override
	public void free(long ptr) {
		if (ptr <= 0) throw new RuntimeException("Bad address: cannot free address 0x" + Long.toHexString(ptr) + " (" + ptr +")");
		super.free(ptr);
	}
	
	@Override
	public long calloc(long num, long size) {
		long addr = super.calloc(num, size);
		if (addr == 0) throw new RuntimeException("Bad alloc: failed to allocate " + num + " objects with " + size + " size!");
		return addr;
	}
	
	@Override
	public long realloc(long ptr, long size) {
		long addr = super.realloc(ptr, size);
		if (addr == 0) throw new RuntimeException("Bad alloc: failed to reallocate to " + size + " size! Old address: 0x" + Long.toHexString(addr));
		return addr;
	}
	
	@Override
	public long aligned_alloc(long alignment, long size) {
		long addr = super.aligned_alloc(alignment, size);
		if (addr == 0) throw new RuntimeException("Bad alloc: Unable to allocate " + size + " bytes with " + alignment + " alignment!");
		return addr;
	}
	
	@Override
	public void aligned_free(long ptr) {
		if (ptr <= 0) throw new RuntimeException("Bad address: cannot free address 0x" + Long.toHexString(ptr) + " (" + ptr +")");
		super.aligned_free(ptr);
	}
	
}
