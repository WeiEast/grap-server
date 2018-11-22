package com.treefinance.saas.grapserver.common.utils;

import static com.treefinance.saas.grapserver.common.utils.RSAUtils.decrytData;
import static com.treefinance.saas.grapserver.common.utils.RSAUtils.encryptData;

import java.util.Map;
import org.junit.Test;

/**
 * @author henengqiang
 * @date 2018/11/13
 */
public class UtilsTests {

    @Test
    public void JsonUtilsTest() {
        String str = "{\"test1\":\"zhangsan\",\"test2\":\"lisi\",\"test3\":\"wanger\"}";
        Map<String, String> map = JsonUtils.toMap(str, String.class, String.class);
        System.out.println(map);
    }

    @Test
    public void RSAUtilsTest1() throws Exception {
        String params = "{\"idcard\":\"110000199001041119\",\"name\":\"xxx\",\"email\":\"xxx\",\"mobile\":\"12312xxx\"}";
        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMe0CmYZFqmhj9cnRYz8eP0ChRaz8LR8gVEtgNj4XuhpJOGR3xnGEjVvi0NrJx8DAsoJRvJDZS4fZu0094X4kPAB9dZ8z3KNT+g4LnqLufua0JhCaPo1XtlynTL2I7l9Zz+EV3q8eXiL0G3yCDtAZPix3UsHoQxi+Q9VFafWAuxwIDAQAB";
        String data = encryptData(params, key);
        System.out.println(data);
    }

    @Test
    public void RSAUtilsTest2() throws Exception {
        String data1 ="b28SKWVc4d9DwjXijTNtxqU9BY8WFLy9a4GUgGWoXNNxOty65%2BVOGBI8t9Ug1LUenPseE%2BnKLQEegm69m9ErBwhHi5%2Fp%2FyEeq%2B2ZdJvvT1ltw1qaLF7QrKnPs1znMGzQp%2BEdXKGC9yw0ovXJV1NN5sKLn%2BI7hQv0UH28YAVWykUv1yyfEqLY1pWvy9L9CJ%2Fwf%2Bzc%2FPiEpaUkfNsA03oJ8WMOaNjRPEvTngPG27V0Wp799ssixxJCJehZ2umok%2Bac6ajRmcf4gUDL8f2MFX%2BsT9dR%2F1LISHaiwA1N2gP9AbbSY4GDI%2FVZkSzMTFdMnEIzmk39ZMSs8K3pexcuviyEkg%3D%3D";
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIT0AQ9e2pyIHf8PVeJ0WeDSuid53JLCs8H3ZkZOQIESAGJe/T8zndMCpF4wBY0vZFr5wIuDFglJfJWp3ydgzrAKDJ9dCBWzsRn08pTlHnDAuoF70eV3CxpvO9r94+5DcwJAZStsO1PTsP+39dOOaR7SIczRWj2BmM3wnk5VrHtNAgMBAAECgYB3KYdlslt33pwcBi+w9x2zXsvQI+3pcC7TE52MOAiiZnMHuCF9KxMyk/SkIqBnKWnmDGFh6YyvqDjMn2NOFPtKCrXv4ozyHQMdU2/qHGbGW4/44xdqlcqJXsFZ2zr7DQedPb6KaHzm/0quj3fRmf3kSxpUbC2a5AMxW3CrvGR/gQJBAMoAJm4OCwgTBQzChLCAXHp6ZjOtV9aLeRIh5AP8qWGBCI3wytHsWx37GqGVC+BiQoyOixDrG6BsrabJ52TNiO0CQQCofpzaKHVpw16W+HDV53trgf0MWtUbHQc9vHkV0Zt2u8DH7b/80kDcKRfWtxqTJJQEIzZbYz4hqmNDb53UHk/hAkEAkD1NM7bpX6mdKIBZmWL/JiHyiqITn8pV+IGkvMgU9t/ZiOlRGlcItT7RxGGWanIfjRhX6wvG3WXdFcUEavFs/QJAG3VGqmade7bS21dxS+qFOmfcjP7ga6K3Y2Are1rFMvw11l7wQosg9r/bmqoMMp5rYKE8Yngxz9qsgnjWB5NJAQJARX8Ud6bI0RxMFZMt/uFMNCh6aewPPjv0GgJJxc6wvrb927SIbRLgUcAozcw+YEWWYWaYMKQBHfmQg12OQl+LeA==";
        String dData = decrytData(data1, privateKey);
        System.out.println(dData);
    }

}
