#ifndef LEXER_H
#define LEXER_H

#include <climits>
#include <iostream>
#include <string>
#include <deque>
#include <vector>
#include <map>


// The different types of tokens that can be lexed.
enum TokenType {
    STRING,
    NUMBER,
    NAME,
    ARRAY_BEGIN,
    ARRAY_END,
    END_OF_FILE,
    ERROR
};


// A class to represent a single token that has been lexed.
class Token {
    public:

        TokenType type;

        // Variables for the different data types.
        double number;
        std::string string;

        // A few constructors for directly assigning values.
        Token(TokenType type) : type(type) {}
        Token(double value) : type(NUMBER), number(value) {}
        Token(TokenType type, std::string value) : type(type), string(value) {}

        // Equality operator.
        bool operator==(Token const &other) const;
};


// Convenience to write a Token directly to an output stream.
std::ostream& operator<<(std::ostream &out, Token const &token);


// The type of a "parameter list", e.g. mapping from strings to sets of numbers.
typedef std::map<std::string, std::vector<double> > ParamList;


// The lexing class itself.
class Lexer {
    private:

        // The input stream to read from.
        std::istream *_input;
        
        // Raw function to read the input stream and return the next token.
        Token _processStream();
        
        // A temporary storage for lexed tokens that have not been parsed yet.
        std::deque<Token> _buffer;

    public:

        // Construct a lexer. Must be given an input stream.
        Lexer(std::istream *input) : _input(input) {}

        // Peek at the next token, but don't consume it.
        Token peek(unsigned int index = 0);

        // Get the next token.
        Token next();

        // Skip a number of tokens.
        void skip(unsigned int count = 1);

        // The remaining functions will throw a std::string exception if the
        // are unable to perform as requested.

        // Retrieve a command name.
        std::string getName();

        // Get a list of numbers. Min/max refer to the required size of the
        // list.
        std::vector<double> getNumbers(unsigned int min = 0, 
                                       unsigned int max = UINT_MAX);
        
        // Get a single number.
        double getNumber();

        // Get a single string.
        std::string getString();

        // Get a parameter list (i.e. strings mapping to number lists).
        // Min/max apply to each number list as for getNumbers().
        ParamList getParamList(unsigned int min = 0, 
                               unsigned int max = UINT_MAX);
};

#endif
