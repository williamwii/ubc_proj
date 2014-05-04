#include <cstdlib>

#define MIN(a, b) ((a) < (b) ? (a) : (b))
#define MAX(a, b) ((a) > (b) ? (a) : (b))

// Returns a random float from 0 to 1.
// Just looks like a function, but really isn't.
#define randf() (float(rand()) / float(RAND_MAX))

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif