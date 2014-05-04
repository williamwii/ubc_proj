#include <vector>
#include <string>
#include <ctime>

#include "object.hpp"
#include "parser.hpp"
#include "raytracer.hpp"
#include "scene.hpp"
#include "linalg.hpp"

using namespace std;

static string scenefile("scenes/basic.ray");
static string outfilename("output.ppm");
static string outfilename_depth("output_depth.ppm");

int main(int argc, char **argv)
{    
    // Scene filename specified.
    if(argc > 1) 
        scenefile = string(argv[1]);
    
    // Output filename specified
    if(argc > 2) {
        outfilename = string(argv[2]);
        outfilename_depth = outfilename.substr(0, outfilename.length()-4);
        outfilename_depth.append("_depth.ppm");
    }

    std::cout << "Rendering " << scenefile << std::endl;
    std::cout << "Output to " << outfilename << std::endl;
   
    // Parse the scene file.
    Parser parser(new std::ifstream(scenefile.c_str()));
    if (!parser.parse()) {
        return 1;
    }

    // Render the image using our raytracer.
    // Time how long it takes.
    Raytracer raytracer;
    clock_t startTime = clock();
    raytracer.render(
        outfilename.c_str(),
        outfilename_depth.c_str(),
        parser.scene
    );
    clock_t endTime = clock();

    std::cout << "Rendered in " << 
        int(1000.0 * double(endTime-startTime) / (double)CLOCKS_PER_SEC)
        << "ms" << std::endl;
}

