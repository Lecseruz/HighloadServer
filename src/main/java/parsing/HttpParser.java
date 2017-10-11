package parsing;


import io.netty.buffer.ByteBuf;
import models.HttpRequest;
import models.PairNameValue;

public class HttpParser {
    private enum ParseState {
        METHOD_START,
        METHOD,
        URI,
        QUERY_PARAM_START,
        QUERY_PARAM_NAME,
        QUERY_PARAM_VALUE,
        HTTP_VERSION_H,
        HTTP_VERSION_T_1,
        HTTP_VERSION_T_2,
        HTTP_VERSION_P,
        HTTP_VERSION_SLASH,
        HTTP_VERSION_MAJOR_START,
        HTTP_VERSION_MAJOR,
        HTTP_VERSION_MINOR_START,
        HTTP_VERSION_MINOR,
        EXPECTING_NEWLINE_1,
        HEADER_LINE_START,
        HEADER_LWS,
        HEADER_NAME,
        SPACE_BEFORE_HEADER_VALUE,
        HEADER_VALUE,
        EXPECTING_NEWLINE_2,
        EXPECTING_NEWLINE_3
    }

    private enum ParseResult {
        GOOD,
        BAD,
        INDETERMINATE
    }

    private ParseState parseState;

    public HttpParser() {
        this.parseState = ParseState.METHOD_START;
    }

    public void reset() {
        parseState = ParseState.METHOD_START;
    }

    public ParseResult parse(HttpRequest request, ByteBuf buffer){
        while (buffer.isReadable()) {
            ParseResult result = consume(request, (char) buffer.readByte());
            if (result == ParseResult.GOOD || result == ParseResult.BAD) {
                return result;
            }
        }
        return ParseResult.INDETERMINATE;
    }

    private boolean isCtl(int value){
        return (value >= 0 && value <= 31) || (value == 127);
    }

    private boolean isChar(int value){
        return value >= 0 && value <= 127;
    }

    private boolean isDigit(int value){
        return value >= '0' && value <= '9';
    }
    private boolean isTspecial(int c) {
        switch (c) {
            case '(': case ')': case '<': case '>': case '@':
            case ',': case ';': case ':': case '\\': case '"':
            case '/': case '[': case ']': case '?': case '=':
            case '{': case '}': case ' ': case '\t':
                return true;
            default:
                return false;
        }
    }

