//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
public interface LineParser {
    ProtocolVersion parseProtocolVersion(CharArrayBuffer var1, ParserCursor var2) throws ParseException;

    boolean hasProtocolVersion(CharArrayBuffer var1, ParserCursor var2);

    RequestLine parseRequestLine(CharArrayBuffer var1, ParserCursor var2) throws ParseException;

    StatusLine parseStatusLine(CharArrayBuffer var1, ParserCursor var2) throws ParseException;

    Header parseHeader(CharArrayBuffer var1) throws ParseException;
}
