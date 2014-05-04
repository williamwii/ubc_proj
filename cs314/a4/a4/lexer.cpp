
#include <cctype>
#include <iostream>
#include <sstream>

#include "lexer.hpp"


#define LEX_ERROR(error) { \
    std::stringstream ss; \
    ss << error; \
    throw ss.str(); \
}


bool Token::operator==(Token const &other) const {
    if (type != other.type) {
        return false;
    }
    switch (type) {
        case NUMBER:
            return number == other.number;
        case STRING:
        case NAME:
            return string == other.string;
    }
    return true;
}


std::ostream& operator<<(std::ostream &out, Token const &token) {
    switch(token.type) {
        case STRING:
            out << "STRING:\"" << token.string << "\"";
            break;
        case NUMBER:
            out << "NUMBER:" << token.number;
            break;
        case NAME:
            out << "NAME:" << token.string;
            break;
        case ARRAY_BEGIN:
            out << "ARRAY_BEGIN";
            break;
        case ARRAY_END:
            out << "ARRAY_END";
            break;
        case END_OF_FILE:
            out << "EOF";
            break;
        case ERROR:
            out << "ERROR";
            break;
        default:
            out << "UNKNOWN";
            break;
    }
    return out;
}


Token Lexer::_processStream(void) {
    
    // Handle immediate/error conditions.
    if (_input->eof()) {
        return Token(END_OF_FILE);
    }
    if (!_input->good()) {
        return Token(ERROR);
    }

    // The next character in the stream.
    char c = _input->peek();
    
    // Strip whitespace and comments.
    bool did_strip_something;
    do {
        did_strip_something = false;

        // Strip whitespace.
        while (isspace(c)) {
            _input->ignore(1);
            c = _input->peek();
            did_strip_something = true;
        }

        // Strip comments.
        if (c == '#') {
            do {
                c = _input->get();
            } while (c != '\r' && c != '\n');
            did_strip_something = true;
            c = _input->peek();
        }

    } while (did_strip_something);

    // Arrays.
    switch (c) {
        case '[':
            _input->ignore(1);
            return Token(ARRAY_BEGIN);
        case ']':
            _input->ignore(1);
            return Token(ARRAY_END);
    }

    // Strings.
    if (c == '"') {
        _input->get();
        std::string value;
        bool finished = false;
        while (!finished) {
            c = _input->get();
            switch (c) {
                case '"':
                    finished = true;
                    break;
                // TODO: handle escapes.
                default:
                    value += c;
            }
        }
        return Token(STRING, value);
    }

    // Numbers; try to read one and if it doesn't work reset the error
    // state and carry on.
    double number;
    *_input >> number;
    if (!_input->fail()) {
        return Token(number);
    }
    _input->clear();

    // Names.
    std::string name;
    (*_input) >> name;
    if (name.size()) {
        return Token(NAME, name);
    }

    // There is nothing left to read.
    return Token(END_OF_FILE);

}


Token Lexer::peek(unsigned int index) {
    if (_buffer.size() <= index) {
        _buffer.push_back(_processStream());
    }
    return _buffer[index];
}


Token Lexer::next() {
    if (!_buffer.size()) {
        return _processStream();
    }
    Token token = _buffer.front();
    _buffer.pop_front();
    return token;
}


void Lexer::skip(unsigned int count) {
    for (unsigned int i = 0; i < count; i++) {
        next();
    }
}


std::string Lexer::getName() {
    Token token = peek();
    if (token.type != NAME) {
        LEX_ERROR("expected NAME; got " << token)
    }
    skip(1);
    return token.string;
}


std::vector<double> Lexer::getNumbers(unsigned int min, unsigned int max) {

    std::vector<double> values;

    bool is_array = peek().type == ARRAY_BEGIN;
    if (is_array) {
        skip(1);
    }

    // UGLY HACK: Only here so we have something to `continue` to.
    do {
    
        Token token = next();
        
        switch (token.type) {
            case NUMBER:
                values.push_back(token.number);
                if (!is_array) {
                    break;
                }
                continue;
                
            case ARRAY_END:
                
                if (is_array) {
                    break;
                }
                // falling through here
            default:
                LEX_ERROR("expected NUMBER; got " << token);
        }
        
        if (values.size() >= min && values.size() <= max) {
            return values;
        }
        LEX_ERROR("expected " << min << " to " << max << " NUMBERs; got " << values.size())

    } while (true);

}


double Lexer::getNumber() {
    return getNumbers(1, 1)[0];
}


std::string Lexer::getString() {
    Token token = peek();
    if (token.type != STRING) {
        LEX_ERROR("expected STRING; got " << token)
    }
    skip(1);
    return token.string;
}


ParamList Lexer::getParamList(unsigned int min, unsigned int max) {
    ParamList map;
    while (true) {
        std::string key;
        try {
            key = getString();
        } catch (std::string) {
            return map;
        }
        map[key] = getNumbers(min, max);
    }
}










