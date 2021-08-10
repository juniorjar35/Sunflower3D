package juniorjar35.sunflower3d.Utils;

public class CpuInfo {
	
	static {
		Utils.loadNativeLibrary();
	}
	
	private String brand, vendor;
	private int cores, lcpus;
	private boolean sse,
					sse2,
					sse3,
					sse41,
					sse42,
					avx,
					avx2,
					rdrand,
					pclmulqdq,
					monitor,
					ssse3,
					fma,
					cmpxchng16b,
					movebe,
					popcnt,
					aes,
					xsave,
					osxsave,
					f16c,
					msr,
					cx8,
					sep,
					cmov,
					clfsh,
					mmx,
					fxse,
					fsgsbase,
					bmi1,
					bmi2,
					erms,
					invpcid,
					avx512f,
					rdseed,
					adx,
					avx512pf,
					avx512er,
					avx512cd,
					sha,
					prefetchwt1;
	
	public CpuInfo() {
	}
	
	private native void retrieveDetails0();
	
	public void retrieveDetails() {
		Utils.checkIfLoaded();
		retrieveDetails0();
	}
	
	public String getCpuBrand() {
		return brand;
	}
	
	public String getCpuVendor() {
		return vendor;
	}
	
	public int getCpuCores() {
		return cores;
	}
	
	public int getLogicalCpuCores() {
		return lcpus;
	}

	public String getBrand() {
		return brand;
	}

	public String getVendor() {
		return vendor;
	}

	public int getCores() {
		return cores;
	}

	public int getLcpus() {
		return lcpus;
	}

	public boolean SSE() {
		return sse;
	}

	public boolean SSE2() {
		return sse2;
	}

	public boolean SSE3() {
		return sse3;
	}

	public boolean SSE41() {
		return sse41;
	}

	public boolean SSE42() {
		return sse42;
	}

	public boolean AVX() {
		return avx;
	}

	public boolean AVX2() {
		return avx2;
	}

	public boolean RDRAND() {
		return rdrand;
	}

	public boolean PCLMULQDQ() {
		return pclmulqdq;
	}

	public boolean MONITOR() {
		return monitor;
	}

	public boolean SSSE3() {
		return ssse3;
	}

	public boolean FMA() {
		return fma;
	}

	public boolean CMPXCHNG16B() {
		return cmpxchng16b;
	}

	public boolean MOVEBE() {
		return movebe;
	}

	public boolean POPCNT() {
		return popcnt;
	}

	public boolean AES() {
		return aes;
	}

	public boolean XSAVE() {
		return xsave;
	}

	public boolean OSXSAVE() {
		return osxsave;
	}

	public boolean F16C() {
		return f16c;
	}

	public boolean MSR() {
		return msr;
	}

	public boolean CX8() {
		return cx8;
	}

	public boolean SEP() {
		return sep;
	}

	public boolean CMOV() {
		return cmov;
	}

	public boolean CLFSH() {
		return clfsh;
	}

	public boolean MMX() {
		return mmx;
	}

	public boolean FXSE() {
		return fxse;
	}

	public boolean FSGSBASE() {
		return fsgsbase;
	}

	public boolean BMI1() {
		return bmi1;
	}

	public boolean BMI2() {
		return bmi2;
	}

	public boolean ERMS() {
		return erms;
	}

	public boolean INCPCID() {
		return invpcid;
	}

	public boolean AVX512F() {
		return avx512f;
	}

	public boolean RDSEED() {
		return rdseed;
	}

	public boolean ADX() {
		return adx;
	}

	public boolean AVX512PF() {
		return avx512pf;
	}

	public boolean AVX512ER() {
		return avx512er;
	}

	public boolean AVX512CD() {
		return avx512cd;
	}

	public boolean SHA() {
		return sha;
	}

	public boolean PREFERCHWT1() {
		return prefetchwt1;
	}
	
	
}
