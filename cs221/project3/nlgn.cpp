#include <iostream>
#include <cstdlib>
#include <stdio.h>

using namespace std;

struct item {
	int data;
	bool stat;
};

void printMed(struct item** a, int first, int last) {
	if (first>=last) {
		/*if ( a[first]->stat!=true ) {
			printf ("I %d\n",a[first]->data);
			a[first]->stat = true;
		}*/
		return;
	}	
	
	else {
		double med = (first+last)/2.0;
		/*if ( a[(int)med]->stat!=true ) {
			printf ("I %d\n",a[(int)med]->data);
			a[(int)med]->stat = true;
		}*/
		if ( a[(int)(med/2.0)]->stat!=true ){
			printf ("I %d\n",a[(int)(med/2.0)]->data);
			a[(int)(med/2.0)]->stat=true;
		}
		if ( a[(int)(3.0*(med/2.0))]->stat!=true ){
			printf ("I %d\n",a[(int)(3.0*(med/2.0))]->data);
			a[(int)(3.0*(med/2.0))]->stat=true;
		}
		printMed(a,first,(int)med);
		printMed(a,(int)(med+1.0),last);
	}
}
int main( int argc, char ** argv ) {
	
	int size = atoi ( argv[1] );

	struct item** a = new struct item*[size];
	for (int i=0;i<size;i++){
		a[i] = new struct item;
		a[i]->data = i+1;
		//cout<<a[i]->data<<endl;
	}
	printMed(a,0,size/2);

}
