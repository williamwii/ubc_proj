BIN = bezier
OBJ = main.o pointcanvas.o bezier.o
LIB = -lGLU -lGL -lglut -lm

#CXXFLAGS = -g -Wno-uninitialized -DDEBUG
CXXFLAGS = -g -Wall -DDEBUG
LDFLAGS = -g

.cpp.o:
	g++ -c $(CXXFLAGS) $< -o $@

$(BIN): $(OBJ)
	g++ $(LDFLAGS) -o $@ $(OBJ) $(LIB)

clean:
	rm -f $(OBJ) $(BIN)
