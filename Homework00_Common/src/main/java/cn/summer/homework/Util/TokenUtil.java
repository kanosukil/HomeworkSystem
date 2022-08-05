package cn.summer.homework.Util;

import cn.summer.homework.DTO.UserDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/8/5-13:54
 */

public class TokenUtil {
    private static final long expire = 1000 * 60 * 60 * 5;
    private static final String id = "userid";
    private static final String account = "account";
    private static final String role = "roles";

    public static String generateJWToken(UserDTO user, List<String> roles) {
        if (user == null || user.getAccount() == null
                || user.getPassword() == null || roles.size() <= 0) {
            return null;
        }
        try {
            return JWT.create().withSubject(SaltUtil.getSubject())
                    .withClaim(id, user.getId())
                    .withClaim(account, user.getAccount())
                    .withArrayClaim(role, roles.toArray(new String[0]))
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + expire))
                    .sign(Algorithm.HMAC256(SaltUtil.getSalt().getBytes(StandardCharsets.UTF_8)));
        } catch (JWTCreationException je) {
            System.out.printf("JWTCreationException:%s\nToken 生成失败,User:%s\n",
                    je.getMessage(), user.getAccount());
            return "";
        }
    }

    public static Map<String, String> checkJWToken(String token) {
        try {
            DecodedJWT verify = JWT.require(Algorithm.HMAC256(SaltUtil.getSalt().getBytes(StandardCharsets.UTF_8)))
                    .build().verify(token);
            return new HashMap<>() {{
                put(id, verify.getClaim(id).asString());
                put(account, verify.getClaim(account).asString());
                put(role, verify.getClaim(role).asString());
            }};
        } catch (JWTVerificationException exception) {
            System.out.printf("JWTVerificationException:%s\nToken 验证失败,Token:%s\n",
                    exception.getMessage(), token);
            return null;
        }
    }
}
