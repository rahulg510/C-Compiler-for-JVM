Program printshape;

void printrow(int max, int index, int count){
    int t = 0;
    while(t < max){
        if(t < index){
            print(" ");
        }
        else{
            if(count > 0){
                print("*");
                count = count - 1;
            }
            else{
                print(" ");
            }
        }


        t = t + 1;
    }
}

int main(){
    int i;
    int start = 10;
    int count = 1;
    for(i = 0; i < 10; i = i + 1){
        printrow(20, start, count);
        print("\n");
        start = start - 1;
        count = count + 2;
    }
}