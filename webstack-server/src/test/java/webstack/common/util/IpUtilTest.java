package webstack.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IpUtilTest {

    @Test
    void maskIpv4ShouldHideLastTwoSegments() {
        assertEquals("192.168.*.*", IpUtil.maskIp("192.168.1.100"));
    }

    @Test
    void maskIpv6ShouldKeepFirstTwoSegments() {
        assertEquals("2001:db8:****", IpUtil.maskIp("2001:db8:0:0:0:0:0:1"));
    }

    @Test
    void maskNullShouldReturnNull() {
        assertNull(IpUtil.maskIp(null));
    }
}
