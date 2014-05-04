
CC = g++
CFLAGS = -g 

BIN = raytracer
CLS = lexer parser linalg image object mesh light material raytracer
SRC = $(CLS:%=%.cpp)
OBJ = $(SRC:%.cpp=build/%.o)

default: build
build: $(BIN)

$(BIN): build/main.o $(OBJ)
	$(CC) -o $@ $^

build/%.o: %.cpp
	@ mkdir -p $(@D)
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -rf *~ $(OBJ) $(BIN) build

test: $(BIN)
	./$(BIN) scenes/basic.ray
	convert output.ppm output.jpeg


