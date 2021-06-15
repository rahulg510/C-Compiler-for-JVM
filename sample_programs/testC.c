Program TestCheck;

int max(int x, int y, int z){
    if(x > y){
        if(x > z){
            return x;
        }
        else{
            return z;
        }
    }
    else{
        if(y > z){
            return y;
        }
        else{
            return z;
        }
    }
}

void printmyname(string name){
    print("String is: %s",name);
}

void output(){
    printmyname("call within a function!\n");
}

string makestring(){
    return "hello, there";
}

char getchar(char ch){
    switch (ch)
    {
    case 'a':
        return 'a';
        break;
    case 'd':
        return 'd';
        break;
    default:
        return 'b';
        break;
    }
}

void loopcheck(){
    int x = 0;
    while(x < 5){
        print("%d ", x);
        x = x + 1;
    }
}

int main()
{
	print("####################\n");
	print("# Team FreeAssange #\n");
	print("####################\n");
    print("Max Value should be 4: %d\n", max(2,4,3));
    print("Char should be b: %c\n", getchar('c'));
    output();
    loopcheck();
    print("\n");
    for(int i = 0; i < 3; i = i + 1){
        printmyname("hello,");
        print(" testing.. \n");
    }

}