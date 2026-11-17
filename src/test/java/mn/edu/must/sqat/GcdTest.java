package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Хэсэг A — gcd()-ийн ALL-DU-PAIRS хамралт.
 *
 * Мөрийн дугаарууд нь DataFlowTargets.java-гийн ФАЙЛЫН мөрүүд:
 *   13: def x, def y        15: p-use y (while)
 *   16: def tmp, c-use x, c-use y
 *   17: def x, c-use y      18: def y, c-use tmp
 *   20: c-use x (return)
 *
 * Нийт 11 DU хос: x — 4, y — 6, tmp — 1.
 */
class GcdTest {

    @Test
    @DisplayName("A1 gcd(7,0): loop never entered | pairs (13y->15 FALSE), (13x->20)")
    void gcd_yIsZero_skipsLoopEntirely() {
        // Давталтад огт ордоггүй цорын ганц тохиолдол:
        // def x(13) нь 16, 17-гоор дарагдалгүй шууд return-д (20) хүрнэ.
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    @Test
    @DisplayName("A2 gcd(0,5): exactly one iteration | pairs (13y->15 TRUE), (13x->16), (13y->16), (13y->17), (16tmp->18), (17x->20), (18y->15 FALSE)")
    void gcd_singleIteration_coversInitialDefinitions() {
        // x=0,y=5 -> tmp=0, x=5, y=0 -> давталт зогсож 5 буцна.
        // Эхний def-үүд (13) давталтын биед хүрч, 17, 18-ийн def-үүд үүснэ.
        assertEquals(5, DataFlowTargets.gcd(0, 5));
    }

    @Test
    @DisplayName("A3 gcd(12,8): two iterations | pairs (17x->16), (18y->16), (18y->17), (18y->15 TRUE)")
    void gcd_twoIterations_coversLoopCarriedDefinitions() {
        // 1-р эргэлт: tmp=4, x=8, y=4   2-р эргэлт: tmp=0, x=4, y=0
        // Давталтын дотор үүссэн def-үүд (17, 18) ДАРААГИЙН эргэлтийн use-д хүрнэ —
        // эдгээр хосууд хоёроос доошгүй эргэлт шаарддаг.
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }
}
