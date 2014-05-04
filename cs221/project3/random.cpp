#include <iostream>
#include <stdio.h>
#include <time.h>
#include <cstdlib>

using namespace std;

int main( int argc, char ** argv ) {

	int size = atoi ( argv[1] );

	int a[size];

	srand ( time(NULL) );

	for (int i=0;i<size;i++) {
		int temp = rand();
		a[i] = temp;
		printf("I %d\n",temp);
	}

	for (int j=size-1;j>=0;j--) {
		int temp1 = rand()%size;	
		cout<<"F "<<a[temp1]<<endl;
	}
	
	for (int k=0;k<size;k++) {
		int temp2 = rand()%size;	
		cout<<"R "<<a[temp2]<<endl;
	}

}
