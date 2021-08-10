#include <windows.h>
#include <thread>
#include <stdint.h>
#include <intrin.h>
#include <stdio.h>
#include <string>
#include <algorithm>
#include <cctype>
#include "Sunflower3d.h"

#define EAX 0
#define EBX 1
#define ECX 2
#define EDX 3

static const int SSE_EDX_1 = (1 << 25);// EDX 1
static const int SSE2_EDX_1 = (1 << 26);// EDX 1
static const int SSE3_ECX_1 = (1 << 0);// ECX 1
static const int SSE41_ECX_1 = (1 << 19);// ECX 1
static const int SSE42_ECX_1 = (1 << 20);// ECX 1
static const int AVX_ECX_1 = (1 << 28);// ECX 1
static const int AVX2_EBX_7 = (1 << 5);// EBX 7
static const int RDRAND_ECX_1 = (1 << 30);// ECX 1
static const int PCLMULQDQ_ECX_1 = (1 << 1); // ECX 1
static const int MONITOR_ECX_1 = (1 << 3);// ECX 1
static const int SSSE3_ECX_1 = (1 << 9);// ECX 1
static const int FMA_ECX_1 = (1 << 12);// ECX 1
static const int CMPXCHNG16B_ECX_1 = (1 << 13);// ECX 1
static const int MOVEBE_ECX_1 = (1 << 22);// ECX 1
static const int POPCNT_ECX_1 = (1 << 23);// ECX 1
static const int AES_ECX_1 = (1 << 25);// ECX 1
static const int XSAVE_ECX_1 = (1 << 26);// ECX 1
static const int OSXSAVE_ECX_1 = (1 << 27);// ECX 1
static const int F16C_ECX_1 = (1 << 30);// ECX 1
static const int MSR_EDX_1 = (1 << 5);// EDX 1
static const int CX8_EDX_1 = (1 << 8);// EDX 1
static const int SEP_EDX_1 = (1 << 11);// EDX 1
static const int CMOV_EDX_1 = (1 << 15);// EDX 1
static const int CLFSH_EDX_1 = (1 << 19);// EDX 1
static const int MMX_EDX_1 = (1 << 23);// EDX 1
static const int FXSE_EDX_1 = (1 << 24);// EDX 1
static const int FSGSBASE_EBX_7 = (1 << 0);// EBX 7
static const int BMI1_EBX_7 = (1 << 3);// EBX 7
static const int BMI2_EBX_7 = (1 << 8);// EBX 7
static const int ERMS_EBX_7 = (1 << 9);// EBX 7
static const int INVPCID_EBX_7 = (1 << 10);// EBX 7
static const int AVX512F_EBX_7 = (1 << 16);// EBX 7
static const int RDSEED_EBX_7 = (1 << 18);// EBX 7
static const int ADX_EBX_7 = (1 << 19);// EBX 7
static const int AVX512PF_EBX_7 = (1 << 26);// EBX 7
static const int AVX512ER_EBX_7 = (1 << 27);// EBX 7
static const int AVX512CD_EBX_7 = (1 << 28);// EBX 7
static const int SHA_EBX_7 = (1 << 29);// EBX 7
static const int PREFETCHWT1_ECX_7 = (1 << 0);// ECX 7