    private ParseResult consume(HttpRequest request, char input){

        switch (parseState) {
            case METHOD_START:
                if (!isChar(input) || isCtl(input) || isTspecial(input)) {
                    return ParseResult.BAD;
                } else {
                    parseState = ParseState.METHOD;
                    request.pushBackMethod(input);

                    return ParseResult.INDETERMINATE;
                }
            case METHOD:
                if (input == ' ') {
                    parseState = ParseState.URI;

                    return ParseResult.INDETERMINATE;
                } else if (!isChar(input) || isCtl(input) || isTspecial(input)) {
                    return ParseResult.BAD;
                } else {
                    request.pushBackMethod(input);
                    return ParseResult.INDETERMINATE;
                }
            case URI:
                if (input == '?') {
                    parseState = ParseState.QUERY_PARAM_START;

                    return ParseResult.INDETERMINATE;
                } else if (input == ' ') {
                    parseState = ParseState.HTTP_VERSION_H;

                    return ParseResult.INDETERMINATE;
                } else if (isCtl(input)) {
                    return ParseResult.BAD;
                } else {
                    request.pushBackUri(input);

                    return ParseResult.INDETERMINATE;
                }
            case QUERY_PARAM_START:
                parseState = ParseState.QUERY_PARAM_NAME;
                request.getQueryParameters().add(new PairNameValue());
                request.getHeaders().lastElement().pushBackName(input);

                return ParseResult.INDETERMINATE;
            case QUERY_PARAM_NAME:
                if (input == '=') {
                    parseState = ParseState.QUERY_PARAM_VALUE;

                    return ParseResult.INDETERMINATE;
                } else {
                    request.getHeaders().lastElement().pushBackName(input);
                    return ParseResult.INDETERMINATE;
                }
            case QUERY_PARAM_VALUE:
                if (input == '&') {

                    parseState = ParseState.QUERY_PARAM_START;

                    return ParseResult.INDETERMINATE;
                } else if (input == ' ') {
                    parseState = ParseState.HTTP_VERSION_H;

                    return ParseResult.INDETERMINATE;
                } else {
                    request.getQueryParameters().lastElement().pushBackValue(input);

                    return ParseResult.INDETERMINATE;
                }
            case HTTP_VERSION_H:
                if (input == 'H') {
                    parseState = ParseState.HTTP_VERSION_T_1;

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HTTP_VERSION_T_1:
                if (input == 'T') {
                    parseState = ParseState.HTTP_VERSION_T_2;

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HTTP_VERSION_T_2:
                if (input == 'T') {
                    parseState = ParseState.HTTP_VERSION_P;

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HTTP_VERSION_P:
                if (input == 'P') {
                    parseState = ParseState.HTTP_VERSION_SLASH;

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HTTP_VERSION_SLASH:
                if (input == '/') {
                    parseState = ParseState.HTTP_VERSION_MAJOR_START;
                    request.setHttpVersionMajor(0);
                    request.setHttpVersionMinor(0);

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HTTP_VERSION_MAJOR_START:
                if (isDigit(input)) {
                    parseState = ParseState.HTTP_VERSION_MAJOR;
                    request.setHttpVersionMajor(request.getHttpVersionMajor() * 10 + input - '0');

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HTTP_VERSION_MAJOR:
                if (input == '.') {
                    parseState = ParseState.HTTP_VERSION_MINOR_START;

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HTTP_VERSION_MINOR_START:
                if (isDigit(input)) {
                    parseState = ParseState.HTTP_VERSION_MINOR;
                    request.setHttpVersionMinor(request.getHttpVersionMinor() * 10 + input - '0');

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HTTP_VERSION_MINOR:
                if (input == '\r') {
                    parseState = ParseState.EXPECTING_NEWLINE_1;

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case EXPECTING_NEWLINE_1:
                if (input == '\n') {
                    parseState = ParseState.HEADER_LINE_START;

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HEADER_LINE_START:
                if (input == '\r') {
                    parseState = ParseState.EXPECTING_NEWLINE_3;

                    return ParseResult.INDETERMINATE;
                } else if (!request.getHeaders().isEmpty() && (input == ' ' || input == '\t')) {
                    parseState = ParseState.HEADER_LWS;

                    return ParseResult.INDETERMINATE;
                } else if (!isChar(input) || isCtl(input) || isTspecial(input)) {
                    return ParseResult.BAD;
                } else {
                    request.getHeaders().add(new PairNameValue());
                    request.getHeaders().lastElement().pushBackName(input);
                    parseState = ParseState.HEADER_NAME;

                    return ParseResult.INDETERMINATE;
                }
            case HEADER_LWS:
                if (input == '\r') {
                    parseState = ParseState.EXPECTING_NEWLINE_2;

                    return ParseResult.INDETERMINATE;
                } else if (input == ' ' || input == '\t') {
                    return ParseResult.INDETERMINATE;
                } else if (isCtl(input)) {
                    return ParseResult.BAD;
                } else {
                    parseState = ParseState.HEADER_VALUE;

                    return ParseResult.INDETERMINATE;
                }
            case HEADER_NAME:
                if (input == ':') {
                    parseState = ParseState.SPACE_BEFORE_HEADER_VALUE;

                    return ParseResult.INDETERMINATE;
                } else if (!isChar(input) || isCtl(input) || isTspecial(input)) {
                    return ParseResult.BAD;
                } else {
                    request.getHeaders().lastElement().pushBackName(input);
                    return ParseResult.INDETERMINATE;
                }
            case SPACE_BEFORE_HEADER_VALUE:
                if (input == ' ') {
                    parseState = ParseState.HEADER_VALUE;

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case HEADER_VALUE:
                if (input == '\r') {
                    parseState = ParseState.EXPECTING_NEWLINE_2;

                    return ParseResult.INDETERMINATE;
                } else if (isCtl(input)) {
                    return ParseResult.BAD;
                } else {
                    request.getHeaders().lastElement().pushBackValue(input);

                    return ParseResult.INDETERMINATE;
                }
            case EXPECTING_NEWLINE_2:
                if (input == '\n') {
                    parseState = ParseState.HEADER_LINE_START;

                    return ParseResult.INDETERMINATE;
                } else {
                    return ParseResult.BAD;
                }
            case EXPECTING_NEWLINE_3:
                return (input == '\n') ? ParseResult.GOOD: ParseResult.BAD;
            default:
                return ParseResult.BAD;
        }
    }

}
