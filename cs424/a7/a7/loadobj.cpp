#include "loadobj.h"
#include <fstream>
#include <cmath>
#include <cstdarg>
#include <cstdlib>

#define LINESIZE 1024 // maximum line size when reading .OBJ files

static bool
read_int(const char *s,
         int &value,
         bool &leading_slash,
         int &position)
{
    leading_slash=false;
    for(position=0; s[position]!=0; ++position){
        switch(s[position]){
            case '/':
                leading_slash=true;
                break;
            case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                goto found_int;
        }
    }
    return false;

found_int:
    value=0;
    for(;; ++position){
        switch(s[position]){
            case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                value=10*value+s[position]-'0';
                break;
            default:
                return true;
        }
    }
    return true; // should never get here, but keeps compiler happy
}

static void
read_face_list(const char *s,
               std::vector<int> &vertex_list)
{
    vertex_list.clear();
    int v, skip;
    bool leading_slash;
    for(int i=0;;){
        if(read_int(s+i, v, leading_slash, skip)){
            if(!leading_slash)
                vertex_list.push_back(v-1); // correct for 1-based index
            //          vertex_list.push_back(v); // correct for 0-based index
            i+=skip;
        }else
            break;
    }
}

bool
read_objfile(std::vector<Vector>& verts,
             std::vector< std::vector<int> >& tris,
             const char* filename)
{
    std::ifstream input(filename, std::ifstream::binary);
    if(!input.good()) return false;

    verts.clear();
    tris.clear();

    char line[LINESIZE];
    std::vector<int> vertex_list;
    while(input.good()){
        input.getline(line, LINESIZE);
        switch(line[0]){
            case 'v': // vertex data
                if(line[1]==' '){
                    Vector new_vertex;
                    std::sscanf(line+2, "%f %f %f", &new_vertex[0],
                                                    &new_vertex[1],
                                                    &new_vertex[2]);
                    verts.push_back(new_vertex);
                }
                break;
            case 'f': // face data
                if(line[1]==' '){
                    read_face_list(line+2, vertex_list);
                    for(int j=0; j<(int)vertex_list.size()-2; ++j){
                        std::vector<int> face;
                        face.push_back(vertex_list[j]);
                        face.push_back(vertex_list[j+1]);
                        face.push_back(vertex_list[j+2]);
                        tris.push_back(face);
                    }
                }
                break;
        }
    }
    return true;
}