JNIEXPORT void JNICALL Java_juniorjar35_sunflower3d_Utils_CpuInfo_retrieveDetails0(JNIEnv* env, jobject object)
{
	jclass clazz = env->GetObjectClass(object);

	jfieldID jbrand = env->GetFieldID(clazz,"brand","Ljava/lang/String;");
	jfieldID jvendor = env->GetFieldID(clazz, "vendor", "Ljava/lang/String;");
	jfieldID jcores = env->GetFieldID(clazz, "cores", "I");
	jfieldID jlcpus = env->GetFieldID(clazz, "lcpus", "I");
	jfieldID jsse = env->GetFieldID(clazz, "sse", "Z");
	jfieldID jsse2 = env->GetFieldID(clazz, "sse2", "Z");
	jfieldID jsse3 = env->GetFieldID(clazz, "sse3", "Z");
	jfieldID jsse41 = env->GetFieldID(clazz, "sse41", "Z");
	jfieldID jsse42 = env->GetFieldID(clazz, "sse42", "Z");
	jfieldID javx = env->GetFieldID(clazz, "avx", "Z");
	jfieldID javx2 = env->GetFieldID(clazz, "avx2", "Z");
	jfieldID jrdrand = env->GetFieldID(clazz, "rdrand", "Z");
	jfieldID jpclmulqdq = env->GetFieldID(clazz, "pclmulqdq", "Z");
	jfieldID jmonitor = env->GetFieldID(clazz, "monitor", "Z");
	jfieldID jssse3 = env->GetFieldID(clazz, "ssse3", "Z");
	jfieldID jfma = env->GetFieldID(clazz, "fma", "Z");
	jfieldID jcmpxchng16b = env->GetFieldID(clazz, "cmpxchng16b", "Z");
	jfieldID jmovebe = env->GetFieldID(clazz, "movebe", "Z");
	jfieldID jpopcnt = env->GetFieldID(clazz, "popcnt", "Z");
	jfieldID jaes = env->GetFieldID(clazz, "aes", "Z");
	jfieldID jxsave = env->GetFieldID(clazz, "xsave", "Z");
	jfieldID josxsave = env->GetFieldID(clazz, "osxsave", "Z");
	jfieldID jf16c = env->GetFieldID(clazz, "f16c", "Z");
	jfieldID jmsr = env->GetFieldID(clazz, "msr", "Z");
	jfieldID jcx8 = env->GetFieldID(clazz, "cx8", "Z");
	jfieldID jsep = env->GetFieldID(clazz, "sep", "Z");
	jfieldID jcmov = env->GetFieldID(clazz, "cmov", "Z");
	jfieldID jclfsh = env->GetFieldID(clazz, "clfsh", "Z");
	jfieldID jmmx = env->GetFieldID(clazz, "mmx", "Z");
	jfieldID jfxse = env->GetFieldID(clazz, "fxse", "Z");
	jfieldID jfsgsbase = env->GetFieldID(clazz, "fxse", "Z");
	jfieldID jbmi1 = env->GetFieldID(clazz, "bmi1", "Z");
	jfieldID jbmi2 = env->GetFieldID(clazz, "bmi2", "Z");
	jfieldID jerms = env->GetFieldID(clazz, "erms", "Z");
	jfieldID jinvpcid = env->GetFieldID(clazz, "invpcid", "Z");
	jfieldID javx512f = env->GetFieldID(clazz, "avx512f", "Z");
	jfieldID jrdseed = env->GetFieldID(clazz, "rdseed", "Z");
	jfieldID jadx = env->GetFieldID(clazz, "adx", "Z");
	jfieldID javx512pf = env->GetFieldID(clazz, "avx512pf", "Z");
	jfieldID javx512er = env->GetFieldID(clazz, "avx512er", "Z");
	jfieldID javx512cd = env->GetFieldID(clazz, "avx512cd", "Z");
	jfieldID jsha = env->GetFieldID(clazz, "sha", "Z");
	jfieldID jprefetchwt1 = env->GetFieldID(clazz, "prefetchwt1", "Z");

	uint32_t regs[4] = {0};
	char vendor[0x20];
	memset(vendor, 0, sizeof(vendor));
	__cpuid((int*)regs, 0);
	uint32_t hfs = regs[EAX];
	*reinterpret_cast<int*>(vendor) = regs[EBX];
	*reinterpret_cast<int*>(vendor + 4) = regs[EDX];
	*reinterpret_cast<int*>(vendor + 8) = regs[ECX];
	jstring vendorString = env->NewStringUTF(&vendor[0]);
	env->SetObjectField(object, jvendor, vendorString);

	__cpuidex((int*)regs, 1, 0);

	bool sse = (regs[EDX] & SSE_EDX_1) == SSE_EDX_1;
	bool sse2 = (regs[EDX] & SSE2_EDX_1) == SSE2_EDX_1;
	bool sse3 = (regs[ECX] & SSE3_ECX_1) == SSE3_ECX_1;
	bool sse41 = (regs[ECX] & SSE41_ECX_1) == SSE41_ECX_1;
	bool sse42 = (regs[ECX] & SSE42_ECX_1) == SSE42_ECX_1;
	bool avx = (regs[ECX] & AVX_ECX_1) == AVX_ECX_1;
	bool rdrand = (regs[ECX] & RDRAND_ECX_1) == RDRAND_ECX_1;
	bool pclmulqdq = (regs[ECX] & PCLMULQDQ_ECX_1) == PCLMULQDQ_ECX_1;
	bool monitor = (regs[ECX] & MONITOR_ECX_1) == MONITOR_ECX_1;
	bool ssse3 = (regs[ECX] & SSSE3_ECX_1) == SSSE3_ECX_1;
	bool fma = (regs[ECX] & FMA_ECX_1) == FMA_ECX_1;
	bool cmpxchng16b = (regs[ECX] & CMPXCHNG16B_ECX_1) == CMPXCHNG16B_ECX_1;
	bool movebe = (regs[ECX] & MOVEBE_ECX_1) == MOVEBE_ECX_1;
	bool popcnt = (regs[ECX] & POPCNT_ECX_1) == POPCNT_ECX_1;
	bool aes = (regs[ECX] & AES_ECX_1) == AES_ECX_1;
	bool xsave = (regs[ECX] & XSAVE_ECX_1) == XSAVE_ECX_1;
	bool osxsave = (regs[ECX] & OSXSAVE_ECX_1) == OSXSAVE_ECX_1;
	bool f16c = (regs[ECX] & F16C_ECX_1) == F16C_ECX_1;
	bool msr = (regs[EDX] & MSR_EDX_1) == MSR_EDX_1;
	bool cx8 = (regs[EDX] & CX8_EDX_1) == CX8_EDX_1;
	bool sep = (regs[EDX] & SEP_EDX_1) == SEP_EDX_1;
	bool cmov = (regs[EDX] & CMOV_EDX_1) == CMOV_EDX_1;
	bool clfsh = (regs[EDX] & CLFSH_EDX_1) == CLFSH_EDX_1;
	bool mmx = (regs[EDX] & MMX_EDX_1) == CLFSH_EDX_1;
	bool fxse = (regs[EDX] & FXSE_EDX_1) == FXSE_EDX_1;

	__cpuidex((int*)regs, 7, 0);

	bool avx2 = (regs[EBX] & AVX2_EBX_7) == AVX2_EBX_7;
	bool fsgsbase = (regs[EBX] & FSGSBASE_EBX_7) == FSGSBASE_EBX_7;
	bool bmi1 = (regs[EBX] & BMI1_EBX_7) == BMI1_EBX_7;
	bool bmi2 = (regs[EBX] & BMI2_EBX_7) == BMI2_EBX_7;
	bool erms = (regs[EBX] & ERMS_EBX_7) == ERMS_EBX_7;
	bool invpcid = (regs[EBX] & INVPCID_EBX_7) == INVPCID_EBX_7;
	bool avx512f = (regs[EBX] & AVX512F_EBX_7) == AVX512F_EBX_7;
	bool rdseed = (regs[EBX] & AVX512F_EBX_7) == AVX512F_EBX_7;
	bool adx = (regs[EBX] & ADX_EBX_7) == ADX_EBX_7;
	bool avx512pf = (regs[EBX] & AVX512PF_EBX_7) == AVX512PF_EBX_7;
	bool avx512er = (regs[EBX] & AVX512ER_EBX_7) == AVX512ER_EBX_7;
	bool avx512cd = (regs[EBX] & AVX512CD_EBX_7) == AVX512CD_EBX_7;
	bool sha = (regs[EBX] & SHA_EBX_7) == SHA_EBX_7;
	bool prefetchwt1 = (regs[ECX] & PREFETCHWT1_ECX_7) == PREFETCHWT1_ECX_7;
	
	env->SetBooleanField(object, jsse, sse ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jsse2, sse2 ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jsse3, sse3 ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jsse41, sse41 ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, javx, avx ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, javx2, avx2 ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jrdrand, rdrand ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jpclmulqdq, pclmulqdq ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jmonitor, monitor ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jssse3, ssse3 ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jfma, fma ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jcmpxchng16b, cmpxchng16b ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jmovebe, movebe ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jpopcnt, popcnt ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jaes, aes ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jxsave, xsave ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, josxsave, osxsave ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jf16c, f16c ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jmsr, msr ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jcx8, cx8 ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jsep, sep ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jcmov, cmov ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jclfsh, clfsh ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jmmx, mmx ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jfxse, fxse ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jfsgsbase, fsgsbase ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jbmi1, bmi1 ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jbmi2, bmi2 ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jerms, erms ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jinvpcid, invpcid ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, javx512f, avx512f ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jrdseed, rdseed ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jadx, adx ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, javx512pf, avx512pf ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, javx512er, avx512er ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, javx512cd, avx512cd ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jsha, sha ? JNI_TRUE : JNI_FALSE);
	env->SetBooleanField(object, jprefetchwt1, prefetchwt1 ? JNI_TRUE : JNI_FALSE);

	std::string v(vendor);
	std::transform(v.begin(), v.end(), v.begin(), [](unsigned char c) {return std::tolower(c);});

	int cores = std::thread::hardware_concurrency();
	
	SYSTEM_INFO sysinfo;
	GetSystemInfo(&sysinfo);
	int lcpus = sysinfo.dwNumberOfProcessors;;

	env->SetIntField(object, jcores, cores);
	env->SetIntField(object, jlcpus, lcpus);

	char brand[0x40];
	memset((int*)regs, 0, sizeof(regs));
	__cpuid((int*)regs, 0x80000002);
	memcpy(brand, regs, sizeof(regs));
	__cpuid((int*)regs, 0x80000003);
	memcpy(brand + 16, regs, sizeof(regs));
	__cpuid((int*)regs, 0x80000004);
	memcpy(brand + 32, regs, sizeof(regs));
	jstring brandString = env->NewStringUTF(brand);
	env->SetObjectField(object, jbrand, brandString);
}

