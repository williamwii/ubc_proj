#include<iostream>

using namespace std;

int main(){
  int num = 1;
  char* num_char = (char*) &num;
  if ( ((int) num_char[0])==1 )
    cout<<"Little Endian"<<endl;
  else if ( ((int) num_char[0])==0 )
    cout<<"Big Endian"<<endl;
  return 0;

}
