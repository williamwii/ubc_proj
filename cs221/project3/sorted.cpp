#include <iostream>
#include <cstdlib>

using namespace std;

int main( int argc, char ** argv ) {
	
	int size = atoi ( argv[1] );

	for (int i=1;i<=size;i++) {
		cout<<"I"<<i<<endl;
	}

	for (int i=1; i<=size;i++) {
		cout<<"R"<<i<<endl;
	}
	
	for (int i=1;i<=size;i++) {
		cout<<"F"<<i<<endl;
	}
	

}
