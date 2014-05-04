#ifndef PARSER_H
#define PARSER_H

#include <vector>
#include <iostream>
#include <fstream>
#include <sstream>

#include "lexer.hpp"
#include "object.hpp"
#include "scene.hpp"
#include "linalg.hpp"
#include "camera.hpp"

class Parser {
private:

    Lexer lexer; // The lexer we will use to break up the file into tokens.
    
    std::vector<Matrix> transformStack;  // Transformation stack.

    // The following functions parse all of the commands that may be found
    // in a .ray file.
    void parseDimensions();
    void parsePerspective();
    void parseLookAt();
    void parseMaterial();

    void parsePushMatrix();
    void parsePopMatrix();
    void parseTranslate();
    void parseRotate();
    void parseScale();

    void parseSphere();
    void parsePlane();
    void parseMesh();
    void parseConic();

    void parsePointLight();

    // Parses the common parts of each object (trailing material name),
    // and sets up the object in the scene.
    void finishObject(Object *obj);
    

public:

    Scene scene; // The scene that will be created when parsing.

    Parser(char const * filename) : lexer(new std::ifstream(filename)) {}
    Parser(std::istream *input) : lexer(input) {}

    // Parse the file or stream passed into the constructor.
    // Store the results in scene.
    // Returns false on failure; will print to std::cerr.
    bool parse();
};

#endif
