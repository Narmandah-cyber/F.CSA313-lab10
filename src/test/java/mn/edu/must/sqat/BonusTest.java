package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Хэсэг B4 — 1-р шат: ALL-DEFS хамралт.
 *
 * bonus()-д нийт 13 def бий:
 *   attendance(31), threshold(31), a(38), present(35), present(40),
 *   streak(36), streak(41), streak(46), best(37), best(43),
 *   score(49), score(51), score(54)
 *
 * Эдгээрийг ХОЁР тестээр бүрэн хамарна. Хоёроос цөөн болохгүй шалтгаан:
 * score-ийн гурван def нэг гүйцэтгэлд бүгд use-д хүрч чадахгүй —
 *   мөр 50 (present >= threshold) ҮНЭН бол def 51 нь def 49-ийг АЛДАГ (kill),
 *   худал бол def 51 огт үүсэхгүй.
 * Тиймээс 50-ийн үнэн ба худал хоёр тусдаа гүйцэтгэл заавал шаардлагатай.
 */
class BonusTest {

    @Test
    @DisplayName("B1 [1,0,1,1,1,1,1] thr=2 -> 8 | defs 31,35,36,37,38,40,41,43,46,51,54")
    void bonus_streakResetThenLongStreak_returns8() {
        // present=6, цувааны хамгийн урт нь 5 -> best=5
        // 50 ҮНЭН (6>=2) -> score=5 (def 51);  53 ҮНЭН (5>=5) -> score=8 (def 54)
        // Мөр 46-гийн def (streak=0, индекс 1) нь индекс 2 дээрх c-use 41-д хүрнэ.
        assertEquals(8, DataFlowTargets.bonus(new int[]{1, 0, 1, 1, 1, 1, 1}, 2));
    }

    @Test
    @DisplayName("B2 [0] thr=1 -> 0 | def 49 (score=0 reaches return 56)")
    void bonus_singleAbsentDay_belowThreshold_returns0() {
        // present=0 -> 50 ХУДАЛ тул def 51 үүсэхгүй, def 49 амьд үлдэж
        // шууд c-use 56 (return score)-д хүрнэ. Энэ бол def 49-ийг хамрах
        // ЦОРЫН ГАНЦ арга: мөр 50 худал байх ёстой.
        assertEquals(0, DataFlowTargets.bonus(new int[]{0}, 1));
    }
}
