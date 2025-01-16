/****************************\
 *      ________________      *
 *     /  _             \     *
 *     \   \ |\   _  \  /     *
 *      \  / | \ / \  \/      *
 *      /  \ | / | /  /\      *
 *     /  _/ |/  \__ /  \     *
 *     \________________/     *
 *                            *
 \****************************/
/*
 * Copyright 2025 Damien Westerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.damienwesterman.defensedrill.security.util;

import java.util.List;
import java.util.stream.Collectors;

public class Constants {
    public static enum UserRoles {
        USER("USER"),
        ADMIN("ADMIN");

        private String roleString;

        UserRoles(String roleString) {
            this.roleString = roleString;
        }

        public String getStringRepresentation() {
            return this.roleString;
        }
    }

    public static final List<String> ALL_ROLES_LIST = List.of(UserRoles.values()).stream()
        .map(UserRoles::getStringRepresentation)
        .collect(Collectors.toList());

    // TODO: DELETE ME AND FIGURE THIS OUT WITH VAULT
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqp7lE/z+pO6APn6boPj1b6w9n/p7PiJ5PPul0+VF7QHFzOO6JGfBY81e/sMKoEsoZEqf6ew1ZH77PJ9AxDZ6mkgFzPnaOsbbYi6NXiDBu9C0+P1RSMKEX96cz+F4YDN9SgkcMcBndsNGUusWvzt87o0jR93ynr70OG+JAE5131gxYfV9DDDmTaAx/KarqCcgLCf98KpIGfMUqs6X/BXo3MMAIanXMbmvfeBeLZeEHGrlr2w80fw3DgRqKV8dCHRUUDuB7Vr1Fz/sV8cq26XG6vsSzZi1YKzjd3Kd3pBL0xEtimlk5rLRlxqazodXzNbv2AY2z95HbxaOupwW479zSwIDAQAB";
    public static final String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCqnuUT/P6k7oA+fpug+PVvrD2f+ns+Ink8+6XT5UXtAcXM47okZ8FjzV7+wwqgSyhkSp/p7DVkfvs8n0DENnqaSAXM+do6xttiLo1eIMG70LT4/VFIwoRf3pzP4XhgM31KCRwxwGd2w0ZS6xa/O3zujSNH3fKevvQ4b4kATnXfWDFh9X0MMOZNoDH8pquoJyAsJ/3wqkgZ8xSqzpf8FejcwwAhqdcxua994F4tl4QcauWvbDzR/DcOBGopXx0IdFRQO4HtWvUXP+xXxyrbpcbq+xLNmLVgrON3cp3ekEvTES2KaWTmstGXGprOh1fM1u/YBjbP3kdvFo66nBbjv3NLAgMBAAECggEAUalhTPLO0hPQz5h7Vk0x83QFvrb9G50WV+i90rAuiqRJjqoEIn6CYgkeiWLCiDvQpkfNSAGlAGeCSd6S+hZ1EbidBiEjoPQidlh/kuissP7QT0e3CtMG1nfOp/RrT+f09GVt2CYVnxMRaohYR8jqH6bvlusUv4P3kbdYZAwpgG6Fr3U4FlgCsSj0WuqsBz20FVx7HGQcxBDyA6djgzZNccR9cQeT3qxRpNCDblXX1B8vc7EhnG98euTn7+LVjuVtW0Tlo8yvCCj52iTPG42FP+lW3xwK3IZ4CkJzlFPSq/0Dl5QID0P5NwhAdZoTBdvQkqc41lQg+nFf2lsWRIS1mQKBgQC+cD6KgfX1z0RONgUw/TSDqSy6ENfoIVIUCyQv2NA56ajNwvl77Of6LeVW1w4WpwqZyCp4OGvrI8MnI+4qYZ2d7SlPgtdKxopurT7ruZSVaJAAvd8+7uQ83fSo1EVpzBIXg2sgIVXg95rX3NnGiLfrJiHtBBrDU3JS8IKc5Z+anQKBgQDlXBEzS+JYvxkwstS1gRst5BLW9fyHB8l9npXF9wnnVAtj46esbq18NKm21bTJuVHvku2Ytg3xUebww4XpNgw1agfdg9pbhqXafDoZRwAvwmYvAaN1rDNhq5T5STSvDXA5MK1C16klx0pbbwRgKMPsyUSOgK3BUGsw2N9J3BVNBwKBgQCTcC1LaNC3TY/RiYs9YVut72VcbLvr0RhNwk5y6MZVf+yb2S70Xfc/vxbGw9r5eLwBUXJn/y62LsrhW7UBhO/VnA6Wq3LpRotYorKLJ/O8NOcAXAAiJiv4X9MSfr166m67W5itmIF3mdt88lR8m87gtHYhnULnJIznX6OPu0t1FQKBgQCPw5jIdX1IMWhhvQZVSrq1lis08HU8UmY6xkeNEd70UBrfntf/3lQB8aSbhPLQKsgwCgHB4gN1Sugd7ACpAIbAiFirwbUB/7E/9PQZ2gZE6W5Rco55+NkwFHPJmdhbDWpVfqxrpHBSzMtP6pNsjsDYUAQX6pJ/BKBbuuSjED+NywKBgEtaraoaA2/MM6EPk79Cd8oC5y1FMh6kqmQfP4A+atbTLAifi+804xq3eS2aKHj6p3Nro80FYaLkcsxtwDcHZEStbGHv7nWZZZekv5IY0OswINOG1QFe8gbap86Qbt0ZSsGjc0FB5Ow8utpc7AHmIeyZrmVlUJBD0Fpul/vHeNth";

    public static final String JWT_ISSUER = "DefenseDrillWeb";
}