bool isRDRAND() 
{
	uint32_t regs[4] = {0};
	__cpuid((int*)regs, 1);
	return (regs[ECX] & RDRAND_ECX_1) == RDRAND_ECX_1;
}

bool isRDSEED()
{
	uint32_t regs[4] = { 0 };
	__cpuid((int*)regs, 7);
	return (regs[EBX] & RDSEED_EBX_7) == RDSEED_EBX_7;
}

void Java_exception_RDRAND(JNIEnv* env) 
{
	jclass clazz = env->FindClass("java/lang/UnsupportedOperationException");
	env->ThrowNew(clazz, "RDRAND is not supported by CPU!");
}

void Java_exception_RDSEED(JNIEnv* env)
{
	jclass clazz = env->FindClass("java/lang/UnsupportedOperationException");
	env->ThrowNew(clazz, "RDSEED is not supported by CPU!");
}


JNIEXPORT jshort JNICALL Java_juniorjar35_sunflower3d_Utils_Utils_RDRAND160(JNIEnv* env, jclass) 
{
	if (!isRDRAND()) {
		Java_exception_RDRAND(env);
		return 0;
	}
	jshort rn = 0;
	_rdrand16_step((uint16_t*) &rn);
	return rn;
};

JNIEXPORT void JNICALL Java_juniorjar35_sunflower3d_Utils_Utils_RDSEED160(JNIEnv* env, jclass, jshort seed) 
{
	if (!isRDSEED()) {
		Java_exception_RDSEED(env);
		return;
	}
	_rdseed16_step((uint16_t*) &seed);
};

