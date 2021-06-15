Program TestFunction;

int exp(int num, int power)
{
    int i = 0;
    int result = 1;
    while(i < power)
    {
        result = result * num;
        i = i + 1;
    }

    return result;
}

void printpoweroftwo(int limit)
{
    int i = 0;
    while(i <= limit)
    {
       print("%d\n", exp(2, i));
       i = i + 1;
    }
}


int main()
{
   printpoweroftwo(10);
}