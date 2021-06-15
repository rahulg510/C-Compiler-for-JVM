.class public TestFunction
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	TestFunction/_sysin Ljava/util/Scanner;
	return

.limit locals 0
.limit stack 3
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this LTestFunction;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

;
; FUNCTION exp
;
.method private static exp(II)I

.var 0 is num I
.var 1 is power I
;
; 005 inti=0;
;
	iconst_0
	istore_3
;
; 006 intresult=1;
;
	iconst_1
	istore	4
;
; 007 while(i<power){result=result*num;i=i+1;}
;
L001:
	iload_3
	iload_1
	if_icmplt	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifeq	L002
;
; 009 result=result*num;
;
	iload	4
	iload_0
	imul
	istore	4
;
; 010 i=i+1;
;
	iload_3
	iconst_1
	iadd
	istore_3
	goto	L001
L002:
;
; 013 returnresult;
;
	iload	4

	istore_2
	iload_2
	ireturn

.limit locals 5
.limit stack 15
.end method

;
; FUNCTION printpoweroftwo
;
.method private static printpoweroftwo(I)V

.var 0 is limit I
;
; 018 inti=0;
;
	iconst_0
	istore_2
;
; 019 while(i<=limit){print("%d\n",exp(2,i));i=i+1;}
;
L005:
	iload_2
	iload_0
	if_icmple	L007
	iconst_0
	goto	L008
L007:
	iconst_1
L008:
	ifeq	L006
;
; 021 print("%d\n",exp(2,i));
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iconst_2
	iload_2
	invokestatic	TestFunction/exp(II)I
	invokestatic	java/lang/Integer/valueOf(I)Ljava/lang/Integer;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
;
; 022 i=i+1;
;
	iload_2
	iconst_1
	iadd
	istore_2
	goto	L005
L006:

	return

.limit locals 3
.limit stack 15
.end method

;
; MAIN
;
.method public static main([Ljava/lang/String;)V
.var 0 is args [Ljava/lang/String;
.var 1 is _start Ljava/time/Instant;
.var 2 is _end Ljava/time/Instant;
.var 3 is _elapsed J

	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_1

;
; 029 printpoweroftwo(10);
;
	bipush	10
	invokestatic	TestFunction/printpoweroftwo(I)V

	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_2
	aload_1
	aload_2
	invokestatic	java/time/Duration/between(Ljava/time/temporal/Temporal;Ljava/time/temporal/Temporal;)Ljava/time/Duration;
	invokevirtual	java/time/Duration/toMillis()J
	lstore_3
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n[%,d milliseconds execution time.]\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	lload_3
	invokestatic	java/lang/Long/valueOf(J)Ljava/lang/Long;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop

	return

.limit locals 10
.limit stack 15
.end method
