#ifndef _LOADOBJ_H_
#define _LOADOBJ_H_

#include "linalg.h"
#include <vector>

bool
read_objfile(std::vector<Vector>& verts,
             std::vector< std::vector<int> >& tris,
             const char* filename);

#endif
