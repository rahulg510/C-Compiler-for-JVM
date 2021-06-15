.class public printshape
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static count I
.field private static i I
.field private static start I

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	printshape/_sysin Ljava/util/Scanner;
	return

.limit locals 0
.limit stack 3
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this Lprintshape;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

;
; FUNCTION printrow
;
.method private static printrow(III)V

.var 2 is count I
.var 1 is index I
.var 0 is max I
;
; 004 intt=0;
;
	iconst_0
	istore	4
;
; 005 while(t<max){if(t<index){print(" ");}else{if(count>0){print("*");cou ...
;
L001:
	iload	4
	iload_0
	if_icmplt	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifeq	L002
;
; 006 if(t<index){print(" ");}else{if(count>0){print("*");count=count-1;}e ...
;
	iload	4
	iload_1
	if_icmplt	L007
	iconst_0
	goto	L008
L007:
	iconst_1
L008:
	ifeq	L006
;
; 007 print(" ");
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	" "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
	goto	L005
L006:
;
; 010 if(count>0){print("*");count=count-1;}else{print(" ");}
;
	iload_2
	iconst_0
	if_icmpgt	L011
	iconst_0
	goto	L012
L011:
	iconst_1
L012:
	ifeq	L010
;
; 011 print("*");
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"*"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; 012 count=count-1;
;
	iload_2
	iconst_1
	isub
	istore_2
	goto	L009
L010:
;
; 015 print(" ");
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	" "
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
L009:
L005:
;
; 020 t=t+1;
;
	iload	4
	iconst_1
	iadd
	istore	4
	goto	L001
L002:

	return

.limit locals 5
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
; 025 inti;
;
	getstatic	printshape/i I
;
; 026 intstart=10;
;
	bipush	10
	putstatic	printshape/start I
;
; 027 intcount=1;
;
	iconst_1
	putstatic	printshape/count I
;
; 028 for(i=0;i<10;i=i+1){printrow(20,start,count);print("\n");start=start ...
;
	iconst_0
	putstatic	printshape/i I
L013:
	getstatic	printshape/i I
	bipush	10
	if_icmplt	L014
	goto	L015
L014:
;
; 029 printrow(20,start,count);
;
	bipush	20
	getstatic	printshape/start I
	getstatic	printshape/count I
	invokestatic	printshape/printrow(III)V
;
; 030 print("\n");
;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n"
	invokevirtual	java/io/PrintStream/print(Ljava/lang/String;)V
;
; 031 start=start-1;
;
	getstatic	printshape/start I
	iconst_1
	isub
	putstatic	printshape/start I
;
; 032 count=count+2;
;
	getstatic	printshape/count I
	iconst_2
	iadd
	putstatic	printshape/count I
	getstatic	printshape/i I
	getstatic	printshape/i I
	iconst_1
	iadd
	putstatic	printshape/i I
	pop
	goto	L013
L015:

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