JNIEXPORT jint JNICALL Java_juniorjar35_sunflower3d_Utils_Utils_RDRAND320(JNIEnv* env, jclass) 
{
	if (!isRDRAND()) {
		Java_exception_RDRAND(env);
		return 0;
	}
	jint rn = 0;
	_rdrand32_step((uint32_t*) &rn);
	return rn;
};

JNIEXPORT void JNICALL Java_juniorjar35_sunflower3d_Utils_Utils_RDSEED320(JNIEnv* env, jclass, jint seed) 
{
	if (!isRDSEED()) {
		Java_exception_RDRAND(env);
		return;
	}
	_rdseed32_step((uint32_t*) &seed);
};

JNIEXPORT jlong JNICALL Java_juniorjar35_sunflower3d_Utils_Utils_RDRAND640(JNIEnv* env, jclass cls) 
{
#ifdef _M_AMD64 || _M_X64
	if (!isRDRAND()) {
		Java_exception_RDRAND(env);
		return 0;
	}
	jlong rn = 0;
	_rdrand64_step((uint64_t*) &rn);
	return rn;
#else
	return Java_juniorjar35_sunflower3d_Utils_Utils_RDRAND320(env, cls);
#endif
};

JNIEXPORT void JNICALL Java_juniorjar35_sunflower3d_Utils_Utils_RDSEED640(JNIEnv* env, jclass cls, jlong seed) 
{
#ifdef _M_AMD64 || _M_X64
	if (!isRDSEED()) {
		Java_exception_RDRAND(env);
		return;
	}
	_rdseed64_step((uint64_t*) &seed);
#else
	Java_juniorjar35_sunflower3d_Utils_Utils_RDSEED320(env, cls, seed);
#endif
};
