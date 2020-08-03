package cn.jongwong.oauth.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.jwt.codec.Codecs;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomJwtHeaderConverter implements Converter<String, Map<String, String>> {
    private final JsonFactory factory = new JsonFactory();

    CustomJwtHeaderConverter() {
    }

    public Map<String, String> convert(String token) {
        int headerEndIndex = token.indexOf(46);
        if (headerEndIndex == -1) {
            throw new InvalidTokenException("Invalid JWT. Missing JOSE Header.");
        } else {
            byte[] decodedHeader;
            try {
                decodedHeader = Codecs.b64UrlDecode(token.substring(0, headerEndIndex));
            } catch (IllegalArgumentException var16) {
                throw new InvalidTokenException("Invalid JWT. Malformed JOSE Header.", var16);
            }

            JsonParser parser = null;

            HashMap headers;
            try {
                parser = this.factory.createParser(decodedHeader);
                headers = new HashMap();
                if (parser.nextToken() == JsonToken.START_OBJECT) {
                    while (parser.nextToken() == JsonToken.FIELD_NAME) {
                        String headerName = parser.getCurrentName();
                        parser.nextToken();
                        String headerValue = parser.getValueAsString();
                        headers.put(headerName, headerValue);
                    }
                }
            } catch (IOException var17) {
                throw new InvalidTokenException("An I/O error occurred while reading the JWT: " + var17.getMessage(), var17);
            } finally {
                try {
                    if (parser != null) {
                        parser.close();
                    }
                } catch (IOException var15) {
                }

            }

            return headers;
        }
    }
}
