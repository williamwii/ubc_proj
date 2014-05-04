# OS X
ifeq ("$(shell uname)", "Darwin")
  CXXFLAGS = -I/usr/include
  LIB = -framework OpenGL -framework GLUT -lm

# Linux
else
LIB = -lGL -lGLU -lglut -lm
endif

BIN = meshview
OBJ = meshview.o trimesh.o subdivision.o loadobj.o linalg.o

CXXFLAGS = -g -Wall -DDEBUG
LDFLAGS = -g

.cpp.o:
	g++ -c $(CXXFLAGS) $< -o $@

$(BIN): $(OBJ)
	g++ $(LDFLAGS) -o $@ $(OBJ) $(LIB)

clean:
	rm -f $(OBJ) $(BIN)
