package cn.jongwong.oauth.oidc;

import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.Use;
import org.jose4j.jws.AlgorithmIdentifiers;


public interface Constants {

    /*Fixed,  resource-id */
    String RESOURCE_ID = "myoidc-resource";

    /**
     * Fixed  keyId
     */
    String DEFAULT_KEY_ID = "myoidc-keyid";

    /**
     * 长度 至少 1024, 建议 2048
     */
    int DEFAULT_KEY_SIZE = 2048;


    /**
     * keystore file name
     */
    String KEYSTORE_NAME = "jwks.json";

    /**
     * Default ALG: RS256
     */
    String OIDC_ALG = AlgorithmIdentifiers.RSA_USING_SHA256;

    /**
     * OIDC key use: sig or enc
     */
    String USE_SIG = Use.SIGNATURE;
    String USE_ENC = Use.ENCRYPTION;

    /**
     * id_token constants
     */
    String ID_TOKEN = "id_token";

    /**
     * JWT keyid
     */
    String KEY_ID = JsonWebKey.KEY_ID_PARAMETER;


    //系统字符编码
    String ENCODING = "UTF-8";

    String PROJECT_HOME = "https://github.com/monkeyk/MyOIDC";

    //Current  version
    String VERSION = "1.1.1";


}
