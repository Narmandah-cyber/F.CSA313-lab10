package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Хэсэг B4 — bonus()-ийн тестүүд хоёр шатаар.
 *
 *   1-р шат (ALL-DEFS):     B1, B2 — 13 def бүгдийг use-д хүргэнэ.
 *   2-р шат (ALL-DU-PAIRS): B3..B6 нэмэгдэж, 23 DU хос бүгдийг хамарна.
 *
 * Хоёроос цөөн тестээр all-defs хангагдахгүй: score-ийн def 49, 51 хоёр нэг
 * гүйцэтгэлд хоёулаа use-д хүрч чадахгүй (мөр 50 үнэн бол def 51 нь def 49-ийг
 * алдаг, худал бол def 51 огт үүсэхгүй).
 */
class BonusTest {

    // ===== 1-р шат: ALL-DEFS =====

    @Test
    @DisplayName("B1 [1,0,1,1,1,1,1] thr=2 -> 8 | (35->40) (40->40) (40->50) (36->41) (41->41) (41->42) (41->43) (46->41) (37->42) (43->42) (43->53) (51->54) (54->56)")
    void bonus_streakResetThenLongStreak_returns8() {
        // present=6; цуваа 1,0,1,2,3,4,5 -> best=5
        // 50 ҮНЭН (6>=2) -> def 51; 53 ҮНЭН (5>=5) -> def 54
        // Индекс 1 дэх def 46 (streak=0) нь индекс 2 дээрх c-use 41-д хүрнэ.
        // Индекс 2 дээр streak=1, best=1 тул p-use 42 ХУДАЛ салаа ч дайрагдана.
        assertEquals(8, DataFlowTargets.bonus(new int[]{1, 0, 1, 1, 1, 1, 1}, 2));
    }

    @Test
    @DisplayName("B2 [0] thr=1 -> 0 | (49->56) (38a->39 FALSE) (37->53 FALSE)")
    void bonus_singleAbsentDay_belowThreshold_returns0() {
        // 50 ХУДАЛ тул def 51 үүсэхгүй, def 49 амьд үлдэж c-use 56-д хүрнэ.
        assertEquals(0, DataFlowTargets.bonus(new int[]{0}, 1));
    }

    // ===== 2-р шат: ALL-DU-PAIRS =====

    @Test
    @DisplayName("B3 null -> exception | (31 attendance -> p-use 32 TRUE)")
    void bonus_nullAttendance_throwsIllegalArgument() {
        // || -ийн ЗҮҮН нөхцөл үнэн: богино холболтоор threshold огт уншигдахгүй.
        assertThrows(IllegalArgumentException.class,
                () -> DataFlowTargets.bonus(null, 0));
    }

    @Test
    @DisplayName("B4 [1] thr=-1 -> exception | (31 threshold -> p-use 32 TRUE)")
    void bonus_negativeThreshold_throwsIllegalArgument() {
        // attendance нь null БИШ тул || -ийн баруун нөхцөл заавал уншигдана.
        assertThrows(IllegalArgumentException.class,
                () -> DataFlowTargets.bonus(new int[]{1}, -1));
    }

    @Test
    @DisplayName("B5 [] thr=0 -> 5 | (31 attendance -> p-use 38 FALSE) (35->50) (37->53) (51->56)")
    void bonus_emptyArray_meetsThreshold_returns5() {
        // Давталт огт эргэхгүй: present болон best дахин тодорхойлогдохгүй тул
        // ЭХНИЙ def-үүд (35, 37) шууд 50, 53 дээрх p-use-д хүрдэг цорын ганц тохиолдол.
        // 50 ҮНЭН (0>=0) -> def 51;  53 ХУДАЛ -> def 51 нь c-use 56-д хүрнэ.
        assertEquals(5, DataFlowTargets.bonus(new int[]{}, 0));
    }

    @Test
    @DisplayName("B6 [1,1,1,1,1] thr=10 -> 3 | (49->54) (54->56) (43->53 TRUE) (38a->39 TRUE)")
    void bonus_fiveStreakBelowThreshold_returns3() {
        // present=5 < 10 -> 50 ХУДАЛ тул def 49 амьд үлдэнэ;
        // best=5 -> 53 ҮНЭН тул def 49 нь c-use 54-д хүрч, def 54 нь c-use 56-д хүрнэ.
        assertEquals(3, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 1}, 10));
    }
}
