#include <iostream>
#include <stdio.h>
#include <time.h>
#include <cstdlib>

using namespace std;

int main( int argc, char ** argv ) {

	int size = atoi ( argv[1] );

	for (int i=1;i<=size/3;i++) {
		printf("I %d\n", i);
		printf("F %d\n", i);
		printf("R %d\n", i);
	}

}
